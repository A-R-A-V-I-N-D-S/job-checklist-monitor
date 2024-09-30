package com.manualtasks.jobchecklist.reader;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.manualtasks.jobchecklist.model.JobDetailsData;

public class JobDetailsReader {

	private static Logger logger = LoggerFactory.getLogger(JobDetailsReader.class);

	public static List<JobDetailsData> readSheet(XSSFSheet jobDetailsSheet) {

		logger.info("CTM Job Details Report reader - START");

		// Create a new List
		List<JobDetailsData> jobDetailsList = new ArrayList<>();

		// Transform the row data to the defined data type
		for (Row row : jobDetailsSheet)
			jobDetailsList.add(createModelFromRow(row));

		logger.info("CTM Job Details Report reader - END");
		return jobDetailsList;
	}

	private static JobDetailsData createModelFromRow(Row row) {

		DataFormatter formatter = new DataFormatter();

		JobDetailsData jobDetailsData = new JobDetailsData();
		for (int i = 0; i < TOTAL_COL_IN_CTM_DETAILS_SHEET; i++) {
			switch (i) {
			case 1:
				jobDetailsData.setJobName(formatter.formatCellValue(row.getCell(i)));
				break;
			case 2:
				jobDetailsData.setJobStatus(formatter.formatCellValue(row.getCell(i)));
				break;
			case 10:
				jobDetailsData.setIsJobHeld(formatter.formatCellValue(row.getCell(i)));
				break;
			case 14:
				jobDetailsData.setStartTime(formatter.formatCellValue(row.getCell(i)));
				break;
			case 15:
				jobDetailsData.setEndTime(formatter.formatCellValue(row.getCell(i)));
				break;
			case 20:
				jobDetailsData.setOrderDate(formatter.formatCellValue(row.getCell(i)));
				break;
			case 26:
				jobDetailsData.setIsDeleted(formatter.formatCellValue(row.getCell(i)));
				break;
			case 28:
				jobDetailsData.setFolderName(formatter.formatCellValue(row.getCell(i)));
				break;
			default:
				break;
			}
			jobDetailsData.setJobDetailsFilled(false);
		}
		return jobDetailsData;
	}

}
