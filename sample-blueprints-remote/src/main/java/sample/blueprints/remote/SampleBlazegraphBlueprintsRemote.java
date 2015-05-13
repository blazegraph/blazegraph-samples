package sample.blueprints.remote;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.bigdata.blueprints.BigdataGraph;
import com.bigdata.blueprints.BigdataGraphClient;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;


public class SampleBlazegraphBlueprintsRemote {
	
	protected static final Logger log = Logger.getLogger(SampleBlazegraphBlueprintsRemote.class);

	public static void main(String[] args) throws IOException {
		
		final BigdataGraph graph = new BigdataGraphClient("http://localhost:9999/bigdata");
               
        GraphMLReader.inputGraph(graph, SampleBlazegraphBlueprintsRemote.class.getResourceAsStream("/graph-example-1.xml"));
        
		for (Vertex v : graph.getVertices()) {
			log.info(v);
		}
		for (Edge e : graph.getEdges()) {
			log.info(e);
		}
		
		graph.shutdown();
	}
}
