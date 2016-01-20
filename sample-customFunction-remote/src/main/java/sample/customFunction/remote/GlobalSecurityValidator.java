package sample.customFunction.remote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;

import com.bigdata.rdf.sail.webapp.SD;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

public class GlobalSecurityValidator {
	
	protected static final Logger log = Logger.getLogger(GlobalSecurityValidator.class);
	
	private static final String GRANTED_DOCUMENTS = "select ?user ?doc {" + //
													"?doc <http://www.example.com/grantedTo> ?user" + //
													"}";
	
	private final Map<Value, List<Value>> securityInfo = new HashMap<Value, List<Value>>();
	
	
	public GlobalSecurityValidator(final RemoteRepositoryManager m_repo) throws Exception {
		
			GraphQueryResult repoDescription = m_repo.getRepositoryDescriptions();
			Set<String> namespaces = new HashSet<String>();
			while (repoDescription.hasNext()) {
				Statement stmt = repoDescription.next();
				if (stmt.getPredicate()
						.toString()
						.equals(SD.KB_NAMESPACE.stringValue())) {
					
					namespaces.add(stmt.getObject().stringValue());
				}
		
			}

			TupleQueryResult result;
			
			for (String namespace : namespaces) {
				
				try {
					RemoteRepository repo = m_repo.getRepositoryForNamespace(namespace);
					
					result = repo.prepareTupleQuery(GRANTED_DOCUMENTS).evaluate();
					
					while(result.hasNext()){
						
						BindingSet bs = result.next();
						
						Binding user = bs.getBinding("user");
						Binding document = bs.getBinding("doc");
											
						if(securityInfo.containsKey(user)){
													
							securityInfo.get(user).add(document.getValue());
							
						}else{
							
							List<Value> docs = new LinkedList<Value>();
							docs.add(document.getValue());
							
							securityInfo.put(user.getValue(), docs);
							
						}
						
					}
						
				} catch (Exception e) {
					log.error("Security info was not collected", e);
				}
				
			}
			
	}
	
	
	public boolean validate(final Value user, final Value document){
		
		if(securityInfo.containsKey(user)){
			
			if(securityInfo.get(user).contains(document)){				
				return true;				
			} else {				
				return false;				
			}	
			
		} else {			
			return false;			
		}
		
	}

}
