///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package uk.ac.kcl.texthunter.utils;
//
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//
///**
// *
// * @author rjackson1
// */
//public class TaskTimer {
//    long startTime;
//    long finishTime;
//    Integer processCount;
//    Integer rowCount;
//    
//    TaskTimer(Integer rowCount){
//        startTime = 0;   
//        finishTime = 0;
//        this.rowCount = rowCount;
//    }
//    
//    public long startClock(){
//        startTime = System.currentTimeMillis( );        
//        return startTime;        
//    }    
//    
//    
//    
//    public long stopClock(){
//        finishTime = startTime - System.currentTimeMillis( );        
//        return finishTime;        
//    }            
//        
//    public void updateProcessCount(Integer processCount){
//        this.processCount = processCount;
//    }
//    
//    public void someting() {
//    
//        int next = processCount;      
//        if(System.currentTimeMillis( ) >= startTime + 5000){
//
//            DecimalFormat twoDForm = new DecimalFormat(".#");
//
////            int docCountThisPeriod = next - prev;                                
////            double docsPerMin = (double)docCountThisPeriod * 12;                        
////            docsPerMin = Double.valueOf(twoDForm.format(docsPerMin));
////            docsPerMinAR.add(docsPerMin);
////            double outputDocsPerMinAR = 0;
////            for(Double number :docsPerMinAR ){
////                outputDocsPerMinAR = outputDocsPerMinAR + number;
////            }
////            outputDocsPerMinAR = outputDocsPerMinAR/docsPerMinAR.size();
////
////
////            double minsTillCompletion = (rowCount - next)/ outputDocsPerMinAR;                                           
////            SimpleDateFormat df = new SimpleDateFormat("dd-MM 'at' HH:mm");
////            Calendar cal = Calendar.getInstance();
////            cal.getTime();
////            cal.add(Calendar.MINUTE, (int)minsTillCompletion);
////            String newTime = df.format(cal.getTime());       
////
////            DecimalFormat myFormat = new DecimalFormat("00.0");
////            String myDoubleString = myFormat.format(outputDocsPerMinAR);
////
////            infoTextPane.setText(next + " instances of " + rowCount + " annotated\n"
////                    + myDoubleString + " docs/min\n"
////                    + "estimated completion time " + newTime);
////            svmProgressBar.setValue(next);
////            svmProgressBar.repaint();                      
////            current = next;          
////            startT = System.currentTimeMillis( );
////            prev = next;    
//    
//    }
//    
//    
//    
////}
//
//
//
//                    
