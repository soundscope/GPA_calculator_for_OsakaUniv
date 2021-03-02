package score_calculator_for_SIKcsv;

import java.awt.Dimension;
import javax.swing.JLabel;

public class StatusBar extends JLabel {


	private static final long serialVersionUID = 1L;

	public StatusBar() {
		super();
		super.setPreferredSize(new Dimension(100, 25));
		setStr("");
	}

	public void setStr(String str) {
		setText(str);        
	}        
}
