package com.dydeve.data.example.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 下午3:56 2019/11/25
 * @Author: joker
 */
public class GetFileSystem {

	public static void main(String[] args) throws IOException, URISyntaxException {

		FileSystem fileSystem = getFileSystem();

	}

	public static FileSystem getFileSystem() throws URISyntaxException, IOException {

		Configuration conf = new Configuration();
		URI uri = new URI("hdfs://localhost:9000");
		FileSystem hdfs = FileSystem.get(uri, conf);

		//DFS[DFSClient[clientName=DFSClient_NONMAPREDUCE_-737877740_1, ugi=xmly (auth:SIMPLE)]]
		System.out.println(hdfs);

		/*FileSystem defaultFs = FileSystem.get(conf);

		//org.apache.hadoop.hive.ql.io.ProxyLocalFileSystem@5ba88be8
		System.out.println(defaultFs);


		//org.apache.hadoop.hive.ql.io.ProxyLocalFileSystem@5ba88be8
		//与上面相等
		FileSystem local = FileSystem.getLocal(conf);
		System.out.println(local);*/

		return hdfs;
	}


}
