package com.kevinmatz.jwi.visitorelements;

import java.util.Map;

import com.kevinmatz.jwi.SymbolTableEntry;


public interface ContextCheckerVisitorInterface {
    public boolean checkNode(Map<String, SymbolTableEntry> symbolTable);
}
