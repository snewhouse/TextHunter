inputAS = doc.getAnnotations("")
gate.AnnotationSet targetKeywordAnnot = inputAS.get("TargetKeyword")
gate.AnnotationSet contextKeywordAnnot = inputAS.get("Context")

if(!targetKeywordAnnot.isEmpty()){
    for(annot in targetKeywordAnnot){	
		int startwords = 0
		gate.AnnotationSet miniAS = gate.Utils.getOverlappingAnnotations( contextKeywordAnnot,annot )
		Iterator it3 = miniAS.iterator()
		while(it3.hasNext()){
			Annotation context = it3.next()		
			if(context.getFeatures().get("targetKeywordID") == annot.getFeatures().get("targetKeywordID")
				& (int)context.getFeatures().get("numWords") > startwords ){
				annot.getFeatures().put("contextString",gate.Utils.stringFor(doc,context))  
				annot.getFeatures().put("contextStart",gate.Utils.start(context))			
				annot.getFeatures().put("contextEnd",gate.Utils.end(context))
				annot.getFeatures().put("annotStart",gate.Utils.start(annot))			
				annot.getFeatures().put("annotEnd",gate.Utils.end(annot)) 
				startwords = (int)context.getFeatures().get("numWords")
			}			
		}
		if(annot.getFeatures().get("contextStart") == null){
			annot.getFeatures().put("contextString","No suitable context found")
		}
	}
}
