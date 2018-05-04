package com.dark.broker.controller;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
@RequestMapping("/mariadbentodysseus")
public class MariaDBEntOdysseus {

	@Autowired
	private AllServiceConfiguration sc;
	ResourceUtil rutil = new ResourceUtil();
	private static final String FILENAME = "test/Sample.csv";
	@RequestMapping(value = "/createtable", method = RequestMethod.GET)
	public ResponseEntity<String> createTable() throws Exception {
		deleteTable();
		PreparedStatement preparedStatement = null;
		Connection dbConnection = null;
		String createTableSQL = "CREATE TABLE Persons(" + "id int NOT NULL AUTO_INCREMENT,"
				+ "lastname varchar(255) NOT NULL," + "firstname varchar(255)," + "age varchar(255),"
				+ "email varchar(255)," + "Photo LONGBLOB," + " PRIMARY KEY (ID)" + ")";

		try {
			dbConnection = (Connection) sc.getServiceInstance();
			preparedStatement = dbConnection.prepareStatement(createTableSQL);
			System.out.println(createTableSQL);
			preparedStatement.executeUpdate();
			System.out.println("Table \"Persons\" is created!");
		} catch (SQLException e) {
			return new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return new ResponseEntity<String>("Table Persons Created",HttpStatus.OK);
	}

	@RequestMapping(value = "/insert", method = RequestMethod.GET)
	public ResponseEntity<String>  insertRecords() throws Exception {

		PreparedStatement preparedStatement = null;
		Connection dbConnection = null;
		String insertTableSQL = "INSERT INTO Persons" + "(lastname, firstname, age,email) VALUES" + "(?,?,?,?)";

		try {
			dbConnection = (Connection) sc.getServiceInstance();
			preparedStatement = dbConnection.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, "Kiran");
			preparedStatement.setString(2, "Vedantham");
			preparedStatement.setString(3, "36");
			preparedStatement.setString(4, "kk@kmail.com");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			return new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return new ResponseEntity<String> ("Inserted record",HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public ResponseEntity<String> updateRecords() throws Exception {

		PreparedStatement preparedStatement = null;
		Connection dbConnection = null;
		String updateTableSQL = "UPDATE Persons SET email = ? " + " WHERE firstname = 'Vedantham' ";

		try {
			dbConnection = (Connection) sc.getServiceInstance();
			preparedStatement = dbConnection.prepareStatement(updateTableSQL);
			preparedStatement.setString(1, "changedEmail@gmail.com");

			System.out.println(preparedStatement.toString());
			preparedStatement.executeUpdate();
			System.out.println("Table \"Persons\" is updated!");
		} catch (SQLException e) {
			return new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return new ResponseEntity<String> ("Updated record ",HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ResponseEntity<String> deleteRecords() throws Exception {

		Statement statement = null;
		Connection dbConnection = null;
		String deleteTableSQL = "DELETE from Persons";

		try {
			dbConnection = (Connection) sc.getServiceInstance();
			statement = dbConnection.prepareStatement(deleteTableSQL);
			System.out.println(statement.toString());
			statement.execute(deleteTableSQL);
		} catch (SQLException e) {
			return new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return new ResponseEntity<String> ("Deleting All records :",HttpStatus.OK);
	}
	@RequestMapping(value = "/deleteTable", method = RequestMethod.GET)
	public ResponseEntity<String> deleteTable() throws Exception {

		Statement statement = null;
		Connection dbConnection = null;
		String deleteTableSQL = "drop table if exists Persons";

		try {
			dbConnection = (Connection) sc.getServiceInstance();
			statement = dbConnection.prepareStatement(deleteTableSQL);
			System.out.println(statement.toString());
			statement.execute(deleteTableSQL);
			System.out.println("Table \"Persons\" is deleted!");
		} catch (SQLException e) {
			return new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return new ResponseEntity<String> ("Deleteing the table",HttpStatus.OK);
	}

	@RequestMapping(value = "/fetch", method = RequestMethod.GET)
	public ResponseEntity<String> fetchRecords() throws Exception {

		Statement statement = null;
		Connection dbConnection = null;
		String fetchTableSQL = "Select * from Persons";
		int size=0;

		try {
			dbConnection = (Connection) sc.getServiceInstance();
			statement = dbConnection.prepareStatement(fetchTableSQL);
			System.out.println(statement.toString());
			ResultSet rs = statement.executeQuery(fetchTableSQL);
			rs.last();
			size = rs.getRow();
		} catch (SQLException e) {
			 return new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		 return new ResponseEntity<String> ("fetched records "+size,HttpStatus.OK);
	}

	@RequestMapping(value = "/load", method = RequestMethod.GET)
	public ResponseEntity<String> loadRecords() throws Exception {

		PreparedStatement preparedStatement = null;
		Connection dbConnection = null;
		dbConnection = (Connection) sc.getServiceInstance();

		String insertTableSQL = "INSERT INTO Persons" + "(lastname, firstname, age,email) VALUES" + "(?,?,?,?)";
		for (int i = 1; i < 100; i++) {
			try {

				preparedStatement = dbConnection.prepareStatement(insertTableSQL);
				preparedStatement.setString(1, "Kiran " + i);
				preparedStatement.setString(2, "Vedantham " + i);
				preparedStatement.setString(3, "36");
				preparedStatement.setString(4, i + "kk@kmail.com");
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				return new ResponseEntity<String> (e.getMessage(),HttpStatus.OK);
			}

		}
		System.out.println("Table \"Persons\" loaded 100 records!");
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
		return new ResponseEntity<String> ("loading 100 records",HttpStatus.OK);
	}

	@RequestMapping(value = "/storageCapacity", method = RequestMethod.GET)
	public ResponseEntity<String> storageCapacity() throws Exception {

		PreparedStatement preparedStatement = null;
		Connection dbConnection = null;
		dbConnection = (Connection) sc.getServiceInstance();
		   FileInputStream fis = null;
			String insertTableSQL = "INSERT INTO Persons" + "(lastname, firstname, age,email,Photo) VALUES" + "(?,?,?,?,?)";
		 File file = rutil.getFile(FILENAME);
		for (int i = 0; i < 1000; i++) {
			try {
			      fis = new FileInputStream(file);
				preparedStatement = dbConnection.prepareStatement(insertTableSQL);
				preparedStatement.setString(1, "Kiran " + i);
				preparedStatement.setString(2, "Vedantham " + i);
				preparedStatement.setString(3, "36");
				preparedStatement.setString(4, i + "kk@kmail.com");
				preparedStatement.setAsciiStream(5,fis, (int) file.length());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
			}

		}
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
		return new ResponseEntity<String> ("Storage Capacity Check: Inserted 1000MB Data",HttpStatus.OK);
	}
	@RequestMapping(value = "/storageCapacity/{size}", method = RequestMethod.GET)
	public ResponseEntity<String> feeddb(@PathVariable String size) throws Exception {

		PreparedStatement preparedStatement = null;
		Connection dbConnection = null;
		dbConnection = (Connection) sc.getServiceInstance();
		   FileInputStream fis = null;
		   int mb = Integer.parseInt(size);
			String insertTableSQL = "INSERT INTO Persons" + "(lastname, firstname, age,email,Photo) VALUES" + "(?,?,?,?,?)";
		 File file = rutil.getFile(FILENAME);
		 System.out.println(file.length());
		for (int i = 1; i < mb; i++) {
			try {
			      fis = new FileInputStream(file);
				preparedStatement = dbConnection.prepareStatement(insertTableSQL);
				preparedStatement.setString(1, "Kiran " + i);
				preparedStatement.setString(2, "Vedantham " + i);
				preparedStatement.setString(3, "36");
				preparedStatement.setString(4, i + "kk@kmail.com");
				preparedStatement.setAsciiStream(5,fis, (int) file.length());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				return new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
			}

		}
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
		return new ResponseEntity<String> ("Storage Capacity Check: Inserted data for given size "+size+" MB Size",HttpStatus.OK);
	}
	@RequestMapping(value = "/failstorageCapacity", method = RequestMethod.GET)
	public  ResponseEntity<String> failstorageCapacity() throws Exception {

		PreparedStatement preparedStatement = null;
		Connection dbConnection = null;
		dbConnection = (Connection) sc.getServiceInstance();
		   FileInputStream inputstream = null;
			String insertTableSQL = "INSERT INTO Persons" + "(lastname, firstname, age,email,Photo) VALUES" + "(?,?,?,?,?)";
		 File file = rutil.getFile(FILENAME);
		for (int i = 1; i < 5; i++) {
			try {
				inputstream = new FileInputStream(file);
				preparedStatement = dbConnection.prepareStatement(insertTableSQL);
				preparedStatement.setString(1, "Kiran " + i);
				preparedStatement.setString(2, "Vedantham " + i);
				preparedStatement.setString(3, "36");
				preparedStatement.setString(4, i + "kk@kmail.com");
				preparedStatement.setAsciiStream(5,inputstream, (int) file.length());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				return new ResponseEntity<String> (e.getMessage(),HttpStatus.EXPECTATION_FAILED);
			}

		}
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (dbConnection != null) {
			dbConnection.close();
		}
		return new ResponseEntity<String> ("Storage Capacity Check -ve Test ",HttpStatus.OK);
	}
}
