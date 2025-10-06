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
package de.erdesignerng.visual.java3d;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 * Helper Class.
 */
public final class Helper {

    private Helper() {
    }

    public static BranchGroup addElementAt(final Node aShape, final Vector3f aTranslation, final float aScale) {

        final BranchGroup theGroup = new BranchGroup();
        theGroup.setCapability(BranchGroup.ALLOW_DETACH);

        final Transform3D theTransform = new Transform3D();
        theTransform.setTranslation(aTranslation);
        theTransform.setScale(aScale);
        final TransformGroup theTransformGroup = new TransformGroup(theTransform);
        theTransformGroup.addChild(aShape);

        theGroup.addChild(theTransformGroup);

        return theGroup;
    }

}
