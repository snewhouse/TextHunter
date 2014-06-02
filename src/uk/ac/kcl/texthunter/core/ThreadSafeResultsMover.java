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


public class ThreadSafeResultsMover {
    private Object dataToCommit;
    private boolean empty;
    private boolean finalBatch;

    public ThreadSafeResultsMover() {
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