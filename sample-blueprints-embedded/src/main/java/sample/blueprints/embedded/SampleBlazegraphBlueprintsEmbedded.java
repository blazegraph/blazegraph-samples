/**

Copyright (C) SYSTAP, LLC 2006-2015.  All rights reserved.

Contact:
     SYSTAP, LLC
     2501 Calvert ST NW #106
     Washington, DC 20008
     licenses@systap.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*
 * Wiki page link:
 * http://wiki.blazegraph.com/wiki/index.php/Blueprints_API_embedded_mode
 */

package sample.blueprints.embedded;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;

import com.bigdata.blueprints.BigdataGraph;
import com.bigdata.blueprints.BigdataGraphEmbedded;
import com.bigdata.journal.BufferMode;
import com.bigdata.journal.Options;
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
		
		/*
		 * For more configuration parameters see
		 * http://www.blazegraph.com/docs/api/index.html?com/bigdata/journal/BufferMode.html
		 */
		props.put(Options.BUFFER_MODE, BufferMode.DiskRW);
		
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

		try {
			final BigdataGraph g = new BigdataGraphEmbedded(repo);
	
			GraphMLReader.inputGraph(g, SampleBlazegraphBlueprintsEmbedded.class
					.getResourceAsStream("/graph-example-1.xml"));
	
			for (Vertex v : g.getVertices()) {
				log.info(v);
			}
			for (Edge e : g.getEdges()) {
				log.info(e);
			}
		} finally {
			repo.shutDown();
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
