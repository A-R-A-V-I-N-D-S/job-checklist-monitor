package com.manualtasks.jobchecklist.utils;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.*;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelDesign {

	private static Logger logger = LoggerFactory.getLogger(ExcelDesign.class);

	public static void setChecklistExcelDesign(XSSFWorkbook outputWorkbook) {

		logger.info("Excel designer - START");

		XSSFSheet sheet = outputWorkbook.getSheet(CHECKLIST_SHEET_NAME);
		setDefaultChecklistStyle(sheet);

		if (outputWorkbook.getNumberOfSheets() > 1) {
			XSSFSheet untrackedJobSheet = outputWorkbook.getSheet(UNTRACKED_JOB_SHEET_NAME);
			setUntrackedJobSheetStyle(untrackedJobSheet);
		}

		logger.info("Excel designer - END");

	}

	private static void setUntrackedJobSheetStyle(XSSFSheet untrackedJobSheet) {
		// All rows Style
		XSSFCellStyle rowCellStyle = untrackedJobSheet.getWorkbook().createCellStyle();
		rowCellStyle.setBorderLeft(BorderStyle.THIN);
		rowCellStyle.setBorderRight(BorderStyle.THIN);
		rowCellStyle.setBorderTop(BorderStyle.THIN);
		rowCellStyle.setBorderBottom(BorderStyle.THIN);
		for (Row row : untrackedJobSheet)
			setRowsStyle(row, rowCellStyle);

		// Setting wrap text for some cells
		XSSFCellStyle wrapCellStyle = untrackedJobSheet.getWorkbook().createCellStyle();
		wrapCellStyle.setBorderLeft(BorderStyle.THIN);
		wrapCellStyle.setBorderRight(BorderStyle.THIN);
		wrapCellStyle.setBorderTop(BorderStyle.THIN);
		wrapCellStyle.setBorderBottom(BorderStyle.THIN);
		wrapCellStyle.setWrapText(true);
		for (Row row : untrackedJobSheet) {
			row.getCell(1).setCellStyle(wrapCellStyle);
		}

		// Header row color and font
		XSSFFont font = untrackedJobSheet.getWorkbook().createFont();
		font.setBold(true);

		XSSFCellStyle headerCellStyle = untrackedJobSheet.getWorkbook().createCellStyle();
		headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(180, 198, 231)));
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		wrapCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerCellStyle.setFont(font);
		headerCellStyle.setWrapText(true);
		setRowsStyle(untrackedJobSheet.getRow(0), headerCellStyle);

		untrackedJobSheet.setColumnWidth(0, (35 * 256) + 200);
		untrackedJobSheet.setColumnWidth(1, (70 * 256) + 200);

		// Set tab color
		untrackedJobSheet.setTabColor(IndexedColors.RED.getIndex());
	}

	private static void setDefaultChecklistStyle(XSSFSheet sheet) {
		adjustColumnWidth(sheet);

		// All rows Style
		XSSFCellStyle rowCellStyle = sheet.getWorkbook().createCellStyle();
		rowCellStyle.setBorderLeft(BorderStyle.THIN);
		rowCellStyle.setBorderRight(BorderStyle.THIN);
		rowCellStyle.setBorderTop(BorderStyle.THIN);
		rowCellStyle.setBorderBottom(BorderStyle.THIN);
		for (Row row : sheet)
			setRowsStyle(row, rowCellStyle);

		// Setting wrap text for some cells
		XSSFCellStyle wrapCellStyle = sheet.getWorkbook().createCellStyle();
		wrapCellStyle.setBorderLeft(BorderStyle.THIN);
		wrapCellStyle.setBorderRight(BorderStyle.THIN);
		wrapCellStyle.setBorderTop(BorderStyle.THIN);
		wrapCellStyle.setBorderBottom(BorderStyle.THIN);
		wrapCellStyle.setWrapText(true);
		for (Row row : sheet) {
			row.getCell(10).setCellStyle(wrapCellStyle);
			row.getCell(15).setCellStyle(wrapCellStyle);
		}

		// Header row color and font
		XSSFFont font = sheet.getWorkbook().createFont();
		font.setBold(true);

		XSSFCellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
		headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(180, 198, 231)));
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setFont(font);
		headerCellStyle.setWrapText(true);
		for (Row row : sheet)
			highlightFonts(row.getCell(15));

		setRowsStyle(sheet.getRow(0), headerCellStyle);

		sheet.createFreezePane(0, 1);
		sheet.setAutoFilter(new CellRangeAddress(0, sheet.getLastRowNum(), 0, TOTAL_COL_IN_CHECKLIST_SHEET - 2));

	}

	private static void highlightFonts(Cell cell) {
		CreationHelper creationHelper = cell.getSheet().getWorkbook().getCreationHelper();
		Font highlightFont = cell.getSheet().getWorkbook().createFont();
		highlightFont.setColor(IndexedColors.RED.getIndex());
		String cellContent = cell.getStringCellValue();
		RichTextString richTextString = creationHelper.createRichTextString(cellContent);
		if (cellContent.length() >= 1 && !cellContent.equals("Error Details")) {
			richTextString.applyFont(0, 2, highlightFont);
		}
//		cell.setCellValue(richTextString);
	}

	private static void setRowsStyle(Row row, XSSFCellStyle rowCellStyle) {
		for (Cell cell : row) {
			cell.setCellStyle(rowCellStyle);
		}
	}

	private static void adjustColumnWidth(XSSFSheet sheet) {
		sheet.setColumnWidth(2, (70 * 256) + 200);
		sheet.setColumnWidth(3, (70 * 256) + 200);
		sheet.setColumnWidth(4, (50 * 256) + 200);
		sheet.setColumnWidth(5, (26 * 256) + 200);
		sheet.setColumnWidth(6, (14 * 256) + 200);
		sheet.setColumnWidth(7, (24 * 256) + 200);
		sheet.setColumnWidth(8, (35 * 256) + 200);
		sheet.setColumnWidth(9, (8 * 256) + 200);
		sheet.setColumnWidth(10, (70 * 256) + 200);
		sheet.setColumnWidth(11, (19 * 256) + 200);
		sheet.setColumnWidth(12, (19 * 256) + 200);
		sheet.setColumnWidth(13, (9 * 256) + 200);
		sheet.setColumnWidth(14, (15 * 256) + 200);
		sheet.setColumnWidth(15, (150 * 256) + 200);
		sheet.setColumnWidth(16, (40 * 256) + 200);
		sheet.setColumnWidth(17, (40 * 256) + 200);
		sheet.setColumnWidth(18, (40 * 256) + 200);
		sheet.setColumnWidth(19, (19 * 256) + 200);
	}

}
