inputAS = doc.getAnnotations("")
gate.AnnotationSet targetKeywordAnnot = inputAS.get("ManualAnnotation")
gate.AnnotationSet contextKeywordAnnot = inputAS.get("Context")

gate.Annotation targetKeyword = gate.Utils.getOnlyAnn(targetKeywordAnnot)

long targetPriority = Long.parseLong(targetKeyword.getFeatures().get("priority").toString())

if(!contextKeywordAnnot.isEmpty()){
    for(annot in contextKeywordAnnot){
		long annotPriority = Long.parseLong(annot.getFeatures().get("priority").toString())	
		if(annotPriority != targetPriority){
			inputAS.remove(annot)
		}		
	}
}