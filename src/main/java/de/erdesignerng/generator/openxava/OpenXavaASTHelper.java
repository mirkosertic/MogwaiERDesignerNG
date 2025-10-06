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

    public static MethodDeclaration findMethodDeclaration(final String aName, final ClassOrInterfaceDeclaration aType) {
        if (aType.getMembers() != null) {
            for (final BodyDeclaration theBody : aType.getMembers()) {
                if (theBody instanceof MethodDeclaration) {
                    final MethodDeclaration theMethod = (MethodDeclaration) theBody;
                    if (theMethod.getName().equals(aName)) {
                        return theMethod;
                    }
                }
            }
        }
        return null;
    }

    public static void addAnnotationTo(final BodyDeclaration aDeclaration, final AnnotationExpr aAnnotation) {
        if (aDeclaration.getAnnotations() != null) {
            aDeclaration.getAnnotations().add(aAnnotation);
        } else {
            final List<AnnotationExpr> theExpressions = new ArrayList<>();
            theExpressions.add(aAnnotation);
            aDeclaration.setAnnotations(theExpressions);
        }
    }

    public static void removeAnnotationFrom(final String aName, final BodyDeclaration aDeclaration) {
        if (aDeclaration.getAnnotations() != null) {
            for (final AnnotationExpr theExpression : aDeclaration.getAnnotations()) {
                if (theExpression instanceof MarkerAnnotationExpr) {
                    final MarkerAnnotationExpr theMarker = (MarkerAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        aDeclaration.getAnnotations().remove(theMarker);
                        return;
                    }
                }
                if (theExpression instanceof NormalAnnotationExpr) {
                    final NormalAnnotationExpr theAnnotation = (NormalAnnotationExpr) theExpression;
                    if (theAnnotation.getName().getName().equals(aName)) {
                        aDeclaration.getAnnotations().remove(theAnnotation);
                        return;
                    }
                }
                if (theExpression instanceof SingleMemberAnnotationExpr) {
                    final SingleMemberAnnotationExpr theAnnotation = (SingleMemberAnnotationExpr) theExpression;
                    if (theAnnotation.getName().getName().equals(aName)) {
                        aDeclaration.getAnnotations().remove(theAnnotation);
                        return;
                    }
                }
            }
        }
    }

    public static boolean hasAnnotation(final String aName, final BodyDeclaration aDeclaration) {
        if (aDeclaration.getAnnotations() != null) {
            for (final AnnotationExpr theExpression : aDeclaration.getAnnotations()) {
                if (theExpression instanceof MarkerAnnotationExpr) {
                    final MarkerAnnotationExpr theMarker = (MarkerAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        return true;
                    }
                }
                if (theExpression instanceof NormalAnnotationExpr) {
                    final NormalAnnotationExpr theAnnotation = (NormalAnnotationExpr) theExpression;
                    if (theAnnotation.getName().getName().equals(aName)) {
                        return true;
                    }
                }
                if (theExpression instanceof SingleMemberAnnotationExpr) {
                    final SingleMemberAnnotationExpr theAnnotation = (SingleMemberAnnotationExpr) theExpression;
                    if (theAnnotation.getName().getName().equals(aName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static MarkerAnnotationExpr addMarkerAnnotationTo(final String aName, final BodyDeclaration aDeclaration) {

        MarkerAnnotationExpr theAnnotation = null;
        if (aDeclaration.getAnnotations() != null) {
            for (final AnnotationExpr theExpression : aDeclaration.getAnnotations()) {
                if (theExpression instanceof MarkerAnnotationExpr) {
                    final MarkerAnnotationExpr theMarker = (MarkerAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        theAnnotation = theMarker;
                    }
                }
            }
        }
        if (theAnnotation == null) {
            theAnnotation = new MarkerAnnotationExpr(ASTHelper.createNameExpr(aName));
            addAnnotationTo(aDeclaration, theAnnotation);
        }

        return theAnnotation;
    }

    public static SingleMemberAnnotationExpr addSingleMemberAnnotationTo(final String aName, final Expression aExpression,
                                                                         final BodyDeclaration aDeclaration) {

        SingleMemberAnnotationExpr theAnnotation = null;
        if (aDeclaration.getAnnotations() != null) {
            for (final AnnotationExpr theExpression : aDeclaration.getAnnotations()) {
                if (theExpression instanceof SingleMemberAnnotationExpr) {
                    final SingleMemberAnnotationExpr theMarker = (SingleMemberAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        theAnnotation = theMarker;
                    }
                }
            }
        }
        if (theAnnotation == null) {
            theAnnotation = new SingleMemberAnnotationExpr(ASTHelper.createNameExpr(aName), aExpression);
            addAnnotationTo(aDeclaration, theAnnotation);
        } else {
            theAnnotation.setMemberValue(aExpression);
        }

        return theAnnotation;
    }

    public static NormalAnnotationExpr overwriteNormalAnnotation(final String aName, final List<MemberValuePair> aValues,
                                                                 final BodyDeclaration aDeclaration) {
        removeAnnotationFrom(aName, aDeclaration);
        return addNormalAnnotationTo(aName, aValues, aDeclaration);
    }

    public static NormalAnnotationExpr addNormalAnnotationTo(final String aName, final List<MemberValuePair> aValues,
                                                             final BodyDeclaration aDeclaration) {

        NormalAnnotationExpr theAnnotation = null;
        if (aDeclaration.getAnnotations() != null) {
            for (final AnnotationExpr theExpression : aDeclaration.getAnnotations()) {
                if (theExpression instanceof NormalAnnotationExpr) {
                    final NormalAnnotationExpr theMarker = (NormalAnnotationExpr) theExpression;
                    if (theMarker.getName().getName().equals(aName)) {
                        theAnnotation = theMarker;
                    }
                }
            }
        }
        if (theAnnotation == null) {
            theAnnotation = new NormalAnnotationExpr(ASTHelper.createNameExpr(aName), aValues);
            addAnnotationTo(aDeclaration, theAnnotation);
        } else {
            theAnnotation.setPairs(aValues);
        }

        return theAnnotation;
    }

    public static FieldDeclaration findFieldDeclaration(final String aFieldName, final ClassOrInterfaceDeclaration aType) {
        if (aType.getMembers() != null) {
            for (final BodyDeclaration theBody : aType.getMembers()) {
                if (theBody instanceof FieldDeclaration) {
                    final FieldDeclaration theField = (FieldDeclaration) theBody;
                    for (final VariableDeclarator theDeclaration : theField.getVariables()) {
                        if (aFieldName.equals(theDeclaration.getId().getName())) {
                            return theField;
                        }
                    }
                }
            }
        }
        return null;
    }
}
