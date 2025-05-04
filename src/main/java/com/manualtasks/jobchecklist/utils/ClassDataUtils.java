package com.manualtasks.jobchecklist.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDataUtils {

	public ClassDataUtils() {
		super();
	}

	public static final String ORDER_DATE_FORMAT = "MM/dd/yyyy";

	public static final String SHIFT_TIME_FORMAT = "MM/dd/yy HH:mm";

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");

	public static final int TOTAL_COL_IN_CHECKLIST_SHEET = 21;

	public static final int TOTAL_COL_IN_CTM_DETAILS_SHEET = 29;

	public static final String CHECKLIST_SHEET_NAME = "Checklist";

	public static final String CTM_DETAILS_SHEET_NAME = "CTM Details";

	public static final String UNTRACKED_JOB_SHEET_NAME = "Untracked Jobs";

	public static final String STATUS_ENDED_OK = "Ended OK";

	public static final String STATUS_EXECUTING = "Executing";

	public static final String STATUS_YET_TO_START = "Yet to Start";

	public static final String STATUS_CANCELLED = "Cancelled";

	public static final String STATUS_HELD = "Held";

	public static final String STATUS_NA = "NA";

	public static final int LOG_NAME_START_INDEX1 = 60;

	public static final int LOG_NAME_START_INDEX2 = 56;

	public static final String[] BATCH_SERVERS_LIST = { "dc04plvbuc300", "dc04plvbuc301", "va10puvbas002" };

	public static final String S1_START_TIME = "05:45";

	public static final String S1_END_TIME = "15:15";

	public static final String S2_START_TIME = "13:45";

	public static final String S2_END_TIME = "23:15";

	public static final String S3_START_TIME = "21:20";

	public static final String S3_END_TIME = "06:40";

	public static final List<String> ERROR_KEYWORDS_LIST = Arrays.asList("Exception", "exception", "Error", "error",
			"Failed", "failed"/* , "FAIL" */);

	public static final ArrayList<String> ASCSSBO_LOGS_LOCATIONS_300_301 = new ArrayList<String>(Arrays.asList(
			"/apps/sclc/batch/producerprecheck/logs/", "/apps/sclc/batch/preprocess/logs/",
			"/apps/sclc/batch/ProducerSboMaintenance/logs/", "/apps/sclc/batch/producerrtsprocess/logs/",
			"/apps/sclc/batch/ProducerSetup/logs/", "/apps/sclc/batch/ProducerEventLoader/logs/",
			"/apps/sclc/batch/producerniprreconprocess/logs/", "/apps/sclc/batch/UnappointedProducerProcess/logs/",
			"/apps/sclc/batch/minmandatory/logs/", "/apps/sclc/batch/fileloader/logs/",
			"/apps/sclc/batch/agentdata/logs/", "/apps/sclc/batch/NIPRPDBAlertProcess/logs/",
			"/apps/sclc/batch/errorreprocess/logs/", "/apps/sclc/batch/ProducerNiprProcess/logs/",
			"/apps/sclc/batch/produceroptinprocess/logs/", "/apps/sclc/batch/Producerfileloader/logs/",
			"/apps/sclc/batch/rts/logs/", "/apps/sclc/batch/excelexporter/logs/",
			"/apps/sclc/batch/producerlanding/logs/", "/apps/sclc/batch/Sboarchive/logs/",
			"/apps/sclc/batch/aceAgentReports/logs/", "/apps/sclc/batch/fileextract/logs/",
			"/apps/sclc/batch/common_config/logs/", "/apps/sclc/batch/ProducerSboProcess/logs/",
			"/apps/sclc/batch/cmdinbound/logs/", "/apps/sclc/batch/ProducerLetterProcess/logs/",
			"/apps/sclc/batch/MassUpload/logs/", "/apps/sclc/logs/"));

	public static final ArrayList<String> ASCSSBO_LOGS_LOCATIONS_002 = new ArrayList<String>(
			Arrays.asList("/usr/app/blcs/data_process/logs/", "/usr/app/ascs/data_process/logs/"));
}
