package sample.rdr;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.rio.RDFFormat;

import com.bigdata.rdf.sail.webapp.client.IPreparedTupleQuery;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository.AddOp;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

public class SampleBlazegraphRDR {
	
	protected static final Logger log = Logger.getLogger(SampleBlazegraphRDR.class);
	private static final String sparqlEndPoint = "http://localhost:9999/bigdata";
	
	public static void main(String[] args) throws Exception  {
	
		final HttpClient client = new HttpClient((new SslContextFactory(true/*trust all*/)));
		client.start();
		final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
		
			public Thread newThread(Runnable r) {
				Thread th = new Thread(r);
				th.setDaemon(true);
				return th;
			}
		});
		final RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(sparqlEndPoint, true /*useLBS*/, client, executorService);
	
		try{	
		
			final String namespace = "namespaceRDR";
			final Properties properties = new Properties();
			properties.setProperty("com.bigdata.rdf.sail.namespace", namespace);
			properties.setProperty("com.bigdata.rdf.store.AbstractTripleStore.statementIdentifiers", "true");
			
			if(!namespaceExists(namespace, repositoryManager)){
				log.info(String.format("Create namespace %s...", namespace));
				repositoryManager.createRepository(namespace, properties);
				log.info(String.format("Create namespace %s done", namespace));
			}
			
			InputStream is = SampleBlazegraphRDR.class.getResourceAsStream("/rdr_test.ttl");
			repositoryManager.getRepositoryForNamespace(namespace).add(new AddOp(is, RDFFormat.forMIMEType("application/x-turtle-RDR")));
			
			//execute query
			try{
				RemoteRepository r = repositoryManager.getRepositoryForNamespace(namespace);
			    IPreparedTupleQuery query = r.prepareTupleQuery("SELECT ?age ?src WHERE {?bob foaf:name \"Bob\" . <<?bob foaf:age ?age>> dc:source ?src .}");
			    TupleQueryResult result = query.evaluate();
			    while(result.hasNext()){
			    	BindingSet bs = result.next();
			    	log.info(bs);
			    }
			    result.close();
			}catch(Exception e){
				e.printStackTrace();
			}
	
		} finally {
			client.stop();
			repositoryManager.close();
	
		}
		}
	
		private static boolean namespaceExists(String namespace, RemoteRepositoryManager repo) throws Exception{
			GraphQueryResult res = repo.getRepositoryDescriptions();
			try{
				while(res.hasNext()){
					Statement stmt = res.next();
					if (stmt.getPredicate().toString().equals("http://www.bigdata.com/rdf#/features/KB/Namespace")) {
						if(namespace.equals(stmt.getObject().stringValue())){
							log.info(String.format("Namespace %s already exists", namespace));
							return true;
						}
					}
				}
			} finally {
				try {
				res.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
			return false;
		}

}
