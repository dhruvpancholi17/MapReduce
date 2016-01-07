package Part3;

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
import cascading.pipe.assembly.Retain;
import cascading.property.AppProps;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;

/**
 * Created by dhruv.pancholi on 07/01/16.
 */


public class Main {
    public static void main(String[] args) {
        String docPath = args[0];
        String wcPath = args[1];

        Properties properties = new Properties();
        AppProps.setApplicationJarClass(properties, Main.class);
        FlowConnector flowConnector = new HadoopFlowConnector(properties);

        // create source and sink taps
        // Assuming the fields in the input are \t delimited
        Tap docTap = new Hfs(new TextDelimited(true, "\t"), docPath);
        Tap wcTap = new Hfs(new TextDelimited(true, "\t"), wcPath);

        // specify a regex operation to split the "document" text lines into a token stream
        Fields token = new Fields("token");
        Fields text = new Fields("text");
        RegexSplitGenerator splitter = new RegexSplitGenerator(token, "[ \\[\\]\\(\\),.]");
        Fields fieldSelector = new Fields("doc_id", "token");
        // selecting two fields for every token generated from the text field, the doc_id is the one to which token corresponded to?
        // how the internal of this function works
        Pipe docPipe = new Each("token", text, splitter, fieldSelector);

        // define "ScrubFunction" to clean up the token stream
        Fields scrubArguments = new Fields("doc_id", "token");
        // this will know that the given fields in the input are two 1. doc_id and 2. token
        // now this will filter all the rows such that whose token is valid
        docPipe = new Each(docPipe, scrubArguments, new ScrubFunction(scrubArguments), Fields.RESULTS);

        // determine the word counts
        Pipe wcPipe = new Pipe("wc", docPipe);
        // retain only the token field from the pipe
        wcPipe = new Retain(wcPipe, token);

        wcPipe = new GroupBy(wcPipe, token);
        // what is the point of this Fields.ALL
        wcPipe = new Every(wcPipe, Fields.ALL, new Count(), Fields.ALL);

        // connect the taps, pipes, etc., into a flow
        FlowDef flowDef = FlowDef.flowDef().setName("wc").addSource(docPipe, docTap).addTailSink(wcPipe, wcTap);

        // write a DOT file and run the flow
        Flow wcFlow = flowConnector.connect(flowDef);
        wcFlow.writeDOT("dot/wc.dot");
        wcFlow.complete();
    }
}
