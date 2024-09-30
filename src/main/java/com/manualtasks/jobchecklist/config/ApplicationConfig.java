package com.manualtasks.jobchecklist.config;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Configuration
@EnableAsync
public class ApplicationConfig {

	private static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

	@Bean
	@Scope(value = "prototype")
	public ChannelSftp connectSftp(String hostServer, String username, String password) throws JSchException {
		Session session = new JSch().getSession(username, hostServer, 22);
		session.setPassword(password);
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();
		ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
		sftpChannel.connect();
		if (sftpChannel.isConnected()) {
			logger.info("Connected to - {}", session.getHost());
		}
		return sftpChannel;
	}

	public void disconnectSftp(ChannelSftp sftpChannel) throws JSchException {
		Session session = sftpChannel.getSession();
		if (sftpChannel != null && sftpChannel.isConnected()) {
			sftpChannel.disconnect();
		}
		if (session != null && session.isConnected()) {
			session.disconnect();
		}
		if (!session.isConnected()) {
			logger.info("Disconnected from - {}", session.getHost());
		}
	}

	@Bean("asyncTaskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(3);
		executor.setQueueCapacity(15);
		executor.setThreadNamePrefix("SFTP Thread-");
		executor.initialize();
		return executor;
	}

}
