package com.dydeve.data.example.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @Description:
 * @Date 下午1:52 2019/11/26
 * @Author: joker
 */
public class CopyFilesToHdfs {

	public static void main(String[] args) throws IOException, URISyntaxException {
		copyFilesToHdfs();
	}

	public static void copyFilesToHdfs() throws IOException, URISyntaxException {
		FileSystem hdfs = GetFileSystem.getFileSystem();

		Configuration conf = new Configuration();
		FileSystem local = FileSystem.getLocal(conf);

		//given path
		//local.listStatus();

		//a regular expression specifying the path pattern
		FileStatus[] localStatus = local.globStatus(new Path("/Users/xmly/Documents/*"), new RegexPathFilter("^.*jpeg$"));
		Path[] listedPaths = FileUtil.stat2Paths(localStatus);

		//如果没有这个目录，则直接存成这个文件名
		Path dst = new Path("hdfs://localhost:9000/middle/filter");

		if (hdfs.exists(dst)) {
			hdfs.delete(dst, true);
		}

		//hdfs.create(dst);  file
		hdfs.mkdirs(dst);

		for (Path src : listedPaths) {
			hdfs.copyFromLocalFile(src, dst);
		}

	}

	public static class RegexPathFilter implements PathFilter {

		private final String regex;

		public RegexPathFilter(String regex) {
			this.regex = regex;
		}


		@Override
		public boolean accept(Path path) {
			boolean flag = path.toString().matches(regex);
			return flag;
		}
	}

}
