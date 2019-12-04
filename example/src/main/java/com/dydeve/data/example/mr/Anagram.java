package com.dydeve.data.example.mr;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Description:
 * @Date 下午11:29 2019/12/3
 * @Author: joker
 */
public class Anagram {

	public static class AnagramMapper extends Mapper<Object, Text, Text, Text> {

		private Text sortedKey = new Text();
		private Text text = new Text();

		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String[] words = value.toString().split("\\s");
			for (String word : words) {
				char[] chars = word.toCharArray();
				Arrays.sort(chars);
				String sortedWord = new String(chars);
				sortedKey.set(sortedWord);
				text.set(word);
				context.write(sortedKey, text);
			}

		}
	}

	public static class AnagramReducer extends Reducer<Text, Text, Text, Text> {

		private Text text = new Text();

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			String output = StringUtils.join(values, ",");
			if (output.contains(",")) {
				text.set(output);
				context.write(key, text);
			}
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		Path outpath = new Path("/opt/files/ws");

		//conf 默认取本地fs；配为hdfs时，取hdfs
		FileSystem fs = outpath.getFileSystem(conf);
		if (fs.isDirectory(outpath)) {
			fs.delete(outpath, true);
		}

		Job job = Job.getInstance();
		job.setJarByClass(Anagram.class);
		job.setMapperClass(AnagramMapper.class);
		//可以注释掉reduce，观察map的输出
		job.setReducerClass(AnagramReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);


		FileInputFormat.addInputPath(job, new Path("/opt/files/wc.txt"));
		FileOutputFormat.setOutputPath(job, outpath);

		job.waitForCompletion(true);
	}

}
