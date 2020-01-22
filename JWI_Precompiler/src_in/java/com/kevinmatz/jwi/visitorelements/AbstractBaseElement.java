package com.kevinmatz.jwi.visitorelements;

import java.util.Map;

import com.kevinmatz.jwi.SymbolTableEntry;


public abstract class AbstractBaseElement implements ContextCheckerVisitorInterface {

    /* TODO Matz: make fields private and add accessors */

    public String name = null;

    public AbstractBaseElement(String name) {
        this.name = name;
    }

    public abstract boolean checkNode(Map<String, SymbolTableEntry> symbolTable);
}
