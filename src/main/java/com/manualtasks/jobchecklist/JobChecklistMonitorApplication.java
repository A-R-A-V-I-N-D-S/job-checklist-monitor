package com.manualtasks.jobchecklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import com.manualtasks.jobchecklist.JobChecklistMonitorApplication;
import com.manualtasks.jobchecklist.controller.ApplicationController;

@SpringBootApplication
public class JobChecklistMonitorApplication {

	@Autowired
	private ApplicationController applicationController;

	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(JobChecklistMonitorApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
		context.close();
//		SpringApplication.run(JobChecklistMonitorApplication.class, args).close();
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return runner -> {
			applicationController.executeTheTask();
		};
	}

}