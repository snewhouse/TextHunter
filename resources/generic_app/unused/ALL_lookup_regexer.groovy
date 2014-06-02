import java.util.regex.Matcher;
import java.util.regex.Pattern;
ArrayList<String> ar = new ArrayList();
ar.add("cannabis");
ar.add("skunk");

for (doc in docs){
    outputAS = doc.getAnnotations()
    for(String patternString : ar){
        Pattern pattern = Pattern.compile(patternString)
        Matcher matcher = pattern.matcher(doc.getContent().toString());
        while(matcher.find()) {
          FeatureMap fm = Factory.newFeatureMap();
          fm.put("cleanKeyword", matcher.group(0));
          outputAS.add(matcher.start(),
                       matcher.end(),
                       "PatternHit", fm);
        }
    }
    
    patternAS = doc.getAnnotations()["PatternHit"]
    for(annot in patternAS){
        coveringTokensAS = gate.Utils.getOverlappingAnnotations(outputAS,annot,"Token" )
        coveringTokensList = new ArrayList();
        coveringTokensList.addAll(gate.Utils.inDocumentOrder(coveringTokensAS))
        
          outputAS.add(coveringTokensList.get(0).start(),
                       coveringTokensList.get(coveringTokensList.size()-1).end(),
                       "Lookup2", annot.getFeatures());
    }

}

