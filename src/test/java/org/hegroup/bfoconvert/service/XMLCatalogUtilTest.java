package org.hegroup.bfoconvert.service;


import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import org.hegroup.bfoconvert.service.XMLCatalogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

public class XMLCatalogUtilTest {
	private XMLCatalogManager catalogManager ;

	@Before
	public void setUp() throws Exception {
		catalogManager = new XMLCatalogManager();
	}

	@After
	public void tearDown() throws Exception {
	}

/*	
	
	@Test
	public final void testReadCatalog() {
		try {
			
			catalogManager.setCatalogURL(new URL("http://svn.code.sf.net/p/vaccineontology/code/trunk/src/ontology/catalog-v001.xml"));
			catalogManager.readCatalog();
			HashMap<IRI, IRI> nameList = catalogManager.getNameList();
			
			nameList.size();
			
			URI testURI = URI.create("http://purl.obolibrary.org/obo/vo/external/FMA_import.owl");
			URI redirectURI = URI.create("http://svn.code.sf.net/p/vaccineontology/code/trunk/src/ontology/FMA_import.owl");
			assertEquals(catalogManager.getRedirect(testURI), redirectURI);
			System.out.println(catalogManager.getRedirect(testURI));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
*/
}
