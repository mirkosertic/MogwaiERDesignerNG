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
package de.erdesignerng.visual.layout.zest;

import org.eclipse.mylyn.zest.layouts.LayoutEntity;
import org.eclipse.mylyn.zest.layouts.constraints.LayoutConstraint;

import de.erdesignerng.visual.cells.TableCell;

public class ERDesignerLayoutEntity implements LayoutEntity {

    private double heightInLayout;

    private double widthInLayout;

    private double xInLayout;

    private double yInLayout;

    private Object layoutInformation;
    
    private TableCell cell;

    public void populateLayoutConstraint(LayoutConstraint aConstraint) {
    }

    public void setLocationInLayout(double aX, double aY) {
        xInLayout = aX;
        yInLayout = aY;
    }

    public void setSizeInLayout(double aWidth, double aHeight) {
        widthInLayout = aWidth;
        heightInLayout = aHeight;
    }

    public int compareTo(Object aObject) {
        return 0;
    }

    /**
     * @return the heightInLayout
     */
    public double getHeightInLayout() {
        return heightInLayout;
    }

    /**
     * @param heightInLayout
     *            the heightInLayout to set
     */
    public void setHeightInLayout(double heightInLayout) {
        this.heightInLayout = heightInLayout;
    }

    /**
     * @return the layoutInformation
     */
    public Object getLayoutInformation() {
        return layoutInformation;
    }

    /**
     * @param layoutInformation
     *            the layoutInformation to set
     */
    public void setLayoutInformation(Object layoutInformation) {
        this.layoutInformation = layoutInformation;
    }

    /**
     * @return the widthInLayout
     */
    public double getWidthInLayout() {
        return widthInLayout;
    }

    /**
     * @param widthInLayout
     *            the widthInLayout to set
     */
    public void setWidthInLayout(double widthInLayout) {
        this.widthInLayout = widthInLayout;
    }

    /**
     * @return the xInLayout
     */
    public double getXInLayout() {
        return xInLayout;
    }

    /**
     * @param inLayout
     *            the xInLayout to set
     */
    public void setXInLayout(double inLayout) {
        xInLayout = inLayout;
    }

    /**
     * @return the yInLayout
     */
    public double getYInLayout() {
        return yInLayout;
    }

    /**
     * @param inLayout
     *            the yInLayout to set
     */
    public void setYInLayout(double inLayout) {
        yInLayout = inLayout;
    }

    /**
     * @return the cellView
     */
    public TableCell getCell() {
        return cell;
    }

    /**
     * @param cellView the cellView to set
     */
    public void setCell(TableCell cellView) {
        this.cell = cellView;
    }
}
