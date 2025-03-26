package vn.edu.bima;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import vn.edu.bima.model.storage.Storage;
import vn.edu.bima.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class BimaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BimaApplication.class, args);
	}

	@Bean
	CommandLineRunner init(Storage storage) {
		return (args) -> {
			storage.init();
		};
	}
}
