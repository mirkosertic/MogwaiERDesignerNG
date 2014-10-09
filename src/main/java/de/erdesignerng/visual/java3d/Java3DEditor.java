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

import anaglyphcanvas3d.AnaglyphCanvas3D;
import anaglyphcanvas3d.AnaglyphMode;
import anaglyphcanvas3d.StereoMode;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Box;
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
import de.erdesignerng.visual.DisplayLevel;
import de.erdesignerng.visual.DisplayOrder;
import de.erdesignerng.visual.EditorFactory;
import de.erdesignerng.visual.FadeInFadeOutHelper;
import de.erdesignerng.visual.common.*;
import de.erdesignerng.visual.editor.BaseEditor;
import de.erdesignerng.visual.editor.DialogConstants;
import de.erdesignerng.visual.java2d.TableComponent;
import de.erdesignerng.visual.java2d.ViewComponent;
import de.erdesignerng.visual.jgraph.export.Exporter;
import de.mogwai.common.client.looks.UIInitializer;
import de.mogwai.common.client.looks.components.DefaultComboBox;
import de.mogwai.common.client.looks.components.DefaultLabel;
import de.mogwai.common.client.looks.components.DefaultPanel;
import de.mogwai.common.client.looks.components.DefaultPopupMenu;
import de.mogwai.common.client.looks.components.menu.DefaultMenu;
import de.mogwai.common.i18n.ResourceHelper;
import de.mogwai.common.i18n.ResourceHelperProvider;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Editor to show model as a 3D Scene.
 * <p/>
 * Allows Navigation & Browsing.
 */
public class Java3DEditor implements GenericModelEditor {

    private static final ResourceHelper HELPER = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

    private static final float ZDISTANCE = 0.5f;

    private final JPanel mainPanel;
    private AnaglyphCanvas3D canvas;
    private final SimpleUniverse universe;
    private final PickCanvas pickCanvas;
    private final BranchGroup rootGroup;
    private TransformGroup modelGroup;
    private final TransformGroup moveGroup;
    private Switch switchGroup;
    private final ResourceHelper resourceHelper;
    private final JLabel currentElement;
    private Model model;
    private ModelItem selectedModelItem;
    private Texture nodeTexture;
    private final FadeInFadeOutHelper fadingHelper;
    private UserObjectInfo fadingComponent;
    private int currentLayer;
    private int maxLayer;
    private List<List<ModelItem>> modelLayers;
    private BranchGroup connectorGroup;
    private Map<ModelItem, Point3d> modelItemPositions;
    private final String helpHTML;
    private BufferedImage helpImage;

    private class DisplayPanel extends DefaultPanel implements ResourceHelperProvider {
        @Override
        public ResourceHelper getResourceHelper() {
            return HELPER;
        }
    }

    private class NameValuePair {

        final Object value;
        final String key;

        public NameValuePair(Object aValue, String aKey) {
            value = aValue;
            key = aKey;
        }

        @Override
        public String toString() {
            return HELPER.getText(key);
        }
    }

    private class UserObjectInfo {

        final ModelItem item;
        final float zlevel;

        public UserObjectInfo(ModelItem aItem, float aZLevel) {
            item = aItem;
            zlevel = aZLevel;
        }

    }

    private class MouseNavigationBehavior extends Behavior {

        private final WakeupCondition wakeupCondition;

        public MouseNavigationBehavior() {
            WakeupOnAWTEvent mouseMoveCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED);
            WakeupOnAWTEvent mouseClickCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);
            WakeupOnAWTEvent mouseWheel = new WakeupOnAWTEvent(MouseEvent.MOUSE_WHEEL);
            WakeupOnAWTEvent mouseDrag = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
            WakeupOnAWTEvent mouseReleased = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
            wakeupCondition = new WakeupOr(new WakeupCriterion[]{mouseMoveCondition, mouseClickCondition, mouseWheel, mouseDrag, mouseReleased});
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
            // We only handle events if there is something selected
            if (selectedModelItem != null) {
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
                    case MouseEvent.MOUSE_DRAGGED:
                        mouseDragged(e);
                        break;
                    case MouseEvent.MOUSE_RELEASED:
                        mouseReleased(e);
                        break;
                }
            }
        }

        private Point lastMouseDragLocation;

        private void mouseReleased(MouseEvent e) {
            lastMouseDragLocation = null;

        }

        private void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                if (lastMouseDragLocation != null) {
                    int my = lastMouseDragLocation.y - e.getY();

                    scaleByAmount(my * 0.01);
                }
                lastMouseDragLocation = e.getPoint();
            }
        }

        private void scaleByAmount(double aAmount) {
            Transform3D theTransform = new Transform3D();
            modelGroup.getTransform(theTransform);

            theTransform.setScale(theTransform.getScale() + aAmount);

            if (theTransform.getScale() > 4.5) {
                theTransform.setScale(4.5);
            }
            if (theTransform.getScale() < 0.3) {
                theTransform.setScale(0.3);
            }

            modelGroup.setTransform(theTransform);
        }

        private void mouseWheel(MouseWheelEvent e) {
            if (!e.isControlDown()) {
                if (e.getWheelRotation() < 0 && currentLayer < maxLayer - 1) {
                    currentLayer++;
                }
                if (e.getWheelRotation() > 0 && currentLayer > 0) {
                    currentLayer--;
                }

                setCurrentLayer(currentLayer);
            } else {
                scaleByAmount(e.getUnitsToScroll() * -0.1);
            }
        }

        private void mouseClicked(MouseEvent e) {

            lastMouseDragLocation = null;

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

                                    List<ModelItem> theItems = new ArrayList<>();
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

        StringBuilder theHelpHtml = new StringBuilder();
        theHelpHtml.append("<html>");
        theHelpHtml.append("<h1>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_2D3D_EDITOR_1));
        theHelpHtml.append("</h1>");
        theHelpHtml.append("<ul>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_3D_EDITOR_1));
        theHelpHtml.append("</li>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_3D_EDITOR_2));
        theHelpHtml.append("</li>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_3D_EDITOR_3));
        theHelpHtml.append("</li>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_3D_EDITOR_4));
        theHelpHtml.append("</li>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_3D_EDITOR_5));
        theHelpHtml.append("</li>");
        theHelpHtml.append("</ul>");
        theHelpHtml.append("</html>");
        helpHTML = theHelpHtml.toString();

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

        mainPanel = new DisplayPanel();

        canvas = new AnaglyphCanvas3D(SimpleUniverse.getPreferredConfiguration(), mainPanel) {

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

                if (selectedModelItem == null) {

                    if (helpImage == null) {
                        Dimension theHelpSize = new Dimension(600, 300);

                        JLabel theHelpLabel = new JLabel(helpHTML);
                        theHelpLabel.setFont(g.getFont());
                        theHelpLabel.setSize(theHelpSize);
                        theHelpLabel.setForeground(Color.white);

                        helpImage = new BufferedImage((int) theHelpSize.getWidth(), (int) theHelpSize.getHeight(),
                                BufferedImage.TYPE_INT_RGB);
                        Graphics2D theHelpGraphics = (Graphics2D) helpImage.getGraphics();
                        theHelpGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        theHelpLabel.paint(theHelpGraphics);
                    }

                    int xp = canvas.getWidth() / 2 - helpImage.getWidth() / 2;
                    int yp = canvas.getHeight() / 2 - helpImage.getHeight() / 2;

                    g.drawImage(helpImage, xp, yp, null);
                }

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

        moveGroup = new TransformGroup();
        moveGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        moveGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        moveGroup.setCapability(BranchGroup.ALLOW_DETACH);
        moveGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        moveGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        moveGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        rootGroup.addChild(modelGroup);
        modelGroup.addChild(moveGroup);

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

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(canvas, BorderLayout.CENTER);

        // Set the stereo mode
        canvas.setAnaglyphMode(AnaglyphMode.REDGREEN_ANAGLYPHS);
        canvas.setStereoMode(StereoMode.OFF);

        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout(FlowLayout.LEFT));
        currentElement = new JLabel();
        currentElement.setMinimumSize(new Dimension(300, 21));
        currentElement.setPreferredSize(new Dimension(300, 21));

        bottom.add(currentElement);
        bottom.add(new DefaultLabel(ERDesignerBundle.STEREOMODE));

        final DefaultComboBox theStereoModeSelector = new DefaultComboBox();
        DefaultComboBoxModel theStereoModeModel = new DefaultComboBoxModel();
        theStereoModeModel.addElement(new NameValuePair(null, ERDesignerBundle.STEREOMODE_OFF));
        theStereoModeModel.addElement(new NameValuePair(AnaglyphMode.REDGREEN_ANAGLYPHS, ERDesignerBundle.STEREOMODE_ANAGLYPH_RED_GREEN));
        theStereoModeModel.addElement(new NameValuePair(AnaglyphMode.REDBLUE_ANAGLYPHS, ERDesignerBundle.STEREOMODE_ANAGLYPH_RED_BLUE));
        theStereoModeModel.addElement(new NameValuePair(AnaglyphMode.GRAY_ANAGLYPHS, ERDesignerBundle.STEREOMODE_ANAGLYPH_GRAY));
        theStereoModeModel.addElement(new NameValuePair(AnaglyphMode.COLOR_ANAGLYPHS, ERDesignerBundle.STEREOMODE_ANAGLYPH_FULLCOLOR));
        theStereoModeModel.addElement(new NameValuePair(AnaglyphMode.HALFCOLOR_ANAGLYPHS, ERDesignerBundle.STEREOMODE_ANAGLYPH_HALFCOLOR));
        theStereoModeModel.addElement(new NameValuePair(AnaglyphMode.OPTIMIZED_ANAGLYPHS, ERDesignerBundle.STEREOMODE_ANAGLYPH_OPTIMIZED));
        theStereoModeSelector.setModel(theStereoModeModel);
        theStereoModeSelector.addActionListener(e -> {
            int theIndex = theStereoModeSelector.getSelectedIndex();
            if (theIndex == 0) {
                canvas.setStereoMode(StereoMode.OFF);
            } else {
                NameValuePair thePair = (NameValuePair) theStereoModeSelector.getSelectedItem();

                canvas.setAnaglyphMode((AnaglyphMode) thePair.value);
                canvas.setStereoMode(StereoMode.ANAGLYPH);
            }
        });

        bottom.add(theStereoModeSelector);

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

    private void setCurrentLayer(int aCurrentLayer) {

        currentLayer = aCurrentLayer;

        BitSet visibleNodes = switchGroup.getChildMask();

        visibleNodes.clear();
        for (int i = 0; i < switchGroup.numChildren(); i++) {
            if (i >= currentLayer) {
                visibleNodes.set(i);
            }
        }

        switchGroup.setChildMask(visibleNodes);

        // Finally, we build the connectors between the items
        connectorGroup.removeAllChildren();

        Set<ModelItem> theAlreadyKnown = new HashSet<>();
        for (int theLayer = currentLayer; theLayer < maxLayer; theLayer++) {
            theAlreadyKnown.addAll(modelLayers.get(theLayer));
        }

        Appearance theLineAppearance = new Appearance();
        theLineAppearance.setColoringAttributes(new ColoringAttributes(1f, 0, 0, ColoringAttributes.NICEST));
        LineAttributes theLineAttributes = new LineAttributes();
        theLineAttributes.setLineWidth(4.0f);
        theLineAppearance.setLineAttributes(theLineAttributes);
        theLineAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.80f));

        Set<String> theProcessed = new HashSet<>();
        for (ModelItem theItem : theAlreadyKnown) {
            if (theItem instanceof Table) {
                // Only tables can have connectors
                for (Relation theRelation : model.getRelations().getForeignKeysFor((Table) theItem)) {
                    Table theExportingTable = theRelation.getExportingTable();
                    if (theExportingTable != theItem && theAlreadyKnown.contains(theExportingTable)) {
                        // We do not display self references

                        boolean add = true;
                        String theSearch1 = theItem.getName() + " " + theExportingTable.getName();
                        String theSearch2 = theExportingTable.getName() + " " + theItem.getName();
                        if (theProcessed.contains(theSearch1) || theProcessed.contains(theSearch2)) {
                            add = false;
                        }

                        if (add) {
                            Point3d[] theLinePoints = new Point3d[2];
                            theLinePoints[0] = modelItemPositions.get(theItem);
                            theLinePoints[1] = modelItemPositions.get(theExportingTable);
                            LineArray theLineArray = new LineArray(2, LineArray.COORDINATES);
                            theLineArray.setCoordinates(0, theLinePoints);
                            Shape3D theLineShape = new Shape3D(theLineArray, theLineAppearance);

                            BranchGroup theGroup = new BranchGroup();
                            theGroup.setCapability(BranchGroup.ALLOW_DETACH);
                            theGroup.addChild(theLineShape);
                            connectorGroup.addChild(theGroup);

                            theProcessed.add(theSearch1);
                            theProcessed.add(theSearch2);
                        }
                    }
                }
            }
        }


        Transform3D theTransform = new Transform3D();
        moveGroup.getTransform(theTransform);
        theTransform.setTranslation(new Vector3d(0, 0, aCurrentLayer * ZDISTANCE));
        moveGroup.setTransform(theTransform);
    }

    private Node createElement(ModelItem aItem) {

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

    @Override
    public void setSelectedObject(ModelItem aSelectedObject) {
        moveGroup.removeAllChildren();

        modelLayers = new ArrayList<>();
        List<ModelItem> theAlreadyKnown = new ArrayList<>();

        // We only show views and tables as the center of attention in the editor.
        if (aSelectedObject instanceof Index) {
            aSelectedObject = ((Index) aSelectedObject).getOwner();
        }
        if (aSelectedObject instanceof Attribute) {
            ModelItem theOwner = ((Attribute) aSelectedObject).getOwner();

            if (theOwner instanceof Table) {
                aSelectedObject = theOwner;
            }
        }
        if (aSelectedObject instanceof Relation) {
            aSelectedObject = ((Relation) aSelectedObject).getImportingTable();
        }
        if (!(aSelectedObject instanceof Table) && !(aSelectedObject instanceof View)) {
            aSelectedObject = null;
        }

        if (aSelectedObject == selectedModelItem) {
            return;
        }

        selectedModelItem = null;

        if (aSelectedObject != null) {

            selectedModelItem = aSelectedObject;

            // There is always one layer in the graph
            List<ModelItem> layer1 = new ArrayList<>();
            layer1.add(aSelectedObject);
            theAlreadyKnown.add(aSelectedObject);
            modelLayers.add(layer1);

            if (aSelectedObject instanceof Table) {

                // Tables can have multiple layers
                Table theTable = (Table) aSelectedObject;

                List<Relation> theIncomingRelations = model.getRelations().getForeignKeysFor(theTable);
                while (theIncomingRelations.size() > 0) {
                    List<ModelItem> nextLayer = new ArrayList<>();
                    theIncomingRelations.stream().filter(theRelation -> !nextLayer.contains(theRelation.getExportingTable()) && !theAlreadyKnown.contains(theRelation.getExportingTable())).forEach(theRelation -> {
                        nextLayer.add(theRelation.getExportingTable());
                        theAlreadyKnown.add(theRelation.getExportingTable());
                    });

                    theIncomingRelations.clear();
                    for (ModelItem theItem : nextLayer) {
                        theIncomingRelations.addAll(model.getRelations().getForeignKeysFor((Table) theItem));
                    }

                    if (nextLayer.size() > 0) {
                        modelLayers.add(nextLayer);
                    }
                }
            }
        }

        maxLayer = modelLayers.size();

        modelItemPositions = new HashMap<>();

        switchGroup = new Switch(Switch.CHILD_MASK);
        switchGroup.setCapability(Switch.ALLOW_SWITCH_WRITE);
        switchGroup.setCapability(Switch.ALLOW_SWITCH_READ);

        Font3D f3d = new Font3D(mainPanel.getFont(), new FontExtrusion());

        // Now we iterate over the known layers and build them as 3d objects
        for (int theLayer = 0; theLayer < modelLayers.size(); theLayer++) {

            BranchGroup theLayerGroup = new BranchGroup();
            float theZOffset = -ZDISTANCE * theLayer;

            // Starting with the layer object itself
            Transform3D theTransform = new Transform3D();
            theTransform.setTranslation(new Vector3d(0, 0, theZOffset - 0.004f));
            TransformGroup theRoot = new TransformGroup(theTransform);

            Appearance theAppearance = new Appearance();
            theAppearance.setColoringAttributes(new ColoringAttributes(1f, 1f, 0.94f, ColoringAttributes.NICEST));

            theAppearance.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 0.95f));
            PolygonAttributes thePa = new PolygonAttributes();
            thePa.setCullFace(PolygonAttributes.CULL_NONE);
            theAppearance.setPolygonAttributes(thePa);

            Box theBox = new Box(1f, 1f, 0.002f, Primitive.GENERATE_NORMALS, theAppearance);
            theRoot.addChild(theBox);

            BranchGroup theGroup = new BranchGroup();
            theGroup.setCapability(BranchGroup.ALLOW_DETACH);
            theGroup.addChild(theRoot);
            theLayerGroup.addChild(theGroup);

            Transform3D theTextTrans = new Transform3D();
            theTextTrans.setScale(0.01f);
            theTextTrans.setTranslation(new Vector3d(-0.9, 0.8, 0));
            TransformGroup theTextTransform = new TransformGroup(theTextTrans);
            Text3D theLayerText = new Text3D(f3d, "Layer " + (theLayer + 1) + " / " + maxLayer, new Point3f(
                    0, 0, 0));
            theLayerText.setCapability(Geometry.ALLOW_INTERSECT);
            Shape3D s3D1 = new Shape3D();
            s3D1.setGeometry(theLayerText);

            theTextTransform.addChild(s3D1);

            theRoot.addChild(theTextTransform);


            // And with the layer objects
            // For now, we use radial layout
            List<ModelItem> theItems = modelLayers.get(theLayer);

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
                theLayerGroup.addChild(Helper.addElementAt(theButton2, new Vector3f((float) mx, (float) my, theZOffset), 1f));

                theCurrentAngle += theIncrement;

                modelItemPositions.put(theItem, new Point3d(mx, my, theZOffset));
            }

            switchGroup.addChild(theLayerGroup);
        }

        BranchGroup theSwitchGroup = new BranchGroup();
        theSwitchGroup.setCapability(BranchGroup.ALLOW_DETACH);
        theSwitchGroup.addChild(switchGroup);
        moveGroup.addChild(theSwitchGroup);

        connectorGroup = new BranchGroup();
        connectorGroup.setCapability(BranchGroup.ALLOW_DETACH);
        connectorGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        connectorGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        connectorGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        moveGroup.addChild(connectorGroup);

        setCurrentLayer(0);
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

    @Override
    public void initLayoutMenu(ERDesignerComponent aComponent, DefaultMenu aLayoutMenu) {
        aLayoutMenu.setEnabled(false);
    }

    @Override
    public void setIntelligentLayoutEnabled(boolean aStatus) {
    }

    @Override
    public boolean supportsIntelligentLayout() {
        return false;
    }
}