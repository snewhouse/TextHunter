


// get default set
inputAS = doc.getAnnotations("TestKey")
originalAnnos = inputAS.get("Context")

// creates this set is if doesn't exist
outputAS  =doc.getAnnotations("ML")
outputAnnos = "ContextKey"    



for (anno in originalAnnos){
		fm = anno.getFeatures().toFeatureMap().clone()
		outputAS.add(anno.start(), anno.end(), outputAnnos, fm)
	}        
   




// to be used for ML TRAINING mode
//AnnotationSet keySet = doc.getAnnotations("TestKey");
AnnotationSet inputAS = doc.getAnnotations("ML")
inputAS.get("Context").each{ context ->

	mlfm = context.getFeatures().toFeatureMap().clone() 
	Utils.getContainedAnnotations(inputAS, context, "ContextKey").inDocumentOrder().each { abs ->
        keyfm = abs.getFeatures().toFeatureMap().clone()
		mlfm.put("keyObservation",keyfm.get("observation"))
		context.setFeatures(mlfm)

	}
  

}  




