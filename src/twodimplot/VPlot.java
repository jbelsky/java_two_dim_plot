package twodimplot;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import genomics_functions.TF;

public class VPlot {

	public static void update_v_plot_matrix(SAMRecordIterator itr, 
			int[][] v_plot, 
			TF feature,
			int y_axis_high_frag,
			int low_pos_boundary,
			int high_pos_boundary,
			int plot_width
			){
				
			// Iterate through each read within this range											   
			while(itr.hasNext()){

				// Obtain the read
				SAMRecord read = itr.next();
						
				// Get the read coordinates
				int read_start = read.getAlignmentStart();
				int read_width = read.getInferredInsertSize();
				int read_mid = read_start + (read_width/2);
						
				// Subset on those reads that meet the x-axis and y-axis conditions
				if((read_width <= y_axis_high_frag) &&
				   (read_mid >= low_pos_boundary) && (read_mid <= high_pos_boundary)
				  ){
						
					// Create the relative pos
					int rel_pos = read_mid - low_pos_boundary;
							
					// Update the coordinate if the strand is negative
					if(feature.getStrand() == '-'){
						rel_pos = (plot_width - 1) - rel_pos;
					}
						
					// Update the v-plot-count at that position
					v_plot[read_width][rel_pos]++;
							
				}
							
			}
			
			// Close the iterator
			itr.close();
		
		
	}
	
	public static void write_output(String file_name, int[][] v_plot,
			int x_axis_window, int y_axis_high_frag, int plot_width
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
			
			// Iterate through each relative position on that fragment length
			for(int p = 0; p < plot_width; p++){
				
				// Update the sep at the end of the line
				if(p == (plot_width - 1)){sep = "\n";}
				
				// Write out the matrix
				output.write(v_plot[f][p] + sep);
			
			}
					
		}
		
		// Close the output buffer
		output.close();
		
	}
	
	
	public static void main(String[] args) throws IOException {

		// Get the file name properties
		String bam_file_name = args[0];
		String feature_file_name = args[1];
		String output_file_header = args[2];
		
		// Set the x-axis window
		int x_axis_window = Integer.parseInt(args[3]);
				
		// Set the y-limit threshold
		int y_axis_high_frag = Integer.parseInt(args[4]);
		
		
		
		
		
		/////////////////////////////////////////////////////////////////////////////////
		
		// Set the output file name footer
		String output_file_footer = "_xwin_" + x_axis_window + "_ywin_" + y_axis_high_frag + ".csv";
		
		// Set the plot_width variable
		int plot_width = 2 * x_axis_window + 1;
			
		System.out.println("Making the v-plot...");
		
		// Read in the ACS positions into the list
		ArrayList<TF> feature_list = TF.read_in_tf_list(feature_file_name);
								
		// Get the bam file
		SAMFileReader bam_file = new SAMFileReader(new File(bam_file_name), new File(bam_file_name + ".bai"));
	
		// Create a new storage matrix		
		int[][] v_plot = new int[y_axis_high_frag + 1][plot_width];
	
		// Iterate through each acs_list
		for(int a = 0; a < feature_list.size(); a++){
		
			// Get the current feature object
			TF cur_feature = feature_list.get(a);
			
			System.out.print("Updating the v-plot with reads around feature id " + cur_feature.getName() + "\r");
			
			// Obtain the feature midpoint position
			int pos = cur_feature.getPos();
						
			// Set the lower and upper boundary thresholds
			int low_pos_boundary = (pos - x_axis_window);
			int high_pos_boundary = (pos + x_axis_window);
				
			// Get the SAM Record Iterator over that particular region
			SAMRecordIterator bam_itr = 
					bam_file.queryOverlapping(
						cur_feature.getChr(), 
						pos - 2 * x_axis_window,
						pos + 2 * x_axis_window
						);
														   
			// Iterate through each read within this range											   
			update_v_plot_matrix(bam_itr, v_plot, cur_feature, 
					y_axis_high_frag, low_pos_boundary, high_pos_boundary, plot_width
					);
			
		}
		
		// Write the output
		write_output(output_file_header + output_file_footer, v_plot, 
				x_axis_window, y_axis_high_frag, plot_width
				);
		
		// Close the SAM File Reader
		bam_file.close();
					
		System.out.println("\nComplete!");
		
	}

}
