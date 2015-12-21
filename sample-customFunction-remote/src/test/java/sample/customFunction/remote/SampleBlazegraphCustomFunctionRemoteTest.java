package sample.customFunction.remote;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.eclipse.jetty.server.Server;

import com.bigdata.journal.BufferMode;
import com.bigdata.journal.Journal;
import com.bigdata.rdf.sail.webapp.NanoSparqlServer;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

public class SampleBlazegraphCustomFunctionRemoteTest {
	
	@Test
	public void testCustomFunctionRemote() throws Exception{
		
		final String namespace = "test";
		
		final Properties journalProperties = new Properties();
        {
            journalProperties.setProperty(Journal.Options.BUFFER_MODE,
                    BufferMode.MemStore.name());
            
        }
        
        Journal m_indexManager = new Journal(journalProperties);
 		
		Server nss = NanoSparqlServer.newInstance(9999, "jettyMavenTest.xml", m_indexManager, null);
		
		final RemoteRepositoryManager m_repo = new RemoteRepositoryManager(
				SampleBlazegraphCustomFunctionRemote.serviceURL , false /* useLBS */);
		
		try {
			
			nss.start();
			
			m_repo.createRepository(namespace, journalProperties);
			
			final String resource = "/data.n3";
			SampleBlazegraphCustomFunctionRemote.loadDataFromResource(m_repo, namespace, resource);

			// execute query
			final TupleQueryResult result = m_repo.getRepositoryForNamespace(namespace)
					.prepareTupleQuery(SampleBlazegraphCustomFunctionRemote.QUERY)
					.evaluate();
			
			int countResults = 0;
			String expected = "http://www.example.com/document1";
			String actual = null;
			
			while(result.hasNext()){
				
				BindingSet bs = result.next();
			
				actual = bs.getBinding("doc").getValue().stringValue();
				
				countResults++;
	
			}
			
			result.close();
			
			Assert.assertEquals(1, countResults);
			
			Assert.assertEquals(expected, actual);
			
			
			
			
		} finally {
			
			nss.stop();
			
		}
		
	}

}
