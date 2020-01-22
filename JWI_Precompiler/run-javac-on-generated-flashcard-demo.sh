#!/bin/bash
cd tmp/generated_java
javac
jwidemos/flashcardtrainer/goals/*.java jwidemos/flashcardtrainer/requirements/abstract_/*.java jwidemos/flashcardtrainer/requirements/*.java jwidemos/flashcardtrainer/intentions/abstract_/*.java jwidemos/flashcardtrainer/intentions/*.java jwidemos/flashcardtrainer/*.java
cd ../..
