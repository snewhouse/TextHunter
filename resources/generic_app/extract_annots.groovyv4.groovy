// Richard Jackson richard.g.jackson@slam.nhs.uk 
// to use this app, make sure AS transfer moved target annots to default set. Then run once with target annots (use default set in groovy params
// and again for Lookup annotations to include negatives. warning, make sure lookups only are used in negatives, or it could get v confusing!
// Takes a corpus and spits out a tsv of annotations/features
// now also adds a context feature - use jape to create the desired context and supply the script with:
// contextAnnotation
// path = output directory
// annotationSet = input annotation set
// annotationType = input annotation type
//
import java.util.*;
import java.io.*;
import java.lang.*;
import gate.Utils.*

	void beforeCorpus(c) {

	// to match annotations to columns between documents
	csvFeatureMap = new LinkedHashMap()
	
	//default path
	if(scriptParams.path == null){
		path = "C:\\Documents and Settings\\rjackson1\\Desktop\\outputs\\default.tsv"
	} else{
		path = scriptParams.path
	}
	
	
	tempFile = new File(path + "_temp")
	
	
	
	
	//gramDocLookup = new String[]

	featureValue = ""
	output = null
	i =0
	
	
	}


try{
    def tempOut = new FileWriter(tempFile, true)    


	
	//inputAS = doc.getAnnotations(scriptParams.annotationSet)
	//default to Automatic annot set if not supplied
	if(scriptParams.annotationSet == null){
		inputAS = doc.getAnnotations("Automatic")
	} else{
		inputAS = doc.getAnnotations(scriptParams.annotationSet)	
	}
	

	
observation = scriptParams.annotationType
annots = inputAS.get(observation)
contextAnnotation = scriptParams.contextAnnotation



docFeatures = doc.getFeatures()

//ad doc features to all annots if there are any, otherwise just add doc annots
println doc.getName() + " " + annots.isEmpty()

if(!annots.isEmpty()){

	for(annot in annots){
		features = annot.getFeatures()
		
			tempOut.write( doc.getName() + "\t")
			//copy document features into master map
			Iterator it1 = docFeatures.entrySet().iterator()
			while(it1.hasNext()){
				Map.Entry pairs = (Map.Entry)it1.next();
				Object ob = pairs.getValue()
				csvFeatureMap.put(gate.Utils.cleanString(pairs.getKey()),gate.Utils.cleanString(ob.toString()))
			}
			
			//copy string for annotation
			csvFeatureMap.put("String",gate.Utils.cleanStringFor(doc, annot))
			
			//copy additional context for annotation, based upon pre-existing overlapping JAPE (use jape to create context first and supply annot name
			//to script
			
			AnnotationSet contextStringAS = gate.Utils.getOverlappingAnnotations(inputAS, annot)
			Iterator it3 = contextStringAS.iterator()
			while(it3.hasNext()){
				Annotation coveringAnnot = it3.next();
				if(coveringAnnot.getType().equals(contextAnnotation)){
					csvFeatureMap.put("ContextString",gate.Utils.cleanStringFor(doc, coveringAnnot))
				}

			}
			
			//.. and finally some more details
			csvFeatureMap.put("annotType",annot.getType())
			csvFeatureMap.put("annotID",annot.getId())
			csvFeatureMap.put("annotStart",annot.start())
			csvFeatureMap.put("annotEnd",annot.end())
				
			//copy annotations features into master map
			Iterator it = features.entrySet().iterator()	
			while(it.hasNext()){
				Map.Entry pairs = (Map.Entry)it.next();
				Object ob = pairs.getValue()
				csvFeatureMap.put(gate.Utils.cleanString(pairs.getKey()),gate.Utils.cleanString(ob.toString()))
			}


			
			
			
			//Iterator through master and output annotations
			Iterator it2 = csvFeatureMap.entrySet().iterator()
			while(it2.hasNext()){
				Map.Entry masterPairs = (Map.Entry)it2.next();			
				tempOut.write(masterPairs.getValue() + "\t")
				masterPairs.setValue("")	
			}
			//trying to write output string of annotation, but doesn't work properly due to variable number of featuers
			//tempOut.write(gate.Utils.cleanStringFor(doc, annot))
			tempOut.write("\n")
	}
}
else{
	Iterator it1 = docFeatures.entrySet().iterator()
	while(it1.hasNext()){
		Map.Entry pairs = (Map.Entry)it1.next();
		Object ob = pairs.getValue()
		//copy document features into master map
		csvFeatureMap.put(gate.Utils.cleanString(pairs.getKey()),gate.Utils.cleanString(ob.toString()))
	}
	tempOut.write( doc.getName() + "\t")
	Iterator it2 = csvFeatureMap.entrySet().iterator()
	while(it2.hasNext()){
		Map.Entry masterPairs = (Map.Entry)it2.next();			
		tempOut.write(masterPairs.getValue() + "\t")
		masterPairs.setValue("")	
	}
	tempOut.write("\n")
}








    tempOut.close()
  }catch (Exception e){//Catch exception if any
  System.err.println("Error: " + e.getMessage());
  }

  
  	void afterCorpus(c) {
	
	
		def out = new FileWriter(path, true)
		def a = new FileReader(tempFile)
		def inFile = new BufferedReader(a)	   
		
		
		
		
		out.write("docName\t")
	   	Iterator it3 = csvFeatureMap.entrySet().iterator()
		while(it3.hasNext()){
			Map.Entry masterPairs = (Map.Entry)it3.next()	
			out.write(masterPairs.getKey() + "\t")
		}
		out.write("String")
		out.write("\n")
		

		
        String line;
        while((line = inFile.readLine()) != null) {
			out.write(line);
			out.write("\n")

        }		
		
		


	
	out.close()
	inFile.close()
	tempFile.delete()
	
	}
	
	
	
	
