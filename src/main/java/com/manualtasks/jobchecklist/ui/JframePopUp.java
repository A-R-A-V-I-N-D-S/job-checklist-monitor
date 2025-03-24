package com.manualtasks.jobchecklist.ui;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class JframePopUp {

	public void showInProgressDialogBox() {
		JOptionPane.showMessageDialog(new JLabel(), "In progress");
//		JOptionPane.showConfirmDialog(new JLabel(), getClass(), "In Progress", 0);
//		JOptionPane.showOptionDialog(null, "Hello", "Empty?", JOptionPane.DEFAULT_OPTION,
//				JOptionPane.INFORMATION_MESSAGE, null, new Object[] {}, null);
	}

}
