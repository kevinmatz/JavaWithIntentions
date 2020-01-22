#!/bin/bash
java -classpath .:executables:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/antlr-3.2.jar:/Users/kevin_matz/Projects/M801/EclipseWorkspace/JWI_Precompiler/lib/jewelcli-0.6.jar com.kevinmatz.jwi.JWIPreprocessor --ojava tmp/generated_java ../JWI_DemoProject1/src-jwi/jwidemos/flashcardtrainer/goals/*.jwi ../JWI_DemoProject1/src-jwi/jwidemos/flashcardtrainer/requirements/abstract_/*.jwi ../JWI_DemoProject1/src-jwi/jwidemos/flashcardtrainer/requirements/*.jwi ../JWI_DemoProject1/src-jwi/jwidemos/flashcardtrainer/intentions/abstract_/*.jwi ../JWI_DemoProject1/src-jwi/jwidemos/flashcardtrainer/intentions/*.jwi ../JWI_DemoProject1/src-jwi/jwidemos/flashcardtrainer/*.jwi $1 $2 $3 $4 $5 $6 $7 $8 $9
