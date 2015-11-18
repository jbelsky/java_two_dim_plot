package twodimplot;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import genomics_functions.DensityEst;


public class VPlotSmooth {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// Set the file names
		String input_file_name = args[0];
		String output_file_name = args[1];
		int xwin = Integer.parseInt(args[2]);
		int yheight = Integer.parseInt(args[3]);
		int win = Integer.parseInt(args[4]);
		double bw = Double.parseDouble(args[5]);
		
		// Create a distance matrix
		double[][] dist_mat = new double[2*win + 1][2*win + 1];
				
		// Iterate through each win_matrix
		for(int r = 0; r < dist_mat.length; r++){
			for(int c = 0; c < dist_mat[r].length; c++){
		
				dist_mat[r][c] = Math.sqrt(Math.pow(r-win, 2) + Math.pow(c-win, 2));

			}
		}
		
		
		
		// Open the BufferedReader
		BufferedReader input = new BufferedReader(new FileReader(input_file_name));
		
		// Read in the header
		String header = input.readLine();
		
		// Set the input matrix
		double[][] input_matrix = new double[yheight][2*xwin+1];
		
		// Initialize the line
		String line = "";
		
		// Read the input_matrix
		while((line = input.readLine()) != null){
			
			// Split on comma
			String[] line_arr = line.split(",");
			
			// Get the fragment length index
			int frag_length_idx = Integer.parseInt(line_arr[0]) - 1;
			
			// Enter into the input_matrix
			for(int idx = 1; idx < line_arr.length; idx++)
				input_matrix[frag_length_idx][idx-1] = Double.parseDouble(line_arr[idx]);
			
		}
		
		// Close the input buffer
		input.close();
		
		// Create the smooth output matrix
		double[][] smooth_matrix = new double[yheight][2*xwin+1];
		
		// Iterate through the smooth matrix
		for(int r = 0; r < smooth_matrix.length; r++){
			for(int c = 0; c < smooth_matrix[r].length; c++){
				
				// Get the smoothing part
				double[][] point_mat = DensityEst.get_two_dim_smooth(dist_mat, input_matrix[r][c], bw);
				
				// Update the smooth_matrix
				for(int r_win = 0; r_win < point_mat.length; r_win++){
					for(int c_win = 0; c_win < point_mat[0].length; c_win++){
						
						// Get the appropriate smooth_matrix coordinates to update
						int r_idx = (r - win) + r_win;
						int c_idx = (c - win) + c_win;
						
						// Check if these coordinates exist in the smooth_matrix
						if(r_idx >= 0 & r_idx < smooth_matrix.length &
						   c_idx >= 0 & c_idx < smooth_matrix[0].length
						  ){
						
							// Update the smooth_matrix
							smooth_matrix[r_idx][c_idx] += point_mat[r_win][c_win];
							
						} // if loop
						
					} // for c_win loop
					
				} // for r_win loop
				
			} // for c loop
		
		} // for r loop
		
		// Open the output buffer
		BufferedWriter output = new BufferedWriter(new FileWriter(output_file_name));
		
		// Write the header
		output.write(header + "\n");
		
		// Set the DecimalFormat
		DecimalFormat df = new DecimalFormat("#.######");
		
		// Write out the smooth_matrix
		for(int r_sm = 0; r_sm < smooth_matrix.length; r_sm++){
			
			// Set the sep
			String sep = ",";
			
			// Write the fragment length
			output.write((r_sm + 1) + sep);
			
			for(int c_sm = 0; c_sm < smooth_matrix[r_sm].length; c_sm++){
				
				if(c_sm == smooth_matrix[r_sm].length - 1)
					sep = "\n";
				
				// Write the output
				output.write(df.format(smooth_matrix[r_sm][c_sm]) + sep);
				
			}
			
		}
		
		// Close the output buffer
		output.close();
				
	}
	
}
