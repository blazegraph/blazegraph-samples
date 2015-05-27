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
 * http://wiki.blazegraph.com/wiki/index.php/Sesame_API_remote_mode
 * 
 */

package sample.sesame.remote;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.bigdata.rdf.sail.webapp.SD;
import com.bigdata.rdf.sail.webapp.client.ConnectOptions;
import com.bigdata.rdf.sail.webapp.client.JettyResponseListener;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rio.RDFFormat;

public class SampleBlazegraphSesameRemote {

	protected static final Logger log = Logger
			.getLogger(SampleBlazegraphSesameRemote.class);
	private static final String sparqlEndPoint = "http://localhost:9999/bigdata";

	public static void main(String[] args) throws Exception {

		final HttpClient client = new HttpClient(
				(new SslContextFactory(true/* trust all */)));
		client.start();
		final ExecutorService executorService = Executors
				.newSingleThreadExecutor(new ThreadFactory() {

					public Thread newThread(Runnable r) {
						Thread th = new Thread(r);
						th.setDaemon(true);
						return th;
					}
				});

		final RemoteRepositoryManager repo = new RemoteRepositoryManager(
				sparqlEndPoint, true /* useLBS */, client, executorService);

		try {

			JettyResponseListener response = getStatus(repo);
			log.info(response.getResponseBody());

			// create a new namespace if not exists
			final String namespace = "newNamespace";
			final Properties properties = new Properties();
			properties.setProperty("com.bigdata.rdf.sail.namespace", namespace);
			if (!namespaceExists(repo, namespace)) {
				log.info(String.format("Create namespace %s...", namespace));
				repo.createRepository(namespace, properties);
				log.info(String.format("Create namespace %s done", namespace));
			} else {
				log.info(String.format("Namespace %s already exists", namespace));
			}

			//get properties for namespace
			log.info(String.format("Property list for namespace %s", namespace));
			response = getNamespaceProperties(repo, namespace);
			log.info(response.getResponseBody());

			/*
			 * Load data from file located in the resource folder
			 * src/main/resources/data.n3
			 */
			String resource = "/data.n3";
			loadDataFromResource(repo, namespace, resource);

			// execute query
			TupleQueryResult result = repo.getRepositoryForNamespace(namespace)
					.prepareTupleQuery("SELECT * {?s ?p ?o} LIMIT 100")
					.evaluate();
			
			//result processing
			try {
				while (result.hasNext()) {
					BindingSet bs = result.next();
					log.info(bs);
				}
			} finally {
				result.close();
			}

		} finally {
			client.stop();
			repo.close();
		}

	}

	/*
	 * Status request.
	 */
	private static JettyResponseListener getStatus(RemoteRepositoryManager repo)
			throws Exception {

		ConnectOptions opts = new ConnectOptions(sparqlEndPoint + "/status");
		opts.method = "GET";
		return repo.doConnect(opts);

	}

	/*
	 * Check namespace already exists.
	 */
	private static boolean namespaceExists(RemoteRepositoryManager repo,
			String namespace) throws Exception {
		
		GraphQueryResult res = repo.getRepositoryDescriptions();
		try {
			while (res.hasNext()) {
				Statement stmt = res.next();
				if (stmt.getPredicate()
						.toString()
						.equals(SD.KB_NAMESPACE)) {
					if (namespace.equals(stmt.getObject().stringValue())) {
						return true;
					}
				}
			}
		} finally {
			res.close();
		}
		return false;
	}

	/*
	 * Get namespace properties.
	 */
	private static JettyResponseListener getNamespaceProperties(
			RemoteRepositoryManager repo, String namespace) throws Exception {

		ConnectOptions opts = new ConnectOptions(sparqlEndPoint + "/namespace/"
				+ namespace + "/properties");
		opts.method = "GET";
		return repo.doConnect(opts);

	}

	/*
	 * Load data into a namespace.
	 */
	private static void loadDataFromResource(RemoteRepositoryManager repo,
			String namespace, String resource) throws Exception {
		InputStream is = SampleBlazegraphSesameRemote.class
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
