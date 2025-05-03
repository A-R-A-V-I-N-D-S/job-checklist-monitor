package com.manualtasks.jobchecklist.components;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.STATUS_YET_TO_START;
import static com.manualtasks.jobchecklist.utils.ClassDataUtils.STATUS_NA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.manualtasks.jobchecklist.model.ChecklistTemplateData;

@Component
public class JobLogsEntryService {

	public Map<String, String> fillJobErrorsAndValidationStatus(Map<String, ArrayList<String>> jobsAndErrorsPair,
			List<ChecklistTemplateData> jobChecklistDataList, String shift) {

		Map<String, String> untrackedJobsAndErrors = new HashMap<>();
		ArrayList<String> allTrackedJobNames = new ArrayList<>();

		for (int i = 0; i < jobChecklistDataList.size(); i++) {
			String jobName = jobChecklistDataList.get(i).getJobName();
			allTrackedJobNames.add(jobName);
			if (jobsAndErrorsPair.containsKey(jobName)
					&& !(jobChecklistDataList.get(i).getJobStatus().equalsIgnoreCase(STATUS_YET_TO_START))
					&& !(jobChecklistDataList.get(i).getJobStatus().equalsIgnoreCase(STATUS_NA))) {

				// If condition to check if already a value is present like previous shift
				// details
				if (jobChecklistDataList.get(i).getErrorDetails().length() > 2) {
					jobChecklistDataList.get(i).setErrorDetails(jobChecklistDataList.get(i).getErrorDetails() + "\n"
							+ shift.toUpperCase() + ":\n" + (jobsAndErrorsPair.get(jobName).size() == 0 ? "No Errors"
									: StringUtils.collectionToDelimitedString(jobsAndErrorsPair.get(jobName), "\n")));
					jobChecklistDataList.get(i).setIsLogsValidated("Y");
				} else {
					jobChecklistDataList.get(i).setErrorDetails(jobChecklistDataList.get(i).getErrorDetails()
							+ shift.toUpperCase() + ":\n" + (jobsAndErrorsPair.get(jobName).size() == 0 ? "No Errors"
									: StringUtils.collectionToDelimitedString(jobsAndErrorsPair.get(jobName), "\n")));
					jobChecklistDataList.get(i).setIsLogsValidated("Y");
				}
			}
		}
		for (Map.Entry<String, ArrayList<String>> entry : jobsAndErrorsPair.entrySet()) {
			if (!allTrackedJobNames.contains(entry.getKey())) {
				untrackedJobsAndErrors.put(entry.getKey(),
						StringUtils.collectionToDelimitedString(entry.getValue(), "\n"));
				System.out.println(entry.getKey());
			}
		}
		return untrackedJobsAndErrors;
	}
}
