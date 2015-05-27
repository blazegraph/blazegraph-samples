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

/*
 * Wiki page link:
 * http://wiki.blazegraph.com/wiki/index.php/First_Application_Tutorial
 */

import java.util.Properties;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import com.bigdata.journal.BufferMode;
import com.bigdata.journal.Options;
import com.bigdata.rdf.sail.BigdataSail;
import com.bigdata.rdf.sail.BigdataSailRepository;

public class HelloBlazegraph {
	public static void main(String[] args) throws OpenRDFException {

		final Properties props = new Properties();
		props.put(Options.BUFFER_MODE, BufferMode.DiskRW); // persistent file system located journal
		props.put(Options.FILE, "/tmp/blazegraph/test.jnl"); // journal file location

		final BigdataSail sail = new BigdataSail(props); // instantiate a sail
		final Repository repo = new BigdataSailRepository(sail); // create a Sesame repository

		repo.initialize();

		try {
			// prepare a statement
			URIImpl subject = new URIImpl("http://blazegraph.com/Blazegraph");
			URIImpl predicate = new URIImpl("http://blazegraph.com/says");
			Literal object = new LiteralImpl("hello");
			Statement stmt = new StatementImpl(subject, predicate, object);

			// open repository connection
			RepositoryConnection cxn = repo.getConnection();

			// upload data to repository
			try {
				cxn.begin();
				cxn.add(stmt);
				cxn.commit();
			} catch (OpenRDFException ex) {
				cxn.rollback();
				throw ex;
			} finally {
				// close the repository connection
				cxn.close();
			}

			// open connection
			if (repo instanceof BigdataSailRepository) {
				cxn = ((BigdataSailRepository) repo).getReadOnlyConnection();
			} else {
				cxn = repo.getConnection();
			}

			// evaluate sparql query
			try {

				final TupleQuery tupleQuery = cxn
						.prepareTupleQuery(QueryLanguage.SPARQL,
								"select ?p ?o where { <http://blazegraph.com/Blazegraph> ?p ?o . }");
				TupleQueryResult result = tupleQuery.evaluate();
				try {
					while (result.hasNext()) {
						BindingSet bindingSet = result.next();
						System.err.println(bindingSet);
					}
				} finally {
					result.close();
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
