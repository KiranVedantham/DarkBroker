package com.dark.broker.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dark.broker.services.AllServiceConfiguration;
import com.dark.broker.services.ResourceUtil;

@RestController
@RequestMapping("/analytics")
public class AllConnectionsController {

	@Autowired
	private AllServiceConfiguration sc;
	ResourceUtil rutil = new ResourceUtil();

	@SuppressWarnings("unused")
	@RequestMapping(value = "/cons/{numConns}", method = RequestMethod.GET)
	public ResponseEntity<String> createConnections(@PathVariable String numConns) throws Exception {
		int numberOfconns = Integer.parseInt(numConns);
		Date t = Calendar.getInstance().getTime();
		System.out.println("Started Creating cons : "+Calendar.getInstance().getTime());
		for (int i = 1; i <= numberOfconns; i++) {
			Object fisrtc = null;
			try {
				fisrtc = sc.getServiceInstance();
				System.out.println("Connection Number : " + i);
			} catch (Exception e) {
				System.out.println(" Failed at Connection Number : " + i);
				return new ResponseEntity<String>("Failed at Connection Number : " + i,HttpStatus.EXPECTATION_FAILED);
			}
		}
		System.out.println("Started Creating cons : "+t);

		System.out.println("completed Creating cons : "+Calendar.getInstance().getTime());
		
		return new ResponseEntity<String>("Connections Created :"+numConns,HttpStatus.OK);
	}
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/service", method = RequestMethod.GET)
	public ResponseEntity<String> getServiceName() throws Exception {
				String serviceInstance = sc.getDBlabel();
				String plan = sc.getServicePlan();
				String scName = sc.getServiceName();
				StringBuffer buffre = new StringBuffer();
				buffre.append("Service Name : ");
				buffre.append(scName);
				buffre.append("</br>Service Type  : ");
				buffre.append(serviceInstance.toUpperCase());
				buffre.append("</br>Service Plan : ");
				buffre.append(plan);
				buffre.append("</br>");
				populateOperations(serviceInstance);
		return new ResponseEntity<String>(buffre.toString(),HttpStatus.OK);
	}
	@SuppressWarnings("unused")
	@RequestMapping(value = "/operationlist", method = RequestMethod.GET)
	public ResponseEntity<String> getOperations() throws Exception {
				String serviceInstance = sc.getDBlabel();
				String plan = sc.getServicePlan();
				List<String> result =  populateOperations(serviceInstance);
		return new ResponseEntity<String>(result.toString(),HttpStatus.OK);
	}

	private List<String> populateOperations(String serviceInstance) throws Exception {

		Properties prop = new Properties();
		InputStream input = null;
		List<String> result = new ArrayList<String>() ;
		File file;
		try {

			file = rutil.getFile("application.properties") ;
			 input = new FileInputStream(file);
			// load a properties file
			prop.load(input);
			String Ops= (String) prop.get(serviceInstance.concat(".ops"));
		 result = Arrays.asList(Ops.split("\\s*,\\s*"));
	        
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;

	}
}
