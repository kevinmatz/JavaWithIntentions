#!/bin/bash
./make-lexer-grammar.sh
./make-parser-grammar.sh
./make-javac.sh
# ./run-parser.bat rsrc/test/TestInput1.txt
./run-precompiler-on-flashcard-demo.sh
