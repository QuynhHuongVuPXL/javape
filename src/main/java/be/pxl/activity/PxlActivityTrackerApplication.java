package be.pxl.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "be.pxl.activity.domain") // Ensures entities in the domain package are scanned
@EnableJpaRepositories(basePackages = "be.pxl.activity.repository") // Ensures repositories are scanned
public class PxlActivityTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PxlActivityTrackerApplication.class, args);
	}
}
