package sample.customFunction.embedded;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;

public class GlobalSecurityValidator {
	
	protected static final Logger log = Logger.getLogger(GlobalSecurityValidator.class);
	
	private static final String GRANTED_DOCUMENTS = "select ?user ?doc {" + //
													"?doc <http://www.example.com/grantedTo> ?user" + //
													"}";
	
	private final Map<Value, List<Value>> securityInfo = new HashMap<Value, List<Value>>();
	
	
	public GlobalSecurityValidator(final Repository repo) {
		
			final TupleQueryResult result;
			try {
				
				result = Utils.executeSelectQuery(repo, GRANTED_DOCUMENTS, QueryLanguage.SPARQL);
						
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
			
			} catch (OpenRDFException e) {
				log.error("Security info was not collected", e);
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
