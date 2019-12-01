package com.dydeve.data.example.hdfs;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 下午7:25 2019/12/1
 * @Author: joker
 */
public class GetHdfsNodes {

	public static void main(String[] args) throws IOException, URISyntaxException {

		getHdfsNodes();

	}

	public static void getHdfsNodes() throws IOException, URISyntaxException {
		FileSystem fs = GetFileSystem.getFileSystem();
		DistributedFileSystem hdfs = (DistributedFileSystem) fs;
		DatanodeInfo[] dataNodeStats = hdfs.getDataNodeStats();
		for (int i = 0; i < dataNodeStats.length; i++) {
			System.out.println(dataNodeStats[i]);
			System.out.println(dataNodeStats[i].getHostName());
			System.out.println("--------------");
		}

	}
}
