package com.manualtasks.jobchecklist.controller;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.CHECKLIST_SHEET_NAME;
import static com.manualtasks.jobchecklist.utils.ClassDataUtils.CTM_DETAILS_SHEET_NAME;
import static com.manualtasks.jobchecklist.utils.ClassDataUtils.BATCH_SERVERS_LIST;
import static com.manualtasks.jobchecklist.utils.ClassDataUtils.JOB_NAME_AND_LOG_NAME_MAP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;

import com.manualtasks.jobchecklist.components.FileClosureValidator;
import com.manualtasks.jobchecklist.components.JSchSpringBatchLogsValidatingService;
import com.manualtasks.jobchecklist.components.JobLogsEntryService;
import com.manualtasks.jobchecklist.components.JobTimingsEntryService;
import com.manualtasks.jobchecklist.model.ChecklistTemplateData;
import com.manualtasks.jobchecklist.model.JFrameWindowUI;
import com.manualtasks.jobchecklist.model.JobDetailsData;
import com.manualtasks.jobchecklist.reader.ChecklistTemplateReader;
import com.manualtasks.jobchecklist.reader.JobDetailsReader;
import com.manualtasks.jobchecklist.utils.ExcelDesign;
import com.manualtasks.jobchecklist.writer.ChecklistWriter;

@Controller
public class ApplicationController {

	@Value("${file.input.name}")
	private String inpFileAddress;

	@Value("${file.output.name}")
	private String outFileAddress;

	private JobTimingsEntryService jobTimingsEntryService;

	private JSchSpringBatchLogsValidatingService logsValidatingService;

	private FileClosureValidator fileClosureValidator;

	private JobLogsEntryService jobLogsEntryService;

	private JFrameWindowUI jFrameWindowUI;

	@Autowired
	public ApplicationController(JobTimingsEntryService jobTimingsEntryService,
			JSchSpringBatchLogsValidatingService logsValidatingService, JobLogsEntryService jobLogsEntryService,
			FileClosureValidator fileClosureValidator, JFrameWindowUI jFrameWindowUI) {
		this.jobTimingsEntryService = jobTimingsEntryService;
		this.logsValidatingService = logsValidatingService;
		this.jobLogsEntryService = jobLogsEntryService;
		this.fileClosureValidator = fileClosureValidator;
		this.jFrameWindowUI = jFrameWindowUI;
	}

	public void executeTheTask() throws IOException, ParseException, InterruptedException, ExecutionException {
		// Input File stream
		while (!fileClosureValidator.ifFileOpenWaitForClosed(inpFileAddress))
			;
		FileInputStream fileInputStream = new FileInputStream(inpFileAddress);

		// Declaring the sheets
		XSSFWorkbook inputWorkbook = new XSSFWorkbook(fileInputStream);
		XSSFSheet templateSheet = inputWorkbook.getSheet(CHECKLIST_SHEET_NAME);
		XSSFSheet jobDetailsSheet = inputWorkbook.getSheet(CTM_DETAILS_SHEET_NAME);

//		CompletableFuture<ArrayList<String>> tempArrayList1 = jFrameWindowUI.getInputDetails();
//		ArrayList<String> tempArrayList2 = tempArrayList1.get();
//		System.out.println(tempArrayList2.toString());

		for (String key : JOB_NAME_AND_LOG_NAME_MAP.keySet()) {
			System.out.println(JOB_NAME_AND_LOG_NAME_MAP.get(key));
		}

		// Get the order date from the user, for which the details needs to be filled
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the Order Date that you need to enter timings for: ");
		String gvnOrdrDate = scan.nextLine();

		List<ChecklistTemplateData> jobChecklistDataList = ChecklistTemplateReader.readSheet(templateSheet);
		List<JobDetailsData> jobDetailsData = JobDetailsReader.readSheet(jobDetailsSheet);
		jobTimingsEntryService.fillJobTimingsAndStatus(jobChecklistDataList, jobDetailsData, gvnOrdrDate);

		System.out.println("Enter the shift: ");
		String shift = scan.nextLine();
		while (!(shift.equalsIgnoreCase("s1") || shift.equalsIgnoreCase("s2") || shift.equalsIgnoreCase("s3"))) {
			System.err.println("Enter correct value: ");
			shift = scan.nextLine();
		}

		System.out.println("Do you want to check logs for custom timings(y/n):");
		String response = scan.nextLine();
		while (!(response.equalsIgnoreCase("y") || response.equalsIgnoreCase("n"))) {
			System.err.println("Enter correct value: ");
			response = scan.nextLine();
		}
		Map<String, ArrayList<String>> listOfErrorsOfLogs = new HashMap<>();

		if (response.equalsIgnoreCase("y")) {
			System.out.println("Enter Shift start time in EST(MM/dd/yy HH:mm):");
			String shiftStartTime = scan.nextLine();
			System.out.println(shiftStartTime);
			System.out.println("Enter Shift end time in EST(MM/dd/yy HH:mm):");
			String shiftEndTime = scan.nextLine();
			System.out.println(shiftEndTime);
			listOfErrorsOfLogs = validateLogsForGvnTime(shift, shiftStartTime, shiftEndTime, listOfErrorsOfLogs);
		} else {
			listOfErrorsOfLogs = validateLogsRealTime(shift, listOfErrorsOfLogs);
		}
		scan.close();

		if (!(listOfErrorsOfLogs == null))
			jobLogsEntryService.fillJobErrorsAndValidationStatus(listOfErrorsOfLogs, jobChecklistDataList, shift);

		// Write output file
		XSSFWorkbook outputWorkbook = new XSSFWorkbook();
		XSSFSheet checklistSheet = outputWorkbook.createSheet(CHECKLIST_SHEET_NAME);
		ChecklistWriter.writeChecklist(checklistSheet, jobChecklistDataList);
		ExcelDesign.setChecklistExcelDesign(outputWorkbook);
		while (!fileClosureValidator.ifFileOpenWaitForClosed(outFileAddress))
			;
		FileOutputStream fileOutputStream = new FileOutputStream(outFileAddress);
		outputWorkbook.write(fileOutputStream);
		fileOutputStream.close();
	}

	public Map<String, ArrayList<String>> validateLogsRealTime(String shift,
			Map<String, ArrayList<String>> listOfErrorsOfLogs) throws InterruptedException, ExecutionException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		CompletableFuture<Map<String, ArrayList<String>>> completableFutureList1 = logsValidatingService
				.validateLogsInServer(shift, BATCH_SERVERS_LIST[0]);
		CompletableFuture<Map<String, ArrayList<String>>> completableFutureList2 = logsValidatingService
				.validateLogsInServer(shift, BATCH_SERVERS_LIST[1]);
		CompletableFuture<Map<String, ArrayList<String>>> completableFutureList3 = logsValidatingService
				.validateLogsInServer(shift, BATCH_SERVERS_LIST[2]);
		CompletableFuture.allOf(completableFutureList1, completableFutureList2, completableFutureList3).join();
		Map<String, ArrayList<String>> listOfErrorsInLogs1 = completableFutureList1.get();
		Map<String, ArrayList<String>> listOfErrorsInLogs2 = completableFutureList2.get();
		Map<String, ArrayList<String>> listOfErrorsInLogs3 = completableFutureList3.get();
		listOfErrorsOfLogs.putAll(listOfErrorsInLogs1);
		listOfErrorsOfLogs.putAll(listOfErrorsInLogs2);
		listOfErrorsOfLogs.putAll(listOfErrorsInLogs3);
		stopWatch.stop();
		System.err.println(stopWatch.getTotalTimeSeconds());
		return listOfErrorsOfLogs;
	}

	public Map<String, ArrayList<String>> validateLogsForGvnTime(String shift, String shiftStartTime,
			String shiftEndTime, Map<String, ArrayList<String>> listOfErrorsOfLogs)
			throws InterruptedException, ExecutionException {
		CompletableFuture<Map<String, ArrayList<String>>> completableFutureList1 = logsValidatingService
				.validateLogsInServer(shift, shiftStartTime, shiftEndTime, BATCH_SERVERS_LIST[0]);
		CompletableFuture<Map<String, ArrayList<String>>> completableFutureList2 = logsValidatingService
				.validateLogsInServer(shift, shiftStartTime, shiftEndTime, BATCH_SERVERS_LIST[1]);
		CompletableFuture<Map<String, ArrayList<String>>> completableFutureList3 = logsValidatingService
				.validateLogsInServer(shift, shiftStartTime, shiftEndTime, BATCH_SERVERS_LIST[2]);
		CompletableFuture.allOf(completableFutureList1, completableFutureList2, completableFutureList3).join();
		Map<String, ArrayList<String>> listOfErrorsInLogs1 = completableFutureList1.get();
		Map<String, ArrayList<String>> listOfErrorsInLogs2 = completableFutureList2.get();
		Map<String, ArrayList<String>> listOfErrorsInLogs3 = completableFutureList3.get();
		listOfErrorsOfLogs.putAll(listOfErrorsInLogs1);
		listOfErrorsOfLogs.putAll(listOfErrorsInLogs2);
		listOfErrorsOfLogs.putAll(listOfErrorsInLogs3);
		return listOfErrorsOfLogs;
	}

}
