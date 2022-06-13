package hl7.file.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoaderApplication.class, args);
	}

}
