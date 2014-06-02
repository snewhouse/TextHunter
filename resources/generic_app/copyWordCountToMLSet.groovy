inputAS = doc.getAnnotations()
inputAS2 = doc.getAnnotations("ML")


List<Annotation> dList = new ArrayList<Annotation>(inputAS.get("Context"));
Collections.sort(dList, new OffsetComparator());

List<Annotation> MLList = new ArrayList<Annotation>(inputAS2.get("Context"));
Collections.sort(MLList, new OffsetComparator());




for (Annotation annI in dList) {
  
  for (Annotation annJ in MLList) {
    
    if (annJ.getStartNode().getOffset() == (annI.getStartNode().getOffset())
        && annJ.getEndNode().getOffset() == (annI.getEndNode().getOffset()) ) {
		
		FeatureMap fm = annI.getFeatures().toFeatureMap().clone() 
		FeatureMap fm2 = annJ.getFeatures()
		
		for (Map.Entry<String, String> entry : fm.entrySet())
		{
			fm2.put(entry.getKey(),entry.getValue());
		}

        annJ.setFeatures(fm2)
		
		
      break;
    }
  }
}

