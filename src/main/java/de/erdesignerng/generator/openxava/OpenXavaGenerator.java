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

import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.Table;

public class OpenXavaGenerator {

    private static final String JAVA_ENCODING = "UTF-8";

    private static final Logger LOGGER = Logger.getLogger(OpenXavaGenerator.class);

    public OpenXavaGenerator() {
    }

    private CompilationUnit createNewCompilationUnit(String aPackageName, String aMainTypeName) throws IOException {
        CompilationUnit theUnit = new CompilationUnit();
        if (!StringUtils.isEmpty(aPackageName)) {
            theUnit.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(aPackageName)));
        }
        List<ImportDeclaration> theImports = new ArrayList<ImportDeclaration>();
        theImports.add(new ImportDeclaration(ASTHelper.createNameExpr("java.util"), false, true));
        theImports.add(new ImportDeclaration(ASTHelper.createNameExpr("javax.persistence"), false, true));
        theImports.add(new ImportDeclaration(ASTHelper.createNameExpr("org.openxava.annotations"), false, true));
        theUnit.setImports(theImports);
        ClassOrInterfaceDeclaration theType = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC, false, aMainTypeName);
        ASTHelper.addTypeDeclaration(theUnit, theType);
        return theUnit;
    }

    private CompilationUnit createCompilationUnitFromFile(File aFile) throws IOException {
        FileInputStream theStream = null;
        try {
            theStream = new FileInputStream(aFile);
            return JavaParser.parse(theStream);
        } catch (ParseException e) {
            throw new IOException(e);
        } finally {
            if (theStream != null) {
                theStream.close();
            }
        }
    }

    public void generate(Model aModel, OpenXavaOptions aOptions) throws IOException {

        String thePackageName = aOptions.getPackageName();

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

        for (Table theTable : aModel.getTables()) {
            String theTableClassName = aOptions.createTableName(theTable.getName());

            LOGGER.info("Processing generated code for " + theTableClassName);

            File theTargetFile = new File(theTargetDirectory, theTableClassName + ".java");
            CompilationUnit theUnit = null;
            if (!theTargetFile.exists()) {

                LOGGER.info("Creating new file for " + theTableClassName + " at " + theTargetFile);

                theUnit = createNewCompilationUnit(thePackageName, theTableClassName);
            } else {
                theUnit = createCompilationUnitFromFile(theTargetFile);
            }

            updateCompilationUnit(aModel, aOptions, theTable, theUnit);

            PrintWriter theWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(theTargetFile),
                    JAVA_ENCODING));
            theWriter.print(theUnit.toString());
            theWriter.close();
        }
    }

    private void updateCompilationUnit(Model aModel, OpenXavaOptions aOptions, Table aTable, CompilationUnit aUnit) {

        ClassOrInterfaceDeclaration theType = null;
        for (TypeDeclaration theDecl : aUnit.getTypes()) {
            if (theDecl instanceof ClassOrInterfaceDeclaration) {
                theType = (ClassOrInterfaceDeclaration) theDecl;
                break;
            }
        }

        if (theType == null) {
            throw new RuntimeException("Cannot find main type");
        }
        
        List<FieldDeclaration> theFields = new ArrayList<FieldDeclaration>();
        
        addMarkerAnnotationTo("Entity", theType);

        for (Attribute theAttribute : aTable.getAttributes()) {

            String theFieldName = aOptions.createFieldName(theAttribute.getName());
            if (!theAttribute.isForeignKey()) {

                DataType theDataType = theAttribute.getDatatype();
                OpenXavaTypeMap theMap = aOptions.getTypeMapping().get(theDataType);

                String theJavaType = theMap.getJavaType();
                String theStereoType = theMap.getStereoType();

                FieldDeclaration theDecl = findFieldDeclaration(theFieldName, theType);
                if (theDecl == null) {
                    theDecl = ASTHelper.createFieldDeclaration(ModifierSet.PRIVATE, new ClassOrInterfaceType(
                            theJavaType), theFieldName);
                    if (theType.getMembers() != null) {
                        theType.getMembers().add(theDecl);
                    } else {
                        List<BodyDeclaration> theMembers = new ArrayList<BodyDeclaration>();
                        theMembers.add(theDecl);
                        theType.setMembers(theMembers);
                    }
                } else {
                    theDecl.setType(new ClassOrInterfaceType(theJavaType));
                }
                
                theFields.add(theDecl);

                if (theAttribute.isPrimaryKey()) {
                    addMarkerAnnotationTo("Id", theDecl);
                } else {
                    removeAnnotatiomFrom("Id", theDecl);
                }
                if (!theAttribute.isNullable()) {
                    addMarkerAnnotationTo("Required", theDecl);
                } else {
                    removeAnnotatiomFrom("Required", theDecl);
                }
                if (!StringUtils.isEmpty(theStereoType)) {
                    addSingleMemberAnnotationTo("Stereotype", new StringLiteralExpr(theStereoType), theDecl);
                } else {
                    removeAnnotatiomFrom("Stereotype", theDecl);
                }

                List<MemberValuePair> theValues = new ArrayList<MemberValuePair>();
                theValues.add(new MemberValuePair("name", new StringLiteralExpr(theAttribute.getName())));

                if (theDataType.supportsSize()) {
                    theValues.add(new MemberValuePair("length", new IntegerLiteralExpr(Integer.toString(theAttribute
                            .getSize()))));
                }
                if (theDataType.supportsFraction()) {
                    theValues.add(new MemberValuePair("precision", new IntegerLiteralExpr(Integer.toString(theAttribute
                            .getFraction()))));
                }
                if (theDataType.supportsScale()) {
                    theValues.add(new MemberValuePair("scale", new IntegerLiteralExpr(Integer.toString(theAttribute
                            .getScale()))));
                }
                if (!theAttribute.isNullable()) {
                    theValues.add(new MemberValuePair("nullable", new BooleanLiteralExpr(false)));
                } else {
                    theValues.add(new MemberValuePair("nullable", new BooleanLiteralExpr(false)));
                }
                addNormalAnnotationTo("Column", theValues, theDecl);
            }
        }

        // Generate the associations
        for (Relation theRelation : aModel.getRelations().getExportedKeysFor(aTable)) {

            if (theRelation.getMapping().size() == 1
                    && !theRelation.getExportingTable().equals(theRelation.getImportingTable())) {
                Table theImportingTable = theRelation.getImportingTable();

                String theImpFieldName = aOptions.createFieldName(theImportingTable.getName());
                String theImpTableClassName = aOptions.createTableName(theImportingTable.getName());

                FieldDeclaration theDecl = findFieldDeclaration(theImpFieldName, theType);
                if (theDecl == null) {
                    ClassOrInterfaceType theRelationType = new ClassOrInterfaceType("Set");
                    List<Type> theTypeArgs = new ArrayList<Type>();
                    theTypeArgs.add(new ClassOrInterfaceType(theImpTableClassName));
                    theRelationType.setTypeArgs(theTypeArgs);
                    theDecl = ASTHelper.createFieldDeclaration(ModifierSet.PRIVATE, theRelationType, theImpFieldName);
                    if (theType.getMembers() != null) {
                        theType.getMembers().add(theDecl);
                    } else {
                        List<BodyDeclaration> theMembers = new ArrayList<BodyDeclaration>();
                        theMembers.add(theDecl);
                        theType.setMembers(theMembers);
                    }
                } else {
                    ClassOrInterfaceType theRelationType = new ClassOrInterfaceType("Set");
                    List<Type> theTypeArgs = new ArrayList<Type>();
                    theTypeArgs.add(new ClassOrInterfaceType(theImpTableClassName));
                    theRelationType.setTypeArgs(theTypeArgs);
                    theDecl.setType(theRelationType);
                }
                
                theFields.add(theDecl);

                addMarkerAnnotationTo("OneToMany", theDecl);
            } else {
                LOGGER.warn("Ignoring relation " + theRelation.getName()
                        + " is there are either more than one mapping attribute or it is a self reference");
            }
        }
        
        for (FieldDeclaration theField : theFields) {
            
            String theName = theField.getVariables().get(0).getId().getName();
            String thePropertyName = aOptions.createPropertyName(theName);
            
            boolean isBoolean = "boolean".equals(theField.getType().toString());
            
            MethodDeclaration theSetMethod = findMethodDeclaration("set"+thePropertyName, theType);
            if (theSetMethod == null) {
                //TODO: Generate code here
            }
            
            MethodDeclaration theGetMethod = null;
            if (isBoolean) {
                theGetMethod = findMethodDeclaration("is"+thePropertyName, theType);
            } else {
                theGetMethod = findMethodDeclaration("get"+thePropertyName, theType);
            }
            
            if (theGetMethod == null) {
                // TODO: Generate code here
            }
            
        }
    }

    private MethodDeclaration findMethodDeclaration(String aName, ClassOrInterfaceDeclaration aType) {
        if (aType.getMembers() != null) {
            for (BodyDeclaration theBody : aType.getMembers()) {
                if (theBody instanceof MethodDeclaration) {
                    MethodDeclaration theMethod = (MethodDeclaration) theBody;
                    if (theMethod.getName().equals(aName)) {
                        return theMethod;
                    }
                }
            }
        }
        return null;
    }

    private void addAnnotationTo(BodyDeclaration aDecl, AnnotationExpr aAnnotation) {
        if (aDecl.getAnnotations() != null) {
            aDecl.getAnnotations().add(aAnnotation);
        } else {
            List<AnnotationExpr> theExpressions = new ArrayList<AnnotationExpr>();
            theExpressions.add(aAnnotation);
            aDecl.setAnnotations(theExpressions);
        }
    }

    private void removeAnnotatiomFrom(String aName, BodyDeclaration aDecl) {
        if (aDecl.getAnnotations() != null) {
            for (AnnotationExpr theExpression : aDecl.getAnnotations()) {
                if (theExpression instanceof MarkerAnnotationExpr) {
                    MarkerAnnotationExpr theMarker = (MarkerAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        aDecl.getAnnotations().remove(theMarker);
                        return;
                    }
                }
                if (theExpression instanceof NormalAnnotationExpr) {
                    NormalAnnotationExpr theAnnotation = (NormalAnnotationExpr) theExpression;
                    if (theAnnotation.getName().getName().equals(aName)) {
                        aDecl.getAnnotations().remove(theAnnotation);
                        return;
                    }
                }
                if (theExpression instanceof SingleMemberAnnotationExpr) {
                    SingleMemberAnnotationExpr theAnnotation = (SingleMemberAnnotationExpr) theExpression;
                    if (theAnnotation.getName().getName().equals(aName)) {
                        aDecl.getAnnotations().remove(theAnnotation);
                        return;
                    }
                }
            }
        }
    }

    private MarkerAnnotationExpr addMarkerAnnotationTo(String aName, BodyDeclaration aDecl) {

        MarkerAnnotationExpr theAnnotation = null;
        if (aDecl.getAnnotations() != null) {
            for (AnnotationExpr theExpression : aDecl.getAnnotations()) {
                if (theExpression instanceof MarkerAnnotationExpr) {
                    MarkerAnnotationExpr theMarker = (MarkerAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        theAnnotation = theMarker;
                    }
                }
            }
        }
        if (theAnnotation == null) {
            MarkerAnnotationExpr theExpression = new MarkerAnnotationExpr(ASTHelper.createNameExpr(aName));
            addAnnotationTo(aDecl, theExpression);
        }

        return theAnnotation;
    }

    private SingleMemberAnnotationExpr addSingleMemberAnnotationTo(String aName, Expression aExpression,
            BodyDeclaration aDecl) {

        SingleMemberAnnotationExpr theAnnotation = null;
        if (aDecl.getAnnotations() != null) {
            for (AnnotationExpr theExpression : aDecl.getAnnotations()) {
                if (theExpression instanceof SingleMemberAnnotationExpr) {
                    SingleMemberAnnotationExpr theMarker = (SingleMemberAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        theAnnotation = theMarker;
                    }
                }
            }
        }
        if (theAnnotation == null) {
            SingleMemberAnnotationExpr theExpression = new SingleMemberAnnotationExpr(ASTHelper.createNameExpr(aName),
                    aExpression);
            addAnnotationTo(aDecl, theExpression);
        } else {
            theAnnotation.setMemberValue(aExpression);
        }

        return theAnnotation;
    }

    private NormalAnnotationExpr addNormalAnnotationTo(String aName, List<MemberValuePair> aValues,
            BodyDeclaration aDecl) {

        NormalAnnotationExpr theAnnotation = null;
        if (aDecl.getAnnotations() != null) {
            for (AnnotationExpr theExpression : aDecl.getAnnotations()) {
                if (theExpression instanceof NormalAnnotationExpr) {
                    NormalAnnotationExpr theMarker = (NormalAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        theAnnotation = theMarker;
                    }
                }
            }
        }
        if (theAnnotation == null) {
            NormalAnnotationExpr theExpression = new NormalAnnotationExpr(ASTHelper.createNameExpr(aName), aValues);
            addAnnotationTo(aDecl, theExpression);
        } else {
            theAnnotation.setPairs(aValues);
        }

        return theAnnotation;
    }
    
    private FieldDeclaration findFieldDeclaration(String aFieldName, ClassOrInterfaceDeclaration aType) {
        if (aType.getMembers() != null) {
            for (BodyDeclaration theBody : aType.getMembers()) {
                if (theBody instanceof FieldDeclaration) {
                    FieldDeclaration theField = (FieldDeclaration) theBody;
                    for (VariableDeclarator theDecl : theField.getVariables()) {
                        if (aFieldName.equals(theDecl.getId().getName())) {
                            return theField;
                        }
                    }
                }
            }
        }
        return null;
    }
}