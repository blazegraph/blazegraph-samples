package sample.blueprints.remote;

import org.apache.log4j.Logger;

import com.bigdata.blueprints.BigdataGraph;
import com.bigdata.blueprints.BigdataGraphClient;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class SampleBlazegraphBlueprintsRemote {
	
	protected static final Logger log = Logger.getLogger(SampleBlazegraphBlueprintsRemote.class);

	public static void main(String[] args) throws Exception {
		
		final BigdataGraph graph = new BigdataGraphClient("http://localhost:9999/bigdata");
		try {
			graph.loadGraphML(SampleBlazegraphBlueprintsRemote.class.getResource("/graph-example-1.xml").getFile());
			for (Vertex v : graph.getVertices()) {
				log.info(v);
			}
			for (Edge e : graph.getEdges()) {
				log.info(e);
			}
		} finally {
			graph.shutdown();
		}
	}
}
