package sample.customFunction.remote;

import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.bigdata.bop.BOpContextBase;
import com.bigdata.bop.IValueExpression;
import com.bigdata.rdf.internal.IV;
import com.bigdata.rdf.sail.webapp.BigdataRDFServletContextListener;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;
import com.bigdata.rdf.sparql.ast.FunctionRegistry;
import com.bigdata.rdf.sparql.ast.GlobalAnnotations;
import com.bigdata.rdf.sparql.ast.ValueExpressionNode;
import com.bigdata.rdf.sparql.ast.eval.AST2BOpUtility;

public class MyBigdataRDFContextListener extends BigdataRDFServletContextListener {
	
	protected static final Logger log = Logger.getLogger(MyBigdataRDFContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent e) {
		
		super.contextInitialized(e);
		
		URI myFunctionURI = new URIImpl("http://www.example.com/validate");

		if(!FunctionRegistry.containsFunction(myFunctionURI)) { 
			
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
							      
				    
				    RemoteRepositoryManager m_repo = new RemoteRepositoryManager(SampleBlazegraphCustomFunctionRemote.serviceURL , false /* useLBS */);
				     
				    GlobalSecurityValidator securityValidator = null;
					try {
							securityValidator = new GlobalSecurityValidator(m_repo);
							
						} catch (Exception e) {
							
							log.error("Error on creating security validator");
						}
									     			      
				      // Return your custom function.
				      return new SecurityFilter(user, document, globals, securityValidator);
	
				}
	
			};	
	
	 		FunctionRegistry.add(myFunctionURI, securityFactory);
 		
		}
	}

}
