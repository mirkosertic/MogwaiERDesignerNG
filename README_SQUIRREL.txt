
 
     Readme for MogwaiERDesignerNG Squirrel Plugin
     
Requirements

This Plugin has been tested with SquirrelSQL 2.6.8 and higher    
     
Installation

Copy the contents of the plugin directory of the distribution
to the plugins directory of your squirrel installation, including
all jars and other files and directories

Copy the contents of the lib directory of the distribution
to the lib directory of your squirrel installation, including
all jars and other files and directories

If you are using the full installation of Squirrel, there may
be some .jar files already existing in the lib directory, but with
a different version:

form*.jar
hibernate*.jar
commons-*.jar

In such a case, please do not copy these files from the
Mogwai plugin package, and use the existing ones.

Usage

The database browser has a new context menu entry for catalog and
schema objects called "Start MogwaiERDesigner". After clicking this
entry, the plugin will create a new Mogwai Database Diagram View and
will start the reverse engineering process. After the reverse enginnering
is finished, you can view, format, modify and export your database
as XML, PNG, GIF, JPEG, SVG.

Upgrade

Please make sure hat before installing a new version of Mogwai ERDesigner NG
into Squirrel, every jar added by the older installation is removed and replaced
with the newer version!

Have fun!

For problems and comments, please use the issue tracker hosted at
http://sourceforge.net/projects/mogwai. Thank you for using Mogwai
ERDesigner