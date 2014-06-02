myFile = new File("C:\\Documents and Settings\\rjackson1\\Desktop\\outputs\\OCS_ML_app\\for_upload_all_docs_all_classtypes_big.txt")
i = 0
successfulUploads = 0
myFile.eachLine{ line ->
    //skip header
    if(i!=0){
        elements = line.split("\t")
        if(elements.size()>8){
            document=elements[5] 
            type=elements[1]
            id=Integer.parseInt(elements[11])
            start=Long.parseLong(elements[12])
            end=Long.parseLong(elements[13])
            text=elements[5]
            probability=elements[6]
            feature=elements[7]
            corrections1=elements[22]
            corrections2=elements[23]
            corrections3=elements[24]            
           
            docnameelements=document.split("_")
           // docname=docnameelements[0]+docnameelements[1]
            docs.each{ doc ->
                fixeddocname = document
                if(fixeddocname.equals(doc.getFeatures().get("CN_Doc_ID"))){
                    AnnotationSet key = doc.getAnnotations("Key")
                    
                    key.add(id,start,end,"OCS",['allClasses':corrections1,'nrAndUncertain':corrections2,'nrVsRest':corrections3].toFeatureMap())
                    successfulUploads++
                    println("Upload " + i + " was successful - 337 expected")
                }
            }
        } else {
            println("Bad line: " + line)
        }
    }
i++
 
}

println(successfulUploads + " of " + (i-1) +  " were successful")