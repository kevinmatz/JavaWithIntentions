#!/bin/bash
# cd executables
# java -classpath .:executables:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/antlr-3.2.jar JWIPreprocessor_Parser $1

java -classpath .:executables:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/antlr-3.2.jar:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/jewelcli-0.6.jar com.kevinmatz.jwi.JWIPreprocessor $1 $2 $3 $4 $5 $6 $7 $8 $9

# cd ..
