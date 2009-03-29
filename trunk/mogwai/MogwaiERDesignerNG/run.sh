#!/bin/sh
cd `dirname $0`  
java -cp ./lib/mogwai-smartstart-1.0.jar de.mogwai.smartstart.SmartStart de.erdesignerng.visual.ERDesigner lib
