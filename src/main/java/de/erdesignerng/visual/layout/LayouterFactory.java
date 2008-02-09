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
package de.erdesignerng.visual.layout;

import de.erdesignerng.visual.layout.graphviz.GraphvizLayout;
import de.erdesignerng.visual.layout.jung.JungFRLayouter;
import de.erdesignerng.visual.layout.zest.GridZestLayouter;
import de.erdesignerng.visual.layout.zest.RadialZestLayouter;
import de.erdesignerng.visual.layout.zest.SpringZestLayouter;
import de.erdesignerng.visual.layout.zest.TreeZestLayouter;

public final class LayouterFactory {

    private static LayouterFactory me;

    private LayouterFactory() {
    }

    public static LayouterFactory getInstance() {
        if (me == null) {
            me = new LayouterFactory();
        }
        return me;
    }

    public Layouter createGraphvizLayouter() {
        return new GraphvizLayout();
    }
    
    public Layouter createRadialLayouter() {
        return new RadialZestLayouter();
    }
    
    public Layouter createSpringLayouter() {
        return new SpringZestLayouter();
    }

    public Layouter createGridLayouter() {
        return new GridZestLayouter();
    }

    public Layouter createTreeLayouter() {
        return new TreeZestLayouter();
    }

    public Layouter createFRLayouter() {
        return new JungFRLayouter();
    }
}
