package com.dydeve.data.example.hdfs;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 下午5:46 2019/11/25
 * @Author: joker
 */
public class MakeDirOrFile {

	public static void main(String[] args) throws IOException, URISyntaxException {
		mkDirOrFile();
	}

	public static void mkDirOrFile() throws IOException, URISyntaxException {
		FileSystem  hdfs = GetFileSystem.getFileSystem();

		hdfs.mkdirs(new Path("hdfs://localhost:9000/middle/weibo"));

		hdfs.createNewFile(new Path("hdfs://localhost:9000/middle/weibo/djt.txt"));
	}

}
