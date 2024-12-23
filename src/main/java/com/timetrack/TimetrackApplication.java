package com.timetrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.Getter;

@SpringBootApplication
@Getter
public class TimetrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimetrackApplication.class, args);
	}

}
