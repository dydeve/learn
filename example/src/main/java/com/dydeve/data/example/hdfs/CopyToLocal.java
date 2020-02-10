package com.dydeve.data.example.hdfs;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 下午6:54 2019/12/1
 * @Author: joker
 */
public class CopyToLocal {

	public static void main(String[] args) throws IOException, URISyntaxException {
		copyToLocal();
	}

	public static void copyToLocal() throws IOException, URISyntaxException {
		FileSystem hdfs = GetFileSystem.getFileSystem();

		Path src = new Path("hdfs://localhost:9000/middle/filter/target00.jpeg");
		Path dst = new Path("/opt/different");
		hdfs.copyToLocalFile(src, dst);
	}

}
