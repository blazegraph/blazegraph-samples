/**

Copyright (C) SYSTAP, LLC 2006-2015.  All rights reserved.

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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package sample.rdf.rules;

import java.util.LinkedList;
import java.util.List;

import com.bigdata.rdf.rules.FastClosure;
import com.bigdata.rdf.store.AbstractTripleStore;
import com.bigdata.relation.rule.Rule;


/**
 * The closure program must include the new custom inference rules by
 * overriding the parent method {@link BaseClosure#getCustomRules(String)}.
 */
public class SampleClosure extends FastClosure {

	public SampleClosure(final AbstractTripleStore db) {
		super(db);
	}

	/**
	 * Called once by super class during construction of inference program.
	 */
	public List<Rule> getCustomRules(final String relationName) {
		
		final List<Rule> rules = new LinkedList<Rule>();
		
		rules.add(new SampleRule(relationName, vocab));
		
		return rules;
		
	}
	
}
