package com.manualtasks.jobchecklist.model;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
public class JFrameWindowUI extends JFrame implements ActionListener {

	private JTextField orderDateField;
	private JTextField shiftField;
	private JRadioButton customTimingsYes;
	private JRadioButton customTimingsNo;
	private JTextField shiftStartTimeField;
	private JTextField shiftEndTimeField;
	private JButton submitButton;

	private String orderDate;
	private String shift;
	private boolean customTImings;
	private String shiftStartTime;
	private String shiftEndTime;

	public JFrameWindowUI() {
		setTitle("Job Checklist Monitor");
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(7, 2)); // 7 rows, 2 columns

		// Order Date field
		JLabel orderDateLabel = new JLabel("Order Date:");
		orderDateField = new JTextField(20);
		add(orderDateLabel);
		add(orderDateField);

		// Shift field
		JLabel shiftLabel = new JLabel("Shift:");
		shiftField = new JTextField(20);
		add(shiftLabel);
		add(shiftField);

		// Custom Timings Radio Buttons
		JLabel customTimingsLabel = new JLabel("Custom Timings:");
		customTimingsYes = new JRadioButton("Yes");
		customTimingsNo = new JRadioButton("No");
		ButtonGroup customTimingsGroup = new ButtonGroup();
		customTimingsGroup.add(customTimingsYes);
		customTimingsGroup.add(customTimingsNo);

		add(customTimingsLabel);
		JPanel radioPanel = new JPanel();
		radioPanel.add(customTimingsYes);
		radioPanel.add(customTimingsNo);
		add(radioPanel);

		// Shift Start Time and End Time (hidden by default)
		JLabel shiftStartTimeLabel = new JLabel("Shift Start Time:");
		shiftStartTimeField = new JTextField(20);
		JLabel shiftEndTimeLabel = new JLabel("Shift End Time:");
		shiftEndTimeField = new JTextField(20);

		add(shiftStartTimeLabel);
		add(shiftStartTimeField);
		add(shiftEndTimeLabel);
		add(shiftEndTimeField);

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
		submitButton.addActionListener(this); // Registering this class as action listener
		add(submitButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {
			orderDate = orderDateField.getText();
			shift = shiftField.getText();
			customTImings = customTimingsYes.isSelected();
			shiftStartTime = shiftStartTimeField.getText();
			shiftEndTime = shiftEndTimeField.getText();
		}
	}

	public CompletableFuture<ArrayList<String>> getInputDetails() {

		setVisible(true);

		if (customTImings)
			return CompletableFuture.completedFuture(
					new ArrayList<String>(Arrays.asList(orderDate, shift, shiftStartTime, shiftEndTime)));
		else
			return CompletableFuture.completedFuture(new ArrayList<String>(Arrays.asList(orderDate, shift)));
	}

}
