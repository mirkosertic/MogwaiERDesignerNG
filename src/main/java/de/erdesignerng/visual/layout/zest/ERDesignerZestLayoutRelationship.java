package de.erdesignerng.visual.layout.zest;

import org.eclipse.mylyn.zest.layouts.LayoutBendPoint;
import org.eclipse.mylyn.zest.layouts.LayoutRelationship;
import org.eclipse.mylyn.zest.layouts.constraints.LayoutConstraint;
import org.eclipse.mylyn.zest.layouts.dataStructures.BendPoint;

public class ERDesignerZestLayoutRelationship implements LayoutRelationship {

    private ERDesignerZestLayoutEntity destinationInLayout;
    private ERDesignerZestLayoutEntity sourceInLayout;

    private Object layoutInformation;
    
    private LayoutBendPoint[] bendPoints;
    
    public void populateLayoutConstraint(LayoutConstraint aConstraints) {
    }

    /**
     * @return the bendPoints
     */
    public LayoutBendPoint[] getBendPoints() {
        return bendPoints;
    }

    /**
     * @param bendPoints the bendPoints to set
     */
    public void setBendPoints(LayoutBendPoint[] bendPoints) {
        this.bendPoints = bendPoints;
    }

    /**
     * @return the destinationInLayout
     */
    public ERDesignerZestLayoutEntity getDestinationInLayout() {
        return destinationInLayout;
    }

    /**
     * @param destinationInLayout the destinationInLayout to set
     */
    public void setDestinationInLayout(ERDesignerZestLayoutEntity destinationInLayout) {
        this.destinationInLayout = destinationInLayout;
    }

    /**
     * @return the layoutInformation
     */
    public Object getLayoutInformation() {
        return layoutInformation;
    }

    /**
     * @param layoutInformation the layoutInformation to set
     */
    public void setLayoutInformation(Object layoutInformation) {
        this.layoutInformation = layoutInformation;
    }

    /**
     * @return the sourceInLayout
     */
    public ERDesignerZestLayoutEntity getSourceInLayout() {
        return sourceInLayout;
    }

    /**
     * @param sourceInLayout the sourceInLayout to set
     */
    public void setSourceInLayout(ERDesignerZestLayoutEntity sourceInLayout) {
        this.sourceInLayout = sourceInLayout;
    }

    public void clearBendPoints() {
        bendPoints = new BendPoint[0];
    }
}
