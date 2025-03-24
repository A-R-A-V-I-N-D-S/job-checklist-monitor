package com.manualtasks.jobchecklist.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.manualtasks.jobchecklist.config.ApplicationConfig;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.*;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

@Service
public class JSchSpringBatchLogsValidatingService {

	@Value("${batch.server.username}")
	private String username;

	@Value("${batch.server.password}")
	private String password;

	@Value("${file.logoutput.path}")
	private String errorValidationLogPath;

	@Autowired
	private LogsReaderService logsReaderService;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationConfig applicationConfig;

	private TimeZone easternTimeZone = TimeZone.getTimeZone("America/New_York");

	private static Logger logger = LoggerFactory.getLogger(JSchSpringBatchLogsValidatingService.class);

	@Async("asyncTaskExecutor")
	public CompletableFuture<Map<String, ArrayList<String>>> validateLogsInServer(String shift, String batchServer) {
		Map<String, ArrayList<String>> listOfErrorsOfLogs = new HashMap<>();
		int logNameStartIndexNum;
		try {

			logger.info("Validating the Spring Batch logs in {} for errors - START", batchServer);

			FileWriter errorLogFile = new FileWriter(errorValidationLogPath + batchServer.toUpperCase() + ".txt");
			ChannelSftp sftpChannel = applicationContext.getBean(ChannelSftp.class, batchServer, username, password);
			ArrayList<String> listOfDatesForLogsCheck = logsReaderService.findLogOccuringDatesByShift(shift);

			String shiftStartTime = getShiftTimings(shift.toUpperCase()).get(0);
			String shiftEndTime = getShiftTimings(shift.toUpperCase()).get(1);

			logger.debug("Checking the logs from \"" + shiftStartTime + "\" to \"" + shiftEndTime + "\"");

			if (batchServer.contains("dc04")) {
				logNameStartIndexNum = LOG_NAME_START_INDEX1;
				for (String logPath : ASCSSBO_LOGS_LOCATIONS_300_301) {
//					if (logPath.equals("/apps/sclc/logs/") || logPath.equals("/apps/sclc/logs/ace/")) {
//						listOfDatesForLogsCheck = logsReaderService.findLogOccuringDatesByShift2(shiftStartTime,
//								shiftEndTime);
//					}
					try {
						logsReaderService.readLogFilesForErrors(sftpChannel, logPath, listOfDatesForLogsCheck,
								listOfErrorsOfLogs, errorLogFile, logNameStartIndexNum, shiftStartTime, shiftEndTime);
						sftpChannel.cd(logPath);
					} catch (Exception exc) {
						if (exc.toString().contains("2: No such file")) {
							logger.warn("{} --> {} is not applicable for logs", sftpChannel.getSession().getHost(),
									logPath);
							continue;
						} else {
							logger.error(exc.getMessage());
							exc.printStackTrace();
						}
					}
				}
			} else {
				logNameStartIndexNum = LOG_NAME_START_INDEX2;
				for (String logPath : ASCSSBO_LOGS_LOCATIONS_002) {
					try {
						logsReaderService.readLogFilesForErrors(sftpChannel, logPath, listOfDatesForLogsCheck,
								listOfErrorsOfLogs, errorLogFile, logNameStartIndexNum, shiftStartTime, shiftEndTime);
						sftpChannel.cd(logPath);
					} catch (Exception exc) {
						if (exc.toString().contains("2: No such file")) {
							logger.warn("{} --> {} is not applicable for logs", sftpChannel.getSession().getHost(),
									logPath);
							continue;
						} else {
							logger.error(exc.getMessage());
							exc.printStackTrace();
						}
					}
				}
			}

			applicationConfig.disconnectSftp(sftpChannel);
			logger.info("Validating the Spring Batch logs in {} for errors - END", batchServer);

		} catch (Exception e) {
			if (e.toString().contains("Auth fail for methods 'publickey,password,keyboard-interactive'")) {
				logger.error(e.getMessage());
				System.err
						.println("Change the password in the application.properties file to avoid the domain ID lock");
				return null;
			} else {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return CompletableFuture.completedFuture(listOfErrorsOfLogs);
	}

	@Async("asyncTaskExecutor")
	public CompletableFuture<Map<String, ArrayList<String>>> validateLogsInServer(String shift, String shiftStartTime,
			String shiftEndTime, String batchServer) {
		Map<String, ArrayList<String>> listOfErrorsOfLogs = new HashMap<>();
		int logNameStartIndexNum;
		try {

			logger.info("Validating the Spring Batch logs in {} for errors - START", batchServer);

			FileWriter errorLogFile = new FileWriter(errorValidationLogPath + batchServer.toUpperCase() + ".txt");
			ChannelSftp sftpChannel = applicationContext.getBean(ChannelSftp.class, batchServer, username, password);

			SimpleDateFormat dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			dateFormatter2.setTimeZone(easternTimeZone);

			SimpleDateFormat dateFormatter4 = new SimpleDateFormat(SHIFT_TIME_FORMAT);
			dateFormatter4.setTimeZone(easternTimeZone);

			final String formattedShiftStartTime = dateFormatter2.format(dateFormatter4.parse(shiftStartTime));
			final String formattedShiftEndTime = dateFormatter2.format(dateFormatter4.parse(shiftEndTime));
//			logger.info("Formatted shift timings is from {} to {}", formattedShiftStartTime, formattedShiftEndTime);

			ArrayList<String> listOfDatesForLogsCheck = logsReaderService.findLogOccuringDatesByShift(shift,
					formattedShiftStartTime, formattedShiftEndTime);

			logger.debug(
					"Checking the logs from \"" + formattedShiftStartTime + "\" to \"" + formattedShiftEndTime + "\"");

			if (batchServer.contains("dc04")) {
				logNameStartIndexNum = LOG_NAME_START_INDEX1;

				for (String logPath : ASCSSBO_LOGS_LOCATIONS_300_301) {
//					if (logPath.equals("/apps/sclc/logs/") || logPath.equals("/apps/sclc/logs/ace/")) {
//						listOfDatesForLogsCheck = logsReaderService.findLogOccuringDatesByShift2(shiftStartTime,
//								shiftEndTime);
//					}
					try {
						logsReaderService.readLogFilesForErrors(sftpChannel, logPath, listOfDatesForLogsCheck,
								listOfErrorsOfLogs, errorLogFile, logNameStartIndexNum, formattedShiftStartTime,
								formattedShiftEndTime);
						sftpChannel.cd(logPath);
					} catch (Exception exc) {
						if (exc.toString().contains("2: No such file")) {
							logger.warn("{} --> {} is not applicable for logs", sftpChannel.getSession().getHost(),
									logPath);
							continue;
						} else {
							logger.error(exc.getMessage());
							exc.printStackTrace();
						}
					}
				}
			} else {
				logNameStartIndexNum = LOG_NAME_START_INDEX2;
				for (String logPath : ASCSSBO_LOGS_LOCATIONS_002) {
					try {
						logsReaderService.readLogFilesForErrors(sftpChannel, logPath, listOfDatesForLogsCheck,
								listOfErrorsOfLogs, errorLogFile, logNameStartIndexNum, formattedShiftStartTime,
								formattedShiftEndTime);
						sftpChannel.cd(logPath);
					} catch (Exception exc) {
						if (exc.toString().contains("2: No such file")) {
							logger.warn("{} --> {} is not applicable for logs", sftpChannel.getSession().getHost(),
									logPath);
							continue;
						} else {
							logger.error(exc.getMessage());
							exc.printStackTrace();
						}
					}
				}
			}

			applicationConfig.disconnectSftp(sftpChannel);
			logger.info("Validating the Spring Batch logs in {} for errors - END", batchServer);

		} catch (Exception e) {
			if (e.toString().contains("Auth fail for methods 'publickey,password,keyboard-interactive'")) {
				System.err
						.println("Change the password in the application.properties file to avoid the domain ID lock");
				return null;
			} else {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return CompletableFuture.completedFuture(listOfErrorsOfLogs);
	}

	private List<String> getShiftTimings(String shift) {
		List<String> shiftTimings = new ArrayList<>();
		String shiftStartTime = "", shiftEndTime = "";

		SimpleDateFormat dateFormatter5 = new SimpleDateFormat("yyyy-MM-dd");
		dateFormatter5.setTimeZone(easternTimeZone);

		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(easternTimeZone);

		switch (shift) {
		case "S1":
			shiftStartTime = dateFormatter5.format(calendar.getTime()) + " 06:15";
			shiftEndTime = dateFormatter5.format(calendar.getTime()) + " 15:15";
			break;
		case "S2":
			shiftStartTime = dateFormatter5.format(calendar.getTime()) + " 13:45";
			shiftEndTime = dateFormatter5.format(calendar.getTime()) + " 23:15";
			break;
		case "S3":
			shiftEndTime = dateFormatter5.format(calendar.getTime()) + " 06:45";
			calendar.add(Calendar.DATE, -1);
			shiftStartTime = dateFormatter5.format(calendar.getTime()) + " 20:45";
			break;
		default:
			break;
		}
		shiftTimings.add(shiftStartTime);
		shiftTimings.add(shiftEndTime);
		return shiftTimings;
	}

}
