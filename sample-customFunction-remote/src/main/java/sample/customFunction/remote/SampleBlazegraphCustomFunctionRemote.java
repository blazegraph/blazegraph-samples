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

package sample.customFunction.remote;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rio.RDFFormat;

import com.bigdata.journal.BufferMode;
import com.bigdata.journal.Journal;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryDecls;


public class SampleBlazegraphCustomFunctionRemote {
	
	protected static final Logger log = Logger
			.getLogger(SampleBlazegraphCustomFunctionRemote.class);
	public static final String serviceURL = "http://localhost:9999/bigdata";
	
	/*
	 * Select all documents available to <http://www.example.com/John> 
	 */
	public static final String QUERY = "SELECT ?doc " + // 
			"{ ?doc rdf:type <http://www.example.com/Document> . " + //
			" filter(<http://www.example.com/validate>(<http://www.example.com/John>, ?doc)) . }";

	public static void main(String[] args) throws Exception {
		
		final String namespace = "test";
		
		final Properties journalProperties = new Properties();
        {
            journalProperties.setProperty(Journal.Options.BUFFER_MODE,
                    BufferMode.MemStore.name());
            
        }
			
		final RemoteRepositoryManager repo = new RemoteRepositoryManager(
				serviceURL , false /* useLBS */);
		
		repo.createRepository(namespace, journalProperties);
		
		try {

			/*
			 * Load data from file located in the resource folder
			 * src/main/resources/data.n3
			 */
			final String resource = "/data.n3";
			loadDataFromResource(repo, namespace, resource);

			// execute query
			final TupleQueryResult result = repo.getRepositoryForNamespace(namespace)
					.prepareTupleQuery(QUERY)
					.evaluate();
			
			//result processing
			try {
				while (result.hasNext()) {
					final BindingSet bs = result.next();
					log.info(bs);
				}
			} finally {
				result.close();
			}

		} finally {
			repo.close();
		}
		

	}
	

	/*
	 * Load data into a namespace.
	 */
	public static void loadDataFromResource(final RemoteRepositoryManager repo,
			final String namespace, final String resource) throws Exception {
		final InputStream is = SampleBlazegraphCustomFunctionRemote.class
				.getResourceAsStream(resource);
		if (is == null) {
			throw new IOException("Could not locate resource: " + resource);
		}
		try {
			repo.getRepositoryForNamespace(namespace).add(
					new RemoteRepository.AddOp(is, RDFFormat.N3));
		} finally {
			is.close();
		}
	}
	
	
	
}



		


