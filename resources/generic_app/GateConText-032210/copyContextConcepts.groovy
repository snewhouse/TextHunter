import java.util.*;
import java.io.*;
import java.lang.*;
import gate.Utils.*




inputAS = doc.getAnnotations("")
annots = inputAS.get("CoreConcept")



if(!annots.isEmpty()){

	for(annot in annots){

		AnnotationSet contextStringAS = gate.Utils.getOverlappingAnnotations(inputAS, annot, "TargetKeyword")
		Iterator it3 = contextStringAS.iterator()
		while(it3.hasNext()){
			Annotation coveringAnnot = it3.next();
			fm = annot.getFeatures().toFeatureMap().clone()
			coveringAnnot.getFeatures().put("Directionality",fm.get("Directionality"))
			coveringAnnot.getFeatures().put("Experiencer",fm.get("Experiencer"))
			coveringAnnot.getFeatures().put("Temporality",fm.get("Temporality"))				
		}
		
		contextStringAS = gate.Utils.getOverlappingAnnotations(inputAS, annot, "Context")
		it3 = contextStringAS.iterator()
		while(it3.hasNext()){
			Annotation coveringAnnot = it3.next();
			fm = annot.getFeatures().toFeatureMap().clone()
			coveringAnnot.getFeatures().put("Directionality",fm.get("Directionality"))
			coveringAnnot.getFeatures().put("Experiencer",fm.get("Experiencer"))
			coveringAnnot.getFeatures().put("Temporality",fm.get("Temporality"))				
		}		

	}


}

annots = inputAS.get("ForSomeTime")
if(!annots.isEmpty()){

	for(annot in annots){

		AnnotationSet contextStringAS = gate.Utils.getOverlappingAnnotations(inputAS, annot, "TargetKeyword")
		Iterator it3 = contextStringAS.iterator()
		while(it3.hasNext()){
			Annotation coveringAnnot = it3.next();
			fm = annot.getFeatures().toFeatureMap().clone()
			coveringAnnot.getFeatures().put("Length of Time",fm.get("Length of Time"))
			coveringAnnot.getFeatures().put("Unit of Time",fm.get("Unit of Time"))				
		}
		
		contextStringAS = gate.Utils.getOverlappingAnnotations(inputAS, annot, "Context")
		it3 = contextStringAS.iterator()
		while(it3.hasNext()){
			Annotation coveringAnnot = it3.next();
			fm = annot.getFeatures().toFeatureMap().clone()
			coveringAnnot.getFeatures().put("Length of Time",fm.get("Length of Time"))
			coveringAnnot.getFeatures().put("Unit of Time",fm.get("Unit of Time"))				
		}		

	}


}	

  