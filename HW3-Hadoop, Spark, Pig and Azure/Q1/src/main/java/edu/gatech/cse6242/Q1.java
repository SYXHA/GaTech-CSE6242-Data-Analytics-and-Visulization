package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Q1 {
  
  // Mapper
  public static class TokenizerMapper
      extends Mapper<Object, Text, Text, Text>{

    private Text word  = new Text();
    private Text value = new Text();


    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      String[] strings=value.toString().split("\\s+");
    	value.set(strings[1]+","+strings[2])
      word.set(strings[0]);
    	context.write(word,value);
    }
  }

  
  // Reducer
  public static class Reducer 
      extends Reducer<Text,Text,Text,Text>{
  
  	private Text key= new Text();
  	private Text value = new Text();
  	
  	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{

      // initialization
      int max_weight = 0;
    	int min_target = 0;  
  	  String source=key.toString();
  
      // update
    	for (Text i: values){
  	
      	String[] current_values = i.toString().split(",");
      	int current_target = Integer.parseInt(current_values[0]);
      	int current_weight = Integer.parseInt(current_values[1]);
  	
      	if(current_weight > max_weight){
          max_weight = current_weight;
          min_target = current_target;
        } else if(current_weight == max_weight){
          min_target = Math.min(current_target, min_target);
  	    }
  	  }
  	
  	  key.set(source);
  	  value.set(min_target+","+max_weight);
      context.write(key,value);
    }
  
  }
  

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");

    /* TODO: Needs to be implemented */
  	job.setJarByClass(Q1.class);

	  job.setMapperClass(TokenizerMapper.class);
  	job.setReducerClass(Reducer.class);
	
	  job.setOutputKeyClass(Text.class);
  	job.setOutputValueClass(Text.class);

    
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
