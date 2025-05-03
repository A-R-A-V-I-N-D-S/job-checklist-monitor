package com.manualtasks.jobchecklist.components;

import static com.manualtasks.jobchecklist.utils.ClassDataUtils.ORDER_DATE_FORMAT;
import static com.manualtasks.jobchecklist.utils.ClassDataUtils.SHIFT_TIME_FORMAT;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class InputDataValidatorService {

	private RestTemplate restTemplate = new RestTemplate();

	private TimeZone easternTimeZone = TimeZone.getTimeZone("America/New_York");

	private static Logger logger = LoggerFactory.getLogger(InputDataValidatorService.class);

	public boolean isLogCretdInGivnShift(String logTimeStamp, String shiftStartTime, String shiftEndTime,
			String logNameWithTimeStamp) throws ParseException {

		SimpleDateFormat dateFormatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		SimpleDateFormat dateFormatter3 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat[] dateFormatter4 = { new SimpleDateFormat("yyyyMMddHHmmss"),
				new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") };

		dateFormatter2.setTimeZone(easternTimeZone);

		dateFormatter3.setTimeZone(easternTimeZone);
		String gvnLogCrtdTime1 = null;
		for (SimpleDateFormat formatter : dateFormatter4) {
			formatter.setTimeZone(easternTimeZone);
			try {
				gvnLogCrtdTime1 = dateFormatter2.format(formatter.parse(logTimeStamp));
				break;
			} catch (ParseException | DateTimeException e) {
				System.out.println("ParseException --> Going to next formatter");
				continue;
			}
		}

//		String gvnLogCrtdTime = dateFormatter2
//				.format(dateFormatter3.parse(logTimeStamp.substring(0, logTimeStamp.length() - 2)));
//		System.out.println("Shift Start time - " + shiftStartTime);
//		System.out.println("Log created time - " + gvnLogCrtdTime1);
//		System.out.println("Shift End time - " + shiftEndTime);

		if ((dateFormatter2.parse(gvnLogCrtdTime1).after(dateFormatter2.parse(shiftStartTime))
				|| dateFormatter2.parse(gvnLogCrtdTime1).equals(dateFormatter2.parse(shiftStartTime)))
				&& (dateFormatter2.parse(gvnLogCrtdTime1).before(dateFormatter2.parse(shiftEndTime))
						|| dateFormatter2.parse(gvnLogCrtdTime1).equals(dateFormatter2.parse(shiftEndTime)))) {
			System.out.println("Considering TRUE");
			return true;
		} else {
			System.out.println("Considering FALSE");
			return false;
		}
	}

	// Not Used As it throws PKIX Sun Certificate Exception
	public String convertISTtoESTNotUsed(String indianTime) {
		String apiUrl = "https://timeapi.io/api/conversion/converttimezone";

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("fromTimeZone", "Asia/Kolkata");
		requestBody.put("dateTime", indianTime);
		requestBody.put("toTimeZone", "America/New_York");
		requestBody.put("dstAmbiguity", "");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity,
				JsonNode.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			JsonNode responseBody = response.getBody();
			if (responseBody != null) {
				String easternTime = responseBody.get("conversionResult").asText();
				logger.info("The EST is - {}", easternTime);
			}
		}
		return null;
	}

	public String convertISTtoEST(String indianTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SHIFT_TIME_FORMAT);
		isGvnDateValid(indianTime, "ShiftTime");
		LocalDateTime localDateTime = LocalDateTime.parse(indianTime, formatter);
		ZonedDateTime indianDateTime = localDateTime.atZone(ZoneId.of("Asia/Kolkata"));
		ZonedDateTime easternDateTime = indianDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
		logger.info("The converted EST time is - {}", easternDateTime.format(formatter));
		return easternDateTime.format(formatter).toString();
	}

	public boolean isGvnDateValid(String dateTime, String dateType) throws DateTimeException {
		SimpleDateFormat orderDateFormat = new SimpleDateFormat(ORDER_DATE_FORMAT);
		orderDateFormat.setLenient(false);
		SimpleDateFormat shiftDateTimeFormat = new SimpleDateFormat(SHIFT_TIME_FORMAT);
		shiftDateTimeFormat.setLenient(false);
		try {
			if (dateType.equalsIgnoreCase("OrderDate")) {
				Date date1 = orderDateFormat.parse(dateTime);
				logger.info("The provided Order Date is - {} and valid", orderDateFormat.format(date1));
			} else {
				Date date2 = shiftDateTimeFormat.parse(dateTime);
				logger.info("The provided Shift Date Time is - {} and valid", shiftDateTimeFormat.format(date2));
			}

		} catch (DateTimeException | ParseException e) {
			logger.error("Unable to parse the given {} - {}", dateType, dateTime);
			return false;
		}
		return true;
	}

}
