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
package de.erdesignerng.visual.common;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.*;
import de.mogwai.common.client.looks.components.action.DefaultAction;
import de.mogwai.common.client.looks.components.menu.DefaultMenuItem;
import de.mogwai.common.i18n.ResourceHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for context menues.
 * 
 * @author mirkosertic
 */
public final class ContextMenuFactory {

	private ContextMenuFactory() {
	}

	public static void addActionsToMenu(JPopupMenu aMenu,
			List<ModelItem> aItemList, final ERDesignerComponent aComponent) {

		ResourceHelper theHelper = ResourceHelper
				.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

		final List<ModelItem> theNewSubjectAreaItems = new ArrayList<ModelItem>();
		final List<ModelItem> theItemsToBeDeleted = new ArrayList<ModelItem>();

		for (final ModelItem theUserObject : aItemList) {
			if (theUserObject instanceof Table) {

				Table theTable = (Table) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITTABLE, theTable.getName()));
				theEditItem.addActionListener(new EditTableCommand(aComponent,
						theTable));

				aMenu.add(theEditItem);

				JMenuItem theDataBrowserItem = new JMenuItem();
				theDataBrowserItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.SHOWDATAOF, theTable.getName()));
				theDataBrowserItem.addActionListener(new DataBrowserCommand(
						aComponent, theTable));

				aMenu.add(theDataBrowserItem);

				theNewSubjectAreaItems.add(theTable);
				theItemsToBeDeleted.add(theTable);
			}
			if (theUserObject instanceof View) {

				View theView = (View) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITVIEW, theView.getName()));
				theEditItem.addActionListener(new EditViewCommand(aComponent,
						theView));

				JMenuItem theDataBrowserItem = new JMenuItem();
				theDataBrowserItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.SHOWDATAOF, theView.getName()));
				theDataBrowserItem.addActionListener(new DataBrowserCommand(
						aComponent, theView));

				aMenu.add(theDataBrowserItem);

				aMenu.add(theEditItem);

				theNewSubjectAreaItems.add(theView);
				theItemsToBeDeleted.add(theView);
			}
			if (theUserObject instanceof Relation) {

				Relation theRelation = (Relation) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITRELATION, theRelation.getName()));
				theEditItem.addActionListener(new EditRelationCommand(
						aComponent, theRelation));

				aMenu.add(theEditItem);
				theItemsToBeDeleted.add(theRelation);
			}
			if (theUserObject instanceof CustomType) {

				CustomType theCustomType = (CustomType) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITCUSTOMTYPE, theCustomType
								.getName()));
				theEditItem.addActionListener(new EditCustomTypesCommand(
						aComponent, theCustomType));

				aMenu.add(theEditItem);

			}
			if (theUserObject instanceof Domain) {

				Domain theDomain = (Domain) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITDOMAIN, theDomain.getName()));
				theEditItem.addActionListener(new EditDomainCommand(aComponent,
						theDomain));

				aMenu.add(theEditItem);

			}
			if (theUserObject instanceof SubjectArea) {

				final SubjectArea theSubjectArea = (SubjectArea) theUserObject;
				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITSUBJECTAREA, theSubjectArea
								.getName()));
				theEditItem.addActionListener(new EditSubjectAreaCommand(
						aComponent, theSubjectArea));

				aMenu.add(theEditItem);

				DefaultAction theHideAction = new DefaultAction(
						ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.HIDE);
				DefaultMenuItem theAddItem = new DefaultMenuItem(theHideAction);
				theHideAction.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						aComponent.commandHideSubjectArea(theSubjectArea);
					}
				});

				aMenu.add(theAddItem);

			}
			if (theUserObject instanceof Attribute) {

				Attribute theAttribute = (Attribute) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem
						.setText(theHelper.getFormattedText(
								ERDesignerBundle.EDITATTRIBUTE, theAttribute
										.getName()));
				theEditItem.addActionListener(new EditTableCommand(aComponent,
						theAttribute.getOwner(), theAttribute));

				aMenu.add(theEditItem);
			}
			if (theUserObject instanceof Index) {

				Index theIndex = (Index) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITINDEX, theIndex.getName()));
				theEditItem.addActionListener(new EditTableCommand(aComponent,
						theIndex.getOwner(), theIndex));

				aMenu.add(theEditItem);
			}
		}

		if (theNewSubjectAreaItems.size() > 0) {

			DefaultAction theAddAction = new DefaultAction(
					ERDesignerBundle.BUNDLE_NAME,
					ERDesignerBundle.ADDTONEWSUBJECTAREA);
			DefaultMenuItem theAddItem = new DefaultMenuItem(theAddAction);
			theAddAction.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					aComponent
							.commandAddToNewSubjectArea(theNewSubjectAreaItems);
				}
			});

			aMenu.addSeparator();
			aMenu.add(theAddItem);
		}

		if (theItemsToBeDeleted.size() > 0) {

			DefaultAction theDeleteAction = new DefaultAction(
					ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.DELETE);
			DefaultMenuItem theDeleteItem = new DefaultMenuItem(theDeleteAction);
			theDeleteAction.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					aComponent.commandDelete(theItemsToBeDeleted);
				}
			});
			aMenu.addSeparator();
			aMenu.add(theDeleteItem);
		}
	}
}
