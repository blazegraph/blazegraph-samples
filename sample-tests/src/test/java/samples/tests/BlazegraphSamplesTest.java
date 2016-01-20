package samples.tests;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bigdata.journal.BufferMode;
import com.bigdata.journal.Journal;
import com.bigdata.rdf.sail.webapp.NanoSparqlServer;

import sample.blueprints.embedded.SampleBlazegraphBlueprintsEmbedded;
import sample.blueprints.remote.SampleBlazegraphBlueprintsRemote;
import sample.btree.JournalExample;
import sample.btree.JournalReadOnlyTxExample;
import sample.btree.JournalTxExample;
import sample.btree.ReadWriteIndexExample;
import sample.btree.ReadWriteIndexTxExample;
import sample.btree.TempStoreExample;
import sample.customFunction.embedded.SampleBlazegraphCustomFunctionEmbedded;
import sample.customFunction.remote.SampleBlazegraphCustomFunctionRemote;
import sample.rdf.rules.TestSample;
import sample.rdr.SampleBlazegraphRDR;
import sample.sesame.embedded.SampleBlazegraphSesameEmbedded;
import sample.sesame.first.HelloBlazegraph;
import sample.sesame.remote.SampleBlazegraphSesameRemote;

/**
 * 
 * This test suite verifies consistency of Blazegraph Samples, ensuring:
 * - syntax compatibility with core blazegraph;
 * - successful dependency resolution;
 * - no runtime errors generated on execution of all 'main' methods. 
 *
 */
public class BlazegraphSamplesTest {

    private Server nss;
    private Journal m_indexManager;
    private static final Logger log = Logger.getLogger(BlazegraphSamplesTest.class);
    
	
	
	@Before
	public void startNss() throws Exception{
		
		final Properties journalProperties = new Properties();
        {
            journalProperties.setProperty(Journal.Options.BUFFER_MODE,
                    BufferMode.MemStore.name());
        }
        
        m_indexManager = new Journal(journalProperties);
 		
		nss = NanoSparqlServer.newInstance(9999, "jettyMavenTest.xml", m_indexManager, null);
		
		nss.start();
		
	}
	
	@Test
	public void testSampleBlazegraphSesameRemote() throws Exception{
		
		log.info("SampleBlazegraphSesameRemote test:");
		SampleBlazegraphSesameRemote.main(null);
		log.info("ok");
	
	}
	
	@Test
	public void testSampleBlazegraphBlueprintsRemote() throws Exception{
		
		SampleBlazegraphBlueprintsRemote.main(null);
	
	}
	
	@Test
	public void testSampleBlazegraphRDR() throws Exception{
		
		SampleBlazegraphRDR.main(null);
	
	}
	
	@Test
	public void testSampleBlazegraphSesameEmbedded() throws Exception{
		
		SampleBlazegraphSesameEmbedded.main(null);
	
	}
	
	@Test
	public void testHelloBlazegraph() throws Exception{
		
		HelloBlazegraph.main(null);
	
	}
	
	@Test
	public void testSampleBlazegraphBlueprintsEmbedded() throws Exception{
		
		SampleBlazegraphBlueprintsEmbedded.main(null);
	
	}
	
	@Test
	public void testSampleBlazegraphCustomFunctionRemote() throws Exception{
		
		SampleBlazegraphCustomFunctionRemote.main(null);
	
	}
	
	@Test
	public void testJournalExample() throws Exception{
	
		JournalExample.main(null);
		
	}
	
	@Test
	public void testReadOnlyTxExample() throws Exception{
	
		JournalReadOnlyTxExample.main(null);
		
	}
	
	@Test
	public void testTxExample() throws Exception{
	
		JournalTxExample.main(null);
		
	}
	
	@Test
	public void testReadWriteIndexExample() throws Exception{
	
		ReadWriteIndexExample.main(null);
		
	}
	
	@Test
	public void testReadWriteIndexTxExample() throws Exception{
	
		ReadWriteIndexTxExample.main(null);
		
	}
	
	@Test
	public void testTempStoreExample() throws Exception{
	
		TempStoreExample.main(null);
		
	}

	
	@Test
	public void testSampleRDFRules() throws Exception{
		
		TestSample.main(null);
	
	}
	
	@After
	public void stopNss() throws Exception{
		
		nss.stop();
		
	}

}
