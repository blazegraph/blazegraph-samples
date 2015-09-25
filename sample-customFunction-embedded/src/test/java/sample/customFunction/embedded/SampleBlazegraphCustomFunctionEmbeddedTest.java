package sample.customFunction.embedded;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;

public class SampleBlazegraphCustomFunctionEmbeddedTest {
	
	@Test
	public void testCustomFunctionEmbedded() throws OpenRDFException, IOException{
		
		final Repository repo = SampleBlazegraphCustomFunctionEmbedded.createRepository();
		SampleBlazegraphCustomFunctionEmbedded.registerCustomFunction(repo);
		
		try {
			
			repo.initialize();
			Utils.loadDataFromResources(repo, "data.n3", "");
			final TupleQueryResult result = Utils.executeSelectQuery(repo, 
					SampleBlazegraphCustomFunctionEmbedded.QUERY, QueryLanguage.SPARQL);
			
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
			
		}finally {
			repo.shutDown();
		}
		
	}

}
