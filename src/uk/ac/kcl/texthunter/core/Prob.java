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

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author rjackson1
 */
    public class Prob implements Comparable<Prob> {
        Map<String,Float> map;
        int modelID;
        Prob (){
            this.map = new TreeMap();            
        }
        
        Prob(Prob bean){
            this.map = bean.getMap();
            this.modelID = bean.getModelID();
        }

        Prob(int modelID,Map<String, Float> map) {
            this.map = map;
            this.modelID = modelID;
        }
        


        public Map<String, Float> getMap() {
            return map;
        }


  

        public int getModelID() {
            return modelID;
        }

        public void setModelID(int modelID) {
            this.modelID = modelID;
        }
        
        @Override
        public int compareTo(Prob o)
        {
             return(modelID - o.modelID);
        }


    
            
            
    }