package score_calculator_for_SIKcsv;

import java.util.HashMap;

public class ParseCSV {
	//"学生所属コード","学籍番号","画面指定年度","画面指定学期","通算ＧＰＡ","計算日時"	
	String AffiliationCode;
	String StudentID;
	String CalculateDate;
	public String TotalGPAbyKOAN;
	//"学生所属コード","学籍番号","画面指定年度","画面指定学期","年度","１学期ＧＰＡ","１学期計算日時","２学期ＧＰＡ","２学期計算日時"	
	String CalculateDates;
	public String OutputColumns[][] = new String[41][5]; 	public int index = 1;
	public int OutputColumnSums[][] = new int[41][3];
	public String OutputColumns2[][] = new String[200][7]; 	public int index2 = 0;
	public int OutputColumnSums2[][] = new int[200][3];
	public int excepts = 0;


	public ParseCSV(String st) {
		String br = System.getProperty("line.separator");

		String[] culumn = st.replaceAll(",\"([0-9]),([0-9]+)\",", ",\"$1$2\",").replace("\"","").split(br);
		String[][] matrix = new String[culumn.length + 10][];
		int i = 0;
		for(; i < culumn.length; i++) {
			if(i != 0 && ! culumn[i].substring(0, 1).matches("[0-9a-zA-Z]")) break;
			matrix[i] = culumn[i].split(",");
		}
		AffiliationCode = matrix[i - 1][0];
		StudentID = matrix[i - 1][1];
		CalculateDate = matrix[i - 1][5];
		TotalGPAbyKOAN = matrix[i++ - 1][4];
		int matLen = 0;
		int j = i;
		for(; j < culumn.length; j++) {
			if(j != 0 && ! culumn[j].substring(0, 1).matches("[0-9a-zA-Z]")) break;
			matrix[matLen++] = culumn[j].split(",");
		}
	


		int estimatedTerm = 4;		
		// 0 -> spring  .. 3 -> winter 
		for(int k = 0; k < 4; k++) {
			if( matLen <= k + 1 || !matrix[k][4].equals(matrix[k + 1][4])) {
				estimatedTerm = k; 
				break;
			}
		}
		final String[] TERM = {
				"Spring", "Summer", "Autumn", "Winter"
		};
		HashMap<String,Integer> hmap = new HashMap<String,Integer>();
		for(; i < culumn.length; i++) {
			if(i != 0 && ! culumn[i].substring(0, 1).matches("[0-9a-zA-Z]")) break;
			matrix[0] = culumn[i].split(",");
			hmap.put(matrix[0][4] + "_" + (estimatedTerm  + 4) % 4, index); 
			OutputColumns[index][0] = matrix[0][4] + " " +TERM[(estimatedTerm + 4)%4]; // term
			OutputColumns[index][4] = matrix[0][6]; // calculated date
			estimatedTerm = (estimatedTerm - 1 + 4) % 4;
			index++;
		}

		HashMap<String,Integer> termToNum = new HashMap<String,Integer>();
		termToNum.put("春学期", 0); termToNum.put("夏学期", 1);
		termToNum.put("秋学期", 2); termToNum.put("冬学期", 3);
		HashMap<String,Integer> alpToNum = new HashMap<String,Integer>();
		alpToNum.put("F", 0); alpToNum.put("C", 1);
		alpToNum.put("Ｆ", 0); alpToNum.put("Ｃ", 1);
		alpToNum.put("B", 2); alpToNum.put("A", 3);
		alpToNum.put("Ｂ", 2); alpToNum.put("Ａ", 3);
		alpToNum.put("S", 4);
		alpToNum.put("Ｓ", 4);				
		for(matLen = 0; i < culumn.length; i++) {
			if(culumn[i].lastIndexOf("評語,合否") != -1) break;
		}
		for(i++; i < culumn.length; i++) {
			if(i != 0 && ! culumn[i].substring(0, 1).matches("[0-9a-zA-Z]")) break;
			matrix[0] = culumn[i].split(",");

			int value = 0;
			try {
				value = hmap.get(matrix[0][11] + "_" +  termToNum.get(matrix[0][12]));
				if(matrix[0][13].matches("[合否]")) {
					if (matrix[0][13].equals("合") ) {
						
						OutputColumnSums[value][2] += Integer.parseInt(matrix[0][10]);
						excepts += Integer.parseInt(matrix[0][10]);
					}
					OutputColumns2[index2][0] = matrix[0][11] + " " + TERM[termToNum.get(matrix[0][12])]; // term
					OutputColumns2[index2][1] = matrix[0][5];
					OutputColumns2[index2][2] = matrix[0][6];
					OutputColumns2[index2][3] = matrix[0][7];
					OutputColumns2[index2][4] = matrix[0][10];
					OutputColumns2[index2][5] = "0 (Not counted)";
					OutputColumns2[index2++][6] = "0 (Not counted)";
					continue;
				}
			} catch (NullPointerException e) {
				System.err.println("cannot find term (key -> value not found)");
				System.err.println("sorry, this program does not support fall enrollment");
				return;
			}
			OutputColumns2[index2][0] = matrix[0][11] + " " + TERM[termToNum.get(matrix[0][12])]; // term
			OutputColumns2[index2][1] = matrix[0][5];
			OutputColumns2[index2][2] = matrix[0][6];
			OutputColumns2[index2][3] = matrix[0][7];
			OutputColumns2[index2][4] = matrix[0][10];
			OutputColumns2[index2][5] = "" + alpToNum.get(matrix[0][13]);
			OutputColumns2[index2++][6] = "" + (alpToNum.get(matrix[0][13]) * Integer.parseInt(matrix[0][10]));
			OutputColumnSums[value][0] += Integer.parseInt(matrix[0][10]); // creditの総和
			OutputColumnSums[value][1] += alpToNum.get(matrix[0][13]) * Integer.parseInt(matrix[0][10]); // score * credit
		}		
	}
}
