/**
 * Service class to handle OWL files
 */
package org.hegroup.bfoconvert.service;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zxiang
 * @author Edison Ong
 *
 */
public class OWLFileManager {
	private static final Logger logger = LoggerFactory.getLogger(OWLFileManager.class);
	private OWLOntologyManager man;
	private OWLOntology ontMain;
	private Set<OWLOntology> onts;
	private OWLDataFactory df;
	private List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
	private PrefixDocumentFormat formatNew;
	
	private String inputFile;
	private String workDir;
	private String outputFile;
	
	private HashMap<String, String> hashMapTermMapping;
	private HashMap<String, HashMap<String, String>> hashMapTermSeparation;
	private HashMap<String, String> hashAddNamespace = new HashMap<String, String>();
	private HashMap<String, String> hashRemoveNamespace = new HashMap<String, String>();
	private HashMap<String, String> hashReplaceNamespace = new HashMap<String, String>();

	/**
	 * @return the hashReplaceNamespace
	 */
	public HashMap<String, String> getHashReplaceNamespace() {
		return hashReplaceNamespace;
	}



	/**
	 * @param hashReplaceNamespace the hashReplaceNamespace to set
	 */
	public void setHashReplaceNamespace(HashMap<String, String> hashReplaceNamespace) {
		this.hashReplaceNamespace = hashReplaceNamespace;
	}



	/**
	 * @return the workDir
	 */
	public String getWorkDir() {
		return workDir;
	}
	
	
	
	/**
	 * @param workDir the workDir to set
	 */
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}
		
	/**
	 * @return the inputFile
	 */
	public String getInputFile() {
		return inputFile;
	}



	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}



	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}



	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}



	/**
	 * @return the hashMapTerm
	 */
	public HashMap<String, String> getHashMapTermMapping() {
		return hashMapTermMapping;
	}



	/**
	 * @param hashMapTerm the hashMapTerm to set
	 */
	public void setHashMapTermMapping(HashMap<String, String> hashMapTermMapping) {
		this.hashMapTermMapping = hashMapTermMapping;
	}



	/**
	 * @return the hashMapTermSeparation
	 */
	public HashMap<String, HashMap<String, String>> getHashMapTermSeparation() {
		return hashMapTermSeparation;
	}



	/**
	 * @param hashMapTermSeparation the hashMapTermSeparation to set
	 */
	public void setHashMapTermSeparation(
			HashMap<String, HashMap<String, String>> hashMapTermSeparation) {
		this.hashMapTermSeparation = hashMapTermSeparation;
	}


	/**
	 * @return the hashAddNamespace
	 */
	public HashMap<String, String> getHashAddNamespace() {
		return hashAddNamespace;
	}



	/**
	 * @param hashAddNamespace the hashAddNamespace to set
	 */
	public void setHashAddNamespace(HashMap<String, String> hashAddNamespace) {
		this.hashAddNamespace = hashAddNamespace;
	}



	/**
	 * @return the hashRemoveNamespace
	 */
	public HashMap<String, String> getHashRemoveNamespace() {
		return hashRemoveNamespace;
	}
	

	/**
	 * @param hashRemoveNamespace the hashRemoveNamespace to set
	 */
	public void setHashRemoveNamespace(HashMap<String, String> hashRemoveNamespace) {
		this.hashRemoveNamespace = hashRemoveNamespace;
	}



	/**
	 * Do one to one term replacement for one OWL file
	 * @return if conversion is successful or not
	 */
	
	public boolean doTermConversion() {
		//OWLEntityRenamer owlEntityRenamer = new OWLEntityRenamer(man, man.getOntologies());
		OWLEntityRenamer owlEntityRenamer = new OWLEntityRenamer(man, onts);
    	for(String termOld : hashMapTermMapping.keySet()) {
			//logger.debug("mapping: {} ==> {}", termOld, hashMapTermMapping.get(termOld));
			changes.addAll(owlEntityRenamer.changeIRI(IRI.create(termOld.trim()), IRI.create(hashMapTermMapping.get(termOld).trim())));
    	}

		return true;
	}



	/**
	 * Load an ontology.
	 * @return true if ontology successfully loaded.
	 */


	public boolean loadOntology() {
		try {
			man = OWLManager.createOWLOntologyManager();

			final HashMap<IRI, IRI> ontMap = new HashMap<IRI, IRI>();
			Set<OWLOntologyIRIMapper> mapper = new HashSet<OWLOntologyIRIMapper>();

			//parse catalog, set up IRI mapping
			
			String catalogPath = (new File(inputFile)).getParent() + File.separator + "catalog-v001.xml";
			
			logger.debug("catalogPath: {}", catalogPath);
			
			if ((new File(catalogPath)).exists()) {
			
				XMLCatalogManager catalogUtil = new XMLCatalogManager();
				catalogUtil.setCatalogURL((new File(catalogPath)).toURI().toURL());
				catalogUtil.readCatalog();
				HashMap<IRI, IRI> nameList = catalogUtil.getNameList();
				
				for (IRI key : nameList.keySet()) {
					ontMap.put(key, nameList.get(key));
					//logger.debug("mapping: {} => {}", key, nameList.get(key));
				}
			}
			
			if ( workDir != null ) {
				logger.debug( "IRIMapper directory: {}", workDir );
				mapper.add( new AutoIRIMapper( new File( workDir ), true ) );
				man.setIRIMappers( mapper );
			}
			
			logger.debug("load: {}", inputFile);
			ontMain = man.loadOntology(IRI.create(new File(inputFile)));
			onts = ontMain.getDirectImports();
			onts.add(ontMain);
			df = man.getOWLDataFactory();
			

		} catch (OWLOntologyCreationException e) {
			logger.error("Could not create ontology: {}", e.getMessage());
			return false;
		} catch (UnknownOWLOntologyException e) {
			logger.error("UnknownOWLOntologyException: {}", e.getMessage());
			return false;
		} catch (MalformedURLException e) {
			logger.error("Could not parse catolog: {}", e.getMessage());
			return false;
		} 
		
		return true;
	}

	

	/**
	 * Split some terms to two terms, depending on if the term is an occurrent or a continuant.
	 * @return true if the mapping file is successfully parsed.
	 */


	public boolean doTermSeparation() {
		try {
			IRI occurrent = IRI.create("http://purl.obolibrary.org/obo/BFO_0000003");
			IRI continuant = IRI.create("http://purl.obolibrary.org/obo/BFO_0000002");

			
            for (OWLOntology ont : ontMain.getImportsClosure()) {
			
				for (String strToReplace : hashMapTermSeparation.keySet()) {
					OWLObjectPropertyExpression toReplace = df.getOWLObjectProperty(IRI
							.create(strToReplace));
					
					//for occurrent
					OWLObjectPropertyExpression replacementO = df.getOWLObjectProperty(IRI
							.create(hashMapTermSeparation.get(strToReplace).get("occurrent")));
					//for continuant			
					OWLObjectPropertyExpression replacementC = df.getOWLObjectProperty(IRI
							.create(hashMapTermSeparation.get(strToReplace).get("continuant")));
	
	
					for (OWLEquivalentClassesAxiom axiom : ont
							.getAxioms(AxiomType.EQUIVALENT_CLASSES)) {
	
						if (axiom.getObjectPropertiesInSignature().contains(toReplace)) {
							Iterator<OWLClassExpression> clss = axiom
									.getClassExpressions().iterator();
							OWLClassExpression clsL = clss.next();
							OWLClassExpression clsR = clss.next();
	
	
				            if (hasSuperClass(ontMain, occurrent, clsL) || hasSuperClass(ontMain, continuant, clsL)){
				            	OWLObjectPropertyExpression replacement = replacementO;
					            if (hasSuperClass(ontMain, continuant, clsL)){
					            	replacement = replacementC;
								}
		
					            RemoveAxiom removeAxiom = new RemoveAxiom(ont,
										(OWLAxiom) axiom);
								changes.add(removeAxiom);
				
					            OWLEquivalentClassesAxiom equiClassAiom = df
										.getOWLEquivalentClassesAxiom(
												clsL,
												replaceObjectPropertyExpression(df,
														clsR, toReplace, replacement));
				
								AddAxiom addAx = new AddAxiom(ont, equiClassAiom);
								changes.add(addAx);
				            }
						}
					}
					
					
					for (OWLSubClassOfAxiom axiom : ont
							.getAxioms(AxiomType.SUBCLASS_OF)) {
						
						if (axiom.getObjectPropertiesInSignature().contains(toReplace)) {
	
				            if (hasSuperClass(ontMain, occurrent, axiom.getSubClass()) 
				            		|| hasSuperClass(ontMain, continuant, axiom.getSubClass())){
				            	OWLObjectPropertyExpression replacement = replacementO;
					            if (hasSuperClass(ontMain, continuant, axiom.getSubClass())){
					            	replacement = replacementC;
								}
							
								RemoveAxiom removeAxiom = new RemoveAxiom(ont,
										(OWLAxiom) axiom);
								changes.add(removeAxiom);
		
								OWLSubClassOfAxiom subClassAiom = df
										.getOWLSubClassOfAxiom(
												axiom.getSubClass(),
												replaceObjectPropertyExpression(df,
														axiom.getSuperClass(), toReplace, replacement));
			
								AddAxiom addAx = new AddAxiom(ont, subClassAiom);
								changes.add(addAx);
				            }
						}
	
					}
					
				}
            }

		} catch (UnknownOWLOntologyException e) {
			logger.error("UnknownOWLOntologyException: {}", e.getMessage());
			return false;
		} 
		
		return true;
	}
	
	public boolean doNamespaceCleanup() {	
		List<String> addNSs = new ArrayList<String>();
		List<String> removeNSs = new ArrayList<String>();
		HashMap<String, String> replaceNSs = new HashMap<String, String>();
		List<String> ontIRIs = new ArrayList<String>(); 
		
		Set<OWLOntology> onts = ontMain.getImports();
		for ( OWLOntology ont : onts ) {
			ontIRIs.add( ont.getOntologyID().getOntologyIRI().get().toURI().toString() );
		}
		
		for ( String replaceNS : hashReplaceNamespace.keySet() ) {
			String type = hashReplaceNamespace.get( replaceNS );
			String[] replaceNSArray = replaceNS.split( ";" );
			logger.debug( "replace {}: {}", type, replaceNS );
			if ( type.equalsIgnoreCase( "import" ) ) {
				if ( ontIRIs.contains( replaceNSArray[0] ) ) {
					changes.add( new RemoveImport( ontMain, df.getOWLImportsDeclaration( IRI.create( replaceNSArray[0] ) ) ) );
					changes.add( new AddImport( ontMain, df.getOWLImportsDeclaration( IRI.create( replaceNSArray[1] ) ) ) );
				}
			} else if ( type.equalsIgnoreCase( "xmlns" ) ) {
				replaceNSs.put( replaceNSArray[0], replaceNSArray[1] );
			} else {
				return false;
			}
		}
		
		for  (String addNS : hashAddNamespace.keySet() ) {
			String type = hashAddNamespace.get( addNS );
			logger.debug( "add {}: {}", type, addNS );
			if ( type.equalsIgnoreCase( "import" ) ) {
				changes.add( new AddImport( ontMain, df.getOWLImportsDeclaration( IRI.create( addNS ) ) ) );
			} else if ( type.equalsIgnoreCase( "xmlns" ) ) {
				addNSs.add( addNS );
			} else {
				return false;
			}
			
		}
		
		for ( String removeNS : hashRemoveNamespace.keySet() ) {
			String type = hashRemoveNamespace.get( removeNS );
			logger.debug( "remove {}: {}", type, removeNS );
			if ( type.equalsIgnoreCase( "import" ) ) {
				changes.add( new RemoveImport( ontMain, df.getOWLImportsDeclaration( IRI.create( removeNS ) ) ) );
			} else if ( type.equalsIgnoreCase( "xmlns" ) ) {
				removeNSs.add( removeNS );
			} else {
				return false;
			}
		}
		
		OWLDocumentFormat format = man.getOntologyFormat( ontMain );
		formatNew = man.getOntologyFormat( ontMain ).asPrefixOWLOntologyFormat();
		formatNew.clear();
		
		if ( format.isPrefixOWLOntologyFormat() ) {
			
			Map<String, String> mapping = format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap();
			for ( String prefixName : mapping.keySet() ) {
				String prefix = mapping.get( prefixName );
				if ( replaceNSs.containsKey( prefix ) ) {
					String replacePrefix = replaceNSs.get( prefix );
					String[] replacePrefixList = replacePrefix.split( "/" );
					String replacePrefixName = replacePrefixList[replacePrefixList.length-1];
					formatNew.setPrefix( replacePrefixName, replacePrefix );
				} else if ( !removeNSs.contains( prefix ) ) {
					formatNew.setPrefix( prefixName, prefix );
				}
			}
			for ( String newPrefix : addNSs ) {
				if ( mapping.values().contains( newPrefix ) ) {
					continue;
				}
				String[] newPrefixList = newPrefix.split( "/" );
				String newPrefixName = newPrefixList[newPrefixList.length-1];
				formatNew.setPrefix( newPrefixName, newPrefix );
			}
		}
		
		return true;
	}
	
	public boolean changeImports(String bfoVersion) {
		HashSet<String> importIRIs = new HashSet<String>();
		switch (bfoVersion) {
		case "bfo12":
			importIRIs.clear();
			importIRIs.add("http://www.ifomis.org/bfo/1.1");
			importIRIs.add("http://www.ifomis.org/bfo/1.0");
			changeImport(importIRIs, "https://raw.github.com/ontodev/bfo/master/bfo-1.2.owl");

			importIRIs.clear();
			importIRIs.add("http://www.obofoundry.org/ro/ro.owl");
			importIRIs.add("http://purl.org/obo/owl/ro");
			changeImport(importIRIs, "http://purl.obolibrary.org/obo/ro.owl");
			
			break;
		
		case "bfo20pregraz":
			importIRIs.clear();
			importIRIs.add("http://www.ifomis.org/bfo/1.1");
			importIRIs.add("http://www.ifomis.org/bfo/1.0");
			importIRIs.add("https://raw.github.com/ontodev/bfo/master/bfo-1.2.owl");
			changeImport(importIRIs, "http://purl.obolibrary.org/obo/bfo/2010-05-25/ruttenberg-bfo2.owl");

			importIRIs.clear();
			importIRIs.add("http://www.obofoundry.org/ro/ro.owl");
			importIRIs.add("http://purl.org/obo/owl/ro");
			changeImport(importIRIs, "http://purl.obolibrary.org/obo/ro.owl");
			
			break;

		case "bfo20graz":
			importIRIs.clear();
			importIRIs.add("http://www.ifomis.org/bfo/1.1");
			importIRIs.add("http://www.ifomis.org/bfo/1.0");
			importIRIs.add("https://raw.github.com/ontodev/bfo/master/bfo-1.2.owl");
			importIRIs.add("http://purl.obolibrary.org/obo/bfo/2010-05-25/ruttenberg-bfo2.owl");
			changeImport(importIRIs, "http://purl.obolibrary.org/obo/bfo.owl");

			importIRIs.clear();
			importIRIs.add("http://www.obofoundry.org/ro/ro.owl");
			importIRIs.add("http://purl.org/obo/owl/ro");
			changeImport(importIRIs, "http://purl.obolibrary.org/obo/ro.owl");
			
		case "bfogeneral":
			importIRIs.clear();
			importIRIs.add("http://www.ifomis.org/bfo/1.1");
			importIRIs.add("http://www.ifomis.org/bfo/1.0");
			importIRIs.add("https://raw.github.com/ontodev/bfo/master/bfo-1.2.owl");
			importIRIs.add("http://purl.obolibrary.org/obo/bfo/2010-05-25/ruttenberg-bfo2.owl");
			changeImport(importIRIs, "http://purl.obolibrary.org/obo/bfo.owl");

			importIRIs.clear();
			importIRIs.add("http://www.obofoundry.org/ro/ro.owl");
			importIRIs.add("http://purl.org/obo/owl/ro");
			changeImport(importIRIs, "http://purl.obolibrary.org/obo/ro.owl");

			break;
		}
		
		
		return true;
	}

	/**
	 * 
	 * @param importIRI
	 * @param importIRINew
	 * @return
	 */
	public boolean changeImport(HashSet<String> importIRIs, String importIRINew) {
		
		for (OWLOntology ont : ontMain.getImports()) {
			logger.debug("{} import: {}", ontMain.getOntologyID().getOntologyIRI().toString(), ont.getOntologyID().getOntologyIRI().toString());
			if (importIRIs.contains(ont.getOntologyID().getOntologyIRI().toString())) {
				for (String importIRI : importIRIs) {
					changes.add(new RemoveImport(ontMain, df.getOWLImportsDeclaration(
							IRI.create(importIRI))));
				}
				changes.add(new AddImport(ontMain, df.getOWLImportsDeclaration(
						IRI.create(importIRINew))));
				break;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Save changes back to owl files.
	 * @return true if no error occured while saving ontologies.
	 */

	public boolean saveChanges() {
		man.applyChanges(changes);
		
		HashSet<OWLOntology> ontologyChanged = new HashSet<OWLOntology>();
		
		for (OWLOntologyChange change : changes) {
			ontologyChanged.add(change.getOntology());
		}
		
		
		for (OWLOntology ont : ontologyChanged) {
            try {
            	man.saveOntology(ont, formatNew);
            	
				logger.debug("save ontology: {}", ont.getOntologyID().getOntologyIRI());
			} catch (OWLOntologyStorageException e) {
				logger.error("error saving ontology: {}. {}", ont.getOntologyID().getOntologyIRI(), e.getMessage());
				//return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Check if a class has certain parent class
	 * @param ont
	 * @param sup
	 * @param sub
	 * @return
	 */
	private boolean hasSuperClass(OWLOntology ont, IRI sup, OWLClassExpression sub) {
		OWLReasoner reasoner = new StructuralReasoner(ont,
                new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
		for (Node<OWLClass> supTemp :  reasoner.getSuperClasses( sub, true ) ) {
			for (OWLClass supCl : supTemp ) {
				if ( supCl.getIRI().equals( sup ) ) {
					return true;
				} else {
					return hasSuperClass(ont, sup, supCl);
				}
			}
		}
		
		return false;
		
	}
	
	
	/**
	 * Replace an OWLObjectPropertyExpression with another for an OWLClassExpression recursively.
	 * @param df
	 * @param target
	 * @param toReplace
	 * @param replacement
	 * @return
	 */
	private OWLClassExpression replaceObjectPropertyExpression(
			OWLDataFactory df, OWLClassExpression target,
			OWLObjectPropertyExpression toReplace,
			OWLObjectPropertyExpression replacement) {

		if (!target.getObjectPropertiesInSignature().contains(toReplace)) {
			return target;
		}

		switch (target.getClassExpressionType()) {
		case OWL_CLASS:
			return target;
		case OBJECT_COMPLEMENT_OF:
			return df.getOWLObjectComplementOf(replaceObjectPropertyExpression(
					df, target.getComplementNNF(), toReplace, replacement));
		case OBJECT_INTERSECTION_OF:
			OWLObjectIntersectionOf intersects = (OWLObjectIntersectionOf) target;
			HashSet<OWLClassExpression> replacedIntersects = new HashSet<OWLClassExpression>();

			for (OWLClassExpression cls : intersects.asConjunctSet()) {
				if (cls.getObjectPropertiesInSignature().contains(toReplace)) {
					replacedIntersects.add(replaceObjectPropertyExpression(df, cls,
						toReplace, replacement));
				}
				else {
					replacedIntersects.add(cls);
				}
			}

			return df.getOWLObjectIntersectionOf(replacedIntersects);

		case OBJECT_UNION_OF:
			OWLObjectUnionOf unions = (OWLObjectUnionOf) target;
			HashSet<OWLClassExpression> replacedUnions = new HashSet<OWLClassExpression>();

			for (OWLClassExpression cls : unions.asDisjunctSet()) {
				if (cls.getObjectPropertiesInSignature().contains(toReplace)) {
					replacedUnions.add(replaceObjectPropertyExpression(df, cls,
						toReplace, replacement));
				}
				else {
					replacedUnions.add(cls);
				}
			}

			return df.getOWLObjectUnionOf(replacedUnions);

		case OBJECT_SOME_VALUES_FROM:
			OWLObjectSomeValuesFrom someRestrict = (OWLObjectSomeValuesFrom) target;
			OWLObjectPropertyExpression someRole = someRestrict.getProperty();
			if (someRole.equals(toReplace))
				someRole = replacement;
			OWLClassExpression someFiller = someRestrict.getFiller();

			if (someFiller.getObjectPropertiesInSignature().contains(toReplace)) {
				someFiller = replaceObjectPropertyExpression(df, someFiller, toReplace,
						replacement); 
			}
			return df.getOWLObjectSomeValuesFrom(
					someRole, someFiller);

		case OBJECT_ALL_VALUES_FROM:
			OWLObjectAllValuesFrom allRestrict = (OWLObjectAllValuesFrom) target;
			OWLObjectPropertyExpression allRole = allRestrict.getProperty();
			if (allRole.equals(toReplace))
				allRole = replacement;
			OWLClassExpression allFiller = allRestrict.getFiller();

			if (allFiller.getObjectPropertiesInSignature().contains(toReplace)) {
				allFiller = replaceObjectPropertyExpression(df, allFiller, toReplace,
						replacement); 
			}
			
			return df.getOWLObjectAllValuesFrom(
					allRole,
					replaceObjectPropertyExpression(df, allFiller, toReplace,
							replacement));

		case OBJECT_MAX_CARDINALITY:
			OWLObjectMaxCardinality maxRestrict = (OWLObjectMaxCardinality) target;

			int maxCardinality = maxRestrict.getCardinality();
			OWLClassExpression maxFiller = maxRestrict.getFiller();
			OWLObjectPropertyExpression maxRole = maxRestrict.getProperty();
			if (maxRole.equals(toReplace))
				maxRole = replacement;

			if (maxFiller.getObjectPropertiesInSignature().contains(toReplace)) {
				maxFiller = replaceObjectPropertyExpression(df, maxFiller, toReplace,
						replacement); 
			}
			return df.getOWLObjectMaxCardinality(maxCardinality, maxRole,
					maxFiller);

		case OBJECT_MIN_CARDINALITY:
			OWLObjectMinCardinality minRestrict = (OWLObjectMinCardinality) target;

			int minCardinality = minRestrict.getCardinality();
			OWLClassExpression minFiller = minRestrict.getFiller();
			OWLObjectPropertyExpression minRole = minRestrict.getProperty();
			if (minRole.equals(toReplace))
				minRole = replacement;

			if (minFiller.getObjectPropertiesInSignature().contains(toReplace)) {
				minFiller = replaceObjectPropertyExpression(df, minFiller, toReplace,
						replacement); 
			}
			return df.getOWLObjectMinCardinality(minCardinality, minRole,
					minFiller);

		default:
			return target;
		}

	}	
}
