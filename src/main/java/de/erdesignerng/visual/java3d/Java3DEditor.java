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

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.universe.SimpleUniverse;
import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Comment;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.SubjectArea;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.util.MavenPropertiesLocator;
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.EditorFactory;
import de.erdesignerng.visual.FadeInFadeOutHelper;
import de.erdesignerng.visual.common.ContextMenuFactory;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.GenericModelEditor;
import de.erdesignerng.visual.common.OutlineComponent;
import de.erdesignerng.visual.common.ToolEnum;
import de.erdesignerng.visual.common.ZoomInfo;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.java2d.TableComponent;
import de.erdesignerng.visual.java2d.ViewComponent;
import de.erdesignerng.visual.jgraph.export.Exporter;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Editor to show model as a 3D Scene.
 * <p/>
 * Allows Navigation & Browsing.
 */
public class Java3DEditor implements GenericModelEditor {

    private JPanel mainPanel;
    private Canvas3D canvas;
    private SimpleUniverse universe;
    private PickCanvas pickCanvas;
    private BranchGroup rootGroup;
    private TransformGroup modelGroup;
    private ResourceHelper resourceHelper;
    private JLabel currentElement;
    private Model model;
    private ModelItem selectedModelItem;
    private Texture nodeTexture;
    private FadeInFadeOutHelper fadingHelper;
    private UserObjectInfo fadingComponent;

    private class UserObjectInfo {

        ModelItem item;
        float zlevel;

        public UserObjectInfo(ModelItem aItem, float aZLevel) {
            item = aItem;
            zlevel = aZLevel;
        }

    }

    private class MouseNavigationBehavior extends Behavior {

        private WakeupCondition wakeupCondition;

        public MouseNavigationBehavior() {
            WakeupOnAWTEvent mouseMoveCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED);
            WakeupOnAWTEvent mouseClickCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);
            WakeupOnAWTEvent mouseWheel = new WakeupOnAWTEvent(MouseEvent.MOUSE_WHEEL);
            wakeupCondition = new WakeupOr(new WakeupCriterion[]{mouseMoveCondition, mouseClickCondition, mouseWheel});
        }

        @Override
        public void initialize() {
            wakeupOn(wakeupCondition);
        }

        @Override
        public void processStimulus(Enumeration enumeration) {
            WakeupCriterion wakeup;
            AWTEvent[] events;
            MouseEvent evt;

            while (enumeration.hasMoreElements()) {
                wakeup = (WakeupCriterion) enumeration.nextElement();
                if (wakeup instanceof WakeupOnAWTEvent) {
                    events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                    if (events.length > 0) {
                        evt = (MouseEvent) events[events.length - 1];
                        doProcess(evt);
                    }
                }
            }
            wakeupOn(wakeupCondition);
        }

        private void doProcess(MouseEvent e) {
            switch (e.getID()) {
                case MouseEvent.MOUSE_MOVED:
                    mouseMoved(e);
                    break;
                case MouseEvent.MOUSE_CLICKED:
                    mouseClicked(e);
                    break;
                case MouseEvent.MOUSE_WHEEL:
                    mouseWheel((MouseWheelEvent) e);
                    break;
            }
        }

        private void mouseWheel(MouseWheelEvent e) {
        }

        private void mouseClicked(MouseEvent e) {

            pickCanvas.setShapeLocation(e);
            PickResult[] thePickResult = pickCanvas.pickAllSorted();
            if (thePickResult != null) {
                for (PickResult theResult : thePickResult) {
                    if (theResult != null) {
                        Primitive p = (Primitive) theResult.getNode(PickResult.PRIMITIVE);
                        if (p != null) {
                            if (p.getUserData() instanceof UserObjectInfo) {
                                UserObjectInfo theItem = (UserObjectInfo) p.getUserData();
                                if (!SwingUtilities.isRightMouseButton(e)) {
                                    if (e.getClickCount() == 2) {
                                        editModelItem(theItem.item);
                                    } else {
                                        OutlineComponent.getDefault().setSelectedItem(theItem.item);
                                    }
                                    // We only handle the first found object
                                    return;
                                } else {
                                    DefaultPopupMenu theMenu = new DefaultPopupMenu(ResourceHelper
                                            .getResourceHelper(ERDesignerBundle.BUNDLE_NAME));

                                    List<ModelItem> theItems = new ArrayList<ModelItem>();
                                    theItems.add(theItem.item);
                                    ContextMenuFactory.addActionsToMenu(Java3DEditor.this, theMenu, theItems);

                                    UIInitializer.getInstance().initialize(theMenu);

                                    theMenu.show(mainPanel, e.getX(), e.getY());
                                    // We only handle the first found object
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        private void mouseMoved(MouseEvent e) {

            currentElement.setText("");

            UserObjectInfo pickedObject = null;
            pickCanvas.setShapeLocation(e);
            // We have to use pickAll because of transparency
            PickResult[] thePickResult = pickCanvas.pickAllSorted();
            if (thePickResult != null) {
                for (PickResult theResult : thePickResult) {
                    if (theResult != null) {
                        Primitive p = (Primitive) theResult.getNode(PickResult.PRIMITIVE);
                        if (p != null) {
                            if (p.getUserData() instanceof UserObjectInfo && pickedObject == null) {
                                UserObjectInfo theItem = (UserObjectInfo) p.getUserData();
                                pickedObject = theItem;
                            }
                        }
                    }
                }
            }

            if (pickedObject != null) {
                currentElement.setText(pickedObject.item.getName());
                if (pickedObject != fadingComponent) {
                    fadingHelper.setComponentToHighlightFadeOut(fadingHelper.getComponentToHighlight() != null);
                    fadingHelper.setComponentToHighlightNext(getHighlightComponentFor(pickedObject));
                }
            } else {
                fadingHelper.setComponentToHighlightFadeOut(true);
                fadingHelper.setComponentToHighlightNext(null);
            }
            fadingComponent = pickedObject;

            fadingHelper.startWaitTimer();

            // Repaint triggern
            Transform3D theTransform = new Transform3D();
            modelGroup.getTransform(theTransform);
            modelGroup.setTransform(theTransform);
        }

        private JComponent getHighlightComponentFor(UserObjectInfo aComponent) {
            if (aComponent.item instanceof Table) {
                return new TableComponent((Table) aComponent.item, true);
            }
            if (aComponent.item instanceof View) {
                return new ViewComponent((View) aComponent.item);
            }
            throw new IllegalArgumentException();
        }
    }

    public Java3DEditor() {

        resourceHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
        fadingHelper = new FadeInFadeOutHelper() {
            @Override
            public void doRepaint() {
                // Repaint triggern
                Transform3D theTransform = new Transform3D();
                modelGroup.getTransform(theTransform);
                modelGroup.setTransform(theTransform);
            }
        };

        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration()) {

            @Override
            public void postRender() {
                super.postRender();

                J3DGraphics2D g = getGraphics2D();

                String theVersion = MavenPropertiesLocator.getERDesignerVersionInfo();
                String theTitle = "(c) " + resourceHelper.getText(ERDesignerBundle.TITLE) + " "
                        + theVersion + " ";

                g.setColor(Color.white);
                g.setFont(new Font("Helvetica", Font.PLAIN, 12));
                g.drawString(theTitle, 10, 20);

                if (fadingHelper.getComponentToHighlight() != null) {

                    int x = canvas.getWidth() - fadingHelper.getComponentToHighlightPosition();
                    int y = 10;

                    Composite theOld = g.getComposite();
                    Paint theOldPaint = g.getPaint();

                    g.translate(x, y);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            0.85f));
                    fadingHelper.getComponentToHighlight().paint(g);

                    g.translate(-x, -y);
                    g.setComposite(theOld);
                    g.setPaint(theOldPaint);
                }

                g.flush(true);
            }
        };
        canvas.setDoubleBufferEnable(true);

        // Create the universe
        universe = new SimpleUniverse(canvas);

        rootGroup = new BranchGroup();

        Color3f light1Color = new Color3f(1f, 1f, 1f);
        BoundingSphere bounds =
                new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
        DirectionalLight light1
                = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        rootGroup.addChild(light1);

        modelGroup = new TransformGroup();
        modelGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        modelGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        rootGroup.addChild(modelGroup);

        MouseRotate theRotationBehavior = new MouseRotate();
        theRotationBehavior.setSchedulingBounds(new BoundingSphere());
        theRotationBehavior.setTransformGroup(modelGroup);
        rootGroup.addChild(theRotationBehavior);

        MouseNavigationBehavior theNavigationBehavior = new MouseNavigationBehavior();
        theNavigationBehavior.setSchedulingBounds(new BoundingSphere());
        rootGroup.addChild(theNavigationBehavior);

        MouseWheelZoom theZoom = new MouseWheelZoom();
        theZoom.setSchedulingBounds(new BoundingSphere());
        theZoom.setTransformGroup(modelGroup);
        rootGroup.addChild(theZoom);


        rootGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        rootGroup.setCapability(Group.ALLOW_CHILDREN_READ);
        rootGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);

        modelGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        modelGroup.setCapability(Group.ALLOW_CHILDREN_READ);
        modelGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);

        // look towards the ball
        universe.getViewingPlatform().setNominalViewingTransform();

        // add the group of objects to the Universe
        universe.addBranchGraph(rootGroup);

        pickCanvas = new PickCanvas(canvas, rootGroup);
        pickCanvas.setMode(PickCanvas.GEOMETRY);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(canvas, BorderLayout.CENTER);

        ResourceHelper theHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout(FlowLayout.LEFT));
        currentElement = new JLabel();

        currentElement.setMinimumSize(new Dimension(300, 21));
        currentElement.setPreferredSize(new Dimension(300, 21));

        bottom.add(currentElement);

        mainPanel.add(bottom, BorderLayout.SOUTH);

        UIInitializer.getInstance().initialize(mainPanel);
    }

    private void editModelItem(ModelItem aItem) {
        BaseEditor theEditor = EditorFactory.createEditorFor(aItem, mainPanel);
        if (theEditor.showModal() == DialogConstants.MODAL_RESULT_OK) {
            try {
                theEditor.applyValues();

                ERDesignerComponent.getDefault().updateSubjectAreasMenu();

                OutlineComponent.getDefault().refresh(ERDesignerComponent.getDefault().getModel());

                setSelectedObject(selectedModelItem);
            } catch (Exception e1) {
                ERDesignerComponent.getDefault().getWorldConnector().notifyAboutException(e1);
            }
        }
    }

    public Node createElement(ModelItem aItem) {

        int offset = 10;

        JComponent theRendererComponent = null;
        if (aItem instanceof Table) {
            theRendererComponent = new TableComponent((Table) aItem);
        }
        if (aItem instanceof View) {
            theRendererComponent = new ViewComponent((View) aItem);
        }

        Dimension theSize = theRendererComponent.getSize();

        int width = (int) theSize.getWidth() - offset + 2;
        int height = (int) theSize.getHeight() - offset + 2;

        BufferedImage theFrontImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D theFrontGraphics = (Graphics2D) theFrontImage.getGraphics();
        theFrontGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint theBackgroundPaint = new GradientPaint(0, 0, new Color(0, 148, 255), width, height,
                new Color(0, 38, 255), false);
        theFrontGraphics.setPaint(theBackgroundPaint);
        theFrontGraphics.fillRect(0, 0, width, height);
        theFrontGraphics.setPaint(Color.black);

        if (nodeTexture == null) {
            TextureLoader theTextureLoader = new TextureLoader(theFrontImage, "RGB",
                    TextureLoader.ALLOW_NON_POWER_OF_TWO);
            nodeTexture = theTextureLoader.getTexture();
            nodeTexture.setBoundaryModeS(Texture.CLAMP_TO_BOUNDARY);
            nodeTexture.setBoundaryModeT(Texture.CLAMP_TO_BOUNDARY);
            nodeTexture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.5f, 0f));
        }

        TextureAttributes theBackTextureAttribute = new TextureAttributes();
        theBackTextureAttribute.setTextureMode(TextureAttributes.MODULATE);

        Appearance theBackAppearance = new Appearance();
        theBackAppearance.setTextureAttributes(theBackTextureAttribute);
        theBackAppearance.setTexture(nodeTexture);

        theFrontGraphics.translate(-offset, -offset);
        theRendererComponent.paint(theFrontGraphics);

        TextureLoader theTextureLoader = new TextureLoader(theFrontImage, "RGB",
                TextureLoader.ALLOW_NON_POWER_OF_TWO);
        Texture theFrontTexture = theTextureLoader.getTexture();
        theFrontTexture.setBoundaryModeS(Texture.CLAMP_TO_BOUNDARY);
        theFrontTexture.setBoundaryModeT(Texture.CLAMP_TO_BOUNDARY);
        theFrontTexture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.5f, 0f));

        TextureAttributes theFrontTextureAttribute = new TextureAttributes();
        theFrontTextureAttribute.setTextureMode(TextureAttributes.MODULATE);

        Appearance theFrontAppearance = new Appearance();
        theFrontAppearance.setTextureAttributes(theFrontTextureAttribute);
        theFrontAppearance.setTexture(theFrontTexture);

        PolygonAttributes thePa = new PolygonAttributes();
        thePa.setCullFace(PolygonAttributes.CULL_NONE);
        theFrontAppearance.setPolygonAttributes(thePa);
        theBackAppearance.setPolygonAttributes(thePa);
        theBackAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.6f));

        Box theBox = new Box(0.0006f * width, 0.0006f * height, 0.01f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, theBackAppearance);
        theBox.getShape(Box.FRONT).setAppearance(theFrontAppearance);
        theBox.setUserData(new UserObjectInfo(aItem, 1f));

        return theBox;
    }

    public void setSelectedObject(ModelItem aSelectedObject) {
        modelGroup.removeAllChildren();

        selectedModelItem = null;

        List<List<ModelItem>> theLayers = new ArrayList<List<ModelItem>>();
        List<ModelItem> theAlreadyKnown = new ArrayList<ModelItem>();

        if (aSelectedObject != null) {

            // There is always one layer in the graph
            List<ModelItem> layer1 = new ArrayList<ModelItem>();
            layer1.add(aSelectedObject);
            theAlreadyKnown.add(aSelectedObject);
            theLayers.add(layer1);

            if (aSelectedObject instanceof Table) {
                // Tables can have multiple layers
                Table theTable = (Table) aSelectedObject;

                List<Relation> theIncomingRelations = model.getRelations().getForeignKeysFor(theTable);
                while (theIncomingRelations.size() > 0) {
                    List<ModelItem> nextLayer = new ArrayList<ModelItem>();
                    for (Relation theRelation : theIncomingRelations) {
                        if (!nextLayer.contains(theRelation.getExportingTable()) && !theAlreadyKnown.contains(theRelation.getExportingTable())) {
                            nextLayer.add(theRelation.getExportingTable());
                            theAlreadyKnown.add(theRelation.getExportingTable());
                        }
                    }

                    theIncomingRelations.clear();
                    for (ModelItem theItem : nextLayer) {
                        theIncomingRelations.addAll(model.getRelations().getForeignKeysFor((Table) theItem));
                    }

                    if (nextLayer.size() > 0) {
                        theLayers.add(nextLayer);
                    }
                }
            }
        }

        Map<ModelItem, Point3d> theItemPositions = new HashMap<ModelItem, Point3d>();

        // Now we iterate over the known layers and build them as 3d objects
        for (int theLayer = 0; theLayer < theLayers.size(); theLayer++) {

            float theZOffset = -0.5f * theLayer;

            // Starting with the layer object itself
            Transform3D theTransform = new Transform3D();
            theTransform.setTranslation(new Vector3d(0, 0, theZOffset - 0.004f));
            TransformGroup theRoot = new TransformGroup(theTransform);

            Appearance theAppearance = new Appearance();
            theAppearance.setColoringAttributes(new ColoringAttributes(1f, 1f, 0.94f, ColoringAttributes.SHADE_GOURAUD));

            theAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.95f));
            PolygonAttributes thePa = new PolygonAttributes();
            thePa.setCullFace(PolygonAttributes.CULL_NONE);
            theAppearance.setPolygonAttributes(thePa);

            Box theBox = new Box(1f, 1f, 0.002f, Primitive.GENERATE_NORMALS, theAppearance);
            theRoot.addChild(theBox);

            BranchGroup theGroup = new BranchGroup();
            theGroup.setCapability(BranchGroup.ALLOW_DETACH);
            theGroup.addChild(theRoot);
            modelGroup.addChild(theGroup);

            // And with the layer objects
            // For now, we use radial layout
            List<ModelItem> theItems = theLayers.get(theLayer);

            double theIncrement = Math.toRadians(360) / theItems.size();
            double theCurrentAngle = 0;
            double theRadius = 0.5f;
            if (theItems.size() == 1) {
                // If there is only one item at a layer, layout it at the middle
                theRadius = 0;
            }

            for (ModelItem theItem : theItems) {

                Node theButton2 = createElement(theItem);
                double mx = Math.cos(theCurrentAngle) * theRadius;
                double my = Math.sin(theCurrentAngle) * theRadius;
                modelGroup.addChild(Helper.addElementAt(theButton2, new Vector3f((float) mx, (float) my, theZOffset), 1f));

                theCurrentAngle += theIncrement;

                theItemPositions.put(theItem, new Point3d(mx, my, theZOffset));
            }
        }

        // Finally, we build the connectors between the items
        Appearance theLineAppearance = new Appearance();
        theLineAppearance.setColoringAttributes(new ColoringAttributes(1f, 0, 0, ColoringAttributes.NICEST));
        LineAttributes theLineAttributes = new LineAttributes();
        theLineAttributes.setLineWidth(4.0f);
        theLineAppearance.setLineAttributes(theLineAttributes);
        theLineAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.80f));

        Set<String> theProcessed = new HashSet<String>();
        for (ModelItem theItem : theAlreadyKnown) {
            if (theItem instanceof Table) {
                // Only tables can have connectors
                for (Relation theRelation : model.getRelations().getForeignKeysFor((Table) theItem)) {
                    Table theExportingTable = theRelation.getExportingTable();
                    if (theExportingTable != theItem) {
                        // We do not display self references

                        boolean add = true;
                        String theSearch1 = theItem.getName() + " " + theExportingTable.getName();
                        String theSearch2 = theExportingTable.getName() + " " + theItem.getName();
                        if (theProcessed.contains(theSearch1) || theProcessed.contains(theSearch2)) {
                            add = false;
                        }

                        if (add) {
                            Point3d[] theLinePoints = new Point3d[2];
                            theLinePoints[0] = theItemPositions.get(theItem);
                            theLinePoints[1] = theItemPositions.get(theExportingTable);
                            LineArray theLineArray = new LineArray(2, LineArray.COORDINATES);
                            theLineArray.setCoordinates(0, theLinePoints);
                            Shape3D theLineShape = new Shape3D(theLineArray, theLineAppearance);

                            BranchGroup theGroup = new BranchGroup();
                            theGroup.setCapability(BranchGroup.ALLOW_DETACH);
                            theGroup.addChild(theLineShape);
                            modelGroup.addChild(theGroup);

                            theProcessed.add(theSearch1);
                            theProcessed.add(theSearch2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void repaintGraph() {
        // Repaint triggern
        Transform3D theTransform = new Transform3D();
        modelGroup.getTransform(theTransform);
        modelGroup.setTransform(theTransform);

        mainPanel.invalidate();
        canvas.invalidate();
        mainPanel.doLayout();
        mainPanel.repaint();
    }

    @Override
    public void commandSetDisplayLevel(DisplayLevel aLevel) {
    }

    @Override
    public void commandSetDisplayOrder(DisplayOrder aOrder) {
    }

    @Override
    public void commandHideSubjectArea(SubjectArea aArea) {
    }

    @Override
    public void commandShowSubjectArea(SubjectArea aArea) {
    }

    @Override
    public void commandSetTool(ToolEnum aTool) {
    }

    @Override
    public void commandSetZoom(ZoomInfo aZoomInfo) {
    }

    @Override
    public void setModel(Model aModel) {
        model = aModel;
        setSelectedObject(null);
    }

    @Override
    public void commandSetDisplayCommentsState(boolean aState) {
    }

    @Override
    public void commandSetDisplayGridState(boolean aState) {
    }

    @Override
    public void refreshPreferences() {
    }

    @Override
    public void commandNotifyAboutEdit() {
        setSelectedObject(selectedModelItem);
    }

    @Override
    public void setIntelligentLayoutEnabled(boolean aStatus) {
    }

    @Override
    public void commandAddToNewSubjectArea(List<ModelItem> aItems) {
    }

    @Override
    public void commandDelete(List<ModelItem> aItems) {
    }

    @Override
    public void commandCreateComment(Comment aComment, Point2D aLocation) {
    }

    @Override
    public void commandCreateRelation(Relation aRelation) {
    }

    @Override
    public void commandCreateTable(Table aTable, Point2D aLocation) {
    }

    @Override
    public void commandCreateView(View aView, Point2D aLocation) {
    }

    @Override
    public void commandShowOrHideRelationsFor(Table aTable, boolean aShow) {
    }

    @Override
    public JComponent getDetailComponent() {
        return mainPanel;
    }

    @Override
    public void addExportEntries(DefaultMenu aMenu, Exporter aExporter) {
    }

    @Override
    public boolean supportsZoom() {
        return false;
    }

    @Override
    public boolean supportsHandAction() {
        return true;
    }

    @Override
    public boolean supportsRelationAction() {
        return false;
    }

    @Override
    public boolean supportsCommentAction() {
        return false;
    }

    @Override
    public boolean supportsViewAction() {
        return false;
    }

    @Override
    public boolean supportsIntelligentLayout() {
        return false;
    }

    @Override
    public void initExportEntries(ResourceHelperProvider aProvider, DefaultMenu aExportMenu) {
        aExportMenu.setEnabled(false);
    }

    @Override
    public boolean supportsEntityAction() {
        return false;
    }

    @Override
    public boolean supportsGrid() {
        return false;
    }

    @Override
    public boolean supportsDisplayLevel() {
        return false;
    }

    @Override
    public boolean supportsSubjectAreas() {
        return false;
    }

    @Override
    public boolean supportsAttributeOrder() {
        return false;
    }

    @Override
    public boolean supportsDeletionOfObjects() {
        return false;
    }

    @Override
    public boolean supportShowingAndHidingOfRelations() {
        return false;
    }
}