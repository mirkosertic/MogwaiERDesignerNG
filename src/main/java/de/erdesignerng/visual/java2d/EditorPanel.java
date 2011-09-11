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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class EditorPanel extends JPanel {

    public static class EditorComponent {
        int currentRadius;
        int finalRadius;
        float angle;
        JComponent paintComponent;
        boolean center;
        Object userObject;

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

        EditorComponent from;
        EditorComponent to;
        Color color;

        public Connector(EditorComponent aFrom, EditorComponent aTo) {
            from = aFrom;
            to = aTo;
            int r = 75 + (int) (Math.random() * 180);
            color = new Color(r, r, r);
        }
    }

    private List<EditorComponent> components = new ArrayList<EditorComponent>();
    private List<Connector> connectors = new ArrayList<Connector>();
    private Point lastMouseLocation;

    public EditorPanel() {
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                for (EditorComponent theComponent : components) {
                    if (!theComponent.center) {
                        int aAmount = e.getUnitsToScroll() * 2;
                        if (theComponent.currentRadius + aAmount > 0) {
                            theComponent.currentRadius += aAmount;
                        }
                    }
                }
                EditorPanel.this.invalidate();
                EditorPanel.this.repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                lastMouseLocation = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    EditorComponent theSelected = findEditorComponentAt(e.getPoint());
                    if (theSelected != null) {
                        componentClicked(theSelected);
                    }
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                Dimension theCenter = EditorPanel.this.getSize();
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

                    for (EditorComponent theComponent : components) {
                        if (!theComponent.center) {
                            theComponent.angle += newAngle - oldAngle;
                        }
                    }
                    EditorPanel.this.invalidate();
                    EditorPanel.this.repaint();

                    lastMouseLocation = theCurrentLocation;
                }
            }
        });
        setDoubleBuffered(true);
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

    public void componentClicked(EditorComponent aComponent) {
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

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            EditorPanel.this.invalidate();
                            EditorPanel.this.repaint();
                        }
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
