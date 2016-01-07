package Part2;

/**
 * Created by dhruv on 6/1/16.
 */

import java.util.Properties;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.FlowDef;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.operation.aggregator.Count;
import cascading.operation.regex.RegexSplitGenerator;
import cascading.pipe.Each;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;


public class Main {
    public static void main(String[] args) {
        String docPath = args[0];
        String wcPath = args[1];

        Properties properties = new Properties();
        AppProps.setApplicationJarClass(properties, Main.class);
        AppProps.setApplicationName(properties, "Impatient Part 2");
        AppProps.addApplicationTag(properties, "tutorial:impatient");
        AppProps.addApplicationTag(properties, "technology:Cascading");
        FlowConnector flowConnector = new HadoopFlowConnector(properties);

        // the underlying datastructures at the end of each step

        // create source and sink taps
        // whats the point of defining the textdelimited here?
        Tap docTap = new Hfs(new TextDelimited(true, "\t"), docPath);
        Tap wcTap = new Hfs(new TextDelimited(true, "\t"), wcPath);

        // specify a regex operation to split the "document" text lines into a token stream
        Fields token = new Fields("token");
        Fields text = new Fields("text");

        // what pattern will this regex match?
        RegexSplitGenerator splitter = new RegexSplitGenerator(token, "[ \\[\\]\\(\\),.]");

        // only returns "token"
        // does it take text field as input?
        // whats the point of this Fields.RESULTS
        // if we want the field token as result then why not use the object field with name token
        Pipe docPipe = new Each("token", text, splitter, Fields.RESULTS);

        // determine the word counts
        Pipe wcPipe = new Pipe("wc", docPipe);
        // assuming there is a field with name token
        wcPipe = new GroupBy(wcPipe, token);
        wcPipe = new Every(wcPipe, Fields.ALL, new Count(), Fields.ALL);

        // the way of execution of the above two steps

        // connect the taps, pipes, etc., into a flow
        // defining the name of the whole flow as wc, and stating that the fields in the input are separated by /t
        FlowDef flowDef = FlowDef.flowDef().setName("wc").addSource(docPipe, docTap).addTailSink(wcPipe, wcTap);

        // write a DOT file and run the flow
        Flow wcFlow = flowConnector.connect(flowDef);
        wcFlow.writeDOT("dot/wc.dot");
        wcFlow.complete();
    }
}
