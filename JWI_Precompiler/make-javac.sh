#!/bin/bash
# cd src_generated/java
# javac -classpath .:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/antlr-3.2.jar com/kevinmatz/jwi/*.java -d ../../executables $1 $2 $3 $4 $5 $6 $7 $8 $9
# cd ../..
cd src_in/java
javac -classpath .:../../src_generated/java/com/kevinmatz/jwi:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/antlr-3.2.jar:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/jewelcli-0.6.jar com/kevinmatz/jwi/*.java -d ../../executables $1 $2 $3 $4 $5 $6 $7 $8 $9
cd ../..


