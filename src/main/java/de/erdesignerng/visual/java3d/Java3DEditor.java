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
import de.erdesignerng.model.*;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.View;
import de.erdesignerng.util.MavenPropertiesLocator;
import de.erdesignerng.visual.EditorFactory;
import de.erdesignerng.visual.common.ERDesignerComponent;
import de.erdesignerng.visual.common.OutlineComponent;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.i18n.ResourceHelper;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.media.j3d.*;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/**
 * Editor to show model as a 3D Scene.
 * <p/>
 * Allows Navigation & Browsing.
 */
public class Java3DEditor {

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

    private class UserObjectInfo {

        ModelItem item;
        float zlevel;

        public UserObjectInfo(ModelItem aItem, float aZLevel) {
            item = aItem;
            zlevel = aZLevel;
        }

    }

    public Java3DEditor() {

        resourceHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration()) {

            @Override
            public void postRender() {
                super.postRender();

                J3DGraphics2D g = getGraphics2D();

                String theVersion = MavenPropertiesLocator.getERDesignerVersionInfo();
                String theTitle = "(c) " + resourceHelper.getText(ERDesignerBundle.TITLE) + " "
                        + theVersion + " ";


                g.setFont(new Font("Helvetica", Font.PLAIN, 12));
                g.drawString(theTitle, 10, 20);
                g.flush(true);
            }
        };

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

        MouseRotate theRotate = new MouseRotate();
        theRotate.setSchedulingBounds(new BoundingSphere());
        theRotate.setTransformGroup(modelGroup);
        rootGroup.addChild(theRotate);

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

        canvas.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(final MouseEvent me) {
                super.mouseMoved(me);

                currentElement.setText("");

                pickCanvas.setShapeLocation(me);
                PickResult result = pickCanvas.pickClosest();
                if (result != null) {
                    Primitive p = (Primitive) result.getNode(PickResult.PRIMITIVE);
                    if (p != null) {
                        if (p.getUserData() instanceof UserObjectInfo) {
                            UserObjectInfo theItem = (UserObjectInfo) p.getUserData();
                            currentElement.setText(theItem.item.getName());
                        }
                    }
                }
            }
        });
        canvas.addMouseListener(new MouseAdapter() {

            private void handle(MouseEvent e, ModelItem aItem) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    if (e.getClickCount() == 2) {
                        editModelItem(aItem);
                    } else {
                        OutlineComponent.getDefault().setSelectedItem(aItem);
                    }
                } else {
                    // Context Menue anzeigen
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                pickCanvas.setShapeLocation(e);
                PickResult result = pickCanvas.pickClosest();
                if (result != null) {
                    Primitive p = (Primitive) result.getNode(PickResult.PRIMITIVE);
                    if (p != null) {
                        if (p.getUserData() instanceof UserObjectInfo) {
                            UserObjectInfo theItem = (UserObjectInfo) p.getUserData();
                            handle(e, theItem.item);
                        }
                    }
                }
            }
        });

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
                setSelectedObject(model, selectedModelItem);
            }
        };

        includeIncoming.setSelected(true);
        includeIncoming.addActionListener(theUpdateActionListener);
        includeOutgoing.setSelected(true);
        includeOutgoing.addActionListener(theUpdateActionListener);

        currentElement.setMinimumSize(new Dimension(300,21));
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

                OutlineComponent.getDefault().refresh(ERDesignerComponent.getDefault().getModel(), aItem);

                setSelectedObject(model, selectedModelItem);
            } catch (Exception e1) {
                ERDesignerComponent.getDefault().getWorldConnector().notifyAboutException(e1);
            }
        }
    }

    public void resetDisplay(Model aModel) {
        setSelectedObject(aModel, null);
        includeIncoming.setSelected(true);
        includeOutgoing.setSelected(true);
    }

    public Component getEditorComponent() {
        return mainPanel;
    }

    public Node createElement(ModelItem aItem, List<ModelItem> aOnSameLevel, float aZLevel) {

        Font theFont = new Font("Helvetica", Font.PLAIN, 60);
        FontMetrics theMetrics = canvas.getFontMetrics(theFont);
        Rectangle2D theDimension = theMetrics.getStringBounds(aItem.getName(), canvas.getGraphics());
        int maxWidth = (int) theDimension.getWidth();
        int mywidth = maxWidth;
        int maxHeight = (int) theDimension.getHeight();
        if (aOnSameLevel != null) {
            for (ModelItem theItem : aOnSameLevel) {
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
        int padding = (width - mywidth) / 2;

        BufferedImage theFrontImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D theFrontGraphics = (Graphics2D) theFrontImage.getGraphics();
        theFrontGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint theBackgroundPaint = new GradientPaint(0, 0, new Color(0, 148, 255), width, height,
                new Color(0, 38, 255));
        theFrontGraphics.setPaint(theBackgroundPaint);
        theFrontGraphics.fillRect(0, 0, width, height);
        theFrontGraphics.setPaint(Color.black);

        TextureLoader theTextureLoader = new TextureLoader(theFrontImage, "RGB",
                TextureLoader.ALLOW_NON_POWER_OF_TWO);
        Texture theBackTexture = theTextureLoader.getTexture();
        theBackTexture.setBoundaryModeS(Texture.CLAMP_TO_BOUNDARY);
        theBackTexture.setBoundaryModeT(Texture.CLAMP_TO_BOUNDARY);
        theBackTexture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.5f, 0f));

        TextureAttributes theBackTextureAttribute = new TextureAttributes();
        theBackTextureAttribute.setTextureMode(TextureAttributes.MODULATE);

        Appearance theBackAppearance = new Appearance();
        theBackAppearance.setTextureAttributes(theBackTextureAttribute);
        theBackAppearance.setTexture(theBackTexture);

        // Vordergrund mit Text
        theFrontGraphics.setFont(theFont);
        theFrontGraphics.setColor(new Color(229, 235, 232));
        theFrontGraphics.drawString(aItem.getName(), padding, theMetrics.getAscent());

        theTextureLoader = new TextureLoader(theFrontImage, "RGB",
                TextureLoader.ALLOW_NON_POWER_OF_TWO);
        Texture theFrontTexture = theTextureLoader.getTexture();
        theBackTexture.setBoundaryModeS(Texture.CLAMP_TO_BOUNDARY);
        theBackTexture.setBoundaryModeT(Texture.CLAMP_TO_BOUNDARY);
        theBackTexture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.5f, 0f));

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


    private List<ModelItem> getRelationsFor(Model aModel, ModelItem aTable, List<ModelItem> aElementsToIgnore) {
        List<ModelItem> theResult = new ArrayList<ModelItem>();
        for (Relation theRelation : aModel.getRelations()) {
            if (includeIncoming.isSelected() && theRelation.getImportingTable() == aTable && !aElementsToIgnore.contains(theRelation.getExportingTable()) && !theResult.contains(theRelation.getExportingTable())) {
                theResult.add(theRelation.getExportingTable());
            }
            if (includeOutgoing.isSelected() && theRelation.getExportingTable() == aTable && !aElementsToIgnore.contains(theRelation.getImportingTable()) && !theResult.contains(theRelation.getImportingTable())) {
                theResult.add(theRelation.getImportingTable());
            }
        }
        return theResult;
    }

    public void setSelectedObject(Model aModel, ModelItem aSelectedObject) {
        modelGroup.removeAllChildren();

        model = aModel;
        selectedModelItem = null;

        if (aSelectedObject instanceof Table) {

            Table theTable = (Table) aSelectedObject;
            selectedModelItem = theTable;

            modelGroup.addChild(Helper.addElementAt(createElement(theTable, null, 0f), new Vector3f(0f, 0f, 0f), 1f));

            List<ModelItem> theObjectsToIgnore = new ArrayList<ModelItem>();
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
            List<ModelItem> theRelations = getRelationsFor(aModel, theTable, theObjectsToIgnore);
            if (theRelations.size() > 0) {

                List<ModelItem> theObjectsToIgnore2 = new ArrayList<ModelItem>();
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

                for (ModelItem theDependentTable : theRelations) {
                    Node theButton2 = createElement(theDependentTable, theRelations, zLevel1);
                    double mx = Math.cos(Math.toRadians(-i)) * r1;
                    double my = Math.sin(Math.toRadians(-i)) * r1;
                    modelGroup.addChild(Helper.addElementAt(theButton2, new Vector3f((float) mx, (float) my, zLevel1), s1));

                    double mx2 = Math.cos(Math.toRadians(-i)) * r1 / 2;
                    double my2 = Math.sin(Math.toRadians(-i)) * r1 / 2;

                    modelGroup.addChild(createConnector(new Vector3f((float) mx2, (float) my2, zLevel1), r1, (int) (i + 90), connectorR1));

                    // 2. Ordnung
                    List<ModelItem> theRelations2 = getRelationsFor(aModel, theDependentTable, theObjectsToIgnore2);
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

                        for (ModelItem theDependentTable2 : theRelations2) {

                            Node theButton3 = createElement(theDependentTable2, theRelations2, zLevel2);

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

            modelGroup.addChild(Helper.addElementAt(createElement(theView, null, 0f), new Vector3f(0f, 0f, 0f), 1f));
        }
    }
}