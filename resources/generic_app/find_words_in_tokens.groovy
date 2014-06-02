//get default set
inputAS = doc.getAnnotations()
originalAnnos = inputAS.get("Token")



//creates this set is if doesn't exist
outputAS  =doc.getAnnotations()
outputAnnos = "Word"    



for (anno in originalAnnos){
	if(anno.getFeatures().get("kind").equals("word")){
		fm = Factory.newFeatureMap()
		fm.putAll(anno.getFeatures())
		outputAS.add(anno.start(), anno.end(), outputAnnos, fm)
	}  
}        


