package de.mogwai.erdesignerng.visual.components;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2007-07-08 10:06:41 $
 */
public class StatusBar extends JLabel {

	public StatusBar() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(10, 21));
	}

}
