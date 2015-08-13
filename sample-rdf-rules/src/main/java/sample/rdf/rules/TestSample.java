package sample.rdf.rules;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import com.bigdata.journal.BufferMode;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;

/**
* A sample to demonstrate custom inference rules. 
* 
* @author <a href="mailto:mrpersonick@users.sourceforge.net">Mike Personick</a>
*/

public class TestSample {

	public static void main(String[] args) throws OpenRDFException {
		
		final Logger log = Logger.getLogger(TestSample.class);
		
		final Properties props = new Properties();
	
		props.put(BigdataSail.Options.BUFFER_MODE, BufferMode.MemStore.toString());
		props.put(BigdataSail.Options.FILE, "/tmp/blazegraph/test.jnl");
		
		props.put(BigdataSail.Options.AXIOMS_CLASS, SampleAxioms.class.getName());
		props.put(BigdataSail.Options.VOCABULARY_CLASS, SampleVocab.class.getName());
		props.put(BigdataSail.Options.CLOSURE_CLASS, SampleClosure.class.getName());
		
		props.put(BigdataSail.Options.TRUTH_MAINTENANCE, "true");
		props.put(BigdataSail.Options.JUSTIFY, "true");

//		props.setProperty(BigdataSail.Options.FORWARD_CHAIN_RDF_TYPE_RDFS_RESOURCE, "true");
		
		// instantiate a sail
		final BigdataSail sail = new BigdataSail(props);
		final Repository repo = new BigdataSailRepository(sail);
		
		try{
			repo.initialize();
			
			RepositoryConnection cxn = repo.getConnection();
			
			try {
				cxn.begin();
				try {
					
					final ValueFactory vf = sail.getValueFactory();
					
					final URI a = vf.createURI(SAMPLE.NAMESPACE+"a");
					final URI b = vf.createURI(SAMPLE.NAMESPACE+"b");
					final URI c = vf.createURI(SAMPLE.NAMESPACE+"c");
					final URI thing = vf.createURI(SAMPLE.NAMESPACE+"thing");
					final URI attribute = vf.createURI(SAMPLE.NAMESPACE+"attribute");
					final URI link = vf.createURI(SAMPLE.NAMESPACE+"link");
					final URI x = vf.createURI(SAMPLE.NAMESPACE+"x");
					final URI y = vf.createURI(SAMPLE.NAMESPACE+"y");
					
					final Literal foo = vf.createLiteral("foo");
					final Literal bar = vf.createLiteral("bar");
					
					cxn.add(a, RDF.TYPE, thing);
					cxn.add(a, SAMPLE.SIMILAR_TO, b);
					cxn.add(a, SAMPLE.SIMILAR_TO, c);
					
					// a and b are both rdf:type #thing
					cxn.add(b, RDF.TYPE, thing);
					// so a should pick up the literal attribute "foo"
					cxn.add(b, attribute, foo);
					// but not the link to x, since x is not a literal
					cxn.add(b, link, x);
					
					// c is not the same type as a, so a should not pick up any attributes,
					// even though they are marked as similar
					cxn.add(c, attribute, bar);
					cxn.add(c, link, y);
				
					cxn.commit();
					
					log.info("\n"+sail.getDatabase().dumpStore(true, true, false));
										
					log.info("'a' should pick up 'foo' " + cxn.hasStatement(a, attribute, foo, true));
					log.info("'a' should not pick up link to 'x' " + cxn.hasStatement(a, link, x, true));
					log.info("'a' should not pick up 'bar' " + cxn.hasStatement(a, attribute, bar, true));
					log.info("'a' should not pick up link to 'y' " + cxn.hasStatement(a, link, y, true));
					
					// now test truth maintenance
					
					// removing the similarity to b will cause a to lose "foo"
					cxn.remove(a, SAMPLE.SIMILAR_TO, b);
					// adding a common type between a and c will cause a to pick up "bar"
					cxn.add(c, RDF.TYPE, thing);
					
					cxn.commit();
					
					if (log.isInfoEnabled()) {
						log.info("\n"+sail.getDatabase().dumpStore(true, true, false));
					}
					
					log.info("'a' should have lost 'foo' " + cxn.hasStatement(a, attribute, foo, true));
					log.info("'a' should not pick up link to 'x' " + cxn.hasStatement(a, link, x, true));
					log.info("'a' should have picked up 'bar' " + cxn.hasStatement(a, attribute, bar, true));
					log.info("'a' should not pick up link to 'y' " + cxn.hasStatement(a, link, y, true));
					
				} catch (OpenRDFException ex) {
					cxn.rollback();
					throw ex;
				}
			} finally {
				// close the repository connection
				cxn.close();
			}
			
		} finally {
			repo.shutDown();
		}

	}

}
