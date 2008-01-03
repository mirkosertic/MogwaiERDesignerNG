package de.erdesignerng.visual.components;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2008-01-03 13:11:18 $
 */
public class StatusBar extends JLabel {

	public StatusBar() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(10, 21));
	}

}
