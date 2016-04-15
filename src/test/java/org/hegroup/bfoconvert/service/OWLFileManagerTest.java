/**
 * 
 */
package org.hegroup.bfoconvert.service;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

/**
 * @author zxiang
 *
 */
public class OWLFileManagerTest {

	/**
	 * Test method for {@link org.hegroup.bfoconvert.service.OWLFileManager#doTermConversion()}.
	 */
	@Test
	public final void testDoTermConversion() {
		OWLFileManager owlFileManager = new OWLFileManager();
		owlFileManager.setHashMapTermMapping(new HashMap<String, String>());
		owlFileManager.setInputFile("");
		owlFileManager.setOutputFile("");
//		owlFileManager.doTermConversion();
	}

	/**
	 * Test method for {@link org.hegroup.bfoconvert.service.OWLFileManager#doTermSeparation()}.
	 */
	@Test
	public final void testDoTermSeparation() {
		OWLFileManager owlFileManager = new OWLFileManager();
		owlFileManager.setHashMapTermMapping(new HashMap<String, String>());
		owlFileManager.setInputFile("");
		owlFileManager.setOutputFile("");
//		owlFileManager.doTermSeparation();
	}

}
