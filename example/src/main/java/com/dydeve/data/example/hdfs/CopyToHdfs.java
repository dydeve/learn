package com.dydeve.data.example.hdfs;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 上午11:05 2019/11/26
 * @Author: joker
 */
public class CopyToHdfs {

	public static void main(String[] args) throws IOException, URISyntaxException {
		copyToHdfs();
	}

	public static void copyToHdfs() throws IOException, URISyntaxException {
		FileSystem fs = GetFileSystem.getFileSystem();
		Path src = new Path("/Users/xmly/Documents/GetFileSystem.java");
		Path dst = new Path("hdfs://localhost:9000/middle/");
		fs.copyFromLocalFile(src, dst);
	}

}
