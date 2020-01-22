package com.kevinmatz.jwi.visitorelements;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.kevinmatz.jwi.ReferenceNeedingContextChecking;
import com.kevinmatz.jwi.SymbolTableEntry;


public abstract class GeneralIntentionDefinition extends AbstractBaseElement {

    /* TODO Matz: make fields private and add accessors */

    public Set<ReferenceNeedingContextChecking> extendedIntentions = new HashSet<ReferenceNeedingContextChecking>();
    
    public Set<String> nonAbstractFieldNames = null;

    public GeneralIntentionDefinition(String name) {
        super(name);
    }

    public boolean checkNode(Map<String, SymbolTableEntry> symbolTable) {
        
        Iterator<ReferenceNeedingContextChecking> it = extendedIntentions.iterator();
        while (it.hasNext()) {
            ReferenceNeedingContextChecking ref = it.next();

            ref.checkedYet = true;
            
            SymbolTableEntry matchingEntry = symbolTable.get(ref.targetElementName);
                       
            if (matchingEntry == null) {
                ref.referenceIsOK = false;
                
                System.out.println(ref.filenameWhereReferenceOccurs + ":" + ref.lineNumberWhereReferenceOccurs +
                        ": JWI error: referenced intention \"" + ref.targetElementName + "\" not found");  
                        
                return false;
            } else {
                // TODO Matz: Check type of entry to see if it matches expected (i.e. some kind of intention)
                ref.referenceIsOK = true;
            }
        }
        
        return true;
    }

}
