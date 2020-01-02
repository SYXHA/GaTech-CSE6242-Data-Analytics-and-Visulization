package edu.gatech.cse6242;
import java.util.StringTokenizer;
import org.apache.hadoop.fs.FileSystem;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;

public class Q4 {

  public static class DegreeMapper 
    extends Mapper<Object, Text, IntWritable, IntWritable> {
	  private final static IntWritable out_int = new IntWritable(-1);    
	  private final static IntWritable in_int = new IntWritable(1);
   	private IntWritable source = new IntWritable();
   	private IntWritable target = new IntWritable();

	  public void map(Object key, Text value, Context context) 
	    throws IOException, InterruptedException{

      StringTokenizer itr = new StringTokenizer(value.toString(), "\n");
	    while (itr.hasMoreTokens()) {
      	String record[] = itr.nextToken().split("\t");
        source.set(Integer.parseInt(record[0]));
	      target.set(Integer.parseInt(record[1]));
	      context.write(source, in_int);
        context.write(target, out_int);
	   	}
	  }
  }

  public static class DegreeReducer
    extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

	  private IntWritable result = new IntWritable();

	  public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) 
      throws IOException, InterruptedException{
		  int sum = 0;
		  for (IntWritable val : values){
			  sum = sum + val.get();
		  }  
		  result.set(sum);
		  context.write(key, result);	  
		}
	}  


  public static class DiffMapper
    extends Mapper<Object, Text, IntWritable, IntWritable> {

	  private final static IntWritable one = new IntWritable(1);
    private IntWritable difference = new IntWritable();

	  public void map(Object key, Text value, Context context) 
	    throws IOException, InterruptedException{

      StringTokenizer itr = new StringTokenizer(value.toString(), "\n");
	    while (itr.hasMoreTokens()) {
	    	String record[] = itr.nextToken().split("\t");
	    	difference.set(Integer.parseInt(record[1]));
      	context.write(difference, one);
    	}
		}
  }


  public static class DiffReducer
    extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {

	  private IntWritable result = new IntWritable();

	  public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) 
      throws IOException, InterruptedException{
		  int sum = 0;
		  for (IntWritable val : values){
			  sum = sum + val.get();
		  }  
		  result.set(sum);
		  context.write(key, result);	  
		}
	}  



  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job1 = Job.getInstance(conf, "Q4");

    /* TODO: Needs to be implemented */
    job1.setJarByClass(Q4.class);
    job1.setMapperClass(DegreeMapper.class);
    job1.setCombinerClass(DegreeReducer.class);
    job1.setReducerClass(DegreeReducer.class);
    job1.setOutputKeyClass(IntWritable.class);
    job1.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job1, new Path(args[0]));
    FileOutputFormat.setOutputPath(job1, new Path("temp"));

    job1.waitForCompletion(true);
    Job job2 = Job.getInstance(conf, "Job2");
    job2.setJarByClass(Q4.class);
    job2.setMapperClass(DiffMapper.class);
    job2.setCombinerClass(DiffReducer.class);
    job2.setReducerClass(DiffReducer.class);

    FileInputFormat.addInputPath(job2, new Path("temp"));
    FileOutputFormat.setOutputPath(job2, new Path(args[1]));
    job2.setOutputKeyClass(IntWritable.class);
    job2.setOutputValueClass(IntWritable.class);
    boolean Status = job2.waitForCompletion(true);
    FileSystem FSystem = FileSystem.get(conf);
    FSystem.delete(new Path("temp"), true);
    System.exit(Status ? 0 : 1);
  }
}
