inputAS = doc.getAnnotations("")
gate.AnnotationSet targetKeywordAnnot = inputAS.get("TargetKeyword")
gate.AnnotationSet contextKeywordAnnot = inputAS.get("Context")
long minWords = 12
long startWords = 100000
long priority = 10
if(!targetKeywordAnnot.isEmpty()){
    for(annot in targetKeywordAnnot){
		annot.getFeatures().put("numWords",startWords)	
		annot.getFeatures().put("priority",priority)			
		gate.AnnotationSet miniAS = gate.Utils.getOverlappingAnnotations( contextKeywordAnnot,annot )
		Iterator it3 = miniAS.iterator()
		while(it3.hasNext()){
			Annotation context = it3.next()
			long targetWords = annot.getFeatures().get("numWords")
			long annotPriority = annot.getFeatures().get("priority")
			long contextWords = context.getFeatures().get("numWords")
			long contextPriority = context.getFeatures().get("priority")
			
			if((targetWords >= contextWords)&&(contextWords >= minWords)&&(contextPriority < annotPriority)){		
				annot.getFeatures().put("priority",contextPriority)			
				annot.getFeatures().put("numWords",contextWords)
				annot.getFeatures().put("contextString",gate.Utils.stringFor(doc,context))  
				annot.getFeatures().put("contextStart",gate.Utils.start(context))			
				annot.getFeatures().put("contextEnd",gate.Utils.end(context))
				annot.getFeatures().put("annotStart",gate.Utils.start(annot))			
				annot.getFeatures().put("annotEnd",gate.Utils.end(annot)) 
			}			
		}
		if(annot.getFeatures().get("contextStart") == null){
			annot.getFeatures().put("contextString","No suitable context found")
		}
	}
}
