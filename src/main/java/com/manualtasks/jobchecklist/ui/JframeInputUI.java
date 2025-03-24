package com.manualtasks.jobchecklist.ui;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.SHIFT_TIME_FORMAT;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
public class JframeInputUI extends JFrame {

	private JTextField orderDateField;
	private JTextField shiftField;
	private JRadioButton customTimingsYes;
	private JRadioButton customTimingsNo;
	private JTextField shiftStartTimeField;
	private JTextField shiftEndTimeField;
	private JButton submitButton;
	private JDialog loadingDialog;

	private String orderDate;
	private String shift;
	private boolean customTimings;
	private String shiftStartTime;
	private String shiftEndTime;

	public CompletableFuture<ArrayList<String>> getInputDetails() {

		JframePopUp popUp = new JframePopUp();
		popUp.showInProgressDialogBox();

		setTitle("Job Checklist Monitor");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.fill = GridBagConstraints.HORIZONTAL;

		// Order Date field
		JLabel orderDateLabel = new JLabel("Order Date:");
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(orderDateLabel, constraints);

		orderDateField = new JTextField(20);
		orderDateField.setText("MM/dd/yy");
		orderDateField.setForeground(Color.LIGHT_GRAY);
		constraints.gridx = 1;
		constraints.gridy = 0;
		add(orderDateField, constraints);
		orderDateField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (orderDateField.getText().isEmpty()) {
					orderDateField.setText("MM/dd/yy");
					orderDateField.setForeground(Color.LIGHT_GRAY);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (orderDateField.getText().equals("MM/dd/yy")) {
					orderDateField.setText("");
					orderDateField.setForeground(Color.BLACK);
				}
			}
		});

		// Shift field
		JLabel shiftLabel = new JLabel("Shift:");
		constraints.gridx = 0;
		constraints.gridy = 1;
		add(shiftLabel, constraints);

		shiftField = new JTextField(20);
		constraints.gridx = 1;
		constraints.gridy = 1;
		add(shiftField, constraints);

		// Custom Timings Radio Buttons
		JLabel customTimingsLabel = new JLabel("Custom Timings:");
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(customTimingsLabel, constraints);

		customTimingsYes = new JRadioButton("Yes");
		customTimingsNo = new JRadioButton("No");
		ButtonGroup customTimingsGroup = new ButtonGroup();
		customTimingsGroup.add(customTimingsYes);
		customTimingsGroup.add(customTimingsNo);

		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		radioPanel.add(customTimingsYes);
		radioPanel.add(customTimingsNo);
		constraints.gridx = 1;
		constraints.gridy = 2;
		add(radioPanel, constraints);

		// Shift Start Time and End Time (hidden by default)
		JLabel shiftStartTimeLabel = new JLabel("Shift Start Time:");
		shiftStartTimeField = new JTextField(20);
		shiftStartTimeField.setText(SHIFT_TIME_FORMAT);
		shiftStartTimeField.setForeground(Color.LIGHT_GRAY);
		shiftStartTimeField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (shiftStartTimeField.getText().isEmpty()) {
					shiftStartTimeField.setText(SHIFT_TIME_FORMAT);
					shiftStartTimeField.setForeground(Color.LIGHT_GRAY);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (shiftStartTimeField.getText().equals(SHIFT_TIME_FORMAT)) {
					shiftStartTimeField.setText("");
					shiftStartTimeField.setForeground(Color.BLACK);
				}
			}
		});

		JLabel shiftEndTimeLabel = new JLabel("Shift End Time:");
		shiftEndTimeField = new JTextField(20);
		shiftEndTimeField.setText(SHIFT_TIME_FORMAT);
		shiftEndTimeField.setForeground(Color.LIGHT_GRAY);
		shiftEndTimeField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (shiftEndTimeField.getText().isEmpty()) {
					shiftEndTimeField.setText(SHIFT_TIME_FORMAT);
					shiftEndTimeField.setForeground(Color.LIGHT_GRAY);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (shiftEndTimeField.getText().equals(SHIFT_TIME_FORMAT)) {
					shiftEndTimeField.setText("");
					shiftEndTimeField.setForeground(Color.BLACK);
				}
			}
		});

		constraints.gridx = 0;
		constraints.gridy = 3;
		add(shiftStartTimeLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 3;
		add(shiftStartTimeField, constraints);

		constraints.gridx = 0;
		constraints.gridy = 4;
		add(shiftEndTimeLabel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 4;
		add(shiftEndTimeField, constraints);

		// Initially hide shift start and end time fields
		shiftStartTimeLabel.setVisible(false);
		shiftStartTimeField.setVisible(false);
		shiftEndTimeLabel.setVisible(false);
		shiftEndTimeField.setVisible(false);

		// Add action listeners for radio buttons
		customTimingsYes.addActionListener(e -> {
			shiftStartTimeLabel.setVisible(true);
			shiftStartTimeField.setVisible(true);
			shiftEndTimeLabel.setVisible(true);
			shiftEndTimeField.setVisible(true);
			revalidate();
			repaint();
		});

		customTimingsNo.addActionListener(e -> {
			shiftStartTimeLabel.setVisible(false);
			shiftStartTimeField.setVisible(false);
			shiftEndTimeLabel.setVisible(false);
			shiftEndTimeField.setVisible(false);
			revalidate();
			repaint();
		});

		// Submit button
		submitButton = new JButton("Submit");
		constraints.gridx = 1;
		constraints.gridy = 5;
		add(submitButton, constraints);

		setLocationRelativeTo(null);

		CompletableFuture<ArrayList<String>> futureArrayList = new CompletableFuture<>();

		submitButton.addActionListener(e -> {
			customTimings = customTimingsYes.isSelected();
			if (customTimings) {
				orderDate = orderDateField.getText();
				shift = shiftField.getText();
				customTimings = customTimingsYes.isSelected();
				shiftStartTime = shiftStartTimeField.getText();
				shiftEndTime = shiftEndTimeField.getText();
				futureArrayList
						.complete(new ArrayList<>(Arrays.asList(orderDate, shift, shiftStartTime, shiftEndTime)));
			} else {
				orderDate = orderDateField.getText();
				shift = shiftField.getText();
				futureArrayList.complete(new ArrayList<>(Arrays.asList(orderDate, shift)));
			}
			try {
				showLoadingDialog();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		enterKeyListener(orderDateField);
		enterKeyListener(shiftField);
		enterKeyListener(shiftStartTimeField);
		enterKeyListener(shiftEndTimeField);

		setVisible(true);

		new Timer(3000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				destroyLoadingDialog();

			}

		});

		return futureArrayList;
	}

	private void showLoadingDialog() throws IOException {
		loadingDialog = new JDialog(this, "Processing", true);
		loadingDialog.setSize(300, 150);
		loadingDialog.setLocationRelativeTo(this);
		loadingDialog.setLayout(new BorderLayout());

		JLabel msgLabel = new JLabel("Checklist process is in progress", JLabel.CENTER);
		loadingDialog.add(msgLabel, BorderLayout.SOUTH);

		Icon theImage = new ImageIcon(getClass().getResource("/static/images/loading-spinner-1.gif"));
		JLabel loadingLabel = new JLabel(theImage);
		loadingDialog.add(loadingLabel, BorderLayout.CENTER);
		loadingDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		loadingDialog.setVisible(true);

	}

	private void destroyLoadingDialog() {
		if (loadingDialog != null && loadingDialog.isVisible()) {
			loadingDialog.dispose();
		}
	}

	public void disposeFrame() {
		dispose();
	}

	private void enterKeyListener(JComponent component) {
		component.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit");
		component.getActionMap().put("submit", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				submitButton.doClick();
			}
		});
	}

}
