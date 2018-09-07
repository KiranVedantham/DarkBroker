package com.dark.broker.healthchecker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthchecker")
public class HealthChecker {
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

@Autowired

@RequestMapping(value = "/3seconds", method = RequestMethod.GET)
public ResponseEntity<String> healthCheck3Sec() throws Exception {
	Date date = new Date();
	System.out.println("Time taken to hit 3seconds healthcheck "+dateFormat.format(date));
		Thread.sleep(3000);
	Date date2 = new Date();
	System.out.println("Time taken to respond 3seconds healthcheck "+dateFormat.format(date2));
		return new ResponseEntity<String>("3 seconds",HttpStatus.OK);
}

@RequestMapping(value = "/15seconds", method = RequestMethod.GET)
public ResponseEntity<String> healthCheck15Sec() throws Exception {
	Date date = new Date();
	System.out.println("Time taken to hit 15seconds healthcheck"+dateFormat.format(date));
		Thread.sleep(15000);
		Date date2 = new Date();
		System.out.println("Time taken to respond 15seconds healthcheck "+dateFormat.format(date2));
		return new ResponseEntity<String>("15 seconds",HttpStatus.OK);
}

@RequestMapping(value = "/1second", method = RequestMethod.GET)
public ResponseEntity<String> healthCheck1Sec() throws Exception {
	Date date = new Date();
	System.out.println("Time taken to hit healthcheck "+dateFormat.format(date));
		return new ResponseEntity<String>("1 second",HttpStatus.OK);
}
}
