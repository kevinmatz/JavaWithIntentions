package com.kevinmatz.jwi;


public class ReferenceNeedingContextChecking /* <T> */ {
    // TODO: Getters and setters
    
    public String targetElementName = null;
    public boolean checkedYet = false;
    public boolean referenceIsOK = false;
    
    public int lineNumberWhereReferenceOccurs = 0;
    public String filenameWhereReferenceOccurs = null;
}
