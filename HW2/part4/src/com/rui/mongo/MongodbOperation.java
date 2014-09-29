package com.rui.mongo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

public class MongodbOperation {
	
	private static final String IP_PATTERN = 
			"(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

	public static void main(String[] args) throws IOException {
		List<String> accessLog = readLog(
				"../access.log",
				".*\\[.*\\].*");
		List<String> errorLog = readLog(
				"../error.log",
				"\\[.*\\].*\\[.*\\].*");
		List<String> installLog = readLog("../install.log",
		 ".*\\w\\sC:.*");

		List<DBObject> dbobjectsAccess = toAccessJson(accessLog);
		List<DBObject> dbobjectsError = toErrorJson(errorLog);
		List<DBObject> dbobjectsInstall = toInstallJson(installLog);
		
//		System.out.println("json");
		
		MongoClient mongo = insertDB("accessLog", dbobjectsAccess);
		insertDB("errorLog", dbobjectsError);
		insertDB("installLog", dbobjectsInstall);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
			System.out.println("Collection? :");
			String searchCollection = br.readLine();
			System.out.println("Column? :");
			String searchColumn = br.readLine();
			System.out.println("Value? :");
			String searchValue = br.readLine();
			find(mongo, searchCollection, searchColumn, searchValue);
		}
	}

	private static List<String> readLog(String filename, String regEx) {
		List<String> entries = new ArrayList<String>();
		try {
			FileInputStream fstream = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));

			String strLine;

			Pattern p = Pattern.compile(regEx);
//			int i = 0;
			/* read log line by line */
			while ((strLine = br.readLine()) != null) {
				/* parse strLine to obtain what you want */
				Matcher m = p.matcher(strLine);
				if (m.find()) {
//					System.out.println(strLine);
					entries.add(strLine);
				}
//				i++;
//				if (i == 15)
//					break;
			}
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return entries;
	}

	private static MongoClient insertDB(String collectionName, List<DBObject> dbobjects) {
		MongoClient mongo;
		try {
			mongo = new MongoClient("localhost", 27017);
			WriteConcern w = new WriteConcern( 1, 2000 );
			mongo.setWriteConcern( w );
			DB db = mongo.getDB("test");
			DBCollection collection = db.getCollection(collectionName);
			collection.insert(dbobjects);
			return mongo;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void find(MongoClient mongo, String collectionStr, String attribute, String value) {
		DB db = mongo.getDB("test");
		DBCollection coll = db.getCollection(collectionStr);
		BasicDBObject query = new BasicDBObject(attribute, value);

		DBCursor cursor = coll.find(query);

		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
		} finally {
			cursor.close();
		}
	}

	private static List<DBObject> toAccessJson(List<String> entries) {
		List<DBObject> dBObjects = new ArrayList<DBObject>();
		for (String entry : entries) {
			DBObject dbo = new BasicDBObject();

			dbo.put("ip", extractRegEx(IP_PATTERN, entry));
			dbo.put("time", extractRegEx("\\[.*\\]", entry));
			dbo.put("method", extractRegEx("\".*\"", entry));
			dbo.put("http_code", extractRegEx("\\s[1-5][0-1][0-9]\\s", entry).trim());
			String[] strs = entry.split("\\s");
//			dbo.put("length", extractRegEx("\\s[0-9]+?", entry).trim());
			dbo.put("length", strs[strs.length-1]);
			
			dBObjects.add(dbo);
		}
		return dBObjects;
	}

	private static List<DBObject> toErrorJson(List<String> entries) {
		List<DBObject> dBObjects = new ArrayList<DBObject>();
		for (String entry : entries) {
			DBObject dbo = new BasicDBObject();

			dbo.put("time", extractRegEx("\\[.*\\d{4}\\]\\s", entry).trim());
			dbo.put("status", extractRegEx("\\s\\[\\w*\\]\\s", entry).trim());
			dbo.put("client", extractRegEx("\\[client.*\\]", entry));
			String[] strs = entry.split("\\]");
			dbo.put("length", strs[strs.length-1]);
			
			dBObjects.add(dbo);
		}
		return dBObjects;
	}
	
	private static List<DBObject> toInstallJson(List<String> entries) {
		List<DBObject> dBObjects = new ArrayList<DBObject>();
		int i = 0; 
		for (String entry : entries) {
			DBObject dbo = new BasicDBObject();
			
			if(extractRegEx("\\w+\\s", entry).trim().equals("to")){
				DBObject prev = dBObjects.get(i);
				prev.put("to", extractRegEx("\\sC:.*", entry).trim());
				i++;
				continue;
			}

			dbo.put("opration", extractRegEx("\\w+\\s", entry).trim());
			dbo.put("address", extractRegEx("\\sC:.*", entry).trim());
			
			dBObjects.add(dbo);
			i = dBObjects.size()-1;
		}
		return dBObjects;
	}
	
	private static String extractRegEx(String pattern, String entry){
		String field = null;
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(entry);
		if (m.find()) {
			field = m.group();
        }
		return field;
	}
}
