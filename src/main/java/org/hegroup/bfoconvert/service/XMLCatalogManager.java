/**
 * Service class to read XML catalog file (catalog-v001.xml) which 
 * contains the URL mapping information 
 */
package org.hegroup.bfoconvert.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;
import org.protege.xmlcatalog.entry.UriEntry;
import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XMLCatalogManager {
	private static final Logger logger = LoggerFactory.getLogger(XMLCatalogManager.class);

	private XMLCatalog catalog;
	private HashMap<IRI, IRI> nameList = new HashMap<IRI, IRI>();
	
	private URL catalogURL;


	/**
	 * @return the catalogURL
	 */
	public URL getCatalogURL() {
		return catalogURL;
	}

	/**
	 * @param catalogURL the catalogURL to set
	 */
	public void setCatalogURL(URL catalogURL) {
		this.catalogURL = catalogURL;
	}

	/**
	 * @return the nameList
	 */
	public HashMap<IRI, IRI> getNameList() {
		return nameList;
	}

	/**
	 * @param nameList the nameList to set
	 */
	public void setNameList(HashMap<IRI, IRI> nameList) {
		this.nameList = nameList;
	}

	/**
	 * @return the catalog
	 */
	public XMLCatalog getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog the catalog to set
	 */
	public void setCatalog(XMLCatalog catalog) {
		this.catalog = catalog;
	}

	public boolean readCatalog() {
        System.setProperty("xml.catalog.ignoreMissing", "true");
        
        try {
			setCatalog(CatalogUtilities.parseDocument(catalogURL));
			
			for (Entry entry : catalog.getEntries()) {
				if (entry instanceof UriEntry) {
					URI name = URI.create(((UriEntry) entry).getName());
					if (getRedirect(name)!=null)
						nameList.put(IRI.create(name), IRI.create(getRedirect(name)));
				}
				
			}
			
		} catch (IOException e) {
			logger.error("IOException: {}", catalogURL);
		}
        
        return true;
    }
    
    public URI getRedirect(URI urlIn){
    	return CatalogUtilities.getRedirect(urlIn, catalog);
    }
}
