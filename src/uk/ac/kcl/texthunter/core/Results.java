//    Text Hunter: User friendly concept extraction from text
//
//    Copyright (C) 2014  Richard Jackson (richgjackson@yahoo.co.uk)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.


package uk.ac.kcl.texthunter.core;

import gate.Annotation;
import java.util.ArrayList;

/**
 *
 * @author rjackson1
 */
public class Results {
    
    ArrayList<Annotation> annotations;
    String fileContents;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    String filename;
    
    Results (){
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