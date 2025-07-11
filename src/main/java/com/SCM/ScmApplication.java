package com.SCM;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
//@EnableJpaRepositories(basePackages = "com.SCM*")
public class ScmApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ScmApplication.class, args);
	}

}
