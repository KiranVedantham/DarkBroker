package com.dark.broker.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dark.broker.services.AllServiceConfiguration;
import com.dark.broker.services.ResourceUtil;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

@RestController
@RequestMapping("/mongodb")
public class MongoDbController {
	private static final String FILENAME = "test/Sample.csv";
	@Autowired
	private AllServiceConfiguration sc;
	ResourceUtil rutil = new ResourceUtil();

	@RequestMapping(value = "/insertOne", method = RequestMethod.GET)
	public  ResponseEntity<String> insertOne() throws Exception {
		System.out.println("Inserting sincle Document");
		MongoDatabase dbConnection = null;
		Document document = new  Document();
		try {
			/**** Get database ****/
			dbConnection = (MongoDatabase) sc.getServiceInstance();
			/**** Get collection / table from 'testdb' ****/
			// if collection doesn't exists, MongoDB will create it for you
			MongoCollection<Document> table = dbConnection.getCollection("user");
			/**** Insert ****/
			// create a document to store key and value
			document.put("name", "AppCloud");
			document.put("lastname", "Swisscom");
			document.put("createdDate", new Date());
			table.insertOne(document);
			System.out.println("Document  is Inserted!"+document.toJson());
			
		} catch (SQLException e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);	
			}
		return new ResponseEntity<String>(document.toString(),HttpStatus.OK);
			
	}
	@RequestMapping(value = "/insertMany", method = RequestMethod.GET)
	public ResponseEntity<String> insertMany() throws Exception {
		MongoDatabase dbConnection = null;
		List<Document> documents = new ArrayList<>();

		try {
			dbConnection = (MongoDatabase) sc.getServiceInstance();
			MongoCollection<Document> table = dbConnection.getCollection("user");

			for (int i = 0; i < 500; i++) {
				Document document = new Document();
				document.put("name", "AppCloud" + i);
				document.put("lastname", "Swisscom" + i);
				document.put("createdDate", new Date());
				documents.add(document);
			}

			table.insertMany(documents);

		} catch (SQLException e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		}
		System.out.println(documents.size() + "Documents Inserted!");
		return new ResponseEntity<String>(documents.toString(),HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ResponseEntity<String> deleteRecords() throws Exception {
		MongoDatabase dbConnection = null;
		try {
			/**** Get database ****/
			dbConnection = (MongoDatabase) sc.getServiceInstance();
			/**** Get collection / table from 'testdb' ****/
			// if collection doesn't exists, MongoDB will create it for you
			MongoCollection<Document> table = dbConnection.getCollection("user");
			/**** Insert ****/
			// create a document to store key and value
			Bson filter = new Document("name", "AppCloud");
			table.deleteOne(filter);

		} catch (SQLException e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<String>("Document  is Deleted!",HttpStatus.OK);

	}

	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ResponseEntity<String> update() throws Exception {
		MongoDatabase dbConnection = null;
		UpdateResult resutl=null;
		try {
			/**** Get database ****/
			dbConnection = (MongoDatabase) sc.getServiceInstance();
			/**** Get collection / table from 'testdb' ****/
			// if collection doesn't exists, MongoDB will create it for you
			MongoCollection<Document> table = dbConnection.getCollection("user");
			/**** Insert ****/
			// create a document to store key and value
			Bson filter = new Document("name", "AppCloud2");
			Bson update = new Document("name", "Swisscom Changed");
			resutl=table.updateOne(filter, new Document("$set", update));

		} catch (SQLException e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<String>("Document is Updated "+resutl.toString(),HttpStatus.OK);
	}

	@RequestMapping(value = "/bulkwrite", method = RequestMethod.GET)
	public ResponseEntity<String> bulkwrite() throws Exception {
		MongoDatabase dbConnection = null;
		try {
			/**** Get database ****/
			dbConnection = (MongoDatabase) sc.getServiceInstance();
			/**** Get collection / table from 'testdb' ****/
			// if collection doesn't exists, MongoDB will create it for you
			MongoCollection<Document> table = dbConnection.getCollection("user");
			/**** Insert ****/
			// create a document to store key and value
			Bson filter = new Document("name", "AppCloud1");
			Bson update = new Document("name", "Swisscom Changed bulkoperation");
			Random rand = new Random();
			table.bulkWrite(Arrays.asList(new InsertOneModel<>(new Document("_id", rand.nextInt())),
					new InsertOneModel<>(new Document("_id", rand.nextInt())), new InsertOneModel<>(new Document("_id", rand.nextInt())),
					new UpdateOneModel<>(filter, new Document("$set", update)),
					new DeleteOneModel<>(new Document("_id", 2)),
					new ReplaceOneModel<>(new Document("_id", 3), new Document("_id", 3).append("x", 4))));

		} catch (SQLException e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<String>("Bulkwrite operations Completed",HttpStatus.OK);
	}

	@RequestMapping(value = "/rsstatus", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> getUserInfo() throws Exception {
		DB dbConnection = null;
		CommandResult cr = null;
		try {
			dbConnection = (DB) sc.getMonogDBUserInfo();
			cr = dbConnection.command("replSetGetStatus");
			System.out.println("dbConnection.getStats()" + cr.toJson());
		} catch (SQLException e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<String>(cr.toJson(),HttpStatus.OK);
	}

	@RequestMapping(value = "/fetchAll", method = RequestMethod.GET)
	public ResponseEntity<String> fetchRecords() throws Exception {
		MongoDatabase dbConnection = null;
		FindIterable<Document> documents=null;
		MongoCollection<Document> table=null;
		try {
			dbConnection = (MongoDatabase) sc.getServiceInstance();
			 table = dbConnection.getCollection("user");
			 documents = table.find();
			for (Document document : documents) {
				System.out.println("Document!" + document.toString());
			}
		} catch (SQLException e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<String>("Fetched records Count:"+table.count(),HttpStatus.OK);

	}

	@RequestMapping(value = "/deletecollection", method = RequestMethod.GET)
	public ResponseEntity<String> deleteCollection() throws Exception {
		DB dbConnection = null;
		dbConnection = (DB) sc.getMongoDBInstance1("mongodb");
		DBCollection table = dbConnection.getCollection("user");
		table.drop();
		return new ResponseEntity<String>("Collection is deleted!",HttpStatus.OK);
	}

	@RequestMapping(value = "/loadStorageCapacity", method = RequestMethod.GET)
	public ResponseEntity<String> storageCapacity() throws Exception {
		try {
			DB db = (DB) sc.getMongoDBInstance1("mongodb");
			File imageFile = rutil.getFile(FILENAME);
			// create a "photo" namespace
			GridFS gfsPhoto = new GridFS(db, "photo");
			for (int i = 0; i < 5000; i++) {
				// get image file from local drive
				GridFSInputFile gfsFile = gfsPhoto.createFile(imageFile);
				// set a new filename for identify purpose
				gfsFile.setFilename(imageFile.getName());
				// save the image file into mongoDB
				gfsFile.save();
				System.out.println("Inserted file" + i);
			}
			System.out.println("Done  Now DB size is " +  db.getStats().getDouble("storageSize")/1024/1024/1024+ "  GB");

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		} 
		return new ResponseEntity<String>("loading 5000 images",HttpStatus.OK);
	}
	
	@RequestMapping(value = "/stats", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<String> getdbstats() throws Exception {
		DB db ;
		try {
			 db = (DB) sc.getMongoDBInstance1("mongodb");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		}
		System.out.println("Done  Now DB size is " +  db.getStats().getDouble("storageSize")/1024/1024/1024 + "  GB");
		return new ResponseEntity<String>(db.getStats().toJson(),HttpStatus.OK);
	}
	@RequestMapping(value = "/failStorageCapacity", method = RequestMethod.GET)
	public ResponseEntity<String> failstorageCapacity() throws Exception {
		try {
			DB db = (DB) sc.getMongoDBInstance1("mongodb");
			File imageFile = rutil.getFile(FILENAME);
			// create a "photo" namespace
			GridFS gfsPhoto = new GridFS(db, "photo");
			for (int i = 0; i < 5; i++) {
				// get image file from local drive
				GridFSInputFile gfsFile = gfsPhoto.createFile(imageFile);
				// set a new filename for identify purpose
				gfsFile.setFilename(imageFile.getName());
				// save the image file into mongoDB
				gfsFile.save();
				System.out.println("Inserted file" + i);
			}
			System.out.println("Done  Now DB size is " + db.getStats().getDouble("storageSize")/1024/1024/1024+ "  GB");
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		} 
		return new  ResponseEntity<String>("Storage Fail test passed",HttpStatus.OK);
	}
}
