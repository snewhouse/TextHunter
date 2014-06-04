/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.kcl.texthunter.core;

/**
 *
 * @author rjackson1
 */
class MLModelMakerBusyException extends Exception {

    public MLModelMakerBusyException(String message) {
        super(message);
    }

    public MLModelMakerBusyException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
}
