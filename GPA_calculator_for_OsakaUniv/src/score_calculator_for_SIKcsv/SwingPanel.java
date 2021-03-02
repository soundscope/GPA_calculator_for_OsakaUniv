package score_calculator_for_SIKcsv;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.*;



public class SwingPanel extends JFrame {
	Object saveOutputColumns[][] ; 
	Object saveOutputColumns2[][] ; 

	private static final long serialVersionUID = 1L;

	public SwingPanel(String string, ParseCSV parser) {
		setBounds( 10, 10, 800, 500);
		String[] columnNames = {"TERM", "GPA", "GP", "CREDITS", "CALCULATE_DATE"};
		int tmp;
		for(tmp = 0; tmp < parser.OutputColumns.length ; tmp++) 
			if (parser.OutputColumns[tmp][0] == null) break;  
		saveOutputColumns = new String[tmp][5];
		for(int j = 0; j < tmp; j++) 
			saveOutputColumns[j] = parser.OutputColumns[j].clone();

		JTable table = new JTable(saveOutputColumns, columnNames);
		DefaultTableColumnModel columnModel = (DefaultTableColumnModel)table.getColumnModel();
		TableColumn column = null;
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.RIGHT );
		for(int i = 1 ; i < 5 ; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
		}

		column = columnModel.getColumn(0);
		column.setPreferredWidth(65);

		column = columnModel.getColumn(1);
		column.setPreferredWidth(30);

		column = columnModel.getColumn(2);
		column.setPreferredWidth(15);

		column = columnModel.getColumn(3);
		column.setPreferredWidth(15);

		JTabbedPane tabs = new JTabbedPane();
		tabs.setUI(new BasicTabbedPaneUI());


		JScrollPane sp = new JScrollPane(table);
		String[] columnNames2 = {"TERM","GROUP1","GROUP2","CLASS" ,"CREDITS", "GRADES","GP"};
		for(tmp = 0; tmp < parser.OutputColumns2.length ; tmp++) 
			if (parser.OutputColumns2[tmp][0] == null) break;  
		saveOutputColumns2 = new Object[tmp][8];
		for(int j = 0; j < tmp; j++) {
			for(int k = 0; k < 7; k++) {
				if(k < 4) saveOutputColumns2[j][k] = parser.OutputColumns2[j][k];
				else 
					try {
						saveOutputColumns2[j][k] = Double.parseDouble(parser.OutputColumns2[j][k]);
					} catch (NumberFormatException e) {
						saveOutputColumns2[j][k] = Double.NaN;
					}

			}
		}

		DefaultTableModel model = new DefaultTableModel(saveOutputColumns2,columnNames2) {
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				case 1:
					return String.class;
				case 2:
					return String.class;
				case 3:
					return String.class;
				case 4:
					return Double.class;
				case 5:
					return Double.class;
				case 6:
					return Double.class;
				default:
					return Integer.class;
				}
			}
		};
		JTable table2 = new JTable(model);
		JScrollPane sp2= new JScrollPane(table2);
		table2.setAutoCreateRowSorter(true);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table2.getModel());
		int columnIndexToSort = 0;
		sorter.setComparator(columnIndexToSort, new Comparator<String>() {
		    @Override
		    public int compare(String name1, String name2) {
		    	String[] st1 = name1.split(" ");
		    	String[] st2 = name2.split(" ");
				HashMap<String,Integer> termToNum = new HashMap<String,Integer>();
				termToNum.put("Spring", 0); termToNum.put("Summer", 1);
				termToNum.put("Autumn", 2); termToNum.put("Winter", 3);
		    	if(Integer.parseInt(st1[0]) != Integer.parseInt(st2[0]))
		    		return Integer.compare(Integer.parseInt(st1[0]), Integer.parseInt(st2[0]));
		    	else 
		    		return Integer.compare(termToNum.get(st1[1]),termToNum.get(st2[1]));
		    }
		});
		table2.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));
		
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		
		
		sp.setPreferredSize(new Dimension(550, 400));
		JPanel p = new JPanel();
		p.add(sp);

		tabs.addTab("TermScore", sp);
		tabs.addTab("Classes",  sp2);
		getContentPane().add(tabs);
		table.setRowHeight(20);
		table2.setRowHeight(20);
		table.setFont(new Font("Monospaced", Font.PLAIN, 15));
		table2.setFont(new Font("Monospaced", Font.PLAIN, 15));
		columnModel = (DefaultTableColumnModel)table2.getColumnModel();
		column = columnModel.getColumn(0);
		column.setPreferredWidth(45);

		column = columnModel.getColumn(1);
		column.setPreferredWidth(130);
		column = columnModel.getColumn(2);
		column.setPreferredWidth(100);
		column = columnModel.getColumn(3);
		column.setPreferredWidth(140);
		column = columnModel.getColumn(4);
		column.setPreferredWidth(5);
		column = columnModel.getColumn(5);
		column.setPreferredWidth(5);
		column = columnModel.getColumn(6);
		column.setPreferredWidth(5);
		
		StatusBar sb = new StatusBar();
		getContentPane().add(sb, java.awt.BorderLayout.SOUTH);
		DecimalFormat fm = new DecimalFormat("0.#");
		table2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {		
				Double sum[] = {0.0,0.0,0.0};
				Double offset = 0.0;
				for (int x:table2.getSelectedRows()) {
					for(int i = 0; i < 3; i++) {
						if((Double.isNaN((Double)(table2.getValueAt(x, 4 + i))))){
							offset += (Double)table2.getValueAt(x, 4);
							break;
						}
						sum[i] +=  (Double) table2.getValueAt(x, 4 + i);
					}

					sb.setStr(
							"selected sum       CREDITS: "+ fm.format(sum[0]) + "      GRADES: " 
									+ fm.format(sum[1]) + "      GP: " + fm.format(sum[2]) + "      GPA: " 
									+ sum[2]  / (sum[0] - offset)
							);
				}
			}
		});

		table2.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {		
				Double sum[] = {0.0,0.0,0.0};
				int offset = 0;
				for (int x:table2.getSelectedRows()) {
					for(int i = 0; i < 3; i++) {
						if((Double.isNaN((Double)(table2.getValueAt(x, 4 + i))))){
							offset += (Double) table2.getValueAt(x, 4);
							break;
						}
						sum[i] +=  (Double) table2.getValueAt(x, 4 + i);
					}

					sb.setStr(
							"selected sum       CREDITS: "+ fm.format(sum[0]) + "      GRADES: " 
									+ fm.format(sum[1]) + "      GP: " + fm.format(sum[2]) + "      GPA: " 
									+ sum[2]  / (sum[0] - offset)
							);
				}
			}
		});
	}
}
