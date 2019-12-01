package com.dydeve.data.example.hdfs;


import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 下午10:38 2019/11/26
 * @Author: joker
 */
public class ListAllFiles {


	public static void main(String[] args) throws IOException, URISyntaxException {
		listAllFile();
	}

	public static void listAllFile() throws IOException, URISyntaxException {
		FileSystem hdfs = GetFileSystem.getFileSystem();

		//hdfs://localhost:9000/middle/filter
		//FileStatus[] fileStatuses = hdfs.globStatus(new Path("hdfs://localhost:9000/middle/filter"));

		//hdfs://localhost:9000/middle/filter
		//FileStatus[] fileStatuses = hdfs.globStatus(new Path("hdfs://localhost:9000/middle/filter/"));


		/**
		 * hdfs://localhost:9000/middle/filter/target00.jpeg
		 * hdfs://localhost:9000/middle/filter/target01.jpeg
		 **/
		FileStatus[] fileStatuses = hdfs.globStatus(new Path("hdfs://localhost:9000/middle/filter/*"));

		Path[] paths = FileUtil.stat2Paths(fileStatuses);
		for (Path path : paths) {
			System.out.println(path);
		}


		for (FileStatus fileStatus : fileStatuses) {
			System.out.println(fileStatus);
		}
	}

}
