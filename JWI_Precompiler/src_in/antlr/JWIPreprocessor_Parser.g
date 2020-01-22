/* 
 * ANTLR parser grammar for Java with Intentions (work in progress)
 * Kevin Matz, 2011.03.03
 *
 * Based on Terence Parr's Java 1.5 grammar (see comment blocks below).
 */

/*
 [The "BSD licence"]
 Copyright (c) 2007-2008 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
 
 
parser grammar JWIPreprocessor_Parser;

options {
    backtrack=true;
    memoize=true;

    // We're going to output using templates:
    output = template;
    
    // Turn rewrite mode on, which automatically copies input to the output, except
    // where you specify translations using template rewrite rules:
    rewrite = true;
    
    // Use tokens defined in the lexer grammar:
    tokenVocab = JWIPreprocessor_Lexer;
}


@header {
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
}


@members {

/** This symbol table is only for a single input file at this time. TODO Matz: Extend for multiple files */
// public static Map<String, SymbolTableEntry> symbolTable = new HashMap<String, SymbolTableEntry>();

}  // (End of @members)



// Parser
// ------


// Starting point for parsing a JWI file:
jwiCompilationUnit[String filenameBeingProcessed] returns [String packageName]
    scope {
        String filename;
    }
:   {
        // System.out.println("CHECKPOINT AAA");
        $jwiCompilationUnit::filename = $filenameBeingProcessed;
    }
    packageName1=compilationUnit { $packageName = $packageName1.packageName; }
    // Note: Do not put "EOF" here. Unsure why not, but it doesn't work.
    ;


// Starting point for parsing a java file:
/* The annotations are separated out to make parsing faster, but must be associated with
   a packageDeclaration or a typeDeclaration (and not an empty one). */
compilationUnit returns [String packageName]
    :   annotations
        (   packageName1=packageDeclaration { $packageName = $packageName1.packageName; } importDeclaration* typeDeclaration*
        |   classOrInterfaceDeclaration typeDeclaration*
        )
    |   (packageName2=packageDeclaration? { $packageName = $packageName2.packageName; } ) importDeclaration* (standaloneIntentionDeclaration | typeDeclaration)*
    ;

packageDeclaration returns [String packageName]
    :   PACKAGE packageName1=qualifiedName SEMICOLON
        {
            $packageName = $packageName1.text;
        }
    ;
    
importDeclaration
    :   IMPORT STATIC? qualifiedName (DOT ASTERISK)? SEMICOLON
    ;


standaloneIntentionDeclaration
    scope {
        IntentionDefinition_NonAbstract defn;
    }
    :   ABSTRACT ? INTENTION name1=Identifier
        {
            // System.out.println("CHECKPOINT A; $name1.text == \"" + $name1.text + "\"");

            // TODO Matz: Choose either IntentionDefinition_Abstract or IntentionDefinition_NonAbstract depending on the presence of the ABSTRACT modifier
            
            // IntentionDefinition_NonAbstract
            $standaloneIntentionDeclaration::defn = new IntentionDefinition_NonAbstract($name1.text);
            SymbolTableEntry entry = new SymbolTableEntry($standaloneIntentionDeclaration::defn);
            SymbolTableManager.getInstance().getSymbolTable().put($name1.text, entry);
        }
        intentionExtendsClause[$standaloneIntentionDeclaration::defn]?
        intentionDeclarationBody
    -> template(content={$text}) "/* <content> */"    // Rewrite intention definition to be surrounded by comments
;


standaloneRequirementDeclaration
    scope {
        IntentionDefinition_NonAbstract defn;
    }
    :   ABSTRACT ? REQUIREMENT name1=Identifier
        {
            // System.out.println("CHECKPOINT A; $name1.text == \"" + $name1.text + "\"");

            // TODO Matz: Choose either IntentionDefinition_Abstract or IntentionDefinition_NonAbstract depending on the presence of the ABSTRACT modifier
            
            // IntentionDefinition_NonAbstract
            $standaloneIntentionDeclaration::defn = new IntentionDefinition_NonAbstract($name1.text);
            SymbolTableEntry entry = new SymbolTableEntry($standaloneIntentionDeclaration::defn);
            SymbolTableManager.getInstance().getSymbolTable().put($name1.text, entry);
        }
        intentionExtendsClause[$standaloneIntentionDeclaration::defn]?
        intentionDeclarationBody
    -> template(content={$text}) "/* <content> */"    // Rewrite intention definition to be surrounded by comments
;


standaloneGoalDeclaration
    scope {
        IntentionDefinition_NonAbstract defn;
    }
    :   ABSTRACT ? GOAL name1=Identifier
        {
            // System.out.println("CHECKPOINT A; $name1.text == \"" + $name1.text + "\"");

            // TODO Matz: Choose either IntentionDefinition_Abstract or IntentionDefinition_NonAbstract depending on the presence of the ABSTRACT modifier
            
            // IntentionDefinition_NonAbstract
            $standaloneIntentionDeclaration::defn = new IntentionDefinition_NonAbstract($name1.text);
            SymbolTableEntry entry = new SymbolTableEntry($standaloneIntentionDeclaration::defn);
            SymbolTableManager.getInstance().getSymbolTable().put($name1.text, entry);
        }
        intentionExtendsClause[$standaloneIntentionDeclaration::defn]?
        intentionDeclarationBody
    -> template(content={$text}) "/* <content> */"    // Rewrite intention definition to be surrounded by comments
;


intentionDeclarationBody
    :   LEFTBRACE
        DESCRIPTION FREETEXTINBRACES
        (
            INTERFACEREFERENCE Identifier SEMICOLON
            |
            INTERFACEREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier (ASSIGNMENT_EQUALS LEFTBRACE (Identifier (COMMA Identifier)*)? RIGHTBRACE) SEMICOLON
            |
            CLASSREFERENCE Identifier SEMICOLON
            |
            CLASSREFERENCE LEFTSQUAREBRACKET RIGHTSQUAREBRACKET Identifier (ASSIGNMENT_EQUALS LEFTBRACE (Identifier (COMMA Identifier)*)? RIGHTBRACE) SEMICOLON
            |
            TEXTFIELD Identifier (ASSIGNMENT_EQUALS StringLiteral)? SEMICOLON
        )*
        RIGHTBRACE
    ;

// freeTextInBraces /* TODO Matz: This is not working; why? -->  options { greedy = false; } */
//     :   LEFTBRACE
//         /* TODO Matz: This is not working; why? -->  .* */
//         ( options { greedy = false; } : . )*
//         RIGHTBRACE
//     ;

intentionExtendsClause[GeneralIntentionDefinition defn]
    :   EXTENDS name1=Identifier
        {
            // System.out.println("CHECKPOINT D");

            ReferenceNeedingContextChecking ref = new ReferenceNeedingContextChecking();
            ref.targetElementName = $name1.text;
            ref.lineNumberWhereReferenceOccurs = $name1.line;
            ref.filenameWhereReferenceOccurs = $jwiCompilationUnit::filename;
    
            defn.extendedIntentions.add(ref);
        }
;

classImplementsIntentionClause[ClassDefinition defn]
    :   IMPLEMENTSINTENTION name1=Identifier
        {
            // System.out.println("CHECKPOINT E");
            
            ReferenceNeedingContextChecking ref = new ReferenceNeedingContextChecking();
            ref.targetElementName = $name1.text;
            ref.lineNumberWhereReferenceOccurs = $name1.line;
            ref.filenameWhereReferenceOccurs = $jwiCompilationUnit::filename;
            
            defn.intentionsImplemented.add(ref);
        }
        (COMMA name2=Identifier
            {
                // System.out.println("CHECKPOINT F");

                ReferenceNeedingContextChecking ref2 = new ReferenceNeedingContextChecking();
                ref2.targetElementName = $name2.text;
                ref2.lineNumberWhereReferenceOccurs = $name2.line;
                ref2.filenameWhereReferenceOccurs = $jwiCompilationUnit::filename;
            
                defn.intentionsImplemented.add(ref2);
            }
        )*
    -> template(content={$text}) "/* <content> */"    // Rewrite clause to be surrounded by comments
;

methodImplementsIntentionClause    // TODO Matz: [ClassDefinition defn] or equivalent
    :   IMPLEMENTSINTENTION name1=Identifier
        (COMMA name2=Identifier)*
    -> template(content={$text}) "/* <content> */"    // Rewrite clause to be surrounded by comments
;

typeDeclaration
    :   classOrInterfaceDeclaration
    |   SEMICOLON
    ;
    
classOrInterfaceDeclaration
    :   classOrInterfaceModifiers
        (classDef=classDeclaration
         |
         interfaceDef=interfaceDeclaration
        )
    ;
        
classOrInterfaceModifiers
    :   classOrInterfaceModifier*
    ;

classOrInterfaceModifier
    :   annotation   // class or interface
    |   PUBLIC       // class or interface
    |   PROTECTED    // class or interface
    |   PRIVATE      // class or interface
    |   ABSTRACT     // class or interface
    |   STATIC       // class or interface
    |   FINAL        // class only -- does not apply to interfaces
    |   STRICTFP     // class or interface
    ;

modifiers
    :   modifier*
    ;

classDeclaration returns [ClassDefinition classDefinitionObj]
    :   classDefn=normalClassDeclaration
        {
            $classDefinitionObj = $classDefn.classDefinitionObj;
        }
    |   enumDefn=enumDeclaration
        {
            $classDefinitionObj = $enumDefn.classDefinitionObj;
        }
    ;
    
normalClassDeclaration returns [ClassDefinition classDefinitionObj]
    scope {
        ClassDefinition defn;
    }
    :   CLASS name1=Identifier typeParameters?
        (EXTENDS type)?
        (IMPLEMENTS typeList)?
        {
            // System.out.println("CHECKPOINT G");
    
            /* ClassDefinition */
            $normalClassDeclaration::defn = new ClassDefinition($name1.text);
            SymbolTableEntry entry = new SymbolTableEntry($normalClassDeclaration::defn);
            SymbolTableManager.getInstance().getSymbolTable().put($name1.text, entry);
            
            $classDefinitionObj = $normalClassDeclaration::defn;
        }
        classImplementsIntentionClause[$normalClassDeclaration::defn]
        classBody
    ;
    
typeParameters
    :   LESSTHAN typeParameter (COMMA typeParameter)* GREATERTHAN
    ;

typeParameter
    :   Identifier (EXTENDS typeBound)?
    ;
        
typeBound
    :   type (BITWISE_AND type)*
    ;

enumDeclaration returns [ClassDefinition classDefinitionObj]
    :   ENUM name1=Identifier (IMPLEMENTS typeList)?
        {
            // System.out.println("CHECKPOINT B");
    
            ClassDefinition defn = new ClassDefinition($name1.text);
            SymbolTableEntry entry = new SymbolTableEntry(defn);
            SymbolTableManager.getInstance().getSymbolTable().put($name1.text, entry);
            
            $classDefinitionObj = defn;
        }
        enumBody
    ;

enumBody
    :   LEFTBRACE enumConstants? COMMA? enumBodyDeclarations? RIGHTBRACE
    ;

enumConstants
    :   enumConstant (COMMA enumConstant)*
    ;
    
enumConstant
    :   annotations? Identifier arguments? classBody?
    ;
    
enumBodyDeclarations
    :   SEMICOLON (classBodyDeclaration)*
    ;
    
interfaceDeclaration returns [ClassDefinition classDefinitionObj]    // TODO Matz: Should return InterfaceDefinition but that hasn't been defined yet
    :   interfaceDef=normalInterfaceDeclaration
        {
            $classDefinitionObj = $interfaceDef.classDefinitionObj;
        }
    |   annotationDef=annotationTypeDeclaration
        {
            $classDefinitionObj = $annotationDef.classDefinitionObj;
        }
    ;

normalInterfaceDeclaration returns [ClassDefinition classDefinitionObj]
    :   INTERFACE name1=Identifier typeParameters? (EXTENDS typeList)?
        {
            // System.out.println("CHECKPOINT B");
    
            ClassDefinition defn = new ClassDefinition($name1.text);
            SymbolTableEntry entry = new SymbolTableEntry(defn);
            SymbolTableManager.getInstance().getSymbolTable().put($name1.text, entry);
            
            $classDefinitionObj = defn;
        }    
        interfaceBody
    ;
    
typeList
    :   type (COMMA type)*
    ;
    
classBody
    :   LEFTBRACE classBodyDeclaration* RIGHTBRACE
    ;
    
interfaceBody
    :   LEFTBRACE interfaceBodyDeclaration* RIGHTBRACE
    ;

classBodyDeclaration
    :   SEMICOLON
    |   STATIC? block
    |   modifiers memberDecl
    ;
    
memberDecl
    :   genericMethodOrConstructorDecl
    |   memberDeclaration
    |   VOID Identifier voidMethodDeclaratorRest
    |   Identifier constructorDeclaratorRest
    |   interfaceDeclaration
    |   classDeclaration
    ;
    
memberDeclaration
    :   type (methodDeclaration | fieldDeclaration)
    ;

genericMethodOrConstructorDecl
    :   typeParameters genericMethodOrConstructorRest
    ;
    
genericMethodOrConstructorRest
    :   (type | VOID) Identifier methodDeclaratorRest
    |   Identifier constructorDeclaratorRest
    ;

methodDeclaration
    :   Identifier methodDeclaratorRest
    ;

fieldDeclaration
    :   variableDeclarators SEMICOLON
    ;
        
interfaceBodyDeclaration
    :   modifiers interfaceMemberDecl
    |   SEMICOLON
    ;

interfaceMemberDecl
    :   interfaceMethodOrFieldDecl
    |   interfaceGenericMethodDecl
    |   VOID Identifier voidInterfaceMethodDeclaratorRest
    |   interfaceDeclaration
    |   classDeclaration
    ;
    
interfaceMethodOrFieldDecl
    :   type Identifier interfaceMethodOrFieldRest
    ;
    
interfaceMethodOrFieldRest
    :   constantDeclaratorsRest SEMICOLON
    |   interfaceMethodDeclaratorRest
    ;
    
methodDeclaratorRest
    :   formalParameters (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)*
        methodImplementsIntentionClause?
        (THROWS qualifiedNameList)?
        (   methodBody
        |   SEMICOLON
        )
    ;
    
voidMethodDeclaratorRest
    :   formalParameters
        methodImplementsIntentionClause?
        (THROWS qualifiedNameList)?
        (   methodBody
        |   SEMICOLON
        )
    ;
    
interfaceMethodDeclaratorRest
    :   formalParameters (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)* (THROWS qualifiedNameList)? SEMICOLON
    ;
    
interfaceGenericMethodDecl
    :   typeParameters (type | VOID) Identifier
        interfaceMethodDeclaratorRest
    ;
    
voidInterfaceMethodDeclaratorRest
    :   formalParameters
        methodImplementsIntentionClause?
        (THROWS qualifiedNameList)? SEMICOLON
    ;
    
constructorDeclaratorRest
    :   formalParameters
        methodImplementsIntentionClause?
        (THROWS qualifiedNameList)? constructorBody
    ;

constantDeclarator
    :   Identifier constantDeclaratorRest
    ;
    
variableDeclarators
    :   variableDeclarator (COMMA variableDeclarator)*
    ;

variableDeclarator
    :   variableDeclaratorId (ASSIGNMENT_EQUALS variableInitializer)?
    ;
    
constantDeclaratorsRest
    :   constantDeclaratorRest (COMMA constantDeclarator)*
    ;

constantDeclaratorRest
    :   (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)* ASSIGNMENT_EQUALS variableInitializer
    ;
    
variableDeclaratorId
    :   Identifier (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)*
    ;

variableInitializer
    :   arrayInitializer
    |   expression
    ;
        
arrayInitializer
    :   LEFTBRACE (variableInitializer (COMMA variableInitializer)* (COMMA)? )? RIGHTBRACE
    ;

modifier
    :   annotation
    |   PUBLIC
    |   PROTECTED
    |   PRIVATE
    |   STATIC
    |   ABSTRACT
    |   FINAL
    |   NATIVE
    |   SYNCHRONIZED
    |   TRANSIENT
    |   VOLATIVE
    |   STRICTFP
    ;

packageOrTypeName
    :   qualifiedName
    ;

enumConstantName
    :   Identifier
    ;

typeName
    :   qualifiedName
    ;

type
	:	classOrInterfaceType (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)*
	|	primitiveType (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)*
	;

classOrInterfaceType
	:	Identifier typeArguments? (DOT Identifier typeArguments? )*
	;

primitiveType
    :   BOOLEAN
    |   CHAR
    |   BYTE
    |   SHORT
    |   INT
    |   LONG
    |   FLOAT
    |   DOUBLE
    ;

variableModifier
    :   FINAL
    |   annotation
    ;

typeArguments
    :   LESSTHAN typeArgument (COMMA typeArgument)* GREATERTHAN
    ;
    
typeArgument
    :   type
    |   QUESTIONMARK ((EXTENDS | SUPER) type)?
    ;
    
qualifiedNameList
    :   qualifiedName (COMMA qualifiedName)*
    ;

formalParameters
    :   LEFTPARENTHESIS formalParameterDecls? RIGHTPARENTHESIS
    ;
    
formalParameterDecls
    :   variableModifiers type formalParameterDeclsRest
    ;
    
formalParameterDeclsRest
    :   variableDeclaratorId (COMMA formalParameterDecls)?
    |   ELLIPSIS variableDeclaratorId
    ;
    
methodBody
    :   block
    ;

constructorBody
    :   LEFTBRACE explicitConstructorInvocation? blockStatement* RIGHTBRACE
    ;

explicitConstructorInvocation
    :   nonWildcardTypeArguments? (THIS | SUPER) arguments SEMICOLON
    |   primary DOT nonWildcardTypeArguments? SUPER arguments SEMICOLON
    ;


qualifiedName
    :   Identifier (DOT Identifier)*
    ;
    
literal 
    :   integerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   booleanLiteral
    |   NULL
    ;

integerLiteral
    :   HexLiteral
    |   OctalLiteral
    |   DecimalLiteral
    ;

booleanLiteral
    :   TRUE
    |   FALSE
    ;

// ANNOTATIONS

annotations
    :   annotation+
    ;

annotation
    :   ATSIGN annotationName ( LEFTPARENTHESIS ( elementValuePairs | elementValue )? RIGHTPARENTHESIS )?
    ;
    
annotationName
    : Identifier (DOT Identifier)*
    ;

elementValuePairs
    :   elementValuePair (COMMA elementValuePair)*
    ;

elementValuePair
    :   Identifier ASSIGNMENT_EQUALS elementValue
    ;
    
elementValue
    :   conditionalExpression
    |   annotation
    |   elementValueArrayInitializer
    ;
    
elementValueArrayInitializer
    :   LEFTBRACE (elementValue (COMMA elementValue)*)? (COMMA)? RIGHTBRACE
    ;
    
annotationTypeDeclaration returns [ClassDefinition classDefinitionObj]
    :   ATSIGN INTERFACE name1=Identifier
        {
            // System.out.println("CHECKPOINT B");
    
            ClassDefinition defn = new ClassDefinition($name1.text);
            SymbolTableEntry entry = new SymbolTableEntry(defn);
            SymbolTableManager.getInstance().getSymbolTable().put($name1.text, entry);
            
            $classDefinitionObj = defn;
        }   
        annotationTypeBody
    ;

annotationTypeBody
    :   LEFTBRACE (annotationTypeElementDeclaration)* RIGHTBRACE
    ;
    
annotationTypeElementDeclaration
    :   modifiers annotationTypeElementRest
    ;
    
annotationTypeElementRest
    :   type annotationMethodOrConstantRest SEMICOLON
    |   normalClassDeclaration SEMICOLON?
    |   normalInterfaceDeclaration SEMICOLON?
    |   enumDeclaration SEMICOLON?
    |   annotationTypeDeclaration SEMICOLON?
    ;
    
annotationMethodOrConstantRest
    :   annotationMethodRest
    |   annotationConstantRest
    ;
    
annotationMethodRest
    :   Identifier LEFTPARENTHESIS RIGHTPARENTHESIS defaultValue?
    ;
    
annotationConstantRest
    :   variableDeclarators
    ;
    
defaultValue
    :   DEFAULT elementValue
    ;

// STATEMENTS / BLOCKS

block
    :   LEFTBRACE (inlineIntentionBlock | blockStatement)* RIGHTBRACE
    ;

inlineIntentionBlock
    :   inlineIntentionCommentOpeningTag
        (inlineIntentionBlock | blockStatement)*
        inlineIntentionCommentClosingTag
    ;    

inlineIntentionCommentOpeningTag
    :   INLINEINTENTIONOPENINGTAGTOKEN
    -> template(content={$text}) "/* <content> */"    // Rewrite intention definition to be surrounded by comments
    ;
    
// inlineIntentionCommentOpeningTag
//     :   LEFTSQUAREBRACKET LEFTSQUAREBRACKET intentionName=Identifier 
//         PIPEFREETEXTANDENDOFINLINEINTENTIONTAG
//         // PIPE commentText=Identifier RIGHTSQUAREBRACKET RIGHTSQUAREBRACKET
//     -> template(content={$text}) "/* <content> */"    // Rewrite intention definition to be surrounded by comments
//     ;

inlineIntentionCommentClosingTag
    :   INLINEINTENTIONCLOSINGTAGTOKEN
    -> template(content={$text}) "/* <content> */"    // Rewrite intention definition to be surrounded by comments
    ;

// inlineIntentionCommentClosingTag
//     :   LEFTSQUAREBRACKET LEFTSQUAREBRACKET SLASH intentionName2=Identifier RIGHTSQUAREBRACKET RIGHTSQUAREBRACKET
//     -> template(content={$text}) "/* <content> */"    // Rewrite intention definition to be surrounded by comments
//     ;
        
blockStatement
    :   localVariableDeclarationStatement
    |   classOrInterfaceDeclaration
    |   statement
    ;
    
localVariableDeclarationStatement
    :    localVariableDeclaration SEMICOLON
    ;

localVariableDeclaration
    :   variableModifiers type variableDeclarators
    ;
    
variableModifiers
    :   variableModifier*
    ;

statement
    :   block
    |   ASSERT expression (COLON expression)? SEMICOLON
    |   IF parExpression statement (options {k=1;}:ELSE statement)?
    |   FOR LEFTPARENTHESIS forControl RIGHTPARENTHESIS statement
    |   WHILE parExpression statement
    |   DO statement WHILE parExpression SEMICOLON
    |   TRY block
        ( catches FINALLY block
        | catches
        |   FINALLY block
        )
    |   SWITCH parExpression LEFTBRACE switchBlockStatementGroups RIGHTBRACE
    |   SYNCHRONIZED parExpression block
    |   RETURN expression? SEMICOLON
    |   THROW expression SEMICOLON
    |   BREAK Identifier? SEMICOLON
    |   CONTINUE Identifier? SEMICOLON
    |   SEMICOLON 
    |   statementExpression SEMICOLON
    |   Identifier COLON statement
    ;
    
catches
    :   catchClause (catchClause)*
    ;
    
catchClause
    :   CATCH LEFTPARENTHESIS formalParameter RIGHTPARENTHESIS block
    ;

formalParameter
    :   variableModifiers type variableDeclaratorId
    ;
        
switchBlockStatementGroups
    :   (switchBlockStatementGroup)*
    ;
    
/* The change here (switchLabel -> switchLabel+) technically makes this grammar
   ambiguous; but with appropriately greedy parsing it yields the most
   appropriate AST, one in which each group, except possibly the last one, has
   labels and statements. */
switchBlockStatementGroup
    :   switchLabel+ blockStatement*
    ;
    
switchLabel
    :   CASE constantExpression COLON
    |   CASE enumConstantName COLON
    |   DEFAULT COLON
    ;
    
forControl
options {k=3;} // be efficient for common case: for (ID ID : ID) ...
    :   enhancedForControl
    |   forInit? SEMICOLON expression? SEMICOLON forUpdate?
    ;

forInit
    :   localVariableDeclaration
    |   expressionList
    ;
    
enhancedForControl
    :   variableModifiers type Identifier COLON expression
    ;

forUpdate
    :   expressionList
    ;

// EXPRESSIONS

parExpression
    :   LEFTPARENTHESIS expression RIGHTPARENTHESIS
    ;
    
expressionList
    :   expression (COMMA expression)*
    ;

statementExpression
    :   expression
    ;
    
constantExpression
    :   expression
    ;
    
expression
    :   conditionalExpression (assignmentOperator expression)?
    ;
    
assignmentOperator
    :   ASSIGNMENT_EQUALS
    |   PLUSEQUALS
    |   MINUSEQUALS
    |   ASTERISKEQUALS
    |   SLASHEQUALS
    |   BITWISE_AND_EQUALS
    |   BITWISE_OR_EQUALS
    |   CARETEQUALS
    |   PERCENTEQUALS
    |   (LESSTHAN LESSTHAN ASSIGNMENT_EQUALS)=> t1=LESSTHAN t2=LESSTHAN t3=ASSIGNMENT_EQUALS 
        { $t1.getLine() == $t2.getLine() &&
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    |   (GREATERTHAN GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS)=> t1=GREATERTHAN t2=GREATERTHAN t3=GREATERTHAN t4=ASSIGNMENT_EQUALS
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() &&
          $t3.getLine() == $t4.getLine() && 
          $t3.getCharPositionInLine() + 1 == $t4.getCharPositionInLine() }?
    |   (GREATERTHAN GREATERTHAN ASSIGNMENT_EQUALS)=> t1=GREATERTHAN t2=GREATERTHAN t3=ASSIGNMENT_EQUALS
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    ;

conditionalExpression
    :   conditionalOrExpression ( QUESTIONMARK expression COLON expression )?
    ;

conditionalOrExpression
    :   conditionalAndExpression ( LOGICAL_OR conditionalAndExpression )*
    ;

conditionalAndExpression
    :   inclusiveOrExpression ( LOGICAL_AND inclusiveOrExpression )*
    ;

inclusiveOrExpression
    :   exclusiveOrExpression ( PIPE /* BITWISE_OR */ exclusiveOrExpression )*
    ;

exclusiveOrExpression
    :   andExpression ( CARET andExpression )*
    ;

andExpression
    :   equalityExpression ( BITWISE_AND equalityExpression )*
    ;

equalityExpression
    :   instanceOfExpression ( (EQUALITY_EQUALS | EXCLAMATIONMARKEQUALS) instanceOfExpression )*
    ;

instanceOfExpression
    :   relationalExpression (INSTANCEOF type)?
    ;

relationalExpression
    :   shiftExpression ( relationalOp shiftExpression )*
    ;
    
relationalOp
    :   (LESSTHAN ASSIGNMENT_EQUALS)=> t1=LESSTHAN t2=ASSIGNMENT_EQUALS 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   (GREATERTHAN ASSIGNMENT_EQUALS)=> t1=GREATERTHAN t2=ASSIGNMENT_EQUALS 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   LESSTHAN 
    |   GREATERTHAN 
    ;

shiftExpression
    :   additiveExpression ( shiftOp additiveExpression )*
    ;

shiftOp
    :   (LESSTHAN LESSTHAN)=> t1=LESSTHAN t2=LESSTHAN 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   (GREATERTHAN GREATERTHAN GREATERTHAN)=> t1=GREATERTHAN t2=GREATERTHAN t3=GREATERTHAN 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    |   (GREATERTHAN GREATERTHAN)=> t1=GREATERTHAN t2=GREATERTHAN
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    ;


additiveExpression
    :   multiplicativeExpression ( (PLUS | MINUS) multiplicativeExpression )*
    ;

multiplicativeExpression
    :   unaryExpression ( ( ASTERISK | SLASH | PERCENT ) unaryExpression )*
    ;
    
unaryExpression
    :   PLUS unaryExpression
    |   MINUS unaryExpression
    |   PLUSPLUS unaryExpression
    |   MINUSMINUS unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   TILDE unaryExpression
    |   EXCLAMATIONMARK unaryExpression
    |   castExpression
    |   primary selector* (PLUSPLUS|MINUSMINUS)?
    ;

castExpression
    :  LEFTPARENTHESIS primitiveType RIGHTPARENTHESIS unaryExpression
    |  LEFTPARENTHESIS (type | expression) RIGHTPARENTHESIS unaryExpressionNotPlusMinus
    ;

primary
    :   parExpression
    |   THIS (DOT Identifier)* identifierSuffix?
    |   SUPER superSuffix
    |   literal
    |   NEW creator
    |   Identifier (DOT Identifier)* identifierSuffix?
    |   primitiveType (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)* DOT CLASS
    |   VOID DOT CLASS
    ;

identifierSuffix
    :   (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)+ DOT CLASS
    |   (LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET)+ // can also be matched by selector, but do here
    |   arguments
    |   DOT CLASS
    |   DOT explicitGenericInvocation
    |   DOT THIS
    |   DOT SUPER arguments
    |   DOT NEW innerCreator
    ;

creator
    :   nonWildcardTypeArguments createdName classCreatorRest
    |   createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :   classOrInterfaceType
    |   primitiveType
    ;
    
innerCreator
    :   nonWildcardTypeArguments? Identifier classCreatorRest
    ;

arrayCreatorRest
    :   LEFTSQUAREBRACKET
        (   RIGHTSQUAREBRACKET (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)* arrayInitializer
        |   expression RIGHTSQUAREBRACKET (LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET)* (LEFTSQUAREBRACKET RIGHTSQUAREBRACKET)*
        )
    ;

classCreatorRest
    :   arguments classBody?
    ;
    
explicitGenericInvocation
    :   nonWildcardTypeArguments Identifier arguments
    ;
    
nonWildcardTypeArguments
    :   LESSTHAN typeList GREATERTHAN
    ;
    
selector
    :   DOT Identifier arguments?
    |   DOT THIS
    |   DOT SUPER superSuffix
    |   DOT NEW innerCreator
    |   LEFTSQUAREBRACKET expression RIGHTSQUAREBRACKET
    ;
    
superSuffix
    :   arguments
    |   DOT Identifier arguments?
    ;

arguments
    :   LEFTPARENTHESIS expressionList? RIGHTPARENTHESIS
    ;

