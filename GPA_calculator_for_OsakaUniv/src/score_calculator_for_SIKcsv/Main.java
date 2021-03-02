package score_calculator_for_SIKcsv;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
	public static void main(String[] args) throws UnsupportedEncodingException {

		byte[] buf = null;
		FileUtils fs = new FileUtils();
		if(fs.getFile("./") != null) {
			try {
				buf = fs.readFile();
			} catch (IOException e) {
				System.err.println("cannot read");
				e.printStackTrace();
				return;
			}
		} else if (fs.getFile("/home/soundscope/Downloads") != null) {
			try {
				buf = fs.readFile();
			} catch (IOException e) {
				System.err.println("cannot read");
				return;
			}
		} else {
			JFileChooser filechooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("SIK_CSV FILE", "csv");
			filechooser.setFileFilter(filter);
			int selected = filechooser.showOpenDialog(null);
			if (selected == JFileChooser.APPROVE_OPTION){
				try {
					File file = filechooser.getSelectedFile();
					buf = fs.readFile(file);
				} catch (IOException e) {
					System.err.println("cannot read SIKcsv");
					return;
				}
			}
		}
		if(buf == null) {
			System.err.println("cannot read");
			return;
		}
		String st = null;
		if(fs.isUTF8orSJIS(buf, "UTF8")) st = new String(buf, "UTF8");
		else if(fs.isUTF8orSJIS(buf, "Shift-JIS")) st = new String(buf, "Shift-JIS");
		else {
			System.err.println("cannot decide the code");
			return;
		}
		ParseCSV parser= new ParseCSV(st);

		for(int i = 1;i < parser.index; i++) {
			parser.OutputColumnSums[0][0] += parser.OutputColumnSums[i][0];
			parser.OutputColumnSums[0][1] += parser.OutputColumnSums[i][1];
			parser.OutputColumns[i][3] = "" + parser.OutputColumnSums[i][0];
			if (parser.OutputColumnSums[i][2] != 0)
				parser.OutputColumns[i][3] += "(" + parser.OutputColumnSums[i][2] + ")";
			parser.OutputColumns[i][2] = "" + parser.OutputColumnSums[i][1];
			if(parser.OutputColumnSums[i][0] == 0) parser.OutputColumns[i][1] = "0.00000";
			else parser.OutputColumns[i][1] = String.format("%.5f",
					Math.floor(((parser.OutputColumnSums[i][1] + 0.0) / 
							parser.OutputColumnSums[i][0]) * 100000) / 100000);	
		}
		parser.OutputColumns[0][0] = "Total";
		parser.OutputColumns[0][3] = "" + parser.OutputColumnSums[0][0] + "("+ parser.excepts +")";
		parser.OutputColumns[0][2] = "" + parser.OutputColumnSums[0][1];
		if(parser.OutputColumnSums[0][0] == 0) parser.OutputColumns[0][1] = "0.00000";
		else parser.OutputColumns[0][1] = String.format("%.5f",
				Math.floor(((parser.OutputColumnSums[0][1] + 0.0) / 
						parser.OutputColumnSums[0][0]) * 100000) / 100000);	

		SwingPanel swing = new SwingPanel("GPACalculator", parser);
		//swing.setExtendedState(SwingPanel.MAXIMIZED_BOTH);
		swing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		swing.setVisible(true);
		double delta = Math.abs(Math.floor(((parser.OutputColumnSums[0][1] + 0.0) / parser.OutputColumnSums[0][0]) * 100) / 100  
				-  Double.parseDouble(parser.TotalGPAbyKOAN)) ;
		if (delta < 0.01){
			swing.setTitle("Total GPA is consistent with KOAN calculation");
		} else {
			swing.setTitle("Total GPA is not mached"  + 
					" GPA by KOAN: " + parser.TotalGPAbyKOAN + " difference(ABS): " + String.format("%.5f", delta));
		}
					
	}

}
