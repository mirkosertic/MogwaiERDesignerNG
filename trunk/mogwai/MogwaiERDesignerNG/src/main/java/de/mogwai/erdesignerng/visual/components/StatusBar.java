package de.mogwai.erdesignerng.visual.components;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

public class StatusBar extends JLabel {

	public StatusBar() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(10, 21));
	}

}
