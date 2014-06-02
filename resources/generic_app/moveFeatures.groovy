// to be used for ML TRAINING mode
AnnotationSet keySet = doc.getAnnotations("");

inputAS.get(scriptParams.contextAnnot).each{ context ->
    hasFeature = false;

  Utils.getContainedAnnotations(keySet, context, "ManualAnnotation").inDocumentOrder().each { abs ->
        hasFeature = true
 
		FeatureMap fm = abs.getFeatures().toFeatureMap().clone() 
		FeatureMap fm2 = context.getFeatures()
		
		for (Map.Entry<String, String> entry : fm.entrySet())
		{
			fm2.put(entry.getKey(),entry.getValue());
		}

        context.setFeatures(fm2)
		

  }
  


}  