package com.rui.hbaseloader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.util.Bytes;

import com.rui.hbaseloader.utils.HBaseHelper;


public class HBaseLoader {

	private static final HBaseHelper hbase =  HBaseHelper.create();
	
    public static void main(String[] args) {    	
//    	if (args.length < 1 || args.length > 2) {
//    		error("HBaseLoader <tablename> <dir>");
//    		return;
//    	}
		if (hbase == null) {
			error("Couldn't establish connection to HBase");
		}

//		String fileName = args[1];
		String fileName = "/home/hadoop/bigdata/dataset/hw/bx";
		File file = new File(fileName);
		if (file == null) {
			error(String.format("Directory %s doesn't exist", fileName));
		}
		if (!file.isDirectory()) {
			error(String.format("File %s is not a directory", fileName));
		}
		
		HTableInterface table;
		try {
//			String tableName = args[0];
			String tableName = "book";
			table = hbase.getOrCreateTable(tableName, "content");
			for (File each: file.listFiles()) {
				if (each.isFile()) {
					insertInto(table, each);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }

	private static void insertInto(HTableInterface table, File file) {
		try {
			byte[] content = Bytes.toBytes(FileUtils.readFileToString(file, "UTF-8"));
			hbase.insert(table, file.getName(), "content", "", content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void error(String str) {
		System.out.println("Error: " + str);
	}
    
}