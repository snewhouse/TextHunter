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

import gate.Controller;
import gate.CorpusController;
import gate.LanguageAnalyser;
import gate.ProcessingResource;
import gate.creole.AnalyserRunningStrategy;
import gate.creole.ConditionalController;
import gate.creole.ResourceInstantiationException;
import gate.creole.RunningStrategy;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author rjackson1
 */
public class TextHunterMLCCWrapper {
    Controller app;
    TextHunterMLCCWrapper (Controller app){
        this.app = app;        
    } 
    
    
        public void changeFeatureSelection(Parameter param){
                    //////
            
            ConditionalController pipeline = (ConditionalController) app;
            Collection<RunningStrategy> oldStrategies = pipeline.getRunningStrategies();
            Collection<RunningStrategy> newStrategies = new ArrayList();
            
            
            for (RunningStrategy strategy : oldStrategies){
                String currentPR = strategy.getPR().getName();
                switch (currentPR) {
                    case "ALL_copyContextConcepts":
                    if(param.isRUN_NEGEX()){
                        LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                        newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    }else{
                        LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                        newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));                                                
                    }
                    break;                                        
                    case "ALL_SySTR_GazetteerPM.def":
                        if(param.isRUN_NEGEX()){
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                        }else{
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));                                                
                        }
                        break;
                    case "ALL_CaTIES_CaseInsensitiveGazetteer.def":
                        if(param.isRUN_NEGEX()){
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                        }else{
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));                                                
                        }
                        break;
                    case "ALL_preContextFeatureTagger":
                        if(param.isRUN_NEGEX()){
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                        }else{
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));                                                
                        }
                        break;
                    case "ALL_gate_contextFeatureTagger_PR_BRC_Version":
                        if(param.isRUN_NEGEX()){
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                        }else{
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));                                                
                        }
                        break;
                    case "ALL_SySTR_Gazetteer.def":
                        if(param.isRUN_NEGEX()){
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                        }else{
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));                                                
                        }
                        break;    
                    case "ALL_removeStopWords":
                        if(param.isRemoveStopWords()){
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                        }else{
                            LanguageAnalyser la = (LanguageAnalyser)strategy.getPR();
                            newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));                                                
                        }
                        break;                        
                    default: 
                        newStrategies.add(strategy);   
                        break;
                }                             
            }
            pipeline.setRunningStrategies(newStrategies);
    }    

    public CorpusController getApp() {
        return (CorpusController)app;
    }

    public void setApp(CorpusController app) {
        this.app = app;
    }
        
    public void reinitialisePRs() throws ResourceInstantiationException{
        ArrayList<ProcessingResource> AppPRsList = new ArrayList(app.getPRs());

        for (int i = 0; i <=AppPRsList.size()-1; i++) {
            if(AppPRsList.get(i).getName().toString().equals("ALL_learningBRC")){
                AppPRsList.get(i).reInit();
            }               
        }         
    }    
    
    
    public void setTrainingMode(String mode) throws ResourceInstantiationException{
        ConditionalController pipeline = (ConditionalController) app;
        Collection<RunningStrategy> oldStrategies = pipeline.getRunningStrategies();
        Collection<RunningStrategy> newStrategies = new ArrayList();
        
        
        
        
        if(mode.equalsIgnoreCase("TRAINING")){
            ArrayList<ProcessingResource> AppPRsList = new ArrayList(app.getPRs());
            for (int i = 0; i <=AppPRsList.size()-1; i++) {           
                if(AppPRsList.get(i).getName().toString().equals("ALL_learningBRC")){
                    AppPRsList.get(i).setParameterValue("learningMode","TRAINING");                    
                }                    
            }                        
            for (RunningStrategy strategy : oldStrategies){
                String currentPR = strategy.getPR().getName();
                LanguageAnalyser la;
                switch(currentPR){
                case "TRAIN_Document_reset":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;  
                case "TRAIN_AST_KEY_TO_DEFAULT":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;              
                case "TEST_AST_KEY_TO_DEFAULT":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                      
                case "TRAIN_context_training":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;         
                case "TRAIN_MoveFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;    
                case "TRAIN_Select_Preferred_Annot":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                                                                
                case "TEST_context_test":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                       
                case "TEST_retainBestProb":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                       
                case "TEST_copyContextFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;           
                case "TEST_firstcleanup":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                     
                case "TEST_MoveKeytoDefault":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;               
                case "TEST_MoveMLtoDefault":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                     
                case "TEST_MoveFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                     
                case "TEST_MoveDefaultToTestKey":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                
                case "TEST_finalcleanup":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                                          
                default: 
                    newStrategies.add(strategy);   
                    break;            

                }
            }
        }else if(mode.equalsIgnoreCase("APPLICATION_POS_ONLY")){
            ArrayList<ProcessingResource> AppPRsList = new ArrayList(app.getPRs());
            for (int i = 0; i <=AppPRsList.size()-1; i++) {           
                if(AppPRsList.get(i).getName().toString().equals("ALL_learningBRC")){
                    AppPRsList.get(i).setParameterValue("learningMode","APPLICATION");
                }                    
            }                        
            for (RunningStrategy strategy : oldStrategies){
                String currentPR = strategy.getPR().getName();
                LanguageAnalyser la;
                switch(currentPR){
                case "TRAIN_Document_reset":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;  
                case "TRAIN_AST_KEY_TO_DEFAULT":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;         
                case "TEST_AST_KEY_TO_DEFAULT":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                      
                case "TRAIN_context_training":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;         
                case "TRAIN_MoveFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;    
                case "TRAIN_Select_Preferred_Annot":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                        
                case "TEST_context_test":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                       
                case "TEST_retainBestProb":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                       
                case "TEST_copyContextFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;           
                case "TEST_firstcleanup":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                     
                case "TEST_MoveKeytoDefault":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;               
                case "TEST_MoveMLtoDefault":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                     
                case "TEST_MoveFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                     
                case "TEST_MoveDefaultToTestKey":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                
                case "TEST_finalcleanup":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                                          
                default: 
                    newStrategies.add(strategy);   
                    break;            

                }         
            }
        }else if(mode.equalsIgnoreCase("APPLICATION_ALL_CLASSES")){
            ArrayList<ProcessingResource> AppPRsList = new ArrayList(app.getPRs());
            for (int i = 0; i <=AppPRsList.size()-1; i++) {           
                if(AppPRsList.get(i).getName().toString().equals("ALL_learningBRC")){
                    AppPRsList.get(i).setParameterValue("learningMode","APPLICATION");
                }                    
            }                        
            for (RunningStrategy strategy : oldStrategies){
                String currentPR = strategy.getPR().getName();
                LanguageAnalyser la;
                switch(currentPR){
                case "TRAIN_Document_reset":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;  
                case "TRAIN_AST_KEY_TO_DEFAULT":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;              
                case "TRAIN_context_training":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;         
                case "TRAIN_MoveFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;    
                case "TRAIN_Select_Preferred_Annot":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                        
                case "TEST_context_test":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                       
                case "TEST_retainBestProb":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                       
                case "TEST_copyContextFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;           
                case "TEST_firstcleanup":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                     
                case "TEST_MoveKeytoDefault":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;               
                case "TEST_MoveMLtoDefault":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                     
                case "TEST_MoveFeatures":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                     
                case "TEST_MoveDefaultToTestKey":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_ALWAYS,null,null));
                    break;                
                case "TEST_finalcleanup":
                    la = (LanguageAnalyser)strategy.getPR();
                    newStrategies.add(new AnalyserRunningStrategy(la,RunningStrategy.RUN_NEVER,null,null));
                    break;                                          
                default: 
                    newStrategies.add(strategy);   
                    break;           

                }
            }               
        }        
        pipeline.setRunningStrategies(newStrategies);
    }
}
