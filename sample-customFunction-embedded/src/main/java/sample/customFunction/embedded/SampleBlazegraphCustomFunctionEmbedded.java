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

package sample.customFunction.embedded;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;

import com.bigdata.bop.BOpContextBase;
import com.bigdata.bop.IValueExpression;
import com.bigdata.journal.BufferMode;
import com.bigdata.journal.Options;
import com.bigdata.rdf.internal.IV;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;
import com.bigdata.rdf.sparql.ast.FunctionRegistry;
import com.bigdata.rdf.sparql.ast.GlobalAnnotations;
import com.bigdata.rdf.sparql.ast.ValueExpressionNode;
import com.bigdata.rdf.sparql.ast.eval.AST2BOpUtility;


public class SampleBlazegraphCustomFunctionEmbedded {
	
	protected static final Logger log = Logger.getLogger(SampleBlazegraphCustomFunctionEmbedded.class);
	
	public static final String journalFile = "/tmp/blazegraph/test.jnl";
	
	/*
	 * Select all documents available to <http://www.example.com/John> 
	 */
	public static final String QUERY = "SELECT ?doc " + // 
			"{ ?doc rdf:type <http://www.example.com/Document> . " + //
			" filter(<http://www.example.com/validate>(<http://www.example.com/John>, ?doc)) . }";

	public static void main(String[] args) throws OpenRDFException, IOException {
		
		final Repository repo = createRepository();
		
		registerCustomFunction(repo);
			
		try{
			repo.initialize();
			
			/*
			 * Load data from resources 
			 * src/main/resources/data.n3
			 */
	
			Utils.loadDataFromResources(repo, "data.n3", "");
											
			final TupleQueryResult result = Utils.executeSelectQuery(repo, QUERY, QueryLanguage.SPARQL);
			
			try {
				while(result.hasNext()){
					
					BindingSet bs = result.next();
					log.info(bs);
					
				}
			} finally {
				result.close();
			}
		} finally {
			repo.shutDown();
		}
	}
	
	public static Repository createRepository(){
		
		final Properties props = new Properties();
		props.put(Options.BUFFER_MODE, BufferMode.DiskRW); 
		props.put(Options.FILE, journalFile); 
		final BigdataSail sail = new BigdataSail(props);
		final Repository repo = new BigdataSailRepository(sail);
		return repo;
		
	}
	
	public static void registerCustomFunction(final Repository repo){
		
		final URI myFunctionURI = new URIImpl("http://www.example.com/validate");
		
		final FunctionRegistry.Factory securityFactory = new FunctionRegistry.Factory() {

			public IValueExpression<? extends IV> create(
					BOpContextBase context, GlobalAnnotations globals,
					Map<String, Object> scalarValues,
					ValueExpressionNode... args) {
				
				
			      // Validate your argument(s)
			      FunctionRegistry.checkArgs(args, ValueExpressionNode.class, ValueExpressionNode.class);

			      // Turn them into physical (executable) bops
			      final IValueExpression<? extends IV> user =  AST2BOpUtility.toVE(context, globals, args[0]);
			      final IValueExpression<? extends IV> document = AST2BOpUtility.toVE(context, globals, args[1]);
						      
			     
			      final GlobalSecurityValidator securityValidator = new GlobalSecurityValidator(repo);			      
			     			      
			      // Return your custom function.
			      return new SecurityFilter(user, document, globals, securityValidator);

			}

		};		
		
		FunctionRegistry.add(myFunctionURI, securityFactory);
		
	}
}



		


