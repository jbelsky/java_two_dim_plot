package twodimplot;

import java.io.IOException;
import java.util.Arrays;

public class CMDLineMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// Get the program to execute
		String java_executable_program = args[0];
		
		// Get the command line arguments
		String[] cmd_line_args = Arrays.copyOfRange(args, 1, args.length);
		
		// Choose the program to run based on the args[0]
		if(java_executable_program.equals("AggregateTyphoonPlot")){
			System.out.println("Executing AggregateTyphoonPlot!");
			AggregateTyphoonPlot.main(cmd_line_args);
		}else if(java_executable_program.equals("TyphoonPlot")){
			System.out.println("Executing TyphoonPlot!");
			TyphoonPlot.main(cmd_line_args);
		}else if(java_executable_program.equals("VPlot")){
			System.out.println("Executing VPlot!");
			VPlot.main(cmd_line_args);
		}else if(java_executable_program.equals("VPlotSmooth")){
			System.out.println("Executing VPlotSmooth!");
			VPlotSmooth.main(cmd_line_args);
		}else{
			System.out.println("Did not specify either 'AggregateTyphoonPlot', 'TyphoonPlot', 'VPlot', or 'VPlotSmooth' " +
							   "as first argument!");
		}

	}

}
