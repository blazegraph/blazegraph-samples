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

/**
 * See <a href="http://wiki.blazegraph.com/wiki/index.php/Blueprints_API_remote_mode">Blueprints API remote mode</a>
 */
/*
 * Wiki page link:
 * http://wiki.blazegraph.com/wiki/index.php/Blueprints_API_remote_mode
 */

package sample.blueprints.remote;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

import com.bigdata.blueprints.BigdataGraph;
import com.bigdata.blueprints.BigdataGraphClient;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class SampleBlazegraphBlueprintsRemote {
	
	public static final Logger log = Logger.getLogger(SampleBlazegraphBlueprintsRemote.class);

    public static final String serviceURL = "http://localhost:9999/bigdata/sparql";

	public static void main(String[] args) throws Exception {
		
		final BigdataGraph graph = new BigdataGraphClient(SampleBlazegraphBlueprintsRemote.serviceURL);
		
		try{
		
			final InputStream is = SampleBlazegraphBlueprintsRemote.class.getClassLoader().getResourceAsStream("graph-example-1.xml");
			final Path file = Files.createTempFile("graph-example-1.xml", "tmp");
			try{
				Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
				graph.loadGraphML(file.toString());
			} finally {					
				Files.delete(file);	
				is.close();
			}
			
			for (final Vertex v : graph.getVertices()) {
				log.info(v);
			}
			for (final Edge e : graph.getEdges()) {
				log.info(e);
			}
				
		} finally {
			graph.shutdown();
		}
	}
}
