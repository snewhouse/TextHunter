/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.kcl.texthunter.core;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author rjackson1
 */
    public class ProbBean implements Comparable<ProbBean> {
        Map<String,Float> map;
        int modelID;
        ProbBean (){
            this.map = new TreeMap();            
        }
        
        ProbBean(ProbBean bean){
            this.map = bean.getMap();
            this.modelID = bean.getModelID();
        }

        ProbBean(int modelID,Map<String, Float> map) {
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
        public int compareTo(ProbBean o)
        {
             return(modelID - o.modelID);
        }


    
            
            
    }