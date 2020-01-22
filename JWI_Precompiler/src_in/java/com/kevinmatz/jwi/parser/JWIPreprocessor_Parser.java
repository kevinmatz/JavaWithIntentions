// $ANTLR 3.2 Sep 23, 2009 12:02:23 JWIPreprocessor_Parser.g 2011-03-03 03:53:52

    package com.kevinmatz.jwi.parser;
    
    import java.util.Collection;
    import java.util.Iterator;
    import java.util.Map;
    import java.util.Set;
    import java.util.HashMap;
    import java.util.HashSet;
    import java.util.Stack;

    import com.kevinmatz.jwi.ReferenceNeedingContextChecking;
    import com.kevinmatz.jwi.SymbolTableEntry;
    import com.kevinmatz.jwi.SymbolTableManager;
    import com.kevinmatz.jwi.visitorelements.ClassDefinition;
    import com.kevinmatz.jwi.visitorelements.GeneralIntentionDefinition;
    import com.kevinmatz.jwi.visitorelements.IntentionDefinition_NonAbstract;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;
import java.util.HashMap;
/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *          Again, JLS doesn't seem to allow this, but java.lang.Class has an example of
 *          of this construct.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *      
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more 
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or 
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse 
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several 
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and 
 *          normalInterfaceDeclaration rather than classDeclaration and 
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.  
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation, 
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source 
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java 
 *      letter-or-digit is a character for which the method 
 *      Character.isJavaIdentifierPart(int) returns true."
 */
public class JWIPreprocessor_Parser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "INTENTION", "IMPLEMENTSINTENTION", "REQUIREMENT", "IMPLEMENTSREQUIREMENT", "GOAL", "IMPLEMENTSGOAL", "DESCRIPTION", "CLASSREFERENCE", "INTERFACEREFERENCE", "TEXTFIELD", "ABSTRACT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", "CHAR", "CLASS", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", "EXTENDS", "FALSE", "FINAL", "FINALLY", "FLOAT", "FOR", "IF", "IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", "NEW", "NULL", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", "STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", "THROWS", "TRANSIENT", "TRUE", "TRY", "WHILE", "VOID", "VOLATILE", "SEMICOLON", "COLON", "COMMA", "LEFTBRACE", "RIGHTBRACE", "LEFTPARENTHESIS", "RIGHTPARENTHESIS", "LEFTSQUAREBRACKET", "RIGHTSQUAREBRACKET", "LESSTHAN", "GREATERTHAN", "ELLIPSIS", "DOT", "ATSIGN", "TILDE", "QUESTIONMARK", "PLUSEQUALS", "PLUSPLUS", "PLUS", "MINUSEQUALS", "MINUSMINUS", "MINUS", "ASTERISKEQUALS", "ASTERISK", "SLASHEQUALS", "SLASH", "PERCENTEQUALS", "PERCENT", "CARETEQUALS", "CARET", "EXCLAMATIONMARKEQUALS", "EXCLAMATIONMARK", "EQUALITY_EQUALS", "ASSIGNMENT_EQUALS", "LOGICAL_AND", "BITWISE_AND_EQUALS", "BITWISE_AND", "LOGICAL_OR", "BITWISE_OR_EQUALS", "PIPE", "HexDigit", "IntegerTypeSuffix", "HexLiteral", "DecimalLiteral", "OctalLiteral", "Exponent", "FloatTypeSuffix", "FloatingPointLiteral", "EscapeSequence", "CharacterLiteral", "StringLiteral", "UnicodeEscape", "OctalEscape", "ENUM", "ASSERT", "Letter", "JavaIDDigit", "Identifier", "FREETEXTINBRACES", "WS", "INLINEINTENTIONCLOSINGTAGTOKEN", "INLINEINTENTIONOPENINGTAGTOKEN", "COMMENT", "LINE_COMMENT", "VOLATIVE"
    };
    public static final int LEFTPARENTHESIS=68;
    public static final int PACKAGE=43;
    public static final int WHILE=60;
    public static final int FloatTypeSuffix=109;
    public static final int OctalLiteral=107;
    public static final int CASE=18;
    public static final int NEW=41;
    public static final int CHAR=20;
    public static final int DO=24;
    public static final int MINUSMINUS=83;
    public static final int EOF=-1;
    public static final int IMPLEMENTSREQUIREMENT=7;
    public static final int LOGICAL_AND=97;
    public static final int BREAK=16;
    public static final int FREETEXTINBRACES=121;
    public static final int Identifier=120;
    public static final int FINAL=29;
    public static final int IMPORT=35;
    public static final int LEFTBRACE=66;
    public static final int DESCRIPTION=10;
    public static final int CARET=92;
    public static final int THIS=54;
    public static final int RETURN=47;
    public static final int DOUBLE=25;
    public static final int VOID=61;
    public static final int ATSIGN=76;
    public static final int SUPER=51;
    public static final int COMMENT=125;
    public static final int GREATERTHAN=73;
    public static final int MINUSEQUALS=82;
    public static final int LINE_COMMENT=126;
    public static final int CARETEQUALS=91;
    public static final int IntegerTypeSuffix=104;
    public static final int STATIC=49;
    public static final int PRIVATE=44;
    public static final int SWITCH=52;
    public static final int NULL=42;
    public static final int STRICTFP=50;
    public static final int ELSE=26;
    public static final int ELLIPSIS=74;
    public static final int NATIVE=40;
    public static final int THROWS=56;
    public static final int INT=37;
    public static final int SEMICOLON=63;
    public static final int SLASHEQUALS=87;
    public static final int ASSERT=117;
    public static final int TRY=59;
    public static final int WS=122;
    public static final int FloatingPointLiteral=110;
    public static final int INLINEINTENTIONOPENINGTAGTOKEN=124;
    public static final int JavaIDDigit=119;
    public static final int CLASSREFERENCE=11;
    public static final int ASTERISKEQUALS=85;
    public static final int PERCENTEQUALS=89;
    public static final int CATCH=19;
    public static final int FALSE=28;
    public static final int BITWISE_OR_EQUALS=101;
    public static final int Letter=118;
    public static final int EscapeSequence=111;
    public static final int THROW=55;
    public static final int BITWISE_AND=99;
    public static final int PROTECTED=45;
    public static final int CLASS=21;
    public static final int INTERFACEREFERENCE=12;
    public static final int IMPLEMENTSINTENTION=5;
    public static final int CharacterLiteral=112;
    public static final int PLUSPLUS=80;
    public static final int Exponent=108;
    public static final int FOR=32;
    public static final int FLOAT=31;
    public static final int ABSTRACT=14;
    public static final int HexDigit=103;
    public static final int REQUIREMENT=6;
    public static final int ASTERISK=86;
    public static final int IF=33;
    public static final int BOOLEAN=15;
    public static final int SYNCHRONIZED=53;
    public static final int SLASH=88;
    public static final int IMPLEMENTS=34;
    public static final int CONTINUE=22;
    public static final int COMMA=65;
    public static final int TRANSIENT=57;
    public static final int QUESTIONMARK=78;
    public static final int PLUSEQUALS=79;
    public static final int LOGICAL_OR=100;
    public static final int TILDE=77;
    public static final int RIGHTPARENTHESIS=69;
    public static final int PLUS=81;
    public static final int RIGHTBRACE=67;
    public static final int PIPE=102;
    public static final int EQUALITY_EQUALS=95;
    public static final int TEXTFIELD=13;
    public static final int DOT=75;
    public static final int INTENTION=4;
    public static final int BITWISE_AND_EQUALS=98;
    public static final int INLINEINTENTIONCLOSINGTAGTOKEN=123;
    public static final int VOLATIVE=127;
    public static final int GOAL=8;
    public static final int RIGHTSQUAREBRACKET=71;
    public static final int HexLiteral=105;
    public static final int LESSTHAN=72;
    public static final int BYTE=17;
    public static final int PERCENT=90;
    public static final int VOLATILE=62;
    public static final int DEFAULT=23;
    public static final int SHORT=48;
    public static final int INSTANCEOF=36;
    public static final int MINUS=84;
    public static final int DecimalLiteral=106;
    public static final int TRUE=58;
    public static final int COLON=64;
    public static final int StringLiteral=113;
    public static final int LEFTSQUAREBRACKET=70;
    public static final int ENUM=116;
    public static final int FINALLY=30;
    public static final int UnicodeEscape=114;
    public static final int INTERFACE=38;
    public static final int EXCLAMATIONMARK=94;
    public static final int LONG=39;
    public static final int PUBLIC=46;
    public static final int EXTENDS=27;
    public static final int OctalEscape=115;
    public static final int EXCLAMATIONMARKEQUALS=93;
    public static final int IMPLEMENTSGOAL=9;
    public static final int ASSIGNMENT_EQUALS=96;

    // delegates
    // delegators


        public JWIPreprocessor_Parser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public JWIPreprocessor_Parser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
            this.state.ruleMemo = new HashMap[444+1];
             
             
        }
        
    protected StringTemplateGroup templateLib =
      new StringTemplateGroup("JWIPreprocessor_ParserTemplates", AngleBracketTemplateLexer.class);

    public void setTemplateLib(StringTemplateGroup templateLib) {
      this.templateLib = templateLib;
    }
    public StringTemplateGroup getTemplateLib() {
      return templateLib;
    }
    /** allows convenient multi-value initialization:
     *  "new STAttrMap().put(...).put(...)"
     */
    public static class STAttrMap extends HashMap {
      public STAttrMap put(String attrName, Object value) {
        super.put(attrName, value);
        return this;
      }
      public STAttrMap put(String attrName, int value) {
        super.put(attrName, new Integer(value));
        return this;
      }
    }

    public String[] getTokenNames() { return JWIPreprocessor_Parser.tokenNames; }
    public String getGrammarFileName() { return "JWIPreprocessor_Parser.g"; }



    /** This symbol table is only for a single input file at this time. TODO Matz: Extend for multiple files */
    // public static Map<String, SymbolTableEntry> symbolTable = new HashMap<String, SymbolTableEntry>();



    protected static class jwiCompilationUnit_scope {
        String filename;
    }
    protected Stack jwiCompilationUnit_stack = new Stack();

    public static class jwiCompilationUnit_return extends ParserRuleReturnScope {
        public String packageName;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "jwiCompilationUnit"
    // JWIPreprocessor_Parser.g:227:1: jwiCompilationUnit[String filenameBeingProcessed] returns [String packageName] : packageName1= compilationUnit ;
    public final JWIPreprocessor_Parser.jwiCompilationUnit_return jwiCompilationUnit(String filenameBeingProcessed) throws RecognitionException {
        jwiCompilationUnit_stack.push(new jwiCompilationUnit_scope());
        JWIPreprocessor_Parser.jwiCompilationUnit_return retval = new JWIPreprocessor_Parser.jwiCompilationUnit_return();
        retval.start = input.LT(1);
        int jwiCompilationUnit_StartIndex = input.index();
        JWIPreprocessor_Parser.compilationUnit_return packageName1 = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return retval; }
            // JWIPreprocessor_Parser.g:231:1: (packageName1= compilationUnit )
            // JWIPreprocessor_Parser.g:231:5: packageName1= compilationUnit
            {
            if ( state.backtracking==0 ) {

                      // System.out.println("CHECKPOINT AAA");
                      ((jwiCompilationUnit_scope)jwiCompilationUnit_stack.peek()).filename = filenameBeingProcessed;
                  
            }
            pushFollow(FOLLOW_compilationUnit_in_jwiCompilationUnit156);
            packageName1=compilationUnit();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               retval.packageName = (packageName1!=null?packageName1.packageName:null); 
            }

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 1, jwiCompilationUnit_StartIndex); }
            jwiCompilationUnit_stack.pop();
        }
        return retval;
    }
    // $ANTLR end "jwiCompilationUnit"

    public static class compilationUnit_return extends ParserRuleReturnScope {
        public String packageName;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "compilationUnit"
    // JWIPreprocessor_Parser.g:243:1: compilationUnit returns [String packageName] : ( annotations (packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* ) | ( (packageName2= packageDeclaration )? ) ( importDeclaration )* ( standaloneIntentionDeclaration | typeDeclaration )* );
    public final JWIPreprocessor_Parser.compilationUnit_return compilationUnit() throws RecognitionException {
        JWIPreprocessor_Parser.compilationUnit_return retval = new JWIPreprocessor_Parser.compilationUnit_return();
        retval.start = input.LT(1);
        int compilationUnit_StartIndex = input.index();
        JWIPreprocessor_Parser.packageDeclaration_return packageName1 = null;

        JWIPreprocessor_Parser.packageDeclaration_return packageName2 = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return retval; }
            // JWIPreprocessor_Parser.g:244:5: ( annotations (packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* ) | ( (packageName2= packageDeclaration )? ) ( importDeclaration )* ( standaloneIntentionDeclaration | typeDeclaration )* )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // JWIPreprocessor_Parser.g:244:9: annotations (packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* )
                    {
                    pushFollow(FOLLOW_annotations_in_compilationUnit190);
                    annotations();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:245:9: (packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==PACKAGE) ) {
                        alt4=1;
                    }
                    else if ( (LA4_0==ABSTRACT||LA4_0==CLASS||LA4_0==FINAL||LA4_0==INTERFACE||(LA4_0>=PRIVATE && LA4_0<=PUBLIC)||(LA4_0>=STATIC && LA4_0<=STRICTFP)||LA4_0==ATSIGN||LA4_0==ENUM) ) {
                        alt4=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:245:13: packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )*
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit206);
                            packageName1=packageDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               retval.packageName = (packageName1!=null?packageName1.packageName:null); 
                            }
                            // JWIPreprocessor_Parser.g:245:91: ( importDeclaration )*
                            loop1:
                            do {
                                int alt1=2;
                                int LA1_0 = input.LA(1);

                                if ( (LA1_0==IMPORT) ) {
                                    alt1=1;
                                }


                                switch (alt1) {
                            	case 1 :
                            	    // JWIPreprocessor_Parser.g:0:0: importDeclaration
                            	    {
                            	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit210);
                            	    importDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return retval;

                            	    }
                            	    break;

                            	default :
                            	    break loop1;
                                }
                            } while (true);

                            // JWIPreprocessor_Parser.g:245:110: ( typeDeclaration )*
                            loop2:
                            do {
                                int alt2=2;
                                int LA2_0 = input.LA(1);

                                if ( (LA2_0==ABSTRACT||LA2_0==CLASS||LA2_0==FINAL||LA2_0==INTERFACE||(LA2_0>=PRIVATE && LA2_0<=PUBLIC)||(LA2_0>=STATIC && LA2_0<=STRICTFP)||LA2_0==SEMICOLON||LA2_0==ATSIGN||LA2_0==ENUM) ) {
                                    alt2=1;
                                }


                                switch (alt2) {
                            	case 1 :
                            	    // JWIPreprocessor_Parser.g:0:0: typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit213);
                            	    typeDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return retval;

                            	    }
                            	    break;

                            	default :
                            	    break loop2;
                                }
                            } while (true);


                            }
                            break;
                        case 2 :
                            // JWIPreprocessor_Parser.g:246:13: classOrInterfaceDeclaration ( typeDeclaration )*
                            {
                            pushFollow(FOLLOW_classOrInterfaceDeclaration_in_compilationUnit228);
                            classOrInterfaceDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;
                            // JWIPreprocessor_Parser.g:246:41: ( typeDeclaration )*
                            loop3:
                            do {
                                int alt3=2;
                                int LA3_0 = input.LA(1);

                                if ( (LA3_0==ABSTRACT||LA3_0==CLASS||LA3_0==FINAL||LA3_0==INTERFACE||(LA3_0>=PRIVATE && LA3_0<=PUBLIC)||(LA3_0>=STATIC && LA3_0<=STRICTFP)||LA3_0==SEMICOLON||LA3_0==ATSIGN||LA3_0==ENUM) ) {
                                    alt3=1;
                                }


                                switch (alt3) {
                            	case 1 :
                            	    // JWIPreprocessor_Parser.g:0:0: typeDeclaration
                            	    {
                            	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit230);
                            	    typeDeclaration();

                            	    state._fsp--;
                            	    if (state.failed) return retval;

                            	    }
                            	    break;

                            	default :
                            	    break loop3;
                                }
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:248:9: ( (packageName2= packageDeclaration )? ) ( importDeclaration )* ( standaloneIntentionDeclaration | typeDeclaration )*
                    {
                    // JWIPreprocessor_Parser.g:248:9: ( (packageName2= packageDeclaration )? )
                    // JWIPreprocessor_Parser.g:248:10: (packageName2= packageDeclaration )?
                    {
                    // JWIPreprocessor_Parser.g:248:22: (packageName2= packageDeclaration )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==PACKAGE) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: packageName2= packageDeclaration
                            {
                            pushFollow(FOLLOW_packageDeclaration_in_compilationUnit254);
                            packageName2=packageDeclaration();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                       retval.packageName = (packageName2!=null?packageName2.packageName:null); 
                    }

                    }

                    // JWIPreprocessor_Parser.g:248:91: ( importDeclaration )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==IMPORT) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:0:0: importDeclaration
                    	    {
                    	    pushFollow(FOLLOW_importDeclaration_in_compilationUnit261);
                    	    importDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    // JWIPreprocessor_Parser.g:248:110: ( standaloneIntentionDeclaration | typeDeclaration )*
                    loop7:
                    do {
                        int alt7=3;
                        switch ( input.LA(1) ) {
                        case ABSTRACT:
                            {
                            int LA7_2 = input.LA(2);

                            if ( (LA7_2==ABSTRACT||LA7_2==CLASS||LA7_2==FINAL||LA7_2==INTERFACE||(LA7_2>=PRIVATE && LA7_2<=PUBLIC)||(LA7_2>=STATIC && LA7_2<=STRICTFP)||LA7_2==ATSIGN||LA7_2==ENUM) ) {
                                alt7=2;
                            }
                            else if ( (LA7_2==INTENTION) ) {
                                alt7=1;
                            }


                            }
                            break;
                        case INTENTION:
                            {
                            alt7=1;
                            }
                            break;
                        case CLASS:
                        case FINAL:
                        case INTERFACE:
                        case PRIVATE:
                        case PROTECTED:
                        case PUBLIC:
                        case STATIC:
                        case STRICTFP:
                        case SEMICOLON:
                        case ATSIGN:
                        case ENUM:
                            {
                            alt7=2;
                            }
                            break;

                        }

                        switch (alt7) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:248:111: standaloneIntentionDeclaration
                    	    {
                    	    pushFollow(FOLLOW_standaloneIntentionDeclaration_in_compilationUnit265);
                    	    standaloneIntentionDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;
                    	case 2 :
                    	    // JWIPreprocessor_Parser.g:248:144: typeDeclaration
                    	    {
                    	    pushFollow(FOLLOW_typeDeclaration_in_compilationUnit269);
                    	    typeDeclaration();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 2, compilationUnit_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "compilationUnit"

    public static class packageDeclaration_return extends ParserRuleReturnScope {
        public String packageName;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "packageDeclaration"
    // JWIPreprocessor_Parser.g:251:1: packageDeclaration returns [String packageName] : PACKAGE packageName1= qualifiedName SEMICOLON ;
    public final JWIPreprocessor_Parser.packageDeclaration_return packageDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.packageDeclaration_return retval = new JWIPreprocessor_Parser.packageDeclaration_return();
        retval.start = input.LT(1);
        int packageDeclaration_StartIndex = input.index();
        JWIPreprocessor_Parser.qualifiedName_return packageName1 = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return retval; }
            // JWIPreprocessor_Parser.g:252:5: ( PACKAGE packageName1= qualifiedName SEMICOLON )
            // JWIPreprocessor_Parser.g:252:9: PACKAGE packageName1= qualifiedName SEMICOLON
            {
            match(input,PACKAGE,FOLLOW_PACKAGE_in_packageDeclaration294); if (state.failed) return retval;
            pushFollow(FOLLOW_qualifiedName_in_packageDeclaration298);
            packageName1=qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_packageDeclaration300); if (state.failed) return retval;
            if ( state.backtracking==0 ) {

                          retval.packageName = (packageName1!=null?input.toString(packageName1.start,packageName1.stop):null);
                      
            }

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 3, packageDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "packageDeclaration"

    public static class importDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "importDeclaration"
    // JWIPreprocessor_Parser.g:258:1: importDeclaration : IMPORT ( STATIC )? qualifiedName ( DOT ASTERISK )? SEMICOLON ;
    public final JWIPreprocessor_Parser.importDeclaration_return importDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.importDeclaration_return retval = new JWIPreprocessor_Parser.importDeclaration_return();
        retval.start = input.LT(1);
        int importDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return retval; }
            // JWIPreprocessor_Parser.g:259:5: ( IMPORT ( STATIC )? qualifiedName ( DOT ASTERISK )? SEMICOLON )
            // JWIPreprocessor_Parser.g:259:9: IMPORT ( STATIC )? qualifiedName ( DOT ASTERISK )? SEMICOLON
            {
            match(input,IMPORT,FOLLOW_IMPORT_in_importDeclaration333); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:259:16: ( STATIC )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==STATIC) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: STATIC
                    {
                    match(input,STATIC,FOLLOW_STATIC_in_importDeclaration335); if (state.failed) return retval;

                    }
                    break;

            }

            pushFollow(FOLLOW_qualifiedName_in_importDeclaration338);
            qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:259:38: ( DOT ASTERISK )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==DOT) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // JWIPreprocessor_Parser.g:259:39: DOT ASTERISK
                    {
                    match(input,DOT,FOLLOW_DOT_in_importDeclaration341); if (state.failed) return retval;
                    match(input,ASTERISK,FOLLOW_ASTERISK_in_importDeclaration343); if (state.failed) return retval;

                    }
                    break;

            }

            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_importDeclaration347); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 4, importDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "importDeclaration"

    protected static class standaloneIntentionDeclaration_scope {
        IntentionDefinition_NonAbstract defn;
    }
    protected Stack standaloneIntentionDeclaration_stack = new Stack();

    public static class standaloneIntentionDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "standaloneIntentionDeclaration"
    // JWIPreprocessor_Parser.g:263:1: standaloneIntentionDeclaration : ( ABSTRACT )? INTENTION name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody -> template(content=$text) \"/* <content> */\";
    public final JWIPreprocessor_Parser.standaloneIntentionDeclaration_return standaloneIntentionDeclaration() throws RecognitionException {
        standaloneIntentionDeclaration_stack.push(new standaloneIntentionDeclaration_scope());
        JWIPreprocessor_Parser.standaloneIntentionDeclaration_return retval = new JWIPreprocessor_Parser.standaloneIntentionDeclaration_return();
        retval.start = input.LT(1);
        int standaloneIntentionDeclaration_StartIndex = input.index();
        Token name1=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return retval; }
            // JWIPreprocessor_Parser.g:267:5: ( ( ABSTRACT )? INTENTION name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody -> template(content=$text) \"/* <content> */\")
            // JWIPreprocessor_Parser.g:267:9: ( ABSTRACT )? INTENTION name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody
            {
            // JWIPreprocessor_Parser.g:267:9: ( ABSTRACT )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ABSTRACT) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: ABSTRACT
                    {
                    match(input,ABSTRACT,FOLLOW_ABSTRACT_in_standaloneIntentionDeclaration375); if (state.failed) return retval;

                    }
                    break;

            }

            match(input,INTENTION,FOLLOW_INTENTION_in_standaloneIntentionDeclaration379); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_standaloneIntentionDeclaration383); if (state.failed) return retval;
            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT A; (name1!=null?name1.getText():null) == \"" + (name1!=null?name1.getText():null) + "\"");

                          // TODO Matz: Choose either IntentionDefinition_Abstract or IntentionDefinition_NonAbstract depending on the presence of the ABSTRACT modifier
                          
                          // IntentionDefinition_NonAbstract
                          ((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn = new IntentionDefinition_NonAbstract((name1!=null?name1.getText():null));
                          SymbolTableEntry entry = new SymbolTableEntry(((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn);
                          SymbolTableManager.getInstance().getSymbolTable().put((name1!=null?name1.getText():null), entry);
                      
            }
            // JWIPreprocessor_Parser.g:278:9: ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==EXTENDS) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: intentionExtendsClause[$standaloneIntentionDeclaration::defn]
                    {
                    pushFollow(FOLLOW_intentionExtendsClause_in_standaloneIntentionDeclaration403);
                    intentionExtendsClause(((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn);

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            pushFollow(FOLLOW_intentionDeclarationBody_in_standaloneIntentionDeclaration415);
            intentionDeclarationBody();

            state._fsp--;
            if (state.failed) return retval;


            // TEMPLATE REWRITE
            if ( state.backtracking==0 ) {
              // 280:5: -> template(content=$text) \"/* <content> */\"
              {
                  retval.st = new StringTemplate(templateLib, "/* <content> */",
                new STAttrMap().put("content", input.toString(retval.start,input.LT(-1))));
              }

              ((TokenRewriteStream)input).replace(
                ((Token)retval.start).getTokenIndex(),
                input.LT(-1).getTokenIndex(),
                retval.st);
            }
            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 5, standaloneIntentionDeclaration_StartIndex); }
            standaloneIntentionDeclaration_stack.pop();
        }
        return retval;
    }
    // $ANTLR end "standaloneIntentionDeclaration"

    protected static class standaloneRequirementDeclaration_scope {
        IntentionDefinition_NonAbstract defn;
    }
    protected Stack standaloneRequirementDeclaration_stack = new Stack();

    public static class standaloneRequirementDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "standaloneRequirementDeclaration"
    // JWIPreprocessor_Parser.g:284:1: standaloneRequirementDeclaration : ( ABSTRACT )? REQUIREMENT name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody -> template(content=$text) \"/* <content> */\";
    public final JWIPreprocessor_Parser.standaloneRequirementDeclaration_return standaloneRequirementDeclaration() throws RecognitionException {
        standaloneRequirementDeclaration_stack.push(new standaloneRequirementDeclaration_scope());
        JWIPreprocessor_Parser.standaloneRequirementDeclaration_return retval = new JWIPreprocessor_Parser.standaloneRequirementDeclaration_return();
        retval.start = input.LT(1);
        int standaloneRequirementDeclaration_StartIndex = input.index();
        Token name1=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return retval; }
            // JWIPreprocessor_Parser.g:288:5: ( ( ABSTRACT )? REQUIREMENT name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody -> template(content=$text) \"/* <content> */\")
            // JWIPreprocessor_Parser.g:288:9: ( ABSTRACT )? REQUIREMENT name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody
            {
            // JWIPreprocessor_Parser.g:288:9: ( ABSTRACT )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==ABSTRACT) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: ABSTRACT
                    {
                    match(input,ABSTRACT,FOLLOW_ABSTRACT_in_standaloneRequirementDeclaration458); if (state.failed) return retval;

                    }
                    break;

            }

            match(input,REQUIREMENT,FOLLOW_REQUIREMENT_in_standaloneRequirementDeclaration462); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_standaloneRequirementDeclaration466); if (state.failed) return retval;
            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT A; (name1!=null?name1.getText():null) == \"" + (name1!=null?name1.getText():null) + "\"");

                          // TODO Matz: Choose either IntentionDefinition_Abstract or IntentionDefinition_NonAbstract depending on the presence of the ABSTRACT modifier
                          
                          // IntentionDefinition_NonAbstract
                          ((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn = new IntentionDefinition_NonAbstract((name1!=null?name1.getText():null));
                          SymbolTableEntry entry = new SymbolTableEntry(((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn);
                          SymbolTableManager.getInstance().getSymbolTable().put((name1!=null?name1.getText():null), entry);
                      
            }
            // JWIPreprocessor_Parser.g:299:9: ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==EXTENDS) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: intentionExtendsClause[$standaloneIntentionDeclaration::defn]
                    {
                    pushFollow(FOLLOW_intentionExtendsClause_in_standaloneRequirementDeclaration486);
                    intentionExtendsClause(((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn);

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            pushFollow(FOLLOW_intentionDeclarationBody_in_standaloneRequirementDeclaration498);
            intentionDeclarationBody();

            state._fsp--;
            if (state.failed) return retval;


            // TEMPLATE REWRITE
            if ( state.backtracking==0 ) {
              // 301:5: -> template(content=$text) \"/* <content> */\"
              {
                  retval.st = new StringTemplate(templateLib, "/* <content> */",
                new STAttrMap().put("content", input.toString(retval.start,input.LT(-1))));
              }

              ((TokenRewriteStream)input).replace(
                ((Token)retval.start).getTokenIndex(),
                input.LT(-1).getTokenIndex(),
                retval.st);
            }
            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 6, standaloneRequirementDeclaration_StartIndex); }
            standaloneRequirementDeclaration_stack.pop();
        }
        return retval;
    }
    // $ANTLR end "standaloneRequirementDeclaration"

    protected static class standaloneGoalDeclaration_scope {
        IntentionDefinition_NonAbstract defn;
    }
    protected Stack standaloneGoalDeclaration_stack = new Stack();

    public static class standaloneGoalDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "standaloneGoalDeclaration"
    // JWIPreprocessor_Parser.g:305:1: standaloneGoalDeclaration : ( ABSTRACT )? GOAL name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody -> template(content=$text) \"/* <content> */\";
    public final JWIPreprocessor_Parser.standaloneGoalDeclaration_return standaloneGoalDeclaration() throws RecognitionException {
        standaloneGoalDeclaration_stack.push(new standaloneGoalDeclaration_scope());
        JWIPreprocessor_Parser.standaloneGoalDeclaration_return retval = new JWIPreprocessor_Parser.standaloneGoalDeclaration_return();
        retval.start = input.LT(1);
        int standaloneGoalDeclaration_StartIndex = input.index();
        Token name1=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return retval; }
            // JWIPreprocessor_Parser.g:309:5: ( ( ABSTRACT )? GOAL name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody -> template(content=$text) \"/* <content> */\")
            // JWIPreprocessor_Parser.g:309:9: ( ABSTRACT )? GOAL name1= Identifier ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )? intentionDeclarationBody
            {
            // JWIPreprocessor_Parser.g:309:9: ( ABSTRACT )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ABSTRACT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: ABSTRACT
                    {
                    match(input,ABSTRACT,FOLLOW_ABSTRACT_in_standaloneGoalDeclaration541); if (state.failed) return retval;

                    }
                    break;

            }

            match(input,GOAL,FOLLOW_GOAL_in_standaloneGoalDeclaration545); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_standaloneGoalDeclaration549); if (state.failed) return retval;
            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT A; (name1!=null?name1.getText():null) == \"" + (name1!=null?name1.getText():null) + "\"");

                          // TODO Matz: Choose either IntentionDefinition_Abstract or IntentionDefinition_NonAbstract depending on the presence of the ABSTRACT modifier
                          
                          // IntentionDefinition_NonAbstract
                          ((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn = new IntentionDefinition_NonAbstract((name1!=null?name1.getText():null));
                          SymbolTableEntry entry = new SymbolTableEntry(((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn);
                          SymbolTableManager.getInstance().getSymbolTable().put((name1!=null?name1.getText():null), entry);
                      
            }
            // JWIPreprocessor_Parser.g:320:9: ( intentionExtendsClause[$standaloneIntentionDeclaration::defn] )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==EXTENDS) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: intentionExtendsClause[$standaloneIntentionDeclaration::defn]
                    {
                    pushFollow(FOLLOW_intentionExtendsClause_in_standaloneGoalDeclaration569);
                    intentionExtendsClause(((standaloneIntentionDeclaration_scope)standaloneIntentionDeclaration_stack.peek()).defn);

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            pushFollow(FOLLOW_intentionDeclarationBody_in_standaloneGoalDeclaration581);
            intentionDeclarationBody();

            state._fsp--;
            if (state.failed) return retval;


            // TEMPLATE REWRITE
            if ( state.backtracking==0 ) {
              // 322:5: -> template(content=$text) \"/* <content> */\"
              {
                  retval.st = new StringTemplate(templateLib, "/* <content> */",
                new STAttrMap().put("content", input.toString(retval.start,input.LT(-1))));
              }

              ((TokenRewriteStream)input).replace(
                ((Token)retval.start).getTokenIndex(),
                input.LT(-1).getTokenIndex(),
                retval.st);
            }
            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 7, standaloneGoalDeclaration_StartIndex); }
            standaloneGoalDeclaration_stack.pop();
        }
        return retval;
    }
    // $ANTLR end "standaloneGoalDeclaration"

    public static class intentionDeclarationBody_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "intentionDeclarationBody"
    // JWIPreprocessor_Parser.g:326:1: intentionDeclarationBody : LEFTBRACE DESCRIPTION FREETEXTINBRACES ( INTERFACEREFERENCE Identifier SEMICOLON | INTERFACEREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON | CLASSREFERENCE Identifier SEMICOLON | CLASSREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON | TEXTFIELD Identifier ( ASSIGNMENT_EQUALS StringLiteral )? SEMICOLON )* RIGHTBRACE ;
    public final JWIPreprocessor_Parser.intentionDeclarationBody_return intentionDeclarationBody() throws RecognitionException {
        JWIPreprocessor_Parser.intentionDeclarationBody_return retval = new JWIPreprocessor_Parser.intentionDeclarationBody_return();
        retval.start = input.LT(1);
        int intentionDeclarationBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return retval; }
            // JWIPreprocessor_Parser.g:327:5: ( LEFTBRACE DESCRIPTION FREETEXTINBRACES ( INTERFACEREFERENCE Identifier SEMICOLON | INTERFACEREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON | CLASSREFERENCE Identifier SEMICOLON | CLASSREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON | TEXTFIELD Identifier ( ASSIGNMENT_EQUALS StringLiteral )? SEMICOLON )* RIGHTBRACE )
            // JWIPreprocessor_Parser.g:327:9: LEFTBRACE DESCRIPTION FREETEXTINBRACES ( INTERFACEREFERENCE Identifier SEMICOLON | INTERFACEREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON | CLASSREFERENCE Identifier SEMICOLON | CLASSREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON | TEXTFIELD Identifier ( ASSIGNMENT_EQUALS StringLiteral )? SEMICOLON )* RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_intentionDeclarationBody616); if (state.failed) return retval;
            match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_intentionDeclarationBody626); if (state.failed) return retval;
            match(input,FREETEXTINBRACES,FOLLOW_FREETEXTINBRACES_in_intentionDeclarationBody628); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:329:9: ( INTERFACEREFERENCE Identifier SEMICOLON | INTERFACEREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON | CLASSREFERENCE Identifier SEMICOLON | CLASSREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON | TEXTFIELD Identifier ( ASSIGNMENT_EQUALS StringLiteral )? SEMICOLON )*
            loop22:
            do {
                int alt22=6;
                switch ( input.LA(1) ) {
                case INTERFACEREFERENCE:
                    {
                    int LA22_2 = input.LA(2);

                    if ( (LA22_2==Identifier) ) {
                        alt22=1;
                    }
                    else if ( (LA22_2==LEFTSQUAREBRACKET) ) {
                        alt22=2;
                    }


                    }
                    break;
                case CLASSREFERENCE:
                    {
                    int LA22_3 = input.LA(2);

                    if ( (LA22_3==Identifier) ) {
                        alt22=3;
                    }
                    else if ( (LA22_3==LEFTSQUAREBRACKET) ) {
                        alt22=4;
                    }


                    }
                    break;
                case TEXTFIELD:
                    {
                    alt22=5;
                    }
                    break;

                }

                switch (alt22) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:330:13: INTERFACEREFERENCE Identifier SEMICOLON
            	    {
            	    match(input,INTERFACEREFERENCE,FOLLOW_INTERFACEREFERENCE_in_intentionDeclarationBody652); if (state.failed) return retval;
            	    match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody654); if (state.failed) return retval;
            	    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_intentionDeclarationBody656); if (state.failed) return retval;

            	    }
            	    break;
            	case 2 :
            	    // JWIPreprocessor_Parser.g:332:13: INTERFACEREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON
            	    {
            	    match(input,INTERFACEREFERENCE,FOLLOW_INTERFACEREFERENCE_in_intentionDeclarationBody684); if (state.failed) return retval;
            	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_intentionDeclarationBody686); if (state.failed) return retval;
            	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_intentionDeclarationBody688); if (state.failed) return retval;
            	    match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody690); if (state.failed) return retval;
            	    // JWIPreprocessor_Parser.g:332:80: ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE )
            	    // JWIPreprocessor_Parser.g:332:81: ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE
            	    {
            	    match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_intentionDeclarationBody693); if (state.failed) return retval;
            	    match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_intentionDeclarationBody695); if (state.failed) return retval;
            	    // JWIPreprocessor_Parser.g:332:109: ( Identifier ( COMMA Identifier )* )?
            	    int alt18=2;
            	    int LA18_0 = input.LA(1);

            	    if ( (LA18_0==Identifier) ) {
            	        alt18=1;
            	    }
            	    switch (alt18) {
            	        case 1 :
            	            // JWIPreprocessor_Parser.g:332:110: Identifier ( COMMA Identifier )*
            	            {
            	            match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody698); if (state.failed) return retval;
            	            // JWIPreprocessor_Parser.g:332:121: ( COMMA Identifier )*
            	            loop17:
            	            do {
            	                int alt17=2;
            	                int LA17_0 = input.LA(1);

            	                if ( (LA17_0==COMMA) ) {
            	                    alt17=1;
            	                }


            	                switch (alt17) {
            	            	case 1 :
            	            	    // JWIPreprocessor_Parser.g:332:122: COMMA Identifier
            	            	    {
            	            	    match(input,COMMA,FOLLOW_COMMA_in_intentionDeclarationBody701); if (state.failed) return retval;
            	            	    match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody703); if (state.failed) return retval;

            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop17;
            	                }
            	            } while (true);


            	            }
            	            break;

            	    }

            	    match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_intentionDeclarationBody709); if (state.failed) return retval;

            	    }

            	    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_intentionDeclarationBody712); if (state.failed) return retval;

            	    }
            	    break;
            	case 3 :
            	    // JWIPreprocessor_Parser.g:334:13: CLASSREFERENCE Identifier SEMICOLON
            	    {
            	    match(input,CLASSREFERENCE,FOLLOW_CLASSREFERENCE_in_intentionDeclarationBody740); if (state.failed) return retval;
            	    match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody742); if (state.failed) return retval;
            	    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_intentionDeclarationBody744); if (state.failed) return retval;

            	    }
            	    break;
            	case 4 :
            	    // JWIPreprocessor_Parser.g:336:13: CLASSREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE ) SEMICOLON
            	    {
            	    match(input,CLASSREFERENCE,FOLLOW_CLASSREFERENCE_in_intentionDeclarationBody772); if (state.failed) return retval;
            	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_intentionDeclarationBody774); if (state.failed) return retval;
            	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_intentionDeclarationBody776); if (state.failed) return retval;
            	    match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody778); if (state.failed) return retval;
            	    // JWIPreprocessor_Parser.g:336:76: ( ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE )
            	    // JWIPreprocessor_Parser.g:336:77: ASSIGNMENT_EQUALS LEFTBRACE ( Identifier ( COMMA Identifier )* )? RIGHTBRACE
            	    {
            	    match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_intentionDeclarationBody781); if (state.failed) return retval;
            	    match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_intentionDeclarationBody783); if (state.failed) return retval;
            	    // JWIPreprocessor_Parser.g:336:105: ( Identifier ( COMMA Identifier )* )?
            	    int alt20=2;
            	    int LA20_0 = input.LA(1);

            	    if ( (LA20_0==Identifier) ) {
            	        alt20=1;
            	    }
            	    switch (alt20) {
            	        case 1 :
            	            // JWIPreprocessor_Parser.g:336:106: Identifier ( COMMA Identifier )*
            	            {
            	            match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody786); if (state.failed) return retval;
            	            // JWIPreprocessor_Parser.g:336:117: ( COMMA Identifier )*
            	            loop19:
            	            do {
            	                int alt19=2;
            	                int LA19_0 = input.LA(1);

            	                if ( (LA19_0==COMMA) ) {
            	                    alt19=1;
            	                }


            	                switch (alt19) {
            	            	case 1 :
            	            	    // JWIPreprocessor_Parser.g:336:118: COMMA Identifier
            	            	    {
            	            	    match(input,COMMA,FOLLOW_COMMA_in_intentionDeclarationBody789); if (state.failed) return retval;
            	            	    match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody791); if (state.failed) return retval;

            	            	    }
            	            	    break;

            	            	default :
            	            	    break loop19;
            	                }
            	            } while (true);


            	            }
            	            break;

            	    }

            	    match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_intentionDeclarationBody797); if (state.failed) return retval;

            	    }

            	    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_intentionDeclarationBody800); if (state.failed) return retval;

            	    }
            	    break;
            	case 5 :
            	    // JWIPreprocessor_Parser.g:338:13: TEXTFIELD Identifier ( ASSIGNMENT_EQUALS StringLiteral )? SEMICOLON
            	    {
            	    match(input,TEXTFIELD,FOLLOW_TEXTFIELD_in_intentionDeclarationBody828); if (state.failed) return retval;
            	    match(input,Identifier,FOLLOW_Identifier_in_intentionDeclarationBody830); if (state.failed) return retval;
            	    // JWIPreprocessor_Parser.g:338:34: ( ASSIGNMENT_EQUALS StringLiteral )?
            	    int alt21=2;
            	    int LA21_0 = input.LA(1);

            	    if ( (LA21_0==ASSIGNMENT_EQUALS) ) {
            	        alt21=1;
            	    }
            	    switch (alt21) {
            	        case 1 :
            	            // JWIPreprocessor_Parser.g:338:35: ASSIGNMENT_EQUALS StringLiteral
            	            {
            	            match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_intentionDeclarationBody833); if (state.failed) return retval;
            	            match(input,StringLiteral,FOLLOW_StringLiteral_in_intentionDeclarationBody835); if (state.failed) return retval;

            	            }
            	            break;

            	    }

            	    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_intentionDeclarationBody839); if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_intentionDeclarationBody860); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 8, intentionDeclarationBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "intentionDeclarationBody"

    public static class intentionExtendsClause_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "intentionExtendsClause"
    // JWIPreprocessor_Parser.g:350:1: intentionExtendsClause[GeneralIntentionDefinition defn] : EXTENDS name1= Identifier ;
    public final JWIPreprocessor_Parser.intentionExtendsClause_return intentionExtendsClause(GeneralIntentionDefinition defn) throws RecognitionException {
        JWIPreprocessor_Parser.intentionExtendsClause_return retval = new JWIPreprocessor_Parser.intentionExtendsClause_return();
        retval.start = input.LT(1);
        int intentionExtendsClause_StartIndex = input.index();
        Token name1=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return retval; }
            // JWIPreprocessor_Parser.g:351:5: ( EXTENDS name1= Identifier )
            // JWIPreprocessor_Parser.g:351:9: EXTENDS name1= Identifier
            {
            match(input,EXTENDS,FOLLOW_EXTENDS_in_intentionExtendsClause887); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_intentionExtendsClause891); if (state.failed) return retval;
            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT D");

                          ReferenceNeedingContextChecking ref = new ReferenceNeedingContextChecking();
                          ref.targetElementName = (name1!=null?name1.getText():null);
                          ref.lineNumberWhereReferenceOccurs = (name1!=null?name1.getLine():0);
                          ref.filenameWhereReferenceOccurs = ((jwiCompilationUnit_scope)jwiCompilationUnit_stack.peek()).filename;
                  
                          defn.extendedIntentions.add(ref);
                      
            }

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 9, intentionExtendsClause_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "intentionExtendsClause"

    public static class classImplementsIntentionClause_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classImplementsIntentionClause"
    // JWIPreprocessor_Parser.g:364:1: classImplementsIntentionClause[ClassDefinition defn] : IMPLEMENTSINTENTION name1= Identifier ( COMMA name2= Identifier )* -> template(content=$text) \"/* <content> */\";
    public final JWIPreprocessor_Parser.classImplementsIntentionClause_return classImplementsIntentionClause(ClassDefinition defn) throws RecognitionException {
        JWIPreprocessor_Parser.classImplementsIntentionClause_return retval = new JWIPreprocessor_Parser.classImplementsIntentionClause_return();
        retval.start = input.LT(1);
        int classImplementsIntentionClause_StartIndex = input.index();
        Token name1=null;
        Token name2=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return retval; }
            // JWIPreprocessor_Parser.g:365:5: ( IMPLEMENTSINTENTION name1= Identifier ( COMMA name2= Identifier )* -> template(content=$text) \"/* <content> */\")
            // JWIPreprocessor_Parser.g:365:9: IMPLEMENTSINTENTION name1= Identifier ( COMMA name2= Identifier )*
            {
            match(input,IMPLEMENTSINTENTION,FOLLOW_IMPLEMENTSINTENTION_in_classImplementsIntentionClause917); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_classImplementsIntentionClause921); if (state.failed) return retval;
            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT E");
                          
                          ReferenceNeedingContextChecking ref = new ReferenceNeedingContextChecking();
                          ref.targetElementName = (name1!=null?name1.getText():null);
                          ref.lineNumberWhereReferenceOccurs = (name1!=null?name1.getLine():0);
                          ref.filenameWhereReferenceOccurs = ((jwiCompilationUnit_scope)jwiCompilationUnit_stack.peek()).filename;
                          
                          defn.intentionsImplemented.add(ref);
                      
            }
            // JWIPreprocessor_Parser.g:376:9: ( COMMA name2= Identifier )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==COMMA) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:376:10: COMMA name2= Identifier
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_classImplementsIntentionClause942); if (state.failed) return retval;
            	    name2=(Token)match(input,Identifier,FOLLOW_Identifier_in_classImplementsIntentionClause946); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {

            	                      // System.out.println("CHECKPOINT F");

            	                      ReferenceNeedingContextChecking ref2 = new ReferenceNeedingContextChecking();
            	                      ref2.targetElementName = (name2!=null?name2.getText():null);
            	                      ref2.lineNumberWhereReferenceOccurs = (name2!=null?name2.getLine():0);
            	                      ref2.filenameWhereReferenceOccurs = ((jwiCompilationUnit_scope)jwiCompilationUnit_stack.peek()).filename;
            	                  
            	                      defn.intentionsImplemented.add(ref2);
            	                  
            	    }

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);



            // TEMPLATE REWRITE
            if ( state.backtracking==0 ) {
              // 388:5: -> template(content=$text) \"/* <content> */\"
              {
                  retval.st = new StringTemplate(templateLib, "/* <content> */",
                new STAttrMap().put("content", input.toString(retval.start,input.LT(-1))));
              }

              ((TokenRewriteStream)input).replace(
                ((Token)retval.start).getTokenIndex(),
                input.LT(-1).getTokenIndex(),
                retval.st);
            }
            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 10, classImplementsIntentionClause_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classImplementsIntentionClause"

    public static class methodImplementsIntentionClause_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "methodImplementsIntentionClause"
    // JWIPreprocessor_Parser.g:391:1: methodImplementsIntentionClause : IMPLEMENTSINTENTION name1= Identifier ( COMMA name2= Identifier )* -> template(content=$text) \"/* <content> */\";
    public final JWIPreprocessor_Parser.methodImplementsIntentionClause_return methodImplementsIntentionClause() throws RecognitionException {
        JWIPreprocessor_Parser.methodImplementsIntentionClause_return retval = new JWIPreprocessor_Parser.methodImplementsIntentionClause_return();
        retval.start = input.LT(1);
        int methodImplementsIntentionClause_StartIndex = input.index();
        Token name1=null;
        Token name2=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return retval; }
            // JWIPreprocessor_Parser.g:392:5: ( IMPLEMENTSINTENTION name1= Identifier ( COMMA name2= Identifier )* -> template(content=$text) \"/* <content> */\")
            // JWIPreprocessor_Parser.g:392:9: IMPLEMENTSINTENTION name1= Identifier ( COMMA name2= Identifier )*
            {
            match(input,IMPLEMENTSINTENTION,FOLLOW_IMPLEMENTSINTENTION_in_methodImplementsIntentionClause1009); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_methodImplementsIntentionClause1013); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:393:9: ( COMMA name2= Identifier )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==COMMA) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:393:10: COMMA name2= Identifier
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_methodImplementsIntentionClause1024); if (state.failed) return retval;
            	    name2=(Token)match(input,Identifier,FOLLOW_Identifier_in_methodImplementsIntentionClause1028); if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);



            // TEMPLATE REWRITE
            if ( state.backtracking==0 ) {
              // 394:5: -> template(content=$text) \"/* <content> */\"
              {
                  retval.st = new StringTemplate(templateLib, "/* <content> */",
                new STAttrMap().put("content", input.toString(retval.start,input.LT(-1))));
              }

              ((TokenRewriteStream)input).replace(
                ((Token)retval.start).getTokenIndex(),
                input.LT(-1).getTokenIndex(),
                retval.st);
            }
            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 11, methodImplementsIntentionClause_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodImplementsIntentionClause"

    public static class typeDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "typeDeclaration"
    // JWIPreprocessor_Parser.g:397:1: typeDeclaration : ( classOrInterfaceDeclaration | SEMICOLON );
    public final JWIPreprocessor_Parser.typeDeclaration_return typeDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.typeDeclaration_return retval = new JWIPreprocessor_Parser.typeDeclaration_return();
        retval.start = input.LT(1);
        int typeDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return retval; }
            // JWIPreprocessor_Parser.g:398:5: ( classOrInterfaceDeclaration | SEMICOLON )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==ABSTRACT||LA25_0==CLASS||LA25_0==FINAL||LA25_0==INTERFACE||(LA25_0>=PRIVATE && LA25_0<=PUBLIC)||(LA25_0>=STATIC && LA25_0<=STRICTFP)||LA25_0==ATSIGN||LA25_0==ENUM) ) {
                alt25=1;
            }
            else if ( (LA25_0==SEMICOLON) ) {
                alt25=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // JWIPreprocessor_Parser.g:398:9: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration1064);
                    classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:399:9: SEMICOLON
                    {
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_typeDeclaration1074); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 12, typeDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeDeclaration"

    public static class classOrInterfaceDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classOrInterfaceDeclaration"
    // JWIPreprocessor_Parser.g:402:1: classOrInterfaceDeclaration : classOrInterfaceModifiers (classDef= classDeclaration | interfaceDef= interfaceDeclaration ) ;
    public final JWIPreprocessor_Parser.classOrInterfaceDeclaration_return classOrInterfaceDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.classOrInterfaceDeclaration_return retval = new JWIPreprocessor_Parser.classOrInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int classOrInterfaceDeclaration_StartIndex = input.index();
        JWIPreprocessor_Parser.classDeclaration_return classDef = null;

        JWIPreprocessor_Parser.interfaceDeclaration_return interfaceDef = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return retval; }
            // JWIPreprocessor_Parser.g:403:5: ( classOrInterfaceModifiers (classDef= classDeclaration | interfaceDef= interfaceDeclaration ) )
            // JWIPreprocessor_Parser.g:403:9: classOrInterfaceModifiers (classDef= classDeclaration | interfaceDef= interfaceDeclaration )
            {
            pushFollow(FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration1097);
            classOrInterfaceModifiers();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:404:9: (classDef= classDeclaration | interfaceDef= interfaceDeclaration )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==CLASS||LA26_0==ENUM) ) {
                alt26=1;
            }
            else if ( (LA26_0==INTERFACE||LA26_0==ATSIGN) ) {
                alt26=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // JWIPreprocessor_Parser.g:404:10: classDef= classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_classOrInterfaceDeclaration1110);
                    classDef=classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:406:10: interfaceDef= interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration1134);
                    interfaceDef=interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 13, classOrInterfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceDeclaration"

    public static class classOrInterfaceModifiers_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classOrInterfaceModifiers"
    // JWIPreprocessor_Parser.g:410:1: classOrInterfaceModifiers : ( classOrInterfaceModifier )* ;
    public final JWIPreprocessor_Parser.classOrInterfaceModifiers_return classOrInterfaceModifiers() throws RecognitionException {
        JWIPreprocessor_Parser.classOrInterfaceModifiers_return retval = new JWIPreprocessor_Parser.classOrInterfaceModifiers_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifiers_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return retval; }
            // JWIPreprocessor_Parser.g:411:5: ( ( classOrInterfaceModifier )* )
            // JWIPreprocessor_Parser.g:411:9: ( classOrInterfaceModifier )*
            {
            // JWIPreprocessor_Parser.g:411:9: ( classOrInterfaceModifier )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==ATSIGN) ) {
                    int LA27_2 = input.LA(2);

                    if ( (LA27_2==Identifier) ) {
                        alt27=1;
                    }


                }
                else if ( (LA27_0==ABSTRACT||LA27_0==FINAL||(LA27_0>=PRIVATE && LA27_0<=PUBLIC)||(LA27_0>=STATIC && LA27_0<=STRICTFP)) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: classOrInterfaceModifier
            	    {
            	    pushFollow(FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers1171);
            	    classOrInterfaceModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 14, classOrInterfaceModifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceModifiers"

    public static class classOrInterfaceModifier_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classOrInterfaceModifier"
    // JWIPreprocessor_Parser.g:414:1: classOrInterfaceModifier : ( annotation | PUBLIC | PROTECTED | PRIVATE | ABSTRACT | STATIC | FINAL | STRICTFP );
    public final JWIPreprocessor_Parser.classOrInterfaceModifier_return classOrInterfaceModifier() throws RecognitionException {
        JWIPreprocessor_Parser.classOrInterfaceModifier_return retval = new JWIPreprocessor_Parser.classOrInterfaceModifier_return();
        retval.start = input.LT(1);
        int classOrInterfaceModifier_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return retval; }
            // JWIPreprocessor_Parser.g:415:5: ( annotation | PUBLIC | PROTECTED | PRIVATE | ABSTRACT | STATIC | FINAL | STRICTFP )
            int alt28=8;
            switch ( input.LA(1) ) {
            case ATSIGN:
                {
                alt28=1;
                }
                break;
            case PUBLIC:
                {
                alt28=2;
                }
                break;
            case PROTECTED:
                {
                alt28=3;
                }
                break;
            case PRIVATE:
                {
                alt28=4;
                }
                break;
            case ABSTRACT:
                {
                alt28=5;
                }
                break;
            case STATIC:
                {
                alt28=6;
                }
                break;
            case FINAL:
                {
                alt28=7;
                }
                break;
            case STRICTFP:
                {
                alt28=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // JWIPreprocessor_Parser.g:415:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_classOrInterfaceModifier1191);
                    annotation();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:416:9: PUBLIC
                    {
                    match(input,PUBLIC,FOLLOW_PUBLIC_in_classOrInterfaceModifier1204); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:417:9: PROTECTED
                    {
                    match(input,PROTECTED,FOLLOW_PROTECTED_in_classOrInterfaceModifier1221); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:418:9: PRIVATE
                    {
                    match(input,PRIVATE,FOLLOW_PRIVATE_in_classOrInterfaceModifier1235); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:419:9: ABSTRACT
                    {
                    match(input,ABSTRACT,FOLLOW_ABSTRACT_in_classOrInterfaceModifier1251); if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // JWIPreprocessor_Parser.g:420:9: STATIC
                    {
                    match(input,STATIC,FOLLOW_STATIC_in_classOrInterfaceModifier1266); if (state.failed) return retval;

                    }
                    break;
                case 7 :
                    // JWIPreprocessor_Parser.g:421:9: FINAL
                    {
                    match(input,FINAL,FOLLOW_FINAL_in_classOrInterfaceModifier1283); if (state.failed) return retval;

                    }
                    break;
                case 8 :
                    // JWIPreprocessor_Parser.g:422:9: STRICTFP
                    {
                    match(input,STRICTFP,FOLLOW_STRICTFP_in_classOrInterfaceModifier1301); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 15, classOrInterfaceModifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceModifier"

    public static class modifiers_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "modifiers"
    // JWIPreprocessor_Parser.g:425:1: modifiers : ( modifier )* ;
    public final JWIPreprocessor_Parser.modifiers_return modifiers() throws RecognitionException {
        JWIPreprocessor_Parser.modifiers_return retval = new JWIPreprocessor_Parser.modifiers_return();
        retval.start = input.LT(1);
        int modifiers_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return retval; }
            // JWIPreprocessor_Parser.g:426:5: ( ( modifier )* )
            // JWIPreprocessor_Parser.g:426:9: ( modifier )*
            {
            // JWIPreprocessor_Parser.g:426:9: ( modifier )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==ATSIGN) ) {
                    int LA29_2 = input.LA(2);

                    if ( (LA29_2==Identifier) ) {
                        alt29=1;
                    }


                }
                else if ( (LA29_0==ABSTRACT||LA29_0==FINAL||LA29_0==NATIVE||(LA29_0>=PRIVATE && LA29_0<=PUBLIC)||(LA29_0>=STATIC && LA29_0<=STRICTFP)||LA29_0==SYNCHRONIZED||LA29_0==TRANSIENT||LA29_0==VOLATIVE) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_modifiers1325);
            	    modifier();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 16, modifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "modifiers"

    public static class classDeclaration_return extends ParserRuleReturnScope {
        public ClassDefinition classDefinitionObj;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classDeclaration"
    // JWIPreprocessor_Parser.g:429:1: classDeclaration returns [ClassDefinition classDefinitionObj] : (classDefn= normalClassDeclaration | enumDefn= enumDeclaration );
    public final JWIPreprocessor_Parser.classDeclaration_return classDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.classDeclaration_return retval = new JWIPreprocessor_Parser.classDeclaration_return();
        retval.start = input.LT(1);
        int classDeclaration_StartIndex = input.index();
        JWIPreprocessor_Parser.normalClassDeclaration_return classDefn = null;

        JWIPreprocessor_Parser.enumDeclaration_return enumDefn = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return retval; }
            // JWIPreprocessor_Parser.g:430:5: (classDefn= normalClassDeclaration | enumDefn= enumDeclaration )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==CLASS) ) {
                alt30=1;
            }
            else if ( (LA30_0==ENUM) ) {
                alt30=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // JWIPreprocessor_Parser.g:430:9: classDefn= normalClassDeclaration
                    {
                    pushFollow(FOLLOW_normalClassDeclaration_in_classDeclaration1351);
                    classDefn=normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {

                                  retval.classDefinitionObj = (classDefn!=null?classDefn.classDefinitionObj:null);
                              
                    }

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:434:9: enumDefn= enumDeclaration
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_classDeclaration1373);
                    enumDefn=enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {

                                  retval.classDefinitionObj = (enumDefn!=null?enumDefn.classDefinitionObj:null);
                              
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 17, classDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classDeclaration"

    protected static class normalClassDeclaration_scope {
        ClassDefinition defn;
    }
    protected Stack normalClassDeclaration_stack = new Stack();

    public static class normalClassDeclaration_return extends ParserRuleReturnScope {
        public ClassDefinition classDefinitionObj;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "normalClassDeclaration"
    // JWIPreprocessor_Parser.g:440:1: normalClassDeclaration returns [ClassDefinition classDefinitionObj] : CLASS name1= Identifier ( typeParameters )? ( EXTENDS type )? ( IMPLEMENTS typeList )? classImplementsIntentionClause[$normalClassDeclaration::defn] classBody ;
    public final JWIPreprocessor_Parser.normalClassDeclaration_return normalClassDeclaration() throws RecognitionException {
        normalClassDeclaration_stack.push(new normalClassDeclaration_scope());
        JWIPreprocessor_Parser.normalClassDeclaration_return retval = new JWIPreprocessor_Parser.normalClassDeclaration_return();
        retval.start = input.LT(1);
        int normalClassDeclaration_StartIndex = input.index();
        Token name1=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return retval; }
            // JWIPreprocessor_Parser.g:444:5: ( CLASS name1= Identifier ( typeParameters )? ( EXTENDS type )? ( IMPLEMENTS typeList )? classImplementsIntentionClause[$normalClassDeclaration::defn] classBody )
            // JWIPreprocessor_Parser.g:444:9: CLASS name1= Identifier ( typeParameters )? ( EXTENDS type )? ( IMPLEMENTS typeList )? classImplementsIntentionClause[$normalClassDeclaration::defn] classBody
            {
            match(input,CLASS,FOLLOW_CLASS_in_normalClassDeclaration1418); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_normalClassDeclaration1422); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:444:32: ( typeParameters )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==LESSTHAN) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalClassDeclaration1424);
                    typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:445:9: ( EXTENDS type )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==EXTENDS) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // JWIPreprocessor_Parser.g:445:10: EXTENDS type
                    {
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_normalClassDeclaration1436); if (state.failed) return retval;
                    pushFollow(FOLLOW_type_in_normalClassDeclaration1438);
                    type();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:446:9: ( IMPLEMENTS typeList )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==IMPLEMENTS) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // JWIPreprocessor_Parser.g:446:10: IMPLEMENTS typeList
                    {
                    match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_normalClassDeclaration1451); if (state.failed) return retval;
                    pushFollow(FOLLOW_typeList_in_normalClassDeclaration1453);
                    typeList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT G");
                  
                          /* ClassDefinition */
                          ((normalClassDeclaration_scope)normalClassDeclaration_stack.peek()).defn = new ClassDefinition((name1!=null?name1.getText():null));
                          SymbolTableEntry entry = new SymbolTableEntry(((normalClassDeclaration_scope)normalClassDeclaration_stack.peek()).defn);
                          SymbolTableManager.getInstance().getSymbolTable().put((name1!=null?name1.getText():null), entry);
                          
                          retval.classDefinitionObj = ((normalClassDeclaration_scope)normalClassDeclaration_stack.peek()).defn;
                      
            }
            pushFollow(FOLLOW_classImplementsIntentionClause_in_normalClassDeclaration1475);
            classImplementsIntentionClause(((normalClassDeclaration_scope)normalClassDeclaration_stack.peek()).defn);

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_classBody_in_normalClassDeclaration1486);
            classBody();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 18, normalClassDeclaration_StartIndex); }
            normalClassDeclaration_stack.pop();
        }
        return retval;
    }
    // $ANTLR end "normalClassDeclaration"

    public static class typeParameters_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "typeParameters"
    // JWIPreprocessor_Parser.g:461:1: typeParameters : LESSTHAN typeParameter ( COMMA typeParameter )* GREATERTHAN ;
    public final JWIPreprocessor_Parser.typeParameters_return typeParameters() throws RecognitionException {
        JWIPreprocessor_Parser.typeParameters_return retval = new JWIPreprocessor_Parser.typeParameters_return();
        retval.start = input.LT(1);
        int typeParameters_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return retval; }
            // JWIPreprocessor_Parser.g:462:5: ( LESSTHAN typeParameter ( COMMA typeParameter )* GREATERTHAN )
            // JWIPreprocessor_Parser.g:462:9: LESSTHAN typeParameter ( COMMA typeParameter )* GREATERTHAN
            {
            match(input,LESSTHAN,FOLLOW_LESSTHAN_in_typeParameters1509); if (state.failed) return retval;
            pushFollow(FOLLOW_typeParameter_in_typeParameters1511);
            typeParameter();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:462:32: ( COMMA typeParameter )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==COMMA) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:462:33: COMMA typeParameter
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeParameters1514); if (state.failed) return retval;
            	    pushFollow(FOLLOW_typeParameter_in_typeParameters1516);
            	    typeParameter();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_typeParameters1520); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 19, typeParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeParameters"

    public static class typeParameter_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "typeParameter"
    // JWIPreprocessor_Parser.g:465:1: typeParameter : Identifier ( EXTENDS typeBound )? ;
    public final JWIPreprocessor_Parser.typeParameter_return typeParameter() throws RecognitionException {
        JWIPreprocessor_Parser.typeParameter_return retval = new JWIPreprocessor_Parser.typeParameter_return();
        retval.start = input.LT(1);
        int typeParameter_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return retval; }
            // JWIPreprocessor_Parser.g:466:5: ( Identifier ( EXTENDS typeBound )? )
            // JWIPreprocessor_Parser.g:466:9: Identifier ( EXTENDS typeBound )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_typeParameter1539); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:466:20: ( EXTENDS typeBound )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==EXTENDS) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // JWIPreprocessor_Parser.g:466:21: EXTENDS typeBound
                    {
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_typeParameter1542); if (state.failed) return retval;
                    pushFollow(FOLLOW_typeBound_in_typeParameter1544);
                    typeBound();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 20, typeParameter_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeParameter"

    public static class typeBound_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "typeBound"
    // JWIPreprocessor_Parser.g:469:1: typeBound : type ( BITWISE_AND type )* ;
    public final JWIPreprocessor_Parser.typeBound_return typeBound() throws RecognitionException {
        JWIPreprocessor_Parser.typeBound_return retval = new JWIPreprocessor_Parser.typeBound_return();
        retval.start = input.LT(1);
        int typeBound_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return retval; }
            // JWIPreprocessor_Parser.g:470:5: ( type ( BITWISE_AND type )* )
            // JWIPreprocessor_Parser.g:470:9: type ( BITWISE_AND type )*
            {
            pushFollow(FOLLOW_type_in_typeBound1573);
            type();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:470:14: ( BITWISE_AND type )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==BITWISE_AND) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:470:15: BITWISE_AND type
            	    {
            	    match(input,BITWISE_AND,FOLLOW_BITWISE_AND_in_typeBound1576); if (state.failed) return retval;
            	    pushFollow(FOLLOW_type_in_typeBound1578);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 21, typeBound_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeBound"

    public static class enumDeclaration_return extends ParserRuleReturnScope {
        public ClassDefinition classDefinitionObj;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "enumDeclaration"
    // JWIPreprocessor_Parser.g:473:1: enumDeclaration returns [ClassDefinition classDefinitionObj] : ENUM name1= Identifier ( IMPLEMENTS typeList )? enumBody ;
    public final JWIPreprocessor_Parser.enumDeclaration_return enumDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.enumDeclaration_return retval = new JWIPreprocessor_Parser.enumDeclaration_return();
        retval.start = input.LT(1);
        int enumDeclaration_StartIndex = input.index();
        Token name1=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return retval; }
            // JWIPreprocessor_Parser.g:474:5: ( ENUM name1= Identifier ( IMPLEMENTS typeList )? enumBody )
            // JWIPreprocessor_Parser.g:474:9: ENUM name1= Identifier ( IMPLEMENTS typeList )? enumBody
            {
            match(input,ENUM,FOLLOW_ENUM_in_enumDeclaration1603); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_enumDeclaration1607); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:474:31: ( IMPLEMENTS typeList )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==IMPLEMENTS) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // JWIPreprocessor_Parser.g:474:32: IMPLEMENTS typeList
                    {
                    match(input,IMPLEMENTS,FOLLOW_IMPLEMENTS_in_enumDeclaration1610); if (state.failed) return retval;
                    pushFollow(FOLLOW_typeList_in_enumDeclaration1612);
                    typeList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT B");
                  
                          ClassDefinition defn = new ClassDefinition((name1!=null?name1.getText():null));
                          SymbolTableEntry entry = new SymbolTableEntry(defn);
                          SymbolTableManager.getInstance().getSymbolTable().put((name1!=null?name1.getText():null), entry);
                          
                          retval.classDefinitionObj = defn;
                      
            }
            pushFollow(FOLLOW_enumBody_in_enumDeclaration1634);
            enumBody();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 22, enumDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumDeclaration"

    public static class enumBody_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "enumBody"
    // JWIPreprocessor_Parser.g:487:1: enumBody : LEFTBRACE ( enumConstants )? ( COMMA )? ( enumBodyDeclarations )? RIGHTBRACE ;
    public final JWIPreprocessor_Parser.enumBody_return enumBody() throws RecognitionException {
        JWIPreprocessor_Parser.enumBody_return retval = new JWIPreprocessor_Parser.enumBody_return();
        retval.start = input.LT(1);
        int enumBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return retval; }
            // JWIPreprocessor_Parser.g:488:5: ( LEFTBRACE ( enumConstants )? ( COMMA )? ( enumBodyDeclarations )? RIGHTBRACE )
            // JWIPreprocessor_Parser.g:488:9: LEFTBRACE ( enumConstants )? ( COMMA )? ( enumBodyDeclarations )? RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_enumBody1653); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:488:19: ( enumConstants )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==ATSIGN||LA38_0==Identifier) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: enumConstants
                    {
                    pushFollow(FOLLOW_enumConstants_in_enumBody1655);
                    enumConstants();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:488:34: ( COMMA )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==COMMA) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_enumBody1658); if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:488:41: ( enumBodyDeclarations )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==SEMICOLON) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: enumBodyDeclarations
                    {
                    pushFollow(FOLLOW_enumBodyDeclarations_in_enumBody1661);
                    enumBodyDeclarations();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_enumBody1664); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 23, enumBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumBody"

    public static class enumConstants_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "enumConstants"
    // JWIPreprocessor_Parser.g:491:1: enumConstants : enumConstant ( COMMA enumConstant )* ;
    public final JWIPreprocessor_Parser.enumConstants_return enumConstants() throws RecognitionException {
        JWIPreprocessor_Parser.enumConstants_return retval = new JWIPreprocessor_Parser.enumConstants_return();
        retval.start = input.LT(1);
        int enumConstants_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return retval; }
            // JWIPreprocessor_Parser.g:492:5: ( enumConstant ( COMMA enumConstant )* )
            // JWIPreprocessor_Parser.g:492:9: enumConstant ( COMMA enumConstant )*
            {
            pushFollow(FOLLOW_enumConstant_in_enumConstants1683);
            enumConstant();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:492:22: ( COMMA enumConstant )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==COMMA) ) {
                    int LA41_1 = input.LA(2);

                    if ( (LA41_1==ATSIGN||LA41_1==Identifier) ) {
                        alt41=1;
                    }


                }


                switch (alt41) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:492:23: COMMA enumConstant
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_enumConstants1686); if (state.failed) return retval;
            	    pushFollow(FOLLOW_enumConstant_in_enumConstants1688);
            	    enumConstant();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 24, enumConstants_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumConstants"

    public static class enumConstant_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "enumConstant"
    // JWIPreprocessor_Parser.g:495:1: enumConstant : ( annotations )? Identifier ( arguments )? ( classBody )? ;
    public final JWIPreprocessor_Parser.enumConstant_return enumConstant() throws RecognitionException {
        JWIPreprocessor_Parser.enumConstant_return retval = new JWIPreprocessor_Parser.enumConstant_return();
        retval.start = input.LT(1);
        int enumConstant_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return retval; }
            // JWIPreprocessor_Parser.g:496:5: ( ( annotations )? Identifier ( arguments )? ( classBody )? )
            // JWIPreprocessor_Parser.g:496:9: ( annotations )? Identifier ( arguments )? ( classBody )?
            {
            // JWIPreprocessor_Parser.g:496:9: ( annotations )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==ATSIGN) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: annotations
                    {
                    pushFollow(FOLLOW_annotations_in_enumConstant1713);
                    annotations();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_enumConstant1716); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:496:33: ( arguments )?
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==LEFTPARENTHESIS) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_enumConstant1718);
                    arguments();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:496:44: ( classBody )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==LEFTBRACE) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_enumConstant1721);
                    classBody();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 25, enumConstant_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumConstant"

    public static class enumBodyDeclarations_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "enumBodyDeclarations"
    // JWIPreprocessor_Parser.g:499:1: enumBodyDeclarations : SEMICOLON ( classBodyDeclaration )* ;
    public final JWIPreprocessor_Parser.enumBodyDeclarations_return enumBodyDeclarations() throws RecognitionException {
        JWIPreprocessor_Parser.enumBodyDeclarations_return retval = new JWIPreprocessor_Parser.enumBodyDeclarations_return();
        retval.start = input.LT(1);
        int enumBodyDeclarations_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return retval; }
            // JWIPreprocessor_Parser.g:500:5: ( SEMICOLON ( classBodyDeclaration )* )
            // JWIPreprocessor_Parser.g:500:9: SEMICOLON ( classBodyDeclaration )*
            {
            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_enumBodyDeclarations1745); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:500:19: ( classBodyDeclaration )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( ((LA45_0>=ABSTRACT && LA45_0<=BOOLEAN)||LA45_0==BYTE||(LA45_0>=CHAR && LA45_0<=CLASS)||LA45_0==DOUBLE||LA45_0==FINAL||LA45_0==FLOAT||(LA45_0>=INT && LA45_0<=NATIVE)||(LA45_0>=PRIVATE && LA45_0<=PUBLIC)||(LA45_0>=SHORT && LA45_0<=STRICTFP)||LA45_0==SYNCHRONIZED||LA45_0==TRANSIENT||LA45_0==VOID||LA45_0==SEMICOLON||LA45_0==LEFTBRACE||LA45_0==LESSTHAN||LA45_0==ATSIGN||LA45_0==ENUM||LA45_0==Identifier||LA45_0==VOLATIVE) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:500:20: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1748);
            	    classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 26, enumBodyDeclarations_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumBodyDeclarations"

    public static class interfaceDeclaration_return extends ParserRuleReturnScope {
        public ClassDefinition classDefinitionObj;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "interfaceDeclaration"
    // JWIPreprocessor_Parser.g:503:1: interfaceDeclaration returns [ClassDefinition classDefinitionObj] : (interfaceDef= normalInterfaceDeclaration | annotationDef= annotationTypeDeclaration );
    public final JWIPreprocessor_Parser.interfaceDeclaration_return interfaceDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.interfaceDeclaration_return retval = new JWIPreprocessor_Parser.interfaceDeclaration_return();
        retval.start = input.LT(1);
        int interfaceDeclaration_StartIndex = input.index();
        JWIPreprocessor_Parser.normalInterfaceDeclaration_return interfaceDef = null;

        JWIPreprocessor_Parser.annotationTypeDeclaration_return annotationDef = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return retval; }
            // JWIPreprocessor_Parser.g:504:5: (interfaceDef= normalInterfaceDeclaration | annotationDef= annotationTypeDeclaration )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==INTERFACE) ) {
                alt46=1;
            }
            else if ( (LA46_0==ATSIGN) ) {
                alt46=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // JWIPreprocessor_Parser.g:504:9: interfaceDef= normalInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1783);
                    interfaceDef=normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {

                                  retval.classDefinitionObj = (interfaceDef!=null?interfaceDef.classDefinitionObj:null);
                              
                    }

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:508:9: annotationDef= annotationTypeDeclaration
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1805);
                    annotationDef=annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {

                                  retval.classDefinitionObj = (annotationDef!=null?annotationDef.classDefinitionObj:null);
                              
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 27, interfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceDeclaration"

    public static class normalInterfaceDeclaration_return extends ParserRuleReturnScope {
        public ClassDefinition classDefinitionObj;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "normalInterfaceDeclaration"
    // JWIPreprocessor_Parser.g:514:1: normalInterfaceDeclaration returns [ClassDefinition classDefinitionObj] : INTERFACE name1= Identifier ( typeParameters )? ( EXTENDS typeList )? interfaceBody ;
    public final JWIPreprocessor_Parser.normalInterfaceDeclaration_return normalInterfaceDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.normalInterfaceDeclaration_return retval = new JWIPreprocessor_Parser.normalInterfaceDeclaration_return();
        retval.start = input.LT(1);
        int normalInterfaceDeclaration_StartIndex = input.index();
        Token name1=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return retval; }
            // JWIPreprocessor_Parser.g:515:5: ( INTERFACE name1= Identifier ( typeParameters )? ( EXTENDS typeList )? interfaceBody )
            // JWIPreprocessor_Parser.g:515:9: INTERFACE name1= Identifier ( typeParameters )? ( EXTENDS typeList )? interfaceBody
            {
            match(input,INTERFACE,FOLLOW_INTERFACE_in_normalInterfaceDeclaration1838); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_normalInterfaceDeclaration1842); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:515:36: ( typeParameters )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==LESSTHAN) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: typeParameters
                    {
                    pushFollow(FOLLOW_typeParameters_in_normalInterfaceDeclaration1844);
                    typeParameters();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:515:52: ( EXTENDS typeList )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==EXTENDS) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // JWIPreprocessor_Parser.g:515:53: EXTENDS typeList
                    {
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_normalInterfaceDeclaration1848); if (state.failed) return retval;
                    pushFollow(FOLLOW_typeList_in_normalInterfaceDeclaration1850);
                    typeList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT B");
                  
                          ClassDefinition defn = new ClassDefinition((name1!=null?name1.getText():null));
                          SymbolTableEntry entry = new SymbolTableEntry(defn);
                          SymbolTableManager.getInstance().getSymbolTable().put((name1!=null?name1.getText():null), entry);
                          
                          retval.classDefinitionObj = defn;
                      
            }
            pushFollow(FOLLOW_interfaceBody_in_normalInterfaceDeclaration1876);
            interfaceBody();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 28, normalInterfaceDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "normalInterfaceDeclaration"

    public static class typeList_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "typeList"
    // JWIPreprocessor_Parser.g:528:1: typeList : type ( COMMA type )* ;
    public final JWIPreprocessor_Parser.typeList_return typeList() throws RecognitionException {
        JWIPreprocessor_Parser.typeList_return retval = new JWIPreprocessor_Parser.typeList_return();
        retval.start = input.LT(1);
        int typeList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return retval; }
            // JWIPreprocessor_Parser.g:529:5: ( type ( COMMA type )* )
            // JWIPreprocessor_Parser.g:529:9: type ( COMMA type )*
            {
            pushFollow(FOLLOW_type_in_typeList1899);
            type();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:529:14: ( COMMA type )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==COMMA) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:529:15: COMMA type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList1902); if (state.failed) return retval;
            	    pushFollow(FOLLOW_type_in_typeList1904);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop49;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 29, typeList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeList"

    public static class classBody_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classBody"
    // JWIPreprocessor_Parser.g:532:1: classBody : LEFTBRACE ( classBodyDeclaration )* RIGHTBRACE ;
    public final JWIPreprocessor_Parser.classBody_return classBody() throws RecognitionException {
        JWIPreprocessor_Parser.classBody_return retval = new JWIPreprocessor_Parser.classBody_return();
        retval.start = input.LT(1);
        int classBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return retval; }
            // JWIPreprocessor_Parser.g:533:5: ( LEFTBRACE ( classBodyDeclaration )* RIGHTBRACE )
            // JWIPreprocessor_Parser.g:533:9: LEFTBRACE ( classBodyDeclaration )* RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_classBody1929); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:533:19: ( classBodyDeclaration )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( ((LA50_0>=ABSTRACT && LA50_0<=BOOLEAN)||LA50_0==BYTE||(LA50_0>=CHAR && LA50_0<=CLASS)||LA50_0==DOUBLE||LA50_0==FINAL||LA50_0==FLOAT||(LA50_0>=INT && LA50_0<=NATIVE)||(LA50_0>=PRIVATE && LA50_0<=PUBLIC)||(LA50_0>=SHORT && LA50_0<=STRICTFP)||LA50_0==SYNCHRONIZED||LA50_0==TRANSIENT||LA50_0==VOID||LA50_0==SEMICOLON||LA50_0==LEFTBRACE||LA50_0==LESSTHAN||LA50_0==ATSIGN||LA50_0==ENUM||LA50_0==Identifier||LA50_0==VOLATIVE) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: classBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_classBodyDeclaration_in_classBody1931);
            	    classBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop50;
                }
            } while (true);

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_classBody1934); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 30, classBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classBody"

    public static class interfaceBody_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "interfaceBody"
    // JWIPreprocessor_Parser.g:536:1: interfaceBody : LEFTBRACE ( interfaceBodyDeclaration )* RIGHTBRACE ;
    public final JWIPreprocessor_Parser.interfaceBody_return interfaceBody() throws RecognitionException {
        JWIPreprocessor_Parser.interfaceBody_return retval = new JWIPreprocessor_Parser.interfaceBody_return();
        retval.start = input.LT(1);
        int interfaceBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return retval; }
            // JWIPreprocessor_Parser.g:537:5: ( LEFTBRACE ( interfaceBodyDeclaration )* RIGHTBRACE )
            // JWIPreprocessor_Parser.g:537:9: LEFTBRACE ( interfaceBodyDeclaration )* RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_interfaceBody1957); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:537:19: ( interfaceBodyDeclaration )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( ((LA51_0>=ABSTRACT && LA51_0<=BOOLEAN)||LA51_0==BYTE||(LA51_0>=CHAR && LA51_0<=CLASS)||LA51_0==DOUBLE||LA51_0==FINAL||LA51_0==FLOAT||(LA51_0>=INT && LA51_0<=NATIVE)||(LA51_0>=PRIVATE && LA51_0<=PUBLIC)||(LA51_0>=SHORT && LA51_0<=STRICTFP)||LA51_0==SYNCHRONIZED||LA51_0==TRANSIENT||LA51_0==VOID||LA51_0==SEMICOLON||LA51_0==LESSTHAN||LA51_0==ATSIGN||LA51_0==ENUM||LA51_0==Identifier||LA51_0==VOLATIVE) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: interfaceBodyDeclaration
            	    {
            	    pushFollow(FOLLOW_interfaceBodyDeclaration_in_interfaceBody1959);
            	    interfaceBodyDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_interfaceBody1962); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 31, interfaceBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceBody"

    public static class classBodyDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classBodyDeclaration"
    // JWIPreprocessor_Parser.g:540:1: classBodyDeclaration : ( SEMICOLON | ( STATIC )? block | modifiers memberDecl );
    public final JWIPreprocessor_Parser.classBodyDeclaration_return classBodyDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.classBodyDeclaration_return retval = new JWIPreprocessor_Parser.classBodyDeclaration_return();
        retval.start = input.LT(1);
        int classBodyDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return retval; }
            // JWIPreprocessor_Parser.g:541:5: ( SEMICOLON | ( STATIC )? block | modifiers memberDecl )
            int alt53=3;
            switch ( input.LA(1) ) {
            case SEMICOLON:
                {
                alt53=1;
                }
                break;
            case STATIC:
                {
                int LA53_2 = input.LA(2);

                if ( (LA53_2==LEFTBRACE) ) {
                    alt53=2;
                }
                else if ( ((LA53_2>=ABSTRACT && LA53_2<=BOOLEAN)||LA53_2==BYTE||(LA53_2>=CHAR && LA53_2<=CLASS)||LA53_2==DOUBLE||LA53_2==FINAL||LA53_2==FLOAT||(LA53_2>=INT && LA53_2<=NATIVE)||(LA53_2>=PRIVATE && LA53_2<=PUBLIC)||(LA53_2>=SHORT && LA53_2<=STRICTFP)||LA53_2==SYNCHRONIZED||LA53_2==TRANSIENT||LA53_2==VOID||LA53_2==LESSTHAN||LA53_2==ATSIGN||LA53_2==ENUM||LA53_2==Identifier||LA53_2==VOLATIVE) ) {
                    alt53=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 2, input);

                    throw nvae;
                }
                }
                break;
            case LEFTBRACE:
                {
                alt53=2;
                }
                break;
            case ABSTRACT:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case CLASS:
            case DOUBLE:
            case FINAL:
            case FLOAT:
            case INT:
            case INTERFACE:
            case LONG:
            case NATIVE:
            case PRIVATE:
            case PROTECTED:
            case PUBLIC:
            case SHORT:
            case STRICTFP:
            case SYNCHRONIZED:
            case TRANSIENT:
            case VOID:
            case LESSTHAN:
            case ATSIGN:
            case ENUM:
            case Identifier:
            case VOLATIVE:
                {
                alt53=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // JWIPreprocessor_Parser.g:541:9: SEMICOLON
                    {
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_classBodyDeclaration1981); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:542:9: ( STATIC )? block
                    {
                    // JWIPreprocessor_Parser.g:542:9: ( STATIC )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==STATIC) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: STATIC
                            {
                            match(input,STATIC,FOLLOW_STATIC_in_classBodyDeclaration1991); if (state.failed) return retval;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_block_in_classBodyDeclaration1994);
                    block();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:543:9: modifiers memberDecl
                    {
                    pushFollow(FOLLOW_modifiers_in_classBodyDeclaration2004);
                    modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_memberDecl_in_classBodyDeclaration2006);
                    memberDecl();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 32, classBodyDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classBodyDeclaration"

    public static class memberDecl_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "memberDecl"
    // JWIPreprocessor_Parser.g:546:1: memberDecl : ( genericMethodOrConstructorDecl | memberDeclaration | VOID Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final JWIPreprocessor_Parser.memberDecl_return memberDecl() throws RecognitionException {
        JWIPreprocessor_Parser.memberDecl_return retval = new JWIPreprocessor_Parser.memberDecl_return();
        retval.start = input.LT(1);
        int memberDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return retval; }
            // JWIPreprocessor_Parser.g:547:5: ( genericMethodOrConstructorDecl | memberDeclaration | VOID Identifier voidMethodDeclaratorRest | Identifier constructorDeclaratorRest | interfaceDeclaration | classDeclaration )
            int alt54=6;
            switch ( input.LA(1) ) {
            case LESSTHAN:
                {
                alt54=1;
                }
                break;
            case Identifier:
                {
                int LA54_2 = input.LA(2);

                if ( (LA54_2==LEFTPARENTHESIS) ) {
                    alt54=4;
                }
                else if ( (LA54_2==LEFTSQUAREBRACKET||LA54_2==LESSTHAN||LA54_2==DOT||LA54_2==Identifier) ) {
                    alt54=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 54, 2, input);

                    throw nvae;
                }
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                alt54=2;
                }
                break;
            case VOID:
                {
                alt54=3;
                }
                break;
            case INTERFACE:
            case ATSIGN:
                {
                alt54=5;
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt54=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }

            switch (alt54) {
                case 1 :
                    // JWIPreprocessor_Parser.g:547:9: genericMethodOrConstructorDecl
                    {
                    pushFollow(FOLLOW_genericMethodOrConstructorDecl_in_memberDecl2029);
                    genericMethodOrConstructorDecl();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:548:9: memberDeclaration
                    {
                    pushFollow(FOLLOW_memberDeclaration_in_memberDecl2039);
                    memberDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:549:9: VOID Identifier voidMethodDeclaratorRest
                    {
                    match(input,VOID,FOLLOW_VOID_in_memberDecl2049); if (state.failed) return retval;
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl2051); if (state.failed) return retval;
                    pushFollow(FOLLOW_voidMethodDeclaratorRest_in_memberDecl2053);
                    voidMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:550:9: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_memberDecl2063); if (state.failed) return retval;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_memberDecl2065);
                    constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:551:9: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_memberDecl2075);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // JWIPreprocessor_Parser.g:552:9: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_memberDecl2085);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 33, memberDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "memberDecl"

    public static class memberDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "memberDeclaration"
    // JWIPreprocessor_Parser.g:555:1: memberDeclaration : type ( methodDeclaration | fieldDeclaration ) ;
    public final JWIPreprocessor_Parser.memberDeclaration_return memberDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.memberDeclaration_return retval = new JWIPreprocessor_Parser.memberDeclaration_return();
        retval.start = input.LT(1);
        int memberDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return retval; }
            // JWIPreprocessor_Parser.g:556:5: ( type ( methodDeclaration | fieldDeclaration ) )
            // JWIPreprocessor_Parser.g:556:9: type ( methodDeclaration | fieldDeclaration )
            {
            pushFollow(FOLLOW_type_in_memberDeclaration2108);
            type();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:556:14: ( methodDeclaration | fieldDeclaration )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==Identifier) ) {
                int LA55_1 = input.LA(2);

                if ( (LA55_1==LEFTPARENTHESIS) ) {
                    alt55=1;
                }
                else if ( (LA55_1==SEMICOLON||LA55_1==COMMA||LA55_1==LEFTSQUAREBRACKET||LA55_1==ASSIGNMENT_EQUALS) ) {
                    alt55=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 55, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // JWIPreprocessor_Parser.g:556:15: methodDeclaration
                    {
                    pushFollow(FOLLOW_methodDeclaration_in_memberDeclaration2111);
                    methodDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:556:35: fieldDeclaration
                    {
                    pushFollow(FOLLOW_fieldDeclaration_in_memberDeclaration2115);
                    fieldDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 34, memberDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "memberDeclaration"

    public static class genericMethodOrConstructorDecl_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "genericMethodOrConstructorDecl"
    // JWIPreprocessor_Parser.g:559:1: genericMethodOrConstructorDecl : typeParameters genericMethodOrConstructorRest ;
    public final JWIPreprocessor_Parser.genericMethodOrConstructorDecl_return genericMethodOrConstructorDecl() throws RecognitionException {
        JWIPreprocessor_Parser.genericMethodOrConstructorDecl_return retval = new JWIPreprocessor_Parser.genericMethodOrConstructorDecl_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return retval; }
            // JWIPreprocessor_Parser.g:560:5: ( typeParameters genericMethodOrConstructorRest )
            // JWIPreprocessor_Parser.g:560:9: typeParameters genericMethodOrConstructorRest
            {
            pushFollow(FOLLOW_typeParameters_in_genericMethodOrConstructorDecl2135);
            typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl2137);
            genericMethodOrConstructorRest();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 35, genericMethodOrConstructorDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "genericMethodOrConstructorDecl"

    public static class genericMethodOrConstructorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "genericMethodOrConstructorRest"
    // JWIPreprocessor_Parser.g:563:1: genericMethodOrConstructorRest : ( ( type | VOID ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest );
    public final JWIPreprocessor_Parser.genericMethodOrConstructorRest_return genericMethodOrConstructorRest() throws RecognitionException {
        JWIPreprocessor_Parser.genericMethodOrConstructorRest_return retval = new JWIPreprocessor_Parser.genericMethodOrConstructorRest_return();
        retval.start = input.LT(1);
        int genericMethodOrConstructorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return retval; }
            // JWIPreprocessor_Parser.g:564:5: ( ( type | VOID ) Identifier methodDeclaratorRest | Identifier constructorDeclaratorRest )
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==Identifier) ) {
                int LA57_1 = input.LA(2);

                if ( (LA57_1==LEFTPARENTHESIS) ) {
                    alt57=2;
                }
                else if ( (LA57_1==LEFTSQUAREBRACKET||LA57_1==LESSTHAN||LA57_1==DOT||LA57_1==Identifier) ) {
                    alt57=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 57, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA57_0==BOOLEAN||LA57_0==BYTE||LA57_0==CHAR||LA57_0==DOUBLE||LA57_0==FLOAT||LA57_0==INT||LA57_0==LONG||LA57_0==SHORT||LA57_0==VOID) ) {
                alt57=1;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // JWIPreprocessor_Parser.g:564:9: ( type | VOID ) Identifier methodDeclaratorRest
                    {
                    // JWIPreprocessor_Parser.g:564:9: ( type | VOID )
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==BOOLEAN||LA56_0==BYTE||LA56_0==CHAR||LA56_0==DOUBLE||LA56_0==FLOAT||LA56_0==INT||LA56_0==LONG||LA56_0==SHORT||LA56_0==Identifier) ) {
                        alt56=1;
                    }
                    else if ( (LA56_0==VOID) ) {
                        alt56=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 56, 0, input);

                        throw nvae;
                    }
                    switch (alt56) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:564:10: type
                            {
                            pushFollow(FOLLOW_type_in_genericMethodOrConstructorRest2161);
                            type();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;
                        case 2 :
                            // JWIPreprocessor_Parser.g:564:17: VOID
                            {
                            match(input,VOID,FOLLOW_VOID_in_genericMethodOrConstructorRest2165); if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest2168); if (state.failed) return retval;
                    pushFollow(FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest2170);
                    methodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:565:9: Identifier constructorDeclaratorRest
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_genericMethodOrConstructorRest2180); if (state.failed) return retval;
                    pushFollow(FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest2182);
                    constructorDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 36, genericMethodOrConstructorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "genericMethodOrConstructorRest"

    public static class methodDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "methodDeclaration"
    // JWIPreprocessor_Parser.g:568:1: methodDeclaration : Identifier methodDeclaratorRest ;
    public final JWIPreprocessor_Parser.methodDeclaration_return methodDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.methodDeclaration_return retval = new JWIPreprocessor_Parser.methodDeclaration_return();
        retval.start = input.LT(1);
        int methodDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return retval; }
            // JWIPreprocessor_Parser.g:569:5: ( Identifier methodDeclaratorRest )
            // JWIPreprocessor_Parser.g:569:9: Identifier methodDeclaratorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_methodDeclaration2201); if (state.failed) return retval;
            pushFollow(FOLLOW_methodDeclaratorRest_in_methodDeclaration2203);
            methodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 37, methodDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodDeclaration"

    public static class fieldDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "fieldDeclaration"
    // JWIPreprocessor_Parser.g:572:1: fieldDeclaration : variableDeclarators SEMICOLON ;
    public final JWIPreprocessor_Parser.fieldDeclaration_return fieldDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.fieldDeclaration_return retval = new JWIPreprocessor_Parser.fieldDeclaration_return();
        retval.start = input.LT(1);
        int fieldDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return retval; }
            // JWIPreprocessor_Parser.g:573:5: ( variableDeclarators SEMICOLON )
            // JWIPreprocessor_Parser.g:573:9: variableDeclarators SEMICOLON
            {
            pushFollow(FOLLOW_variableDeclarators_in_fieldDeclaration2222);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;
            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_fieldDeclaration2224); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 38, fieldDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "fieldDeclaration"

    public static class interfaceBodyDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "interfaceBodyDeclaration"
    // JWIPreprocessor_Parser.g:576:1: interfaceBodyDeclaration : ( modifiers interfaceMemberDecl | SEMICOLON );
    public final JWIPreprocessor_Parser.interfaceBodyDeclaration_return interfaceBodyDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.interfaceBodyDeclaration_return retval = new JWIPreprocessor_Parser.interfaceBodyDeclaration_return();
        retval.start = input.LT(1);
        int interfaceBodyDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return retval; }
            // JWIPreprocessor_Parser.g:577:5: ( modifiers interfaceMemberDecl | SEMICOLON )
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( ((LA58_0>=ABSTRACT && LA58_0<=BOOLEAN)||LA58_0==BYTE||(LA58_0>=CHAR && LA58_0<=CLASS)||LA58_0==DOUBLE||LA58_0==FINAL||LA58_0==FLOAT||(LA58_0>=INT && LA58_0<=NATIVE)||(LA58_0>=PRIVATE && LA58_0<=PUBLIC)||(LA58_0>=SHORT && LA58_0<=STRICTFP)||LA58_0==SYNCHRONIZED||LA58_0==TRANSIENT||LA58_0==VOID||LA58_0==LESSTHAN||LA58_0==ATSIGN||LA58_0==ENUM||LA58_0==Identifier||LA58_0==VOLATIVE) ) {
                alt58=1;
            }
            else if ( (LA58_0==SEMICOLON) ) {
                alt58=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 58, 0, input);

                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // JWIPreprocessor_Parser.g:577:9: modifiers interfaceMemberDecl
                    {
                    pushFollow(FOLLOW_modifiers_in_interfaceBodyDeclaration2251);
                    modifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration2253);
                    interfaceMemberDecl();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:578:9: SEMICOLON
                    {
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_interfaceBodyDeclaration2263); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 39, interfaceBodyDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceBodyDeclaration"

    public static class interfaceMemberDecl_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "interfaceMemberDecl"
    // JWIPreprocessor_Parser.g:581:1: interfaceMemberDecl : ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | VOID Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration );
    public final JWIPreprocessor_Parser.interfaceMemberDecl_return interfaceMemberDecl() throws RecognitionException {
        JWIPreprocessor_Parser.interfaceMemberDecl_return retval = new JWIPreprocessor_Parser.interfaceMemberDecl_return();
        retval.start = input.LT(1);
        int interfaceMemberDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return retval; }
            // JWIPreprocessor_Parser.g:582:5: ( interfaceMethodOrFieldDecl | interfaceGenericMethodDecl | VOID Identifier voidInterfaceMethodDeclaratorRest | interfaceDeclaration | classDeclaration )
            int alt59=5;
            switch ( input.LA(1) ) {
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
            case Identifier:
                {
                alt59=1;
                }
                break;
            case LESSTHAN:
                {
                alt59=2;
                }
                break;
            case VOID:
                {
                alt59=3;
                }
                break;
            case INTERFACE:
            case ATSIGN:
                {
                alt59=4;
                }
                break;
            case CLASS:
            case ENUM:
                {
                alt59=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }

            switch (alt59) {
                case 1 :
                    // JWIPreprocessor_Parser.g:582:9: interfaceMethodOrFieldDecl
                    {
                    pushFollow(FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl2282);
                    interfaceMethodOrFieldDecl();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:583:9: interfaceGenericMethodDecl
                    {
                    pushFollow(FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl2292);
                    interfaceGenericMethodDecl();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:584:9: VOID Identifier voidInterfaceMethodDeclaratorRest
                    {
                    match(input,VOID,FOLLOW_VOID_in_interfaceMemberDecl2302); if (state.failed) return retval;
                    match(input,Identifier,FOLLOW_Identifier_in_interfaceMemberDecl2304); if (state.failed) return retval;
                    pushFollow(FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl2306);
                    voidInterfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:585:9: interfaceDeclaration
                    {
                    pushFollow(FOLLOW_interfaceDeclaration_in_interfaceMemberDecl2316);
                    interfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:586:9: classDeclaration
                    {
                    pushFollow(FOLLOW_classDeclaration_in_interfaceMemberDecl2326);
                    classDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 40, interfaceMemberDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMemberDecl"

    public static class interfaceMethodOrFieldDecl_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "interfaceMethodOrFieldDecl"
    // JWIPreprocessor_Parser.g:589:1: interfaceMethodOrFieldDecl : type Identifier interfaceMethodOrFieldRest ;
    public final JWIPreprocessor_Parser.interfaceMethodOrFieldDecl_return interfaceMethodOrFieldDecl() throws RecognitionException {
        JWIPreprocessor_Parser.interfaceMethodOrFieldDecl_return retval = new JWIPreprocessor_Parser.interfaceMethodOrFieldDecl_return();
        retval.start = input.LT(1);
        int interfaceMethodOrFieldDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return retval; }
            // JWIPreprocessor_Parser.g:590:5: ( type Identifier interfaceMethodOrFieldRest )
            // JWIPreprocessor_Parser.g:590:9: type Identifier interfaceMethodOrFieldRest
            {
            pushFollow(FOLLOW_type_in_interfaceMethodOrFieldDecl2349);
            type();

            state._fsp--;
            if (state.failed) return retval;
            match(input,Identifier,FOLLOW_Identifier_in_interfaceMethodOrFieldDecl2351); if (state.failed) return retval;
            pushFollow(FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl2353);
            interfaceMethodOrFieldRest();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 41, interfaceMethodOrFieldDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMethodOrFieldDecl"

    public static class interfaceMethodOrFieldRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "interfaceMethodOrFieldRest"
    // JWIPreprocessor_Parser.g:593:1: interfaceMethodOrFieldRest : ( constantDeclaratorsRest SEMICOLON | interfaceMethodDeclaratorRest );
    public final JWIPreprocessor_Parser.interfaceMethodOrFieldRest_return interfaceMethodOrFieldRest() throws RecognitionException {
        JWIPreprocessor_Parser.interfaceMethodOrFieldRest_return retval = new JWIPreprocessor_Parser.interfaceMethodOrFieldRest_return();
        retval.start = input.LT(1);
        int interfaceMethodOrFieldRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return retval; }
            // JWIPreprocessor_Parser.g:594:5: ( constantDeclaratorsRest SEMICOLON | interfaceMethodDeclaratorRest )
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==LEFTSQUAREBRACKET||LA60_0==ASSIGNMENT_EQUALS) ) {
                alt60=1;
            }
            else if ( (LA60_0==LEFTPARENTHESIS) ) {
                alt60=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // JWIPreprocessor_Parser.g:594:9: constantDeclaratorsRest SEMICOLON
                    {
                    pushFollow(FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest2376);
                    constantDeclaratorsRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_interfaceMethodOrFieldRest2378); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:595:9: interfaceMethodDeclaratorRest
                    {
                    pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest2388);
                    interfaceMethodDeclaratorRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 42, interfaceMethodOrFieldRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMethodOrFieldRest"

    public static class methodDeclaratorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "methodDeclaratorRest"
    // JWIPreprocessor_Parser.g:598:1: methodDeclaratorRest : formalParameters ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? ( methodBody | SEMICOLON ) ;
    public final JWIPreprocessor_Parser.methodDeclaratorRest_return methodDeclaratorRest() throws RecognitionException {
        JWIPreprocessor_Parser.methodDeclaratorRest_return retval = new JWIPreprocessor_Parser.methodDeclaratorRest_return();
        retval.start = input.LT(1);
        int methodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return retval; }
            // JWIPreprocessor_Parser.g:599:5: ( formalParameters ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? ( methodBody | SEMICOLON ) )
            // JWIPreprocessor_Parser.g:599:9: formalParameters ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? ( methodBody | SEMICOLON )
            {
            pushFollow(FOLLOW_formalParameters_in_methodDeclaratorRest2411);
            formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:599:26: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==LEFTSQUAREBRACKET) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:599:27: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
            	    {
            	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_methodDeclaratorRest2414); if (state.failed) return retval;
            	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_methodDeclaratorRest2416); if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop61;
                }
            } while (true);

            // JWIPreprocessor_Parser.g:600:9: ( methodImplementsIntentionClause )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==IMPLEMENTSINTENTION) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: methodImplementsIntentionClause
                    {
                    pushFollow(FOLLOW_methodImplementsIntentionClause_in_methodDeclaratorRest2428);
                    methodImplementsIntentionClause();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:601:9: ( THROWS qualifiedNameList )?
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==THROWS) ) {
                alt63=1;
            }
            switch (alt63) {
                case 1 :
                    // JWIPreprocessor_Parser.g:601:10: THROWS qualifiedNameList
                    {
                    match(input,THROWS,FOLLOW_THROWS_in_methodDeclaratorRest2440); if (state.failed) return retval;
                    pushFollow(FOLLOW_qualifiedNameList_in_methodDeclaratorRest2442);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:602:9: ( methodBody | SEMICOLON )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==LEFTBRACE) ) {
                alt64=1;
            }
            else if ( (LA64_0==SEMICOLON) ) {
                alt64=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // JWIPreprocessor_Parser.g:602:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_methodDeclaratorRest2458);
                    methodBody();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:603:13: SEMICOLON
                    {
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_methodDeclaratorRest2472); if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 43, methodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodDeclaratorRest"

    public static class voidMethodDeclaratorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "voidMethodDeclaratorRest"
    // JWIPreprocessor_Parser.g:607:1: voidMethodDeclaratorRest : formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? ( methodBody | SEMICOLON ) ;
    public final JWIPreprocessor_Parser.voidMethodDeclaratorRest_return voidMethodDeclaratorRest() throws RecognitionException {
        JWIPreprocessor_Parser.voidMethodDeclaratorRest_return retval = new JWIPreprocessor_Parser.voidMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return retval; }
            // JWIPreprocessor_Parser.g:608:5: ( formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? ( methodBody | SEMICOLON ) )
            // JWIPreprocessor_Parser.g:608:9: formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? ( methodBody | SEMICOLON )
            {
            pushFollow(FOLLOW_formalParameters_in_voidMethodDeclaratorRest2505);
            formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:609:9: ( methodImplementsIntentionClause )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==IMPLEMENTSINTENTION) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: methodImplementsIntentionClause
                    {
                    pushFollow(FOLLOW_methodImplementsIntentionClause_in_voidMethodDeclaratorRest2515);
                    methodImplementsIntentionClause();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:610:9: ( THROWS qualifiedNameList )?
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==THROWS) ) {
                alt66=1;
            }
            switch (alt66) {
                case 1 :
                    // JWIPreprocessor_Parser.g:610:10: THROWS qualifiedNameList
                    {
                    match(input,THROWS,FOLLOW_THROWS_in_voidMethodDeclaratorRest2527); if (state.failed) return retval;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest2529);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:611:9: ( methodBody | SEMICOLON )
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==LEFTBRACE) ) {
                alt67=1;
            }
            else if ( (LA67_0==SEMICOLON) ) {
                alt67=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 67, 0, input);

                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // JWIPreprocessor_Parser.g:611:13: methodBody
                    {
                    pushFollow(FOLLOW_methodBody_in_voidMethodDeclaratorRest2545);
                    methodBody();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:612:13: SEMICOLON
                    {
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_voidMethodDeclaratorRest2559); if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 44, voidMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "voidMethodDeclaratorRest"

    public static class interfaceMethodDeclaratorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "interfaceMethodDeclaratorRest"
    // JWIPreprocessor_Parser.g:616:1: interfaceMethodDeclaratorRest : formalParameters ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ( THROWS qualifiedNameList )? SEMICOLON ;
    public final JWIPreprocessor_Parser.interfaceMethodDeclaratorRest_return interfaceMethodDeclaratorRest() throws RecognitionException {
        JWIPreprocessor_Parser.interfaceMethodDeclaratorRest_return retval = new JWIPreprocessor_Parser.interfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int interfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 45) ) { return retval; }
            // JWIPreprocessor_Parser.g:617:5: ( formalParameters ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ( THROWS qualifiedNameList )? SEMICOLON )
            // JWIPreprocessor_Parser.g:617:9: formalParameters ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ( THROWS qualifiedNameList )? SEMICOLON
            {
            pushFollow(FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest2592);
            formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:617:26: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==LEFTSQUAREBRACKET) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:617:27: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
            	    {
            	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_interfaceMethodDeclaratorRest2595); if (state.failed) return retval;
            	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_interfaceMethodDeclaratorRest2597); if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop68;
                }
            } while (true);

            // JWIPreprocessor_Parser.g:617:66: ( THROWS qualifiedNameList )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==THROWS) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // JWIPreprocessor_Parser.g:617:67: THROWS qualifiedNameList
                    {
                    match(input,THROWS,FOLLOW_THROWS_in_interfaceMethodDeclaratorRest2602); if (state.failed) return retval;
                    pushFollow(FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest2604);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_interfaceMethodDeclaratorRest2608); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 45, interfaceMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceMethodDeclaratorRest"

    public static class interfaceGenericMethodDecl_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "interfaceGenericMethodDecl"
    // JWIPreprocessor_Parser.g:620:1: interfaceGenericMethodDecl : typeParameters ( type | VOID ) Identifier interfaceMethodDeclaratorRest ;
    public final JWIPreprocessor_Parser.interfaceGenericMethodDecl_return interfaceGenericMethodDecl() throws RecognitionException {
        JWIPreprocessor_Parser.interfaceGenericMethodDecl_return retval = new JWIPreprocessor_Parser.interfaceGenericMethodDecl_return();
        retval.start = input.LT(1);
        int interfaceGenericMethodDecl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 46) ) { return retval; }
            // JWIPreprocessor_Parser.g:621:5: ( typeParameters ( type | VOID ) Identifier interfaceMethodDeclaratorRest )
            // JWIPreprocessor_Parser.g:621:9: typeParameters ( type | VOID ) Identifier interfaceMethodDeclaratorRest
            {
            pushFollow(FOLLOW_typeParameters_in_interfaceGenericMethodDecl2631);
            typeParameters();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:621:24: ( type | VOID )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==BOOLEAN||LA70_0==BYTE||LA70_0==CHAR||LA70_0==DOUBLE||LA70_0==FLOAT||LA70_0==INT||LA70_0==LONG||LA70_0==SHORT||LA70_0==Identifier) ) {
                alt70=1;
            }
            else if ( (LA70_0==VOID) ) {
                alt70=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // JWIPreprocessor_Parser.g:621:25: type
                    {
                    pushFollow(FOLLOW_type_in_interfaceGenericMethodDecl2634);
                    type();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:621:32: VOID
                    {
                    match(input,VOID,FOLLOW_VOID_in_interfaceGenericMethodDecl2638); if (state.failed) return retval;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_interfaceGenericMethodDecl2641); if (state.failed) return retval;
            pushFollow(FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl2651);
            interfaceMethodDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 46, interfaceGenericMethodDecl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "interfaceGenericMethodDecl"

    public static class voidInterfaceMethodDeclaratorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "voidInterfaceMethodDeclaratorRest"
    // JWIPreprocessor_Parser.g:625:1: voidInterfaceMethodDeclaratorRest : formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? SEMICOLON ;
    public final JWIPreprocessor_Parser.voidInterfaceMethodDeclaratorRest_return voidInterfaceMethodDeclaratorRest() throws RecognitionException {
        JWIPreprocessor_Parser.voidInterfaceMethodDeclaratorRest_return retval = new JWIPreprocessor_Parser.voidInterfaceMethodDeclaratorRest_return();
        retval.start = input.LT(1);
        int voidInterfaceMethodDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 47) ) { return retval; }
            // JWIPreprocessor_Parser.g:626:5: ( formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? SEMICOLON )
            // JWIPreprocessor_Parser.g:626:9: formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? SEMICOLON
            {
            pushFollow(FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest2674);
            formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:627:9: ( methodImplementsIntentionClause )?
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==IMPLEMENTSINTENTION) ) {
                alt71=1;
            }
            switch (alt71) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: methodImplementsIntentionClause
                    {
                    pushFollow(FOLLOW_methodImplementsIntentionClause_in_voidInterfaceMethodDeclaratorRest2684);
                    methodImplementsIntentionClause();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:628:9: ( THROWS qualifiedNameList )?
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==THROWS) ) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // JWIPreprocessor_Parser.g:628:10: THROWS qualifiedNameList
                    {
                    match(input,THROWS,FOLLOW_THROWS_in_voidInterfaceMethodDeclaratorRest2696); if (state.failed) return retval;
                    pushFollow(FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest2698);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_voidInterfaceMethodDeclaratorRest2702); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 47, voidInterfaceMethodDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "voidInterfaceMethodDeclaratorRest"

    public static class constructorDeclaratorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "constructorDeclaratorRest"
    // JWIPreprocessor_Parser.g:631:1: constructorDeclaratorRest : formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? constructorBody ;
    public final JWIPreprocessor_Parser.constructorDeclaratorRest_return constructorDeclaratorRest() throws RecognitionException {
        JWIPreprocessor_Parser.constructorDeclaratorRest_return retval = new JWIPreprocessor_Parser.constructorDeclaratorRest_return();
        retval.start = input.LT(1);
        int constructorDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 48) ) { return retval; }
            // JWIPreprocessor_Parser.g:632:5: ( formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? constructorBody )
            // JWIPreprocessor_Parser.g:632:9: formalParameters ( methodImplementsIntentionClause )? ( THROWS qualifiedNameList )? constructorBody
            {
            pushFollow(FOLLOW_formalParameters_in_constructorDeclaratorRest2725);
            formalParameters();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:633:9: ( methodImplementsIntentionClause )?
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==IMPLEMENTSINTENTION) ) {
                alt73=1;
            }
            switch (alt73) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: methodImplementsIntentionClause
                    {
                    pushFollow(FOLLOW_methodImplementsIntentionClause_in_constructorDeclaratorRest2735);
                    methodImplementsIntentionClause();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:634:9: ( THROWS qualifiedNameList )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==THROWS) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // JWIPreprocessor_Parser.g:634:10: THROWS qualifiedNameList
                    {
                    match(input,THROWS,FOLLOW_THROWS_in_constructorDeclaratorRest2747); if (state.failed) return retval;
                    pushFollow(FOLLOW_qualifiedNameList_in_constructorDeclaratorRest2749);
                    qualifiedNameList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            pushFollow(FOLLOW_constructorBody_in_constructorDeclaratorRest2753);
            constructorBody();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 48, constructorDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constructorDeclaratorRest"

    public static class constantDeclarator_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "constantDeclarator"
    // JWIPreprocessor_Parser.g:637:1: constantDeclarator : Identifier constantDeclaratorRest ;
    public final JWIPreprocessor_Parser.constantDeclarator_return constantDeclarator() throws RecognitionException {
        JWIPreprocessor_Parser.constantDeclarator_return retval = new JWIPreprocessor_Parser.constantDeclarator_return();
        retval.start = input.LT(1);
        int constantDeclarator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 49) ) { return retval; }
            // JWIPreprocessor_Parser.g:638:5: ( Identifier constantDeclaratorRest )
            // JWIPreprocessor_Parser.g:638:9: Identifier constantDeclaratorRest
            {
            match(input,Identifier,FOLLOW_Identifier_in_constantDeclarator2772); if (state.failed) return retval;
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclarator2774);
            constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 49, constantDeclarator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantDeclarator"

    public static class variableDeclarators_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "variableDeclarators"
    // JWIPreprocessor_Parser.g:641:1: variableDeclarators : variableDeclarator ( COMMA variableDeclarator )* ;
    public final JWIPreprocessor_Parser.variableDeclarators_return variableDeclarators() throws RecognitionException {
        JWIPreprocessor_Parser.variableDeclarators_return retval = new JWIPreprocessor_Parser.variableDeclarators_return();
        retval.start = input.LT(1);
        int variableDeclarators_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 50) ) { return retval; }
            // JWIPreprocessor_Parser.g:642:5: ( variableDeclarator ( COMMA variableDeclarator )* )
            // JWIPreprocessor_Parser.g:642:9: variableDeclarator ( COMMA variableDeclarator )*
            {
            pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators2797);
            variableDeclarator();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:642:28: ( COMMA variableDeclarator )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==COMMA) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:642:29: COMMA variableDeclarator
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_variableDeclarators2800); if (state.failed) return retval;
            	    pushFollow(FOLLOW_variableDeclarator_in_variableDeclarators2802);
            	    variableDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 50, variableDeclarators_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclarators"

    public static class variableDeclarator_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "variableDeclarator"
    // JWIPreprocessor_Parser.g:645:1: variableDeclarator : variableDeclaratorId ( ASSIGNMENT_EQUALS variableInitializer )? ;
    public final JWIPreprocessor_Parser.variableDeclarator_return variableDeclarator() throws RecognitionException {
        JWIPreprocessor_Parser.variableDeclarator_return retval = new JWIPreprocessor_Parser.variableDeclarator_return();
        retval.start = input.LT(1);
        int variableDeclarator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 51) ) { return retval; }
            // JWIPreprocessor_Parser.g:646:5: ( variableDeclaratorId ( ASSIGNMENT_EQUALS variableInitializer )? )
            // JWIPreprocessor_Parser.g:646:9: variableDeclaratorId ( ASSIGNMENT_EQUALS variableInitializer )?
            {
            pushFollow(FOLLOW_variableDeclaratorId_in_variableDeclarator2823);
            variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:646:30: ( ASSIGNMENT_EQUALS variableInitializer )?
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==ASSIGNMENT_EQUALS) ) {
                alt76=1;
            }
            switch (alt76) {
                case 1 :
                    // JWIPreprocessor_Parser.g:646:31: ASSIGNMENT_EQUALS variableInitializer
                    {
                    match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_variableDeclarator2826); if (state.failed) return retval;
                    pushFollow(FOLLOW_variableInitializer_in_variableDeclarator2828);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 51, variableDeclarator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclarator"

    public static class constantDeclaratorsRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "constantDeclaratorsRest"
    // JWIPreprocessor_Parser.g:649:1: constantDeclaratorsRest : constantDeclaratorRest ( COMMA constantDeclarator )* ;
    public final JWIPreprocessor_Parser.constantDeclaratorsRest_return constantDeclaratorsRest() throws RecognitionException {
        JWIPreprocessor_Parser.constantDeclaratorsRest_return retval = new JWIPreprocessor_Parser.constantDeclaratorsRest_return();
        retval.start = input.LT(1);
        int constantDeclaratorsRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 52) ) { return retval; }
            // JWIPreprocessor_Parser.g:650:5: ( constantDeclaratorRest ( COMMA constantDeclarator )* )
            // JWIPreprocessor_Parser.g:650:9: constantDeclaratorRest ( COMMA constantDeclarator )*
            {
            pushFollow(FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest2853);
            constantDeclaratorRest();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:650:32: ( COMMA constantDeclarator )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==COMMA) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:650:33: COMMA constantDeclarator
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constantDeclaratorsRest2856); if (state.failed) return retval;
            	    pushFollow(FOLLOW_constantDeclarator_in_constantDeclaratorsRest2858);
            	    constantDeclarator();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 52, constantDeclaratorsRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantDeclaratorsRest"

    public static class constantDeclaratorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "constantDeclaratorRest"
    // JWIPreprocessor_Parser.g:653:1: constantDeclaratorRest : ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ASSIGNMENT_EQUALS variableInitializer ;
    public final JWIPreprocessor_Parser.constantDeclaratorRest_return constantDeclaratorRest() throws RecognitionException {
        JWIPreprocessor_Parser.constantDeclaratorRest_return retval = new JWIPreprocessor_Parser.constantDeclaratorRest_return();
        retval.start = input.LT(1);
        int constantDeclaratorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 53) ) { return retval; }
            // JWIPreprocessor_Parser.g:654:5: ( ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ASSIGNMENT_EQUALS variableInitializer )
            // JWIPreprocessor_Parser.g:654:9: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ASSIGNMENT_EQUALS variableInitializer
            {
            // JWIPreprocessor_Parser.g:654:9: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==LEFTSQUAREBRACKET) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:654:10: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
            	    {
            	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_constantDeclaratorRest2880); if (state.failed) return retval;
            	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_constantDeclaratorRest2882); if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);

            match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_constantDeclaratorRest2886); if (state.failed) return retval;
            pushFollow(FOLLOW_variableInitializer_in_constantDeclaratorRest2888);
            variableInitializer();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 53, constantDeclaratorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantDeclaratorRest"

    public static class variableDeclaratorId_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "variableDeclaratorId"
    // JWIPreprocessor_Parser.g:657:1: variableDeclaratorId : Identifier ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ;
    public final JWIPreprocessor_Parser.variableDeclaratorId_return variableDeclaratorId() throws RecognitionException {
        JWIPreprocessor_Parser.variableDeclaratorId_return retval = new JWIPreprocessor_Parser.variableDeclaratorId_return();
        retval.start = input.LT(1);
        int variableDeclaratorId_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 54) ) { return retval; }
            // JWIPreprocessor_Parser.g:658:5: ( Identifier ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* )
            // JWIPreprocessor_Parser.g:658:9: Identifier ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_variableDeclaratorId2911); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:658:20: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( (LA79_0==LEFTSQUAREBRACKET) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:658:21: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
            	    {
            	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_variableDeclaratorId2914); if (state.failed) return retval;
            	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_variableDeclaratorId2916); if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop79;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 54, variableDeclaratorId_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableDeclaratorId"

    public static class variableInitializer_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "variableInitializer"
    // JWIPreprocessor_Parser.g:661:1: variableInitializer : ( arrayInitializer | expression );
    public final JWIPreprocessor_Parser.variableInitializer_return variableInitializer() throws RecognitionException {
        JWIPreprocessor_Parser.variableInitializer_return retval = new JWIPreprocessor_Parser.variableInitializer_return();
        retval.start = input.LT(1);
        int variableInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 55) ) { return retval; }
            // JWIPreprocessor_Parser.g:662:5: ( arrayInitializer | expression )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==LEFTBRACE) ) {
                alt80=1;
            }
            else if ( (LA80_0==BOOLEAN||LA80_0==BYTE||LA80_0==CHAR||LA80_0==DOUBLE||LA80_0==FALSE||LA80_0==FLOAT||LA80_0==INT||LA80_0==LONG||(LA80_0>=NEW && LA80_0<=NULL)||LA80_0==SHORT||LA80_0==SUPER||LA80_0==THIS||LA80_0==TRUE||LA80_0==VOID||LA80_0==LEFTPARENTHESIS||LA80_0==TILDE||(LA80_0>=PLUSPLUS && LA80_0<=PLUS)||(LA80_0>=MINUSMINUS && LA80_0<=MINUS)||LA80_0==EXCLAMATIONMARK||(LA80_0>=HexLiteral && LA80_0<=OctalLiteral)||LA80_0==FloatingPointLiteral||(LA80_0>=CharacterLiteral && LA80_0<=StringLiteral)||LA80_0==Identifier) ) {
                alt80=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // JWIPreprocessor_Parser.g:662:9: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer2937);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:663:9: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer2947);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 55, variableInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableInitializer"

    public static class arrayInitializer_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "arrayInitializer"
    // JWIPreprocessor_Parser.g:666:1: arrayInitializer : LEFTBRACE ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHTBRACE ;
    public final JWIPreprocessor_Parser.arrayInitializer_return arrayInitializer() throws RecognitionException {
        JWIPreprocessor_Parser.arrayInitializer_return retval = new JWIPreprocessor_Parser.arrayInitializer_return();
        retval.start = input.LT(1);
        int arrayInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 56) ) { return retval; }
            // JWIPreprocessor_Parser.g:667:5: ( LEFTBRACE ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHTBRACE )
            // JWIPreprocessor_Parser.g:667:9: LEFTBRACE ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_arrayInitializer2974); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:667:19: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt83=2;
            int LA83_0 = input.LA(1);

            if ( (LA83_0==BOOLEAN||LA83_0==BYTE||LA83_0==CHAR||LA83_0==DOUBLE||LA83_0==FALSE||LA83_0==FLOAT||LA83_0==INT||LA83_0==LONG||(LA83_0>=NEW && LA83_0<=NULL)||LA83_0==SHORT||LA83_0==SUPER||LA83_0==THIS||LA83_0==TRUE||LA83_0==VOID||LA83_0==LEFTBRACE||LA83_0==LEFTPARENTHESIS||LA83_0==TILDE||(LA83_0>=PLUSPLUS && LA83_0<=PLUS)||(LA83_0>=MINUSMINUS && LA83_0<=MINUS)||LA83_0==EXCLAMATIONMARK||(LA83_0>=HexLiteral && LA83_0<=OctalLiteral)||LA83_0==FloatingPointLiteral||(LA83_0>=CharacterLiteral && LA83_0<=StringLiteral)||LA83_0==Identifier) ) {
                alt83=1;
            }
            switch (alt83) {
                case 1 :
                    // JWIPreprocessor_Parser.g:667:20: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2977);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:667:40: ( COMMA variableInitializer )*
                    loop81:
                    do {
                        int alt81=2;
                        int LA81_0 = input.LA(1);

                        if ( (LA81_0==COMMA) ) {
                            int LA81_1 = input.LA(2);

                            if ( (LA81_1==BOOLEAN||LA81_1==BYTE||LA81_1==CHAR||LA81_1==DOUBLE||LA81_1==FALSE||LA81_1==FLOAT||LA81_1==INT||LA81_1==LONG||(LA81_1>=NEW && LA81_1<=NULL)||LA81_1==SHORT||LA81_1==SUPER||LA81_1==THIS||LA81_1==TRUE||LA81_1==VOID||LA81_1==LEFTBRACE||LA81_1==LEFTPARENTHESIS||LA81_1==TILDE||(LA81_1>=PLUSPLUS && LA81_1<=PLUS)||(LA81_1>=MINUSMINUS && LA81_1<=MINUS)||LA81_1==EXCLAMATIONMARK||(LA81_1>=HexLiteral && LA81_1<=OctalLiteral)||LA81_1==FloatingPointLiteral||(LA81_1>=CharacterLiteral && LA81_1<=StringLiteral)||LA81_1==Identifier) ) {
                                alt81=1;
                            }


                        }


                        switch (alt81) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:667:41: COMMA variableInitializer
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer2980); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2982);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop81;
                        }
                    } while (true);

                    // JWIPreprocessor_Parser.g:667:69: ( COMMA )?
                    int alt82=2;
                    int LA82_0 = input.LA(1);

                    if ( (LA82_0==COMMA) ) {
                        alt82=1;
                    }
                    switch (alt82) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:667:70: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer2987); if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_arrayInitializer2994); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 56, arrayInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arrayInitializer"

    public static class modifier_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "modifier"
    // JWIPreprocessor_Parser.g:670:1: modifier : ( annotation | PUBLIC | PROTECTED | PRIVATE | STATIC | ABSTRACT | FINAL | NATIVE | SYNCHRONIZED | TRANSIENT | VOLATIVE | STRICTFP );
    public final JWIPreprocessor_Parser.modifier_return modifier() throws RecognitionException {
        JWIPreprocessor_Parser.modifier_return retval = new JWIPreprocessor_Parser.modifier_return();
        retval.start = input.LT(1);
        int modifier_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 57) ) { return retval; }
            // JWIPreprocessor_Parser.g:671:5: ( annotation | PUBLIC | PROTECTED | PRIVATE | STATIC | ABSTRACT | FINAL | NATIVE | SYNCHRONIZED | TRANSIENT | VOLATIVE | STRICTFP )
            int alt84=12;
            switch ( input.LA(1) ) {
            case ATSIGN:
                {
                alt84=1;
                }
                break;
            case PUBLIC:
                {
                alt84=2;
                }
                break;
            case PROTECTED:
                {
                alt84=3;
                }
                break;
            case PRIVATE:
                {
                alt84=4;
                }
                break;
            case STATIC:
                {
                alt84=5;
                }
                break;
            case ABSTRACT:
                {
                alt84=6;
                }
                break;
            case FINAL:
                {
                alt84=7;
                }
                break;
            case NATIVE:
                {
                alt84=8;
                }
                break;
            case SYNCHRONIZED:
                {
                alt84=9;
                }
                break;
            case TRANSIENT:
                {
                alt84=10;
                }
                break;
            case VOLATIVE:
                {
                alt84=11;
                }
                break;
            case STRICTFP:
                {
                alt84=12;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // JWIPreprocessor_Parser.g:671:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_modifier3013);
                    annotation();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:672:9: PUBLIC
                    {
                    match(input,PUBLIC,FOLLOW_PUBLIC_in_modifier3023); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:673:9: PROTECTED
                    {
                    match(input,PROTECTED,FOLLOW_PROTECTED_in_modifier3033); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:674:9: PRIVATE
                    {
                    match(input,PRIVATE,FOLLOW_PRIVATE_in_modifier3043); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:675:9: STATIC
                    {
                    match(input,STATIC,FOLLOW_STATIC_in_modifier3053); if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // JWIPreprocessor_Parser.g:676:9: ABSTRACT
                    {
                    match(input,ABSTRACT,FOLLOW_ABSTRACT_in_modifier3063); if (state.failed) return retval;

                    }
                    break;
                case 7 :
                    // JWIPreprocessor_Parser.g:677:9: FINAL
                    {
                    match(input,FINAL,FOLLOW_FINAL_in_modifier3073); if (state.failed) return retval;

                    }
                    break;
                case 8 :
                    // JWIPreprocessor_Parser.g:678:9: NATIVE
                    {
                    match(input,NATIVE,FOLLOW_NATIVE_in_modifier3083); if (state.failed) return retval;

                    }
                    break;
                case 9 :
                    // JWIPreprocessor_Parser.g:679:9: SYNCHRONIZED
                    {
                    match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_modifier3093); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // JWIPreprocessor_Parser.g:680:9: TRANSIENT
                    {
                    match(input,TRANSIENT,FOLLOW_TRANSIENT_in_modifier3103); if (state.failed) return retval;

                    }
                    break;
                case 11 :
                    // JWIPreprocessor_Parser.g:681:9: VOLATIVE
                    {
                    match(input,VOLATIVE,FOLLOW_VOLATIVE_in_modifier3113); if (state.failed) return retval;

                    }
                    break;
                case 12 :
                    // JWIPreprocessor_Parser.g:682:9: STRICTFP
                    {
                    match(input,STRICTFP,FOLLOW_STRICTFP_in_modifier3123); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 57, modifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "modifier"

    public static class packageOrTypeName_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "packageOrTypeName"
    // JWIPreprocessor_Parser.g:685:1: packageOrTypeName : qualifiedName ;
    public final JWIPreprocessor_Parser.packageOrTypeName_return packageOrTypeName() throws RecognitionException {
        JWIPreprocessor_Parser.packageOrTypeName_return retval = new JWIPreprocessor_Parser.packageOrTypeName_return();
        retval.start = input.LT(1);
        int packageOrTypeName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 58) ) { return retval; }
            // JWIPreprocessor_Parser.g:686:5: ( qualifiedName )
            // JWIPreprocessor_Parser.g:686:9: qualifiedName
            {
            pushFollow(FOLLOW_qualifiedName_in_packageOrTypeName3142);
            qualifiedName();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 58, packageOrTypeName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "packageOrTypeName"

    public static class enumConstantName_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "enumConstantName"
    // JWIPreprocessor_Parser.g:689:1: enumConstantName : Identifier ;
    public final JWIPreprocessor_Parser.enumConstantName_return enumConstantName() throws RecognitionException {
        JWIPreprocessor_Parser.enumConstantName_return retval = new JWIPreprocessor_Parser.enumConstantName_return();
        retval.start = input.LT(1);
        int enumConstantName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 59) ) { return retval; }
            // JWIPreprocessor_Parser.g:690:5: ( Identifier )
            // JWIPreprocessor_Parser.g:690:9: Identifier
            {
            match(input,Identifier,FOLLOW_Identifier_in_enumConstantName3161); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 59, enumConstantName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enumConstantName"

    public static class typeName_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "typeName"
    // JWIPreprocessor_Parser.g:693:1: typeName : qualifiedName ;
    public final JWIPreprocessor_Parser.typeName_return typeName() throws RecognitionException {
        JWIPreprocessor_Parser.typeName_return retval = new JWIPreprocessor_Parser.typeName_return();
        retval.start = input.LT(1);
        int typeName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 60) ) { return retval; }
            // JWIPreprocessor_Parser.g:694:5: ( qualifiedName )
            // JWIPreprocessor_Parser.g:694:9: qualifiedName
            {
            pushFollow(FOLLOW_qualifiedName_in_typeName3180);
            qualifiedName();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 60, typeName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeName"

    public static class type_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "type"
    // JWIPreprocessor_Parser.g:697:1: type : ( classOrInterfaceType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* | primitiveType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* );
    public final JWIPreprocessor_Parser.type_return type() throws RecognitionException {
        JWIPreprocessor_Parser.type_return retval = new JWIPreprocessor_Parser.type_return();
        retval.start = input.LT(1);
        int type_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 61) ) { return retval; }
            // JWIPreprocessor_Parser.g:698:2: ( classOrInterfaceType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* | primitiveType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* )
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==Identifier) ) {
                alt87=1;
            }
            else if ( (LA87_0==BOOLEAN||LA87_0==BYTE||LA87_0==CHAR||LA87_0==DOUBLE||LA87_0==FLOAT||LA87_0==INT||LA87_0==LONG||LA87_0==SHORT) ) {
                alt87=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 87, 0, input);

                throw nvae;
            }
            switch (alt87) {
                case 1 :
                    // JWIPreprocessor_Parser.g:698:4: classOrInterfaceType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
                    {
                    pushFollow(FOLLOW_classOrInterfaceType_in_type3194);
                    classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:698:25: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
                    loop85:
                    do {
                        int alt85=2;
                        int LA85_0 = input.LA(1);

                        if ( (LA85_0==LEFTSQUAREBRACKET) ) {
                            alt85=1;
                        }


                        switch (alt85) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:698:26: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
                    	    {
                    	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_type3197); if (state.failed) return retval;
                    	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_type3199); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop85;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:699:4: primitiveType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type3206);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:699:18: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
                    loop86:
                    do {
                        int alt86=2;
                        int LA86_0 = input.LA(1);

                        if ( (LA86_0==LEFTSQUAREBRACKET) ) {
                            alt86=1;
                        }


                        switch (alt86) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:699:19: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
                    	    {
                    	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_type3209); if (state.failed) return retval;
                    	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_type3211); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop86;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 61, type_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "type"

    public static class classOrInterfaceType_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classOrInterfaceType"
    // JWIPreprocessor_Parser.g:702:1: classOrInterfaceType : Identifier ( typeArguments )? ( DOT Identifier ( typeArguments )? )* ;
    public final JWIPreprocessor_Parser.classOrInterfaceType_return classOrInterfaceType() throws RecognitionException {
        JWIPreprocessor_Parser.classOrInterfaceType_return retval = new JWIPreprocessor_Parser.classOrInterfaceType_return();
        retval.start = input.LT(1);
        int classOrInterfaceType_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 62) ) { return retval; }
            // JWIPreprocessor_Parser.g:703:2: ( Identifier ( typeArguments )? ( DOT Identifier ( typeArguments )? )* )
            // JWIPreprocessor_Parser.g:703:4: Identifier ( typeArguments )? ( DOT Identifier ( typeArguments )? )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType3224); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:703:15: ( typeArguments )?
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==LESSTHAN) ) {
                int LA88_1 = input.LA(2);

                if ( (LA88_1==BOOLEAN||LA88_1==BYTE||LA88_1==CHAR||LA88_1==DOUBLE||LA88_1==FLOAT||LA88_1==INT||LA88_1==LONG||LA88_1==SHORT||LA88_1==QUESTIONMARK||LA88_1==Identifier) ) {
                    alt88=1;
                }
            }
            switch (alt88) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: typeArguments
                    {
                    pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType3226);
                    typeArguments();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:703:30: ( DOT Identifier ( typeArguments )? )*
            loop90:
            do {
                int alt90=2;
                int LA90_0 = input.LA(1);

                if ( (LA90_0==DOT) ) {
                    alt90=1;
                }


                switch (alt90) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:703:31: DOT Identifier ( typeArguments )?
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_classOrInterfaceType3230); if (state.failed) return retval;
            	    match(input,Identifier,FOLLOW_Identifier_in_classOrInterfaceType3232); if (state.failed) return retval;
            	    // JWIPreprocessor_Parser.g:703:46: ( typeArguments )?
            	    int alt89=2;
            	    int LA89_0 = input.LA(1);

            	    if ( (LA89_0==LESSTHAN) ) {
            	        int LA89_1 = input.LA(2);

            	        if ( (LA89_1==BOOLEAN||LA89_1==BYTE||LA89_1==CHAR||LA89_1==DOUBLE||LA89_1==FLOAT||LA89_1==INT||LA89_1==LONG||LA89_1==SHORT||LA89_1==QUESTIONMARK||LA89_1==Identifier) ) {
            	            alt89=1;
            	        }
            	    }
            	    switch (alt89) {
            	        case 1 :
            	            // JWIPreprocessor_Parser.g:0:0: typeArguments
            	            {
            	            pushFollow(FOLLOW_typeArguments_in_classOrInterfaceType3234);
            	            typeArguments();

            	            state._fsp--;
            	            if (state.failed) return retval;

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop90;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 62, classOrInterfaceType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classOrInterfaceType"

    public static class primitiveType_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "primitiveType"
    // JWIPreprocessor_Parser.g:706:1: primitiveType : ( BOOLEAN | CHAR | BYTE | SHORT | INT | LONG | FLOAT | DOUBLE );
    public final JWIPreprocessor_Parser.primitiveType_return primitiveType() throws RecognitionException {
        JWIPreprocessor_Parser.primitiveType_return retval = new JWIPreprocessor_Parser.primitiveType_return();
        retval.start = input.LT(1);
        int primitiveType_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 63) ) { return retval; }
            // JWIPreprocessor_Parser.g:707:5: ( BOOLEAN | CHAR | BYTE | SHORT | INT | LONG | FLOAT | DOUBLE )
            // JWIPreprocessor_Parser.g:
            {
            if ( input.LA(1)==BOOLEAN||input.LA(1)==BYTE||input.LA(1)==CHAR||input.LA(1)==DOUBLE||input.LA(1)==FLOAT||input.LA(1)==INT||input.LA(1)==LONG||input.LA(1)==SHORT ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 63, primitiveType_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primitiveType"

    public static class variableModifier_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "variableModifier"
    // JWIPreprocessor_Parser.g:717:1: variableModifier : ( FINAL | annotation );
    public final JWIPreprocessor_Parser.variableModifier_return variableModifier() throws RecognitionException {
        JWIPreprocessor_Parser.variableModifier_return retval = new JWIPreprocessor_Parser.variableModifier_return();
        retval.start = input.LT(1);
        int variableModifier_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 64) ) { return retval; }
            // JWIPreprocessor_Parser.g:718:5: ( FINAL | annotation )
            int alt91=2;
            int LA91_0 = input.LA(1);

            if ( (LA91_0==FINAL) ) {
                alt91=1;
            }
            else if ( (LA91_0==ATSIGN) ) {
                alt91=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 91, 0, input);

                throw nvae;
            }
            switch (alt91) {
                case 1 :
                    // JWIPreprocessor_Parser.g:718:9: FINAL
                    {
                    match(input,FINAL,FOLLOW_FINAL_in_variableModifier3343); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:719:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_variableModifier3353);
                    annotation();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 64, variableModifier_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableModifier"

    public static class typeArguments_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "typeArguments"
    // JWIPreprocessor_Parser.g:722:1: typeArguments : LESSTHAN typeArgument ( COMMA typeArgument )* GREATERTHAN ;
    public final JWIPreprocessor_Parser.typeArguments_return typeArguments() throws RecognitionException {
        JWIPreprocessor_Parser.typeArguments_return retval = new JWIPreprocessor_Parser.typeArguments_return();
        retval.start = input.LT(1);
        int typeArguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 65) ) { return retval; }
            // JWIPreprocessor_Parser.g:723:5: ( LESSTHAN typeArgument ( COMMA typeArgument )* GREATERTHAN )
            // JWIPreprocessor_Parser.g:723:9: LESSTHAN typeArgument ( COMMA typeArgument )* GREATERTHAN
            {
            match(input,LESSTHAN,FOLLOW_LESSTHAN_in_typeArguments3372); if (state.failed) return retval;
            pushFollow(FOLLOW_typeArgument_in_typeArguments3374);
            typeArgument();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:723:31: ( COMMA typeArgument )*
            loop92:
            do {
                int alt92=2;
                int LA92_0 = input.LA(1);

                if ( (LA92_0==COMMA) ) {
                    alt92=1;
                }


                switch (alt92) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:723:32: COMMA typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments3377); if (state.failed) return retval;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments3379);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop92;
                }
            } while (true);

            match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_typeArguments3383); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 65, typeArguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeArguments"

    public static class typeArgument_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "typeArgument"
    // JWIPreprocessor_Parser.g:726:1: typeArgument : ( type | QUESTIONMARK ( ( EXTENDS | SUPER ) type )? );
    public final JWIPreprocessor_Parser.typeArgument_return typeArgument() throws RecognitionException {
        JWIPreprocessor_Parser.typeArgument_return retval = new JWIPreprocessor_Parser.typeArgument_return();
        retval.start = input.LT(1);
        int typeArgument_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 66) ) { return retval; }
            // JWIPreprocessor_Parser.g:727:5: ( type | QUESTIONMARK ( ( EXTENDS | SUPER ) type )? )
            int alt94=2;
            int LA94_0 = input.LA(1);

            if ( (LA94_0==BOOLEAN||LA94_0==BYTE||LA94_0==CHAR||LA94_0==DOUBLE||LA94_0==FLOAT||LA94_0==INT||LA94_0==LONG||LA94_0==SHORT||LA94_0==Identifier) ) {
                alt94=1;
            }
            else if ( (LA94_0==QUESTIONMARK) ) {
                alt94=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 94, 0, input);

                throw nvae;
            }
            switch (alt94) {
                case 1 :
                    // JWIPreprocessor_Parser.g:727:9: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument3406);
                    type();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:728:9: QUESTIONMARK ( ( EXTENDS | SUPER ) type )?
                    {
                    match(input,QUESTIONMARK,FOLLOW_QUESTIONMARK_in_typeArgument3416); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:728:22: ( ( EXTENDS | SUPER ) type )?
                    int alt93=2;
                    int LA93_0 = input.LA(1);

                    if ( (LA93_0==EXTENDS||LA93_0==SUPER) ) {
                        alt93=1;
                    }
                    switch (alt93) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:728:23: ( EXTENDS | SUPER ) type
                            {
                            if ( input.LA(1)==EXTENDS||input.LA(1)==SUPER ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }

                            pushFollow(FOLLOW_type_in_typeArgument3427);
                            type();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 66, typeArgument_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "typeArgument"

    public static class qualifiedNameList_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "qualifiedNameList"
    // JWIPreprocessor_Parser.g:731:1: qualifiedNameList : qualifiedName ( COMMA qualifiedName )* ;
    public final JWIPreprocessor_Parser.qualifiedNameList_return qualifiedNameList() throws RecognitionException {
        JWIPreprocessor_Parser.qualifiedNameList_return retval = new JWIPreprocessor_Parser.qualifiedNameList_return();
        retval.start = input.LT(1);
        int qualifiedNameList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 67) ) { return retval; }
            // JWIPreprocessor_Parser.g:732:5: ( qualifiedName ( COMMA qualifiedName )* )
            // JWIPreprocessor_Parser.g:732:9: qualifiedName ( COMMA qualifiedName )*
            {
            pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3452);
            qualifiedName();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:732:23: ( COMMA qualifiedName )*
            loop95:
            do {
                int alt95=2;
                int LA95_0 = input.LA(1);

                if ( (LA95_0==COMMA) ) {
                    alt95=1;
                }


                switch (alt95) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:732:24: COMMA qualifiedName
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_qualifiedNameList3455); if (state.failed) return retval;
            	    pushFollow(FOLLOW_qualifiedName_in_qualifiedNameList3457);
            	    qualifiedName();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop95;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 67, qualifiedNameList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedNameList"

    public static class formalParameters_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "formalParameters"
    // JWIPreprocessor_Parser.g:735:1: formalParameters : LEFTPARENTHESIS ( formalParameterDecls )? RIGHTPARENTHESIS ;
    public final JWIPreprocessor_Parser.formalParameters_return formalParameters() throws RecognitionException {
        JWIPreprocessor_Parser.formalParameters_return retval = new JWIPreprocessor_Parser.formalParameters_return();
        retval.start = input.LT(1);
        int formalParameters_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 68) ) { return retval; }
            // JWIPreprocessor_Parser.g:736:5: ( LEFTPARENTHESIS ( formalParameterDecls )? RIGHTPARENTHESIS )
            // JWIPreprocessor_Parser.g:736:9: LEFTPARENTHESIS ( formalParameterDecls )? RIGHTPARENTHESIS
            {
            match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_formalParameters3478); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:736:25: ( formalParameterDecls )?
            int alt96=2;
            int LA96_0 = input.LA(1);

            if ( (LA96_0==BOOLEAN||LA96_0==BYTE||LA96_0==CHAR||LA96_0==DOUBLE||LA96_0==FINAL||LA96_0==FLOAT||LA96_0==INT||LA96_0==LONG||LA96_0==SHORT||LA96_0==ATSIGN||LA96_0==Identifier) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: formalParameterDecls
                    {
                    pushFollow(FOLLOW_formalParameterDecls_in_formalParameters3480);
                    formalParameterDecls();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_formalParameters3483); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 68, formalParameters_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameters"

    public static class formalParameterDecls_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "formalParameterDecls"
    // JWIPreprocessor_Parser.g:739:1: formalParameterDecls : variableModifiers type formalParameterDeclsRest ;
    public final JWIPreprocessor_Parser.formalParameterDecls_return formalParameterDecls() throws RecognitionException {
        JWIPreprocessor_Parser.formalParameterDecls_return retval = new JWIPreprocessor_Parser.formalParameterDecls_return();
        retval.start = input.LT(1);
        int formalParameterDecls_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 69) ) { return retval; }
            // JWIPreprocessor_Parser.g:740:5: ( variableModifiers type formalParameterDeclsRest )
            // JWIPreprocessor_Parser.g:740:9: variableModifiers type formalParameterDeclsRest
            {
            pushFollow(FOLLOW_variableModifiers_in_formalParameterDecls3506);
            variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_type_in_formalParameterDecls3508);
            type();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_formalParameterDeclsRest_in_formalParameterDecls3510);
            formalParameterDeclsRest();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 69, formalParameterDecls_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameterDecls"

    public static class formalParameterDeclsRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "formalParameterDeclsRest"
    // JWIPreprocessor_Parser.g:743:1: formalParameterDeclsRest : ( variableDeclaratorId ( COMMA formalParameterDecls )? | ELLIPSIS variableDeclaratorId );
    public final JWIPreprocessor_Parser.formalParameterDeclsRest_return formalParameterDeclsRest() throws RecognitionException {
        JWIPreprocessor_Parser.formalParameterDeclsRest_return retval = new JWIPreprocessor_Parser.formalParameterDeclsRest_return();
        retval.start = input.LT(1);
        int formalParameterDeclsRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 70) ) { return retval; }
            // JWIPreprocessor_Parser.g:744:5: ( variableDeclaratorId ( COMMA formalParameterDecls )? | ELLIPSIS variableDeclaratorId )
            int alt98=2;
            int LA98_0 = input.LA(1);

            if ( (LA98_0==Identifier) ) {
                alt98=1;
            }
            else if ( (LA98_0==ELLIPSIS) ) {
                alt98=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 98, 0, input);

                throw nvae;
            }
            switch (alt98) {
                case 1 :
                    // JWIPreprocessor_Parser.g:744:9: variableDeclaratorId ( COMMA formalParameterDecls )?
                    {
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3533);
                    variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:744:30: ( COMMA formalParameterDecls )?
                    int alt97=2;
                    int LA97_0 = input.LA(1);

                    if ( (LA97_0==COMMA) ) {
                        alt97=1;
                    }
                    switch (alt97) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:744:31: COMMA formalParameterDecls
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_formalParameterDeclsRest3536); if (state.failed) return retval;
                            pushFollow(FOLLOW_formalParameterDecls_in_formalParameterDeclsRest3538);
                            formalParameterDecls();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:745:9: ELLIPSIS variableDeclaratorId
                    {
                    match(input,ELLIPSIS,FOLLOW_ELLIPSIS_in_formalParameterDeclsRest3550); if (state.failed) return retval;
                    pushFollow(FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3552);
                    variableDeclaratorId();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 70, formalParameterDeclsRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameterDeclsRest"

    public static class methodBody_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "methodBody"
    // JWIPreprocessor_Parser.g:748:1: methodBody : block ;
    public final JWIPreprocessor_Parser.methodBody_return methodBody() throws RecognitionException {
        JWIPreprocessor_Parser.methodBody_return retval = new JWIPreprocessor_Parser.methodBody_return();
        retval.start = input.LT(1);
        int methodBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 71) ) { return retval; }
            // JWIPreprocessor_Parser.g:749:5: ( block )
            // JWIPreprocessor_Parser.g:749:9: block
            {
            pushFollow(FOLLOW_block_in_methodBody3575);
            block();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 71, methodBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "methodBody"

    public static class constructorBody_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "constructorBody"
    // JWIPreprocessor_Parser.g:752:1: constructorBody : LEFTBRACE ( explicitConstructorInvocation )? ( blockStatement )* RIGHTBRACE ;
    public final JWIPreprocessor_Parser.constructorBody_return constructorBody() throws RecognitionException {
        JWIPreprocessor_Parser.constructorBody_return retval = new JWIPreprocessor_Parser.constructorBody_return();
        retval.start = input.LT(1);
        int constructorBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 72) ) { return retval; }
            // JWIPreprocessor_Parser.g:753:5: ( LEFTBRACE ( explicitConstructorInvocation )? ( blockStatement )* RIGHTBRACE )
            // JWIPreprocessor_Parser.g:753:9: LEFTBRACE ( explicitConstructorInvocation )? ( blockStatement )* RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_constructorBody3594); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:753:19: ( explicitConstructorInvocation )?
            int alt99=2;
            alt99 = dfa99.predict(input);
            switch (alt99) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: explicitConstructorInvocation
                    {
                    pushFollow(FOLLOW_explicitConstructorInvocation_in_constructorBody3596);
                    explicitConstructorInvocation();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:753:50: ( blockStatement )*
            loop100:
            do {
                int alt100=2;
                int LA100_0 = input.LA(1);

                if ( ((LA100_0>=ABSTRACT && LA100_0<=BYTE)||(LA100_0>=CHAR && LA100_0<=CONTINUE)||(LA100_0>=DO && LA100_0<=DOUBLE)||(LA100_0>=FALSE && LA100_0<=FINAL)||(LA100_0>=FLOAT && LA100_0<=IF)||(LA100_0>=INT && LA100_0<=LONG)||(LA100_0>=NEW && LA100_0<=NULL)||(LA100_0>=PRIVATE && LA100_0<=THROW)||(LA100_0>=TRUE && LA100_0<=VOID)||LA100_0==SEMICOLON||LA100_0==LEFTBRACE||LA100_0==LEFTPARENTHESIS||(LA100_0>=ATSIGN && LA100_0<=TILDE)||(LA100_0>=PLUSPLUS && LA100_0<=PLUS)||(LA100_0>=MINUSMINUS && LA100_0<=MINUS)||LA100_0==EXCLAMATIONMARK||(LA100_0>=HexLiteral && LA100_0<=OctalLiteral)||LA100_0==FloatingPointLiteral||(LA100_0>=CharacterLiteral && LA100_0<=StringLiteral)||(LA100_0>=ENUM && LA100_0<=ASSERT)||LA100_0==Identifier) ) {
                    alt100=1;
                }


                switch (alt100) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_constructorBody3599);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop100;
                }
            } while (true);

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_constructorBody3602); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 72, constructorBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constructorBody"

    public static class explicitConstructorInvocation_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "explicitConstructorInvocation"
    // JWIPreprocessor_Parser.g:756:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( THIS | SUPER ) arguments SEMICOLON | primary DOT ( nonWildcardTypeArguments )? SUPER arguments SEMICOLON );
    public final JWIPreprocessor_Parser.explicitConstructorInvocation_return explicitConstructorInvocation() throws RecognitionException {
        JWIPreprocessor_Parser.explicitConstructorInvocation_return retval = new JWIPreprocessor_Parser.explicitConstructorInvocation_return();
        retval.start = input.LT(1);
        int explicitConstructorInvocation_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 73) ) { return retval; }
            // JWIPreprocessor_Parser.g:757:5: ( ( nonWildcardTypeArguments )? ( THIS | SUPER ) arguments SEMICOLON | primary DOT ( nonWildcardTypeArguments )? SUPER arguments SEMICOLON )
            int alt103=2;
            alt103 = dfa103.predict(input);
            switch (alt103) {
                case 1 :
                    // JWIPreprocessor_Parser.g:757:9: ( nonWildcardTypeArguments )? ( THIS | SUPER ) arguments SEMICOLON
                    {
                    // JWIPreprocessor_Parser.g:757:9: ( nonWildcardTypeArguments )?
                    int alt101=2;
                    int LA101_0 = input.LA(1);

                    if ( (LA101_0==LESSTHAN) ) {
                        alt101=1;
                    }
                    switch (alt101) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3621);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
                        input.consume();
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3632);
                    arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_explicitConstructorInvocation3634); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:758:9: primary DOT ( nonWildcardTypeArguments )? SUPER arguments SEMICOLON
                    {
                    pushFollow(FOLLOW_primary_in_explicitConstructorInvocation3644);
                    primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,DOT,FOLLOW_DOT_in_explicitConstructorInvocation3646); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:758:21: ( nonWildcardTypeArguments )?
                    int alt102=2;
                    int LA102_0 = input.LA(1);

                    if ( (LA102_0==LESSTHAN) ) {
                        alt102=1;
                    }
                    switch (alt102) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3648);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,SUPER,FOLLOW_SUPER_in_explicitConstructorInvocation3651); if (state.failed) return retval;
                    pushFollow(FOLLOW_arguments_in_explicitConstructorInvocation3653);
                    arguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_explicitConstructorInvocation3655); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 73, explicitConstructorInvocation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "explicitConstructorInvocation"

    public static class qualifiedName_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "qualifiedName"
    // JWIPreprocessor_Parser.g:762:1: qualifiedName : Identifier ( DOT Identifier )* ;
    public final JWIPreprocessor_Parser.qualifiedName_return qualifiedName() throws RecognitionException {
        JWIPreprocessor_Parser.qualifiedName_return retval = new JWIPreprocessor_Parser.qualifiedName_return();
        retval.start = input.LT(1);
        int qualifiedName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 74) ) { return retval; }
            // JWIPreprocessor_Parser.g:763:5: ( Identifier ( DOT Identifier )* )
            // JWIPreprocessor_Parser.g:763:9: Identifier ( DOT Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_qualifiedName3675); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:763:20: ( DOT Identifier )*
            loop104:
            do {
                int alt104=2;
                int LA104_0 = input.LA(1);

                if ( (LA104_0==DOT) ) {
                    int LA104_2 = input.LA(2);

                    if ( (LA104_2==Identifier) ) {
                        alt104=1;
                    }


                }


                switch (alt104) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:763:21: DOT Identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_qualifiedName3678); if (state.failed) return retval;
            	    match(input,Identifier,FOLLOW_Identifier_in_qualifiedName3680); if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop104;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 74, qualifiedName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "qualifiedName"

    public static class literal_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "literal"
    // JWIPreprocessor_Parser.g:766:1: literal : ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | NULL );
    public final JWIPreprocessor_Parser.literal_return literal() throws RecognitionException {
        JWIPreprocessor_Parser.literal_return retval = new JWIPreprocessor_Parser.literal_return();
        retval.start = input.LT(1);
        int literal_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 75) ) { return retval; }
            // JWIPreprocessor_Parser.g:767:5: ( integerLiteral | FloatingPointLiteral | CharacterLiteral | StringLiteral | booleanLiteral | NULL )
            int alt105=6;
            switch ( input.LA(1) ) {
            case HexLiteral:
            case DecimalLiteral:
            case OctalLiteral:
                {
                alt105=1;
                }
                break;
            case FloatingPointLiteral:
                {
                alt105=2;
                }
                break;
            case CharacterLiteral:
                {
                alt105=3;
                }
                break;
            case StringLiteral:
                {
                alt105=4;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt105=5;
                }
                break;
            case NULL:
                {
                alt105=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 105, 0, input);

                throw nvae;
            }

            switch (alt105) {
                case 1 :
                    // JWIPreprocessor_Parser.g:767:9: integerLiteral
                    {
                    pushFollow(FOLLOW_integerLiteral_in_literal3706);
                    integerLiteral();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:768:9: FloatingPointLiteral
                    {
                    match(input,FloatingPointLiteral,FOLLOW_FloatingPointLiteral_in_literal3716); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:769:9: CharacterLiteral
                    {
                    match(input,CharacterLiteral,FOLLOW_CharacterLiteral_in_literal3726); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:770:9: StringLiteral
                    {
                    match(input,StringLiteral,FOLLOW_StringLiteral_in_literal3736); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:771:9: booleanLiteral
                    {
                    pushFollow(FOLLOW_booleanLiteral_in_literal3746);
                    booleanLiteral();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // JWIPreprocessor_Parser.g:772:9: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_literal3756); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 75, literal_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class integerLiteral_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "integerLiteral"
    // JWIPreprocessor_Parser.g:775:1: integerLiteral : ( HexLiteral | OctalLiteral | DecimalLiteral );
    public final JWIPreprocessor_Parser.integerLiteral_return integerLiteral() throws RecognitionException {
        JWIPreprocessor_Parser.integerLiteral_return retval = new JWIPreprocessor_Parser.integerLiteral_return();
        retval.start = input.LT(1);
        int integerLiteral_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 76) ) { return retval; }
            // JWIPreprocessor_Parser.g:776:5: ( HexLiteral | OctalLiteral | DecimalLiteral )
            // JWIPreprocessor_Parser.g:
            {
            if ( (input.LA(1)>=HexLiteral && input.LA(1)<=OctalLiteral) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 76, integerLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "integerLiteral"

    public static class booleanLiteral_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "booleanLiteral"
    // JWIPreprocessor_Parser.g:781:1: booleanLiteral : ( TRUE | FALSE );
    public final JWIPreprocessor_Parser.booleanLiteral_return booleanLiteral() throws RecognitionException {
        JWIPreprocessor_Parser.booleanLiteral_return retval = new JWIPreprocessor_Parser.booleanLiteral_return();
        retval.start = input.LT(1);
        int booleanLiteral_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 77) ) { return retval; }
            // JWIPreprocessor_Parser.g:782:5: ( TRUE | FALSE )
            // JWIPreprocessor_Parser.g:
            {
            if ( input.LA(1)==FALSE||input.LA(1)==TRUE ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 77, booleanLiteral_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "booleanLiteral"

    public static class annotations_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotations"
    // JWIPreprocessor_Parser.g:788:1: annotations : ( annotation )+ ;
    public final JWIPreprocessor_Parser.annotations_return annotations() throws RecognitionException {
        JWIPreprocessor_Parser.annotations_return retval = new JWIPreprocessor_Parser.annotations_return();
        retval.start = input.LT(1);
        int annotations_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 78) ) { return retval; }
            // JWIPreprocessor_Parser.g:789:5: ( ( annotation )+ )
            // JWIPreprocessor_Parser.g:789:9: ( annotation )+
            {
            // JWIPreprocessor_Parser.g:789:9: ( annotation )+
            int cnt106=0;
            loop106:
            do {
                int alt106=2;
                int LA106_0 = input.LA(1);

                if ( (LA106_0==ATSIGN) ) {
                    int LA106_2 = input.LA(2);

                    if ( (LA106_2==Identifier) ) {
                        int LA106_3 = input.LA(3);

                        if ( (synpred151_JWIPreprocessor_Parser()) ) {
                            alt106=1;
                        }


                    }


                }


                switch (alt106) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: annotation
            	    {
            	    pushFollow(FOLLOW_annotation_in_annotations3845);
            	    annotation();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    if ( cnt106 >= 1 ) break loop106;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(106, input);
                        throw eee;
                }
                cnt106++;
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 78, annotations_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotations"

    public static class annotation_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotation"
    // JWIPreprocessor_Parser.g:792:1: annotation : ATSIGN annotationName ( LEFTPARENTHESIS ( elementValuePairs | elementValue )? RIGHTPARENTHESIS )? ;
    public final JWIPreprocessor_Parser.annotation_return annotation() throws RecognitionException {
        JWIPreprocessor_Parser.annotation_return retval = new JWIPreprocessor_Parser.annotation_return();
        retval.start = input.LT(1);
        int annotation_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 79) ) { return retval; }
            // JWIPreprocessor_Parser.g:793:5: ( ATSIGN annotationName ( LEFTPARENTHESIS ( elementValuePairs | elementValue )? RIGHTPARENTHESIS )? )
            // JWIPreprocessor_Parser.g:793:9: ATSIGN annotationName ( LEFTPARENTHESIS ( elementValuePairs | elementValue )? RIGHTPARENTHESIS )?
            {
            match(input,ATSIGN,FOLLOW_ATSIGN_in_annotation3865); if (state.failed) return retval;
            pushFollow(FOLLOW_annotationName_in_annotation3867);
            annotationName();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:793:31: ( LEFTPARENTHESIS ( elementValuePairs | elementValue )? RIGHTPARENTHESIS )?
            int alt108=2;
            int LA108_0 = input.LA(1);

            if ( (LA108_0==LEFTPARENTHESIS) ) {
                alt108=1;
            }
            switch (alt108) {
                case 1 :
                    // JWIPreprocessor_Parser.g:793:33: LEFTPARENTHESIS ( elementValuePairs | elementValue )? RIGHTPARENTHESIS
                    {
                    match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_annotation3871); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:793:49: ( elementValuePairs | elementValue )?
                    int alt107=3;
                    int LA107_0 = input.LA(1);

                    if ( (LA107_0==Identifier) ) {
                        int LA107_1 = input.LA(2);

                        if ( (LA107_1==ASSIGNMENT_EQUALS) ) {
                            alt107=1;
                        }
                        else if ( (LA107_1==INSTANCEOF||(LA107_1>=LEFTPARENTHESIS && LA107_1<=LEFTSQUAREBRACKET)||(LA107_1>=LESSTHAN && LA107_1<=GREATERTHAN)||LA107_1==DOT||LA107_1==QUESTIONMARK||(LA107_1>=PLUSPLUS && LA107_1<=PLUS)||(LA107_1>=MINUSMINUS && LA107_1<=MINUS)||LA107_1==ASTERISK||LA107_1==SLASH||LA107_1==PERCENT||(LA107_1>=CARET && LA107_1<=EXCLAMATIONMARKEQUALS)||LA107_1==EQUALITY_EQUALS||LA107_1==LOGICAL_AND||(LA107_1>=BITWISE_AND && LA107_1<=LOGICAL_OR)||LA107_1==PIPE) ) {
                            alt107=2;
                        }
                    }
                    else if ( (LA107_0==BOOLEAN||LA107_0==BYTE||LA107_0==CHAR||LA107_0==DOUBLE||LA107_0==FALSE||LA107_0==FLOAT||LA107_0==INT||LA107_0==LONG||(LA107_0>=NEW && LA107_0<=NULL)||LA107_0==SHORT||LA107_0==SUPER||LA107_0==THIS||LA107_0==TRUE||LA107_0==VOID||LA107_0==LEFTBRACE||LA107_0==LEFTPARENTHESIS||(LA107_0>=ATSIGN && LA107_0<=TILDE)||(LA107_0>=PLUSPLUS && LA107_0<=PLUS)||(LA107_0>=MINUSMINUS && LA107_0<=MINUS)||LA107_0==EXCLAMATIONMARK||(LA107_0>=HexLiteral && LA107_0<=OctalLiteral)||LA107_0==FloatingPointLiteral||(LA107_0>=CharacterLiteral && LA107_0<=StringLiteral)) ) {
                        alt107=2;
                    }
                    switch (alt107) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:793:51: elementValuePairs
                            {
                            pushFollow(FOLLOW_elementValuePairs_in_annotation3875);
                            elementValuePairs();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;
                        case 2 :
                            // JWIPreprocessor_Parser.g:793:71: elementValue
                            {
                            pushFollow(FOLLOW_elementValue_in_annotation3879);
                            elementValue();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_annotation3884); if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 79, annotation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotation"

    public static class annotationName_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotationName"
    // JWIPreprocessor_Parser.g:796:1: annotationName : Identifier ( DOT Identifier )* ;
    public final JWIPreprocessor_Parser.annotationName_return annotationName() throws RecognitionException {
        JWIPreprocessor_Parser.annotationName_return retval = new JWIPreprocessor_Parser.annotationName_return();
        retval.start = input.LT(1);
        int annotationName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 80) ) { return retval; }
            // JWIPreprocessor_Parser.g:797:5: ( Identifier ( DOT Identifier )* )
            // JWIPreprocessor_Parser.g:797:7: Identifier ( DOT Identifier )*
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationName3908); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:797:18: ( DOT Identifier )*
            loop109:
            do {
                int alt109=2;
                int LA109_0 = input.LA(1);

                if ( (LA109_0==DOT) ) {
                    alt109=1;
                }


                switch (alt109) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:797:19: DOT Identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_annotationName3911); if (state.failed) return retval;
            	    match(input,Identifier,FOLLOW_Identifier_in_annotationName3913); if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop109;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 80, annotationName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationName"

    public static class elementValuePairs_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "elementValuePairs"
    // JWIPreprocessor_Parser.g:800:1: elementValuePairs : elementValuePair ( COMMA elementValuePair )* ;
    public final JWIPreprocessor_Parser.elementValuePairs_return elementValuePairs() throws RecognitionException {
        JWIPreprocessor_Parser.elementValuePairs_return retval = new JWIPreprocessor_Parser.elementValuePairs_return();
        retval.start = input.LT(1);
        int elementValuePairs_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 81) ) { return retval; }
            // JWIPreprocessor_Parser.g:801:5: ( elementValuePair ( COMMA elementValuePair )* )
            // JWIPreprocessor_Parser.g:801:9: elementValuePair ( COMMA elementValuePair )*
            {
            pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3934);
            elementValuePair();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:801:26: ( COMMA elementValuePair )*
            loop110:
            do {
                int alt110=2;
                int LA110_0 = input.LA(1);

                if ( (LA110_0==COMMA) ) {
                    alt110=1;
                }


                switch (alt110) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:801:27: COMMA elementValuePair
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_elementValuePairs3937); if (state.failed) return retval;
            	    pushFollow(FOLLOW_elementValuePair_in_elementValuePairs3939);
            	    elementValuePair();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop110;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 81, elementValuePairs_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValuePairs"

    public static class elementValuePair_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "elementValuePair"
    // JWIPreprocessor_Parser.g:804:1: elementValuePair : Identifier ASSIGNMENT_EQUALS elementValue ;
    public final JWIPreprocessor_Parser.elementValuePair_return elementValuePair() throws RecognitionException {
        JWIPreprocessor_Parser.elementValuePair_return retval = new JWIPreprocessor_Parser.elementValuePair_return();
        retval.start = input.LT(1);
        int elementValuePair_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 82) ) { return retval; }
            // JWIPreprocessor_Parser.g:805:5: ( Identifier ASSIGNMENT_EQUALS elementValue )
            // JWIPreprocessor_Parser.g:805:9: Identifier ASSIGNMENT_EQUALS elementValue
            {
            match(input,Identifier,FOLLOW_Identifier_in_elementValuePair3960); if (state.failed) return retval;
            match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_elementValuePair3962); if (state.failed) return retval;
            pushFollow(FOLLOW_elementValue_in_elementValuePair3964);
            elementValue();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 82, elementValuePair_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValuePair"

    public static class elementValue_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "elementValue"
    // JWIPreprocessor_Parser.g:808:1: elementValue : ( conditionalExpression | annotation | elementValueArrayInitializer );
    public final JWIPreprocessor_Parser.elementValue_return elementValue() throws RecognitionException {
        JWIPreprocessor_Parser.elementValue_return retval = new JWIPreprocessor_Parser.elementValue_return();
        retval.start = input.LT(1);
        int elementValue_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 83) ) { return retval; }
            // JWIPreprocessor_Parser.g:809:5: ( conditionalExpression | annotation | elementValueArrayInitializer )
            int alt111=3;
            switch ( input.LA(1) ) {
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FALSE:
            case FLOAT:
            case INT:
            case LONG:
            case NEW:
            case NULL:
            case SHORT:
            case SUPER:
            case THIS:
            case TRUE:
            case VOID:
            case LEFTPARENTHESIS:
            case TILDE:
            case PLUSPLUS:
            case PLUS:
            case MINUSMINUS:
            case MINUS:
            case EXCLAMATIONMARK:
            case HexLiteral:
            case DecimalLiteral:
            case OctalLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case Identifier:
                {
                alt111=1;
                }
                break;
            case ATSIGN:
                {
                alt111=2;
                }
                break;
            case LEFTBRACE:
                {
                alt111=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 111, 0, input);

                throw nvae;
            }

            switch (alt111) {
                case 1 :
                    // JWIPreprocessor_Parser.g:809:9: conditionalExpression
                    {
                    pushFollow(FOLLOW_conditionalExpression_in_elementValue3987);
                    conditionalExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:810:9: annotation
                    {
                    pushFollow(FOLLOW_annotation_in_elementValue3997);
                    annotation();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:811:9: elementValueArrayInitializer
                    {
                    pushFollow(FOLLOW_elementValueArrayInitializer_in_elementValue4007);
                    elementValueArrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 83, elementValue_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValue"

    public static class elementValueArrayInitializer_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "elementValueArrayInitializer"
    // JWIPreprocessor_Parser.g:814:1: elementValueArrayInitializer : LEFTBRACE ( elementValue ( COMMA elementValue )* )? ( COMMA )? RIGHTBRACE ;
    public final JWIPreprocessor_Parser.elementValueArrayInitializer_return elementValueArrayInitializer() throws RecognitionException {
        JWIPreprocessor_Parser.elementValueArrayInitializer_return retval = new JWIPreprocessor_Parser.elementValueArrayInitializer_return();
        retval.start = input.LT(1);
        int elementValueArrayInitializer_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 84) ) { return retval; }
            // JWIPreprocessor_Parser.g:815:5: ( LEFTBRACE ( elementValue ( COMMA elementValue )* )? ( COMMA )? RIGHTBRACE )
            // JWIPreprocessor_Parser.g:815:9: LEFTBRACE ( elementValue ( COMMA elementValue )* )? ( COMMA )? RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_elementValueArrayInitializer4030); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:815:19: ( elementValue ( COMMA elementValue )* )?
            int alt113=2;
            int LA113_0 = input.LA(1);

            if ( (LA113_0==BOOLEAN||LA113_0==BYTE||LA113_0==CHAR||LA113_0==DOUBLE||LA113_0==FALSE||LA113_0==FLOAT||LA113_0==INT||LA113_0==LONG||(LA113_0>=NEW && LA113_0<=NULL)||LA113_0==SHORT||LA113_0==SUPER||LA113_0==THIS||LA113_0==TRUE||LA113_0==VOID||LA113_0==LEFTBRACE||LA113_0==LEFTPARENTHESIS||(LA113_0>=ATSIGN && LA113_0<=TILDE)||(LA113_0>=PLUSPLUS && LA113_0<=PLUS)||(LA113_0>=MINUSMINUS && LA113_0<=MINUS)||LA113_0==EXCLAMATIONMARK||(LA113_0>=HexLiteral && LA113_0<=OctalLiteral)||LA113_0==FloatingPointLiteral||(LA113_0>=CharacterLiteral && LA113_0<=StringLiteral)||LA113_0==Identifier) ) {
                alt113=1;
            }
            switch (alt113) {
                case 1 :
                    // JWIPreprocessor_Parser.g:815:20: elementValue ( COMMA elementValue )*
                    {
                    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer4033);
                    elementValue();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:815:33: ( COMMA elementValue )*
                    loop112:
                    do {
                        int alt112=2;
                        int LA112_0 = input.LA(1);

                        if ( (LA112_0==COMMA) ) {
                            int LA112_1 = input.LA(2);

                            if ( (LA112_1==BOOLEAN||LA112_1==BYTE||LA112_1==CHAR||LA112_1==DOUBLE||LA112_1==FALSE||LA112_1==FLOAT||LA112_1==INT||LA112_1==LONG||(LA112_1>=NEW && LA112_1<=NULL)||LA112_1==SHORT||LA112_1==SUPER||LA112_1==THIS||LA112_1==TRUE||LA112_1==VOID||LA112_1==LEFTBRACE||LA112_1==LEFTPARENTHESIS||(LA112_1>=ATSIGN && LA112_1<=TILDE)||(LA112_1>=PLUSPLUS && LA112_1<=PLUS)||(LA112_1>=MINUSMINUS && LA112_1<=MINUS)||LA112_1==EXCLAMATIONMARK||(LA112_1>=HexLiteral && LA112_1<=OctalLiteral)||LA112_1==FloatingPointLiteral||(LA112_1>=CharacterLiteral && LA112_1<=StringLiteral)||LA112_1==Identifier) ) {
                                alt112=1;
                            }


                        }


                        switch (alt112) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:815:34: COMMA elementValue
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer4036); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_elementValue_in_elementValueArrayInitializer4038);
                    	    elementValue();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop112;
                        }
                    } while (true);


                    }
                    break;

            }

            // JWIPreprocessor_Parser.g:815:57: ( COMMA )?
            int alt114=2;
            int LA114_0 = input.LA(1);

            if ( (LA114_0==COMMA) ) {
                alt114=1;
            }
            switch (alt114) {
                case 1 :
                    // JWIPreprocessor_Parser.g:815:58: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_elementValueArrayInitializer4045); if (state.failed) return retval;

                    }
                    break;

            }

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_elementValueArrayInitializer4049); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 84, elementValueArrayInitializer_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "elementValueArrayInitializer"

    public static class annotationTypeDeclaration_return extends ParserRuleReturnScope {
        public ClassDefinition classDefinitionObj;
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotationTypeDeclaration"
    // JWIPreprocessor_Parser.g:818:1: annotationTypeDeclaration returns [ClassDefinition classDefinitionObj] : ATSIGN INTERFACE name1= Identifier annotationTypeBody ;
    public final JWIPreprocessor_Parser.annotationTypeDeclaration_return annotationTypeDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.annotationTypeDeclaration_return retval = new JWIPreprocessor_Parser.annotationTypeDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeDeclaration_StartIndex = input.index();
        Token name1=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 85) ) { return retval; }
            // JWIPreprocessor_Parser.g:819:5: ( ATSIGN INTERFACE name1= Identifier annotationTypeBody )
            // JWIPreprocessor_Parser.g:819:9: ATSIGN INTERFACE name1= Identifier annotationTypeBody
            {
            match(input,ATSIGN,FOLLOW_ATSIGN_in_annotationTypeDeclaration4076); if (state.failed) return retval;
            match(input,INTERFACE,FOLLOW_INTERFACE_in_annotationTypeDeclaration4078); if (state.failed) return retval;
            name1=(Token)match(input,Identifier,FOLLOW_Identifier_in_annotationTypeDeclaration4082); if (state.failed) return retval;
            if ( state.backtracking==0 ) {

                          // System.out.println("CHECKPOINT B");
                  
                          ClassDefinition defn = new ClassDefinition((name1!=null?name1.getText():null));
                          SymbolTableEntry entry = new SymbolTableEntry(defn);
                          SymbolTableManager.getInstance().getSymbolTable().put((name1!=null?name1.getText():null), entry);
                          
                          retval.classDefinitionObj = defn;
                      
            }
            pushFollow(FOLLOW_annotationTypeBody_in_annotationTypeDeclaration4105);
            annotationTypeBody();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 85, annotationTypeDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeDeclaration"

    public static class annotationTypeBody_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotationTypeBody"
    // JWIPreprocessor_Parser.g:832:1: annotationTypeBody : LEFTBRACE ( annotationTypeElementDeclaration )* RIGHTBRACE ;
    public final JWIPreprocessor_Parser.annotationTypeBody_return annotationTypeBody() throws RecognitionException {
        JWIPreprocessor_Parser.annotationTypeBody_return retval = new JWIPreprocessor_Parser.annotationTypeBody_return();
        retval.start = input.LT(1);
        int annotationTypeBody_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 86) ) { return retval; }
            // JWIPreprocessor_Parser.g:833:5: ( LEFTBRACE ( annotationTypeElementDeclaration )* RIGHTBRACE )
            // JWIPreprocessor_Parser.g:833:9: LEFTBRACE ( annotationTypeElementDeclaration )* RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_annotationTypeBody4124); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:833:19: ( annotationTypeElementDeclaration )*
            loop115:
            do {
                int alt115=2;
                int LA115_0 = input.LA(1);

                if ( ((LA115_0>=ABSTRACT && LA115_0<=BOOLEAN)||LA115_0==BYTE||(LA115_0>=CHAR && LA115_0<=CLASS)||LA115_0==DOUBLE||LA115_0==FINAL||LA115_0==FLOAT||(LA115_0>=INT && LA115_0<=NATIVE)||(LA115_0>=PRIVATE && LA115_0<=PUBLIC)||(LA115_0>=SHORT && LA115_0<=STRICTFP)||LA115_0==SYNCHRONIZED||LA115_0==TRANSIENT||LA115_0==VOID||LA115_0==LESSTHAN||LA115_0==ATSIGN||LA115_0==ENUM||LA115_0==Identifier||LA115_0==VOLATIVE) ) {
                    alt115=1;
                }


                switch (alt115) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:833:20: annotationTypeElementDeclaration
            	    {
            	    pushFollow(FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody4127);
            	    annotationTypeElementDeclaration();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop115;
                }
            } while (true);

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_annotationTypeBody4131); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 86, annotationTypeBody_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeBody"

    public static class annotationTypeElementDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotationTypeElementDeclaration"
    // JWIPreprocessor_Parser.g:836:1: annotationTypeElementDeclaration : modifiers annotationTypeElementRest ;
    public final JWIPreprocessor_Parser.annotationTypeElementDeclaration_return annotationTypeElementDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.annotationTypeElementDeclaration_return retval = new JWIPreprocessor_Parser.annotationTypeElementDeclaration_return();
        retval.start = input.LT(1);
        int annotationTypeElementDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 87) ) { return retval; }
            // JWIPreprocessor_Parser.g:837:5: ( modifiers annotationTypeElementRest )
            // JWIPreprocessor_Parser.g:837:9: modifiers annotationTypeElementRest
            {
            pushFollow(FOLLOW_modifiers_in_annotationTypeElementDeclaration4154);
            modifiers();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration4156);
            annotationTypeElementRest();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 87, annotationTypeElementDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeElementDeclaration"

    public static class annotationTypeElementRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotationTypeElementRest"
    // JWIPreprocessor_Parser.g:840:1: annotationTypeElementRest : ( type annotationMethodOrConstantRest SEMICOLON | normalClassDeclaration ( SEMICOLON )? | normalInterfaceDeclaration ( SEMICOLON )? | enumDeclaration ( SEMICOLON )? | annotationTypeDeclaration ( SEMICOLON )? );
    public final JWIPreprocessor_Parser.annotationTypeElementRest_return annotationTypeElementRest() throws RecognitionException {
        JWIPreprocessor_Parser.annotationTypeElementRest_return retval = new JWIPreprocessor_Parser.annotationTypeElementRest_return();
        retval.start = input.LT(1);
        int annotationTypeElementRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 88) ) { return retval; }
            // JWIPreprocessor_Parser.g:841:5: ( type annotationMethodOrConstantRest SEMICOLON | normalClassDeclaration ( SEMICOLON )? | normalInterfaceDeclaration ( SEMICOLON )? | enumDeclaration ( SEMICOLON )? | annotationTypeDeclaration ( SEMICOLON )? )
            int alt120=5;
            switch ( input.LA(1) ) {
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
            case Identifier:
                {
                alt120=1;
                }
                break;
            case CLASS:
                {
                alt120=2;
                }
                break;
            case INTERFACE:
                {
                alt120=3;
                }
                break;
            case ENUM:
                {
                alt120=4;
                }
                break;
            case ATSIGN:
                {
                alt120=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 120, 0, input);

                throw nvae;
            }

            switch (alt120) {
                case 1 :
                    // JWIPreprocessor_Parser.g:841:9: type annotationMethodOrConstantRest SEMICOLON
                    {
                    pushFollow(FOLLOW_type_in_annotationTypeElementRest4179);
                    type();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest4181);
                    annotationMethodOrConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_annotationTypeElementRest4183); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:842:9: normalClassDeclaration ( SEMICOLON )?
                    {
                    pushFollow(FOLLOW_normalClassDeclaration_in_annotationTypeElementRest4193);
                    normalClassDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:842:32: ( SEMICOLON )?
                    int alt116=2;
                    int LA116_0 = input.LA(1);

                    if ( (LA116_0==SEMICOLON) ) {
                        alt116=1;
                    }
                    switch (alt116) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: SEMICOLON
                            {
                            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_annotationTypeElementRest4195); if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:843:9: normalInterfaceDeclaration ( SEMICOLON )?
                    {
                    pushFollow(FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest4206);
                    normalInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:843:36: ( SEMICOLON )?
                    int alt117=2;
                    int LA117_0 = input.LA(1);

                    if ( (LA117_0==SEMICOLON) ) {
                        alt117=1;
                    }
                    switch (alt117) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: SEMICOLON
                            {
                            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_annotationTypeElementRest4208); if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:844:9: enumDeclaration ( SEMICOLON )?
                    {
                    pushFollow(FOLLOW_enumDeclaration_in_annotationTypeElementRest4219);
                    enumDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:844:25: ( SEMICOLON )?
                    int alt118=2;
                    int LA118_0 = input.LA(1);

                    if ( (LA118_0==SEMICOLON) ) {
                        alt118=1;
                    }
                    switch (alt118) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: SEMICOLON
                            {
                            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_annotationTypeElementRest4221); if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:845:9: annotationTypeDeclaration ( SEMICOLON )?
                    {
                    pushFollow(FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest4232);
                    annotationTypeDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:845:35: ( SEMICOLON )?
                    int alt119=2;
                    int LA119_0 = input.LA(1);

                    if ( (LA119_0==SEMICOLON) ) {
                        alt119=1;
                    }
                    switch (alt119) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: SEMICOLON
                            {
                            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_annotationTypeElementRest4234); if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 88, annotationTypeElementRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationTypeElementRest"

    public static class annotationMethodOrConstantRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotationMethodOrConstantRest"
    // JWIPreprocessor_Parser.g:848:1: annotationMethodOrConstantRest : ( annotationMethodRest | annotationConstantRest );
    public final JWIPreprocessor_Parser.annotationMethodOrConstantRest_return annotationMethodOrConstantRest() throws RecognitionException {
        JWIPreprocessor_Parser.annotationMethodOrConstantRest_return retval = new JWIPreprocessor_Parser.annotationMethodOrConstantRest_return();
        retval.start = input.LT(1);
        int annotationMethodOrConstantRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 89) ) { return retval; }
            // JWIPreprocessor_Parser.g:849:5: ( annotationMethodRest | annotationConstantRest )
            int alt121=2;
            int LA121_0 = input.LA(1);

            if ( (LA121_0==Identifier) ) {
                int LA121_1 = input.LA(2);

                if ( (LA121_1==LEFTPARENTHESIS) ) {
                    alt121=1;
                }
                else if ( (LA121_1==SEMICOLON||LA121_1==COMMA||LA121_1==LEFTSQUAREBRACKET||LA121_1==ASSIGNMENT_EQUALS) ) {
                    alt121=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 121, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 121, 0, input);

                throw nvae;
            }
            switch (alt121) {
                case 1 :
                    // JWIPreprocessor_Parser.g:849:9: annotationMethodRest
                    {
                    pushFollow(FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest4258);
                    annotationMethodRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:850:9: annotationConstantRest
                    {
                    pushFollow(FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest4268);
                    annotationConstantRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 89, annotationMethodOrConstantRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationMethodOrConstantRest"

    public static class annotationMethodRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotationMethodRest"
    // JWIPreprocessor_Parser.g:853:1: annotationMethodRest : Identifier LEFTPARENTHESIS RIGHTPARENTHESIS ( defaultValue )? ;
    public final JWIPreprocessor_Parser.annotationMethodRest_return annotationMethodRest() throws RecognitionException {
        JWIPreprocessor_Parser.annotationMethodRest_return retval = new JWIPreprocessor_Parser.annotationMethodRest_return();
        retval.start = input.LT(1);
        int annotationMethodRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 90) ) { return retval; }
            // JWIPreprocessor_Parser.g:854:5: ( Identifier LEFTPARENTHESIS RIGHTPARENTHESIS ( defaultValue )? )
            // JWIPreprocessor_Parser.g:854:9: Identifier LEFTPARENTHESIS RIGHTPARENTHESIS ( defaultValue )?
            {
            match(input,Identifier,FOLLOW_Identifier_in_annotationMethodRest4291); if (state.failed) return retval;
            match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_annotationMethodRest4293); if (state.failed) return retval;
            match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_annotationMethodRest4295); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:854:53: ( defaultValue )?
            int alt122=2;
            int LA122_0 = input.LA(1);

            if ( (LA122_0==DEFAULT) ) {
                alt122=1;
            }
            switch (alt122) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: defaultValue
                    {
                    pushFollow(FOLLOW_defaultValue_in_annotationMethodRest4297);
                    defaultValue();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 90, annotationMethodRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationMethodRest"

    public static class annotationConstantRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "annotationConstantRest"
    // JWIPreprocessor_Parser.g:857:1: annotationConstantRest : variableDeclarators ;
    public final JWIPreprocessor_Parser.annotationConstantRest_return annotationConstantRest() throws RecognitionException {
        JWIPreprocessor_Parser.annotationConstantRest_return retval = new JWIPreprocessor_Parser.annotationConstantRest_return();
        retval.start = input.LT(1);
        int annotationConstantRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 91) ) { return retval; }
            // JWIPreprocessor_Parser.g:858:5: ( variableDeclarators )
            // JWIPreprocessor_Parser.g:858:9: variableDeclarators
            {
            pushFollow(FOLLOW_variableDeclarators_in_annotationConstantRest4321);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 91, annotationConstantRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "annotationConstantRest"

    public static class defaultValue_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "defaultValue"
    // JWIPreprocessor_Parser.g:861:1: defaultValue : DEFAULT elementValue ;
    public final JWIPreprocessor_Parser.defaultValue_return defaultValue() throws RecognitionException {
        JWIPreprocessor_Parser.defaultValue_return retval = new JWIPreprocessor_Parser.defaultValue_return();
        retval.start = input.LT(1);
        int defaultValue_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 92) ) { return retval; }
            // JWIPreprocessor_Parser.g:862:5: ( DEFAULT elementValue )
            // JWIPreprocessor_Parser.g:862:9: DEFAULT elementValue
            {
            match(input,DEFAULT,FOLLOW_DEFAULT_in_defaultValue4344); if (state.failed) return retval;
            pushFollow(FOLLOW_elementValue_in_defaultValue4346);
            elementValue();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 92, defaultValue_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "defaultValue"

    public static class block_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "block"
    // JWIPreprocessor_Parser.g:867:1: block : LEFTBRACE ( inlineIntentionBlock | blockStatement )* RIGHTBRACE ;
    public final JWIPreprocessor_Parser.block_return block() throws RecognitionException {
        JWIPreprocessor_Parser.block_return retval = new JWIPreprocessor_Parser.block_return();
        retval.start = input.LT(1);
        int block_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 93) ) { return retval; }
            // JWIPreprocessor_Parser.g:868:5: ( LEFTBRACE ( inlineIntentionBlock | blockStatement )* RIGHTBRACE )
            // JWIPreprocessor_Parser.g:868:9: LEFTBRACE ( inlineIntentionBlock | blockStatement )* RIGHTBRACE
            {
            match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_block4367); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:868:19: ( inlineIntentionBlock | blockStatement )*
            loop123:
            do {
                int alt123=3;
                int LA123_0 = input.LA(1);

                if ( (LA123_0==INLINEINTENTIONOPENINGTAGTOKEN) ) {
                    alt123=1;
                }
                else if ( ((LA123_0>=ABSTRACT && LA123_0<=BYTE)||(LA123_0>=CHAR && LA123_0<=CONTINUE)||(LA123_0>=DO && LA123_0<=DOUBLE)||(LA123_0>=FALSE && LA123_0<=FINAL)||(LA123_0>=FLOAT && LA123_0<=IF)||(LA123_0>=INT && LA123_0<=LONG)||(LA123_0>=NEW && LA123_0<=NULL)||(LA123_0>=PRIVATE && LA123_0<=THROW)||(LA123_0>=TRUE && LA123_0<=VOID)||LA123_0==SEMICOLON||LA123_0==LEFTBRACE||LA123_0==LEFTPARENTHESIS||(LA123_0>=ATSIGN && LA123_0<=TILDE)||(LA123_0>=PLUSPLUS && LA123_0<=PLUS)||(LA123_0>=MINUSMINUS && LA123_0<=MINUS)||LA123_0==EXCLAMATIONMARK||(LA123_0>=HexLiteral && LA123_0<=OctalLiteral)||LA123_0==FloatingPointLiteral||(LA123_0>=CharacterLiteral && LA123_0<=StringLiteral)||(LA123_0>=ENUM && LA123_0<=ASSERT)||LA123_0==Identifier) ) {
                    alt123=2;
                }


                switch (alt123) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:868:20: inlineIntentionBlock
            	    {
            	    pushFollow(FOLLOW_inlineIntentionBlock_in_block4370);
            	    inlineIntentionBlock();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;
            	case 2 :
            	    // JWIPreprocessor_Parser.g:868:43: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_block4374);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop123;
                }
            } while (true);

            match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_block4378); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 93, block_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "block"

    public static class inlineIntentionBlock_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "inlineIntentionBlock"
    // JWIPreprocessor_Parser.g:871:1: inlineIntentionBlock : inlineIntentionCommentOpeningTag ( inlineIntentionBlock | blockStatement )* inlineIntentionCommentClosingTag ;
    public final JWIPreprocessor_Parser.inlineIntentionBlock_return inlineIntentionBlock() throws RecognitionException {
        JWIPreprocessor_Parser.inlineIntentionBlock_return retval = new JWIPreprocessor_Parser.inlineIntentionBlock_return();
        retval.start = input.LT(1);
        int inlineIntentionBlock_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 94) ) { return retval; }
            // JWIPreprocessor_Parser.g:872:5: ( inlineIntentionCommentOpeningTag ( inlineIntentionBlock | blockStatement )* inlineIntentionCommentClosingTag )
            // JWIPreprocessor_Parser.g:872:9: inlineIntentionCommentOpeningTag ( inlineIntentionBlock | blockStatement )* inlineIntentionCommentClosingTag
            {
            pushFollow(FOLLOW_inlineIntentionCommentOpeningTag_in_inlineIntentionBlock4397);
            inlineIntentionCommentOpeningTag();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:873:9: ( inlineIntentionBlock | blockStatement )*
            loop124:
            do {
                int alt124=3;
                int LA124_0 = input.LA(1);

                if ( (LA124_0==INLINEINTENTIONOPENINGTAGTOKEN) ) {
                    alt124=1;
                }
                else if ( ((LA124_0>=ABSTRACT && LA124_0<=BYTE)||(LA124_0>=CHAR && LA124_0<=CONTINUE)||(LA124_0>=DO && LA124_0<=DOUBLE)||(LA124_0>=FALSE && LA124_0<=FINAL)||(LA124_0>=FLOAT && LA124_0<=IF)||(LA124_0>=INT && LA124_0<=LONG)||(LA124_0>=NEW && LA124_0<=NULL)||(LA124_0>=PRIVATE && LA124_0<=THROW)||(LA124_0>=TRUE && LA124_0<=VOID)||LA124_0==SEMICOLON||LA124_0==LEFTBRACE||LA124_0==LEFTPARENTHESIS||(LA124_0>=ATSIGN && LA124_0<=TILDE)||(LA124_0>=PLUSPLUS && LA124_0<=PLUS)||(LA124_0>=MINUSMINUS && LA124_0<=MINUS)||LA124_0==EXCLAMATIONMARK||(LA124_0>=HexLiteral && LA124_0<=OctalLiteral)||LA124_0==FloatingPointLiteral||(LA124_0>=CharacterLiteral && LA124_0<=StringLiteral)||(LA124_0>=ENUM && LA124_0<=ASSERT)||LA124_0==Identifier) ) {
                    alt124=2;
                }


                switch (alt124) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:873:10: inlineIntentionBlock
            	    {
            	    pushFollow(FOLLOW_inlineIntentionBlock_in_inlineIntentionBlock4408);
            	    inlineIntentionBlock();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;
            	case 2 :
            	    // JWIPreprocessor_Parser.g:873:33: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_inlineIntentionBlock4412);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop124;
                }
            } while (true);

            pushFollow(FOLLOW_inlineIntentionCommentClosingTag_in_inlineIntentionBlock4424);
            inlineIntentionCommentClosingTag();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 94, inlineIntentionBlock_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inlineIntentionBlock"

    public static class inlineIntentionCommentOpeningTag_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "inlineIntentionCommentOpeningTag"
    // JWIPreprocessor_Parser.g:877:1: inlineIntentionCommentOpeningTag : INLINEINTENTIONOPENINGTAGTOKEN -> template(content=$text) \"/* <content> */\";
    public final JWIPreprocessor_Parser.inlineIntentionCommentOpeningTag_return inlineIntentionCommentOpeningTag() throws RecognitionException {
        JWIPreprocessor_Parser.inlineIntentionCommentOpeningTag_return retval = new JWIPreprocessor_Parser.inlineIntentionCommentOpeningTag_return();
        retval.start = input.LT(1);
        int inlineIntentionCommentOpeningTag_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 95) ) { return retval; }
            // JWIPreprocessor_Parser.g:878:5: ( INLINEINTENTIONOPENINGTAGTOKEN -> template(content=$text) \"/* <content> */\")
            // JWIPreprocessor_Parser.g:878:9: INLINEINTENTIONOPENINGTAGTOKEN
            {
            match(input,INLINEINTENTIONOPENINGTAGTOKEN,FOLLOW_INLINEINTENTIONOPENINGTAGTOKEN_in_inlineIntentionCommentOpeningTag4447); if (state.failed) return retval;


            // TEMPLATE REWRITE
            if ( state.backtracking==0 ) {
              // 879:5: -> template(content=$text) \"/* <content> */\"
              {
                  retval.st = new StringTemplate(templateLib, "/* <content> */",
                new STAttrMap().put("content", input.toString(retval.start,input.LT(-1))));
              }

              ((TokenRewriteStream)input).replace(
                ((Token)retval.start).getTokenIndex(),
                input.LT(-1).getTokenIndex(),
                retval.st);
            }
            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 95, inlineIntentionCommentOpeningTag_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inlineIntentionCommentOpeningTag"

    public static class inlineIntentionCommentClosingTag_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "inlineIntentionCommentClosingTag"
    // JWIPreprocessor_Parser.g:889:1: inlineIntentionCommentClosingTag : INLINEINTENTIONCLOSINGTAGTOKEN -> template(content=$text) \"/* <content> */\";
    public final JWIPreprocessor_Parser.inlineIntentionCommentClosingTag_return inlineIntentionCommentClosingTag() throws RecognitionException {
        JWIPreprocessor_Parser.inlineIntentionCommentClosingTag_return retval = new JWIPreprocessor_Parser.inlineIntentionCommentClosingTag_return();
        retval.start = input.LT(1);
        int inlineIntentionCommentClosingTag_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 96) ) { return retval; }
            // JWIPreprocessor_Parser.g:890:5: ( INLINEINTENTIONCLOSINGTAGTOKEN -> template(content=$text) \"/* <content> */\")
            // JWIPreprocessor_Parser.g:890:9: INLINEINTENTIONCLOSINGTAGTOKEN
            {
            match(input,INLINEINTENTIONCLOSINGTAGTOKEN,FOLLOW_INLINEINTENTIONCLOSINGTAGTOKEN_in_inlineIntentionCommentClosingTag4496); if (state.failed) return retval;


            // TEMPLATE REWRITE
            if ( state.backtracking==0 ) {
              // 891:5: -> template(content=$text) \"/* <content> */\"
              {
                  retval.st = new StringTemplate(templateLib, "/* <content> */",
                new STAttrMap().put("content", input.toString(retval.start,input.LT(-1))));
              }

              ((TokenRewriteStream)input).replace(
                ((Token)retval.start).getTokenIndex(),
                input.LT(-1).getTokenIndex(),
                retval.st);
            }
            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 96, inlineIntentionCommentClosingTag_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inlineIntentionCommentClosingTag"

    public static class blockStatement_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "blockStatement"
    // JWIPreprocessor_Parser.g:899:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );
    public final JWIPreprocessor_Parser.blockStatement_return blockStatement() throws RecognitionException {
        JWIPreprocessor_Parser.blockStatement_return retval = new JWIPreprocessor_Parser.blockStatement_return();
        retval.start = input.LT(1);
        int blockStatement_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 97) ) { return retval; }
            // JWIPreprocessor_Parser.g:900:5: ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement )
            int alt125=3;
            alt125 = dfa125.predict(input);
            switch (alt125) {
                case 1 :
                    // JWIPreprocessor_Parser.g:900:9: localVariableDeclarationStatement
                    {
                    pushFollow(FOLLOW_localVariableDeclarationStatement_in_blockStatement4547);
                    localVariableDeclarationStatement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:901:9: classOrInterfaceDeclaration
                    {
                    pushFollow(FOLLOW_classOrInterfaceDeclaration_in_blockStatement4557);
                    classOrInterfaceDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:902:9: statement
                    {
                    pushFollow(FOLLOW_statement_in_blockStatement4567);
                    statement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 97, blockStatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "blockStatement"

    public static class localVariableDeclarationStatement_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "localVariableDeclarationStatement"
    // JWIPreprocessor_Parser.g:905:1: localVariableDeclarationStatement : localVariableDeclaration SEMICOLON ;
    public final JWIPreprocessor_Parser.localVariableDeclarationStatement_return localVariableDeclarationStatement() throws RecognitionException {
        JWIPreprocessor_Parser.localVariableDeclarationStatement_return retval = new JWIPreprocessor_Parser.localVariableDeclarationStatement_return();
        retval.start = input.LT(1);
        int localVariableDeclarationStatement_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 98) ) { return retval; }
            // JWIPreprocessor_Parser.g:906:5: ( localVariableDeclaration SEMICOLON )
            // JWIPreprocessor_Parser.g:906:10: localVariableDeclaration SEMICOLON
            {
            pushFollow(FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4591);
            localVariableDeclaration();

            state._fsp--;
            if (state.failed) return retval;
            match(input,SEMICOLON,FOLLOW_SEMICOLON_in_localVariableDeclarationStatement4593); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 98, localVariableDeclarationStatement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "localVariableDeclarationStatement"

    public static class localVariableDeclaration_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "localVariableDeclaration"
    // JWIPreprocessor_Parser.g:909:1: localVariableDeclaration : variableModifiers type variableDeclarators ;
    public final JWIPreprocessor_Parser.localVariableDeclaration_return localVariableDeclaration() throws RecognitionException {
        JWIPreprocessor_Parser.localVariableDeclaration_return retval = new JWIPreprocessor_Parser.localVariableDeclaration_return();
        retval.start = input.LT(1);
        int localVariableDeclaration_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 99) ) { return retval; }
            // JWIPreprocessor_Parser.g:910:5: ( variableModifiers type variableDeclarators )
            // JWIPreprocessor_Parser.g:910:9: variableModifiers type variableDeclarators
            {
            pushFollow(FOLLOW_variableModifiers_in_localVariableDeclaration4612);
            variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_type_in_localVariableDeclaration4614);
            type();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_variableDeclarators_in_localVariableDeclaration4616);
            variableDeclarators();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 99, localVariableDeclaration_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "localVariableDeclaration"

    public static class variableModifiers_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "variableModifiers"
    // JWIPreprocessor_Parser.g:913:1: variableModifiers : ( variableModifier )* ;
    public final JWIPreprocessor_Parser.variableModifiers_return variableModifiers() throws RecognitionException {
        JWIPreprocessor_Parser.variableModifiers_return retval = new JWIPreprocessor_Parser.variableModifiers_return();
        retval.start = input.LT(1);
        int variableModifiers_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 100) ) { return retval; }
            // JWIPreprocessor_Parser.g:914:5: ( ( variableModifier )* )
            // JWIPreprocessor_Parser.g:914:9: ( variableModifier )*
            {
            // JWIPreprocessor_Parser.g:914:9: ( variableModifier )*
            loop126:
            do {
                int alt126=2;
                int LA126_0 = input.LA(1);

                if ( (LA126_0==FINAL||LA126_0==ATSIGN) ) {
                    alt126=1;
                }


                switch (alt126) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: variableModifier
            	    {
            	    pushFollow(FOLLOW_variableModifier_in_variableModifiers4639);
            	    variableModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop126;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 100, variableModifiers_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "variableModifiers"

    public static class statement_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "statement"
    // JWIPreprocessor_Parser.g:917:1: statement : ( block | ASSERT expression ( COLON expression )? SEMICOLON | IF parExpression statement ( options {k=1; } : ELSE statement )? | FOR LEFTPARENTHESIS forControl RIGHTPARENTHESIS statement | WHILE parExpression statement | DO statement WHILE parExpression SEMICOLON | TRY block ( catches FINALLY block | catches | FINALLY block ) | SWITCH parExpression LEFTBRACE switchBlockStatementGroups RIGHTBRACE | SYNCHRONIZED parExpression block | RETURN ( expression )? SEMICOLON | THROW expression SEMICOLON | BREAK ( Identifier )? SEMICOLON | CONTINUE ( Identifier )? SEMICOLON | SEMICOLON | statementExpression SEMICOLON | Identifier COLON statement );
    public final JWIPreprocessor_Parser.statement_return statement() throws RecognitionException {
        JWIPreprocessor_Parser.statement_return retval = new JWIPreprocessor_Parser.statement_return();
        retval.start = input.LT(1);
        int statement_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 101) ) { return retval; }
            // JWIPreprocessor_Parser.g:918:5: ( block | ASSERT expression ( COLON expression )? SEMICOLON | IF parExpression statement ( options {k=1; } : ELSE statement )? | FOR LEFTPARENTHESIS forControl RIGHTPARENTHESIS statement | WHILE parExpression statement | DO statement WHILE parExpression SEMICOLON | TRY block ( catches FINALLY block | catches | FINALLY block ) | SWITCH parExpression LEFTBRACE switchBlockStatementGroups RIGHTBRACE | SYNCHRONIZED parExpression block | RETURN ( expression )? SEMICOLON | THROW expression SEMICOLON | BREAK ( Identifier )? SEMICOLON | CONTINUE ( Identifier )? SEMICOLON | SEMICOLON | statementExpression SEMICOLON | Identifier COLON statement )
            int alt133=16;
            alt133 = dfa133.predict(input);
            switch (alt133) {
                case 1 :
                    // JWIPreprocessor_Parser.g:918:9: block
                    {
                    pushFollow(FOLLOW_block_in_statement4659);
                    block();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:919:9: ASSERT expression ( COLON expression )? SEMICOLON
                    {
                    match(input,ASSERT,FOLLOW_ASSERT_in_statement4669); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_statement4671);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:919:27: ( COLON expression )?
                    int alt127=2;
                    int LA127_0 = input.LA(1);

                    if ( (LA127_0==COLON) ) {
                        alt127=1;
                    }
                    switch (alt127) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:919:28: COLON expression
                            {
                            match(input,COLON,FOLLOW_COLON_in_statement4674); if (state.failed) return retval;
                            pushFollow(FOLLOW_expression_in_statement4676);
                            expression();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_statement4680); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:920:9: IF parExpression statement ( options {k=1; } : ELSE statement )?
                    {
                    match(input,IF,FOLLOW_IF_in_statement4690); if (state.failed) return retval;
                    pushFollow(FOLLOW_parExpression_in_statement4692);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_statement_in_statement4694);
                    statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:920:36: ( options {k=1; } : ELSE statement )?
                    int alt128=2;
                    int LA128_0 = input.LA(1);

                    if ( (LA128_0==ELSE) ) {
                        int LA128_1 = input.LA(2);

                        if ( (synpred183_JWIPreprocessor_Parser()) ) {
                            alt128=1;
                        }
                    }
                    switch (alt128) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:920:52: ELSE statement
                            {
                            match(input,ELSE,FOLLOW_ELSE_in_statement4704); if (state.failed) return retval;
                            pushFollow(FOLLOW_statement_in_statement4706);
                            statement();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:921:9: FOR LEFTPARENTHESIS forControl RIGHTPARENTHESIS statement
                    {
                    match(input,FOR,FOLLOW_FOR_in_statement4718); if (state.failed) return retval;
                    match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_statement4720); if (state.failed) return retval;
                    pushFollow(FOLLOW_forControl_in_statement4722);
                    forControl();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_statement4724); if (state.failed) return retval;
                    pushFollow(FOLLOW_statement_in_statement4726);
                    statement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:922:9: WHILE parExpression statement
                    {
                    match(input,WHILE,FOLLOW_WHILE_in_statement4736); if (state.failed) return retval;
                    pushFollow(FOLLOW_parExpression_in_statement4738);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_statement_in_statement4740);
                    statement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // JWIPreprocessor_Parser.g:923:9: DO statement WHILE parExpression SEMICOLON
                    {
                    match(input,DO,FOLLOW_DO_in_statement4750); if (state.failed) return retval;
                    pushFollow(FOLLOW_statement_in_statement4752);
                    statement();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,WHILE,FOLLOW_WHILE_in_statement4754); if (state.failed) return retval;
                    pushFollow(FOLLOW_parExpression_in_statement4756);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_statement4758); if (state.failed) return retval;

                    }
                    break;
                case 7 :
                    // JWIPreprocessor_Parser.g:924:9: TRY block ( catches FINALLY block | catches | FINALLY block )
                    {
                    match(input,TRY,FOLLOW_TRY_in_statement4768); if (state.failed) return retval;
                    pushFollow(FOLLOW_block_in_statement4770);
                    block();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:925:9: ( catches FINALLY block | catches | FINALLY block )
                    int alt129=3;
                    int LA129_0 = input.LA(1);

                    if ( (LA129_0==CATCH) ) {
                        int LA129_1 = input.LA(2);

                        if ( (synpred188_JWIPreprocessor_Parser()) ) {
                            alt129=1;
                        }
                        else if ( (synpred189_JWIPreprocessor_Parser()) ) {
                            alt129=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 129, 1, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA129_0==FINALLY) ) {
                        alt129=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 129, 0, input);

                        throw nvae;
                    }
                    switch (alt129) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:925:11: catches FINALLY block
                            {
                            pushFollow(FOLLOW_catches_in_statement4782);
                            catches();

                            state._fsp--;
                            if (state.failed) return retval;
                            match(input,FINALLY,FOLLOW_FINALLY_in_statement4784); if (state.failed) return retval;
                            pushFollow(FOLLOW_block_in_statement4786);
                            block();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;
                        case 2 :
                            // JWIPreprocessor_Parser.g:926:11: catches
                            {
                            pushFollow(FOLLOW_catches_in_statement4798);
                            catches();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;
                        case 3 :
                            // JWIPreprocessor_Parser.g:927:13: FINALLY block
                            {
                            match(input,FINALLY,FOLLOW_FINALLY_in_statement4812); if (state.failed) return retval;
                            pushFollow(FOLLOW_block_in_statement4814);
                            block();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 8 :
                    // JWIPreprocessor_Parser.g:929:9: SWITCH parExpression LEFTBRACE switchBlockStatementGroups RIGHTBRACE
                    {
                    match(input,SWITCH,FOLLOW_SWITCH_in_statement4834); if (state.failed) return retval;
                    pushFollow(FOLLOW_parExpression_in_statement4836);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,LEFTBRACE,FOLLOW_LEFTBRACE_in_statement4838); if (state.failed) return retval;
                    pushFollow(FOLLOW_switchBlockStatementGroups_in_statement4840);
                    switchBlockStatementGroups();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,RIGHTBRACE,FOLLOW_RIGHTBRACE_in_statement4842); if (state.failed) return retval;

                    }
                    break;
                case 9 :
                    // JWIPreprocessor_Parser.g:930:9: SYNCHRONIZED parExpression block
                    {
                    match(input,SYNCHRONIZED,FOLLOW_SYNCHRONIZED_in_statement4852); if (state.failed) return retval;
                    pushFollow(FOLLOW_parExpression_in_statement4854);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_block_in_statement4856);
                    block();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // JWIPreprocessor_Parser.g:931:9: RETURN ( expression )? SEMICOLON
                    {
                    match(input,RETURN,FOLLOW_RETURN_in_statement4866); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:931:16: ( expression )?
                    int alt130=2;
                    int LA130_0 = input.LA(1);

                    if ( (LA130_0==BOOLEAN||LA130_0==BYTE||LA130_0==CHAR||LA130_0==DOUBLE||LA130_0==FALSE||LA130_0==FLOAT||LA130_0==INT||LA130_0==LONG||(LA130_0>=NEW && LA130_0<=NULL)||LA130_0==SHORT||LA130_0==SUPER||LA130_0==THIS||LA130_0==TRUE||LA130_0==VOID||LA130_0==LEFTPARENTHESIS||LA130_0==TILDE||(LA130_0>=PLUSPLUS && LA130_0<=PLUS)||(LA130_0>=MINUSMINUS && LA130_0<=MINUS)||LA130_0==EXCLAMATIONMARK||(LA130_0>=HexLiteral && LA130_0<=OctalLiteral)||LA130_0==FloatingPointLiteral||(LA130_0>=CharacterLiteral && LA130_0<=StringLiteral)||LA130_0==Identifier) ) {
                        alt130=1;
                    }
                    switch (alt130) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_statement4868);
                            expression();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_statement4871); if (state.failed) return retval;

                    }
                    break;
                case 11 :
                    // JWIPreprocessor_Parser.g:932:9: THROW expression SEMICOLON
                    {
                    match(input,THROW,FOLLOW_THROW_in_statement4881); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_statement4883);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_statement4885); if (state.failed) return retval;

                    }
                    break;
                case 12 :
                    // JWIPreprocessor_Parser.g:933:9: BREAK ( Identifier )? SEMICOLON
                    {
                    match(input,BREAK,FOLLOW_BREAK_in_statement4895); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:933:15: ( Identifier )?
                    int alt131=2;
                    int LA131_0 = input.LA(1);

                    if ( (LA131_0==Identifier) ) {
                        alt131=1;
                    }
                    switch (alt131) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement4897); if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_statement4900); if (state.failed) return retval;

                    }
                    break;
                case 13 :
                    // JWIPreprocessor_Parser.g:934:9: CONTINUE ( Identifier )? SEMICOLON
                    {
                    match(input,CONTINUE,FOLLOW_CONTINUE_in_statement4910); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:934:18: ( Identifier )?
                    int alt132=2;
                    int LA132_0 = input.LA(1);

                    if ( (LA132_0==Identifier) ) {
                        alt132=1;
                    }
                    switch (alt132) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: Identifier
                            {
                            match(input,Identifier,FOLLOW_Identifier_in_statement4912); if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_statement4915); if (state.failed) return retval;

                    }
                    break;
                case 14 :
                    // JWIPreprocessor_Parser.g:935:9: SEMICOLON
                    {
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_statement4925); if (state.failed) return retval;

                    }
                    break;
                case 15 :
                    // JWIPreprocessor_Parser.g:936:9: statementExpression SEMICOLON
                    {
                    pushFollow(FOLLOW_statementExpression_in_statement4936);
                    statementExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_statement4938); if (state.failed) return retval;

                    }
                    break;
                case 16 :
                    // JWIPreprocessor_Parser.g:937:9: Identifier COLON statement
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_statement4948); if (state.failed) return retval;
                    match(input,COLON,FOLLOW_COLON_in_statement4950); if (state.failed) return retval;
                    pushFollow(FOLLOW_statement_in_statement4952);
                    statement();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 101, statement_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "statement"

    public static class catches_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "catches"
    // JWIPreprocessor_Parser.g:940:1: catches : catchClause ( catchClause )* ;
    public final JWIPreprocessor_Parser.catches_return catches() throws RecognitionException {
        JWIPreprocessor_Parser.catches_return retval = new JWIPreprocessor_Parser.catches_return();
        retval.start = input.LT(1);
        int catches_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 102) ) { return retval; }
            // JWIPreprocessor_Parser.g:941:5: ( catchClause ( catchClause )* )
            // JWIPreprocessor_Parser.g:941:9: catchClause ( catchClause )*
            {
            pushFollow(FOLLOW_catchClause_in_catches4975);
            catchClause();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:941:21: ( catchClause )*
            loop134:
            do {
                int alt134=2;
                int LA134_0 = input.LA(1);

                if ( (LA134_0==CATCH) ) {
                    alt134=1;
                }


                switch (alt134) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:941:22: catchClause
            	    {
            	    pushFollow(FOLLOW_catchClause_in_catches4978);
            	    catchClause();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop134;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 102, catches_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "catches"

    public static class catchClause_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "catchClause"
    // JWIPreprocessor_Parser.g:944:1: catchClause : CATCH LEFTPARENTHESIS formalParameter RIGHTPARENTHESIS block ;
    public final JWIPreprocessor_Parser.catchClause_return catchClause() throws RecognitionException {
        JWIPreprocessor_Parser.catchClause_return retval = new JWIPreprocessor_Parser.catchClause_return();
        retval.start = input.LT(1);
        int catchClause_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 103) ) { return retval; }
            // JWIPreprocessor_Parser.g:945:5: ( CATCH LEFTPARENTHESIS formalParameter RIGHTPARENTHESIS block )
            // JWIPreprocessor_Parser.g:945:9: CATCH LEFTPARENTHESIS formalParameter RIGHTPARENTHESIS block
            {
            match(input,CATCH,FOLLOW_CATCH_in_catchClause5003); if (state.failed) return retval;
            match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_catchClause5005); if (state.failed) return retval;
            pushFollow(FOLLOW_formalParameter_in_catchClause5007);
            formalParameter();

            state._fsp--;
            if (state.failed) return retval;
            match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_catchClause5009); if (state.failed) return retval;
            pushFollow(FOLLOW_block_in_catchClause5011);
            block();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 103, catchClause_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "catchClause"

    public static class formalParameter_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "formalParameter"
    // JWIPreprocessor_Parser.g:948:1: formalParameter : variableModifiers type variableDeclaratorId ;
    public final JWIPreprocessor_Parser.formalParameter_return formalParameter() throws RecognitionException {
        JWIPreprocessor_Parser.formalParameter_return retval = new JWIPreprocessor_Parser.formalParameter_return();
        retval.start = input.LT(1);
        int formalParameter_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 104) ) { return retval; }
            // JWIPreprocessor_Parser.g:949:5: ( variableModifiers type variableDeclaratorId )
            // JWIPreprocessor_Parser.g:949:9: variableModifiers type variableDeclaratorId
            {
            pushFollow(FOLLOW_variableModifiers_in_formalParameter5030);
            variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_type_in_formalParameter5032);
            type();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_variableDeclaratorId_in_formalParameter5034);
            variableDeclaratorId();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 104, formalParameter_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "formalParameter"

    public static class switchBlockStatementGroups_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "switchBlockStatementGroups"
    // JWIPreprocessor_Parser.g:952:1: switchBlockStatementGroups : ( switchBlockStatementGroup )* ;
    public final JWIPreprocessor_Parser.switchBlockStatementGroups_return switchBlockStatementGroups() throws RecognitionException {
        JWIPreprocessor_Parser.switchBlockStatementGroups_return retval = new JWIPreprocessor_Parser.switchBlockStatementGroups_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroups_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 105) ) { return retval; }
            // JWIPreprocessor_Parser.g:953:5: ( ( switchBlockStatementGroup )* )
            // JWIPreprocessor_Parser.g:953:9: ( switchBlockStatementGroup )*
            {
            // JWIPreprocessor_Parser.g:953:9: ( switchBlockStatementGroup )*
            loop135:
            do {
                int alt135=2;
                int LA135_0 = input.LA(1);

                if ( (LA135_0==CASE||LA135_0==DEFAULT) ) {
                    alt135=1;
                }


                switch (alt135) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:953:10: switchBlockStatementGroup
            	    {
            	    pushFollow(FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups5062);
            	    switchBlockStatementGroup();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop135;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 105, switchBlockStatementGroups_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroups"

    public static class switchBlockStatementGroup_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "switchBlockStatementGroup"
    // JWIPreprocessor_Parser.g:960:1: switchBlockStatementGroup : ( switchLabel )+ ( blockStatement )* ;
    public final JWIPreprocessor_Parser.switchBlockStatementGroup_return switchBlockStatementGroup() throws RecognitionException {
        JWIPreprocessor_Parser.switchBlockStatementGroup_return retval = new JWIPreprocessor_Parser.switchBlockStatementGroup_return();
        retval.start = input.LT(1);
        int switchBlockStatementGroup_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 106) ) { return retval; }
            // JWIPreprocessor_Parser.g:961:5: ( ( switchLabel )+ ( blockStatement )* )
            // JWIPreprocessor_Parser.g:961:9: ( switchLabel )+ ( blockStatement )*
            {
            // JWIPreprocessor_Parser.g:961:9: ( switchLabel )+
            int cnt136=0;
            loop136:
            do {
                int alt136=2;
                int LA136_0 = input.LA(1);

                if ( (LA136_0==CASE) ) {
                    int LA136_2 = input.LA(2);

                    if ( (synpred204_JWIPreprocessor_Parser()) ) {
                        alt136=1;
                    }


                }
                else if ( (LA136_0==DEFAULT) ) {
                    int LA136_3 = input.LA(2);

                    if ( (synpred204_JWIPreprocessor_Parser()) ) {
                        alt136=1;
                    }


                }


                switch (alt136) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: switchLabel
            	    {
            	    pushFollow(FOLLOW_switchLabel_in_switchBlockStatementGroup5089);
            	    switchLabel();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    if ( cnt136 >= 1 ) break loop136;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(136, input);
                        throw eee;
                }
                cnt136++;
            } while (true);

            // JWIPreprocessor_Parser.g:961:22: ( blockStatement )*
            loop137:
            do {
                int alt137=2;
                int LA137_0 = input.LA(1);

                if ( ((LA137_0>=ABSTRACT && LA137_0<=BYTE)||(LA137_0>=CHAR && LA137_0<=CONTINUE)||(LA137_0>=DO && LA137_0<=DOUBLE)||(LA137_0>=FALSE && LA137_0<=FINAL)||(LA137_0>=FLOAT && LA137_0<=IF)||(LA137_0>=INT && LA137_0<=LONG)||(LA137_0>=NEW && LA137_0<=NULL)||(LA137_0>=PRIVATE && LA137_0<=THROW)||(LA137_0>=TRUE && LA137_0<=VOID)||LA137_0==SEMICOLON||LA137_0==LEFTBRACE||LA137_0==LEFTPARENTHESIS||(LA137_0>=ATSIGN && LA137_0<=TILDE)||(LA137_0>=PLUSPLUS && LA137_0<=PLUS)||(LA137_0>=MINUSMINUS && LA137_0<=MINUS)||LA137_0==EXCLAMATIONMARK||(LA137_0>=HexLiteral && LA137_0<=OctalLiteral)||LA137_0==FloatingPointLiteral||(LA137_0>=CharacterLiteral && LA137_0<=StringLiteral)||(LA137_0>=ENUM && LA137_0<=ASSERT)||LA137_0==Identifier) ) {
                    alt137=1;
                }


                switch (alt137) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:0:0: blockStatement
            	    {
            	    pushFollow(FOLLOW_blockStatement_in_switchBlockStatementGroup5092);
            	    blockStatement();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop137;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 106, switchBlockStatementGroup_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchBlockStatementGroup"

    public static class switchLabel_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "switchLabel"
    // JWIPreprocessor_Parser.g:964:1: switchLabel : ( CASE constantExpression COLON | CASE enumConstantName COLON | DEFAULT COLON );
    public final JWIPreprocessor_Parser.switchLabel_return switchLabel() throws RecognitionException {
        JWIPreprocessor_Parser.switchLabel_return retval = new JWIPreprocessor_Parser.switchLabel_return();
        retval.start = input.LT(1);
        int switchLabel_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 107) ) { return retval; }
            // JWIPreprocessor_Parser.g:965:5: ( CASE constantExpression COLON | CASE enumConstantName COLON | DEFAULT COLON )
            int alt138=3;
            int LA138_0 = input.LA(1);

            if ( (LA138_0==CASE) ) {
                int LA138_1 = input.LA(2);

                if ( (LA138_1==BOOLEAN||LA138_1==BYTE||LA138_1==CHAR||LA138_1==DOUBLE||LA138_1==FALSE||LA138_1==FLOAT||LA138_1==INT||LA138_1==LONG||(LA138_1>=NEW && LA138_1<=NULL)||LA138_1==SHORT||LA138_1==SUPER||LA138_1==THIS||LA138_1==TRUE||LA138_1==VOID||LA138_1==LEFTPARENTHESIS||LA138_1==TILDE||(LA138_1>=PLUSPLUS && LA138_1<=PLUS)||(LA138_1>=MINUSMINUS && LA138_1<=MINUS)||LA138_1==EXCLAMATIONMARK||(LA138_1>=HexLiteral && LA138_1<=OctalLiteral)||LA138_1==FloatingPointLiteral||(LA138_1>=CharacterLiteral && LA138_1<=StringLiteral)) ) {
                    alt138=1;
                }
                else if ( (LA138_1==Identifier) ) {
                    int LA138_4 = input.LA(3);

                    if ( (LA138_4==COLON) ) {
                        int LA138_5 = input.LA(4);

                        if ( (synpred206_JWIPreprocessor_Parser()) ) {
                            alt138=1;
                        }
                        else if ( (synpred207_JWIPreprocessor_Parser()) ) {
                            alt138=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 138, 5, input);

                            throw nvae;
                        }
                    }
                    else if ( (LA138_4==INSTANCEOF||LA138_4==LEFTPARENTHESIS||LA138_4==LEFTSQUAREBRACKET||(LA138_4>=LESSTHAN && LA138_4<=GREATERTHAN)||LA138_4==DOT||(LA138_4>=QUESTIONMARK && LA138_4<=EXCLAMATIONMARKEQUALS)||(LA138_4>=EQUALITY_EQUALS && LA138_4<=PIPE)) ) {
                        alt138=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 138, 4, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 138, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA138_0==DEFAULT) ) {
                alt138=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 138, 0, input);

                throw nvae;
            }
            switch (alt138) {
                case 1 :
                    // JWIPreprocessor_Parser.g:965:9: CASE constantExpression COLON
                    {
                    match(input,CASE,FOLLOW_CASE_in_switchLabel5116); if (state.failed) return retval;
                    pushFollow(FOLLOW_constantExpression_in_switchLabel5118);
                    constantExpression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,COLON,FOLLOW_COLON_in_switchLabel5120); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:966:9: CASE enumConstantName COLON
                    {
                    match(input,CASE,FOLLOW_CASE_in_switchLabel5130); if (state.failed) return retval;
                    pushFollow(FOLLOW_enumConstantName_in_switchLabel5132);
                    enumConstantName();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,COLON,FOLLOW_COLON_in_switchLabel5134); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:967:9: DEFAULT COLON
                    {
                    match(input,DEFAULT,FOLLOW_DEFAULT_in_switchLabel5144); if (state.failed) return retval;
                    match(input,COLON,FOLLOW_COLON_in_switchLabel5146); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 107, switchLabel_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "switchLabel"

    public static class forControl_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "forControl"
    // JWIPreprocessor_Parser.g:970:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? SEMICOLON ( expression )? SEMICOLON ( forUpdate )? );
    public final JWIPreprocessor_Parser.forControl_return forControl() throws RecognitionException {
        JWIPreprocessor_Parser.forControl_return retval = new JWIPreprocessor_Parser.forControl_return();
        retval.start = input.LT(1);
        int forControl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 108) ) { return retval; }
            // JWIPreprocessor_Parser.g:972:5: ( enhancedForControl | ( forInit )? SEMICOLON ( expression )? SEMICOLON ( forUpdate )? )
            int alt142=2;
            alt142 = dfa142.predict(input);
            switch (alt142) {
                case 1 :
                    // JWIPreprocessor_Parser.g:972:9: enhancedForControl
                    {
                    pushFollow(FOLLOW_enhancedForControl_in_forControl5177);
                    enhancedForControl();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:973:9: ( forInit )? SEMICOLON ( expression )? SEMICOLON ( forUpdate )?
                    {
                    // JWIPreprocessor_Parser.g:973:9: ( forInit )?
                    int alt139=2;
                    int LA139_0 = input.LA(1);

                    if ( (LA139_0==BOOLEAN||LA139_0==BYTE||LA139_0==CHAR||LA139_0==DOUBLE||(LA139_0>=FALSE && LA139_0<=FINAL)||LA139_0==FLOAT||LA139_0==INT||LA139_0==LONG||(LA139_0>=NEW && LA139_0<=NULL)||LA139_0==SHORT||LA139_0==SUPER||LA139_0==THIS||LA139_0==TRUE||LA139_0==VOID||LA139_0==LEFTPARENTHESIS||(LA139_0>=ATSIGN && LA139_0<=TILDE)||(LA139_0>=PLUSPLUS && LA139_0<=PLUS)||(LA139_0>=MINUSMINUS && LA139_0<=MINUS)||LA139_0==EXCLAMATIONMARK||(LA139_0>=HexLiteral && LA139_0<=OctalLiteral)||LA139_0==FloatingPointLiteral||(LA139_0>=CharacterLiteral && LA139_0<=StringLiteral)||LA139_0==Identifier) ) {
                        alt139=1;
                    }
                    switch (alt139) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: forInit
                            {
                            pushFollow(FOLLOW_forInit_in_forControl5187);
                            forInit();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_forControl5190); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:973:28: ( expression )?
                    int alt140=2;
                    int LA140_0 = input.LA(1);

                    if ( (LA140_0==BOOLEAN||LA140_0==BYTE||LA140_0==CHAR||LA140_0==DOUBLE||LA140_0==FALSE||LA140_0==FLOAT||LA140_0==INT||LA140_0==LONG||(LA140_0>=NEW && LA140_0<=NULL)||LA140_0==SHORT||LA140_0==SUPER||LA140_0==THIS||LA140_0==TRUE||LA140_0==VOID||LA140_0==LEFTPARENTHESIS||LA140_0==TILDE||(LA140_0>=PLUSPLUS && LA140_0<=PLUS)||(LA140_0>=MINUSMINUS && LA140_0<=MINUS)||LA140_0==EXCLAMATIONMARK||(LA140_0>=HexLiteral && LA140_0<=OctalLiteral)||LA140_0==FloatingPointLiteral||(LA140_0>=CharacterLiteral && LA140_0<=StringLiteral)||LA140_0==Identifier) ) {
                        alt140=1;
                    }
                    switch (alt140) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: expression
                            {
                            pushFollow(FOLLOW_expression_in_forControl5192);
                            expression();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_forControl5195); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:973:50: ( forUpdate )?
                    int alt141=2;
                    int LA141_0 = input.LA(1);

                    if ( (LA141_0==BOOLEAN||LA141_0==BYTE||LA141_0==CHAR||LA141_0==DOUBLE||LA141_0==FALSE||LA141_0==FLOAT||LA141_0==INT||LA141_0==LONG||(LA141_0>=NEW && LA141_0<=NULL)||LA141_0==SHORT||LA141_0==SUPER||LA141_0==THIS||LA141_0==TRUE||LA141_0==VOID||LA141_0==LEFTPARENTHESIS||LA141_0==TILDE||(LA141_0>=PLUSPLUS && LA141_0<=PLUS)||(LA141_0>=MINUSMINUS && LA141_0<=MINUS)||LA141_0==EXCLAMATIONMARK||(LA141_0>=HexLiteral && LA141_0<=OctalLiteral)||LA141_0==FloatingPointLiteral||(LA141_0>=CharacterLiteral && LA141_0<=StringLiteral)||LA141_0==Identifier) ) {
                        alt141=1;
                    }
                    switch (alt141) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: forUpdate
                            {
                            pushFollow(FOLLOW_forUpdate_in_forControl5197);
                            forUpdate();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 108, forControl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forControl"

    public static class forInit_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "forInit"
    // JWIPreprocessor_Parser.g:976:1: forInit : ( localVariableDeclaration | expressionList );
    public final JWIPreprocessor_Parser.forInit_return forInit() throws RecognitionException {
        JWIPreprocessor_Parser.forInit_return retval = new JWIPreprocessor_Parser.forInit_return();
        retval.start = input.LT(1);
        int forInit_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 109) ) { return retval; }
            // JWIPreprocessor_Parser.g:977:5: ( localVariableDeclaration | expressionList )
            int alt143=2;
            alt143 = dfa143.predict(input);
            switch (alt143) {
                case 1 :
                    // JWIPreprocessor_Parser.g:977:9: localVariableDeclaration
                    {
                    pushFollow(FOLLOW_localVariableDeclaration_in_forInit5217);
                    localVariableDeclaration();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:978:9: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_forInit5227);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 109, forInit_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forInit"

    public static class enhancedForControl_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "enhancedForControl"
    // JWIPreprocessor_Parser.g:981:1: enhancedForControl : variableModifiers type Identifier COLON expression ;
    public final JWIPreprocessor_Parser.enhancedForControl_return enhancedForControl() throws RecognitionException {
        JWIPreprocessor_Parser.enhancedForControl_return retval = new JWIPreprocessor_Parser.enhancedForControl_return();
        retval.start = input.LT(1);
        int enhancedForControl_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 110) ) { return retval; }
            // JWIPreprocessor_Parser.g:982:5: ( variableModifiers type Identifier COLON expression )
            // JWIPreprocessor_Parser.g:982:9: variableModifiers type Identifier COLON expression
            {
            pushFollow(FOLLOW_variableModifiers_in_enhancedForControl5250);
            variableModifiers();

            state._fsp--;
            if (state.failed) return retval;
            pushFollow(FOLLOW_type_in_enhancedForControl5252);
            type();

            state._fsp--;
            if (state.failed) return retval;
            match(input,Identifier,FOLLOW_Identifier_in_enhancedForControl5254); if (state.failed) return retval;
            match(input,COLON,FOLLOW_COLON_in_enhancedForControl5256); if (state.failed) return retval;
            pushFollow(FOLLOW_expression_in_enhancedForControl5258);
            expression();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 110, enhancedForControl_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "enhancedForControl"

    public static class forUpdate_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "forUpdate"
    // JWIPreprocessor_Parser.g:985:1: forUpdate : expressionList ;
    public final JWIPreprocessor_Parser.forUpdate_return forUpdate() throws RecognitionException {
        JWIPreprocessor_Parser.forUpdate_return retval = new JWIPreprocessor_Parser.forUpdate_return();
        retval.start = input.LT(1);
        int forUpdate_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 111) ) { return retval; }
            // JWIPreprocessor_Parser.g:986:5: ( expressionList )
            // JWIPreprocessor_Parser.g:986:9: expressionList
            {
            pushFollow(FOLLOW_expressionList_in_forUpdate5277);
            expressionList();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 111, forUpdate_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "forUpdate"

    public static class parExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "parExpression"
    // JWIPreprocessor_Parser.g:991:1: parExpression : LEFTPARENTHESIS expression RIGHTPARENTHESIS ;
    public final JWIPreprocessor_Parser.parExpression_return parExpression() throws RecognitionException {
        JWIPreprocessor_Parser.parExpression_return retval = new JWIPreprocessor_Parser.parExpression_return();
        retval.start = input.LT(1);
        int parExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 112) ) { return retval; }
            // JWIPreprocessor_Parser.g:992:5: ( LEFTPARENTHESIS expression RIGHTPARENTHESIS )
            // JWIPreprocessor_Parser.g:992:9: LEFTPARENTHESIS expression RIGHTPARENTHESIS
            {
            match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_parExpression5298); if (state.failed) return retval;
            pushFollow(FOLLOW_expression_in_parExpression5300);
            expression();

            state._fsp--;
            if (state.failed) return retval;
            match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_parExpression5302); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 112, parExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "parExpression"

    public static class expressionList_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "expressionList"
    // JWIPreprocessor_Parser.g:995:1: expressionList : expression ( COMMA expression )* ;
    public final JWIPreprocessor_Parser.expressionList_return expressionList() throws RecognitionException {
        JWIPreprocessor_Parser.expressionList_return retval = new JWIPreprocessor_Parser.expressionList_return();
        retval.start = input.LT(1);
        int expressionList_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 113) ) { return retval; }
            // JWIPreprocessor_Parser.g:996:5: ( expression ( COMMA expression )* )
            // JWIPreprocessor_Parser.g:996:9: expression ( COMMA expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList5325);
            expression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:996:20: ( COMMA expression )*
            loop144:
            do {
                int alt144=2;
                int LA144_0 = input.LA(1);

                if ( (LA144_0==COMMA) ) {
                    alt144=1;
                }


                switch (alt144) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:996:21: COMMA expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList5328); if (state.failed) return retval;
            	    pushFollow(FOLLOW_expression_in_expressionList5330);
            	    expression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop144;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 113, expressionList_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expressionList"

    public static class statementExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "statementExpression"
    // JWIPreprocessor_Parser.g:999:1: statementExpression : expression ;
    public final JWIPreprocessor_Parser.statementExpression_return statementExpression() throws RecognitionException {
        JWIPreprocessor_Parser.statementExpression_return retval = new JWIPreprocessor_Parser.statementExpression_return();
        retval.start = input.LT(1);
        int statementExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 114) ) { return retval; }
            // JWIPreprocessor_Parser.g:1000:5: ( expression )
            // JWIPreprocessor_Parser.g:1000:9: expression
            {
            pushFollow(FOLLOW_expression_in_statementExpression5351);
            expression();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 114, statementExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "statementExpression"

    public static class constantExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "constantExpression"
    // JWIPreprocessor_Parser.g:1003:1: constantExpression : expression ;
    public final JWIPreprocessor_Parser.constantExpression_return constantExpression() throws RecognitionException {
        JWIPreprocessor_Parser.constantExpression_return retval = new JWIPreprocessor_Parser.constantExpression_return();
        retval.start = input.LT(1);
        int constantExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 115) ) { return retval; }
            // JWIPreprocessor_Parser.g:1004:5: ( expression )
            // JWIPreprocessor_Parser.g:1004:9: expression
            {
            pushFollow(FOLLOW_expression_in_constantExpression5374);
            expression();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 115, constantExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "constantExpression"

    public static class expression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "expression"
    // JWIPreprocessor_Parser.g:1007:1: expression : conditionalExpression ( assignmentOperator expression )? ;
    public final JWIPreprocessor_Parser.expression_return expression() throws RecognitionException {
        JWIPreprocessor_Parser.expression_return retval = new JWIPreprocessor_Parser.expression_return();
        retval.start = input.LT(1);
        int expression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 116) ) { return retval; }
            // JWIPreprocessor_Parser.g:1008:5: ( conditionalExpression ( assignmentOperator expression )? )
            // JWIPreprocessor_Parser.g:1008:9: conditionalExpression ( assignmentOperator expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression5397);
            conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1008:31: ( assignmentOperator expression )?
            int alt145=2;
            alt145 = dfa145.predict(input);
            switch (alt145) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1008:32: assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression5400);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_expression5402);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 116, expression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "expression"

    public static class assignmentOperator_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "assignmentOperator"
    // JWIPreprocessor_Parser.g:1011:1: assignmentOperator : ( ASSIGNMENT_EQUALS | PLUSEQUALS | MINUSEQUALS | ASTERISKEQUALS | SLASHEQUALS | BITWISE_AND_EQUALS | BITWISE_OR_EQUALS | CARETEQUALS | PERCENTEQUALS | ( LESSTHAN LESSTHAN ASSIGNMENT_EQUALS )=>t1= LESSTHAN t2= LESSTHAN t3= ASSIGNMENT_EQUALS {...}? | ( GREATERTHAN GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= GREATERTHAN t3= GREATERTHAN t4= ASSIGNMENT_EQUALS {...}? | ( GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= GREATERTHAN t3= ASSIGNMENT_EQUALS {...}?);
    public final JWIPreprocessor_Parser.assignmentOperator_return assignmentOperator() throws RecognitionException {
        JWIPreprocessor_Parser.assignmentOperator_return retval = new JWIPreprocessor_Parser.assignmentOperator_return();
        retval.start = input.LT(1);
        int assignmentOperator_StartIndex = input.index();
        Token t1=null;
        Token t2=null;
        Token t3=null;
        Token t4=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 117) ) { return retval; }
            // JWIPreprocessor_Parser.g:1012:5: ( ASSIGNMENT_EQUALS | PLUSEQUALS | MINUSEQUALS | ASTERISKEQUALS | SLASHEQUALS | BITWISE_AND_EQUALS | BITWISE_OR_EQUALS | CARETEQUALS | PERCENTEQUALS | ( LESSTHAN LESSTHAN ASSIGNMENT_EQUALS )=>t1= LESSTHAN t2= LESSTHAN t3= ASSIGNMENT_EQUALS {...}? | ( GREATERTHAN GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= GREATERTHAN t3= GREATERTHAN t4= ASSIGNMENT_EQUALS {...}? | ( GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= GREATERTHAN t3= ASSIGNMENT_EQUALS {...}?)
            int alt146=12;
            alt146 = dfa146.predict(input);
            switch (alt146) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1012:9: ASSIGNMENT_EQUALS
                    {
                    match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_assignmentOperator5427); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1013:9: PLUSEQUALS
                    {
                    match(input,PLUSEQUALS,FOLLOW_PLUSEQUALS_in_assignmentOperator5437); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:1014:9: MINUSEQUALS
                    {
                    match(input,MINUSEQUALS,FOLLOW_MINUSEQUALS_in_assignmentOperator5447); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:1015:9: ASTERISKEQUALS
                    {
                    match(input,ASTERISKEQUALS,FOLLOW_ASTERISKEQUALS_in_assignmentOperator5457); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:1016:9: SLASHEQUALS
                    {
                    match(input,SLASHEQUALS,FOLLOW_SLASHEQUALS_in_assignmentOperator5467); if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // JWIPreprocessor_Parser.g:1017:9: BITWISE_AND_EQUALS
                    {
                    match(input,BITWISE_AND_EQUALS,FOLLOW_BITWISE_AND_EQUALS_in_assignmentOperator5477); if (state.failed) return retval;

                    }
                    break;
                case 7 :
                    // JWIPreprocessor_Parser.g:1018:9: BITWISE_OR_EQUALS
                    {
                    match(input,BITWISE_OR_EQUALS,FOLLOW_BITWISE_OR_EQUALS_in_assignmentOperator5487); if (state.failed) return retval;

                    }
                    break;
                case 8 :
                    // JWIPreprocessor_Parser.g:1019:9: CARETEQUALS
                    {
                    match(input,CARETEQUALS,FOLLOW_CARETEQUALS_in_assignmentOperator5497); if (state.failed) return retval;

                    }
                    break;
                case 9 :
                    // JWIPreprocessor_Parser.g:1020:9: PERCENTEQUALS
                    {
                    match(input,PERCENTEQUALS,FOLLOW_PERCENTEQUALS_in_assignmentOperator5507); if (state.failed) return retval;

                    }
                    break;
                case 10 :
                    // JWIPreprocessor_Parser.g:1021:9: ( LESSTHAN LESSTHAN ASSIGNMENT_EQUALS )=>t1= LESSTHAN t2= LESSTHAN t3= ASSIGNMENT_EQUALS {...}?
                    {
                    t1=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_assignmentOperator5528); if (state.failed) return retval;
                    t2=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_assignmentOperator5532); if (state.failed) return retval;
                    t3=(Token)match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_assignmentOperator5536); if (state.failed) return retval;
                    if ( !(( t1.getLine() == t2.getLine() &&
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() && 
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() &&\n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && \n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 11 :
                    // JWIPreprocessor_Parser.g:1026:9: ( GREATERTHAN GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= GREATERTHAN t3= GREATERTHAN t4= ASSIGNMENT_EQUALS {...}?
                    {
                    t1=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_assignmentOperator5570); if (state.failed) return retval;
                    t2=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_assignmentOperator5574); if (state.failed) return retval;
                    t3=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_assignmentOperator5578); if (state.failed) return retval;
                    t4=(Token)match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_assignmentOperator5582); if (state.failed) return retval;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() &&
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() &&
                              t3.getLine() == t4.getLine() && 
                              t3.getCharPositionInLine() + 1 == t4.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&\n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() &&\n          $t3.getLine() == $t4.getLine() && \n          $t3.getCharPositionInLine() + 1 == $t4.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 12 :
                    // JWIPreprocessor_Parser.g:1033:9: ( GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= GREATERTHAN t3= ASSIGNMENT_EQUALS {...}?
                    {
                    t1=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_assignmentOperator5613); if (state.failed) return retval;
                    t2=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_assignmentOperator5617); if (state.failed) return retval;
                    t3=(Token)match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_assignmentOperator5621); if (state.failed) return retval;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() && 
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "assignmentOperator", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && \n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 117, assignmentOperator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "assignmentOperator"

    public static class conditionalExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "conditionalExpression"
    // JWIPreprocessor_Parser.g:1040:1: conditionalExpression : conditionalOrExpression ( QUESTIONMARK expression COLON expression )? ;
    public final JWIPreprocessor_Parser.conditionalExpression_return conditionalExpression() throws RecognitionException {
        JWIPreprocessor_Parser.conditionalExpression_return retval = new JWIPreprocessor_Parser.conditionalExpression_return();
        retval.start = input.LT(1);
        int conditionalExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 118) ) { return retval; }
            // JWIPreprocessor_Parser.g:1041:5: ( conditionalOrExpression ( QUESTIONMARK expression COLON expression )? )
            // JWIPreprocessor_Parser.g:1041:9: conditionalOrExpression ( QUESTIONMARK expression COLON expression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression5650);
            conditionalOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1041:33: ( QUESTIONMARK expression COLON expression )?
            int alt147=2;
            int LA147_0 = input.LA(1);

            if ( (LA147_0==QUESTIONMARK) ) {
                alt147=1;
            }
            switch (alt147) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1041:35: QUESTIONMARK expression COLON expression
                    {
                    match(input,QUESTIONMARK,FOLLOW_QUESTIONMARK_in_conditionalExpression5654); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_conditionalExpression5656);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,COLON,FOLLOW_COLON_in_conditionalExpression5658); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_conditionalExpression5660);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 118, conditionalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalExpression"

    public static class conditionalOrExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "conditionalOrExpression"
    // JWIPreprocessor_Parser.g:1044:1: conditionalOrExpression : conditionalAndExpression ( LOGICAL_OR conditionalAndExpression )* ;
    public final JWIPreprocessor_Parser.conditionalOrExpression_return conditionalOrExpression() throws RecognitionException {
        JWIPreprocessor_Parser.conditionalOrExpression_return retval = new JWIPreprocessor_Parser.conditionalOrExpression_return();
        retval.start = input.LT(1);
        int conditionalOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 119) ) { return retval; }
            // JWIPreprocessor_Parser.g:1045:5: ( conditionalAndExpression ( LOGICAL_OR conditionalAndExpression )* )
            // JWIPreprocessor_Parser.g:1045:9: conditionalAndExpression ( LOGICAL_OR conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5682);
            conditionalAndExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1045:34: ( LOGICAL_OR conditionalAndExpression )*
            loop148:
            do {
                int alt148=2;
                int LA148_0 = input.LA(1);

                if ( (LA148_0==LOGICAL_OR) ) {
                    alt148=1;
                }


                switch (alt148) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1045:36: LOGICAL_OR conditionalAndExpression
            	    {
            	    match(input,LOGICAL_OR,FOLLOW_LOGICAL_OR_in_conditionalOrExpression5686); if (state.failed) return retval;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression5688);
            	    conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop148;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 119, conditionalOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalOrExpression"

    public static class conditionalAndExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "conditionalAndExpression"
    // JWIPreprocessor_Parser.g:1048:1: conditionalAndExpression : inclusiveOrExpression ( LOGICAL_AND inclusiveOrExpression )* ;
    public final JWIPreprocessor_Parser.conditionalAndExpression_return conditionalAndExpression() throws RecognitionException {
        JWIPreprocessor_Parser.conditionalAndExpression_return retval = new JWIPreprocessor_Parser.conditionalAndExpression_return();
        retval.start = input.LT(1);
        int conditionalAndExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 120) ) { return retval; }
            // JWIPreprocessor_Parser.g:1049:5: ( inclusiveOrExpression ( LOGICAL_AND inclusiveOrExpression )* )
            // JWIPreprocessor_Parser.g:1049:9: inclusiveOrExpression ( LOGICAL_AND inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5710);
            inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1049:31: ( LOGICAL_AND inclusiveOrExpression )*
            loop149:
            do {
                int alt149=2;
                int LA149_0 = input.LA(1);

                if ( (LA149_0==LOGICAL_AND) ) {
                    alt149=1;
                }


                switch (alt149) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1049:33: LOGICAL_AND inclusiveOrExpression
            	    {
            	    match(input,LOGICAL_AND,FOLLOW_LOGICAL_AND_in_conditionalAndExpression5714); if (state.failed) return retval;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5716);
            	    inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop149;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 120, conditionalAndExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "conditionalAndExpression"

    public static class inclusiveOrExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "inclusiveOrExpression"
    // JWIPreprocessor_Parser.g:1052:1: inclusiveOrExpression : exclusiveOrExpression ( PIPE exclusiveOrExpression )* ;
    public final JWIPreprocessor_Parser.inclusiveOrExpression_return inclusiveOrExpression() throws RecognitionException {
        JWIPreprocessor_Parser.inclusiveOrExpression_return retval = new JWIPreprocessor_Parser.inclusiveOrExpression_return();
        retval.start = input.LT(1);
        int inclusiveOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 121) ) { return retval; }
            // JWIPreprocessor_Parser.g:1053:5: ( exclusiveOrExpression ( PIPE exclusiveOrExpression )* )
            // JWIPreprocessor_Parser.g:1053:9: exclusiveOrExpression ( PIPE exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5738);
            exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1053:31: ( PIPE exclusiveOrExpression )*
            loop150:
            do {
                int alt150=2;
                int LA150_0 = input.LA(1);

                if ( (LA150_0==PIPE) ) {
                    alt150=1;
                }


                switch (alt150) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1053:33: PIPE exclusiveOrExpression
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression5742); if (state.failed) return retval;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5746);
            	    exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop150;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 121, inclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "inclusiveOrExpression"

    public static class exclusiveOrExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "exclusiveOrExpression"
    // JWIPreprocessor_Parser.g:1056:1: exclusiveOrExpression : andExpression ( CARET andExpression )* ;
    public final JWIPreprocessor_Parser.exclusiveOrExpression_return exclusiveOrExpression() throws RecognitionException {
        JWIPreprocessor_Parser.exclusiveOrExpression_return retval = new JWIPreprocessor_Parser.exclusiveOrExpression_return();
        retval.start = input.LT(1);
        int exclusiveOrExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 122) ) { return retval; }
            // JWIPreprocessor_Parser.g:1057:5: ( andExpression ( CARET andExpression )* )
            // JWIPreprocessor_Parser.g:1057:9: andExpression ( CARET andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5768);
            andExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1057:23: ( CARET andExpression )*
            loop151:
            do {
                int alt151=2;
                int LA151_0 = input.LA(1);

                if ( (LA151_0==CARET) ) {
                    alt151=1;
                }


                switch (alt151) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1057:25: CARET andExpression
            	    {
            	    match(input,CARET,FOLLOW_CARET_in_exclusiveOrExpression5772); if (state.failed) return retval;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression5774);
            	    andExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop151;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 122, exclusiveOrExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "exclusiveOrExpression"

    public static class andExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "andExpression"
    // JWIPreprocessor_Parser.g:1060:1: andExpression : equalityExpression ( BITWISE_AND equalityExpression )* ;
    public final JWIPreprocessor_Parser.andExpression_return andExpression() throws RecognitionException {
        JWIPreprocessor_Parser.andExpression_return retval = new JWIPreprocessor_Parser.andExpression_return();
        retval.start = input.LT(1);
        int andExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 123) ) { return retval; }
            // JWIPreprocessor_Parser.g:1061:5: ( equalityExpression ( BITWISE_AND equalityExpression )* )
            // JWIPreprocessor_Parser.g:1061:9: equalityExpression ( BITWISE_AND equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression5796);
            equalityExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1061:28: ( BITWISE_AND equalityExpression )*
            loop152:
            do {
                int alt152=2;
                int LA152_0 = input.LA(1);

                if ( (LA152_0==BITWISE_AND) ) {
                    alt152=1;
                }


                switch (alt152) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1061:30: BITWISE_AND equalityExpression
            	    {
            	    match(input,BITWISE_AND,FOLLOW_BITWISE_AND_in_andExpression5800); if (state.failed) return retval;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression5802);
            	    equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop152;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 123, andExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "andExpression"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "equalityExpression"
    // JWIPreprocessor_Parser.g:1064:1: equalityExpression : instanceOfExpression ( ( EQUALITY_EQUALS | EXCLAMATIONMARKEQUALS ) instanceOfExpression )* ;
    public final JWIPreprocessor_Parser.equalityExpression_return equalityExpression() throws RecognitionException {
        JWIPreprocessor_Parser.equalityExpression_return retval = new JWIPreprocessor_Parser.equalityExpression_return();
        retval.start = input.LT(1);
        int equalityExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 124) ) { return retval; }
            // JWIPreprocessor_Parser.g:1065:5: ( instanceOfExpression ( ( EQUALITY_EQUALS | EXCLAMATIONMARKEQUALS ) instanceOfExpression )* )
            // JWIPreprocessor_Parser.g:1065:9: instanceOfExpression ( ( EQUALITY_EQUALS | EXCLAMATIONMARKEQUALS ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5824);
            instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1065:30: ( ( EQUALITY_EQUALS | EXCLAMATIONMARKEQUALS ) instanceOfExpression )*
            loop153:
            do {
                int alt153=2;
                int LA153_0 = input.LA(1);

                if ( (LA153_0==EXCLAMATIONMARKEQUALS||LA153_0==EQUALITY_EQUALS) ) {
                    alt153=1;
                }


                switch (alt153) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1065:32: ( EQUALITY_EQUALS | EXCLAMATIONMARKEQUALS ) instanceOfExpression
            	    {
            	    if ( input.LA(1)==EXCLAMATIONMARKEQUALS||input.LA(1)==EQUALITY_EQUALS ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression5836);
            	    instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop153;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 124, equalityExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "equalityExpression"

    public static class instanceOfExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "instanceOfExpression"
    // JWIPreprocessor_Parser.g:1068:1: instanceOfExpression : relationalExpression ( INSTANCEOF type )? ;
    public final JWIPreprocessor_Parser.instanceOfExpression_return instanceOfExpression() throws RecognitionException {
        JWIPreprocessor_Parser.instanceOfExpression_return retval = new JWIPreprocessor_Parser.instanceOfExpression_return();
        retval.start = input.LT(1);
        int instanceOfExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 125) ) { return retval; }
            // JWIPreprocessor_Parser.g:1069:5: ( relationalExpression ( INSTANCEOF type )? )
            // JWIPreprocessor_Parser.g:1069:9: relationalExpression ( INSTANCEOF type )?
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression5858);
            relationalExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1069:30: ( INSTANCEOF type )?
            int alt154=2;
            int LA154_0 = input.LA(1);

            if ( (LA154_0==INSTANCEOF) ) {
                alt154=1;
            }
            switch (alt154) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1069:31: INSTANCEOF type
                    {
                    match(input,INSTANCEOF,FOLLOW_INSTANCEOF_in_instanceOfExpression5861); if (state.failed) return retval;
                    pushFollow(FOLLOW_type_in_instanceOfExpression5863);
                    type();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 125, instanceOfExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "instanceOfExpression"

    public static class relationalExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "relationalExpression"
    // JWIPreprocessor_Parser.g:1072:1: relationalExpression : shiftExpression ( relationalOp shiftExpression )* ;
    public final JWIPreprocessor_Parser.relationalExpression_return relationalExpression() throws RecognitionException {
        JWIPreprocessor_Parser.relationalExpression_return retval = new JWIPreprocessor_Parser.relationalExpression_return();
        retval.start = input.LT(1);
        int relationalExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 126) ) { return retval; }
            // JWIPreprocessor_Parser.g:1073:5: ( shiftExpression ( relationalOp shiftExpression )* )
            // JWIPreprocessor_Parser.g:1073:9: shiftExpression ( relationalOp shiftExpression )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression5884);
            shiftExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1073:25: ( relationalOp shiftExpression )*
            loop155:
            do {
                int alt155=2;
                int LA155_0 = input.LA(1);

                if ( (LA155_0==LESSTHAN) ) {
                    int LA155_2 = input.LA(2);

                    if ( (LA155_2==BOOLEAN||LA155_2==BYTE||LA155_2==CHAR||LA155_2==DOUBLE||LA155_2==FALSE||LA155_2==FLOAT||LA155_2==INT||LA155_2==LONG||(LA155_2>=NEW && LA155_2<=NULL)||LA155_2==SHORT||LA155_2==SUPER||LA155_2==THIS||LA155_2==TRUE||LA155_2==VOID||LA155_2==LEFTPARENTHESIS||LA155_2==TILDE||(LA155_2>=PLUSPLUS && LA155_2<=PLUS)||(LA155_2>=MINUSMINUS && LA155_2<=MINUS)||LA155_2==EXCLAMATIONMARK||LA155_2==ASSIGNMENT_EQUALS||(LA155_2>=HexLiteral && LA155_2<=OctalLiteral)||LA155_2==FloatingPointLiteral||(LA155_2>=CharacterLiteral && LA155_2<=StringLiteral)||LA155_2==Identifier) ) {
                        alt155=1;
                    }


                }
                else if ( (LA155_0==GREATERTHAN) ) {
                    int LA155_3 = input.LA(2);

                    if ( (LA155_3==BOOLEAN||LA155_3==BYTE||LA155_3==CHAR||LA155_3==DOUBLE||LA155_3==FALSE||LA155_3==FLOAT||LA155_3==INT||LA155_3==LONG||(LA155_3>=NEW && LA155_3<=NULL)||LA155_3==SHORT||LA155_3==SUPER||LA155_3==THIS||LA155_3==TRUE||LA155_3==VOID||LA155_3==LEFTPARENTHESIS||LA155_3==TILDE||(LA155_3>=PLUSPLUS && LA155_3<=PLUS)||(LA155_3>=MINUSMINUS && LA155_3<=MINUS)||LA155_3==EXCLAMATIONMARK||LA155_3==ASSIGNMENT_EQUALS||(LA155_3>=HexLiteral && LA155_3<=OctalLiteral)||LA155_3==FloatingPointLiteral||(LA155_3>=CharacterLiteral && LA155_3<=StringLiteral)||LA155_3==Identifier) ) {
                        alt155=1;
                    }


                }


                switch (alt155) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1073:27: relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression5888);
            	    relationalOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression5890);
            	    shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop155;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 126, relationalExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalExpression"

    public static class relationalOp_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "relationalOp"
    // JWIPreprocessor_Parser.g:1076:1: relationalOp : ( ( LESSTHAN ASSIGNMENT_EQUALS )=>t1= LESSTHAN t2= ASSIGNMENT_EQUALS {...}? | ( GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= ASSIGNMENT_EQUALS {...}? | LESSTHAN | GREATERTHAN );
    public final JWIPreprocessor_Parser.relationalOp_return relationalOp() throws RecognitionException {
        JWIPreprocessor_Parser.relationalOp_return retval = new JWIPreprocessor_Parser.relationalOp_return();
        retval.start = input.LT(1);
        int relationalOp_StartIndex = input.index();
        Token t1=null;
        Token t2=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 127) ) { return retval; }
            // JWIPreprocessor_Parser.g:1077:5: ( ( LESSTHAN ASSIGNMENT_EQUALS )=>t1= LESSTHAN t2= ASSIGNMENT_EQUALS {...}? | ( GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= ASSIGNMENT_EQUALS {...}? | LESSTHAN | GREATERTHAN )
            int alt156=4;
            int LA156_0 = input.LA(1);

            if ( (LA156_0==LESSTHAN) ) {
                int LA156_1 = input.LA(2);

                if ( (LA156_1==ASSIGNMENT_EQUALS) && (synpred237_JWIPreprocessor_Parser())) {
                    alt156=1;
                }
                else if ( (LA156_1==BOOLEAN||LA156_1==BYTE||LA156_1==CHAR||LA156_1==DOUBLE||LA156_1==FALSE||LA156_1==FLOAT||LA156_1==INT||LA156_1==LONG||(LA156_1>=NEW && LA156_1<=NULL)||LA156_1==SHORT||LA156_1==SUPER||LA156_1==THIS||LA156_1==TRUE||LA156_1==VOID||LA156_1==LEFTPARENTHESIS||LA156_1==TILDE||(LA156_1>=PLUSPLUS && LA156_1<=PLUS)||(LA156_1>=MINUSMINUS && LA156_1<=MINUS)||LA156_1==EXCLAMATIONMARK||(LA156_1>=HexLiteral && LA156_1<=OctalLiteral)||LA156_1==FloatingPointLiteral||(LA156_1>=CharacterLiteral && LA156_1<=StringLiteral)||LA156_1==Identifier) ) {
                    alt156=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 156, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA156_0==GREATERTHAN) ) {
                int LA156_2 = input.LA(2);

                if ( (LA156_2==ASSIGNMENT_EQUALS) && (synpred238_JWIPreprocessor_Parser())) {
                    alt156=2;
                }
                else if ( (LA156_2==BOOLEAN||LA156_2==BYTE||LA156_2==CHAR||LA156_2==DOUBLE||LA156_2==FALSE||LA156_2==FLOAT||LA156_2==INT||LA156_2==LONG||(LA156_2>=NEW && LA156_2<=NULL)||LA156_2==SHORT||LA156_2==SUPER||LA156_2==THIS||LA156_2==TRUE||LA156_2==VOID||LA156_2==LEFTPARENTHESIS||LA156_2==TILDE||(LA156_2>=PLUSPLUS && LA156_2<=PLUS)||(LA156_2>=MINUSMINUS && LA156_2<=MINUS)||LA156_2==EXCLAMATIONMARK||(LA156_2>=HexLiteral && LA156_2<=OctalLiteral)||LA156_2==FloatingPointLiteral||(LA156_2>=CharacterLiteral && LA156_2<=StringLiteral)||LA156_2==Identifier) ) {
                    alt156=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 156, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 156, 0, input);

                throw nvae;
            }
            switch (alt156) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1077:9: ( LESSTHAN ASSIGNMENT_EQUALS )=>t1= LESSTHAN t2= ASSIGNMENT_EQUALS {...}?
                    {
                    t1=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_relationalOp5925); if (state.failed) return retval;
                    t2=(Token)match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_relationalOp5929); if (state.failed) return retval;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "relationalOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1080:9: ( GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= ASSIGNMENT_EQUALS {...}?
                    {
                    t1=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_relationalOp5959); if (state.failed) return retval;
                    t2=(Token)match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_relationalOp5963); if (state.failed) return retval;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "relationalOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:1083:9: LESSTHAN
                    {
                    match(input,LESSTHAN,FOLLOW_LESSTHAN_in_relationalOp5984); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:1084:9: GREATERTHAN
                    {
                    match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_relationalOp5995); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 127, relationalOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "relationalOp"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "shiftExpression"
    // JWIPreprocessor_Parser.g:1087:1: shiftExpression : additiveExpression ( shiftOp additiveExpression )* ;
    public final JWIPreprocessor_Parser.shiftExpression_return shiftExpression() throws RecognitionException {
        JWIPreprocessor_Parser.shiftExpression_return retval = new JWIPreprocessor_Parser.shiftExpression_return();
        retval.start = input.LT(1);
        int shiftExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 128) ) { return retval; }
            // JWIPreprocessor_Parser.g:1088:5: ( additiveExpression ( shiftOp additiveExpression )* )
            // JWIPreprocessor_Parser.g:1088:9: additiveExpression ( shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression6015);
            additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1088:28: ( shiftOp additiveExpression )*
            loop157:
            do {
                int alt157=2;
                int LA157_0 = input.LA(1);

                if ( (LA157_0==LESSTHAN) ) {
                    int LA157_1 = input.LA(2);

                    if ( (LA157_1==LESSTHAN) ) {
                        int LA157_4 = input.LA(3);

                        if ( (LA157_4==BOOLEAN||LA157_4==BYTE||LA157_4==CHAR||LA157_4==DOUBLE||LA157_4==FALSE||LA157_4==FLOAT||LA157_4==INT||LA157_4==LONG||(LA157_4>=NEW && LA157_4<=NULL)||LA157_4==SHORT||LA157_4==SUPER||LA157_4==THIS||LA157_4==TRUE||LA157_4==VOID||LA157_4==LEFTPARENTHESIS||LA157_4==TILDE||(LA157_4>=PLUSPLUS && LA157_4<=PLUS)||(LA157_4>=MINUSMINUS && LA157_4<=MINUS)||LA157_4==EXCLAMATIONMARK||(LA157_4>=HexLiteral && LA157_4<=OctalLiteral)||LA157_4==FloatingPointLiteral||(LA157_4>=CharacterLiteral && LA157_4<=StringLiteral)||LA157_4==Identifier) ) {
                            alt157=1;
                        }


                    }


                }
                else if ( (LA157_0==GREATERTHAN) ) {
                    int LA157_2 = input.LA(2);

                    if ( (LA157_2==GREATERTHAN) ) {
                        int LA157_5 = input.LA(3);

                        if ( (LA157_5==GREATERTHAN) ) {
                            int LA157_7 = input.LA(4);

                            if ( (LA157_7==BOOLEAN||LA157_7==BYTE||LA157_7==CHAR||LA157_7==DOUBLE||LA157_7==FALSE||LA157_7==FLOAT||LA157_7==INT||LA157_7==LONG||(LA157_7>=NEW && LA157_7<=NULL)||LA157_7==SHORT||LA157_7==SUPER||LA157_7==THIS||LA157_7==TRUE||LA157_7==VOID||LA157_7==LEFTPARENTHESIS||LA157_7==TILDE||(LA157_7>=PLUSPLUS && LA157_7<=PLUS)||(LA157_7>=MINUSMINUS && LA157_7<=MINUS)||LA157_7==EXCLAMATIONMARK||(LA157_7>=HexLiteral && LA157_7<=OctalLiteral)||LA157_7==FloatingPointLiteral||(LA157_7>=CharacterLiteral && LA157_7<=StringLiteral)||LA157_7==Identifier) ) {
                                alt157=1;
                            }


                        }
                        else if ( (LA157_5==BOOLEAN||LA157_5==BYTE||LA157_5==CHAR||LA157_5==DOUBLE||LA157_5==FALSE||LA157_5==FLOAT||LA157_5==INT||LA157_5==LONG||(LA157_5>=NEW && LA157_5<=NULL)||LA157_5==SHORT||LA157_5==SUPER||LA157_5==THIS||LA157_5==TRUE||LA157_5==VOID||LA157_5==LEFTPARENTHESIS||LA157_5==TILDE||(LA157_5>=PLUSPLUS && LA157_5<=PLUS)||(LA157_5>=MINUSMINUS && LA157_5<=MINUS)||LA157_5==EXCLAMATIONMARK||(LA157_5>=HexLiteral && LA157_5<=OctalLiteral)||LA157_5==FloatingPointLiteral||(LA157_5>=CharacterLiteral && LA157_5<=StringLiteral)||LA157_5==Identifier) ) {
                            alt157=1;
                        }


                    }


                }


                switch (alt157) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1088:30: shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression6019);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression6021);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop157;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 128, shiftExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"

    public static class shiftOp_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "shiftOp"
    // JWIPreprocessor_Parser.g:1091:1: shiftOp : ( ( LESSTHAN LESSTHAN )=>t1= LESSTHAN t2= LESSTHAN {...}? | ( GREATERTHAN GREATERTHAN GREATERTHAN )=>t1= GREATERTHAN t2= GREATERTHAN t3= GREATERTHAN {...}? | ( GREATERTHAN GREATERTHAN )=>t1= GREATERTHAN t2= GREATERTHAN {...}?);
    public final JWIPreprocessor_Parser.shiftOp_return shiftOp() throws RecognitionException {
        JWIPreprocessor_Parser.shiftOp_return retval = new JWIPreprocessor_Parser.shiftOp_return();
        retval.start = input.LT(1);
        int shiftOp_StartIndex = input.index();
        Token t1=null;
        Token t2=null;
        Token t3=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 129) ) { return retval; }
            // JWIPreprocessor_Parser.g:1092:5: ( ( LESSTHAN LESSTHAN )=>t1= LESSTHAN t2= LESSTHAN {...}? | ( GREATERTHAN GREATERTHAN GREATERTHAN )=>t1= GREATERTHAN t2= GREATERTHAN t3= GREATERTHAN {...}? | ( GREATERTHAN GREATERTHAN )=>t1= GREATERTHAN t2= GREATERTHAN {...}?)
            int alt158=3;
            alt158 = dfa158.predict(input);
            switch (alt158) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1092:9: ( LESSTHAN LESSTHAN )=>t1= LESSTHAN t2= LESSTHAN {...}?
                    {
                    t1=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_shiftOp6052); if (state.failed) return retval;
                    t2=(Token)match(input,LESSTHAN,FOLLOW_LESSTHAN_in_shiftOp6056); if (state.failed) return retval;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1095:9: ( GREATERTHAN GREATERTHAN GREATERTHAN )=>t1= GREATERTHAN t2= GREATERTHAN t3= GREATERTHAN {...}?
                    {
                    t1=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_shiftOp6088); if (state.failed) return retval;
                    t2=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_shiftOp6092); if (state.failed) return retval;
                    t3=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_shiftOp6096); if (state.failed) return retval;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() &&
                              t2.getLine() == t3.getLine() && 
                              t2.getCharPositionInLine() + 1 == t3.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&\n          $t2.getLine() == $t3.getLine() && \n          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() ");
                    }

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:1100:9: ( GREATERTHAN GREATERTHAN )=>t1= GREATERTHAN t2= GREATERTHAN {...}?
                    {
                    t1=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_shiftOp6126); if (state.failed) return retval;
                    t2=(Token)match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_shiftOp6130); if (state.failed) return retval;
                    if ( !(( t1.getLine() == t2.getLine() && 
                              t1.getCharPositionInLine() + 1 == t2.getCharPositionInLine() )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "shiftOp", " $t1.getLine() == $t2.getLine() && \n          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() ");
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 129, shiftOp_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "shiftOp"

    public static class additiveExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "additiveExpression"
    // JWIPreprocessor_Parser.g:1106:1: additiveExpression : multiplicativeExpression ( ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final JWIPreprocessor_Parser.additiveExpression_return additiveExpression() throws RecognitionException {
        JWIPreprocessor_Parser.additiveExpression_return retval = new JWIPreprocessor_Parser.additiveExpression_return();
        retval.start = input.LT(1);
        int additiveExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 130) ) { return retval; }
            // JWIPreprocessor_Parser.g:1107:5: ( multiplicativeExpression ( ( PLUS | MINUS ) multiplicativeExpression )* )
            // JWIPreprocessor_Parser.g:1107:9: multiplicativeExpression ( ( PLUS | MINUS ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6160);
            multiplicativeExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1107:34: ( ( PLUS | MINUS ) multiplicativeExpression )*
            loop159:
            do {
                int alt159=2;
                int LA159_0 = input.LA(1);

                if ( (LA159_0==PLUS||LA159_0==MINUS) ) {
                    alt159=1;
                }


                switch (alt159) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1107:36: ( PLUS | MINUS ) multiplicativeExpression
            	    {
            	    if ( input.LA(1)==PLUS||input.LA(1)==MINUS ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression6172);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop159;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 130, additiveExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "additiveExpression"

    public static class multiplicativeExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "multiplicativeExpression"
    // JWIPreprocessor_Parser.g:1110:1: multiplicativeExpression : unaryExpression ( ( ASTERISK | SLASH | PERCENT ) unaryExpression )* ;
    public final JWIPreprocessor_Parser.multiplicativeExpression_return multiplicativeExpression() throws RecognitionException {
        JWIPreprocessor_Parser.multiplicativeExpression_return retval = new JWIPreprocessor_Parser.multiplicativeExpression_return();
        retval.start = input.LT(1);
        int multiplicativeExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 131) ) { return retval; }
            // JWIPreprocessor_Parser.g:1111:5: ( unaryExpression ( ( ASTERISK | SLASH | PERCENT ) unaryExpression )* )
            // JWIPreprocessor_Parser.g:1111:9: unaryExpression ( ( ASTERISK | SLASH | PERCENT ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6194);
            unaryExpression();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1111:25: ( ( ASTERISK | SLASH | PERCENT ) unaryExpression )*
            loop160:
            do {
                int alt160=2;
                int LA160_0 = input.LA(1);

                if ( (LA160_0==ASTERISK||LA160_0==SLASH||LA160_0==PERCENT) ) {
                    alt160=1;
                }


                switch (alt160) {
            	case 1 :
            	    // JWIPreprocessor_Parser.g:1111:27: ( ASTERISK | SLASH | PERCENT ) unaryExpression
            	    {
            	    if ( input.LA(1)==ASTERISK||input.LA(1)==SLASH||input.LA(1)==PERCENT ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression6212);
            	    unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop160;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 131, multiplicativeExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "unaryExpression"
    // JWIPreprocessor_Parser.g:1114:1: unaryExpression : ( PLUS unaryExpression | MINUS unaryExpression | PLUSPLUS unaryExpression | MINUSMINUS unaryExpression | unaryExpressionNotPlusMinus );
    public final JWIPreprocessor_Parser.unaryExpression_return unaryExpression() throws RecognitionException {
        JWIPreprocessor_Parser.unaryExpression_return retval = new JWIPreprocessor_Parser.unaryExpression_return();
        retval.start = input.LT(1);
        int unaryExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 132) ) { return retval; }
            // JWIPreprocessor_Parser.g:1115:5: ( PLUS unaryExpression | MINUS unaryExpression | PLUSPLUS unaryExpression | MINUSMINUS unaryExpression | unaryExpressionNotPlusMinus )
            int alt161=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt161=1;
                }
                break;
            case MINUS:
                {
                alt161=2;
                }
                break;
            case PLUSPLUS:
                {
                alt161=3;
                }
                break;
            case MINUSMINUS:
                {
                alt161=4;
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FALSE:
            case FLOAT:
            case INT:
            case LONG:
            case NEW:
            case NULL:
            case SHORT:
            case SUPER:
            case THIS:
            case TRUE:
            case VOID:
            case LEFTPARENTHESIS:
            case TILDE:
            case EXCLAMATIONMARK:
            case HexLiteral:
            case DecimalLiteral:
            case OctalLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
            case Identifier:
                {
                alt161=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 161, 0, input);

                throw nvae;
            }

            switch (alt161) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1115:9: PLUS unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression6238); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6240);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1116:9: MINUS unaryExpression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unaryExpression6250); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6252);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:1117:9: PLUSPLUS unaryExpression
                    {
                    match(input,PLUSPLUS,FOLLOW_PLUSPLUS_in_unaryExpression6262); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6264);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:1118:9: MINUSMINUS unaryExpression
                    {
                    match(input,MINUSMINUS,FOLLOW_MINUSMINUS_in_unaryExpression6274); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression6276);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:1119:9: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6286);
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 132, unaryExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "unaryExpressionNotPlusMinus"
    // JWIPreprocessor_Parser.g:1122:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | EXCLAMATIONMARK unaryExpression | castExpression | primary ( selector )* ( PLUSPLUS | MINUSMINUS )? );
    public final JWIPreprocessor_Parser.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        JWIPreprocessor_Parser.unaryExpressionNotPlusMinus_return retval = new JWIPreprocessor_Parser.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);
        int unaryExpressionNotPlusMinus_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 133) ) { return retval; }
            // JWIPreprocessor_Parser.g:1123:5: ( TILDE unaryExpression | EXCLAMATIONMARK unaryExpression | castExpression | primary ( selector )* ( PLUSPLUS | MINUSMINUS )? )
            int alt164=4;
            alt164 = dfa164.predict(input);
            switch (alt164) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1123:9: TILDE unaryExpression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6305); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6307);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1124:9: EXCLAMATIONMARK unaryExpression
                    {
                    match(input,EXCLAMATIONMARK,FOLLOW_EXCLAMATIONMARK_in_unaryExpressionNotPlusMinus6317); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6319);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:1125:9: castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6329);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:1126:9: primary ( selector )* ( PLUSPLUS | MINUSMINUS )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus6339);
                    primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1126:17: ( selector )*
                    loop162:
                    do {
                        int alt162=2;
                        int LA162_0 = input.LA(1);

                        if ( (LA162_0==LEFTSQUAREBRACKET||LA162_0==DOT) ) {
                            alt162=1;
                        }


                        switch (alt162) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:0:0: selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus6341);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop162;
                        }
                    } while (true);

                    // JWIPreprocessor_Parser.g:1126:27: ( PLUSPLUS | MINUSMINUS )?
                    int alt163=2;
                    int LA163_0 = input.LA(1);

                    if ( (LA163_0==PLUSPLUS||LA163_0==MINUSMINUS) ) {
                        alt163=1;
                    }
                    switch (alt163) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:
                            {
                            if ( input.LA(1)==PLUSPLUS||input.LA(1)==MINUSMINUS ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 133, unaryExpressionNotPlusMinus_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"

    public static class castExpression_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "castExpression"
    // JWIPreprocessor_Parser.g:1129:1: castExpression : ( LEFTPARENTHESIS primitiveType RIGHTPARENTHESIS unaryExpression | LEFTPARENTHESIS ( type | expression ) RIGHTPARENTHESIS unaryExpressionNotPlusMinus );
    public final JWIPreprocessor_Parser.castExpression_return castExpression() throws RecognitionException {
        JWIPreprocessor_Parser.castExpression_return retval = new JWIPreprocessor_Parser.castExpression_return();
        retval.start = input.LT(1);
        int castExpression_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 134) ) { return retval; }
            // JWIPreprocessor_Parser.g:1130:5: ( LEFTPARENTHESIS primitiveType RIGHTPARENTHESIS unaryExpression | LEFTPARENTHESIS ( type | expression ) RIGHTPARENTHESIS unaryExpressionNotPlusMinus )
            int alt166=2;
            int LA166_0 = input.LA(1);

            if ( (LA166_0==LEFTPARENTHESIS) ) {
                int LA166_1 = input.LA(2);

                if ( (synpred259_JWIPreprocessor_Parser()) ) {
                    alt166=1;
                }
                else if ( (true) ) {
                    alt166=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 166, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 166, 0, input);

                throw nvae;
            }
            switch (alt166) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1130:8: LEFTPARENTHESIS primitiveType RIGHTPARENTHESIS unaryExpression
                    {
                    match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_castExpression6367); if (state.failed) return retval;
                    pushFollow(FOLLOW_primitiveType_in_castExpression6369);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_castExpression6371); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression6373);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1131:8: LEFTPARENTHESIS ( type | expression ) RIGHTPARENTHESIS unaryExpressionNotPlusMinus
                    {
                    match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_castExpression6382); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1131:24: ( type | expression )
                    int alt165=2;
                    alt165 = dfa165.predict(input);
                    switch (alt165) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:1131:25: type
                            {
                            pushFollow(FOLLOW_type_in_castExpression6385);
                            type();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;
                        case 2 :
                            // JWIPreprocessor_Parser.g:1131:32: expression
                            {
                            pushFollow(FOLLOW_expression_in_castExpression6389);
                            expression();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }

                    match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_castExpression6392); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6394);
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 134, castExpression_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "castExpression"

    public static class primary_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "primary"
    // JWIPreprocessor_Parser.g:1134:1: primary : ( parExpression | THIS ( DOT Identifier )* ( identifierSuffix )? | SUPER superSuffix | literal | NEW creator | Identifier ( DOT Identifier )* ( identifierSuffix )? | primitiveType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* DOT CLASS | VOID DOT CLASS );
    public final JWIPreprocessor_Parser.primary_return primary() throws RecognitionException {
        JWIPreprocessor_Parser.primary_return retval = new JWIPreprocessor_Parser.primary_return();
        retval.start = input.LT(1);
        int primary_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 135) ) { return retval; }
            // JWIPreprocessor_Parser.g:1135:5: ( parExpression | THIS ( DOT Identifier )* ( identifierSuffix )? | SUPER superSuffix | literal | NEW creator | Identifier ( DOT Identifier )* ( identifierSuffix )? | primitiveType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* DOT CLASS | VOID DOT CLASS )
            int alt172=8;
            switch ( input.LA(1) ) {
            case LEFTPARENTHESIS:
                {
                alt172=1;
                }
                break;
            case THIS:
                {
                alt172=2;
                }
                break;
            case SUPER:
                {
                alt172=3;
                }
                break;
            case FALSE:
            case NULL:
            case TRUE:
            case HexLiteral:
            case DecimalLiteral:
            case OctalLiteral:
            case FloatingPointLiteral:
            case CharacterLiteral:
            case StringLiteral:
                {
                alt172=4;
                }
                break;
            case NEW:
                {
                alt172=5;
                }
                break;
            case Identifier:
                {
                alt172=6;
                }
                break;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                {
                alt172=7;
                }
                break;
            case VOID:
                {
                alt172=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 172, 0, input);

                throw nvae;
            }

            switch (alt172) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1135:9: parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary6413);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1136:9: THIS ( DOT Identifier )* ( identifierSuffix )?
                    {
                    match(input,THIS,FOLLOW_THIS_in_primary6423); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1136:14: ( DOT Identifier )*
                    loop167:
                    do {
                        int alt167=2;
                        int LA167_0 = input.LA(1);

                        if ( (LA167_0==DOT) ) {
                            int LA167_2 = input.LA(2);

                            if ( (LA167_2==Identifier) ) {
                                int LA167_3 = input.LA(3);

                                if ( (synpred262_JWIPreprocessor_Parser()) ) {
                                    alt167=1;
                                }


                            }


                        }


                        switch (alt167) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:1136:15: DOT Identifier
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary6426); if (state.failed) return retval;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary6428); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop167;
                        }
                    } while (true);

                    // JWIPreprocessor_Parser.g:1136:32: ( identifierSuffix )?
                    int alt168=2;
                    alt168 = dfa168.predict(input);
                    switch (alt168) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6432);
                            identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:1137:9: SUPER superSuffix
                    {
                    match(input,SUPER,FOLLOW_SUPER_in_primary6443); if (state.failed) return retval;
                    pushFollow(FOLLOW_superSuffix_in_primary6445);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:1138:9: literal
                    {
                    pushFollow(FOLLOW_literal_in_primary6455);
                    literal();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:1139:9: NEW creator
                    {
                    match(input,NEW,FOLLOW_NEW_in_primary6465); if (state.failed) return retval;
                    pushFollow(FOLLOW_creator_in_primary6467);
                    creator();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // JWIPreprocessor_Parser.g:1140:9: Identifier ( DOT Identifier )* ( identifierSuffix )?
                    {
                    match(input,Identifier,FOLLOW_Identifier_in_primary6477); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1140:20: ( DOT Identifier )*
                    loop169:
                    do {
                        int alt169=2;
                        int LA169_0 = input.LA(1);

                        if ( (LA169_0==DOT) ) {
                            int LA169_2 = input.LA(2);

                            if ( (LA169_2==Identifier) ) {
                                int LA169_3 = input.LA(3);

                                if ( (synpred268_JWIPreprocessor_Parser()) ) {
                                    alt169=1;
                                }


                            }


                        }


                        switch (alt169) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:1140:21: DOT Identifier
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary6480); if (state.failed) return retval;
                    	    match(input,Identifier,FOLLOW_Identifier_in_primary6482); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop169;
                        }
                    } while (true);

                    // JWIPreprocessor_Parser.g:1140:38: ( identifierSuffix )?
                    int alt170=2;
                    alt170 = dfa170.predict(input);
                    switch (alt170) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary6486);
                            identifierSuffix();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // JWIPreprocessor_Parser.g:1141:9: primitiveType ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* DOT CLASS
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary6497);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1141:23: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
                    loop171:
                    do {
                        int alt171=2;
                        int LA171_0 = input.LA(1);

                        if ( (LA171_0==LEFTSQUAREBRACKET) ) {
                            alt171=1;
                        }


                        switch (alt171) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:1141:24: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
                    	    {
                    	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_primary6500); if (state.failed) return retval;
                    	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_primary6502); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop171;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary6506); if (state.failed) return retval;
                    match(input,CLASS,FOLLOW_CLASS_in_primary6508); if (state.failed) return retval;

                    }
                    break;
                case 8 :
                    // JWIPreprocessor_Parser.g:1142:9: VOID DOT CLASS
                    {
                    match(input,VOID,FOLLOW_VOID_in_primary6518); if (state.failed) return retval;
                    match(input,DOT,FOLLOW_DOT_in_primary6520); if (state.failed) return retval;
                    match(input,CLASS,FOLLOW_CLASS_in_primary6522); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 135, primary_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "primary"

    public static class identifierSuffix_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "identifierSuffix"
    // JWIPreprocessor_Parser.g:1145:1: identifierSuffix : ( ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )+ DOT CLASS | ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )+ | arguments | DOT CLASS | DOT explicitGenericInvocation | DOT THIS | DOT SUPER arguments | DOT NEW innerCreator );
    public final JWIPreprocessor_Parser.identifierSuffix_return identifierSuffix() throws RecognitionException {
        JWIPreprocessor_Parser.identifierSuffix_return retval = new JWIPreprocessor_Parser.identifierSuffix_return();
        retval.start = input.LT(1);
        int identifierSuffix_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 136) ) { return retval; }
            // JWIPreprocessor_Parser.g:1146:5: ( ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )+ DOT CLASS | ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )+ | arguments | DOT CLASS | DOT explicitGenericInvocation | DOT THIS | DOT SUPER arguments | DOT NEW innerCreator )
            int alt175=8;
            alt175 = dfa175.predict(input);
            switch (alt175) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1146:9: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )+ DOT CLASS
                    {
                    // JWIPreprocessor_Parser.g:1146:9: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )+
                    int cnt173=0;
                    loop173:
                    do {
                        int alt173=2;
                        int LA173_0 = input.LA(1);

                        if ( (LA173_0==LEFTSQUAREBRACKET) ) {
                            alt173=1;
                        }


                        switch (alt173) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:1146:10: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
                    	    {
                    	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_identifierSuffix6542); if (state.failed) return retval;
                    	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_identifierSuffix6544); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt173 >= 1 ) break loop173;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(173, input);
                                throw eee;
                        }
                        cnt173++;
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix6548); if (state.failed) return retval;
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix6550); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1147:9: ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )+
                    {
                    // JWIPreprocessor_Parser.g:1147:9: ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )+
                    int cnt174=0;
                    loop174:
                    do {
                        int alt174=2;
                        alt174 = dfa174.predict(input);
                        switch (alt174) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:1147:10: LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET
                    	    {
                    	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_identifierSuffix6561); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix6563);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_identifierSuffix6565); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt174 >= 1 ) break loop174;
                    	    if (state.backtracking>0) {state.failed=true; return retval;}
                                EarlyExitException eee =
                                    new EarlyExitException(174, input);
                                throw eee;
                        }
                        cnt174++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:1148:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix6578);
                    arguments();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:1149:9: DOT CLASS
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix6588); if (state.failed) return retval;
                    match(input,CLASS,FOLLOW_CLASS_in_identifierSuffix6590); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:1150:9: DOT explicitGenericInvocation
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix6600); if (state.failed) return retval;
                    pushFollow(FOLLOW_explicitGenericInvocation_in_identifierSuffix6602);
                    explicitGenericInvocation();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 6 :
                    // JWIPreprocessor_Parser.g:1151:9: DOT THIS
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix6612); if (state.failed) return retval;
                    match(input,THIS,FOLLOW_THIS_in_identifierSuffix6614); if (state.failed) return retval;

                    }
                    break;
                case 7 :
                    // JWIPreprocessor_Parser.g:1152:9: DOT SUPER arguments
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix6624); if (state.failed) return retval;
                    match(input,SUPER,FOLLOW_SUPER_in_identifierSuffix6626); if (state.failed) return retval;
                    pushFollow(FOLLOW_arguments_in_identifierSuffix6628);
                    arguments();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 8 :
                    // JWIPreprocessor_Parser.g:1153:9: DOT NEW innerCreator
                    {
                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix6638); if (state.failed) return retval;
                    match(input,NEW,FOLLOW_NEW_in_identifierSuffix6640); if (state.failed) return retval;
                    pushFollow(FOLLOW_innerCreator_in_identifierSuffix6642);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 136, identifierSuffix_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "identifierSuffix"

    public static class creator_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "creator"
    // JWIPreprocessor_Parser.g:1156:1: creator : ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) );
    public final JWIPreprocessor_Parser.creator_return creator() throws RecognitionException {
        JWIPreprocessor_Parser.creator_return retval = new JWIPreprocessor_Parser.creator_return();
        retval.start = input.LT(1);
        int creator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 137) ) { return retval; }
            // JWIPreprocessor_Parser.g:1157:5: ( nonWildcardTypeArguments createdName classCreatorRest | createdName ( arrayCreatorRest | classCreatorRest ) )
            int alt177=2;
            int LA177_0 = input.LA(1);

            if ( (LA177_0==LESSTHAN) ) {
                alt177=1;
            }
            else if ( (LA177_0==BOOLEAN||LA177_0==BYTE||LA177_0==CHAR||LA177_0==DOUBLE||LA177_0==FLOAT||LA177_0==INT||LA177_0==LONG||LA177_0==SHORT||LA177_0==Identifier) ) {
                alt177=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 177, 0, input);

                throw nvae;
            }
            switch (alt177) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1157:9: nonWildcardTypeArguments createdName classCreatorRest
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator6661);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_createdName_in_creator6663);
                    createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_classCreatorRest_in_creator6665);
                    classCreatorRest();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1158:9: createdName ( arrayCreatorRest | classCreatorRest )
                    {
                    pushFollow(FOLLOW_createdName_in_creator6675);
                    createdName();

                    state._fsp--;
                    if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1158:21: ( arrayCreatorRest | classCreatorRest )
                    int alt176=2;
                    int LA176_0 = input.LA(1);

                    if ( (LA176_0==LEFTSQUAREBRACKET) ) {
                        alt176=1;
                    }
                    else if ( (LA176_0==LEFTPARENTHESIS) ) {
                        alt176=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 176, 0, input);

                        throw nvae;
                    }
                    switch (alt176) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:1158:22: arrayCreatorRest
                            {
                            pushFollow(FOLLOW_arrayCreatorRest_in_creator6678);
                            arrayCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;
                        case 2 :
                            // JWIPreprocessor_Parser.g:1158:41: classCreatorRest
                            {
                            pushFollow(FOLLOW_classCreatorRest_in_creator6682);
                            classCreatorRest();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 137, creator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "creator"

    public static class createdName_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "createdName"
    // JWIPreprocessor_Parser.g:1161:1: createdName : ( classOrInterfaceType | primitiveType );
    public final JWIPreprocessor_Parser.createdName_return createdName() throws RecognitionException {
        JWIPreprocessor_Parser.createdName_return retval = new JWIPreprocessor_Parser.createdName_return();
        retval.start = input.LT(1);
        int createdName_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 138) ) { return retval; }
            // JWIPreprocessor_Parser.g:1162:5: ( classOrInterfaceType | primitiveType )
            int alt178=2;
            int LA178_0 = input.LA(1);

            if ( (LA178_0==Identifier) ) {
                alt178=1;
            }
            else if ( (LA178_0==BOOLEAN||LA178_0==BYTE||LA178_0==CHAR||LA178_0==DOUBLE||LA178_0==FLOAT||LA178_0==INT||LA178_0==LONG||LA178_0==SHORT) ) {
                alt178=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 178, 0, input);

                throw nvae;
            }
            switch (alt178) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1162:9: classOrInterfaceType
                    {
                    pushFollow(FOLLOW_classOrInterfaceType_in_createdName6702);
                    classOrInterfaceType();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1163:9: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName6712);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 138, createdName_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "createdName"

    public static class innerCreator_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "innerCreator"
    // JWIPreprocessor_Parser.g:1166:1: innerCreator : ( nonWildcardTypeArguments )? Identifier classCreatorRest ;
    public final JWIPreprocessor_Parser.innerCreator_return innerCreator() throws RecognitionException {
        JWIPreprocessor_Parser.innerCreator_return retval = new JWIPreprocessor_Parser.innerCreator_return();
        retval.start = input.LT(1);
        int innerCreator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 139) ) { return retval; }
            // JWIPreprocessor_Parser.g:1167:5: ( ( nonWildcardTypeArguments )? Identifier classCreatorRest )
            // JWIPreprocessor_Parser.g:1167:9: ( nonWildcardTypeArguments )? Identifier classCreatorRest
            {
            // JWIPreprocessor_Parser.g:1167:9: ( nonWildcardTypeArguments )?
            int alt179=2;
            int LA179_0 = input.LA(1);

            if ( (LA179_0==LESSTHAN) ) {
                alt179=1;
            }
            switch (alt179) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_innerCreator6735);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            match(input,Identifier,FOLLOW_Identifier_in_innerCreator6738); if (state.failed) return retval;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator6740);
            classCreatorRest();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 139, innerCreator_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "innerCreator"

    public static class arrayCreatorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "arrayCreatorRest"
    // JWIPreprocessor_Parser.g:1170:1: arrayCreatorRest : LEFTSQUAREBRACKET ( RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* arrayInitializer | expression RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )* ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ) ;
    public final JWIPreprocessor_Parser.arrayCreatorRest_return arrayCreatorRest() throws RecognitionException {
        JWIPreprocessor_Parser.arrayCreatorRest_return retval = new JWIPreprocessor_Parser.arrayCreatorRest_return();
        retval.start = input.LT(1);
        int arrayCreatorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 140) ) { return retval; }
            // JWIPreprocessor_Parser.g:1171:5: ( LEFTSQUAREBRACKET ( RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* arrayInitializer | expression RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )* ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* ) )
            // JWIPreprocessor_Parser.g:1171:9: LEFTSQUAREBRACKET ( RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* arrayInitializer | expression RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )* ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* )
            {
            match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_arrayCreatorRest6759); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1172:9: ( RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* arrayInitializer | expression RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )* ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* )
            int alt183=2;
            int LA183_0 = input.LA(1);

            if ( (LA183_0==RIGHTSQUAREBRACKET) ) {
                alt183=1;
            }
            else if ( (LA183_0==BOOLEAN||LA183_0==BYTE||LA183_0==CHAR||LA183_0==DOUBLE||LA183_0==FALSE||LA183_0==FLOAT||LA183_0==INT||LA183_0==LONG||(LA183_0>=NEW && LA183_0<=NULL)||LA183_0==SHORT||LA183_0==SUPER||LA183_0==THIS||LA183_0==TRUE||LA183_0==VOID||LA183_0==LEFTPARENTHESIS||LA183_0==TILDE||(LA183_0>=PLUSPLUS && LA183_0<=PLUS)||(LA183_0>=MINUSMINUS && LA183_0<=MINUS)||LA183_0==EXCLAMATIONMARK||(LA183_0>=HexLiteral && LA183_0<=OctalLiteral)||LA183_0==FloatingPointLiteral||(LA183_0>=CharacterLiteral && LA183_0<=StringLiteral)||LA183_0==Identifier) ) {
                alt183=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 183, 0, input);

                throw nvae;
            }
            switch (alt183) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1172:13: RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )* arrayInitializer
                    {
                    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6773); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1172:32: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
                    loop180:
                    do {
                        int alt180=2;
                        int LA180_0 = input.LA(1);

                        if ( (LA180_0==LEFTSQUAREBRACKET) ) {
                            alt180=1;
                        }


                        switch (alt180) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:1172:33: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
                    	    {
                    	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_arrayCreatorRest6776); if (state.failed) return retval;
                    	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6778); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop180;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest6782);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1173:13: expression RIGHTSQUAREBRACKET ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )* ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest6796);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6798); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1173:43: ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )*
                    loop181:
                    do {
                        int alt181=2;
                        alt181 = dfa181.predict(input);
                        switch (alt181) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:1173:44: LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET
                    	    {
                    	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_arrayCreatorRest6801); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest6803);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6805); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop181;
                        }
                    } while (true);

                    // JWIPreprocessor_Parser.g:1173:94: ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )*
                    loop182:
                    do {
                        int alt182=2;
                        int LA182_0 = input.LA(1);

                        if ( (LA182_0==LEFTSQUAREBRACKET) ) {
                            int LA182_2 = input.LA(2);

                            if ( (LA182_2==RIGHTSQUAREBRACKET) ) {
                                alt182=1;
                            }


                        }


                        switch (alt182) {
                    	case 1 :
                    	    // JWIPreprocessor_Parser.g:1173:95: LEFTSQUAREBRACKET RIGHTSQUAREBRACKET
                    	    {
                    	    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_arrayCreatorRest6810); if (state.failed) return retval;
                    	    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6812); if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop182;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 140, arrayCreatorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arrayCreatorRest"

    public static class classCreatorRest_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "classCreatorRest"
    // JWIPreprocessor_Parser.g:1177:1: classCreatorRest : arguments ( classBody )? ;
    public final JWIPreprocessor_Parser.classCreatorRest_return classCreatorRest() throws RecognitionException {
        JWIPreprocessor_Parser.classCreatorRest_return retval = new JWIPreprocessor_Parser.classCreatorRest_return();
        retval.start = input.LT(1);
        int classCreatorRest_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 141) ) { return retval; }
            // JWIPreprocessor_Parser.g:1178:5: ( arguments ( classBody )? )
            // JWIPreprocessor_Parser.g:1178:9: arguments ( classBody )?
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest6843);
            arguments();

            state._fsp--;
            if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1178:19: ( classBody )?
            int alt184=2;
            int LA184_0 = input.LA(1);

            if ( (LA184_0==LEFTBRACE) ) {
                alt184=1;
            }
            switch (alt184) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: classBody
                    {
                    pushFollow(FOLLOW_classBody_in_classCreatorRest6845);
                    classBody();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 141, classCreatorRest_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "classCreatorRest"

    public static class explicitGenericInvocation_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "explicitGenericInvocation"
    // JWIPreprocessor_Parser.g:1181:1: explicitGenericInvocation : nonWildcardTypeArguments Identifier arguments ;
    public final JWIPreprocessor_Parser.explicitGenericInvocation_return explicitGenericInvocation() throws RecognitionException {
        JWIPreprocessor_Parser.explicitGenericInvocation_return retval = new JWIPreprocessor_Parser.explicitGenericInvocation_return();
        retval.start = input.LT(1);
        int explicitGenericInvocation_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 142) ) { return retval; }
            // JWIPreprocessor_Parser.g:1182:5: ( nonWildcardTypeArguments Identifier arguments )
            // JWIPreprocessor_Parser.g:1182:9: nonWildcardTypeArguments Identifier arguments
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6869);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return retval;
            match(input,Identifier,FOLLOW_Identifier_in_explicitGenericInvocation6871); if (state.failed) return retval;
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation6873);
            arguments();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 142, explicitGenericInvocation_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "explicitGenericInvocation"

    public static class nonWildcardTypeArguments_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "nonWildcardTypeArguments"
    // JWIPreprocessor_Parser.g:1185:1: nonWildcardTypeArguments : LESSTHAN typeList GREATERTHAN ;
    public final JWIPreprocessor_Parser.nonWildcardTypeArguments_return nonWildcardTypeArguments() throws RecognitionException {
        JWIPreprocessor_Parser.nonWildcardTypeArguments_return retval = new JWIPreprocessor_Parser.nonWildcardTypeArguments_return();
        retval.start = input.LT(1);
        int nonWildcardTypeArguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 143) ) { return retval; }
            // JWIPreprocessor_Parser.g:1186:5: ( LESSTHAN typeList GREATERTHAN )
            // JWIPreprocessor_Parser.g:1186:9: LESSTHAN typeList GREATERTHAN
            {
            match(input,LESSTHAN,FOLLOW_LESSTHAN_in_nonWildcardTypeArguments6896); if (state.failed) return retval;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments6898);
            typeList();

            state._fsp--;
            if (state.failed) return retval;
            match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_nonWildcardTypeArguments6900); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 143, nonWildcardTypeArguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "nonWildcardTypeArguments"

    public static class selector_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "selector"
    // JWIPreprocessor_Parser.g:1189:1: selector : ( DOT Identifier ( arguments )? | DOT THIS | DOT SUPER superSuffix | DOT NEW innerCreator | LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET );
    public final JWIPreprocessor_Parser.selector_return selector() throws RecognitionException {
        JWIPreprocessor_Parser.selector_return retval = new JWIPreprocessor_Parser.selector_return();
        retval.start = input.LT(1);
        int selector_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 144) ) { return retval; }
            // JWIPreprocessor_Parser.g:1190:5: ( DOT Identifier ( arguments )? | DOT THIS | DOT SUPER superSuffix | DOT NEW innerCreator | LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )
            int alt186=5;
            int LA186_0 = input.LA(1);

            if ( (LA186_0==DOT) ) {
                switch ( input.LA(2) ) {
                case Identifier:
                    {
                    alt186=1;
                    }
                    break;
                case THIS:
                    {
                    alt186=2;
                    }
                    break;
                case SUPER:
                    {
                    alt186=3;
                    }
                    break;
                case NEW:
                    {
                    alt186=4;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 186, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA186_0==LEFTSQUAREBRACKET) ) {
                alt186=5;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 186, 0, input);

                throw nvae;
            }
            switch (alt186) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1190:9: DOT Identifier ( arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector6923); if (state.failed) return retval;
                    match(input,Identifier,FOLLOW_Identifier_in_selector6925); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1190:24: ( arguments )?
                    int alt185=2;
                    int LA185_0 = input.LA(1);

                    if ( (LA185_0==LEFTPARENTHESIS) ) {
                        alt185=1;
                    }
                    switch (alt185) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector6927);
                            arguments();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1191:9: DOT THIS
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector6938); if (state.failed) return retval;
                    match(input,THIS,FOLLOW_THIS_in_selector6940); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // JWIPreprocessor_Parser.g:1192:9: DOT SUPER superSuffix
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector6950); if (state.failed) return retval;
                    match(input,SUPER,FOLLOW_SUPER_in_selector6952); if (state.failed) return retval;
                    pushFollow(FOLLOW_superSuffix_in_selector6954);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // JWIPreprocessor_Parser.g:1193:9: DOT NEW innerCreator
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector6964); if (state.failed) return retval;
                    match(input,NEW,FOLLOW_NEW_in_selector6966); if (state.failed) return retval;
                    pushFollow(FOLLOW_innerCreator_in_selector6968);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // JWIPreprocessor_Parser.g:1194:9: LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET
                    {
                    match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_selector6978); if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_selector6980);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_selector6982); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 144, selector_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "selector"

    public static class superSuffix_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "superSuffix"
    // JWIPreprocessor_Parser.g:1197:1: superSuffix : ( arguments | DOT Identifier ( arguments )? );
    public final JWIPreprocessor_Parser.superSuffix_return superSuffix() throws RecognitionException {
        JWIPreprocessor_Parser.superSuffix_return retval = new JWIPreprocessor_Parser.superSuffix_return();
        retval.start = input.LT(1);
        int superSuffix_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 145) ) { return retval; }
            // JWIPreprocessor_Parser.g:1198:5: ( arguments | DOT Identifier ( arguments )? )
            int alt188=2;
            int LA188_0 = input.LA(1);

            if ( (LA188_0==LEFTPARENTHESIS) ) {
                alt188=1;
            }
            else if ( (LA188_0==DOT) ) {
                alt188=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 188, 0, input);

                throw nvae;
            }
            switch (alt188) {
                case 1 :
                    // JWIPreprocessor_Parser.g:1198:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix7005);
                    arguments();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // JWIPreprocessor_Parser.g:1199:9: DOT Identifier ( arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix7015); if (state.failed) return retval;
                    match(input,Identifier,FOLLOW_Identifier_in_superSuffix7017); if (state.failed) return retval;
                    // JWIPreprocessor_Parser.g:1199:24: ( arguments )?
                    int alt187=2;
                    int LA187_0 = input.LA(1);

                    if ( (LA187_0==LEFTPARENTHESIS) ) {
                        alt187=1;
                    }
                    switch (alt187) {
                        case 1 :
                            // JWIPreprocessor_Parser.g:0:0: arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix7019);
                            arguments();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 145, superSuffix_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "superSuffix"

    public static class arguments_return extends ParserRuleReturnScope {
        public StringTemplate st;
        public Object getTemplate() { return st; }
        public String toString() { return st==null?null:st.toString(); }
    };

    // $ANTLR start "arguments"
    // JWIPreprocessor_Parser.g:1202:1: arguments : LEFTPARENTHESIS ( expressionList )? RIGHTPARENTHESIS ;
    public final JWIPreprocessor_Parser.arguments_return arguments() throws RecognitionException {
        JWIPreprocessor_Parser.arguments_return retval = new JWIPreprocessor_Parser.arguments_return();
        retval.start = input.LT(1);
        int arguments_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 146) ) { return retval; }
            // JWIPreprocessor_Parser.g:1203:5: ( LEFTPARENTHESIS ( expressionList )? RIGHTPARENTHESIS )
            // JWIPreprocessor_Parser.g:1203:9: LEFTPARENTHESIS ( expressionList )? RIGHTPARENTHESIS
            {
            match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_arguments7039); if (state.failed) return retval;
            // JWIPreprocessor_Parser.g:1203:25: ( expressionList )?
            int alt189=2;
            int LA189_0 = input.LA(1);

            if ( (LA189_0==BOOLEAN||LA189_0==BYTE||LA189_0==CHAR||LA189_0==DOUBLE||LA189_0==FALSE||LA189_0==FLOAT||LA189_0==INT||LA189_0==LONG||(LA189_0>=NEW && LA189_0<=NULL)||LA189_0==SHORT||LA189_0==SUPER||LA189_0==THIS||LA189_0==TRUE||LA189_0==VOID||LA189_0==LEFTPARENTHESIS||LA189_0==TILDE||(LA189_0>=PLUSPLUS && LA189_0<=PLUS)||(LA189_0>=MINUSMINUS && LA189_0<=MINUS)||LA189_0==EXCLAMATIONMARK||(LA189_0>=HexLiteral && LA189_0<=OctalLiteral)||LA189_0==FloatingPointLiteral||(LA189_0>=CharacterLiteral && LA189_0<=StringLiteral)||LA189_0==Identifier) ) {
                alt189=1;
            }
            switch (alt189) {
                case 1 :
                    // JWIPreprocessor_Parser.g:0:0: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments7041);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }

            match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_arguments7044); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 146, arguments_StartIndex); }
        }
        return retval;
    }
    // $ANTLR end "arguments"

    // $ANTLR start synpred5_JWIPreprocessor_Parser
    public final void synpred5_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        JWIPreprocessor_Parser.packageDeclaration_return packageName1 = null;


        // JWIPreprocessor_Parser.g:244:9: ( annotations (packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* ) )
        // JWIPreprocessor_Parser.g:244:9: annotations (packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* )
        {
        pushFollow(FOLLOW_annotations_in_synpred5_JWIPreprocessor_Parser190);
        annotations();

        state._fsp--;
        if (state.failed) return ;
        // JWIPreprocessor_Parser.g:245:9: (packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* )
        int alt195=2;
        int LA195_0 = input.LA(1);

        if ( (LA195_0==PACKAGE) ) {
            alt195=1;
        }
        else if ( (LA195_0==ABSTRACT||LA195_0==CLASS||LA195_0==FINAL||LA195_0==INTERFACE||(LA195_0>=PRIVATE && LA195_0<=PUBLIC)||(LA195_0>=STATIC && LA195_0<=STRICTFP)||LA195_0==ATSIGN||LA195_0==ENUM) ) {
            alt195=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 195, 0, input);

            throw nvae;
        }
        switch (alt195) {
            case 1 :
                // JWIPreprocessor_Parser.g:245:13: packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )*
                {
                pushFollow(FOLLOW_packageDeclaration_in_synpred5_JWIPreprocessor_Parser206);
                packageName1=packageDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // JWIPreprocessor_Parser.g:245:91: ( importDeclaration )*
                loop192:
                do {
                    int alt192=2;
                    int LA192_0 = input.LA(1);

                    if ( (LA192_0==IMPORT) ) {
                        alt192=1;
                    }


                    switch (alt192) {
                	case 1 :
                	    // JWIPreprocessor_Parser.g:0:0: importDeclaration
                	    {
                	    pushFollow(FOLLOW_importDeclaration_in_synpred5_JWIPreprocessor_Parser210);
                	    importDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop192;
                    }
                } while (true);

                // JWIPreprocessor_Parser.g:245:110: ( typeDeclaration )*
                loop193:
                do {
                    int alt193=2;
                    int LA193_0 = input.LA(1);

                    if ( (LA193_0==ABSTRACT||LA193_0==CLASS||LA193_0==FINAL||LA193_0==INTERFACE||(LA193_0>=PRIVATE && LA193_0<=PUBLIC)||(LA193_0>=STATIC && LA193_0<=STRICTFP)||LA193_0==SEMICOLON||LA193_0==ATSIGN||LA193_0==ENUM) ) {
                        alt193=1;
                    }


                    switch (alt193) {
                	case 1 :
                	    // JWIPreprocessor_Parser.g:0:0: typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_JWIPreprocessor_Parser213);
                	    typeDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop193;
                    }
                } while (true);


                }
                break;
            case 2 :
                // JWIPreprocessor_Parser.g:246:13: classOrInterfaceDeclaration ( typeDeclaration )*
                {
                pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred5_JWIPreprocessor_Parser228);
                classOrInterfaceDeclaration();

                state._fsp--;
                if (state.failed) return ;
                // JWIPreprocessor_Parser.g:246:41: ( typeDeclaration )*
                loop194:
                do {
                    int alt194=2;
                    int LA194_0 = input.LA(1);

                    if ( (LA194_0==ABSTRACT||LA194_0==CLASS||LA194_0==FINAL||LA194_0==INTERFACE||(LA194_0>=PRIVATE && LA194_0<=PUBLIC)||(LA194_0>=STATIC && LA194_0<=STRICTFP)||LA194_0==SEMICOLON||LA194_0==ATSIGN||LA194_0==ENUM) ) {
                        alt194=1;
                    }


                    switch (alt194) {
                	case 1 :
                	    // JWIPreprocessor_Parser.g:0:0: typeDeclaration
                	    {
                	    pushFollow(FOLLOW_typeDeclaration_in_synpred5_JWIPreprocessor_Parser230);
                	    typeDeclaration();

                	    state._fsp--;
                	    if (state.failed) return ;

                	    }
                	    break;

                	default :
                	    break loop194;
                    }
                } while (true);


                }
                break;

        }


        }
    }
    // $ANTLR end synpred5_JWIPreprocessor_Parser

    // $ANTLR start synpred136_JWIPreprocessor_Parser
    public final void synpred136_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:753:19: ( explicitConstructorInvocation )
        // JWIPreprocessor_Parser.g:753:19: explicitConstructorInvocation
        {
        pushFollow(FOLLOW_explicitConstructorInvocation_in_synpred136_JWIPreprocessor_Parser3596);
        explicitConstructorInvocation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred136_JWIPreprocessor_Parser

    // $ANTLR start synpred140_JWIPreprocessor_Parser
    public final void synpred140_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:757:9: ( ( nonWildcardTypeArguments )? ( THIS | SUPER ) arguments SEMICOLON )
        // JWIPreprocessor_Parser.g:757:9: ( nonWildcardTypeArguments )? ( THIS | SUPER ) arguments SEMICOLON
        {
        // JWIPreprocessor_Parser.g:757:9: ( nonWildcardTypeArguments )?
        int alt210=2;
        int LA210_0 = input.LA(1);

        if ( (LA210_0==LESSTHAN) ) {
            alt210=1;
        }
        switch (alt210) {
            case 1 :
                // JWIPreprocessor_Parser.g:0:0: nonWildcardTypeArguments
                {
                pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred140_JWIPreprocessor_Parser3621);
                nonWildcardTypeArguments();

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        if ( input.LA(1)==SUPER||input.LA(1)==THIS ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        pushFollow(FOLLOW_arguments_in_synpred140_JWIPreprocessor_Parser3632);
        arguments();

        state._fsp--;
        if (state.failed) return ;
        match(input,SEMICOLON,FOLLOW_SEMICOLON_in_synpred140_JWIPreprocessor_Parser3634); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred140_JWIPreprocessor_Parser

    // $ANTLR start synpred151_JWIPreprocessor_Parser
    public final void synpred151_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:789:9: ( annotation )
        // JWIPreprocessor_Parser.g:789:9: annotation
        {
        pushFollow(FOLLOW_annotation_in_synpred151_JWIPreprocessor_Parser3845);
        annotation();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred151_JWIPreprocessor_Parser

    // $ANTLR start synpred177_JWIPreprocessor_Parser
    public final void synpred177_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:900:9: ( localVariableDeclarationStatement )
        // JWIPreprocessor_Parser.g:900:9: localVariableDeclarationStatement
        {
        pushFollow(FOLLOW_localVariableDeclarationStatement_in_synpred177_JWIPreprocessor_Parser4547);
        localVariableDeclarationStatement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred177_JWIPreprocessor_Parser

    // $ANTLR start synpred178_JWIPreprocessor_Parser
    public final void synpred178_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:901:9: ( classOrInterfaceDeclaration )
        // JWIPreprocessor_Parser.g:901:9: classOrInterfaceDeclaration
        {
        pushFollow(FOLLOW_classOrInterfaceDeclaration_in_synpred178_JWIPreprocessor_Parser4557);
        classOrInterfaceDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred178_JWIPreprocessor_Parser

    // $ANTLR start synpred183_JWIPreprocessor_Parser
    public final void synpred183_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:920:52: ( ELSE statement )
        // JWIPreprocessor_Parser.g:920:52: ELSE statement
        {
        match(input,ELSE,FOLLOW_ELSE_in_synpred183_JWIPreprocessor_Parser4704); if (state.failed) return ;
        pushFollow(FOLLOW_statement_in_synpred183_JWIPreprocessor_Parser4706);
        statement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred183_JWIPreprocessor_Parser

    // $ANTLR start synpred188_JWIPreprocessor_Parser
    public final void synpred188_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:925:11: ( catches FINALLY block )
        // JWIPreprocessor_Parser.g:925:11: catches FINALLY block
        {
        pushFollow(FOLLOW_catches_in_synpred188_JWIPreprocessor_Parser4782);
        catches();

        state._fsp--;
        if (state.failed) return ;
        match(input,FINALLY,FOLLOW_FINALLY_in_synpred188_JWIPreprocessor_Parser4784); if (state.failed) return ;
        pushFollow(FOLLOW_block_in_synpred188_JWIPreprocessor_Parser4786);
        block();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred188_JWIPreprocessor_Parser

    // $ANTLR start synpred189_JWIPreprocessor_Parser
    public final void synpred189_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:926:11: ( catches )
        // JWIPreprocessor_Parser.g:926:11: catches
        {
        pushFollow(FOLLOW_catches_in_synpred189_JWIPreprocessor_Parser4798);
        catches();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred189_JWIPreprocessor_Parser

    // $ANTLR start synpred204_JWIPreprocessor_Parser
    public final void synpred204_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:961:9: ( switchLabel )
        // JWIPreprocessor_Parser.g:961:9: switchLabel
        {
        pushFollow(FOLLOW_switchLabel_in_synpred204_JWIPreprocessor_Parser5089);
        switchLabel();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred204_JWIPreprocessor_Parser

    // $ANTLR start synpred206_JWIPreprocessor_Parser
    public final void synpred206_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:965:9: ( CASE constantExpression COLON )
        // JWIPreprocessor_Parser.g:965:9: CASE constantExpression COLON
        {
        match(input,CASE,FOLLOW_CASE_in_synpred206_JWIPreprocessor_Parser5116); if (state.failed) return ;
        pushFollow(FOLLOW_constantExpression_in_synpred206_JWIPreprocessor_Parser5118);
        constantExpression();

        state._fsp--;
        if (state.failed) return ;
        match(input,COLON,FOLLOW_COLON_in_synpred206_JWIPreprocessor_Parser5120); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred206_JWIPreprocessor_Parser

    // $ANTLR start synpred207_JWIPreprocessor_Parser
    public final void synpred207_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:966:9: ( CASE enumConstantName COLON )
        // JWIPreprocessor_Parser.g:966:9: CASE enumConstantName COLON
        {
        match(input,CASE,FOLLOW_CASE_in_synpred207_JWIPreprocessor_Parser5130); if (state.failed) return ;
        pushFollow(FOLLOW_enumConstantName_in_synpred207_JWIPreprocessor_Parser5132);
        enumConstantName();

        state._fsp--;
        if (state.failed) return ;
        match(input,COLON,FOLLOW_COLON_in_synpred207_JWIPreprocessor_Parser5134); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred207_JWIPreprocessor_Parser

    // $ANTLR start synpred208_JWIPreprocessor_Parser
    public final void synpred208_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:972:9: ( enhancedForControl )
        // JWIPreprocessor_Parser.g:972:9: enhancedForControl
        {
        pushFollow(FOLLOW_enhancedForControl_in_synpred208_JWIPreprocessor_Parser5177);
        enhancedForControl();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred208_JWIPreprocessor_Parser

    // $ANTLR start synpred212_JWIPreprocessor_Parser
    public final void synpred212_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:977:9: ( localVariableDeclaration )
        // JWIPreprocessor_Parser.g:977:9: localVariableDeclaration
        {
        pushFollow(FOLLOW_localVariableDeclaration_in_synpred212_JWIPreprocessor_Parser5217);
        localVariableDeclaration();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred212_JWIPreprocessor_Parser

    // $ANTLR start synpred214_JWIPreprocessor_Parser
    public final void synpred214_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1008:32: ( assignmentOperator expression )
        // JWIPreprocessor_Parser.g:1008:32: assignmentOperator expression
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred214_JWIPreprocessor_Parser5400);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred214_JWIPreprocessor_Parser5402);
        expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred214_JWIPreprocessor_Parser

    // $ANTLR start synpred224_JWIPreprocessor_Parser
    public final void synpred224_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1021:9: ( LESSTHAN LESSTHAN ASSIGNMENT_EQUALS )
        // JWIPreprocessor_Parser.g:1021:10: LESSTHAN LESSTHAN ASSIGNMENT_EQUALS
        {
        match(input,LESSTHAN,FOLLOW_LESSTHAN_in_synpred224_JWIPreprocessor_Parser5518); if (state.failed) return ;
        match(input,LESSTHAN,FOLLOW_LESSTHAN_in_synpred224_JWIPreprocessor_Parser5520); if (state.failed) return ;
        match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_synpred224_JWIPreprocessor_Parser5522); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred224_JWIPreprocessor_Parser

    // $ANTLR start synpred225_JWIPreprocessor_Parser
    public final void synpred225_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1026:9: ( GREATERTHAN GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )
        // JWIPreprocessor_Parser.g:1026:10: GREATERTHAN GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS
        {
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred225_JWIPreprocessor_Parser5558); if (state.failed) return ;
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred225_JWIPreprocessor_Parser5560); if (state.failed) return ;
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred225_JWIPreprocessor_Parser5562); if (state.failed) return ;
        match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_synpred225_JWIPreprocessor_Parser5564); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred225_JWIPreprocessor_Parser

    // $ANTLR start synpred226_JWIPreprocessor_Parser
    public final void synpred226_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1033:9: ( GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )
        // JWIPreprocessor_Parser.g:1033:10: GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS
        {
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred226_JWIPreprocessor_Parser5603); if (state.failed) return ;
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred226_JWIPreprocessor_Parser5605); if (state.failed) return ;
        match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_synpred226_JWIPreprocessor_Parser5607); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred226_JWIPreprocessor_Parser

    // $ANTLR start synpred237_JWIPreprocessor_Parser
    public final void synpred237_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1077:9: ( LESSTHAN ASSIGNMENT_EQUALS )
        // JWIPreprocessor_Parser.g:1077:10: LESSTHAN ASSIGNMENT_EQUALS
        {
        match(input,LESSTHAN,FOLLOW_LESSTHAN_in_synpred237_JWIPreprocessor_Parser5917); if (state.failed) return ;
        match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_synpred237_JWIPreprocessor_Parser5919); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred237_JWIPreprocessor_Parser

    // $ANTLR start synpred238_JWIPreprocessor_Parser
    public final void synpred238_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1080:9: ( GREATERTHAN ASSIGNMENT_EQUALS )
        // JWIPreprocessor_Parser.g:1080:10: GREATERTHAN ASSIGNMENT_EQUALS
        {
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred238_JWIPreprocessor_Parser5951); if (state.failed) return ;
        match(input,ASSIGNMENT_EQUALS,FOLLOW_ASSIGNMENT_EQUALS_in_synpred238_JWIPreprocessor_Parser5953); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred238_JWIPreprocessor_Parser

    // $ANTLR start synpred241_JWIPreprocessor_Parser
    public final void synpred241_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1092:9: ( LESSTHAN LESSTHAN )
        // JWIPreprocessor_Parser.g:1092:10: LESSTHAN LESSTHAN
        {
        match(input,LESSTHAN,FOLLOW_LESSTHAN_in_synpred241_JWIPreprocessor_Parser6044); if (state.failed) return ;
        match(input,LESSTHAN,FOLLOW_LESSTHAN_in_synpred241_JWIPreprocessor_Parser6046); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred241_JWIPreprocessor_Parser

    // $ANTLR start synpred242_JWIPreprocessor_Parser
    public final void synpred242_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1095:9: ( GREATERTHAN GREATERTHAN GREATERTHAN )
        // JWIPreprocessor_Parser.g:1095:10: GREATERTHAN GREATERTHAN GREATERTHAN
        {
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred242_JWIPreprocessor_Parser6078); if (state.failed) return ;
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred242_JWIPreprocessor_Parser6080); if (state.failed) return ;
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred242_JWIPreprocessor_Parser6082); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred242_JWIPreprocessor_Parser

    // $ANTLR start synpred243_JWIPreprocessor_Parser
    public final void synpred243_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1100:9: ( GREATERTHAN GREATERTHAN )
        // JWIPreprocessor_Parser.g:1100:10: GREATERTHAN GREATERTHAN
        {
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred243_JWIPreprocessor_Parser6118); if (state.failed) return ;
        match(input,GREATERTHAN,FOLLOW_GREATERTHAN_in_synpred243_JWIPreprocessor_Parser6120); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred243_JWIPreprocessor_Parser

    // $ANTLR start synpred255_JWIPreprocessor_Parser
    public final void synpred255_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1125:9: ( castExpression )
        // JWIPreprocessor_Parser.g:1125:9: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred255_JWIPreprocessor_Parser6329);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred255_JWIPreprocessor_Parser

    // $ANTLR start synpred259_JWIPreprocessor_Parser
    public final void synpred259_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1130:8: ( LEFTPARENTHESIS primitiveType RIGHTPARENTHESIS unaryExpression )
        // JWIPreprocessor_Parser.g:1130:8: LEFTPARENTHESIS primitiveType RIGHTPARENTHESIS unaryExpression
        {
        match(input,LEFTPARENTHESIS,FOLLOW_LEFTPARENTHESIS_in_synpred259_JWIPreprocessor_Parser6367); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred259_JWIPreprocessor_Parser6369);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;
        match(input,RIGHTPARENTHESIS,FOLLOW_RIGHTPARENTHESIS_in_synpred259_JWIPreprocessor_Parser6371); if (state.failed) return ;
        pushFollow(FOLLOW_unaryExpression_in_synpred259_JWIPreprocessor_Parser6373);
        unaryExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred259_JWIPreprocessor_Parser

    // $ANTLR start synpred260_JWIPreprocessor_Parser
    public final void synpred260_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1131:25: ( type )
        // JWIPreprocessor_Parser.g:1131:25: type
        {
        pushFollow(FOLLOW_type_in_synpred260_JWIPreprocessor_Parser6385);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred260_JWIPreprocessor_Parser

    // $ANTLR start synpred262_JWIPreprocessor_Parser
    public final void synpred262_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1136:15: ( DOT Identifier )
        // JWIPreprocessor_Parser.g:1136:15: DOT Identifier
        {
        match(input,DOT,FOLLOW_DOT_in_synpred262_JWIPreprocessor_Parser6426); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred262_JWIPreprocessor_Parser6428); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred262_JWIPreprocessor_Parser

    // $ANTLR start synpred263_JWIPreprocessor_Parser
    public final void synpred263_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1136:32: ( identifierSuffix )
        // JWIPreprocessor_Parser.g:1136:32: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred263_JWIPreprocessor_Parser6432);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred263_JWIPreprocessor_Parser

    // $ANTLR start synpred268_JWIPreprocessor_Parser
    public final void synpred268_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1140:21: ( DOT Identifier )
        // JWIPreprocessor_Parser.g:1140:21: DOT Identifier
        {
        match(input,DOT,FOLLOW_DOT_in_synpred268_JWIPreprocessor_Parser6480); if (state.failed) return ;
        match(input,Identifier,FOLLOW_Identifier_in_synpred268_JWIPreprocessor_Parser6482); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred268_JWIPreprocessor_Parser

    // $ANTLR start synpred269_JWIPreprocessor_Parser
    public final void synpred269_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1140:38: ( identifierSuffix )
        // JWIPreprocessor_Parser.g:1140:38: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred269_JWIPreprocessor_Parser6486);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred269_JWIPreprocessor_Parser

    // $ANTLR start synpred275_JWIPreprocessor_Parser
    public final void synpred275_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1147:10: ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )
        // JWIPreprocessor_Parser.g:1147:10: LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET
        {
        match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_synpred275_JWIPreprocessor_Parser6561); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred275_JWIPreprocessor_Parser6563);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_synpred275_JWIPreprocessor_Parser6565); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred275_JWIPreprocessor_Parser

    // $ANTLR start synpred288_JWIPreprocessor_Parser
    public final void synpred288_JWIPreprocessor_Parser_fragment() throws RecognitionException {   
        // JWIPreprocessor_Parser.g:1173:44: ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )
        // JWIPreprocessor_Parser.g:1173:44: LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET
        {
        match(input,LEFTSQUAREBRACKET,FOLLOW_LEFTSQUAREBRACKET_in_synpred288_JWIPreprocessor_Parser6801); if (state.failed) return ;
        pushFollow(FOLLOW_expression_in_synpred288_JWIPreprocessor_Parser6803);
        expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,RIGHTSQUAREBRACKET,FOLLOW_RIGHTSQUAREBRACKET_in_synpred288_JWIPreprocessor_Parser6805); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred288_JWIPreprocessor_Parser

    // Delegated rules

    public final boolean synpred243_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred243_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred269_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred269_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred183_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred183_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred212_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred212_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred224_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred224_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred206_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred206_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred263_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred263_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred238_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred238_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred268_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred268_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred204_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred204_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred136_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred136_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred188_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred188_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred262_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred262_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred151_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred151_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred207_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred207_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred241_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred241_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred226_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred226_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred208_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred208_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred189_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred189_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred288_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred288_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred242_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred242_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred275_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred275_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred237_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred237_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred140_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred140_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred178_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred178_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred260_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred260_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred259_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred259_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred214_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred214_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred225_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred225_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred255_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred255_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred177_JWIPreprocessor_Parser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred177_JWIPreprocessor_Parser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA99 dfa99 = new DFA99(this);
    protected DFA103 dfa103 = new DFA103(this);
    protected DFA125 dfa125 = new DFA125(this);
    protected DFA133 dfa133 = new DFA133(this);
    protected DFA142 dfa142 = new DFA142(this);
    protected DFA143 dfa143 = new DFA143(this);
    protected DFA145 dfa145 = new DFA145(this);
    protected DFA146 dfa146 = new DFA146(this);
    protected DFA158 dfa158 = new DFA158(this);
    protected DFA164 dfa164 = new DFA164(this);
    protected DFA165 dfa165 = new DFA165(this);
    protected DFA168 dfa168 = new DFA168(this);
    protected DFA170 dfa170 = new DFA170(this);
    protected DFA175 dfa175 = new DFA175(this);
    protected DFA174 dfa174 = new DFA174(this);
    protected DFA181 dfa181 = new DFA181(this);
    static final String DFA8_eotS =
        "\22\uffff";
    static final String DFA8_eofS =
        "\1\2\21\uffff";
    static final String DFA8_minS =
        "\1\4\1\0\20\uffff";
    static final String DFA8_maxS =
        "\1\164\1\0\20\uffff";
    static final String DFA8_acceptS =
        "\2\uffff\1\2\16\uffff\1\1";
    static final String DFA8_specialS =
        "\1\uffff\1\0\20\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\2\11\uffff\1\2\6\uffff\1\2\7\uffff\1\2\5\uffff\1\2\2\uffff"+
            "\1\2\4\uffff\4\2\2\uffff\2\2\14\uffff\1\2\14\uffff\1\1\47\uffff"+
            "\1\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "243:1: compilationUnit returns [String packageName] : ( annotations (packageName1= packageDeclaration ( importDeclaration )* ( typeDeclaration )* | classOrInterfaceDeclaration ( typeDeclaration )* ) | ( (packageName2= packageDeclaration )? ) ( importDeclaration )* ( standaloneIntentionDeclaration | typeDeclaration )* );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA8_1 = input.LA(1);

                         
                        int index8_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_JWIPreprocessor_Parser()) ) {s = 17;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index8_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 8, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA99_eotS =
        "\57\uffff";
    static final String DFA99_eofS =
        "\57\uffff";
    static final String DFA99_minS =
        "\1\16\1\uffff\15\0\40\uffff";
    static final String DFA99_maxS =
        "\1\170\1\uffff\15\0\40\uffff";
    static final String DFA99_acceptS =
        "\1\uffff\1\1\15\uffff\1\2\37\uffff";
    static final String DFA99_specialS =
        "\2\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\40\uffff}>";
    static final String[] DFA99_transitionS = {
            "\1\17\1\15\1\17\1\15\2\uffff\1\15\2\17\1\uffff\1\17\1\15\2\uffff"+
            "\1\11\1\17\1\uffff\1\15\2\17\3\uffff\1\15\1\17\1\15\1\uffff"+
            "\1\13\1\12\1\uffff\4\17\1\15\2\17\1\4\2\17\1\2\1\17\2\uffff"+
            "\1\11\2\17\1\16\1\uffff\1\17\2\uffff\2\17\1\3\3\uffff\1\1\3"+
            "\uffff\2\17\2\uffff\2\17\1\uffff\2\17\11\uffff\1\17\12\uffff"+
            "\3\5\2\uffff\1\6\1\uffff\1\7\1\10\2\uffff\2\17\2\uffff\1\14",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA99_eot = DFA.unpackEncodedString(DFA99_eotS);
    static final short[] DFA99_eof = DFA.unpackEncodedString(DFA99_eofS);
    static final char[] DFA99_min = DFA.unpackEncodedStringToUnsignedChars(DFA99_minS);
    static final char[] DFA99_max = DFA.unpackEncodedStringToUnsignedChars(DFA99_maxS);
    static final short[] DFA99_accept = DFA.unpackEncodedString(DFA99_acceptS);
    static final short[] DFA99_special = DFA.unpackEncodedString(DFA99_specialS);
    static final short[][] DFA99_transition;

    static {
        int numStates = DFA99_transitionS.length;
        DFA99_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA99_transition[i] = DFA.unpackEncodedString(DFA99_transitionS[i]);
        }
    }

    class DFA99 extends DFA {

        public DFA99(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 99;
            this.eot = DFA99_eot;
            this.eof = DFA99_eof;
            this.min = DFA99_min;
            this.max = DFA99_max;
            this.accept = DFA99_accept;
            this.special = DFA99_special;
            this.transition = DFA99_transition;
        }
        public String getDescription() {
            return "753:19: ( explicitConstructorInvocation )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA99_2 = input.LA(1);

                         
                        int index99_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA99_3 = input.LA(1);

                         
                        int index99_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA99_4 = input.LA(1);

                         
                        int index99_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA99_5 = input.LA(1);

                         
                        int index99_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_5);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA99_6 = input.LA(1);

                         
                        int index99_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_6);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA99_7 = input.LA(1);

                         
                        int index99_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_7);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA99_8 = input.LA(1);

                         
                        int index99_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA99_9 = input.LA(1);

                         
                        int index99_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_9);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA99_10 = input.LA(1);

                         
                        int index99_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_10);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA99_11 = input.LA(1);

                         
                        int index99_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_11);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA99_12 = input.LA(1);

                         
                        int index99_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_12);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA99_13 = input.LA(1);

                         
                        int index99_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_13);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA99_14 = input.LA(1);

                         
                        int index99_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred136_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 15;}

                         
                        input.seek(index99_14);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 99, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA103_eotS =
        "\17\uffff";
    static final String DFA103_eofS =
        "\17\uffff";
    static final String DFA103_minS =
        "\1\17\1\uffff\1\0\1\uffff\1\0\12\uffff";
    static final String DFA103_maxS =
        "\1\170\1\uffff\1\0\1\uffff\1\0\12\uffff";
    static final String DFA103_acceptS =
        "\1\uffff\1\1\1\uffff\1\2\13\uffff";
    static final String DFA103_specialS =
        "\2\uffff\1\0\1\uffff\1\1\12\uffff}>";
    static final String[] DFA103_transitionS = {
            "\1\3\1\uffff\1\3\2\uffff\1\3\4\uffff\1\3\2\uffff\1\3\2\uffff"+
            "\1\3\5\uffff\1\3\1\uffff\1\3\1\uffff\2\3\5\uffff\1\3\2\uffff"+
            "\1\4\2\uffff\1\2\3\uffff\1\3\2\uffff\1\3\6\uffff\1\3\3\uffff"+
            "\1\1\40\uffff\3\3\2\uffff\1\3\1\uffff\2\3\6\uffff\1\3",
            "",
            "\1\uffff",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA103_eot = DFA.unpackEncodedString(DFA103_eotS);
    static final short[] DFA103_eof = DFA.unpackEncodedString(DFA103_eofS);
    static final char[] DFA103_min = DFA.unpackEncodedStringToUnsignedChars(DFA103_minS);
    static final char[] DFA103_max = DFA.unpackEncodedStringToUnsignedChars(DFA103_maxS);
    static final short[] DFA103_accept = DFA.unpackEncodedString(DFA103_acceptS);
    static final short[] DFA103_special = DFA.unpackEncodedString(DFA103_specialS);
    static final short[][] DFA103_transition;

    static {
        int numStates = DFA103_transitionS.length;
        DFA103_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA103_transition[i] = DFA.unpackEncodedString(DFA103_transitionS[i]);
        }
    }

    class DFA103 extends DFA {

        public DFA103(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 103;
            this.eot = DFA103_eot;
            this.eof = DFA103_eof;
            this.min = DFA103_min;
            this.max = DFA103_max;
            this.accept = DFA103_accept;
            this.special = DFA103_special;
            this.transition = DFA103_transition;
        }
        public String getDescription() {
            return "756:1: explicitConstructorInvocation : ( ( nonWildcardTypeArguments )? ( THIS | SUPER ) arguments SEMICOLON | primary DOT ( nonWildcardTypeArguments )? SUPER arguments SEMICOLON );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA103_2 = input.LA(1);

                         
                        int index103_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred140_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index103_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA103_4 = input.LA(1);

                         
                        int index103_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred140_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index103_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 103, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA125_eotS =
        "\56\uffff";
    static final String DFA125_eofS =
        "\56\uffff";
    static final String DFA125_minS =
        "\1\16\4\0\51\uffff";
    static final String DFA125_maxS =
        "\1\170\4\0\51\uffff";
    static final String DFA125_acceptS =
        "\5\uffff\1\2\10\uffff\1\3\36\uffff\1\1";
    static final String DFA125_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\51\uffff}>";
    static final String[] DFA125_transitionS = {
            "\1\5\1\4\1\16\1\4\2\uffff\1\4\1\5\1\16\1\uffff\1\16\1\4\2\uffff"+
            "\1\16\1\1\1\uffff\1\4\2\16\3\uffff\1\4\1\5\1\4\1\uffff\2\16"+
            "\1\uffff\3\5\1\16\1\4\2\5\5\16\2\uffff\4\16\1\uffff\1\16\2\uffff"+
            "\1\16\1\uffff\1\16\7\uffff\1\2\1\16\2\uffff\2\16\1\uffff\2\16"+
            "\11\uffff\1\16\12\uffff\3\16\2\uffff\1\16\1\uffff\2\16\2\uffff"+
            "\1\5\1\16\2\uffff\1\3",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA125_eot = DFA.unpackEncodedString(DFA125_eotS);
    static final short[] DFA125_eof = DFA.unpackEncodedString(DFA125_eofS);
    static final char[] DFA125_min = DFA.unpackEncodedStringToUnsignedChars(DFA125_minS);
    static final char[] DFA125_max = DFA.unpackEncodedStringToUnsignedChars(DFA125_maxS);
    static final short[] DFA125_accept = DFA.unpackEncodedString(DFA125_acceptS);
    static final short[] DFA125_special = DFA.unpackEncodedString(DFA125_specialS);
    static final short[][] DFA125_transition;

    static {
        int numStates = DFA125_transitionS.length;
        DFA125_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA125_transition[i] = DFA.unpackEncodedString(DFA125_transitionS[i]);
        }
    }

    class DFA125 extends DFA {

        public DFA125(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 125;
            this.eot = DFA125_eot;
            this.eof = DFA125_eof;
            this.min = DFA125_min;
            this.max = DFA125_max;
            this.accept = DFA125_accept;
            this.special = DFA125_special;
            this.transition = DFA125_transition;
        }
        public String getDescription() {
            return "899:1: blockStatement : ( localVariableDeclarationStatement | classOrInterfaceDeclaration | statement );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA125_1 = input.LA(1);

                         
                        int index125_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_JWIPreprocessor_Parser()) ) {s = 45;}

                        else if ( (synpred178_JWIPreprocessor_Parser()) ) {s = 5;}

                         
                        input.seek(index125_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA125_2 = input.LA(1);

                         
                        int index125_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_JWIPreprocessor_Parser()) ) {s = 45;}

                        else if ( (synpred178_JWIPreprocessor_Parser()) ) {s = 5;}

                         
                        input.seek(index125_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA125_3 = input.LA(1);

                         
                        int index125_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_JWIPreprocessor_Parser()) ) {s = 45;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index125_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA125_4 = input.LA(1);

                         
                        int index125_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred177_JWIPreprocessor_Parser()) ) {s = 45;}

                        else if ( (true) ) {s = 14;}

                         
                        input.seek(index125_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 125, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA133_eotS =
        "\22\uffff";
    static final String DFA133_eofS =
        "\22\uffff";
    static final String DFA133_minS =
        "\1\17\17\uffff\1\44\1\uffff";
    static final String DFA133_maxS =
        "\1\170\17\uffff\1\146\1\uffff";
    static final String DFA133_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\uffff\1\20";
    static final String DFA133_specialS =
        "\22\uffff}>";
    static final String[] DFA133_transitionS = {
            "\1\17\1\14\1\17\2\uffff\1\17\1\uffff\1\15\1\uffff\1\6\1\17\2"+
            "\uffff\1\17\2\uffff\1\17\1\4\1\3\3\uffff\1\17\1\uffff\1\17\1"+
            "\uffff\2\17\4\uffff\1\12\1\17\2\uffff\1\17\1\10\1\11\1\17\1"+
            "\13\2\uffff\1\17\1\7\1\5\1\17\1\uffff\1\16\2\uffff\1\1\1\uffff"+
            "\1\17\10\uffff\1\17\2\uffff\2\17\1\uffff\2\17\11\uffff\1\17"+
            "\12\uffff\3\17\2\uffff\1\17\1\uffff\2\17\3\uffff\1\2\2\uffff"+
            "\1\20",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\17\32\uffff\1\17\1\21\3\uffff\1\17\1\uffff\1\17\1\uffff"+
            "\2\17\1\uffff\1\17\2\uffff\20\17\1\uffff\10\17",
            ""
    };

    static final short[] DFA133_eot = DFA.unpackEncodedString(DFA133_eotS);
    static final short[] DFA133_eof = DFA.unpackEncodedString(DFA133_eofS);
    static final char[] DFA133_min = DFA.unpackEncodedStringToUnsignedChars(DFA133_minS);
    static final char[] DFA133_max = DFA.unpackEncodedStringToUnsignedChars(DFA133_maxS);
    static final short[] DFA133_accept = DFA.unpackEncodedString(DFA133_acceptS);
    static final short[] DFA133_special = DFA.unpackEncodedString(DFA133_specialS);
    static final short[][] DFA133_transition;

    static {
        int numStates = DFA133_transitionS.length;
        DFA133_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA133_transition[i] = DFA.unpackEncodedString(DFA133_transitionS[i]);
        }
    }

    class DFA133 extends DFA {

        public DFA133(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 133;
            this.eot = DFA133_eot;
            this.eof = DFA133_eof;
            this.min = DFA133_min;
            this.max = DFA133_max;
            this.accept = DFA133_accept;
            this.special = DFA133_special;
            this.transition = DFA133_transition;
        }
        public String getDescription() {
            return "917:1: statement : ( block | ASSERT expression ( COLON expression )? SEMICOLON | IF parExpression statement ( options {k=1; } : ELSE statement )? | FOR LEFTPARENTHESIS forControl RIGHTPARENTHESIS statement | WHILE parExpression statement | DO statement WHILE parExpression SEMICOLON | TRY block ( catches FINALLY block | catches | FINALLY block ) | SWITCH parExpression LEFTBRACE switchBlockStatementGroups RIGHTBRACE | SYNCHRONIZED parExpression block | RETURN ( expression )? SEMICOLON | THROW expression SEMICOLON | BREAK ( Identifier )? SEMICOLON | CONTINUE ( Identifier )? SEMICOLON | SEMICOLON | statementExpression SEMICOLON | Identifier COLON statement );";
        }
    }
    static final String DFA142_eotS =
        "\u0087\uffff";
    static final String DFA142_eofS =
        "\u0087\uffff";
    static final String DFA142_minS =
        "\2\17\1\170\1\44\1\106\22\uffff\2\106\1\17\1\170\2\17\1\25\1\17"+
        "\1\77\30\uffff\1\107\1\uffff\1\77\21\0\2\uffff\3\0\21\uffff\1\0"+
        "\5\uffff\1\0\30\uffff\1\0\5\uffff";
    static final String DFA142_maxS =
        "\5\170\22\uffff\10\170\1\140\30\uffff\1\107\1\uffff\1\140\21\0\2"+
        "\uffff\3\0\21\uffff\1\0\5\uffff\1\0\30\uffff\1\0\5\uffff";
    static final String DFA142_acceptS =
        "\5\uffff\1\2\166\uffff\1\1\12\uffff";
    static final String DFA142_specialS =
        "\73\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\2\uffff\1\21\1\22\1\23\21\uffff\1\24\5\uffff"+
        "\1\25\30\uffff\1\26\5\uffff}>";
    static final String[] DFA142_transitionS = {
            "\1\4\1\uffff\1\4\2\uffff\1\4\4\uffff\1\4\2\uffff\1\5\1\1\1\uffff"+
            "\1\4\5\uffff\1\4\1\uffff\1\4\1\uffff\2\5\5\uffff\1\4\2\uffff"+
            "\1\5\2\uffff\1\5\3\uffff\1\5\2\uffff\1\5\1\uffff\1\5\4\uffff"+
            "\1\5\7\uffff\1\2\1\5\2\uffff\2\5\1\uffff\2\5\11\uffff\1\5\12"+
            "\uffff\3\5\2\uffff\1\5\1\uffff\2\5\6\uffff\1\3",
            "\1\30\1\uffff\1\30\2\uffff\1\30\4\uffff\1\30\3\uffff\1\31\1"+
            "\uffff\1\30\5\uffff\1\30\1\uffff\1\30\10\uffff\1\30\33\uffff"+
            "\1\32\53\uffff\1\27",
            "\1\33",
            "\1\5\32\uffff\1\5\1\uffff\1\5\2\uffff\1\5\1\uffff\1\36\1\uffff"+
            "\1\34\1\5\1\uffff\1\35\2\uffff\20\5\1\uffff\10\5\21\uffff\1"+
            "\37",
            "\1\70\4\uffff\1\5\54\uffff\1\72",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\75\1\uffff\1\73\2\uffff\1\74\54\uffff\1\76",
            "\1\77\61\uffff\1\100",
            "\1\102\1\uffff\1\102\2\uffff\1\102\4\uffff\1\102\3\uffff\1"+
            "\103\1\uffff\1\102\5\uffff\1\102\1\uffff\1\102\10\uffff\1\102"+
            "\33\uffff\1\104\53\uffff\1\101",
            "\1\105",
            "\1\111\1\uffff\1\111\2\uffff\1\111\4\uffff\1\111\3\uffff\1"+
            "\112\1\uffff\1\111\5\uffff\1\111\1\uffff\1\111\10\uffff\1\111"+
            "\23\uffff\1\107\6\uffff\1\106\1\113\53\uffff\1\110",
            "\1\117\1\uffff\1\117\2\uffff\1\117\4\uffff\1\117\2\uffff\1"+
            "\5\2\uffff\1\117\5\uffff\1\117\1\uffff\1\117\1\uffff\2\5\5\uffff"+
            "\1\117\2\uffff\1\5\2\uffff\1\5\3\uffff\1\5\2\uffff\1\5\6\uffff"+
            "\1\5\3\uffff\1\5\4\uffff\1\5\1\120\1\uffff\2\5\1\uffff\2\5\11"+
            "\uffff\1\5\1\uffff\1\5\10\uffff\3\5\2\uffff\1\5\1\uffff\2\5"+
            "\6\uffff\1\116",
            "\1\5\23\uffff\1\5\11\uffff\1\5\2\uffff\1\5\21\uffff\1\5\57"+
            "\uffff\1\142",
            "\1\5\1\uffff\1\5\2\uffff\1\5\4\uffff\1\5\2\uffff\1\5\2\uffff"+
            "\1\5\5\uffff\1\5\1\uffff\1\5\1\uffff\2\5\5\uffff\1\5\2\uffff"+
            "\1\5\2\uffff\1\5\3\uffff\1\5\2\uffff\1\5\6\uffff\1\5\2\uffff"+
            "\1\150\5\uffff\1\5\2\uffff\2\5\1\uffff\2\5\11\uffff\1\5\12\uffff"+
            "\3\5\2\uffff\1\5\1\uffff\2\5\6\uffff\1\5",
            "\1\5\1\174\1\5\4\uffff\1\5\31\uffff\1\5",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u0081",
            "",
            "\1\5\1\174\1\5\4\uffff\1\5\31\uffff\1\5",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA142_eot = DFA.unpackEncodedString(DFA142_eotS);
    static final short[] DFA142_eof = DFA.unpackEncodedString(DFA142_eofS);
    static final char[] DFA142_min = DFA.unpackEncodedStringToUnsignedChars(DFA142_minS);
    static final char[] DFA142_max = DFA.unpackEncodedStringToUnsignedChars(DFA142_maxS);
    static final short[] DFA142_accept = DFA.unpackEncodedString(DFA142_acceptS);
    static final short[] DFA142_special = DFA.unpackEncodedString(DFA142_specialS);
    static final short[][] DFA142_transition;

    static {
        int numStates = DFA142_transitionS.length;
        DFA142_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA142_transition[i] = DFA.unpackEncodedString(DFA142_transitionS[i]);
        }
    }

    class DFA142 extends DFA {

        public DFA142(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 142;
            this.eot = DFA142_eot;
            this.eof = DFA142_eof;
            this.min = DFA142_min;
            this.max = DFA142_max;
            this.accept = DFA142_accept;
            this.special = DFA142_special;
            this.transition = DFA142_transition;
        }
        public String getDescription() {
            return "970:1: forControl options {k=3; } : ( enhancedForControl | ( forInit )? SEMICOLON ( expression )? SEMICOLON ( forUpdate )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA142_59 = input.LA(1);

                         
                        int index142_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_59);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA142_60 = input.LA(1);

                         
                        int index142_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_60);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA142_61 = input.LA(1);

                         
                        int index142_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_61);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA142_62 = input.LA(1);

                         
                        int index142_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_62);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA142_63 = input.LA(1);

                         
                        int index142_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_63);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA142_64 = input.LA(1);

                         
                        int index142_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_64);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA142_65 = input.LA(1);

                         
                        int index142_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_65);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA142_66 = input.LA(1);

                         
                        int index142_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_66);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA142_67 = input.LA(1);

                         
                        int index142_67 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_67);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA142_68 = input.LA(1);

                         
                        int index142_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_68);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA142_69 = input.LA(1);

                         
                        int index142_69 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_69);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA142_70 = input.LA(1);

                         
                        int index142_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_70);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA142_71 = input.LA(1);

                         
                        int index142_71 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_71);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA142_72 = input.LA(1);

                         
                        int index142_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_72);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA142_73 = input.LA(1);

                         
                        int index142_73 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_73);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA142_74 = input.LA(1);

                         
                        int index142_74 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_74);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA142_75 = input.LA(1);

                         
                        int index142_75 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_75);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA142_78 = input.LA(1);

                         
                        int index142_78 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_78);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA142_79 = input.LA(1);

                         
                        int index142_79 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_79);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA142_80 = input.LA(1);

                         
                        int index142_80 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_80);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA142_98 = input.LA(1);

                         
                        int index142_98 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_98);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA142_104 = input.LA(1);

                         
                        int index142_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_104);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA142_129 = input.LA(1);

                         
                        int index142_129 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred208_JWIPreprocessor_Parser()) ) {s = 124;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index142_129);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 142, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA143_eotS =
        "\26\uffff";
    static final String DFA143_eofS =
        "\26\uffff";
    static final String DFA143_minS =
        "\1\17\2\uffff\2\0\21\uffff";
    static final String DFA143_maxS =
        "\1\170\2\uffff\2\0\21\uffff";
    static final String DFA143_acceptS =
        "\1\uffff\1\1\3\uffff\1\2\20\uffff";
    static final String DFA143_specialS =
        "\3\uffff\1\0\1\1\21\uffff}>";
    static final String[] DFA143_transitionS = {
            "\1\4\1\uffff\1\4\2\uffff\1\4\4\uffff\1\4\2\uffff\1\5\1\1\1\uffff"+
            "\1\4\5\uffff\1\4\1\uffff\1\4\1\uffff\2\5\5\uffff\1\4\2\uffff"+
            "\1\5\2\uffff\1\5\3\uffff\1\5\2\uffff\1\5\6\uffff\1\5\7\uffff"+
            "\1\1\1\5\2\uffff\2\5\1\uffff\2\5\11\uffff\1\5\12\uffff\3\5\2"+
            "\uffff\1\5\1\uffff\2\5\6\uffff\1\3",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA143_eot = DFA.unpackEncodedString(DFA143_eotS);
    static final short[] DFA143_eof = DFA.unpackEncodedString(DFA143_eofS);
    static final char[] DFA143_min = DFA.unpackEncodedStringToUnsignedChars(DFA143_minS);
    static final char[] DFA143_max = DFA.unpackEncodedStringToUnsignedChars(DFA143_maxS);
    static final short[] DFA143_accept = DFA.unpackEncodedString(DFA143_acceptS);
    static final short[] DFA143_special = DFA.unpackEncodedString(DFA143_specialS);
    static final short[][] DFA143_transition;

    static {
        int numStates = DFA143_transitionS.length;
        DFA143_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA143_transition[i] = DFA.unpackEncodedString(DFA143_transitionS[i]);
        }
    }

    class DFA143 extends DFA {

        public DFA143(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 143;
            this.eot = DFA143_eot;
            this.eof = DFA143_eof;
            this.min = DFA143_min;
            this.max = DFA143_max;
            this.accept = DFA143_accept;
            this.special = DFA143_special;
            this.transition = DFA143_transition;
        }
        public String getDescription() {
            return "976:1: forInit : ( localVariableDeclaration | expressionList );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA143_3 = input.LA(1);

                         
                        int index143_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred212_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index143_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA143_4 = input.LA(1);

                         
                        int index143_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred212_JWIPreprocessor_Parser()) ) {s = 1;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index143_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 143, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA145_eotS =
        "\16\uffff";
    static final String DFA145_eofS =
        "\1\14\15\uffff";
    static final String DFA145_minS =
        "\1\77\13\0\2\uffff";
    static final String DFA145_maxS =
        "\1\145\13\0\2\uffff";
    static final String DFA145_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA145_specialS =
        "\1\uffff\1\7\1\4\1\0\1\11\1\6\1\5\1\2\1\12\1\10\1\3\1\1\2\uffff}>";
    static final String[] DFA145_transitionS = {
            "\3\14\1\uffff\1\14\1\uffff\1\14\1\uffff\1\14\1\12\1\13\5\uffff"+
            "\1\2\2\uffff\1\3\2\uffff\1\4\1\uffff\1\5\1\uffff\1\11\1\uffff"+
            "\1\10\4\uffff\1\1\1\uffff\1\6\2\uffff\1\7",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA145_eot = DFA.unpackEncodedString(DFA145_eotS);
    static final short[] DFA145_eof = DFA.unpackEncodedString(DFA145_eofS);
    static final char[] DFA145_min = DFA.unpackEncodedStringToUnsignedChars(DFA145_minS);
    static final char[] DFA145_max = DFA.unpackEncodedStringToUnsignedChars(DFA145_maxS);
    static final short[] DFA145_accept = DFA.unpackEncodedString(DFA145_acceptS);
    static final short[] DFA145_special = DFA.unpackEncodedString(DFA145_specialS);
    static final short[][] DFA145_transition;

    static {
        int numStates = DFA145_transitionS.length;
        DFA145_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA145_transition[i] = DFA.unpackEncodedString(DFA145_transitionS[i]);
        }
    }

    class DFA145 extends DFA {

        public DFA145(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 145;
            this.eot = DFA145_eot;
            this.eof = DFA145_eof;
            this.min = DFA145_min;
            this.max = DFA145_max;
            this.accept = DFA145_accept;
            this.special = DFA145_special;
            this.transition = DFA145_transition;
        }
        public String getDescription() {
            return "1008:31: ( assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA145_3 = input.LA(1);

                         
                        int index145_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA145_11 = input.LA(1);

                         
                        int index145_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_11);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA145_7 = input.LA(1);

                         
                        int index145_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_7);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA145_10 = input.LA(1);

                         
                        int index145_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_10);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA145_2 = input.LA(1);

                         
                        int index145_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_2);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA145_6 = input.LA(1);

                         
                        int index145_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA145_5 = input.LA(1);

                         
                        int index145_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_5);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA145_1 = input.LA(1);

                         
                        int index145_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_1);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA145_9 = input.LA(1);

                         
                        int index145_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA145_4 = input.LA(1);

                         
                        int index145_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_4);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA145_8 = input.LA(1);

                         
                        int index145_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred214_JWIPreprocessor_Parser()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index145_8);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 145, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA146_eotS =
        "\17\uffff";
    static final String DFA146_eofS =
        "\17\uffff";
    static final String DFA146_minS =
        "\1\110\12\uffff\2\111\2\uffff";
    static final String DFA146_maxS =
        "\1\145\12\uffff\1\111\1\140\2\uffff";
    static final String DFA146_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA146_specialS =
        "\1\1\13\uffff\1\0\2\uffff}>";
    static final String[] DFA146_transitionS = {
            "\1\12\1\13\5\uffff\1\2\2\uffff\1\3\2\uffff\1\4\1\uffff\1\5\1"+
            "\uffff\1\11\1\uffff\1\10\4\uffff\1\1\1\uffff\1\6\2\uffff\1\7",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14",
            "\1\15\26\uffff\1\16",
            "",
            ""
    };

    static final short[] DFA146_eot = DFA.unpackEncodedString(DFA146_eotS);
    static final short[] DFA146_eof = DFA.unpackEncodedString(DFA146_eofS);
    static final char[] DFA146_min = DFA.unpackEncodedStringToUnsignedChars(DFA146_minS);
    static final char[] DFA146_max = DFA.unpackEncodedStringToUnsignedChars(DFA146_maxS);
    static final short[] DFA146_accept = DFA.unpackEncodedString(DFA146_acceptS);
    static final short[] DFA146_special = DFA.unpackEncodedString(DFA146_specialS);
    static final short[][] DFA146_transition;

    static {
        int numStates = DFA146_transitionS.length;
        DFA146_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA146_transition[i] = DFA.unpackEncodedString(DFA146_transitionS[i]);
        }
    }

    class DFA146 extends DFA {

        public DFA146(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 146;
            this.eot = DFA146_eot;
            this.eof = DFA146_eof;
            this.min = DFA146_min;
            this.max = DFA146_max;
            this.accept = DFA146_accept;
            this.special = DFA146_special;
            this.transition = DFA146_transition;
        }
        public String getDescription() {
            return "1011:1: assignmentOperator : ( ASSIGNMENT_EQUALS | PLUSEQUALS | MINUSEQUALS | ASTERISKEQUALS | SLASHEQUALS | BITWISE_AND_EQUALS | BITWISE_OR_EQUALS | CARETEQUALS | PERCENTEQUALS | ( LESSTHAN LESSTHAN ASSIGNMENT_EQUALS )=>t1= LESSTHAN t2= LESSTHAN t3= ASSIGNMENT_EQUALS {...}? | ( GREATERTHAN GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= GREATERTHAN t3= GREATERTHAN t4= ASSIGNMENT_EQUALS {...}? | ( GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS )=>t1= GREATERTHAN t2= GREATERTHAN t3= ASSIGNMENT_EQUALS {...}?);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA146_12 = input.LA(1);

                         
                        int index146_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA146_12==GREATERTHAN) && (synpred225_JWIPreprocessor_Parser())) {s = 13;}

                        else if ( (LA146_12==ASSIGNMENT_EQUALS) && (synpred226_JWIPreprocessor_Parser())) {s = 14;}

                         
                        input.seek(index146_12);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA146_0 = input.LA(1);

                         
                        int index146_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA146_0==ASSIGNMENT_EQUALS) ) {s = 1;}

                        else if ( (LA146_0==PLUSEQUALS) ) {s = 2;}

                        else if ( (LA146_0==MINUSEQUALS) ) {s = 3;}

                        else if ( (LA146_0==ASTERISKEQUALS) ) {s = 4;}

                        else if ( (LA146_0==SLASHEQUALS) ) {s = 5;}

                        else if ( (LA146_0==BITWISE_AND_EQUALS) ) {s = 6;}

                        else if ( (LA146_0==BITWISE_OR_EQUALS) ) {s = 7;}

                        else if ( (LA146_0==CARETEQUALS) ) {s = 8;}

                        else if ( (LA146_0==PERCENTEQUALS) ) {s = 9;}

                        else if ( (LA146_0==LESSTHAN) && (synpred224_JWIPreprocessor_Parser())) {s = 10;}

                        else if ( (LA146_0==GREATERTHAN) ) {s = 11;}

                         
                        input.seek(index146_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 146, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA158_eotS =
        "\30\uffff";
    static final String DFA158_eofS =
        "\30\uffff";
    static final String DFA158_minS =
        "\1\110\1\uffff\1\111\1\17\24\uffff";
    static final String DFA158_maxS =
        "\1\111\1\uffff\1\111\1\170\24\uffff";
    static final String DFA158_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\23\3";
    static final String DFA158_specialS =
        "\1\0\2\uffff\1\1\24\uffff}>";
    static final String[] DFA158_transitionS = {
            "\1\1\1\2",
            "",
            "\1\3",
            "\1\26\1\uffff\1\26\2\uffff\1\26\4\uffff\1\26\2\uffff\1\22\2"+
            "\uffff\1\26\5\uffff\1\26\1\uffff\1\26\1\uffff\1\24\1\23\5\uffff"+
            "\1\26\2\uffff\1\15\2\uffff\1\14\3\uffff\1\22\2\uffff\1\27\6"+
            "\uffff\1\13\4\uffff\1\4\3\uffff\1\11\2\uffff\1\7\1\5\1\uffff"+
            "\1\10\1\6\11\uffff\1\12\12\uffff\3\16\2\uffff\1\17\1\uffff\1"+
            "\20\1\21\6\uffff\1\25",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA158_eot = DFA.unpackEncodedString(DFA158_eotS);
    static final short[] DFA158_eof = DFA.unpackEncodedString(DFA158_eofS);
    static final char[] DFA158_min = DFA.unpackEncodedStringToUnsignedChars(DFA158_minS);
    static final char[] DFA158_max = DFA.unpackEncodedStringToUnsignedChars(DFA158_maxS);
    static final short[] DFA158_accept = DFA.unpackEncodedString(DFA158_acceptS);
    static final short[] DFA158_special = DFA.unpackEncodedString(DFA158_specialS);
    static final short[][] DFA158_transition;

    static {
        int numStates = DFA158_transitionS.length;
        DFA158_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA158_transition[i] = DFA.unpackEncodedString(DFA158_transitionS[i]);
        }
    }

    class DFA158 extends DFA {

        public DFA158(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 158;
            this.eot = DFA158_eot;
            this.eof = DFA158_eof;
            this.min = DFA158_min;
            this.max = DFA158_max;
            this.accept = DFA158_accept;
            this.special = DFA158_special;
            this.transition = DFA158_transition;
        }
        public String getDescription() {
            return "1091:1: shiftOp : ( ( LESSTHAN LESSTHAN )=>t1= LESSTHAN t2= LESSTHAN {...}? | ( GREATERTHAN GREATERTHAN GREATERTHAN )=>t1= GREATERTHAN t2= GREATERTHAN t3= GREATERTHAN {...}? | ( GREATERTHAN GREATERTHAN )=>t1= GREATERTHAN t2= GREATERTHAN {...}?);";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA158_0 = input.LA(1);

                         
                        int index158_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA158_0==LESSTHAN) && (synpred241_JWIPreprocessor_Parser())) {s = 1;}

                        else if ( (LA158_0==GREATERTHAN) ) {s = 2;}

                         
                        input.seek(index158_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA158_3 = input.LA(1);

                         
                        int index158_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA158_3==GREATERTHAN) && (synpred242_JWIPreprocessor_Parser())) {s = 4;}

                        else if ( (LA158_3==PLUS) && (synpred243_JWIPreprocessor_Parser())) {s = 5;}

                        else if ( (LA158_3==MINUS) && (synpred243_JWIPreprocessor_Parser())) {s = 6;}

                        else if ( (LA158_3==PLUSPLUS) && (synpred243_JWIPreprocessor_Parser())) {s = 7;}

                        else if ( (LA158_3==MINUSMINUS) && (synpred243_JWIPreprocessor_Parser())) {s = 8;}

                        else if ( (LA158_3==TILDE) && (synpred243_JWIPreprocessor_Parser())) {s = 9;}

                        else if ( (LA158_3==EXCLAMATIONMARK) && (synpred243_JWIPreprocessor_Parser())) {s = 10;}

                        else if ( (LA158_3==LEFTPARENTHESIS) && (synpred243_JWIPreprocessor_Parser())) {s = 11;}

                        else if ( (LA158_3==THIS) && (synpred243_JWIPreprocessor_Parser())) {s = 12;}

                        else if ( (LA158_3==SUPER) && (synpred243_JWIPreprocessor_Parser())) {s = 13;}

                        else if ( ((LA158_3>=HexLiteral && LA158_3<=OctalLiteral)) && (synpred243_JWIPreprocessor_Parser())) {s = 14;}

                        else if ( (LA158_3==FloatingPointLiteral) && (synpred243_JWIPreprocessor_Parser())) {s = 15;}

                        else if ( (LA158_3==CharacterLiteral) && (synpred243_JWIPreprocessor_Parser())) {s = 16;}

                        else if ( (LA158_3==StringLiteral) && (synpred243_JWIPreprocessor_Parser())) {s = 17;}

                        else if ( (LA158_3==FALSE||LA158_3==TRUE) && (synpred243_JWIPreprocessor_Parser())) {s = 18;}

                        else if ( (LA158_3==NULL) && (synpred243_JWIPreprocessor_Parser())) {s = 19;}

                        else if ( (LA158_3==NEW) && (synpred243_JWIPreprocessor_Parser())) {s = 20;}

                        else if ( (LA158_3==Identifier) && (synpred243_JWIPreprocessor_Parser())) {s = 21;}

                        else if ( (LA158_3==BOOLEAN||LA158_3==BYTE||LA158_3==CHAR||LA158_3==DOUBLE||LA158_3==FLOAT||LA158_3==INT||LA158_3==LONG||LA158_3==SHORT) && (synpred243_JWIPreprocessor_Parser())) {s = 22;}

                        else if ( (LA158_3==VOID) && (synpred243_JWIPreprocessor_Parser())) {s = 23;}

                         
                        input.seek(index158_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 158, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA164_eotS =
        "\21\uffff";
    static final String DFA164_eofS =
        "\21\uffff";
    static final String DFA164_minS =
        "\1\17\2\uffff\1\0\15\uffff";
    static final String DFA164_maxS =
        "\1\170\2\uffff\1\0\15\uffff";
    static final String DFA164_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\13\uffff\1\3";
    static final String DFA164_specialS =
        "\3\uffff\1\0\15\uffff}>";
    static final String[] DFA164_transitionS = {
            "\1\4\1\uffff\1\4\2\uffff\1\4\4\uffff\1\4\2\uffff\1\4\2\uffff"+
            "\1\4\5\uffff\1\4\1\uffff\1\4\1\uffff\2\4\5\uffff\1\4\2\uffff"+
            "\1\4\2\uffff\1\4\3\uffff\1\4\2\uffff\1\4\6\uffff\1\3\10\uffff"+
            "\1\1\20\uffff\1\2\12\uffff\3\4\2\uffff\1\4\1\uffff\2\4\6\uffff"+
            "\1\4",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA164_eot = DFA.unpackEncodedString(DFA164_eotS);
    static final short[] DFA164_eof = DFA.unpackEncodedString(DFA164_eofS);
    static final char[] DFA164_min = DFA.unpackEncodedStringToUnsignedChars(DFA164_minS);
    static final char[] DFA164_max = DFA.unpackEncodedStringToUnsignedChars(DFA164_maxS);
    static final short[] DFA164_accept = DFA.unpackEncodedString(DFA164_acceptS);
    static final short[] DFA164_special = DFA.unpackEncodedString(DFA164_specialS);
    static final short[][] DFA164_transition;

    static {
        int numStates = DFA164_transitionS.length;
        DFA164_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA164_transition[i] = DFA.unpackEncodedString(DFA164_transitionS[i]);
        }
    }

    class DFA164 extends DFA {

        public DFA164(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 164;
            this.eot = DFA164_eot;
            this.eof = DFA164_eof;
            this.min = DFA164_min;
            this.max = DFA164_max;
            this.accept = DFA164_accept;
            this.special = DFA164_special;
            this.transition = DFA164_transition;
        }
        public String getDescription() {
            return "1122:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | EXCLAMATIONMARK unaryExpression | castExpression | primary ( selector )* ( PLUSPLUS | MINUSMINUS )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA164_3 = input.LA(1);

                         
                        int index164_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred255_JWIPreprocessor_Parser()) ) {s = 16;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index164_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 164, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA165_eotS =
        "\7\uffff";
    static final String DFA165_eofS =
        "\7\uffff";
    static final String DFA165_minS =
        "\1\17\1\0\1\105\2\uffff\1\107\1\105";
    static final String DFA165_maxS =
        "\1\170\1\0\1\113\2\uffff\1\107\1\113";
    static final String DFA165_acceptS =
        "\3\uffff\1\2\1\1\2\uffff";
    static final String DFA165_specialS =
        "\1\uffff\1\0\5\uffff}>";
    static final String[] DFA165_transitionS = {
            "\1\2\1\uffff\1\2\2\uffff\1\2\4\uffff\1\2\2\uffff\1\3\2\uffff"+
            "\1\2\5\uffff\1\2\1\uffff\1\2\1\uffff\2\3\5\uffff\1\2\2\uffff"+
            "\1\3\2\uffff\1\3\3\uffff\1\3\2\uffff\1\3\6\uffff\1\3\10\uffff"+
            "\1\3\2\uffff\2\3\1\uffff\2\3\11\uffff\1\3\12\uffff\3\3\2\uffff"+
            "\1\3\1\uffff\2\3\6\uffff\1\1",
            "\1\uffff",
            "\1\4\1\5\4\uffff\1\3",
            "",
            "",
            "\1\6",
            "\1\4\1\5\4\uffff\1\3"
    };

    static final short[] DFA165_eot = DFA.unpackEncodedString(DFA165_eotS);
    static final short[] DFA165_eof = DFA.unpackEncodedString(DFA165_eofS);
    static final char[] DFA165_min = DFA.unpackEncodedStringToUnsignedChars(DFA165_minS);
    static final char[] DFA165_max = DFA.unpackEncodedStringToUnsignedChars(DFA165_maxS);
    static final short[] DFA165_accept = DFA.unpackEncodedString(DFA165_acceptS);
    static final short[] DFA165_special = DFA.unpackEncodedString(DFA165_specialS);
    static final short[][] DFA165_transition;

    static {
        int numStates = DFA165_transitionS.length;
        DFA165_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA165_transition[i] = DFA.unpackEncodedString(DFA165_transitionS[i]);
        }
    }

    class DFA165 extends DFA {

        public DFA165(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 165;
            this.eot = DFA165_eot;
            this.eof = DFA165_eof;
            this.min = DFA165_min;
            this.max = DFA165_max;
            this.accept = DFA165_accept;
            this.special = DFA165_special;
            this.transition = DFA165_transition;
        }
        public String getDescription() {
            return "1131:24: ( type | expression )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA165_1 = input.LA(1);

                         
                        int index165_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred260_JWIPreprocessor_Parser()) ) {s = 4;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index165_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 165, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA168_eotS =
        "\41\uffff";
    static final String DFA168_eofS =
        "\1\4\40\uffff";
    static final String DFA168_minS =
        "\1\44\1\0\1\uffff\1\0\35\uffff";
    static final String DFA168_maxS =
        "\1\146\1\0\1\uffff\1\0\35\uffff";
    static final String DFA168_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\34\uffff";
    static final String DFA168_specialS =
        "\1\uffff\1\0\1\uffff\1\1\35\uffff}>";
    static final String[] DFA168_transitionS = {
            "\1\4\32\uffff\3\4\1\uffff\1\4\1\2\1\4\1\1\3\4\1\uffff\1\3\2"+
            "\uffff\20\4\1\uffff\10\4",
            "\1\uffff",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA168_eot = DFA.unpackEncodedString(DFA168_eotS);
    static final short[] DFA168_eof = DFA.unpackEncodedString(DFA168_eofS);
    static final char[] DFA168_min = DFA.unpackEncodedStringToUnsignedChars(DFA168_minS);
    static final char[] DFA168_max = DFA.unpackEncodedStringToUnsignedChars(DFA168_maxS);
    static final short[] DFA168_accept = DFA.unpackEncodedString(DFA168_acceptS);
    static final short[] DFA168_special = DFA.unpackEncodedString(DFA168_specialS);
    static final short[][] DFA168_transition;

    static {
        int numStates = DFA168_transitionS.length;
        DFA168_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA168_transition[i] = DFA.unpackEncodedString(DFA168_transitionS[i]);
        }
    }

    class DFA168 extends DFA {

        public DFA168(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 168;
            this.eot = DFA168_eot;
            this.eof = DFA168_eof;
            this.min = DFA168_min;
            this.max = DFA168_max;
            this.accept = DFA168_accept;
            this.special = DFA168_special;
            this.transition = DFA168_transition;
        }
        public String getDescription() {
            return "1136:32: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA168_1 = input.LA(1);

                         
                        int index168_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_JWIPreprocessor_Parser()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA168_3 = input.LA(1);

                         
                        int index168_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred263_JWIPreprocessor_Parser()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index168_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 168, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA170_eotS =
        "\41\uffff";
    static final String DFA170_eofS =
        "\1\4\40\uffff";
    static final String DFA170_minS =
        "\1\44\1\0\1\uffff\1\0\35\uffff";
    static final String DFA170_maxS =
        "\1\146\1\0\1\uffff\1\0\35\uffff";
    static final String DFA170_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\34\uffff";
    static final String DFA170_specialS =
        "\1\uffff\1\0\1\uffff\1\1\35\uffff}>";
    static final String[] DFA170_transitionS = {
            "\1\4\32\uffff\3\4\1\uffff\1\4\1\2\1\4\1\1\3\4\1\uffff\1\3\2"+
            "\uffff\20\4\1\uffff\10\4",
            "\1\uffff",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA170_eot = DFA.unpackEncodedString(DFA170_eotS);
    static final short[] DFA170_eof = DFA.unpackEncodedString(DFA170_eofS);
    static final char[] DFA170_min = DFA.unpackEncodedStringToUnsignedChars(DFA170_minS);
    static final char[] DFA170_max = DFA.unpackEncodedStringToUnsignedChars(DFA170_maxS);
    static final short[] DFA170_accept = DFA.unpackEncodedString(DFA170_acceptS);
    static final short[] DFA170_special = DFA.unpackEncodedString(DFA170_specialS);
    static final short[][] DFA170_transition;

    static {
        int numStates = DFA170_transitionS.length;
        DFA170_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA170_transition[i] = DFA.unpackEncodedString(DFA170_transitionS[i]);
        }
    }

    class DFA170 extends DFA {

        public DFA170(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 170;
            this.eot = DFA170_eot;
            this.eof = DFA170_eof;
            this.min = DFA170_min;
            this.max = DFA170_max;
            this.accept = DFA170_accept;
            this.special = DFA170_special;
            this.transition = DFA170_transition;
        }
        public String getDescription() {
            return "1140:38: ( identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA170_1 = input.LA(1);

                         
                        int index170_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred269_JWIPreprocessor_Parser()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index170_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA170_3 = input.LA(1);

                         
                        int index170_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred269_JWIPreprocessor_Parser()) ) {s = 2;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index170_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 170, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA175_eotS =
        "\13\uffff";
    static final String DFA175_eofS =
        "\13\uffff";
    static final String DFA175_minS =
        "\1\104\1\17\1\uffff\1\25\7\uffff";
    static final String DFA175_maxS =
        "\1\113\1\170\1\uffff\1\110\7\uffff";
    static final String DFA175_acceptS =
        "\2\uffff\1\3\1\uffff\1\1\1\2\1\4\1\6\1\7\1\10\1\5";
    static final String DFA175_specialS =
        "\13\uffff}>";
    static final String[] DFA175_transitionS = {
            "\1\2\1\uffff\1\1\4\uffff\1\3",
            "\1\5\1\uffff\1\5\2\uffff\1\5\4\uffff\1\5\2\uffff\1\5\2\uffff"+
            "\1\5\5\uffff\1\5\1\uffff\1\5\1\uffff\2\5\5\uffff\1\5\2\uffff"+
            "\1\5\2\uffff\1\5\3\uffff\1\5\2\uffff\1\5\6\uffff\1\5\2\uffff"+
            "\1\4\5\uffff\1\5\2\uffff\2\5\1\uffff\2\5\11\uffff\1\5\12\uffff"+
            "\3\5\2\uffff\1\5\1\uffff\2\5\6\uffff\1\5",
            "",
            "\1\6\23\uffff\1\11\11\uffff\1\10\2\uffff\1\7\21\uffff\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA175_eot = DFA.unpackEncodedString(DFA175_eotS);
    static final short[] DFA175_eof = DFA.unpackEncodedString(DFA175_eofS);
    static final char[] DFA175_min = DFA.unpackEncodedStringToUnsignedChars(DFA175_minS);
    static final char[] DFA175_max = DFA.unpackEncodedStringToUnsignedChars(DFA175_maxS);
    static final short[] DFA175_accept = DFA.unpackEncodedString(DFA175_acceptS);
    static final short[] DFA175_special = DFA.unpackEncodedString(DFA175_specialS);
    static final short[][] DFA175_transition;

    static {
        int numStates = DFA175_transitionS.length;
        DFA175_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA175_transition[i] = DFA.unpackEncodedString(DFA175_transitionS[i]);
        }
    }

    class DFA175 extends DFA {

        public DFA175(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 175;
            this.eot = DFA175_eot;
            this.eof = DFA175_eof;
            this.min = DFA175_min;
            this.max = DFA175_max;
            this.accept = DFA175_accept;
            this.special = DFA175_special;
            this.transition = DFA175_transition;
        }
        public String getDescription() {
            return "1145:1: identifierSuffix : ( ( LEFTSQUAREBRACKET RIGHTSQUAREBRACKET )+ DOT CLASS | ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )+ | arguments | DOT CLASS | DOT explicitGenericInvocation | DOT THIS | DOT SUPER arguments | DOT NEW innerCreator );";
        }
    }
    static final String DFA174_eotS =
        "\41\uffff";
    static final String DFA174_eofS =
        "\1\1\40\uffff";
    static final String DFA174_minS =
        "\1\44\1\uffff\1\0\36\uffff";
    static final String DFA174_maxS =
        "\1\146\1\uffff\1\0\36\uffff";
    static final String DFA174_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA174_specialS =
        "\2\uffff\1\0\36\uffff}>";
    static final String[] DFA174_transitionS = {
            "\1\1\32\uffff\3\1\1\uffff\1\1\1\uffff\1\1\1\2\3\1\1\uffff\1"+
            "\1\2\uffff\20\1\1\uffff\10\1",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA174_eot = DFA.unpackEncodedString(DFA174_eotS);
    static final short[] DFA174_eof = DFA.unpackEncodedString(DFA174_eofS);
    static final char[] DFA174_min = DFA.unpackEncodedStringToUnsignedChars(DFA174_minS);
    static final char[] DFA174_max = DFA.unpackEncodedStringToUnsignedChars(DFA174_maxS);
    static final short[] DFA174_accept = DFA.unpackEncodedString(DFA174_acceptS);
    static final short[] DFA174_special = DFA.unpackEncodedString(DFA174_specialS);
    static final short[][] DFA174_transition;

    static {
        int numStates = DFA174_transitionS.length;
        DFA174_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA174_transition[i] = DFA.unpackEncodedString(DFA174_transitionS[i]);
        }
    }

    class DFA174 extends DFA {

        public DFA174(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 174;
            this.eot = DFA174_eot;
            this.eof = DFA174_eof;
            this.min = DFA174_min;
            this.max = DFA174_max;
            this.accept = DFA174_accept;
            this.special = DFA174_special;
            this.transition = DFA174_transition;
        }
        public String getDescription() {
            return "()+ loopback of 1147:9: ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA174_2 = input.LA(1);

                         
                        int index174_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred275_JWIPreprocessor_Parser()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index174_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 174, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA181_eotS =
        "\41\uffff";
    static final String DFA181_eofS =
        "\1\2\40\uffff";
    static final String DFA181_minS =
        "\1\44\1\0\37\uffff";
    static final String DFA181_maxS =
        "\1\146\1\0\37\uffff";
    static final String DFA181_acceptS =
        "\2\uffff\1\2\35\uffff\1\1";
    static final String DFA181_specialS =
        "\1\uffff\1\0\37\uffff}>";
    static final String[] DFA181_transitionS = {
            "\1\2\32\uffff\3\2\1\uffff\1\2\1\uffff\1\2\1\1\3\2\1\uffff\1"+
            "\2\2\uffff\20\2\1\uffff\10\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA181_eot = DFA.unpackEncodedString(DFA181_eotS);
    static final short[] DFA181_eof = DFA.unpackEncodedString(DFA181_eofS);
    static final char[] DFA181_min = DFA.unpackEncodedStringToUnsignedChars(DFA181_minS);
    static final char[] DFA181_max = DFA.unpackEncodedStringToUnsignedChars(DFA181_maxS);
    static final short[] DFA181_accept = DFA.unpackEncodedString(DFA181_acceptS);
    static final short[] DFA181_special = DFA.unpackEncodedString(DFA181_specialS);
    static final short[][] DFA181_transition;

    static {
        int numStates = DFA181_transitionS.length;
        DFA181_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA181_transition[i] = DFA.unpackEncodedString(DFA181_transitionS[i]);
        }
    }

    class DFA181 extends DFA {

        public DFA181(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 181;
            this.eot = DFA181_eot;
            this.eof = DFA181_eof;
            this.min = DFA181_min;
            this.max = DFA181_max;
            this.accept = DFA181_accept;
            this.special = DFA181_special;
            this.transition = DFA181_transition;
        }
        public String getDescription() {
            return "()* loopback of 1173:43: ( LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA181_1 = input.LA(1);

                         
                        int index181_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred288_JWIPreprocessor_Parser()) ) {s = 32;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index181_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 181, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_compilationUnit_in_jwiCompilationUnit156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_compilationUnit190 = new BitSet(new long[]{0x0006784020204000L,0x0010000000001000L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit206 = new BitSet(new long[]{0x8006784820204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit210 = new BitSet(new long[]{0x8006784820204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit213 = new BitSet(new long[]{0x8006784020204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_compilationUnit228 = new BitSet(new long[]{0x8006784020204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit230 = new BitSet(new long[]{0x8006784020204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_packageDeclaration_in_compilationUnit254 = new BitSet(new long[]{0x8006784820204012L,0x0010000000001000L});
    public static final BitSet FOLLOW_importDeclaration_in_compilationUnit261 = new BitSet(new long[]{0x8006784820204012L,0x0010000000001000L});
    public static final BitSet FOLLOW_standaloneIntentionDeclaration_in_compilationUnit265 = new BitSet(new long[]{0x8006784020204012L,0x0010000000001000L});
    public static final BitSet FOLLOW_typeDeclaration_in_compilationUnit269 = new BitSet(new long[]{0x8006784020204012L,0x0010000000001000L});
    public static final BitSet FOLLOW_PACKAGE_in_packageDeclaration294 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_packageDeclaration298 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_packageDeclaration300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_importDeclaration333 = new BitSet(new long[]{0x0002000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_STATIC_in_importDeclaration335 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_importDeclaration338 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_importDeclaration341 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ASTERISK_in_importDeclaration343 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_importDeclaration347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABSTRACT_in_standaloneIntentionDeclaration375 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_INTENTION_in_standaloneIntentionDeclaration379 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_standaloneIntentionDeclaration383 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_intentionExtendsClause_in_standaloneIntentionDeclaration403 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_intentionDeclarationBody_in_standaloneIntentionDeclaration415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABSTRACT_in_standaloneRequirementDeclaration458 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_REQUIREMENT_in_standaloneRequirementDeclaration462 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_standaloneRequirementDeclaration466 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_intentionExtendsClause_in_standaloneRequirementDeclaration486 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_intentionDeclarationBody_in_standaloneRequirementDeclaration498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABSTRACT_in_standaloneGoalDeclaration541 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_GOAL_in_standaloneGoalDeclaration545 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_standaloneGoalDeclaration549 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_intentionExtendsClause_in_standaloneGoalDeclaration569 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_intentionDeclarationBody_in_standaloneGoalDeclaration581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_intentionDeclarationBody616 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_DESCRIPTION_in_intentionDeclarationBody626 = new BitSet(new long[]{0x0000000000000000L,0x0200000000000000L});
    public static final BitSet FOLLOW_FREETEXTINBRACES_in_intentionDeclarationBody628 = new BitSet(new long[]{0x0000000000003800L,0x0000000000000008L});
    public static final BitSet FOLLOW_INTERFACEREFERENCE_in_intentionDeclarationBody652 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody654 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_intentionDeclarationBody656 = new BitSet(new long[]{0x0000000000003800L,0x0000000000000008L});
    public static final BitSet FOLLOW_INTERFACEREFERENCE_in_intentionDeclarationBody684 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_intentionDeclarationBody686 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_intentionDeclarationBody688 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody690 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_intentionDeclarationBody693 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_LEFTBRACE_in_intentionDeclarationBody695 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000008L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody698 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_COMMA_in_intentionDeclarationBody701 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody703 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_RIGHTBRACE_in_intentionDeclarationBody709 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_intentionDeclarationBody712 = new BitSet(new long[]{0x0000000000003800L,0x0000000000000008L});
    public static final BitSet FOLLOW_CLASSREFERENCE_in_intentionDeclarationBody740 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody742 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_intentionDeclarationBody744 = new BitSet(new long[]{0x0000000000003800L,0x0000000000000008L});
    public static final BitSet FOLLOW_CLASSREFERENCE_in_intentionDeclarationBody772 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_intentionDeclarationBody774 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_intentionDeclarationBody776 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody778 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_intentionDeclarationBody781 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_LEFTBRACE_in_intentionDeclarationBody783 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000008L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody786 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_COMMA_in_intentionDeclarationBody789 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody791 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_RIGHTBRACE_in_intentionDeclarationBody797 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_intentionDeclarationBody800 = new BitSet(new long[]{0x0000000000003800L,0x0000000000000008L});
    public static final BitSet FOLLOW_TEXTFIELD_in_intentionDeclarationBody828 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_intentionDeclarationBody830 = new BitSet(new long[]{0x8000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_intentionDeclarationBody833 = new BitSet(new long[]{0x0000000000000000L,0x0002000000000000L});
    public static final BitSet FOLLOW_StringLiteral_in_intentionDeclarationBody835 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_intentionDeclarationBody839 = new BitSet(new long[]{0x0000000000003800L,0x0000000000000008L});
    public static final BitSet FOLLOW_RIGHTBRACE_in_intentionDeclarationBody860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXTENDS_in_intentionExtendsClause887 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_intentionExtendsClause891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLEMENTSINTENTION_in_classImplementsIntentionClause917 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_classImplementsIntentionClause921 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_classImplementsIntentionClause942 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_classImplementsIntentionClause946 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLEMENTSINTENTION_in_methodImplementsIntentionClause1009 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_methodImplementsIntentionClause1013 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_methodImplementsIntentionClause1024 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_methodImplementsIntentionClause1028 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_typeDeclaration1064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_typeDeclaration1074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifiers_in_classOrInterfaceDeclaration1097 = new BitSet(new long[]{0x0006784020204000L,0x0010000000001000L});
    public static final BitSet FOLLOW_classDeclaration_in_classOrInterfaceDeclaration1110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_classOrInterfaceDeclaration1134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceModifier_in_classOrInterfaceModifiers1171 = new BitSet(new long[]{0x0006700020004002L,0x0000000000001000L});
    public static final BitSet FOLLOW_annotation_in_classOrInterfaceModifier1191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PUBLIC_in_classOrInterfaceModifier1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROTECTED_in_classOrInterfaceModifier1221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PRIVATE_in_classOrInterfaceModifier1235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABSTRACT_in_classOrInterfaceModifier1251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STATIC_in_classOrInterfaceModifier1266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINAL_in_classOrInterfaceModifier1283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRICTFP_in_classOrInterfaceModifier1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifier_in_modifiers1325 = new BitSet(new long[]{0x0226710020004002L,0x8000000000001000L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_classDeclaration1351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_classDeclaration1373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_in_normalClassDeclaration1418 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_normalClassDeclaration1422 = new BitSet(new long[]{0x0000000408000020L,0x0000000000000100L});
    public static final BitSet FOLLOW_typeParameters_in_normalClassDeclaration1424 = new BitSet(new long[]{0x0000000408000020L,0x0000000000000100L});
    public static final BitSet FOLLOW_EXTENDS_in_normalClassDeclaration1436 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_normalClassDeclaration1438 = new BitSet(new long[]{0x0000000408000020L,0x0000000000000100L});
    public static final BitSet FOLLOW_IMPLEMENTS_in_normalClassDeclaration1451 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_typeList_in_normalClassDeclaration1453 = new BitSet(new long[]{0x0000000408000020L,0x0000000000000100L});
    public static final BitSet FOLLOW_classImplementsIntentionClause_in_normalClassDeclaration1475 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_classBody_in_normalClassDeclaration1486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_typeParameters1509 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters1511 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000202L});
    public static final BitSet FOLLOW_COMMA_in_typeParameters1514 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_typeParameter_in_typeParameters1516 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000202L});
    public static final BitSet FOLLOW_GREATERTHAN_in_typeParameters1520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_typeParameter1539 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_EXTENDS_in_typeParameter1542 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_typeBound_in_typeParameter1544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeBound1573 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_BITWISE_AND_in_typeBound1576 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_typeBound1578 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_ENUM_in_enumDeclaration1603 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_enumDeclaration1607 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_IMPLEMENTS_in_enumDeclaration1610 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_typeList_in_enumDeclaration1612 = new BitSet(new long[]{0x0000000400000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_enumBody_in_enumDeclaration1634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_enumBody1653 = new BitSet(new long[]{0x8000000000000000L,0x010000000000100AL});
    public static final BitSet FOLLOW_enumConstants_in_enumBody1655 = new BitSet(new long[]{0x8000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_COMMA_in_enumBody1658 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_enumBodyDeclarations_in_enumBody1661 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RIGHTBRACE_in_enumBody1664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants1683 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_enumConstants1686 = new BitSet(new long[]{0x0000000000000000L,0x0100000000001000L});
    public static final BitSet FOLLOW_enumConstant_in_enumConstants1688 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_enumConstant1713 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_enumConstant1716 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000014L});
    public static final BitSet FOLLOW_arguments_in_enumConstant1718 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_classBody_in_enumConstant1721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_enumBodyDeclarations1745 = new BitSet(new long[]{0x8226710020004002L,0x8000000000001004L});
    public static final BitSet FOLLOW_classBodyDeclaration_in_enumBodyDeclarations1748 = new BitSet(new long[]{0x8226710020004002L,0x8000000000001004L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_interfaceDeclaration1783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_interfaceDeclaration1805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTERFACE_in_normalInterfaceDeclaration1838 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_normalInterfaceDeclaration1842 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000104L});
    public static final BitSet FOLLOW_typeParameters_in_normalInterfaceDeclaration1844 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000104L});
    public static final BitSet FOLLOW_EXTENDS_in_normalInterfaceDeclaration1848 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_typeList_in_normalInterfaceDeclaration1850 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000104L});
    public static final BitSet FOLLOW_interfaceBody_in_normalInterfaceDeclaration1876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList1899 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList1902 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_typeList1904 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_classBody1929 = new BitSet(new long[]{0x8226710020004000L,0x800000000000100CL});
    public static final BitSet FOLLOW_classBodyDeclaration_in_classBody1931 = new BitSet(new long[]{0x8226710020004000L,0x800000000000100CL});
    public static final BitSet FOLLOW_RIGHTBRACE_in_classBody1934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_interfaceBody1957 = new BitSet(new long[]{0x8226710020004000L,0x800000000000100CL});
    public static final BitSet FOLLOW_interfaceBodyDeclaration_in_interfaceBody1959 = new BitSet(new long[]{0x8226710020004000L,0x800000000000100CL});
    public static final BitSet FOLLOW_RIGHTBRACE_in_interfaceBody1962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_classBodyDeclaration1981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STATIC_in_classBodyDeclaration1991 = new BitSet(new long[]{0x0002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_classBodyDeclaration1994 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_classBodyDeclaration2004 = new BitSet(new long[]{0x200778E0A232C000L,0x0110000000001100L});
    public static final BitSet FOLLOW_memberDecl_in_classBodyDeclaration2006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_genericMethodOrConstructorDecl_in_memberDecl2029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_memberDeclaration_in_memberDecl2039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_memberDecl2049 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl2051 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_voidMethodDeclaratorRest_in_memberDecl2053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_memberDecl2063 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_memberDecl2065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_memberDecl2075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_memberDecl2085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_memberDeclaration2108 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_methodDeclaration_in_memberDeclaration2111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fieldDeclaration_in_memberDeclaration2115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_genericMethodOrConstructorDecl2135 = new BitSet(new long[]{0x200100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_genericMethodOrConstructorRest_in_genericMethodOrConstructorDecl2137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_genericMethodOrConstructorRest2161 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_VOID_in_genericMethodOrConstructorRest2165 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest2168 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_genericMethodOrConstructorRest2170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_genericMethodOrConstructorRest2180 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_constructorDeclaratorRest_in_genericMethodOrConstructorRest2182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_methodDeclaration2201 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_methodDeclaratorRest_in_methodDeclaration2203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_fieldDeclaration2222 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_fieldDeclaration2224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_interfaceBodyDeclaration2251 = new BitSet(new long[]{0x200778E0A232C000L,0x0110000000001100L});
    public static final BitSet FOLLOW_interfaceMemberDecl_in_interfaceBodyDeclaration2253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_interfaceBodyDeclaration2263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldDecl_in_interfaceMemberDecl2282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceGenericMethodDecl_in_interfaceMemberDecl2292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_interfaceMemberDecl2302 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMemberDecl2304 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_voidInterfaceMethodDeclaratorRest_in_interfaceMemberDecl2306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceDeclaration_in_interfaceMemberDecl2316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDeclaration_in_interfaceMemberDecl2326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_interfaceMethodOrFieldDecl2349 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_interfaceMethodOrFieldDecl2351 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000050L});
    public static final BitSet FOLLOW_interfaceMethodOrFieldRest_in_interfaceMethodOrFieldDecl2353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorsRest_in_interfaceMethodOrFieldRest2376 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_interfaceMethodOrFieldRest2378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceMethodOrFieldRest2388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_methodDeclaratorRest2411 = new BitSet(new long[]{0x8102000000000020L,0x0000000000000044L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_methodDeclaratorRest2414 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_methodDeclaratorRest2416 = new BitSet(new long[]{0x8102000000000020L,0x0000000000000044L});
    public static final BitSet FOLLOW_methodImplementsIntentionClause_in_methodDeclaratorRest2428 = new BitSet(new long[]{0x8102000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_THROWS_in_methodDeclaratorRest2440 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_methodDeclaratorRest2442 = new BitSet(new long[]{0x8002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodBody_in_methodDeclaratorRest2458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_methodDeclaratorRest2472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidMethodDeclaratorRest2505 = new BitSet(new long[]{0x8102000000000020L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodImplementsIntentionClause_in_voidMethodDeclaratorRest2515 = new BitSet(new long[]{0x8102000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_THROWS_in_voidMethodDeclaratorRest2527 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidMethodDeclaratorRest2529 = new BitSet(new long[]{0x8002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodBody_in_voidMethodDeclaratorRest2545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_voidMethodDeclaratorRest2559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_interfaceMethodDeclaratorRest2592 = new BitSet(new long[]{0x8100000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_interfaceMethodDeclaratorRest2595 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_interfaceMethodDeclaratorRest2597 = new BitSet(new long[]{0x8100000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_THROWS_in_interfaceMethodDeclaratorRest2602 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_interfaceMethodDeclaratorRest2604 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_interfaceMethodDeclaratorRest2608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeParameters_in_interfaceGenericMethodDecl2631 = new BitSet(new long[]{0x200100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_interfaceGenericMethodDecl2634 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_VOID_in_interfaceGenericMethodDecl2638 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_interfaceGenericMethodDecl2641 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000050L});
    public static final BitSet FOLLOW_interfaceMethodDeclaratorRest_in_interfaceGenericMethodDecl2651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_voidInterfaceMethodDeclaratorRest2674 = new BitSet(new long[]{0x8100000000000020L});
    public static final BitSet FOLLOW_methodImplementsIntentionClause_in_voidInterfaceMethodDeclaratorRest2684 = new BitSet(new long[]{0x8100000000000000L});
    public static final BitSet FOLLOW_THROWS_in_voidInterfaceMethodDeclaratorRest2696 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_voidInterfaceMethodDeclaratorRest2698 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_voidInterfaceMethodDeclaratorRest2702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formalParameters_in_constructorDeclaratorRest2725 = new BitSet(new long[]{0x0100000000000020L,0x0000000000000004L});
    public static final BitSet FOLLOW_methodImplementsIntentionClause_in_constructorDeclaratorRest2735 = new BitSet(new long[]{0x0100000000000020L,0x0000000000000004L});
    public static final BitSet FOLLOW_THROWS_in_constructorDeclaratorRest2747 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_qualifiedNameList_in_constructorDeclaratorRest2749 = new BitSet(new long[]{0x0100000000000020L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorBody_in_constructorDeclaratorRest2753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_constantDeclarator2772 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000040L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclarator2774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators2797 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_variableDeclarators2800 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_variableDeclarator_in_variableDeclarators2802 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_variableDeclarator2823 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_variableDeclarator2826 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_variableInitializer_in_variableDeclarator2828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constantDeclaratorRest_in_constantDeclaratorsRest2853 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_constantDeclaratorsRest2856 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_constantDeclarator_in_constantDeclaratorsRest2858 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_constantDeclaratorRest2880 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_constantDeclaratorRest2882 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000040L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_constantDeclaratorRest2886 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_variableInitializer_in_constantDeclaratorRest2888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_variableDeclaratorId2911 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_variableDeclaratorId2914 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_variableDeclaratorId2916 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer2937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer2947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_arrayInitializer2974 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B201CL});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2977 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer2980 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2982 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer2987 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RIGHTBRACE_in_arrayInitializer2994 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_modifier3013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PUBLIC_in_modifier3023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PROTECTED_in_modifier3033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PRIVATE_in_modifier3043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STATIC_in_modifier3053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABSTRACT_in_modifier3063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINAL_in_modifier3073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NATIVE_in_modifier3083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYNCHRONIZED_in_modifier3093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRANSIENT_in_modifier3103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOLATIVE_in_modifier3113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRICTFP_in_modifier3123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_packageOrTypeName3142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_enumConstantName3161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_typeName3180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_type3194 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_type3197 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_type3199 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_primitiveType_in_type3206 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_type3209 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_type3211 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType3224 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000900L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType3226 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_classOrInterfaceType3230 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_classOrInterfaceType3232 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000900L});
    public static final BitSet FOLLOW_typeArguments_in_classOrInterfaceType3234 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_set_in_primitiveType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINAL_in_variableModifier3343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_variableModifier3353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_typeArguments3372 = new BitSet(new long[]{0x000100A082128000L,0x0100000000004000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments3374 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000202L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments3377 = new BitSet(new long[]{0x000100A082128000L,0x0100000000004000L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments3379 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000202L});
    public static final BitSet FOLLOW_GREATERTHAN_in_typeArguments3383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument3406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTIONMARK_in_typeArgument3416 = new BitSet(new long[]{0x0008000008000002L});
    public static final BitSet FOLLOW_set_in_typeArgument3419 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_typeArgument3427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3452 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_qualifiedNameList3455 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_qualifiedName_in_qualifiedNameList3457 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_formalParameters3478 = new BitSet(new long[]{0x000100A0A2128000L,0x0100000000001020L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameters3480 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_formalParameters3483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameterDecls3506 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_formalParameterDecls3508 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000400L});
    public static final BitSet FOLLOW_formalParameterDeclsRest_in_formalParameterDecls3510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3533 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_formalParameterDeclsRest3536 = new BitSet(new long[]{0x000100A0A2128000L,0x0100000000001000L});
    public static final BitSet FOLLOW_formalParameterDecls_in_formalParameterDeclsRest3538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELLIPSIS_in_formalParameterDeclsRest3550 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameterDeclsRest3552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_methodBody3575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_constructorBody3594 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B311CL});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_constructorBody3596 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B301CL});
    public static final BitSet FOLLOW_blockStatement_in_constructorBody3599 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B301CL});
    public static final BitSet FOLLOW_RIGHTBRACE_in_constructorBody3602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3621 = new BitSet(new long[]{0x0048000000000000L});
    public static final BitSet FOLLOW_set_in_explicitConstructorInvocation3624 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3632 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_explicitConstructorInvocation3634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_explicitConstructorInvocation3644 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_explicitConstructorInvocation3646 = new BitSet(new long[]{0x0008000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitConstructorInvocation3648 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_SUPER_in_explicitConstructorInvocation3651 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_arguments_in_explicitConstructorInvocation3653 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_explicitConstructorInvocation3655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName3675 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_qualifiedName3678 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_qualifiedName3680 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_integerLiteral_in_literal3706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FloatingPointLiteral_in_literal3716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CharacterLiteral_in_literal3726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_StringLiteral_in_literal3736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_booleanLiteral_in_literal3746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal3756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_integerLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_annotations3845 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_ATSIGN_in_annotation3865 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_annotationName_in_annotation3867 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_annotation3871 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B3034L});
    public static final BitSet FOLLOW_elementValuePairs_in_annotation3875 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_elementValue_in_annotation3879 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_annotation3884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationName3908 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_annotationName3911 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_annotationName3913 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3934 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_elementValuePairs3937 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_elementValuePair_in_elementValuePairs3939 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_elementValuePair3960 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_elementValuePair3962 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B3014L});
    public static final BitSet FOLLOW_elementValue_in_elementValuePair3964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_elementValue3987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_elementValue3997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementValueArrayInitializer_in_elementValue4007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_elementValueArrayInitializer4030 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B301EL});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer4033 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer4036 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B3014L});
    public static final BitSet FOLLOW_elementValue_in_elementValueArrayInitializer4038 = new BitSet(new long[]{0x0000000000000000L,0x000000000000000AL});
    public static final BitSet FOLLOW_COMMA_in_elementValueArrayInitializer4045 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RIGHTBRACE_in_elementValueArrayInitializer4049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATSIGN_in_annotationTypeDeclaration4076 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_INTERFACE_in_annotationTypeDeclaration4078 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_annotationTypeDeclaration4082 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_annotationTypeBody_in_annotationTypeDeclaration4105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_annotationTypeBody4124 = new BitSet(new long[]{0x8226710020004000L,0x800000000000100CL});
    public static final BitSet FOLLOW_annotationTypeElementDeclaration_in_annotationTypeBody4127 = new BitSet(new long[]{0x8226710020004000L,0x800000000000100CL});
    public static final BitSet FOLLOW_RIGHTBRACE_in_annotationTypeBody4131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_modifiers_in_annotationTypeElementDeclaration4154 = new BitSet(new long[]{0x000778E0A232C000L,0x0110000000001000L});
    public static final BitSet FOLLOW_annotationTypeElementRest_in_annotationTypeElementDeclaration4156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_annotationTypeElementRest4179 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_annotationMethodOrConstantRest_in_annotationTypeElementRest4181 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_annotationTypeElementRest4183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalClassDeclaration_in_annotationTypeElementRest4193 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_annotationTypeElementRest4195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normalInterfaceDeclaration_in_annotationTypeElementRest4206 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_annotationTypeElementRest4208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enumDeclaration_in_annotationTypeElementRest4219 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_annotationTypeElementRest4221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationTypeDeclaration_in_annotationTypeElementRest4232 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_annotationTypeElementRest4234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationMethodRest_in_annotationMethodOrConstantRest4258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationConstantRest_in_annotationMethodOrConstantRest4268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_annotationMethodRest4291 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_annotationMethodRest4293 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_annotationMethodRest4295 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_defaultValue_in_annotationMethodRest4297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableDeclarators_in_annotationConstantRest4321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_in_defaultValue4344 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B3014L});
    public static final BitSet FOLLOW_elementValue_in_defaultValue4346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTBRACE_in_block4367 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x11334E00401B301CL});
    public static final BitSet FOLLOW_inlineIntentionBlock_in_block4370 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x11334E00401B301CL});
    public static final BitSet FOLLOW_blockStatement_in_block4374 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x11334E00401B301CL});
    public static final BitSet FOLLOW_RIGHTBRACE_in_block4378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineIntentionCommentOpeningTag_in_inlineIntentionBlock4397 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x19334E00401B3014L});
    public static final BitSet FOLLOW_inlineIntentionBlock_in_inlineIntentionBlock4408 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x19334E00401B3014L});
    public static final BitSet FOLLOW_blockStatement_in_inlineIntentionBlock4412 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x19334E00401B3014L});
    public static final BitSet FOLLOW_inlineIntentionCommentClosingTag_in_inlineIntentionBlock4424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INLINEINTENTIONOPENINGTAGTOKEN_in_inlineIntentionCommentOpeningTag4447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INLINEINTENTIONCLOSINGTAGTOKEN_in_inlineIntentionCommentClosingTag4496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_blockStatement4547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_blockStatement4557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_blockStatement4567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_localVariableDeclarationStatement4591 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_localVariableDeclarationStatement4593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_localVariableDeclaration4612 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_localVariableDeclaration4614 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_variableDeclarators_in_localVariableDeclaration4616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifier_in_variableModifiers4639 = new BitSet(new long[]{0x0000000020000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_block_in_statement4659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSERT_in_statement4669 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_statement4671 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_statement4674 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_statement4676 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_statement4680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_statement4690 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_parExpression_in_statement4692 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_statement_in_statement4694 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_ELSE_in_statement4704 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_statement_in_statement4706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_statement4718 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_statement4720 = new BitSet(new long[]{0xA44906A0B2128000L,0x01034E00401B3014L});
    public static final BitSet FOLLOW_forControl_in_statement4722 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_statement4724 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_statement_in_statement4726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_statement4736 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_parExpression_in_statement4738 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_statement_in_statement4740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DO_in_statement4750 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_statement_in_statement4752 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_WHILE_in_statement4754 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_parExpression_in_statement4756 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_statement4758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRY_in_statement4768 = new BitSet(new long[]{0x0002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement4770 = new BitSet(new long[]{0x0000000040080000L});
    public static final BitSet FOLLOW_catches_in_statement4782 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_FINALLY_in_statement4784 = new BitSet(new long[]{0x0002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement4786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_statement4798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_statement4812 = new BitSet(new long[]{0x0002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement4814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SWITCH_in_statement4834 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_parExpression_in_statement4836 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_LEFTBRACE_in_statement4838 = new BitSet(new long[]{0x0000000000840000L,0x0000000000000008L});
    public static final BitSet FOLLOW_switchBlockStatementGroups_in_statement4840 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RIGHTBRACE_in_statement4842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYNCHRONIZED_in_statement4852 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_parExpression_in_statement4854 = new BitSet(new long[]{0x0002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_statement4856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_in_statement4866 = new BitSet(new long[]{0xA44906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_statement4868 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_statement4871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROW_in_statement4881 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_statement4883 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_statement4885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_statement4895 = new BitSet(new long[]{0x8000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_statement4897 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_statement4900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTINUE_in_statement4910 = new BitSet(new long[]{0x8000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_statement4912 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_statement4915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_statement4925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statementExpression_in_statement4936 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_statement4938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_statement4948 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_statement4950 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_statement_in_statement4952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catchClause_in_catches4975 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_catchClause_in_catches4978 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_CATCH_in_catchClause5003 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_catchClause5005 = new BitSet(new long[]{0x000100A0A2128000L,0x0100000000001000L});
    public static final BitSet FOLLOW_formalParameter_in_catchClause5007 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_catchClause5009 = new BitSet(new long[]{0x0002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_catchClause5011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_formalParameter5030 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_formalParameter5032 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_variableDeclaratorId_in_formalParameter5034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchBlockStatementGroup_in_switchBlockStatementGroups5062 = new BitSet(new long[]{0x0000000000840002L});
    public static final BitSet FOLLOW_switchLabel_in_switchBlockStatementGroup5089 = new BitSet(new long[]{0xBCFFFEE3B3F7C002L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_blockStatement_in_switchBlockStatementGroup5092 = new BitSet(new long[]{0xBCFFFEE3B373C002L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_CASE_in_switchLabel5116 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_constantExpression_in_switchLabel5118 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_switchLabel5120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_switchLabel5130 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_enumConstantName_in_switchLabel5132 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_switchLabel5134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DEFAULT_in_switchLabel5144 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_switchLabel5146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_forControl5177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_forInit_in_forControl5187 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_forControl5190 = new BitSet(new long[]{0xA44906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_forControl5192 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_forControl5195 = new BitSet(new long[]{0x244906A0B2128002L,0x01034E00401B3014L});
    public static final BitSet FOLLOW_forUpdate_in_forControl5197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_forInit5217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forInit5227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableModifiers_in_enhancedForControl5250 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_enhancedForControl5252 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_enhancedForControl5254 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_enhancedForControl5256 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_enhancedForControl5258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionList_in_forUpdate5277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_parExpression5298 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_parExpression5300 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_parExpression5302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList5325 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList5328 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_expressionList5330 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_statementExpression5351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_constantExpression5374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression5397 = new BitSet(new long[]{0x0000000000000002L,0x000000250AA48300L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression5400 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_expression5402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_assignmentOperator5427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSEQUALS_in_assignmentOperator5437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUSEQUALS_in_assignmentOperator5447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISKEQUALS_in_assignmentOperator5457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SLASHEQUALS_in_assignmentOperator5467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISE_AND_EQUALS_in_assignmentOperator5477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISE_OR_EQUALS_in_assignmentOperator5487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARETEQUALS_in_assignmentOperator5497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PERCENTEQUALS_in_assignmentOperator5507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_assignmentOperator5528 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LESSTHAN_in_assignmentOperator5532 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_assignmentOperator5536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_assignmentOperator5570 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_assignmentOperator5574 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_assignmentOperator5578 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_assignmentOperator5582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_assignmentOperator5613 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_assignmentOperator5617 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_assignmentOperator5621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression5650 = new BitSet(new long[]{0x0000000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_QUESTIONMARK_in_conditionalExpression5654 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5656 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression5658 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression5660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5682 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_LOGICAL_OR_in_conditionalOrExpression5686 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression5688 = new BitSet(new long[]{0x0000000000000002L,0x0000001000000000L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5710 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_LOGICAL_AND_in_conditionalAndExpression5714 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression5716 = new BitSet(new long[]{0x0000000000000002L,0x0000000200000000L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5738 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression5742 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression5746 = new BitSet(new long[]{0x0000000000000002L,0x0000004000000000L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5768 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_CARET_in_exclusiveOrExpression5772 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression5774 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5796 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_BITWISE_AND_in_andExpression5800 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression5802 = new BitSet(new long[]{0x0000000000000002L,0x0000000800000000L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5824 = new BitSet(new long[]{0x0000000000000002L,0x00000000A0000000L});
    public static final BitSet FOLLOW_set_in_equalityExpression5828 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression5836 = new BitSet(new long[]{0x0000000000000002L,0x00000000A0000000L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression5858 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_INSTANCEOF_in_instanceOfExpression5861 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression5863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5884 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000300L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression5888 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression5890 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000300L});
    public static final BitSet FOLLOW_LESSTHAN_in_relationalOp5925 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_relationalOp5929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_relationalOp5959 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_relationalOp5963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_relationalOp5984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_relationalOp5995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6015 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000300L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression6019 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression6021 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000300L});
    public static final BitSet FOLLOW_LESSTHAN_in_shiftOp6052 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LESSTHAN_in_shiftOp6056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_shiftOp6088 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_shiftOp6092 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_shiftOp6096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_shiftOp6126 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_shiftOp6130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6160 = new BitSet(new long[]{0x0000000000000002L,0x0000000000120000L});
    public static final BitSet FOLLOW_set_in_additiveExpression6164 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression6172 = new BitSet(new long[]{0x0000000000000002L,0x0000000000120000L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6194 = new BitSet(new long[]{0x0000000000000002L,0x0000000005400000L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression6198 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression6212 = new BitSet(new long[]{0x0000000000000002L,0x0000000005400000L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression6238 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression6250 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUSPLUS_in_unaryExpression6262 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUSMINUS_in_unaryExpression6274 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression6276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression6286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus6305 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCLAMATIONMARK_in_unaryExpressionNotPlusMinus6317 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus6319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus6329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus6339 = new BitSet(new long[]{0x0000000000000002L,0x0000000000090840L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus6341 = new BitSet(new long[]{0x0000000000000002L,0x0000000000090840L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus6344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_castExpression6367 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression6369 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_castExpression6371 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression6373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_castExpression6382 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_type_in_castExpression6385 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_castExpression6389 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_castExpression6392 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression6394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary6413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THIS_in_primary6423 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000850L});
    public static final BitSet FOLLOW_DOT_in_primary6426 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_primary6428 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000850L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_in_primary6443 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000810L});
    public static final BitSet FOLLOW_superSuffix_in_primary6445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary6455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_primary6465 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000100L});
    public static final BitSet FOLLOW_creator_in_primary6467 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Identifier_in_primary6477 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000850L});
    public static final BitSet FOLLOW_DOT_in_primary6480 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_primary6482 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000850L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary6486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary6497 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000840L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_primary6500 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_primary6502 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000840L});
    public static final BitSet FOLLOW_DOT_in_primary6506 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_CLASS_in_primary6508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VOID_in_primary6518 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_primary6520 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_CLASS_in_primary6522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_identifierSuffix6542 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_identifierSuffix6544 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000840L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6548 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix6550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_identifierSuffix6561 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix6563 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_identifierSuffix6565 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix6578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6588 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_CLASS_in_identifierSuffix6590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6600 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_explicitGenericInvocation_in_identifierSuffix6602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6612 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_THIS_in_identifierSuffix6614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6624 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_SUPER_in_identifierSuffix6626 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix6628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix6638 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_NEW_in_identifierSuffix6640 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000100L});
    public static final BitSet FOLLOW_innerCreator_in_identifierSuffix6642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator6661 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000100L});
    public static final BitSet FOLLOW_createdName_in_creator6663 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator6665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_createdName_in_creator6675 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator6678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator6682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceType_in_createdName6702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName6712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_innerCreator6735 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_innerCreator6738 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator6740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_arrayCreatorRest6759 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2094L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6773 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000044L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_arrayCreatorRest6776 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6778 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000044L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest6782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest6796 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6798 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_arrayCreatorRest6801 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest6803 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6805 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_arrayCreatorRest6810 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_arrayCreatorRest6812 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest6843 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_classBody_in_classCreatorRest6845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation6869 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_explicitGenericInvocation6871 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation6873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_nonWildcardTypeArguments6896 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments6898 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_nonWildcardTypeArguments6900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector6923 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_selector6925 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_arguments_in_selector6927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector6938 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_THIS_in_selector6940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector6950 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_SUPER_in_selector6952 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000810L});
    public static final BitSet FOLLOW_superSuffix_in_selector6954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector6964 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_NEW_in_selector6966 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000100L});
    public static final BitSet FOLLOW_innerCreator_in_selector6968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_selector6978 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_selector6980 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_selector6982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix7005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix7015 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_superSuffix7017 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_arguments_in_superSuffix7019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_arguments7039 = new BitSet(new long[]{0x244906A0B2128000L,0x01034E00401B3034L});
    public static final BitSet FOLLOW_expressionList_in_arguments7041 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_arguments7044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotations_in_synpred5_JWIPreprocessor_Parser190 = new BitSet(new long[]{0x0006784020204000L,0x0010000000001000L});
    public static final BitSet FOLLOW_packageDeclaration_in_synpred5_JWIPreprocessor_Parser206 = new BitSet(new long[]{0x8006784820204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_importDeclaration_in_synpred5_JWIPreprocessor_Parser210 = new BitSet(new long[]{0x8006784820204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_JWIPreprocessor_Parser213 = new BitSet(new long[]{0x8006784020204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred5_JWIPreprocessor_Parser228 = new BitSet(new long[]{0x8006784020204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_typeDeclaration_in_synpred5_JWIPreprocessor_Parser230 = new BitSet(new long[]{0x8006784020204002L,0x0010000000001000L});
    public static final BitSet FOLLOW_explicitConstructorInvocation_in_synpred136_JWIPreprocessor_Parser3596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred140_JWIPreprocessor_Parser3621 = new BitSet(new long[]{0x0048000000000000L});
    public static final BitSet FOLLOW_set_in_synpred140_JWIPreprocessor_Parser3624 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_arguments_in_synpred140_JWIPreprocessor_Parser3632 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_SEMICOLON_in_synpred140_JWIPreprocessor_Parser3634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotation_in_synpred151_JWIPreprocessor_Parser3845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclarationStatement_in_synpred177_JWIPreprocessor_Parser4547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classOrInterfaceDeclaration_in_synpred178_JWIPreprocessor_Parser4557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELSE_in_synpred183_JWIPreprocessor_Parser4704 = new BitSet(new long[]{0xBCFFFEE3B373C000L,0x01334E00401B3014L});
    public static final BitSet FOLLOW_statement_in_synpred183_JWIPreprocessor_Parser4706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred188_JWIPreprocessor_Parser4782 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_FINALLY_in_synpred188_JWIPreprocessor_Parser4784 = new BitSet(new long[]{0x0002000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_synpred188_JWIPreprocessor_Parser4786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_catches_in_synpred189_JWIPreprocessor_Parser4798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_switchLabel_in_synpred204_JWIPreprocessor_Parser5089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_synpred206_JWIPreprocessor_Parser5116 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_constantExpression_in_synpred206_JWIPreprocessor_Parser5118 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_synpred206_JWIPreprocessor_Parser5120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_synpred207_JWIPreprocessor_Parser5130 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_enumConstantName_in_synpred207_JWIPreprocessor_Parser5132 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_COLON_in_synpred207_JWIPreprocessor_Parser5134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enhancedForControl_in_synpred208_JWIPreprocessor_Parser5177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_localVariableDeclaration_in_synpred212_JWIPreprocessor_Parser5217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred214_JWIPreprocessor_Parser5400 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_synpred214_JWIPreprocessor_Parser5402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_synpred224_JWIPreprocessor_Parser5518 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LESSTHAN_in_synpred224_JWIPreprocessor_Parser5520 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_synpred224_JWIPreprocessor_Parser5522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred225_JWIPreprocessor_Parser5558 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred225_JWIPreprocessor_Parser5560 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred225_JWIPreprocessor_Parser5562 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_synpred225_JWIPreprocessor_Parser5564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred226_JWIPreprocessor_Parser5603 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred226_JWIPreprocessor_Parser5605 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_synpred226_JWIPreprocessor_Parser5607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_synpred237_JWIPreprocessor_Parser5917 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_synpred237_JWIPreprocessor_Parser5919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred238_JWIPreprocessor_Parser5951 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
    public static final BitSet FOLLOW_ASSIGNMENT_EQUALS_in_synpred238_JWIPreprocessor_Parser5953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESSTHAN_in_synpred241_JWIPreprocessor_Parser6044 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LESSTHAN_in_synpred241_JWIPreprocessor_Parser6046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred242_JWIPreprocessor_Parser6078 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred242_JWIPreprocessor_Parser6080 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred242_JWIPreprocessor_Parser6082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred243_JWIPreprocessor_Parser6118 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_GREATERTHAN_in_synpred243_JWIPreprocessor_Parser6120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred255_JWIPreprocessor_Parser6329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTPARENTHESIS_in_synpred259_JWIPreprocessor_Parser6367 = new BitSet(new long[]{0x000100A082128000L,0x0100000000000000L});
    public static final BitSet FOLLOW_primitiveType_in_synpred259_JWIPreprocessor_Parser6369 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHTPARENTHESIS_in_synpred259_JWIPreprocessor_Parser6371 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_unaryExpression_in_synpred259_JWIPreprocessor_Parser6373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_synpred260_JWIPreprocessor_Parser6385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred262_JWIPreprocessor_Parser6426 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_synpred262_JWIPreprocessor_Parser6428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred263_JWIPreprocessor_Parser6432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred268_JWIPreprocessor_Parser6480 = new BitSet(new long[]{0x0000000000000000L,0x0100000000000000L});
    public static final BitSet FOLLOW_Identifier_in_synpred268_JWIPreprocessor_Parser6482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred269_JWIPreprocessor_Parser6486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_synpred275_JWIPreprocessor_Parser6561 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_synpred275_JWIPreprocessor_Parser6563 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_synpred275_JWIPreprocessor_Parser6565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFTSQUAREBRACKET_in_synpred288_JWIPreprocessor_Parser6801 = new BitSet(new long[]{0x244906A092128000L,0x01034E00401B2014L});
    public static final BitSet FOLLOW_expression_in_synpred288_JWIPreprocessor_Parser6803 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHTSQUAREBRACKET_in_synpred288_JWIPreprocessor_Parser6805 = new BitSet(new long[]{0x0000000000000002L});

}