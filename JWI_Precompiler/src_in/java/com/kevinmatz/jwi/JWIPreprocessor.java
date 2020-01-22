package com.kevinmatz.jwi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

import com.kevinmatz.jwi.commandlineparser.CommandLineArgs;
import com.kevinmatz.jwi.parser.JWIPreprocessor_Lexer;
import com.kevinmatz.jwi.parser.JWIPreprocessor_Parser;
import com.kevinmatz.jwi.parser.JWIPreprocessor_Parser.jwiCompilationUnit_return;


public class JWIPreprocessor {

	/** This symbol table is only for a single input file at this time. TODO Matz: Extend for multiple files */
	// public static Map<String, SymbolTableEntry> symbolTable = new HashMap<String, SymbolTableEntry>();

	
	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
		    System.out.println("Java with Intentions precompiler (reference implementation)");
		    System.out.println("v0.0.1");
		    System.out.println("Copyright 2010-2011, Kevin Matz, all rights reserved.");
		    System.out.println("");
		    System.out.println("Syntax: java com.kevinmatz.jwi.JWIPreprocessor --ojava <directory for output *.java files> <list of *.jwi files to process>");
		    System.out.println("");
		}
		
		CommandLineArgs commandLineArgs = null;
		try {
			commandLineArgs = CliFactory.parseArguments(CommandLineArgs.class, args);
		}
		catch (ArgumentValidationException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	    List<File> jwiInputFiles = commandLineArgs.getJwiInputFiles();
	    if ((jwiInputFiles == null) || (jwiInputFiles.size() == 0)) {
	    	System.out.println("No *.jwi filenames specified to process.");
	    	System.exit(1);
	    }
	    
		for (File file : jwiInputFiles) {
			System.out.println("Processing file: " + file.toString());			
		    processJWIFile(file, commandLineArgs.getGeneratedJavaOutputDir());
		}
		
		System.out.println("Symbol table:");
		System.out.println("---------------------");
	    System.out.println(SymbolTableManager.getInstance().getSymbolTable());
		System.out.println("---------------------");
		
        System.out.println("Performing contextual analysis...");
        
        boolean finalResult = checkReferences(SymbolTableManager.getInstance().getSymbolTable());
        
        if (finalResult) {
            System.out.println("Contextual analysis passed.");
        } else {
            System.out.println("Contextual analysis failed.");
        }

	}
	
	/**
	 * 
	 * @param inputJwiFile
	 * @return true, if one or more errors were detected; false, if no errors were detected
	 * @throws IOException
	 */
	public static boolean processJWIFile(File inputJwiFile, File outputDirectory) throws IOException {

		String filename = inputJwiFile.getCanonicalPath().toString();
		
		// JWI_Lexer lexer = new JWI_Lexer(new ANTLRFileStream(args[0]));
	    JWIPreprocessor_Lexer lexer = new JWIPreprocessor_Lexer(new ANTLRFileStream(filename));
	    
	   	// CommonTokenStream does not work with rewrite mode; we must use TokenRewriteStream instead.
	   	// CommonTokenStream tokens = new CommonTokenStream(lexer);
	    TokenRewriteStream tokens = new TokenRewriteStream(lexer);
	    
	    JWIPreprocessor_Parser parser = new JWIPreprocessor_Parser(tokens);

	    try {
	    	jwiCompilationUnit_return retVal = parser.jwiCompilationUnit(filename);
	        
	        // TODO Matz: How to determine whether parsing errors have occurred?  They're not thrown as exceptions...
	        
	        // "tokens" now contains the rewritten version of the file
	        // writeTranslationToFile(filename, tokens);
	        
	        String translatedFile = tokens.toString();
	        
	        // TODO Matz: This is temporary; we still need to insert paths corresponding to
	        // the package hierarchy...

/* From Javadoc reference:
   -d directory
    Set the destination directory for class files. The destination directory must already exist; javac will not create the destination directory. If a class is part of a package, javac puts the class file in a subdirectory reflecting the package name, creating directories as needed. For example, if you specify -d c:\myclasses and the class is called com.mypackage.MyClass, then the class file is called c:\myclasses\com\mypackage\MyClass.class.

    If -d is not specified, javac puts the class file in the same directory as the source file.

    Note that the directory specified by -d is not automatically added to your user class path. 
 */
	    	String packageName = retVal.packageName;
            String packagePath = packageName.replaceAll("\\.", (String) System.getProperties().get("file.separator"));  // TODO Matz: Test this with backslashes (DOS)
            String outputPath = outputDirectory.getPath() + System.getProperties().get("file.separator") + packagePath;
            
            File outputPathFile = new File(outputPath);
            if (! outputPathFile.exists()) {
            	outputPathFile.mkdirs();
            }
            
            String jwiFilename = inputJwiFile.getName();
            String javaFilename = jwiFilename.replace(".jwi", ".java");   // Note: replace() does not use regular expressions!
            
            String outputFilename = outputPath + System.getProperties().get("file.separator") + javaFilename;

	        System.out.println("Output filename: " + outputFilename);
	        
	        FileWriter fileWriter = new FileWriter(outputFilename);
	        fileWriter.write(translatedFile);
	        fileWriter.close();
	        
	    } catch (RecognitionException e)  {
	        e.printStackTrace();
	    }
	    
	    return false;  // TODO Matz
	}

	public static boolean checkReferences(Map<String, SymbolTableEntry> symbolTable) {

	    Collection<SymbolTableEntry> entries = symbolTable.values();

	    Iterator<SymbolTableEntry> it = entries.iterator();

	    boolean finalResult = true;
	    
	    while (it.hasNext()) {
	        SymbolTableEntry entry = it.next();
	        
	        boolean result = entry.element.checkNode(symbolTable);
	        
	        // System.out.println("Result: " + result);
	        
	        if (result == false) {
	            finalResult = false;
	            break;
	        }
	    }
	    
	    return finalResult;
	}	

}
