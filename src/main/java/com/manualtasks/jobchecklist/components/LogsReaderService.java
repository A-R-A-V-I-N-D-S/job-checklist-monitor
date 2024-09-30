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

	private SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");

	private SimpleDateFormat dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static Logger logger = LoggerFactory.getLogger(LogsReaderService.class);

	private Date dateNTime = new Date();

	@Autowired
	private TimingsValidatorService validatorService;

	public ArrayList<String> findLogOccuringDatesByShift(String shift) throws ParseException {

		dateFormatter1.setTimeZone(easternTimeZone);

		String timeStampTdy, timeStampYest;

		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(easternTimeZone);
		calendar.add(Calendar.DATE, -1);

		ArrayList<String> logDates = new ArrayList<>();
		timeStampTdy = dateFormatter1.format(dateNTime);
		timeStampYest = dateFormatter1.format(calendar.getTime());
		if (shift.equalsIgnoreCase("s1")) {
			logDates.add(timeStampYest);
			logDates.add(timeStampTdy);
		} else {
			logDates.add(timeStampTdy);
		}
		return logDates;
	}

	public ArrayList<String> findLogOccuringDatesByShift(String shift, String shiftStartTime, String shiftEndTime)
			throws ParseException {

		dateFormatter1.setTimeZone(easternTimeZone);

		dateFormatter2.setTimeZone(easternTimeZone);

		String timeStampTdy, timeStampYest;

//		System.out.println(shiftStartTime + " - " + shiftEndTime);

		ArrayList<String> logDates = new ArrayList<>();
		timeStampTdy = dateFormatter1.format(dateFormatter2.parse(shiftEndTime));
		timeStampYest = dateFormatter1.format(dateFormatter2.parse(shiftStartTime));
		if (shift.equalsIgnoreCase("s1")) {
			logDates.add(timeStampYest);
			logDates.add(timeStampTdy);
		} else {
			logDates.add(timeStampTdy);
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
					timeStamp = logNameWithTimeStamp.substring(
							logNameWithTimeStamp.indexOf(shiftStartTime.substring(0, 4)),
							logNameWithTimeStamp.indexOf("."));
//					logger.info("{} --> {}/{}", sftpChannel.getSession().getHost(), sftpChannel.pwd(),
//							log.toString().substring(logNameStartIndexNum));
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
						String errorLine = "";
						String jobName = "";
						if (JOB_NAME_AND_LOG_NAME_MAP.containsKey(logName))
							jobName = JOB_NAME_AND_LOG_NAME_MAP.get(logName);
						if (listOfErrorsOfLogs.containsKey(jobName))
							errorList = listOfErrorsOfLogs.get(jobName);
						else
							errorList = new ArrayList<>();
						listOfErrorsOfLogs.put(jobName, errorList);

						while ((line = br.readLine()) != null) {
							if (line.contains("ERROR")) {
								// if condition to limit total characters to 300 per line of error for CP Letter
								// job
								if (jobName.equals("SBO_DAILY_CP_LETTER_LOAD_PROD"))
									maxLen = 196;
								else if (line.length() > 300)
									maxLen = 300;
								else
									maxLen = line.length();
								errorLine = line.substring(line.indexOf("ERROR"), maxLen);
								if (!errorList.contains(errorLine)) {
									errorList.add(errorLine);
									listOfErrorsOfLogs.put(jobName, errorList);
								}
								errorLogFile.write(errorLine + "\n");
								isErrExist = true;
							}
							if (line.contains("deadlock")) {
								errorLine = "Job has been failed with deadlock\n";
								if (!errorList.contains(errorLine)) {
									errorList.add(errorLine);
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

}
