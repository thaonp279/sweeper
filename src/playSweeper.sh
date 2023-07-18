#!/bin/sh

DIRM=`pwd`

DIRM_L="$DIRM/../src/libs"

SAT4J_DIR="$DIRM_L/sat4j-pb.jar"

LOGICNG_DIR="$DIRM_L/logicng-2.2.0.jar"

ANTLR_DIR="$DIRM_L/antlr-runtime-4.8.jar"

UTILS="$DIRM_L/commons-lang3-3.12.0.jar"


#Add paths to other libraries here

CLASSPATH=".:$CLASSPATH:$DIRM:$DIRM_L:$SAT4J_DIR:$LOGICNG_DIR:$ANTLR_DIR:$UTILS"

export CLASSPATH

javac main/*.java

java main.A2main $*
