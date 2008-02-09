package de.erdesignerng.visual.layout.jung;

import edu.uci.ics.jung.graph.DirectedEdge;

public class ERDesignerJungLayoutRelationship {

    private ERDesignerJungLayoutEntity destinationInLayout;

    private ERDesignerJungLayoutEntity sourceInLayout;

    private DirectedEdge edge;

    /**
     * @return the destinationInLayout
     */
    public ERDesignerJungLayoutEntity getDestinationInLayout() {
        return destinationInLayout;
    }

    /**
     * @param destinationInLayout
     *            the destinationInLayout to set
     */
    public void setDestinationInLayout(ERDesignerJungLayoutEntity destinationInLayout) {
        this.destinationInLayout = destinationInLayout;
    }

    /**
     * @return the edge
     */
    public DirectedEdge getEdge() {
        return edge;
    }

    /**
     * @param edge
     *            the edge to set
     */
    public void setEdge(DirectedEdge edge) {
        this.edge = edge;
    }

    /**
     * @return the sourceInLayout
     */
    public ERDesignerJungLayoutEntity getSourceInLayout() {
        return sourceInLayout;
    }

    /**
     * @param sourceInLayout
     *            the sourceInLayout to set
     */
    public void setSourceInLayout(ERDesignerJungLayoutEntity sourceInLayout) {
        this.sourceInLayout = sourceInLayout;
    }
}