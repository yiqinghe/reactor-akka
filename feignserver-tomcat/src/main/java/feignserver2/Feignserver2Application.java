package feignserver2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient

public class Feignserver2Application {

	public static void main(String[] args) {
		SpringApplication.run(Feignserver2Application.class, args);
	}

}
