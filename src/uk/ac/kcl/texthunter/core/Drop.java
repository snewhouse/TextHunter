package uk.ac.kcl.texthunter.core;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author rjackson1
 */
public class Drop {
    private Object dataToCommit;
    private boolean empty;
    private boolean finalBatch;

    public Drop() {
        this.empty = true;
        this.finalBatch = false;
    }

    public synchronized Object take() {
        // Wait until dataToCommit is
        // available.
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
            empty = true;
        notifyAll();
        return dataToCommit;
    }

    public boolean isFinalBatch() {
        return finalBatch;
    }

    public synchronized void put(Object message ) {
        // Wait until dataToCommit has
        // been retrieved.
        while (!empty) {
            try { 
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = false;
        // Store dataToCommit.
        this.dataToCommit = message;
        notifyAll();
    }
    
    public synchronized void setFinalBatch(boolean finalBatch) {
        // Wait until dataToCommit has
        // been retrieved.
        while (!empty) {
            try { 
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = false;
        // Store dataToCommit.
        this.finalBatch = finalBatch;
        //empty arrayList
        this.dataToCommit = null;
        notifyAll();
    }    
}