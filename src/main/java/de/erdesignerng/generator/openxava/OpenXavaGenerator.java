/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.generator.openxava;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

public class OpenXavaGenerator {

    private static final String JAVA_ENCODING = "UTF-8";

    private static final String START_MARKER = "//START_OF_GENERATED_CODE";

    private static final String END_MARKER = "//END_OF_GENERATED_CODE";

    private static final Logger LOGGER = Logger.getLogger(OpenXavaGenerator.class);

    public OpenXavaGenerator() {
    }

    public void generate(Model aModel, String aPackageName, OpenXavaOptions aOptions, File aDirectory)
            throws IOException {

        File theTargetDirectory = aDirectory;
        if (!StringUtils.isEmpty(aPackageName)) {
            theTargetDirectory = new File(aDirectory, aPackageName.replace('.', File.separatorChar));
        }

        if (theTargetDirectory.exists()) {
            LOGGER.info("Target directory " + theTargetDirectory + " exists");
        } else {
            LOGGER.info("Target directory " + theTargetDirectory + " will be created");
            theTargetDirectory.mkdirs();
        }

        for (Table theTable : aModel.getTables()) {
            String theTableClassName = aOptions.createTableName(theTable.getName());

            LOGGER.info("Processing generated code for " + theTableClassName);

            StringWriter theStringWriter = new StringWriter();
            createGeneratedCode(aModel, aOptions, theTable, new PrintWriter(theStringWriter));

            File theTargetFile = new File(theTargetDirectory, theTableClassName + ".java");
            if (!theTargetFile.exists()) {

                LOGGER.info("Creating new file for " + theTableClassName + " at " + theTargetFile);

                PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(theTargetFile),
                        JAVA_ENCODING));

                if (!StringUtils.isEmpty(aPackageName)) {
                    theWriter.println("package " + aPackageName + ";");
                    theWriter.println();
                }

                theWriter.println("import java.util.*;");
                theWriter.println("import javax.persistence.*;");
                theWriter.println("import org.openxava.annotations.*;");
                theWriter.println("");
                theWriter.println("@Entity(name=\"" + theTable.getName() + "\")");
                theWriter.println("public class " + theTableClassName + " {");

                theWriter.println("     " + START_MARKER);
                theWriter.print(theStringWriter.toString());
                theWriter.println("     " + END_MARKER);

                theWriter.println("}");
                theWriter.close();
            } else {

                LOGGER.info("Updating file for " + theTableClassName + " at " + theTargetFile);

                String theCurrentFile = IOUtils.toString(new FileReader(theTargetFile));
                int p = theCurrentFile.indexOf(START_MARKER);
                if (p < 0) {
                    throw new RuntimeException("Cannot find start marker for generated code in " + theTargetFile);
                }
                int p2 = theCurrentFile.indexOf(END_MARKER);
                if (p2 < 0) {
                    throw new RuntimeException("Cannot find end marker for generated code in " + theTargetFile);
                }
                StringWriter theTemp = new StringWriter();

                PrintWriter theTempWriter = new PrintWriter(theTemp);
                theTempWriter.println(theCurrentFile.substring(0, p + START_MARKER.length()));
                theTempWriter.print(theStringWriter.toString());
                theTempWriter.print("     ");
                theTempWriter.print(theCurrentFile.substring(p2));
                theTempWriter.close();

                PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(theTargetFile),
                        JAVA_ENCODING));
                theWriter.print(theTemp.toString());
                theWriter.close();
            }
        }
    }

    private void createGeneratedCode(Model aModel, OpenXavaOptions aOptions, Table theTable, PrintWriter theWriter) {
        for (Attribute theAttribute : theTable.getAttributes()) {

            String theFieldName = aOptions.createFieldName(theAttribute.getName());
            if (!theAttribute.isForeignKey()) {

                if (theAttribute.isPrimaryKey()) {
                    theWriter.println("     @Id");
                }

                DataType theDataType = theAttribute.getDatatype();
                String theJavaType = aOptions.createJavaType(theDataType);

                String theColDef = "name=\"" + theAttribute.getName() + "\"";
                if (theDataType.supportsSize()) {
                    theColDef += ",length=" + theAttribute.getSize();
                }
                if (theDataType.supportsFraction()) {
                    theColDef += ",precision=" + theAttribute.getFraction();
                }
                if (theDataType.supportsScale()) {
                    theColDef += ",scale=" + theAttribute.getScale();
                }
                if (!theAttribute.isNullable()) {
                    theWriter.println("     @Required");
                    theColDef += ",nullable=false";
                } else {
                    theColDef += ",nullable=true";
                }
                theWriter.println("     @Column(" + theColDef + ")");
                theWriter.println("     private " + theJavaType + " " + theFieldName + ";");
                theWriter.println();
            }
        }

        // Generate the associations
        /*
         * for (Relation theRelation :
         * aModel.getRelations().getForeignKeysFor(theTable)) { Table
         * theExportingTable = theRelation.getExportingTable();
         * 
         * String theFKFieldName =
         * aOptions.createFieldName(theExportingTable.getName()); String
         * theFKTableClassName =
         * aOptions.createTableName(theExportingTable.getName());
         * 
         * theWriter.println("     @ManyToOne"); theWriter.println("     " +
         * theFKTableClassName + " " + theFKFieldName + ";");
         * theWriter.println(); }
         */

        List<Relation> theRelations = new ArrayList<Relation>();
        
        for (Relation theRelation : aModel.getRelations().getExportedKeysFor(theTable)) {

            if (theRelation.getMapping().size() == 1
                    && !theRelation.getExportingTable().equals(theRelation.getImportingTable())) {
                Table theImportingTable = theRelation.getImportingTable();

                String theImpFieldName = aOptions.createFieldName(theImportingTable.getName());
                String theImpTableClassName = aOptions.createTableName(theImportingTable.getName());

                theWriter.println("     // Relation " + theRelation.getName() + " " + theRelation.getExportingTable()
                        + " -> " + theRelation.getImportingTable());
                theWriter.println("     @OneToMany");
                theWriter.println("     private Set<" + theImpTableClassName + "> " + theImpFieldName + " = new HashSet<"
                        + theImpTableClassName + ">();");
                theWriter.println();
                
                theRelations.add(theRelation);
            } else {
                LOGGER.warn("Ignoring relation " + theRelation.getName()
                        + " is there are either more than one mapping attribute or it is a self reference");
            }
        }

        // Generate the getter and setter
        for (Attribute theAttribute : theTable.getAttributes()) {

            String theFieldName = aOptions.createFieldName(theAttribute.getName());
            String theAttributeName = aOptions.createPropertyName(theAttribute.getName());

            if (!theAttribute.isForeignKey()) {

                String theJavaType = aOptions.createJavaType(theAttribute.getDatatype());
                theWriter.println("     public void set" + theAttributeName + "(" + theJavaType + " aValue) {");
                theWriter.println("         " + theFieldName + "=aValue;");
                theWriter.println("     }");
                theWriter.println();

                String theGetPrefix = "get";
                if ("boolean".equals(theJavaType)) {
                    theGetPrefix = "is";
                }
                theWriter.println("     public " + theJavaType + " " + theGetPrefix + theAttributeName + "() {");
                theWriter.println("         return " + theFieldName + ";");
                theWriter.println("     }");
                theWriter.println();

            }
        }

        for (Relation theRelation : theRelations) {

            Table theImportingTable = theRelation.getImportingTable();

            String theImpFieldName = aOptions.createFieldName(theImportingTable.getName());
            String theImpAttributeName = aOptions.createPropertyName(theImportingTable.getName());
            String theImpTableClassName = aOptions.createTableName(theImportingTable.getName());

            theWriter.println("     public Set<" + theImpTableClassName + "> get" + theImpAttributeName + "() {");
            theWriter.println("         return " + theImpFieldName + ";");
            theWriter.println("     }");
            theWriter.println();

            theWriter.println("     public void set" + theImpAttributeName + "(Set<" + theImpTableClassName
                    + "> aValue) {");
            theWriter.println("         " + theImpFieldName + " = aValue;");
            theWriter.println("     }");
            theWriter.println();
        }
    }
}
