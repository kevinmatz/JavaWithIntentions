package com.kevinmatz.jwi.visitorelements;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.kevinmatz.jwi.ReferenceNeedingContextChecking;
import com.kevinmatz.jwi.SymbolTableEntry;


public class ClassDefinition extends AbstractBaseElement {
	 
    /* TODO Matz: make fields private and add accessors */
    
    public Set<ReferenceNeedingContextChecking> intentionsImplemented = new HashSet<ReferenceNeedingContextChecking>();

    public ClassDefinition(String name) {
        super(name);
    }

    public boolean checkNode(Map<String, SymbolTableEntry> symbolTable) {
        
        Iterator<ReferenceNeedingContextChecking> it = intentionsImplemented.iterator();
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
                // TODO Matz: Check type of entry to see if it matches expected (i.e. a non-abstract intention)
                ref.referenceIsOK = true;
            }
        }
        
        return true;
    }

}
