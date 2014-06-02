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
import java.util.Comparator;
import java.util.List;
/**
 *
 * @author rjackson1
 */
public class HexTextComparator implements Comparator<Annotation>{
    private List<Annotation> inputAL;
    
    HexTextComparator(ArrayList<Annotation>  inputAL){
        this.inputAL = inputAL;
    }
    @Override
    public int compare(Annotation a, Annotation b){
        int aPos = Integer.parseInt(a.getFeatures().get("priority").toString());
        int bPos = Integer.parseInt(b.getFeatures().get("priority").toString());        
        
        return aPos < bPos ? -1 : aPos == bPos ? 0 : 1;
    }
    
}
