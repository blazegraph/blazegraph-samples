package sample.customFunction.remote;

import java.util.Map;

import org.openrdf.model.Value;

import com.bigdata.bop.BOp;
import com.bigdata.bop.IBindingSet;
import com.bigdata.bop.IValueExpression;
import com.bigdata.rdf.internal.IV;
import com.bigdata.rdf.internal.constraints.INeedsMaterialization;
import com.bigdata.rdf.internal.constraints.XSDBooleanIVValueExpression;
import com.bigdata.rdf.sparql.ast.GlobalAnnotations;

public class SecurityFilter extends XSDBooleanIVValueExpression 
			implements INeedsMaterialization {

	private GlobalSecurityValidator validator;

	/**
	* Required deep copy constructor.
	* 
	* @param op
	*/
	public SecurityFilter(final SecurityFilter op) {
		super(op);
	}
	
	/**
	* Required shallow copy constructor.
	* 
	* @param args
	*            The function arguments.
	* @param anns
	*            The function annotations.
	*/
	public SecurityFilter(final BOp[] args, final Map<String, Object> anns) {
		super(args, anns);
	}
	
	/**
	* The function needs two pieces of information to operate - the document to check
	* and the user to check against.
	 * @param validator 
	*/
	public SecurityFilter(
	    final IValueExpression<? extends IV> user,
	    final IValueExpression<? extends IV> document,
	    final GlobalAnnotations globals, GlobalSecurityValidator validator) {
	
		this(new BOp[] { user, document }, XSDBooleanIVValueExpression.anns(globals));
		this.validator = validator;
	
	}
	
	@Override
	protected boolean accept(final IBindingSet bset) {
	
		// get the bound term for the ?user var
		final Value user = asValue(getAndCheckBound(0, bset));
		
		// get the bound term for the ?document var
		final Value document = asValue(getAndCheckBound(1, bset));
		
		return validator.validate(user, document);
	
	}

	public Requirement getRequirement() {
		return Requirement.SOMETIMES;
	}
}