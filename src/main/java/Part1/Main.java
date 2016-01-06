package Part1;

import cascading.flow.FlowDef;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import com.sun.tools.javac.comp.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by dhruv.pancholi on 05/01/16.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        String inPath = args[0];
        String outPath = args[1];

        LOG.debug("Output:: " + inPath);
        LOG.debug("Output:: " + outPath);

        Properties properties = new Properties();
        AppProps.setApplicationJarClass(properties, Main.class);
        AppProps.setApplicationName(properties, "Impatient Part 1");
        AppProps.addApplicationTag(properties, "tutorial:impatient");
        AppProps.addApplicationTag(properties, "technology:Cascading");

        HadoopFlowConnector flowConnector = new HadoopFlowConnector(properties);

        // create the source tap
        Tap inTap = new Hfs(new TextDelimited(true, "\t"), inPath);

        // create the sink tap
        Tap outTap = new Hfs(new TextDelimited(true, "\t"), outPath);

        // specify a pipe to connect the taps
        Pipe copyPipe = new Pipe("copy");

        // connect the taps, pipes, etc., into a flow
        FlowDef flowDef = FlowDef.flowDef().addSource(copyPipe, inTap).addTailSink(copyPipe, outTap).setName("Copy");

        // run the flow
        flowConnector.connect(flowDef).complete();
    }

    public static void greet(String[] args) {
        p("Starting Part1");
        for (int i = 0; i < args.length; i++) {
            p("Argument:" + i + " " + args[i]);
        }
    }

    public static void p(Object o) {
        System.out.println(o);
    }
}
