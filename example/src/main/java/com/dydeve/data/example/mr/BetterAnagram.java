package com.dydeve.data.example.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * @Description:
 * @Date 上午11:20 2019/12/4
 * @Author: joker
 */
public class BetterAnagram extends Configured implements Tool {

	static class BetterAnagramMapper extends Mapper<LongWritable, Text, Text, Text> {

	}

	static class BetterAnagramReducer extends Reducer<Text, Text, Text, Text> {

	}

	public BetterAnagram(Configuration conf) {
		super(conf);
	}

	public static void main(String[] args) throws Exception {
		String[] arg0 = {"hdfs://localhost:9000/anagrams/",
				"hdfs://localhost:9000/anagrams/out"};
		//执行mapreduce
		int ec = ToolRunner.run(new Configuration(), new BetterAnagram(new Configuration()), arg0);

		//设置退出

		System.exit(ec);
	}

	@Override
	public int run(String[] args) throws Exception {
		// Configuration processed by ToolRunner
		Configuration conf = getConf();

		// Create a JobConf using the processed conf
		Job job = Job.getInstance(conf, "better-anagram");
		job.setJarByClass(BetterAnagram.class);

		// Process custom command-line options
		Path in = new Path(args[1]);
		Path out = new Path(args[2]);

		// Specify various job-specific parameters
		FileInputFormat.addInputPath(job, in);
		FileOutputFormat.setOutputPath(job, out);
		job.setMapperClass(BetterAnagramMapper.class);
		job.setReducerClass(BetterAnagramReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}
}
