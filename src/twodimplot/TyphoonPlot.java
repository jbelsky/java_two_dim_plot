package twodimplot;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import jbfunctions.TF;

public class TyphoonPlot {

	/**
	 * @param args
	 */
	public static void update_typhoon_plot_matrix(SAMRecordIterator itr, 
		int[][] typhoon_plot, 
		int y_axis_high_frag,
		int low_pos_boundary,
		int high_pos_boundary,
		int plot_width
		){
				
			// Set up the read count itr
			int read_count_itr = 0;
		
			// Iterate through each read within this range											   
			while(itr.hasNext()){
				
				// Obtain the read
				SAMRecord read = itr.next();
						
				// Get the read coordinates
				int read_width = read.getInferredInsertSize();
				int read_start = read.getAlignmentStart() + (read_width/4);
				int read_end = (read.getAlignmentStart() + read_width - 1) - (read_width/4);
											
				// Subset on those reads that meet the x-axis and y-axis conditions
				if((read_width <= y_axis_high_frag) &&
				   (
					((read_start >= low_pos_boundary) && (read_end <= high_pos_boundary)) ||
					((read_start <= low_pos_boundary) && (read_end >= low_pos_boundary)) ||
					((read_start <= high_pos_boundary) && (read_end >= high_pos_boundary))	   
				   )
				  ){
						
					// Create the relative pos
					int read_start_rel_pos = Math.max(0, read_start - low_pos_boundary);
					int read_end_rel_pos = Math.min(plot_width - 1, read_end - low_pos_boundary);
							
					// Update the typhoon plot count between the read positions
					for(int p = read_start_rel_pos; p <= read_end_rel_pos; p++)
						typhoon_plot[read_width][p]++;
					
					read_count_itr++;
					
				}
			
			}
			
			// Close the iterator
			itr.close();
		
			System.out.println("\tThere are " + read_count_itr + " in the typhoon plot");
		
	}
	
	public static void write_output(String file_name, int[][] v_plot,
			int x_axis_window, int y_axis_high_frag, int plot_width, TF t
			) throws IOException{
		
		// Setup the output buffer
		BufferedWriter output = new BufferedWriter(new FileWriter(file_name));
		
		// Set up the separator
		String sep = ",";
		
		// Write the header
		for(int p = -x_axis_window; p <= x_axis_window; p++){
		
			// Update the sep at the end of the line
			if(p == x_axis_window){sep = "\n";}
			
			// Write the relative position
			output.write(p + sep);
			
		}
			
		// Write the v_plot matrix
		for(int f = 1; f <= y_axis_high_frag; f++){
			
			// Reset the separator
			sep = ",";
			
			// Write the fragment length
			output.write(f + sep);
			
			
			// if(t.getStrand() == '+'){
				
				// Iterate through each relative position on that fragment length
				for(int p = 0; p < plot_width; p++){
					
					// Update the sep at the end of the line
					if(p == (plot_width - 1)){sep = "\n";}
					
					// Write out the matrix
					output.write(v_plot[f][p] + sep);
				
				}
			
			/*	
				
			}else{
				
				// Iterate through each relative position on that fragment length
				for(int p = plot_width - 1; p >= 0; p--){
					
					// Update the sep at the end of the line
					if(p == 0){sep = "\n";}
					
					// Write out the matrix
					output.write(v_plot[f][p] + sep);
				
				}
				
			}
		
			*/		
					
		}
		
		// Close the output buffer
		output.close();
		
	}
	
	
	public static void main(String[] args) throws IOException {

		// Get the parameters
		String bam_file_dir = 
			"c:/Users/Jason/Documents/macalpine_lab/bam_files/";
						
		String feature_file_name = 		
			"L:/datasets/oridb_acs_feature_file_curated_829_sites.csv";
					
		String bam_file_id = "dm242";
		
		String bam_file_type = "wt";
		
		String output_file_dir = 
			"L:/datasets/dmmt_oridb_typhoon_plot_mat/";
		
		int x_axis_window = 1000;
		int y_axis_high_frag = 250;		
				
		// Set the output file header
		String output_file_footer = "_typhoon_plot_" + bam_file_type + "_" + bam_file_id + "_win_" + x_axis_window + 
									"_high_frag_" + y_axis_high_frag + ".csv";
				
		// Set the bam file name
		String bam_file_name = bam_file_dir + bam_file_id + ".bam";
				
		// Set the plot_width variable
		int plot_width = 2 * x_axis_window + 1;
		
		// Read in the ACS positions into the list
		ArrayList<TF> feature_list = TF.read_in_tf_list(feature_file_name);
		
		// Get the bam file
		SAMFileReader bam_file = new SAMFileReader(new File(bam_file_name), new File(bam_file_name + ".bai"));
				
		// Iterate through each feature
		for(int a = 0; a < 21; a++){
				
			// Create a new storage matrix		
			int[][] typhoon_plot = new int[y_axis_high_frag + 1][plot_width];
				
			// Get the current feature object
			TF cur_feature = feature_list.get(a);
					
			// Obtain the feature midpoint position
			int pos = cur_feature.getPos();
						
			// Set the lower and upper boundary thresholds
			int low_pos_boundary = (pos - x_axis_window);
			int high_pos_boundary = (pos + x_axis_window);
					
			// Get the SAM Record Iterator over that particular region
			SAMRecordIterator bam_itr = 
					bam_file.queryOverlapping(
						cur_feature.getChr(), 
						Math.max(1, pos - x_axis_window),
						pos + x_axis_window
						);
														   
			// Iterate through each read within this range											   
			update_typhoon_plot_matrix(bam_itr, typhoon_plot, 
					y_axis_high_frag, low_pos_boundary, high_pos_boundary, plot_width
					);
			
			// Make the output file name
			String output_file_name = output_file_dir + cur_feature.getName() + output_file_footer;
						
			// Write the output
			write_output(output_file_name, typhoon_plot, 
					x_axis_window, y_axis_high_frag, plot_width, cur_feature
					);
			
		}
		
		// Close the SAM File Reader
		bam_file.close();
	
		System.out.println("\tComplete!");
		
	}

}
