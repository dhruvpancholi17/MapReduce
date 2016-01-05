import java.util.*;

import java.io.IOException;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

/**
 * Created by dhruv.pancholi on 05/01/16.
 */
public class OriginalProcessUnit {

    private static final Logger LOG = LoggerFactory.getLogger(OriginalProcessUnit.class);

    //Mapper class
    public static class E_EMapper extends MapReduceBase implements
            Mapper<LongWritable,/*Input key Type */
                    Text,                /*Input value Type*/
                    Text,                /*Output key Type*/
                    IntWritable>        /*Output value Type*/ {

        //Map function
        public void map(LongWritable key, Text value,
                        OutputCollector<Text, IntWritable> output,
                        Reporter reporter) throws IOException {
            String line = value.toString();
            String lastToken = null;
            StringTokenizer s = new StringTokenizer(line, "\t");
            String year = s.nextToken();

            while (s.hasMoreTokens()) {
                lastToken = s.nextToken();
                LOG.debug(" "+lastToken);
            }

            System.out.println("Mapper:: Parsing lastToken: "+lastToken);
            LOG.debug(" Parsing lastToken: "+lastToken+"\n");
            int avgPrice = Integer.parseInt(lastToken);
            output.collect(new Text(year), new IntWritable(avgPrice));
        }
    }


    //Reducer class
    public static class E_EReduce extends MapReduceBase implements
            Reducer<Text, IntWritable, Text, IntWritable> {

        //Reduce function
        public void reduce(Text key, Iterator<IntWritable> values,
                           OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int maxAvg = 30;
            int val = Integer.MIN_VALUE;

            while (values.hasNext()) {
                if ((val = values.next().get()) > maxAvg) {
                    output.collect(key, new IntWritable(val));
                }
            }
        }
    }


    //Main function
    public static void main(String[] args) {

        System.out.println("Using GZipCodec Compression");

        for(int i =0; i< args.length; i++){
            System.out.println("Argument:"+i+" "+args[i]);
        }

        JobConf conf = new JobConf(OriginalProcessUnit.class);

        conf.set("mapred.compress.map.output", "true");
        conf.set("mapred.output.compression.type", "BLOCK");
        conf.set("mapred.map.output.compression.codec", "org.apache.hadoop.io.compress.GzipCodec");


        conf.setJobName("max_eletricityunits");
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);
        conf.setMapperClass(E_EMapper.class);
        conf.setCombinerClass(E_EReduce.class);
        conf.setReducerClass(E_EReduce.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        FileOutputFormat.setCompressOutput(conf, true);
        FileOutputFormat.setOutputCompressorClass(conf, GzipCodec.class);

        try {
            JobClient.runJob(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
