#!/bin/sh
cd `dirname $0`  
java -Djava.library.path=./java3d/linux64 -cp ./lib/mogwai-smartstart-1.0.jar de.mogwai.smartstart.SmartStart de.erdesignerng.visual.ERDesigner lib
