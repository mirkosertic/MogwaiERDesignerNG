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
package de.erdesignerng.visual.layout.graphviz;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.visual.cells.views.RelationEdgeView;
import de.erdesignerng.visual.layout.LayoutException;
import de.erdesignerng.visual.layout.Layouter;

public class GraphvizLayout implements Layouter {

    private ProcessBuilder processBuilder;

    private String lastLog;

    private Pattern nodePattern = Pattern.compile("^\\s*(n\\d+)\\s*\\[.*pos=\"?(\\d+),(\\d+)");

    private Pattern edgePattern = Pattern.compile("^\\s*(n\\d+)\\s*->\\s*(n\\d+)\\s*"
            + "\\[.*pos=\"e,(\\d+),(\\d+) (\\d+,\\d+(?:(?: \\d+,\\d+){3})+)\"" + ".*lp=\"?(\\d+),(\\d+)");

    private Pattern splitPattern = Pattern.compile("[ ,]");

    private JGraph graph;

    public void applyLayout(ApplicationPreferences aPreferences, JGraph aGraph, Object[] aCells) throws LayoutException {
        
        String theDotCommand = aPreferences.getDotPath();
        
        processBuilder = new ProcessBuilder(theDotCommand, "-y -Tdot");
        processBuilder.redirectErrorStream(true);

        graph = aGraph;
        try {
            if (!callDot(aCells)) {
                throw new LayoutException("Error while trying to layout the graph using Graphviz.\n\n"
                        + "See the output of the process call below:\n\n" + lastLog);
            }
        } catch (LayoutException e) {
            throw e;
        } catch (Exception e) {
            throw new LayoutException("Error during layouting", e);
        }
    }

    private boolean callDot(Object[] aCells) throws IOException, InterruptedException {
        GraphLayoutCache theLayoutCache = graph.getGraphLayoutCache();

        Collection<CellView> theNodes = new HashSet<CellView>();
        Collection<EdgeView> theEdges = new HashSet<EdgeView>();

        GraphModel theModel = graph.getModel();

        for (Object cell : aCells) {

            if (theModel.isPort(cell)) {
                continue;
            } else if (theModel.isEdge(cell)) {
                theEdges.add((EdgeView) theLayoutCache.getMapping(cell, true));
            } else {
                theNodes.add(theLayoutCache.getMapping(cell, true));
            }
        }

        Process theDotProcess = processBuilder.start();

        Writer theProcessWriter = new BufferedWriter(new OutputStreamWriter(theDotProcess.getOutputStream(), "UTF-8"));
        StringBuilder theInput = new StringBuilder();

        // building the input for graphviz
        theInput.append("digraph {\n" + "dpi=72;\n" + "charset=\"UTF-8\";\n");
        theInput.append("edge [labeljust=l");

        Font theEdgesFont = GraphConstants.getFont(theEdges.iterator().next().getAttributes());
        String theFontName = theEdgesFont.getFontName(Locale.getDefault());
        String theFontSize = String.valueOf(theEdgesFont.getSize2D());
        theInput.append(", fontname=\"" + theFontName.replace("\"", "\\\"") + "\"");
        theInput.append(", fontsize=\"" + theFontSize.replace("\"", "\\\"") + "\"");

        theInput.append("];\n");
        theInput.append("node [shape=box, fixedsize=true");
        theInput.append("];\n");

        Map<CellView, String> theNodes2strings = new HashMap<CellView, String>();
        Map<String, CellView> theStrings2nodes = new HashMap<String, CellView>();
        int theId = 0;

        for (CellView theNode : theNodes) {
            String s = "n" + (++theId);
            theNodes2strings.put(theNode, s);
            theStrings2nodes.put(s, theNode);
            Rectangle2D bounds = theNode.getBounds();

            theInput.append(s + " [width=" + bounds.getWidth() / 72. + ", height=" + bounds.getHeight() / 72.
                    + ", shape=");

            if (theNode instanceof VertexView) {
                theInput.append("box");
            }

            theInput.append(", fixedsize=true];\n");
        }

        Map<String, EdgeView> theStrings2edges = new HashMap<String, EdgeView>();
        for (EdgeView edge : theEdges) {

            Object theSource = DefaultGraphModel.getSourceVertex(theModel, edge.getCell());
            Object theTarget = DefaultGraphModel.getTargetVertex(theModel, edge.getCell());
            CellView theSourceView = theLayoutCache.getMapping(theSource, true);
            CellView theTargetView = theLayoutCache.getMapping(theTarget, true);

            String s = theNodes2strings.get(theSourceView) + " -> " + theNodes2strings.get(theTargetView);
            theStrings2edges.put(s, edge);
            // the dots "..." are inserted to get some more space on the left
            // and right side of the labels
            theInput.append(s + " [label=\"..." + edge.getCell().toString().replace("\"", "\\\"") + "...\"" + "];\n");
        }
        theInput.append("}\n");
        theProcessWriter.write(theInput.toString());
        theProcessWriter.close();

        // for Debugging
        // System.out.println("\nDot input:\n" + dotInput.toString());

        // parsing the output of graphviz
        Map<EdgeView, Point> labelsAbsolutePositions = new HashMap<EdgeView, Point>();
        Map<Object, Map> nestedAttributes = new HashMap<Object, Map>();

        BufferedReader theDotResultReader = new BufferedReader(new InputStreamReader(theDotProcess.getInputStream(),
                "UTF-8"));
        StringBuilder buf = new StringBuilder();
        StringBuilder longLine = null;
        for (String line = theDotResultReader.readLine(); line != null; line = theDotResultReader.readLine()) {
            buf.append(line).append("\n");
            if (line.endsWith("\\")) {
                if (longLine == null) {
                    longLine = new StringBuilder();
                }
                longLine.append(line, 0, line.length() - 1);
                continue;
            }
            if (longLine != null) {
                longLine.append(line);
                line = longLine.toString();
                longLine = null;
            }

            /*
             * nodePattern: ^\s*(n\d+)\s*\[.*pos="?(\d+),(\d+)"
             */
            Matcher matcher = nodePattern.matcher(line);
            if (matcher.find()) {
                String s = matcher.group(1);
                CellView n = theStrings2nodes.get(s);
                int x = Integer.parseInt(matcher.group(2));
                int y = Integer.parseInt(matcher.group(3));
                Rectangle2D bounds = n.getBounds();

                x -= bounds.getWidth() / 2;
                y -= bounds.getHeight() / 2;

                Rectangle2D newBounds = new Rectangle2D.Double(x, y, bounds.getWidth(), bounds.getHeight());

                Map editAttributes = new HashMap();
                GraphConstants.setBounds(editAttributes, newBounds);
                nestedAttributes.put(n.getCell(), editAttributes);

                continue;
            }

            /*
             * edgePattern: ^\s*(n\d+)\s*->\s*(n\d+)\s*\[.*pos= "e,(\d+),(\d+)
             * (\d+,\d+(?: (?:\d+,\d+){3})+)".*lp="?(\d+),(\d+)
             */
            matcher = edgePattern.matcher(line);
            if (matcher.find()) {
                String s = matcher.group(1) + " -> " + matcher.group(2);
                EdgeView edge = theStrings2edges.get(s);
                double endx = Double.parseDouble(matcher.group(3));
                double endy = Double.parseDouble(matcher.group(4));

                String[] coords = splitPattern.split(matcher.group(5));
                float[] c = new float[coords.length];

                for (int i = 0; i < coords.length; ++i) {
                    c[i] = Float.parseFloat(coords[i]);
                }

                int lx = Integer.parseInt(matcher.group(6));
                int ly = Integer.parseInt(matcher.group(7));

                Point lp = new Point(lx, ly);
                labelsAbsolutePositions.put(edge, lp);

                List<Point2D> points = new ArrayList<Point2D>();

                for (int i = 0; i < c.length; i += 2) {
                    Point2D p = edge.getAttributes().createPoint(c[i], c[i + 1]);
                    points.add(p);
                }

                points.add(new Point2D.Double(endx, endy));

                Map editAttributes = new HashMap();
                GraphConstants.setPoints(editAttributes, points);
                GraphConstants.setLineStyle(editAttributes, RelationEdgeView.MyRenderer.STYLE_GRAPHVIZ_BEZIER);

                nestedAttributes.put(edge.getCell(), editAttributes);

                continue;
            }
        }
        // finally applys the changed attributes
        theLayoutCache.edit(nestedAttributes);

        lastLog = buf.toString();
        theDotProcess.waitFor();

        // for Debugging
        // System.out.println("\nDot output:\n" + buf.toString());

        setLabelPositions(labelsAbsolutePositions);

        if (theDotProcess.exitValue() == 0) {
            return true;
        } else {
            buf.append("\n!!! exit value of graphviz: " + theDotProcess.exitValue() + " !!!\n");
            lastLog = buf.toString();
            return false;
        }
    }

    /**
     * Sets the positions for the Labels. It gets absolute positions as
     * coordinates and translates them to relative positions according to
     * JGraph.
     * 
     * @param aPositions
     *            A map which maps edges and thus their labels to absolute
     *            positions
     */
    private void setLabelPositions(Map<EdgeView, Point> aPositions) {
        Map<Object, Map> nestedAttributes = new HashMap<Object, Map>();

        Collection keys = aPositions.keySet();
        for (Object key : keys) {
            Map editAttributes = new HashMap();

            EdgeView edge = (EdgeView) key;
            Point2D p = getRelativeLabelPosition(edge, aPositions.get(edge));

            GraphConstants.setLabelPosition(editAttributes, p);
            nestedAttributes.put(edge.getCell(), editAttributes);
        }
        // finally applys the changed attributes
        graph.getGraphLayoutCache().edit(nestedAttributes);
    }

    /**
     * Transforms absolute label positions to the JGraph edge-relative
     * positions.
     * 
     * @param aEdgeView
     *            The edge to which the label belongs to
     * @param aPoint
     *            The absolute position of the label
     * @return The relative position of the label
     */
    private Point2D getRelativeLabelPosition(EdgeView aEdgeView, Point aPoint) {
        /*
         * Calculation code taken from the EdgeView.EdgeHandle.mouseDragged()
         * Method
         */

        Point2D p = graph.fromScreen(aPoint);

        double x = p.getX();
        double y = p.getY();

        Point2D p0 = aEdgeView.getPoint(0);

        double p0x = p0.getX();
        double p0y = p0.getY();

        Point2D vector = aEdgeView.getLabelVector();
        double dx = vector.getX();
        double dy = vector.getY();

        double pex = p0.getX() + dx;
        double pey = p0.getY() + dy;

        double len = Math.sqrt(dx * dx + dy * dy);
        if (len > 0) {
            double u = GraphConstants.PERMILLE;
            double posy = len * (-y * dx + p0y * dx + x * dy - p0x * dy) / (-pey * dy + p0y * dy - dx * pex + dx * p0x);
            double posx = u * (-y * pey + y * p0y + p0y * pey - p0y * p0y - pex * x + pex * p0x + p0x * x - p0x * p0x)
                    / (-pey * dy + p0y * dy - dx * pex + dx * p0x);
            p = new Point2D.Double(posx, posy);
        } else {
            p = new Point2D.Double(x - p0.getX(), y - p0.getY());
        }

        return p;
    }

}
