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
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
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
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.i18n.ResourceHelper;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.Node;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
    private JCheckBox includeIncoming;
    private JCheckBox includeOutgoing;
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
            wakeupCondition = new WakeupOr(new WakeupCriterion[]{mouseMoveCondition, mouseClickCondition});
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
            }
        }

        private void mouseClicked(MouseEvent e) {

            pickCanvas.setShapeLocation(e);
            PickResult result = pickCanvas.pickClosest();
            if (result != null) {
                Primitive p = (Primitive) result.getNode(PickResult.PRIMITIVE);
                if (p != null) {
                    if (p.getUserData() instanceof UserObjectInfo) {
                        UserObjectInfo theItem = (UserObjectInfo) p.getUserData();
                        if (!SwingUtilities.isRightMouseButton(e)) {
                            if (e.getClickCount() == 2) {
                                editModelItem(theItem.item);
                            } else {
                                OutlineComponent.getDefault().setSelectedItem(theItem.item);
                            }
                        } else {
                            // Context Menue anzeigen
                        }
                    }
                }
            }
        }

        private void mouseMoved(MouseEvent e) {

            currentElement.setText("");

            UserObjectInfo pickedObject = null;
            pickCanvas.setShapeLocation(e);
            PickResult result = pickCanvas.pickClosest();
            if (result != null) {
                Primitive p = (Primitive) result.getNode(PickResult.PRIMITIVE);
                if (p != null) {
                    if (p.getUserData() instanceof UserObjectInfo) {
                        UserObjectInfo theItem = (UserObjectInfo) p.getUserData();
                        pickedObject = theItem;
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
        includeIncoming = new JCheckBox(theHelper.getText(ERDesignerBundle.INCLUDEINCOMINGRELATIONS));
        includeOutgoing = new JCheckBox(theHelper.getText(ERDesignerBundle.INCLUDEOUTGOINGRELATIONS));
        currentElement = new JLabel();

        ActionListener theUpdateActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedObject(selectedModelItem);
            }
        };

        includeIncoming.setSelected(true);
        includeIncoming.addActionListener(theUpdateActionListener);
        includeOutgoing.setSelected(true);
        includeOutgoing.addActionListener(theUpdateActionListener);

        currentElement.setMinimumSize(new Dimension(300, 21));
        currentElement.setPreferredSize(new Dimension(300, 21));

        bottom.add(currentElement);
        bottom.add(includeIncoming);
        bottom.add(includeOutgoing);

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

    private Dimension computeDimensionFor(ModelItem aItem, List<Table> aItems) {
        Font theFont = new Font("Helvetica", Font.PLAIN, 60);
        FontMetrics theMetrics = canvas.getFontMetrics(theFont);
        Rectangle2D theDimension = theMetrics.getStringBounds(aItem.getName(), canvas.getGraphics());
        int maxWidth = (int) theDimension.getWidth();
        int maxHeight = (int) theDimension.getHeight();
        if (aItems != null) {
            for (ModelItem theItem : aItems) {
                theDimension = theMetrics.getStringBounds(theItem.getName(), canvas.getGraphics());
                if (theDimension.getWidth() > maxWidth) {
                    maxWidth = (int) theDimension.getWidth();
                }
                if (theDimension.getHeight() > maxHeight) {
                    maxHeight = (int) theDimension.getHeight();
                }
            }
        }
        int height = 80;
        int width = 80 + (height * maxWidth / maxHeight);

        return new Dimension(width, height);
    }

    public Node createElement(ModelItem aItem, Dimension aSize, float aZLevel) {

        Font theFont = new Font("Helvetica", Font.PLAIN, 60);
        FontMetrics theMetrics = canvas.getFontMetrics(theFont);
        Rectangle2D theDimension = theMetrics.getStringBounds(aItem.getName(), canvas.getGraphics());

        int height = (int) aSize.getHeight();
        int width = (int) aSize.getWidth();
        int padding = (width - (int) theDimension.getWidth()) / 2;

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

        // Vordergrund mit Text
        theFrontGraphics.setFont(theFont);
        theFrontGraphics.setColor(new Color(229, 235, 232));
        theFrontGraphics.drawString(aItem.getName(), padding, theMetrics.getAscent());

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

        Color3f theColor = new Color3f(0.8f, 0.8f, 0.8f);
        ColoringAttributes theColorAttributes = new ColoringAttributes(theColor, ColoringAttributes.NICEST);
        theBackAppearance.setColoringAttributes(theColorAttributes);

        float bheight = 0.2f * (float) height / (float) width;

        Box theBox = new Box(0.2f, bheight, bheight, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, theBackAppearance);
        theBox.getShape(Box.FRONT).setAppearance(theFrontAppearance);
        theBox.getShape(Box.TOP).setAppearance(theFrontAppearance);
        theBox.getShape(Box.BOTTOM).setAppearance(theFrontAppearance);
        theBox.getShape(Box.BACK).setAppearance(theFrontAppearance);
        theBox.setUserData(new UserObjectInfo(aItem, aZLevel));

        return theBox;
    }

    public Node createConnector(Vector3f aTranslation, float aSize, int aRotZ, float aRadius) {

        BranchGroup theReturn = new BranchGroup();
        theReturn.setCapability(BranchGroup.ALLOW_DETACH);

        Transform3D theRotation = new Transform3D();
        theRotation.rotZ(Math.toRadians(-aRotZ));

        TransformGroup theGroup = new TransformGroup(theRotation);

        Cylinder theCylinder = new Cylinder(aRadius, aSize);
        theGroup.addChild(theCylinder);

        Transform3D theTranslation = new Transform3D();
        theTranslation.setTranslation(aTranslation);

        TransformGroup theGroup2 = new TransformGroup(theTranslation);
        theGroup2.addChild(theGroup);

        theReturn.addChild(theGroup2);
        return theReturn;
    }

    public Node createRootAt(Vector3f aTranslation, float aSize, float aRadius) {

        BranchGroup theReturn = new BranchGroup();
        theReturn.setCapability(BranchGroup.ALLOW_DETACH);

        Transform3D theRotation = new Transform3D();
        theRotation.rotX(Math.toRadians(90));

        TransformGroup theGroup = new TransformGroup(theRotation);

        Appearance theAppearance = new Appearance();

        Cylinder theCylinder = new Cylinder(aRadius, aSize, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, theAppearance);
        theGroup.addChild(theCylinder);

        Transform3D theTranslation = new Transform3D();
        theTranslation.setTranslation(aTranslation);

        TransformGroup theGroup2 = new TransformGroup(theTranslation);
        theGroup2.addChild(theGroup);

        theReturn.addChild(theGroup2);
        return theReturn;
    }

    private List<Table> getRelationsFor(Table aTable, List<Table> aElementsToIgnore) {
        List<Table> theResult = new ArrayList<Table>();
        if (includeIncoming.isSelected()) {
            for (Relation theRelation : model.getRelations().getForeignKeysFor(aTable)) {
                Table theTable = theRelation.getExportingTable();
                if (!aElementsToIgnore.contains(theTable) && !theResult.contains(theTable)) {
                    theResult.add(theTable);
                }
            }
        }
        if (includeOutgoing.isSelected()) {
            for (Relation theRelation : model.getRelations().getExportedKeysFor(aTable)) {
                Table theTable = theRelation.getImportingTable();
                if (!aElementsToIgnore.contains(theTable) && !theResult.contains(theTable)) {
                    theResult.add(theTable);
                }
            }
        }
        return theResult;
    }

    public void setSelectedObject(ModelItem aSelectedObject) {
        modelGroup.removeAllChildren();

        selectedModelItem = null;

        if (aSelectedObject instanceof Table) {

            Table theTable = (Table) aSelectedObject;
            selectedModelItem = theTable;

            Dimension theSizeLevel0 = computeDimensionFor(theTable, null);
            modelGroup.addChild(Helper.addElementAt(createElement(theTable, theSizeLevel0, 0f), new Vector3f(0f, 0f, 0f), 1f));

            List<Table> theObjectsToIgnore = new ArrayList<Table>();
            theObjectsToIgnore.add(theTable);

            float r1 = 0.5f;
            float r2 = 0.25f;
            float s1 = 0.6f;
            float s2 = 0.5f;
            float a2 = 40f;
            int maxLevel1 = 9;
            int maxLevel2 = 5;

            float connectorR1 = 0.005f;
            float connectorR2 = 0.0025f;

            // 1. Ordnung
            List<Table> theRelations = getRelationsFor(theTable, theObjectsToIgnore);
            if (theRelations.size() > 0) {

                List<Table> theObjectsToIgnore2 = new ArrayList<Table>();
                theObjectsToIgnore2.addAll(theObjectsToIgnore);
                theObjectsToIgnore2.addAll(theRelations);

                boolean deepmodeLevel1;
                float i = 0;
                float zLevel1 = 0;

                float increment = 360 / theRelations.size();
                if (theRelations.size() <= maxLevel1) {
                    deepmodeLevel1 = false;
                    if (theRelations.size() % 2 == 1) {
                        i -= increment / 4;
                    }
                } else {
                    deepmodeLevel1 = true;
                    increment = 180 / maxLevel1;
                }

                Dimension theSizeLevel1 = computeDimensionFor(theTable, theRelations);

                for (Table theDependentTable : theRelations) {
                    Node theButton2 = createElement(theDependentTable, theSizeLevel1, zLevel1);
                    double mx = Math.cos(Math.toRadians(-i)) * r1;
                    double my = Math.sin(Math.toRadians(-i)) * r1;
                    modelGroup.addChild(Helper.addElementAt(theButton2, new Vector3f((float) mx, (float) my, zLevel1), s1));

                    double mx2 = Math.cos(Math.toRadians(-i)) * r1 / 2;
                    double my2 = Math.sin(Math.toRadians(-i)) * r1 / 2;

                    modelGroup.addChild(createConnector(new Vector3f((float) mx2, (float) my2, zLevel1), r1, (int) (i + 90), connectorR1));

                    // 2. Ordnung
                    List<Table> theRelations2 = getRelationsFor(theDependentTable, theObjectsToIgnore2);

                    Dimension theSizeLevel2 = computeDimensionFor(theDependentTable, theRelations2);

                    if (theRelations2.size() > 0) {

                        float theMinAngle = i;
                        float theIncrement2 = (a2 * 2) / theRelations2.size();
                        boolean deepmodeLevel2 = false;
                        float zLevel2 = zLevel1;

                        if (theRelations2.size() > 1) {
                            theMinAngle -= a2;
                            if (theRelations2.size() > maxLevel2) {
                                deepmodeLevel2 = true;
                                theIncrement2 = 180 / maxLevel2;
                                r2 = 0.2f;
                            }
                        }

                        for (Table theDependentTable2 : theRelations2) {

                            Node theButton3 = createElement(theDependentTable2, theSizeLevel2, zLevel2);

                            double mx1 = mx + Math.cos(Math.toRadians(-theMinAngle)) * r2;
                            double my1 = my + Math.sin(Math.toRadians(-theMinAngle)) * r2;
                            modelGroup.addChild(Helper.addElementAt(theButton3, new Vector3f((float) mx1, (float) my1, zLevel2), s2));

                            double mx3 = mx + Math.cos(Math.toRadians(-theMinAngle)) * r2 / 2;
                            double my3 = my + Math.sin(Math.toRadians(-theMinAngle)) * r2 / 2;

                            modelGroup.addChild(createConnector(new Vector3f((float) mx3, (float) my3, zLevel2), r2, ((int) theMinAngle) + 90, connectorR2));

                            theMinAngle += theIncrement2;

                            if (deepmodeLevel2) {
                                zLevel2 -= 0.03 / maxLevel2 * 2;
                            }
                        }

                        if (deepmodeLevel2) {
                            modelGroup.addChild(createRootAt(new Vector3f((float) mx, (float) my, zLevel1 + (zLevel2 - zLevel1) / 2), zLevel2 - zLevel1, connectorR2 * 3));
                        }

                        if (deepmodeLevel2 && deepmodeLevel1) {
                            zLevel1 = zLevel2 - 0.03f;
                        }

                    }
                    i += increment;
                    if (deepmodeLevel1) {
                        zLevel1 -= 0.03 / maxLevel1 * 2;
                    }
                }

                if (deepmodeLevel1) {
                    modelGroup.addChild(createRootAt(new Vector3f(0f, 0f, zLevel1 / 2), zLevel1, connectorR1 * 3));
                }
            }
        }
        if (aSelectedObject instanceof View) {
            View theView = (View) aSelectedObject;

            selectedModelItem = theView;

            Dimension theSizeLevel0 = computeDimensionFor(theView, null);

            modelGroup.addChild(Helper.addElementAt(createElement(theView, theSizeLevel0, 0f), new Vector3f(0f, 0f, 0f), 1f));
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
        includeIncoming.setSelected(true);
        includeOutgoing.setSelected(true);
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
}