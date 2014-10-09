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
package de.erdesignerng.visual.java2d;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.util.MavenPropertiesLocator;
import de.erdesignerng.visual.FadeInFadeOutHelper;
import de.mogwai.common.i18n.ResourceHelper;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class EditorPanel extends JPanel {

    private static final ResourceHelper HELPER = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

    private final String helpHTML;
    private BufferedImage helpImage;

    public static class EditorComponent {
        int currentRadius;
        final int finalRadius;
        float angle;
        final JComponent paintComponent;
        boolean center;
        public final Object userObject;

        public EditorComponent(Object aUserObject, float aAngle, int aRadius, JComponent aPaintComponent) {
            finalRadius = aRadius;
            angle = aAngle;
            paintComponent = aPaintComponent;
            userObject = aUserObject;
        }

        public EditorComponent(Object aUserObject, float aAngle, int aRadius, JComponent aPaintComponent, boolean aCenter) {
            finalRadius = aRadius;
            angle = aAngle;
            paintComponent = aPaintComponent;
            center = aCenter;
            userObject = aUserObject;
        }
    }

    public static class Connector {

        final EditorComponent from;
        final EditorComponent to;
        final Color color;

        public Connector(EditorComponent aFrom, EditorComponent aTo) {
            from = aFrom;
            to = aTo;
            int r = 75 + (int) (Math.random() * 180);
            color = new Color(r, r, r);
        }
    }

    private final List<EditorComponent> components = new ArrayList<>();
    private final List<Connector> connectors = new ArrayList<>();
    private Point lastMouseLocation;
    private EditorComponent fadingComponent;
    private final FadeInFadeOutHelper fadingHelper;
    private final ResourceHelper resourceHelper;

    public EditorPanel() {

        StringBuilder theHelpHtml = new StringBuilder();
        theHelpHtml.append("<html>");
        theHelpHtml.append("<h1>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_2D3D_EDITOR_1));
        theHelpHtml.append("</h1>");
        theHelpHtml.append("<ul>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_2D_EDITOR_1));
        theHelpHtml.append("</li>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_2D_EDITOR_2));
        theHelpHtml.append("</li>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_2D_EDITOR_3));
        theHelpHtml.append("</li>");
        theHelpHtml.append("<li>");
        theHelpHtml.append(HELPER.getText(ERDesignerBundle.HELPTEXT_2D_EDITOR_4));
        theHelpHtml.append("</li>");
        theHelpHtml.append("</ul>");
        theHelpHtml.append("</html>");
        helpHTML = theHelpHtml.toString();

        setBackground(Color.black);
        resourceHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
        fadingHelper = new FadeInFadeOutHelper() {
            @Override
            public void doRepaint() {
                invalidate();
                repaint();
            }
        };

        addMouseWheelListener(e -> {
            components.stream().filter(theComponent -> !theComponent.center).forEach(theComponent -> {
                int aAmount = e.getUnitsToScroll() * 2;
                if (theComponent.currentRadius + aAmount > 0) {
                    theComponent.currentRadius += aAmount;
                }
            });
            invalidate();
            repaint();
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                lastMouseLocation = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                EditorComponent theSelected = findEditorComponentAt(e.getPoint());
                if (theSelected != null) {
                    componentClicked(theSelected, e);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                EditorComponent theSelected = findEditorComponentAt(e.getPoint());
                if (theSelected != null) {
                    if (theSelected != fadingComponent) {
                        fadingHelper.setComponentToHighlightFadeOut(fadingHelper.getComponentToHighlight() != null);
                        fadingHelper.setComponentToHighlightNext(getHighlightComponentFor(theSelected));
                    }
                } else {
                    fadingHelper.setComponentToHighlightFadeOut(true);
                    fadingHelper.setComponentToHighlightNext(null);
                }
                fadingComponent = theSelected;

                fadingHelper.startWaitTimer();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                Dimension theCenter = getSize();
                int theX = theCenter.width / 2;
                int theY = theCenter.height / 2;

                if (lastMouseLocation == null) {
                    if (findEditorComponentAt(e.getPoint()) != null) {
                        lastMouseLocation = e.getPoint();
                    }
                } else {
                    Point theCurrentLocation = e.getPoint();

                    int mx1 = lastMouseLocation.x - theX;
                    int my1 = lastMouseLocation.y - theY;

                    int mx2 = theCurrentLocation.x - theX;
                    int my2 = theCurrentLocation.y - theY;

                    float oldAngle = getAngle(mx1, my1);
                    float newAngle = getAngle(mx2, my2);

                    components.stream().filter(theComponent -> !theComponent.center).forEach(theComponent -> {
                        theComponent.angle += newAngle - oldAngle;
                    });
                    invalidate();
                    repaint();

                    lastMouseLocation = theCurrentLocation;
                }
            }
        });
        setDoubleBuffered(true);
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(Color.black);
    }

    protected JComponent getHighlightComponentFor(EditorComponent aComponent) {
        return null;
    }

    private EditorComponent findEditorComponentAt(Point aPoint) {
        EditorComponent theSelected = null;

        Dimension theCenter = getSize();
        int centerX = theCenter.width / 2;
        int centerY = theCenter.height / 2;

        for (EditorComponent theComponent : components) {

            Dimension theComponentSize = theComponent.paintComponent.getSize();

            int tX = centerX + (int) (Math.cos(Math.toRadians(theComponent.angle)) * theComponent.currentRadius) - theComponentSize.width / 2;
            int ty = centerY + (int) (Math.sin(Math.toRadians(theComponent.angle)) * theComponent.currentRadius) - theComponentSize.height / 2;

            Rectangle theRectangle = new Rectangle(tX, ty, theComponentSize.width, theComponentSize.height);
            if (theRectangle.contains(aPoint)) {
                theSelected = theComponent;
            }
        }
        return theSelected;
    }

    public void componentClicked(EditorComponent aComponent, MouseEvent aEvent) {
    }

    public void cleanup() {
        lastMouseLocation = null;
        components.clear();
        connectors.clear();
    }

    private float getAngle(float dx, float dy) {
        int sector;
        if (dx > 0) {
            if (dy >= 0) {
                sector = 1;
            } else {
                sector = 4;
                dy = -dy;
            }
        } else {
            if (dy >= 0) {
                sector = 2;
            } else {
                sector = 3;
                dy = -dy;
            }
            dx = -dx;
        }

        if (dx == 0) {
            if (sector == 2) {
                return 90;
            }
            if (sector == 3) {
                return 180;
            }
        }

        float theAngle = (float) Math.toDegrees(Math.atan(dy / dx));
        switch (sector) {
            case 1:
                return theAngle;
            case 2:
                return 180 - theAngle;
            case 3:
                return 180 + theAngle;
            case 4:
                return 360 - theAngle;
        }
        return 0;
    }

    public void add(EditorComponent aComponent) {
        components.add(aComponent);
    }

    public void add(Connector aConnector) {
        connectors.add(aConnector);
    }

    public boolean hasConnection(EditorComponent a, EditorComponent b) {
        for (Connector theConnector : connectors) {
            if (theConnector.from == a && theConnector.to == b) {
                return true;
            }
            if (theConnector.to == a && theConnector.from == b) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Dimension theSize = getSize();
        int centerX = theSize.width / 2;
        int centerY = theSize.height / 2;

        String theVersion = MavenPropertiesLocator.getERDesignerVersionInfo();
        String theTitle = "(c) " + resourceHelper.getText(ERDesignerBundle.TITLE) + " "
                + theVersion + " ";

        g.setColor(Color.white);
        g.setFont(new Font("Helvetica", Font.PLAIN, 12));
        g.drawString(theTitle, 10, 20);

        // If we do not have something to display, display the help text
        if (components.isEmpty()) {

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

            int xp = centerX - helpImage.getWidth() / 2;
            int yp = centerY - helpImage.getHeight() / 2;

            g.drawImage(helpImage, xp, yp, null);
        }

        int outerRadius = -1;
        for (Connector theConnector : connectors) {

            if (theConnector.from.center != theConnector.to.center) {

                g.setColor(Color.white);

                int tX1 = centerX + (int) (Math.cos(Math.toRadians(theConnector.from.angle)) * theConnector.from.currentRadius);
                int ty1 = centerY + (int) (Math.sin(Math.toRadians(theConnector.from.angle)) * theConnector.from.currentRadius);

                int tX2 = centerX + (int) (Math.cos(Math.toRadians(theConnector.to.angle)) * theConnector.to.currentRadius);
                int ty2 = centerY + (int) (Math.sin(Math.toRadians(theConnector.to.angle)) * theConnector.to.currentRadius);

                g.drawLine(tX1, ty1, tX2, ty2);
            } else {

                g.setColor(theConnector.color);

                if (outerRadius < 1) {
                    outerRadius = (int) (theConnector.to.currentRadius * 1.5);
                }

                int tX1 = centerX + (int) (Math.cos(Math.toRadians(theConnector.from.angle)) * theConnector.from.currentRadius);
                int ty1 = centerY + (int) (Math.sin(Math.toRadians(theConnector.from.angle)) * theConnector.from.currentRadius);

                int tX1O = centerX + (int) (Math.cos(Math.toRadians(theConnector.from.angle)) * outerRadius);
                int ty1O = centerY + (int) (Math.sin(Math.toRadians(theConnector.from.angle)) * outerRadius);
                g.drawLine(tX1, ty1, tX1O, ty1O);

                int tX2 = centerX + (int) (Math.cos(Math.toRadians(theConnector.to.angle)) * theConnector.to.currentRadius);
                int ty2 = centerY + (int) (Math.sin(Math.toRadians(theConnector.to.angle)) * theConnector.to.currentRadius);

                int tX2O = centerX + (int) (Math.cos(Math.toRadians(theConnector.to.angle)) * outerRadius);
                int ty2O = centerY + (int) (Math.sin(Math.toRadians(theConnector.to.angle)) * outerRadius);
                g.drawLine(tX2, ty2, tX2O, ty2O);

                float theAngleFrom;
                float theAngleTo;
                int lastX;
                int lastY;
                int finalX;
                int finalY;

                if (theConnector.from.angle < theConnector.to.angle) {
                    theAngleFrom = theConnector.from.angle;
                    theAngleTo = theConnector.to.angle;
                    lastX = tX1O;
                    lastY = ty1O;
                    finalX = tX2O;
                    finalY = ty2O;
                } else {
                    theAngleFrom = theConnector.to.angle;
                    theAngleTo = theConnector.from.angle;
                    lastX = tX2O;
                    lastY = ty2O;
                    finalX = tX1O;
                    finalY = ty1O;
                }

                float theCurrentAngle = theAngleFrom;
                while (theCurrentAngle < theAngleTo) {

                    int x1 = centerX + (int) (Math.cos(Math.toRadians(theCurrentAngle)) * outerRadius);
                    int y1 = centerY + (int) (Math.sin(Math.toRadians(theCurrentAngle)) * outerRadius);

                    g.drawLine(lastX, lastY, x1, y1);
                    lastX = x1;
                    lastY = y1;

                    theCurrentAngle += 3;
                }

                g.drawLine(lastX, lastY, finalX, finalY);

                outerRadius += 5;
            }
        }

        for (EditorComponent theComponent : components) {

            Dimension theComponentSize = theComponent.paintComponent.getSize();

            int tX = centerX + (int) (Math.cos(Math.toRadians(theComponent.angle)) * theComponent.currentRadius) - theComponentSize.width / 2;
            int ty = centerY + (int) (Math.sin(Math.toRadians(theComponent.angle)) * theComponent.currentRadius) - theComponentSize.height / 2;

            g.translate(tX, ty);
            theComponent.paintComponent.paint(g);
            g.translate(-tX, -ty);
        }

        if (fadingHelper.getComponentToHighlight() != null) {
            Graphics2D theGraphics = (Graphics2D) g;
            g.translate(theSize.width - fadingHelper.getComponentToHighlightPosition(), 10);
            theGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    0.85f));
            fadingHelper.getComponentToHighlight().paint(g);
        }
    }

    public void explodeAnimation() {
        Thread theThread = new Thread() {
            @Override
            public void run() {
                boolean needsMotification = true;
                while (needsMotification) {

                    needsMotification = false;
                    for (EditorComponent theComponent : components) {
                        if (theComponent.currentRadius < theComponent.finalRadius) {
                            theComponent.currentRadius += 10;
                            needsMotification = true;
                        }
                    }

                    SwingUtilities.invokeLater(() -> {
                        invalidate();
                        repaint();
                    });

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        theThread.start();
    }
}
