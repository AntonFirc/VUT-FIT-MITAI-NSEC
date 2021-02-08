package com.vut.fit.gja2020.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootTest
class AppApplicationTests {

	@Test
	void canSudo() throws IOException, InterruptedException {

		Runtime runtime = Runtime.getRuntime();
		String[] cmd = {
				"/bin/sh",
				"-c",
				"sudo -l -U $(whoami)"
		};
		Process proc = runtime.exec(cmd);
		BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String line;
		boolean access = false;

		while ((line = in.readLine()) != null) {
			access = line.contains("may run the following");
			if (access) {
				break;
			}
		}
		proc.waitFor();


		assert access;

	}

}
