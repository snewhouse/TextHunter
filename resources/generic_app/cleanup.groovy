//get default set
inputAS1 = doc.getAnnotations("TestKey")
originalAnnos1 = inputAS1.get("Context")

//creates this set is if doesn't exist
inputAS2  =doc.getAnnotations("ML")
originalAnnos2 = inputAS2.get("Context")



for (anno in originalAnnos1){
        fm = anno.getFeatures().toFeatureMap().clone()
    if(!fm.get("observation").equals("positive")){
        inputAS1.remove(anno)  
    }  
}

for (anno in originalAnnos2){
        fm = anno.getFeatures().toFeatureMap().clone()
    if(!fm.get("observation").equals("positive")){
        inputAS2.remove(anno)  
    }  
}          
    
