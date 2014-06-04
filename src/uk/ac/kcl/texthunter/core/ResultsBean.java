/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.kcl.texthunter.core;

import gate.Annotation;
import java.util.ArrayList;

/**
 *
 * @author rjackson1
 */
public class ResultsBean {
    
    ArrayList<Annotation> annotations;
    String fileContents;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    String filename;
    
    ResultsBean (){
        this.annotations = new ArrayList();
    }

    public ArrayList<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(ArrayList<Annotation> annotations) {
        this.annotations = annotations;
    }
    public void putAnnotation(Annotation annotation){
        this.annotations.add(annotation);
    }

    
    public String getFileContents() {
        return fileContents;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }
    
}