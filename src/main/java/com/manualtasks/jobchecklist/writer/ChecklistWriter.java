package com.manualtasks.jobchecklist.writer;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.*;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.manualtasks.jobchecklist.model.ChecklistTemplateData;

public class ChecklistWriter {

	private static Logger logger = LoggerFactory.getLogger(ChecklistWriter.class);

	public static void writeChecklist(XSSFSheet sheet, List<ChecklistTemplateData> templateDataList) {

		logger.info("Job checklist writer - START");

		// Writing rows data
		for (int i = 0; i < templateDataList.size(); i++) {
			XSSFRow row = sheet.createRow(i);
//			System.out.print(i+", ");
			for (int j = 0; j < TOTAL_COL_IN_CHECKLIST_SHEET; j++) {
				switch (j) {
				case 0:
					XSSFCell serialNoCell = row.createCell(j);
					// Only for the first column in Header row
//					System.out.println(" - "+"\""+templateDataList.get(i).getSerialNo()+"\"");
					if (i == 0) {
						serialNoCell.setCellValue(templateDataList.get(i).getSerialNo());
					} else {
						serialNoCell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
						serialNoCell.setCellValue(Integer.parseInt(templateDataList.get(i).getSerialNo()));
					}
					break;
				case 1:
					row.createCell(j).setCellValue(templateDataList.get(i).getType());
					break;
				case 2:
					row.createCell(j).setCellValue(templateDataList.get(i).getJobName());
					break;
				case 3:
					row.createCell(j).setCellValue(templateDataList.get(i).getFolderName());
					break;
				case 4:
					row.createCell(j).setCellValue(templateDataList.get(i).getBusinessReportName());
					break;
				case 5:
					row.createCell(j).setCellValue(templateDataList.get(i).getIsReportGenerated());
					break;
				case 6:
					row.createCell(j).setCellValue(templateDataList.get(i).getCtmServer());
					break;
				case 7:
					row.createCell(j).setCellValue(templateDataList.get(i).getSchedule());
					break;
				case 8:
					row.createCell(j).setCellValue(templateDataList.get(i).getFrequency());
					break;
				case 9:
					row.createCell(j).setCellValue(templateDataList.get(i).getShift());
					break;
				case 10:
					row.createCell(j).setCellValue(templateDataList.get(i).getJobDependency());
					break;
				case 11:
					row.createCell(j).setCellValue(templateDataList.get(i).getStartTime());
					break;
				case 12:
					row.createCell(j).setCellValue(templateDataList.get(i).getEndTime());
					break;
				case 13:
					row.createCell(j).setCellValue(templateDataList.get(i).getJobStatus());
					break;
				case 14:
					row.createCell(j).setCellValue(templateDataList.get(i).getIsLogsValidated());
					break;
				case 15:
					row.createCell(j).setCellValue(templateDataList.get(i).getErrorDetails());
					break;
				case 16:
					row.createCell(j).setCellValue(templateDataList.get(i).getResolution());
					break;
				case 17:
					row.createCell(j).setCellValue(templateDataList.get(i).getApplication());
					break;
				case 18:
					row.createCell(j).setCellValue(templateDataList.get(i).getRemarks());
					break;
				case 19:
					XSSFCell processingDateCell = row.createCell(j);
					DataFormat fmt = sheet.getWorkbook().createDataFormat();
					CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
					cellStyle.setDataFormat(fmt.getFormat("@"));
					processingDateCell.setCellStyle(cellStyle);
					processingDateCell.setCellValue(templateDataList.get(i).getProcessingDate());
					break;
				default:
					break;
				}
			}
//			System.out.println(templateDataList.get(i).toString());
		}

		logger.info("Job checklist writer - END");

	}

}
