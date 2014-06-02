
for (doc in docs) {

    //get default set
    inputAS = doc.getAnnotations("Key")
    originalAnnos = inputAS.get("Context")
    
    //creates this set is if doesn't exist
    outputAS  =doc.getAnnotations("Key")
    outputAnnos = "Context"    
    
    
  
    for (anno in originalAnnos){
            fm = anno.getFeatures().toFeatureMap().clone()
            
            
        //replace unwanted annots with somehting new    
        if(fm.get("observation").equals("0")||fm.get("observation").equals("1")){
            fm.put("observation", "5")    
            outputAS.add(anno.start(), anno.end(), outputAnnos, fm)
            inputAS.remove(anno)  
 
        }  else{
            fm.put("observation", "6")    
            outputAS.add(anno.start(), anno.end(), outputAnnos, fm)
            inputAS.remove(anno)          
        
        
        }
        // else if(fm.get("feature").equals("apathetic")){
            // fm.put("observation", "apathetic")        
            // fm.remove("feature")               
            
            
            

        }        
}     
