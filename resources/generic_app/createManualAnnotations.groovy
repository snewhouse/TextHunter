for (doc in docs) {

    //get default set
    inputAS = doc.getAnnotations("Key")
    originalAnnos = inputAS.get("Circumstantial")
    
    //creates this set is if doesn't exist
    outputAS  =doc.getAnnotations("Key")
    outputAnnos = "ManualAnnotation"    
    
    
  
    for (anno in originalAnnos){
            fm = anno.getFeatures().toFeatureMap().clone()
            if(fm.get("observation").equalsIgnoreCase("1")){
                fm.put("observation", "positive")
            }else if (fm.get("observation").equalsIgnoreCase("2")){
                fm.put("observation", "negative")            
            }else{
                fm.put("observation", "unknown")                
            }


            
            outputAS.add(anno.start(), anno.end(), outputAnnos, fm)
  
        }        
}     

