package sample.customFunction.embedded;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openrdf.OpenRDFException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

import com.bigdata.rdf.sail.BigdataSailRepository;

public class Utils {
	
	public static Repository createRepository(){
		return null;
		
		
		
	}
	
	/*
	 * Execute sparql select query.
	 */
	public static TupleQueryResult executeSelectQuery(Repository repo, String query,
			QueryLanguage ql) throws OpenRDFException  {

		RepositoryConnection cxn;
		if (repo instanceof BigdataSailRepository) {
			cxn = ((BigdataSailRepository) repo).getReadOnlyConnection();
		} else {
			cxn = repo.getConnection();
		}

		try {

			final TupleQuery tupleQuery = cxn.prepareTupleQuery(ql, query);
			tupleQuery.setIncludeInferred(true /* includeInferred */);
			return tupleQuery.evaluate();
			
		} finally {
			// close the repository connection
			cxn.close();
		}
	}	
	
	/*
	 * Load data from resources into a repository.
	 */
	public static void loadDataFromResources(Repository repo, String resource, String baseURL)
			throws OpenRDFException, IOException {

		RepositoryConnection cxn = repo.getConnection();
		
		try {
			cxn.begin();
			try {
				InputStream is = SampleBlazegraphCustomFunctionEmbedded.class.getResourceAsStream(resource);
				if (is == null) {
					throw new IOException("Could not locate resource: " + resource);
				}
				Reader reader = new InputStreamReader(new BufferedInputStream(is));
				try {
					cxn.add(reader, baseURL, RDFFormat.N3);
				} finally {
					reader.close();
				}
				cxn.commit();
			} catch (OpenRDFException ex) {
				cxn.rollback();
				throw ex;
			}
		} finally {
			// close the repository connection
			cxn.close();
		}
	}

}
