package com.kevinmatz.jwi;

import java.util.HashMap;
import java.util.Map;

public class SymbolTableManager {

    private static SymbolTableManager instance;
    
    private Map<String, SymbolTableEntry> symbolTable = new HashMap<String, SymbolTableEntry>();

    /**
     * Private constructor to prevent instantiation by outside parties (singleton pattern).
     */
    private SymbolTableManager() {
    }

	public static SymbolTableManager getInstance() {
        if (instance == null) {
		    instance = new SymbolTableManager();
		}
        return instance;
    }	 

	public Map<String, SymbolTableEntry> getSymbolTable() {
		return symbolTable;
	}

}
