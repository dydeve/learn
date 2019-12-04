package com.dydeve.data.example.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @Description:
 * @Date 下午10:52 2019/12/1
 * @Author: joker
 */
public class WordCount {

	/**
	 * @see TextInputFormat
	 */
	public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		private Text word = new Text();
		private IntWritable one = new IntWritable(1);

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer st = new StringTokenizer(value.toString());
			while (st.hasMoreTokens()) {
				word.set(st.nextToken());
				context.write(word, one);
			}
		}
	}

	public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		private IntWritable result = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable value : values) {
				sum += value.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		Path outPath = new Path("/opt/files/mr");
		FileSystem fileSystem = outPath.getFileSystem(conf);
		if (fileSystem.isDirectory(outPath)) {
			fileSystem.delete(outPath, true);
		}
		Job job = Job.getInstance();

		job.setJarByClass(WordCount.class);
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);
		job.setCombinerClass(WordCountReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		/* map与reduce的输出相同，可以不设
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);*/

		//job.setInputFormatClass(TextInputFormat.class);  by default

		//可以add多个path
		FileInputFormat.addInputPath(job, new Path("/opt/files/wc.txt"));
		//只可以set一个path
		//part-r-00000  r表示reduce    如果是map，也是r
		//如果执行两次，会失败。因为/mr目录已存在
		FileOutputFormat.setOutputPath(job, outPath);

		job.waitForCompletion(true);

	}

}
