package com.manualtasks.jobchecklist.components;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.STATUS_YET_TO_START;
import static com.manualtasks.jobchecklist.utils.ClassDataUtils.STATUS_NA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.manualtasks.jobchecklist.model.ChecklistTemplateData;

@Component
public class JobLogsEntryService {

	public void fillJobErrorsAndValidationStatus(Map<String, ArrayList<String>> listOfErrorsOfLogs,
			List<ChecklistTemplateData> jobChecklistDataList, String shift) {
		for (int i = 0; i < jobChecklistDataList.size(); i++) {
			String jobName = jobChecklistDataList.get(i).getJobName();
			if (listOfErrorsOfLogs.containsKey(jobName)
					&& !(jobChecklistDataList.get(i).getJobStatus().equals(STATUS_YET_TO_START))
					&& !(jobChecklistDataList.get(i).getJobStatus().equals(STATUS_NA))) {
				if (jobChecklistDataList.get(i).getErrorDetails().length() > 2) {
					jobChecklistDataList.get(i).setErrorDetails(jobChecklistDataList.get(i).getErrorDetails() + "\n"
							+ shift.toUpperCase() + ":\n" + (listOfErrorsOfLogs.get(jobName).size() == 0 ? "No Errors"
									: StringUtils.collectionToDelimitedString(listOfErrorsOfLogs.get(jobName), "\n")));
					jobChecklistDataList.get(i).setIsLogsValidated("Y");
				} else {
					jobChecklistDataList.get(i).setErrorDetails(jobChecklistDataList.get(i).getErrorDetails()
							+ shift.toUpperCase() + ":\n" + (listOfErrorsOfLogs.get(jobName).size() == 0 ? "No Errors"
									: StringUtils.collectionToDelimitedString(listOfErrorsOfLogs.get(jobName), "\n")));
					jobChecklistDataList.get(i).setIsLogsValidated("Y");
				}
				if (jobChecklistDataList.get(i).getErrorDetails().contains("deadlock")) {
					jobChecklistDataList.get(i).setErrorDetails(jobChecklistDataList.get(i).getErrorDetails() + "\n"
							+ shift.toUpperCase() + ":\n" + (listOfErrorsOfLogs.get(jobName).size() == 0 ? "No Errors"
									: StringUtils.collectionToDelimitedString(listOfErrorsOfLogs.get(jobName), "\n")));
					jobChecklistDataList.get(i).setIsLogsValidated("Y");
				}
			}
		}
	}

}
