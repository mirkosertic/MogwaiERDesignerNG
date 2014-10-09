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
package de.erdesignerng.visual;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.Timer;

public abstract class FadeInFadeOutHelper {

    private JComponent componentToHighlight;
    private int componentToHighlightPosition;
    private boolean componentToHighlightFadeOut;
    private Timer componentToHighlightTimer;
    private final Timer componentToHighlightWaitTimer;
    private JComponent componentToHighlightNext;

    public FadeInFadeOutHelper() {
        componentToHighlightTimer = new Timer(50, e -> {

            if (componentToHighlightFadeOut) {
                if (componentToHighlightPosition > 0) {
                    componentToHighlightPosition -= 40;
                } else {
                    componentToHighlightFadeOut = false;
                    componentToHighlight = componentToHighlightNext;
                }
            } else {
                if (componentToHighlightNext != null) {
                    componentToHighlight = componentToHighlightNext;
                    componentToHighlightNext = null;
                }
                if (componentToHighlight != null) {
                    Dimension theSize = componentToHighlight.getSize();
                    if (componentToHighlightPosition < theSize.width + 10) {
                        int theStep = theSize.width + 10 - componentToHighlightPosition;
                        if (theStep > 40) {
                            theStep = 40;
                        }
                        componentToHighlightPosition += theStep;
                    } else {
                        componentToHighlightTimer.stop();
                    }
                } else {
                    componentToHighlightTimer.stop();
                }
            }

            doRepaint();
        });
        componentToHighlightWaitTimer = new Timer(1000, e -> componentToHighlightTimer.start());
        componentToHighlightWaitTimer.setRepeats(false);
    }

    public abstract void doRepaint();

    public JComponent getComponentToHighlight() {
        return componentToHighlight;
    }

    public int getComponentToHighlightPosition() {
        return componentToHighlightPosition;
    }

    public void setComponentToHighlightFadeOut(boolean componentToHighlightFadeOut) {
        this.componentToHighlightFadeOut = componentToHighlightFadeOut;
    }

    public void setComponentToHighlightNext(JComponent componentToHighlightNext) {
        this.componentToHighlightNext = componentToHighlightNext;
    }

    public void startWaitTimer() {
        componentToHighlightWaitTimer.stop();
        componentToHighlightWaitTimer.start();
    }
}