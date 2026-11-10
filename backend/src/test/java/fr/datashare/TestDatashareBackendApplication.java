package fr.datashare;

import org.springframework.boot.SpringApplication;

public class TestDatashareBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(DatashareBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
