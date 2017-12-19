	package com.dark.broker.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dark.broker.services.AllServiceConfiguration;
import com.dark.broker.services.ResourceUtil;

import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/redis")
public class RedisController {

	@Autowired
	private AllServiceConfiguration sc;
	private static final String FILENAME = "test/Sample.csv";

	@RequestMapping(value = "/insertdata", method = RequestMethod.GET)
	public ResponseEntity<String> insertData() throws Exception {
		Jedis jedis = (Jedis)sc.getServiceInstance();
		jedis.set("Sample", "Hello All This is Redis Sample");
		return new ResponseEntity<String>("Sucessfully insered values </br> Key : Sample", HttpStatus.OK);
	}

	@RequestMapping(path = "/insertlist", method = RequestMethod.GET)
	public ResponseEntity<String> insertList() throws Exception {
		Jedis jedis = (Jedis)sc.getServiceInstance();
		jedis.lpush("AppCloud-list", "Redis"); 
		jedis.lpush("AppCloud-list", "Mongodb"); 
		jedis.lpush("AppCloud-list", "RabbitMQ"); 
		jedis.lpush("AppCloud-list", "Mysql"); 
		return new ResponseEntity<String>("Inserted List of Values",HttpStatus.OK);
	}

	@RequestMapping(path = "/searchList", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<Map<String, ArrayList>> searchList() throws Exception {
		Jedis jedis = (Jedis)sc.getServiceInstance();
		List<String> list = jedis.lrange("AppCloud-list", 0 ,5); 
		ArrayList<String> jsonValues = new ArrayList<String>() ;
		Map<String, ArrayList> map = new HashMap<String, ArrayList>();
		for (String s : list) jsonValues.add(s) ;
		Collections.sort(jsonValues);
		map.put("AppCloud-list",jsonValues);
		return new ResponseEntity<Map<String, ArrayList>>(map,HttpStatus.OK);
	}
	@RequestMapping(value = "/searchdata/{redisKey}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<Map<String,String>> searchData(@PathVariable("redisKey") String redisKey) throws Exception {
		Jedis jedis = (Jedis)sc.getServiceInstance();
		Map<String,String> payload = new HashMap<String,String>() ;
		if (jedis.get(redisKey) == null) {
			return new ResponseEntity<Map<String,String>>(payload,HttpStatus.NOT_FOUND);
		}
		payload.put(redisKey, jedis.get(redisKey)) ;
		return new ResponseEntity<Map<String,String>>(payload,HttpStatus.OK);
	}

	@RequestMapping(path = "/load", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<String> redisLoad() throws Exception {
		String message = getdatafromfile(FILENAME);
		Jedis jedis = (Jedis)sc.getServiceInstance();
		for (int i = 0; i < 20; i++) {
			jedis.set(UUID.randomUUID().toString().replace("-", ""), message);
			System.out.println("MEssage load iteration"+i);
		}
		System.out.println("jedis.dbSize()"+jedis.dbSize());
		System.out.println("jedis.getDB().SIZE :"+jedis.getDB().SIZE);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	@RequestMapping(path = "/chksize", method = RequestMethod.GET)
	public ResponseEntity<String> redisDbSize() throws Exception {
		Jedis jedis = (Jedis)sc.getServiceInstance();
		System.out.println("jedis.getDB().SIZE :"+jedis.getDB().SIZE);
		return new ResponseEntity<String>(jedis.dbSize().toString(),HttpStatus.OK);
	}
	
	@RequestMapping(value  = "/info/{storge}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<String> redisinfo(@PathVariable String storage) throws Exception {
		Jedis jedis = (Jedis)sc.getServiceInstance();
		System.out.println("jedis.info() :"+jedis.info());
		return new ResponseEntity<String>(jedis.info(),HttpStatus.OK);
	}
	
	@RequestMapping(path = "/info", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<Map<String,String>> redisinfo() throws Exception {
		Jedis jedis = (Jedis)sc.getServiceInstance();
		String [] temp ;
		Map<String,String> payload = new HashMap<String,String>() ;
		try {
			String [] lines = jedis.info().split("\\r?\\n") ;
			for (String s: lines) {
				if (s.startsWith("#")) {
					payload.put("_comment", s) ;
				} else {
					if (!s.isEmpty() && s.contains(":")) {
						temp = s.split(":") ;
						payload.put(temp[0], temp[1]) ;
					}
				}
			}
			return new ResponseEntity<Map<String, String>>(payload,HttpStatus.OK);
		} catch (Exception e) {
			payload.put("Error ", e.getMessage());
			return new ResponseEntity<Map<String, String>>(payload,HttpStatus.EXPECTATION_FAILED);
		}
		
		
	}
	
	
	@RequestMapping(path = "/loadsearch", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<String> loadSearch() throws Exception {
		Jedis jedis = (Jedis)sc.getServiceInstance();
		for (int i = 0; i < 10; i++) {
			System.out.println("Message returned : " + jedis.get("Test"+i));
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	private String getdatafromfile(String filename2) throws Exception {
		ResourceUtil ru = new ResourceUtil();
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(ru.getFile(filename2));
			br = new BufferedReader(fr);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(ru.getFile(FILENAME)));
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	@RequestMapping(value = "/deletedata", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }, consumes = {MediaType.ALL_VALUE})
	public ResponseEntity<Map<String, String>> deleteData(@RequestBody String keyForDelete) throws Exception {
		String deletedKeys = "0";
		Map<String, String> payload = new HashMap() ;
		try {
			Jedis jedis = (Jedis)sc.getServiceInstance();
			deletedKeys = String.valueOf(jedis.del(keyForDelete));
			payload.put("Number of items deleted", deletedKeys) ;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return new ResponseEntity<Map<String, String>> (payload, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/flushdata", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE }, consumes = {MediaType.ALL_VALUE})
	public ResponseEntity<String> flushData() throws Exception {
		String statusCode = "0";
		try {
			Jedis jedis = (Jedis)sc.getServiceInstance();
			statusCode = jedis.flushDB();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return new ResponseEntity<String> (statusCode, HttpStatus.OK);
	}
}