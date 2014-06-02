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

/**
 *
 * @author rjackson1
 */
public class GlobalParameters {
    public static final int DBCOMMITBATCHSIZE = 1000;
    public static final int MAXMICROCORPUSSIZE = 100;    
    public static final int MAXIMUMDBCOMMITERPOOLSIZE = 3; 
    public static final int MODELSTOCARRYFORWARD = 1;     
    public static final String OUTPUTTSV = "output.tsv";
    public static final String KEYANNOTSETNAME = "Key";
    public static final String TESTKEYANNOTSETNAME = "TestKey";    
    public static final String KEYANNOTSETTYPE = "ManualAnnotation";    
    public static final String MLANNOTSETNAME = "ML";
    public static final String MLFEATURENAME = "observation";
    public static final String TARGETKEYWORD = "TargetKeyword";
    public static final String CONTEXT = "Context";
    public static final boolean  EMBEDDEDMODE = true;
    public static final boolean debugOn = false;
    public static final String BRCID = "BrcId";
    public static final String CN_DOC_ID = "CN_Doc_ID";
    public static final String DOCUMENT_DATE = "Document_Date";
    public static final String SRC_TABLE = "src_table";
    public static final String SRC_COL = "src_col";
    public static final String ID = "id";
    public static final String OBSERVATION2 = "observation2";    
    public static final String PRIORITY = "priority"; 
    public static final String CONTEXTSTRING = "contextString";
    public static final String ANNOTSTART = "annotStart";
    public static final String ANNOTEND = "annotEnd";
    public static final String CONTEXTSTART = "contextStart";
    public static final String CONTEXTEND = "contextEnd";
    public static final String KEYOBSERVATION1 = "keyObservation1";
    public static final String KEYOBSERVATION2 = "keyObservation2";
    public static final String KEYPRIORITY1 = "KEYPRIORITY1";    
    public static final String TEXTCONTENT = "textContent";   
    public static final int minGSCorpusSize = 50;   
    public static final int minModelCorpusSize = 50;   
    
    
    private GlobalParameters(){}
    
    
}
