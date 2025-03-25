package com.manualtasks.jobchecklist.components;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.JOB_NAME_AND_LOG_NAME_MAP;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

@Component
public class LogsReaderService {

	private TimeZone easternTimeZone = TimeZone.getTimeZone("America/New_York");

	private static Logger logger = LoggerFactory.getLogger(LogsReaderService.class);

	private Date dateNTime = new Date();

	@Autowired
	private InputDataValidatorService validatorService;

	public ArrayList<String> findLogOccuringDatesByShift(String shift) throws ParseException {

		SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");
		dateFormatter1.setTimeZone(easternTimeZone);

		SimpleDateFormat dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd");
		dateFormatter2.setTimeZone(easternTimeZone);

		String timeStampTdy_1, timeStampYest_1;
		String timeStampTdy_2, timeStampYest_2;

		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(easternTimeZone);
		calendar.add(Calendar.DATE, -1);

		ArrayList<String> logDates = new ArrayList<>();
		timeStampTdy_1 = dateFormatter1.format(dateNTime);
		timeStampTdy_2 = dateFormatter2.format(dateNTime);
		timeStampYest_1 = dateFormatter1.format(calendar.getTime());
		timeStampYest_2 = dateFormatter1.format(calendar.getTime());

		if (shift.equalsIgnoreCase("s1")) {
			logDates.add(timeStampYest_1);
			logDates.add(timeStampTdy_1);
			logDates.add(timeStampTdy_2);
			logDates.add(timeStampYest_2);
		} else {
			logDates.add(timeStampTdy_1);
			logDates.add(timeStampTdy_2);
		}
		return logDates;
	}

	public ArrayList<String> findLogOccuringDatesByShift(String shift, String shiftStartTime, String shiftEndTime)
			throws ParseException {

		SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");
		dateFormatter1.setTimeZone(easternTimeZone);

		SimpleDateFormat dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormatter2.setTimeZone(easternTimeZone);

		String timeStampTdy_1, timeStampYest_1;

//		System.out.println(shiftStartTime + " - " + shiftEndTime);

		ArrayList<String> logDates = new ArrayList<>();
		timeStampTdy_1 = dateFormatter1.format(dateFormatter2.parse(shiftEndTime));
		timeStampYest_1 = dateFormatter1.format(dateFormatter2.parse(shiftStartTime));
		if (shift.equalsIgnoreCase("s1")) {
			logDates.add(timeStampYest_1);
			logDates.add(timeStampTdy_1);
		} else {
			logDates.add(timeStampTdy_1);
		}
		return logDates;

	}

	public ArrayList<String> findLogOccuringDatesByShift2(String startTime, String endTime) throws ParseException {

		SimpleDateFormat dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormatter2.setTimeZone(easternTimeZone);

		SimpleDateFormat dateFormatter3 = new SimpleDateFormat("yyyy-MM-dd");
		dateFormatter3.setTimeZone(easternTimeZone);

		String timeStampTdy_1, timeStampYest_1;

		ArrayList<String> logDates = new ArrayList<>();
		timeStampTdy_1 = dateFormatter3.format(dateFormatter2.parse(endTime));
		timeStampYest_1 = dateFormatter3.format(dateFormatter2.parse(startTime));

		if (!timeStampTdy_1.equals(timeStampYest_1)) {
			logDates.add(timeStampYest_1);
			logDates.add(timeStampTdy_1);
		} else {
			logDates.add(timeStampTdy_1);
		}
		return logDates;

	}

	public void readLogFilesForErrors(ChannelSftp sftpChannel, String logPath,
			ArrayList<String> listOfDatesForLogsCheck, Map<String, ArrayList<String>> listOfErrorsOfLogs,
			FileWriter errorLogFile, int logNameStartIndexNum, String shiftStartTime, String shiftEndTime)
			throws SftpException, IOException, ParseException, JSchException {
		sftpChannel.cd(logPath);
		Vector<LsEntry> logsList = sftpChannel.ls(logPath);
		InputStream stream = null;
		BufferedReader br = null;
		String logName = null, logNameWithTimeStamp = null, timeStamp = null;
		int maxLen;

		for (int x = 0; x < listOfDatesForLogsCheck.size(); x++) {
			for (LsEntry log : logsList) {
				ArrayList<String> errorList = null;
				logNameWithTimeStamp = log.toString().substring(logNameStartIndexNum);
				if (logNameWithTimeStamp.contains(listOfDatesForLogsCheck.get(x).toString())) {
//					timeStamp = logNameWithTimeStamp.substring(
//							logNameWithTimeStamp.indexOf(shiftStartTime.substring(0, 4)),
//							logNameWithTimeStamp.indexOf("."));
//					--> Changing the time stamp calculation in the substring for further precision
					timeStamp = logNameWithTimeStamp.substring(
							logNameWithTimeStamp.indexOf(listOfDatesForLogsCheck.get(x).toString()),
							logNameWithTimeStamp.indexOf("."));
					logger.info("{} --> {}/{}", sftpChannel.getSession().getHost(), sftpChannel.pwd(),
							log.toString().substring(logNameStartIndexNum));
					if (validatorService.isLogCretdInGivnShift(timeStamp, shiftStartTime, shiftEndTime,
							logNameWithTimeStamp)) {
						logger.info("{} --> {}/{}", sftpChannel.getSession().getHost(), sftpChannel.pwd(),
								log.toString().substring(logNameStartIndexNum));
						logName = logNameWithTimeStamp.substring(0, logNameWithTimeStamp.indexOf(timeStamp));
						if (logName.contains("_")) {
							logName = logName.substring(0, logName.length() - 1);
						}
						stream = sftpChannel.get(logPath + logNameWithTimeStamp);

						br = new BufferedReader(new InputStreamReader(stream));
						errorLogFile.write("-------------Checking Logs for - " + logName + "-------------\n");
						errorLogFile.write("-->" + logNameWithTimeStamp + "\n");
						boolean isErrExist = false;
						String line;
						String errorLine = "", errorTimestamp = "";
						String jobName = "";
						if (JOB_NAME_AND_LOG_NAME_MAP.containsKey(logName))
							jobName = JOB_NAME_AND_LOG_NAME_MAP.get(logName);
						if (listOfErrorsOfLogs.containsKey(jobName))
							errorList = listOfErrorsOfLogs.get(jobName);
						else
							errorList = new ArrayList<>();
						listOfErrorsOfLogs.put(jobName, errorList);

						while ((line = br.readLine()) != null) {
							// condition to limit total characters to 300 per line of error for CP Letter
							// job
							if (jobName.equals("SBO_DAILY_CP_LETTER_LOAD_PROD"))
								maxLen = 196;
							else if (line.length() > 300)
								maxLen = 300;
							else
								maxLen = line.length();
							if (line.contains("ERROR")) {
								errorLine = line.substring(line.indexOf("ERROR"), maxLen);
								errorTimestamp = line.substring(0, 19);
//								errorLine = line.substring(0, maxLen);
								if (!errorList.contains(errorLine)) {
									errorList.add(errorLine);
//									if (errorLine.contains("ERROR"))
//										addTimeStamp(errorList, errorLine, errorTimestamp);
//									logger.info(errorLine);
									listOfErrorsOfLogs.put(jobName, errorList);
								}
								errorLogFile.write(errorLine + "\n");
								isErrExist = true;
							} else if (line.contains("Exception") || line.contains("exception")
									|| line.contains("Error") || line.contains("error")) {
								errorLine = line.substring(0, maxLen);
								if (!errorList.contains(errorLine)) {
									errorList.add(errorLine);
//									logger.info(errorLine);
									listOfErrorsOfLogs.put(jobName, errorList);
								}
								errorLogFile.write(errorLine + "\n");
								isErrExist = true;
							} else if (line.contains("deadlock")) {
								errorLine = "Job has been failed with deadlock\n";
								if (!errorList.contains(errorLine)) {
									errorList.add(errorLine);
//									logger.info(errorLine);
									listOfErrorsOfLogs.put(jobName, errorList);
								}
								errorLogFile.write(errorLine + "\n");
								isErrExist = true;
							}
						}
						if (!isErrExist) {
							errorLogFile.write("No errors present");
						}
						errorLogFile.write("\n");
						stream.close();
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void addTimeStamp(ArrayList<String> errorList, String errorLine, String errorTimestamp) {
		for (int i = 0; i < errorList.size(); i++) {
			if (errorList.get(i).contains(errorLine) && errorList.get(i).contains("ERROR")) {
				if (errorList.get(i).contains("[[") && errorList.get(i).contains("]]")) {
					errorList.set(i, errorList.get(i).replace("]]", ", " + errorTimestamp + "]]"));
				} else {
					errorList.set(i, errorList.get(i).concat(" [[" + errorTimestamp + "]]"));
				}
			}
		}
	}

}
