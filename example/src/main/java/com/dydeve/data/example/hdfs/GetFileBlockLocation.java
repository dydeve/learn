package com.dydeve.data.example.hdfs;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 下午11:35 2019/12/4
 * @Author: joker
 */
public class GetFileBlockLocation {

	public static void main(String[] args) throws IOException, URISyntaxException {
		getFileBlockLocation();
	}

	public static void getFileBlockLocation() throws IOException, URISyntaxException {
		FileSystem fs = GetFileSystem.getFileSystem();
		Path path = new Path("hdfs://localhost:9000/middle/GetFileSystem.java");

		FileStatus fileStatus = fs.getFileStatus(path);

		BlockLocation[] fileBlockLocations = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
		for (int i = 0; i < fileBlockLocations.length; i++) {
			System.out.println("block_" + i + " location:" + fileBlockLocations[i]);

		}

	}

}
