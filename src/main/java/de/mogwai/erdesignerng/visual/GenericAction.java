package de.mogwai.erdesignerng.visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public class GenericAction extends AbstractAction {

	private ActionListener actionListener;

	public GenericAction(String aKey) {
		this(aKey, (ActionListener) null);
	}

	public GenericAction(String aKey, ActionListener aListener) {
		putValue(NAME, aKey);
		actionListener = aListener;
	}

	public GenericAction(String aKey, Icon aIcon, ActionListener aListener) {
		this(aKey, aListener);
		putValue(SMALL_ICON, aIcon);
	}

	public void actionPerformed(ActionEvent e) {
		if (actionListener != null) {
			actionListener.actionPerformed(e);
		}
	}
}
