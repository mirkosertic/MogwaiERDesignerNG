#!/bin/sh
cd `dirname $0`  
java -Djava.library.path=./java3d/macos-universal -cp ./lib/mogwai-smartstart-1.1.jar de.mogwai.smartstart.SmartStart de.erdesignerng.visual.ERDesigner lib
