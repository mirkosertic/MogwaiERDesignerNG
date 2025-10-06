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

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;
import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OpenXavaGenerator {

    private static final String JAVA_ENCODING = "UTF-8";

    private static final Logger LOGGER = Logger.getLogger(OpenXavaGenerator.class);

    public OpenXavaGenerator() {
    }

    private CompilationUnit createNewCompilationUnit(final String aPackageName, final String aMainTypeName) {
        final CompilationUnit theUnit = new CompilationUnit();
        if (!StringUtils.isEmpty(aPackageName)) {
            theUnit.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(aPackageName)));
        }
        final List<ImportDeclaration> theImports = new ArrayList<>();
        theImports.add(new ImportDeclaration(ASTHelper.createNameExpr("java.util"), false, true));
        theImports.add(new ImportDeclaration(ASTHelper.createNameExpr("javax.persistence"), false, true));
        theImports.add(new ImportDeclaration(ASTHelper.createNameExpr("org.openxava.annotations"), false, true));
        theUnit.setImports(theImports);
        final ClassOrInterfaceDeclaration theType = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC, false, aMainTypeName);
        ASTHelper.addTypeDeclaration(theUnit, theType);
        return theUnit;
    }

    private CompilationUnit createCompilationUnitFromFile(final File aFile) throws IOException {
        try (final FileInputStream theStream = new FileInputStream(aFile)) {
            return JavaParser.parse(theStream);
        } catch (final ParseException e) {
            throw new IOException(e);
        }
    }

    public void generate(final Model aModel, final OpenXavaOptions aOptions) throws IOException {

        final String thePackageName = aOptions.getPackageName();

        File theTargetDirectory = new File(aOptions.getSrcDirectory());
        if (!StringUtils.isEmpty(thePackageName)) {
            theTargetDirectory = new File(theTargetDirectory, thePackageName.replace('.', File.separatorChar));
        }

        if (theTargetDirectory.exists()) {
            LOGGER.info("Target directory " + theTargetDirectory + " exists");
        } else {
            LOGGER.info("Target directory " + theTargetDirectory + " will be created");
            theTargetDirectory.mkdirs();
        }

        for (final Table theTable : aModel.getTables()) {
            final String theTableClassName = aOptions.createTableName(theTable.getName());

            LOGGER.info("Processing generated code for " + theTableClassName);

            final File theTargetFile = new File(theTargetDirectory, theTableClassName + ".java");
            final CompilationUnit theUnit;
            if (!theTargetFile.exists()) {

                LOGGER.info("Creating new file for " + theTableClassName + " at " + theTargetFile);

                theUnit = createNewCompilationUnit(thePackageName, theTableClassName);
            } else {
                theUnit = createCompilationUnitFromFile(theTargetFile);
            }

            updateCompilationUnit(aModel, aOptions, theTable, theUnit);

            final PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(theTargetFile),
                    JAVA_ENCODING));
            theWriter.print(theUnit);
            theWriter.close();
        }
    }

    private void updateCompilationUnit(final Model aModel, final OpenXavaOptions aOptions, final Table aTable, final CompilationUnit aUnit) {

        ClassOrInterfaceDeclaration theType = null;
        for (final TypeDeclaration theDeclaration : aUnit.getTypes()) {
            if (theDeclaration instanceof ClassOrInterfaceDeclaration) {
                theType = (ClassOrInterfaceDeclaration) theDeclaration;
                break;
            }
        }

        if (theType == null) {
            throw new RuntimeException("Cannot find main type");
        }

        // Check the fields
        theType.accept(new VoidVisitorAdapter() {

            @Override
            public void visit(final FieldDeclaration aField, final Object aArg) {
                final ClassOrInterfaceDeclaration theClass = (ClassOrInterfaceDeclaration) aArg;
                if (aField.getVariables().size() != 1) {
                    throw new RuntimeException("Cannot parse Java Type : " + theClass.getName()
                            + ". Please make sure to declare only one field per statement!");
                }
            }
        }, theType);

        final List<FieldDeclaration> thePersistentFields = new ArrayList<>();

        OpenXavaASTHelper.addMarkerAnnotationTo("Entity", theType);

        for (final Attribute<Table> theAttribute : aTable.getAttributes()) {

            final String theFieldName = aOptions.createFieldName(theAttribute.getName());
            if (!theAttribute.isForeignKey()) {

                final DataType theDataType = theAttribute.getDatatype();
                final OpenXavaTypeMap theMap = aOptions.getTypeMapping().get(theDataType);

                final String theJavaType = aOptions.getJavaType(theDataType, theAttribute.isNullable(), theAttribute
                        .isPrimaryKey());

                final String theStereoType = theMap.getStereoType();

                FieldDeclaration theDeclaration = OpenXavaASTHelper.findFieldDeclaration(theFieldName, theType);
                if (theDeclaration == null) {
                    theDeclaration = ASTHelper.createFieldDeclaration(ModifierSet.PRIVATE, new ClassOrInterfaceType(
                            theJavaType), theFieldName);
                    ASTHelper.addMember(theType, theDeclaration);
                } else {
                    theDeclaration.setType(new ClassOrInterfaceType(theJavaType));
                }

                thePersistentFields.add(theDeclaration);

                if (theAttribute.isPrimaryKey()) {
                    OpenXavaASTHelper.addMarkerAnnotationTo("Id", theDeclaration);
                } else {
                    OpenXavaASTHelper.removeAnnotationFrom("Id", theDeclaration);
                }
                if (!theAttribute.isNullable()) {
                    OpenXavaASTHelper.addMarkerAnnotationTo("Required", theDeclaration);
                } else {
                    OpenXavaASTHelper.removeAnnotationFrom("Required", theDeclaration);
                }
                if (!StringUtils.isEmpty(theStereoType)) {
                    OpenXavaASTHelper.addSingleMemberAnnotationTo("Stereotype", new StringLiteralExpr(theStereoType),
                            theDeclaration);
                } else {
                    OpenXavaASTHelper.removeAnnotationFrom("Stereotype", theDeclaration);
                }

                final List<MemberValuePair> theValues = new ArrayList<>();
                theValues.add(new MemberValuePair("name", new StringLiteralExpr(theAttribute.getName())));

                if (theDataType.supportsSize() && theAttribute.getSize() != null) {
                    theValues.add(new MemberValuePair("length", new IntegerLiteralExpr(Integer.toString(theAttribute
                            .getSize()))));
                }
                if (theDataType.supportsFraction() && theAttribute.getFraction() != null) {
                    theValues.add(new MemberValuePair("precision", new IntegerLiteralExpr(Integer.toString(theAttribute
                            .getFraction()))));
                }
                if (theDataType.supportsScale()) {
                    theValues.add(new MemberValuePair("scale", new IntegerLiteralExpr(Integer.toString(theAttribute
                            .getScale()))));
                }
                if (!theAttribute.isNullable() && !theAttribute.isPrimaryKey()) {
                    theValues.add(new MemberValuePair("nullable", new BooleanLiteralExpr(false)));
                } else {
                    theValues.add(new MemberValuePair("nullable", new BooleanLiteralExpr(true)));
                }
                OpenXavaASTHelper.overwriteNormalAnnotation("Column", theValues, theDeclaration);
            }
        }

        // Generate the associations
        for (final Relation theRelation : aModel.getRelations().getExportedKeysFor(aTable)) {

            if (theRelation.getMapping().size() == 1
                    && !theRelation.getExportingTable().equals(theRelation.getImportingTable())) {
                final Table theImportingTable = theRelation.getImportingTable();

                final String theImpFieldName = aOptions.createFieldName(theImportingTable.getName());
                final String theImpTableClassName = aOptions.createTableName(theImportingTable.getName());

                FieldDeclaration theDeclaration = OpenXavaASTHelper.findFieldDeclaration(theImpFieldName, theType);
                if (theDeclaration == null) {
                    final ClassOrInterfaceType theRelationType = new ClassOrInterfaceType("Set");
                    final List<Type> theTypeArgs = new ArrayList<>();
                    theTypeArgs.add(new ClassOrInterfaceType(theImpTableClassName));
                    theRelationType.setTypeArgs(theTypeArgs);
                    theDeclaration = ASTHelper.createFieldDeclaration(ModifierSet.PRIVATE, theRelationType, theImpFieldName);
                    ASTHelper.addMember(theType, theDeclaration);
                } else {
                    final ClassOrInterfaceType theRelationType = new ClassOrInterfaceType("Set");
                    final List<Type> theTypeArgs = new ArrayList<>();
                    theTypeArgs.add(new ClassOrInterfaceType(theImpTableClassName));
                    theRelationType.setTypeArgs(theTypeArgs);
                    theDeclaration.setType(theRelationType);
                }

                thePersistentFields.add(theDeclaration);

                OpenXavaASTHelper.addMarkerAnnotationTo("OneToMany", theDeclaration);
            } else {
                LOGGER.warn("Ignoring relation " + theRelation.getName()
                        + " is there are either more than one mapping attribute or it is a self reference");
            }
        }

        if (theType.getMembers() != null) {

            final List<FieldDeclaration> theFieldsToRemove = new ArrayList<>();

            // Test if the field is marked as persistent and is not a
// persistent attribute
            theType.getMembers().stream().filter(theDeclaration -> theDeclaration instanceof FieldDeclaration).forEach(theDeclaration -> {

                final FieldDeclaration theField = (FieldDeclaration) theDeclaration;

                // Test if the field is marked as persistent and is not a
                // persistent attribute
                if (OpenXavaASTHelper.hasAnnotation("Column", theDeclaration) && !thePersistentFields.contains(theField)) {
                    theFieldsToRemove.add(theField);
                }
            });
            theType.getMembers().removeAll(theFieldsToRemove);

            // Remove the getter and setter for the removed fields
            final List<MethodDeclaration> theMethodsToRemove = new ArrayList<>();
            for (final FieldDeclaration theField : theFieldsToRemove) {

                final String theName = theField.getVariables().getFirst().getId().getName();
                final String thePropertyName = aOptions.createPropertyName(theName);

                final boolean isBoolean = "boolean".equals(theField.getType().toString());

                String theMethodName = "set" + thePropertyName;
                MethodDeclaration theMethod = OpenXavaASTHelper.findMethodDeclaration(theMethodName, theType);
                if (theMethod != null) {
                    theMethodsToRemove.add(theMethod);
                }
                if (isBoolean) {
                    theMethodName = "is" + thePropertyName;
                } else {
                    theMethodName = "get" + thePropertyName;
                }
                theMethod = OpenXavaASTHelper.findMethodDeclaration(theMethodName, theType);
                if (theMethod != null) {
                    theMethodsToRemove.add(theMethod);
                }
            }
            theType.getMembers().removeAll(theMethodsToRemove);
        }

        for (final FieldDeclaration theField : thePersistentFields) {

            final String theName = theField.getVariables().getFirst().getId().getName();
            final String thePropertyName = aOptions.createPropertyName(theName);

            final boolean isBoolean = "boolean".equals(theField.getType().toString());

            String theMethodName = "set" + thePropertyName;
            MethodDeclaration theSetMethod = OpenXavaASTHelper.findMethodDeclaration(theMethodName, theType);
            if (theSetMethod == null) {
                theSetMethod = new MethodDeclaration(ModifierSet.PUBLIC, ASTHelper.VOID_TYPE, theMethodName);
                final List<Parameter> theParams = new ArrayList<>();
                theParams.add(ASTHelper.createParameter(theField.getType(), "aValue"));
                theSetMethod.setParameters(theParams);

                final BlockStmt theBlock = new BlockStmt();
                ASTHelper.addStmt(theBlock, new AssignExpr(new NameExpr(theName), new NameExpr("aValue"),
                        Operator.assign));
                theSetMethod.setBody(theBlock);

                ASTHelper.addMember(theType, theSetMethod);
            } else {
                final List<Parameter> theParams = new ArrayList<>();
                theParams.add(ASTHelper.createParameter(theField.getType(), "aValue"));
                theSetMethod.setParameters(theParams);
            }

            if (isBoolean) {
                theMethodName = "is" + thePropertyName;
            } else {
                theMethodName = "get" + thePropertyName;
            }
            MethodDeclaration theGetMethod = OpenXavaASTHelper.findMethodDeclaration(theMethodName, theType);
            if (theGetMethod == null) {
                theGetMethod = new MethodDeclaration(ModifierSet.PUBLIC, theField.getType(), theMethodName);

                final BlockStmt theBlock = new BlockStmt();
                ASTHelper.addStmt(theBlock, new ReturnStmt(new NameExpr(theName)));
                theGetMethod.setBody(theBlock);

                ASTHelper.addMember(theType, theGetMethod);
            } else {
                theGetMethod.setType(theField.getType());
            }
        }
    }
}