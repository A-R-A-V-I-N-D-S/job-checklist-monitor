package com.manualtasks.jobchecklist.components;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class FileClosureValidator {

	@Autowired
	private ResourceLoader resourceLoader;

	private static Logger logger = LoggerFactory.getLogger(FileClosureValidator.class);

	public boolean ifFileOpenWaitForClosed(String fileAddress) throws IOException {
		try {
			FileInputStream fileInputStream = new FileInputStream(fileAddress);
			fileInputStream.close();
		} catch (FileNotFoundException exc) {
			if (exc.getMessage()
					.contains("The process cannot access the file because it is being used by another process")) {
				System.err.println(
						"You need to close the file - " + fileAddress.substring(fileAddress.lastIndexOf("\\") + 1)
								+ " from your local to continue the process");
				WaitingThread waitingThread = new WaitingThread();
				playSound();
				waitingThread.start();
				while (waitingThread.isAlive())
					;
				return false;
			} else if (exc.getMessage().contains("The system cannot find the file specified")) {
				return true;
			} else {
				logger.error(exc.getMessage());
				exc.printStackTrace();
			}
		}
		return true;

	}

	private void playSound() {
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					resourceLoader.getResource("classpath:static/sounds/Windows Foreground.wav").getInputStream());
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			System.out.println(e.getMessage());
		}

	}

}

class WaitingThread extends Thread {
	@Override
	public void run() {
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			e.getMessage();
		}
	}
}