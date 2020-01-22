package com.kevinmatz.jwi.visitorelements;

import java.util.Set;


public class IntentionDefinition_Abstract extends GeneralIntentionDefinition {

    /* TODO Matz: make fields private and add accessors */
    
    public Set<String> abstractFieldNames;

    public IntentionDefinition_Abstract(String name) {
        super(name);
    }
    
}
