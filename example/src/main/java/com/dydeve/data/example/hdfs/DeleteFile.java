package com.dydeve.data.example.hdfs;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 下午7:17 2019/12/1
 * @Author: joker
 */
public class DeleteFile {

	public static void main(String[] args) throws IOException, URISyntaxException {
		deleteFile();
	}

	public static void deleteFile() throws IOException, URISyntaxException {
		FileSystem hdfs = GetFileSystem.getFileSystem();

		Path path = new Path("hdfs://localhost:9000/middle/filter");
		hdfs.delete(path, true);
	}

}
