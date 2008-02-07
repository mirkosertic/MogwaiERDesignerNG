package de.erdesignerng.visual.layout.zest;

import org.eclipse.mylyn.zest.layouts.LayoutBendPoint;
import org.eclipse.mylyn.zest.layouts.LayoutRelationship;
import org.eclipse.mylyn.zest.layouts.constraints.LayoutConstraint;

public class ERDesignerLayoutRelationship implements LayoutRelationship {

    private ERDesignerLayoutEntity destinationInLayout;
    private ERDesignerLayoutEntity sourceInLayout;

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
    public ERDesignerLayoutEntity getDestinationInLayout() {
        return destinationInLayout;
    }

    /**
     * @param destinationInLayout the destinationInLayout to set
     */
    public void setDestinationInLayout(ERDesignerLayoutEntity destinationInLayout) {
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
    public ERDesignerLayoutEntity getSourceInLayout() {
        return sourceInLayout;
    }

    /**
     * @param sourceInLayout the sourceInLayout to set
     */
    public void setSourceInLayout(ERDesignerLayoutEntity sourceInLayout) {
        this.sourceInLayout = sourceInLayout;
    }

    public void clearBendPoints() {
        bendPoints = null;
    }
}
