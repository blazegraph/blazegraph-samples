package sample.blueprints.embedded;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;

import com.bigdata.blueprints.BigdataGraph;
import com.bigdata.blueprints.BigdataGraphEmbedded;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.bigdata.rdf.sail.remote.BigdataSailFactory;
import com.bigdata.rdf.sail.remote.BigdataSailFactory.Option;
import com.bigdata.rdf.store.AbstractTripleStore;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;

public class SampleBlazegraphBlueprintsEmbedded {
	
	protected static final Logger log = Logger.getLogger(SampleBlazegraphBlueprintsEmbedded.class);
	private static final String journalFile = "/tmp/blazegraph/test.jnl";
	
	public static void main(String[] args) throws IOException,
			RepositoryException {

		final Properties props = new Properties();
		props.put("com.bigdata.journal.AbstractJournal.bufferMode", "DiskRW");
		/*
		 * Lax edges allows us to use non-unique edge identifiers
		 */
		props.setProperty(BigdataGraph.Options.LAX_EDGES, "true");

		/*
		 * SPARQL bottom up evaluation semantics can have performance impact.
		 */
		props.setProperty(AbstractTripleStore.Options.BOTTOM_UP_EVALUATION, "false");
		props.put("com.bigdata.journal.AbstractJournal.file", journalFile);

		BigdataSailRepository repo = getOrCreateRepository(props);

		repo.initialize();

		final BigdataGraph g = new BigdataGraphEmbedded(repo);

		GraphMLReader.inputGraph(g, SampleBlazegraphBlueprintsEmbedded.class
				.getResourceAsStream("/graph-example-1.xml"));

		for (Vertex v : g.getVertices()) {
			log.info(v);
		}
		for (Edge e : g.getEdges()) {
			log.info(e);
		}
	}
	
	private static BigdataSailRepository getOrCreateRepository(Properties props) throws RepositoryException {

		BigdataSailRepository repo = null;

		if (journalFile == null || !new File(journalFile).exists()) {

			/*
			 * No journal specified or journal does not exist yet at specified
			 * location. Create a new store. (If journal== null an in-memory
			 * store will be created.
			 */
			repo = BigdataSailFactory.createRepository(props, journalFile,
					Option.TextIndex);

		} else {

			/*
			 * Journal already exists at specified location. Open existing
			 * store.
			 */
			repo = BigdataSailFactory.openRepository(journalFile);

		}
			
		repo.initialize();
			
		return repo;
	}
}
