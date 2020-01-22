#!/bin/bash
cd src_in/antlr
java -classpath .:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/antlr-3.2.jar org.antlr.Tool JWIPreprocessor_Parser.g -o ../../src_in/java/com/kevinmatz/jwi/parser
# -lib ../../src_generated/antlr
cd ../..
