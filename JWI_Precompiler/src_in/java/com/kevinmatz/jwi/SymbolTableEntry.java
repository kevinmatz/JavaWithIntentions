package com.kevinmatz.jwi;

import com.kevinmatz.jwi.visitorelements.AbstractBaseElement;


public class SymbolTableEntry {

    /* TODO Matz: make fields private and add accessors */

    // public String file;
    public AbstractBaseElement element;
    
    public SymbolTableEntry(AbstractBaseElement element) {
        this.element = element;
    }
    
}
