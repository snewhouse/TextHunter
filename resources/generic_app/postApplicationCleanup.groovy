

    //get default set
    inputAS = doc.getAnnotations("ML")
    originalAnnos = inputAS.get("Context")
        for (anno in originalAnnos){
                fm = anno.getFeatures().toFeatureMap().clone()
                
                
            //replace unwanted annots with somehting new    
            if(fm.get("observation").equals("0")){
			
                inputAS.remove(anno) 
            }              
            
        }        
		
    inputAS = doc.getAnnotations("Key")
    originalAnnos = inputAS.get("Context")
        for (anno in originalAnnos){
                fm = anno.getFeatures().toFeatureMap().clone()
                
                
            //replace unwanted annots with somehting new    
            if(fm.get("observation").equals("0")){
			
                inputAS.remove(anno) 
            }              
            
        }    		
    




/* 	mevoce defaults
    get default set
    // inputAS = doc.getAnnotations("")
    // originalAnnos = inputAS.get("Sentence")
        // for (anno in originalAnnos){
                // fm = anno.getFeatures().toFeatureMap().clone()
                
                
            replace unwanted annots with somehting new    
            // if(fm.get("OCS").equals("unspecified")){
                // inputAS.remove(anno) 
            // }              
            
        // }        
 

 
     get default set
    // inputAS = doc.getAnnotations("")
    // originalAnnos = inputAS.get("Token")
        // for (anno in originalAnnos){
                // inputAS.remove(anno)            
        // }    


     get default set
    // inputAS = doc.getAnnotations("")
    // originalAnnos = inputAS.get("SpaceToken")
        // for (anno in originalAnnos){
                // inputAS.remove(anno)            
        // }     		

     get default set
    // inputAS = doc.getAnnotations("")
    // originalAnnos = inputAS.get("Split")
        // for (anno in originalAnnos){
                // inputAS.remove(anno)            
        // }    
		
     get default set
    // inputAS = doc.getAnnotations("")
    // originalAnnos = inputAS.get("Sentence")
        // for (anno in originalAnnos){
                // inputAS.remove(anno)            
        // }   	 */	