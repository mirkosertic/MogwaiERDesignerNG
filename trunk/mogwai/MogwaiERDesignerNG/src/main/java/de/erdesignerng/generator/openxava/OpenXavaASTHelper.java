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
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;

import java.util.ArrayList;
import java.util.List;

public final class OpenXavaASTHelper {
    
    private OpenXavaASTHelper() {
    }

    public static MethodDeclaration findMethodDeclaration(String aName, ClassOrInterfaceDeclaration aType) {
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

    public static void addAnnotationTo(BodyDeclaration aDecl, AnnotationExpr aAnnotation) {
        if (aDecl.getAnnotations() != null) {
            aDecl.getAnnotations().add(aAnnotation);
        } else {
            List<AnnotationExpr> theExpressions = new ArrayList<AnnotationExpr>();
            theExpressions.add(aAnnotation);
            aDecl.setAnnotations(theExpressions);
        }
    }

    public static void removeAnnotatiomFrom(String aName, BodyDeclaration aDecl) {
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

    public static boolean hasAnnotation(String aName, BodyDeclaration aDecl) {
        if (aDecl.getAnnotations() != null) {
            for (AnnotationExpr theExpression : aDecl.getAnnotations()) {
                if (theExpression instanceof MarkerAnnotationExpr) {
                    MarkerAnnotationExpr theMarker = (MarkerAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        return true;
                    }
                }
                if (theExpression instanceof NormalAnnotationExpr) {
                    NormalAnnotationExpr theAnnotation = (NormalAnnotationExpr) theExpression;
                    if (theAnnotation.getName().getName().equals(aName)) {
                        return true;
                    }
                }
                if (theExpression instanceof SingleMemberAnnotationExpr) {
                    SingleMemberAnnotationExpr theAnnotation = (SingleMemberAnnotationExpr) theExpression;
                    if (theAnnotation.getName().getName().equals(aName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static MarkerAnnotationExpr addMarkerAnnotationTo(String aName, BodyDeclaration aDecl) {

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

    public static SingleMemberAnnotationExpr addSingleMemberAnnotationTo(String aName, Expression aExpression,
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
    
    public static NormalAnnotationExpr overwriteNormalAnnotation(String aName, List<MemberValuePair> aValues,
            BodyDeclaration aDecl) {
        removeAnnotatiomFrom(aName, aDecl);
        return addNormalAnnotationTo(aName, aValues, aDecl);
    }


    public static NormalAnnotationExpr addNormalAnnotationTo(String aName, List<MemberValuePair> aValues,
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

    public static FieldDeclaration findFieldDeclaration(String aFieldName, ClassOrInterfaceDeclaration aType) {
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
