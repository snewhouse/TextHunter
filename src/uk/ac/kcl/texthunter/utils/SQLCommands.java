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


package uk.ac.kcl.texthunter.utils;

/**
 *
 * @author rjackson1
 */
public class SQLCommands {

    private String random;
    private String brcJoin;
    private String currentTimeAsString;
    private String targetTableName;
    private String topLimit1;
    private String bottomLimit1;
    private String topLimit2;
    private String bottomLimit2;
    private String nolock;
    private String applicationTableName;

    private static SQLCommands instance = new SQLCommands();
    private SQLCommands(){}
    public static SQLCommands getInstance() {
        return instance;
    }    
    
    public void setApplicationTableName(String applicationTableName){
        this.applicationTableName = applicationTableName;
    }
    public void configSQLCommands(String dbType, String targetTableName) {
        this.targetTableName = targetTableName;
        this.brcJoin = "  join " + targetTableName + "_DOCUMENTS t3 on t1.CN_DOC_ID= t3.CN_DOC_ID \n";
        switch (dbType) {
            case "sqlserver":
                this.random = "NEWID()";                
                this.currentTimeAsString = "CONVERT(VARCHAR, CURRENT_TIMESTAMP, 13)";
                this.topLimit1 = " TOP 5000";
                this.topLimit2 = " TOP 50000";
                this.bottomLimit1 = "";
                this.bottomLimit2 = "";
                this.nolock = "WITH (NOLOCK)";
                break;
            case "derby":
                this.random = "RANDOM()";                
                this.currentTimeAsString = "CAST (CURRENT_TIMESTAMP AS VARCHAR(100))";
                this.topLimit1 = "";
                this.topLimit2 = "";
                this.bottomLimit1 = "FETCH FIRST 5000 ROWS ONLY";
                this.bottomLimit2 = "FETCH FIRST 50000 ROWS ONLY";
                this.nolock = "";
                break;
        }
    }

    public String getInstancesForActiveLearningAnnotationSQL() {
        String returnString = "SELECT " + topLimit1 + " t1.*"
                + "      ,t3.TextContent \n"
                + "  FROM " + targetTableName + " t1 join " + targetTableName + "_TEMPAL t2 on t1.id = t2.id\n"
                + brcJoin
                + " where t1.id  in (	\n"
                + "	select " + topLimit1 + " MIN(id) from (\n"
                + "		select " + topLimit2 + " *  from " + targetTableName + "\n"
                + "		where keyObservation1 is  null  and mlObservation1 is not null \n"
                + "		order by prob asc \n"
                + bottomLimit2
                + "	)	t4\n"
                + "	group by contextString 				\n"
                + bottomLimit2
                + " ) and  t1.mlObservation1 is not null and t1.GOLDSTANDARD is null \n"
                + "  \norder by t1.prob asc  \n"
                + bottomLimit1;
        return returnString;
    }

    public String getGoldStandardForAnnotationSQL() {
        String returnString = "SELECT " + topLimit1 + " t1.*"
                + "      ,t3.TextContent\n"
                + "  FROM " + targetTableName + " t1 \n"
                + brcJoin
                + "  where GOLDSTANDARD is null and keyObservation1 is null"
                + "  \norder by " + random + "\n"
                + bottomLimit1;
        return returnString;
    }
    
    
    public String getInstancesForIAA(){
        String returnString = "SELECT  t1.* , t3.TextContent\n"
                + " FROM " + targetTableName + " t1 "
                + brcJoin
                + " where  t1.KEYOBSERVATION1 IS NOT NULL \n"
                + " \norder by t1.id ";     
        return returnString;        
    }
    public String getInstancesForModelSeedAnnotationSQLDerby() {
        String returnString = "SELECT  t1.* , t3.TextContent\n"
                + " FROM " + targetTableName + " t1 "
                + brcJoin
                + " where  t1.id not in (\n"
                + " select  id  from " + targetTableName + " \n"
                + " where  GOLDSTANDARD is not null\n"
                + ")  \norder by " + random +"\n"
                + bottomLimit1;        
        return returnString;
    }
    
    
    
//    old version too slow with derby
    public String getInstancesForModelSeedAnnotationSQLMSSQL() {
        String returnString = "SELECT  t1.* , t3.TextContent\n"
                + " FROM " + targetTableName + " t1 "
                + brcJoin
                + " where t1.id  in (	\n"
                + "	select " + topLimit1 + " MIN(id) from (\n"
                + "		select " + topLimit2 + " id , contextString  from " + targetTableName + "\n"
                + "		where keyObservation1 is  null \n"
                + "		order by " + random + " \n"
                + bottomLimit2
                + "	)	t4\n"
                + "	group by contextString 				\n"
                + bottomLimit1
                + " ) and t1.contextString not in (\n"
                + " select  contextString  from " + targetTableName + " \n"
                + " where  GOLDSTANDARD is not null\n"
                + ")  \norder by " + random;
        return returnString;
    }

    public String getGoldStandardForCorpusCreation() {
        String returnString = "SELECT  t1.*"
                + "      ,t3.TextContent\n"
                + "  FROM " + targetTableName + " t1 \n"
                + brcJoin
                + "  where GOLDSTANDARD like 'gold'  and keyObservation1 is not null and keyObservation1 not like 'form' "
                + "  \norder by " + random;
        return returnString;
    }
    
    //not used. this prevents identical contexts being reused, e.g. if keywords appear twice in the same context string
    //theoretically, this could overestimate results. not used because of performance issues in Java DB
    public String getModelTrainingDataForCorpusCreationStrint() {
        String returnString = "SELECT  t1.* , t3.TextContent\n"
                + " FROM " + targetTableName + " t1 "
                + brcJoin
                + " where t1.id  in (	\n"
                + "	select  MIN(id) from (\n"
                + "		select  *  from " + targetTableName + "\n"
                + "		where keyObservation1 is  not null and (GOLDSTANDARD  like 'seed' or GOLDSTANDARD like 'al') \n"
                + "	)	t4\n"
                + "	group by contextString"
                + ")  \norder by " + random;
        return returnString;
    }
public String getModelTrainingDataForCorpusCreation() {
        String returnString = "SELECT  t1.* , t3.TextContent\n"
                + " FROM " + targetTableName + " t1 "
                + brcJoin
                + "where keyObservation1 is  not null and (GOLDSTANDARD  like 'seed' or GOLDSTANDARD like 'al') \n"
                + "  \norder by " + random;
        return returnString;
    }
    public String getAllAnnotatedInstancesForReview() {
        String returnString = "SELECT  t1.*"
                + "      ,t3.TextContent\n"
                + "  FROM " + targetTableName + " t1 \n"
                + brcJoin
                + "  WHERE t1.keyObservation1 is not null"
                + "  \norder by t1.id";
        return returnString;
    }

    public String getAllInstancesForFinalModelTraining() {
        String returnString = "SELECT  t1.* \n"
                + " FROM " + targetTableName + " t1 "
                + brcJoin
                + " where t1.id  in (	\n"
                + "	select  MIN(id) from (\n"
                + "		select  *  from " + targetTableName + "\n"
                + "		where keyObservation1 is  not null and (GOLDSTANDARD  like 'seed' or GOLDSTANDARD like 'al') \n"
                + "	)	t4\n"
                + "	group by contextString"
                + ") ";
        return returnString;
    }

    public String getAllInstancesForModelApplication() {
        String returnString = "SELECT  t1.*  \n"
                + "  FROM " + applicationTableName + " t1  \n"
                + nolock;
        return returnString;
    }

    public String getInstancesForXMLOutput() {
        String returnString = "SELECT  t1.*\n"
                + "      ,t3.TextContent\n"
                + "  FROM " + targetTableName + " t1 \n"
                + brcJoin
                + "  where keyObservation1 is not null"
                + "  \norder by t1.cn_doc_id, t1.src_col ";     
        return returnString;
    }

    public String createFullDocsTable() {
        String returnString = "CREATE TABLE " + targetTableName + "_DOCUMENTS" + " (\n"
                + "CN_DOC_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) , \n"
                + "FILENAME LONG VARCHAR, \n"
                + "TEXTCONTENT CLOB )\n ";                        
        return returnString;
    }
    
    public String createFullDocsTableIndex(){
        String returnString = "CREATE INDEX " 
                + targetTableName +"_DOCUMENTS_ID_INDEX ON " 
                + targetTableName + "_DOCUMENTS (CN_DOC_ID)";
        return returnString;
    }
        
    
    
    
    public String createAnnotationsTable() {
        String returnString = "CREATE TABLE " + targetTableName + " (\n"
                + "ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) , \n"
                + "BrcId INTEGER ,\n"
                + "CN_Doc_ID INTEGER ,\n"
                + "Document_Date LONG VARCHAR ,\n"
                + "date_modified LONG VARCHAR,\n"
                + "src_table VARCHAR(30) ,\n"
                + "src_col VARCHAR(30) ,\n"
                + "annotation_start INTEGER ,\n"
                + "annotation_end INTEGER ,\n"
                + "KEYPRIORITY1 LONG VARCHAR,\n"            
                + "KEYPRIORITY2 LONG VARCHAR,\n"                 
                + "MLPRIORITY LONG VARCHAR,\n"
                + "length_of_time LONG VARCHAR,\n"
                + "unit_of_time LONG VARCHAR,\n"
                + "comments LONG VARCHAR,\n"
                + "contextEnd LONG VARCHAR,\n"
                + "annotStart LONG VARCHAR,\n"
                + "prob LONG VARCHAR,\n"
                + "mlObservation1  VARCHAR(30),\n"
                + "Experiencer LONG VARCHAR,\n"
                + "annotEnd LONG VARCHAR,\n"
                + "mlObservation2  VARCHAR(30),\n"
                + "keyObservation1  VARCHAR(30),\n"
                + "keyObservation2  VARCHAR(30),\n"
                + "GOLDSTANDARD VARCHAR(30),\n"
                + " \"MATCH\" LONG VARCHAR,\n"
                + "numWords LONG VARCHAR,\n"
                + "Temporality LONG VARCHAR,\n"
                + "contextString VARCHAR(32672),\n"
                + "Directionality LONG VARCHAR,\n"
                + "contextStart LONG VARCHAR, \n"
                + "UPDATETIME LONG VARCHAR  ,"
                + "FOREIGN KEY ( CN_DOC_ID ) REFERENCES " 
                + targetTableName 
                + "_DOCUMENTS(CN_DOC_ID) )\n";
        return returnString;
    }

    public String createAnnotationTableIDIndex(){
        String returnString = "CREATE INDEX " 
                + targetTableName 
                + "_ID_INDEX ON " 
                + targetTableName 
                + " (ID)";
        return returnString;
    }    

    public String createAnnotationTableDoc_IDIndex(){
        String returnString = "CREATE INDEX " 
                + targetTableName 
                + "_CN_DOC_ID_INDEX ON " 
                + targetTableName 
                + " (CN_DOC_ID)";
        return returnString;
    }      
    
    public String createAnnotationTableDContextStringIndex(){
        String returnString = "CREATE INDEX " 
                + targetTableName 
                + "_CONTEXTSTRING_INDEX ON " 
                + targetTableName 
                + " (CONTEXTSTRING)";
        return returnString;
    }          
    public String getRandom() {
        return random;
    }

    public String getBrcJoin() {
        return brcJoin;
    }

    public String getCurrentTimeAsString() {
        return currentTimeAsString;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public String getResultsForCSV() {
        String returnString =
                  "         Select     id\n"
                + "      ,  BrcId\n"
                + "      ,  CN_Doc_ID\n"
                + "      ,  src_table\n"
                + "      ,  src_col\n"
                + "      ,  length_of_time\n"
                + "      ,  unit_of_time\n"
                + "      ,  Directionality      \n"
                + "      ,  Temporality    \n"
                + "      ,  Experiencer    \n"
                + "      ,  \"MATCH\"       \n"
                + "      ,  KEYpriority1         \n"
                + "      ,  KEYpriority2         \n"                
                + "      ,  keyObservation1  \n"
                + "      ,  keyObservation2          \n"
                + "      ,  comments\n"
                + "      ,  GOLDSTANDARD   \n"
                + "      ,  UPDATETIME         \n"
                + "      ,  annotStart\n"
                + "      ,  annotEnd    \n"
                + "      ,  mlObservation1      \n"
                + "      ,  mlObservation2        \n"
                + "      ,  prob\n"
                + "	 ,  MLpriority1 FROM " 
                + targetTableName 
                + " \n"
                + "       Where mlObservation1 is not null ";
        return returnString;
    }

    public String isMultiClass() {
        String returnString =
                "SELECT DISTINCT keyObservation1 \n"
                + " FROM " + targetTableName + " \n"
                + "       Where keyObservation1 is not null ";
        return returnString;
    }
    
    public String getModelSummary(){
        String returnString = 
                "SELECT GOLDSTANDARD, keyObservation1, COUNT(*)\n" +
                "  FROM " + targetTableName + " \n" + 
                "  group by GOLDSTANDARD, keyObservation1";
        return returnString;
    }    
}
