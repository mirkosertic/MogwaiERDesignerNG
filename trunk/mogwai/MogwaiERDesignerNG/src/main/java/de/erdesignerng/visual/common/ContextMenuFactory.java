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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Factory for context menues.
 *
 * @author mirkosertic
 */
public final class ContextMenuFactory {

	private ContextMenuFactory() {
	}

	public static void addActionsToMenu(GenericModelEditor aEditor,
										JPopupMenu aMenu,
										List<ModelItem> aItemList) {

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
				theEditItem.addActionListener(new EditTableCommand(theTable));
				aMenu.add(theEditItem);

				final Table theClone = theTable.createCopy();

				Point2D theLocation = theClone.getProperties().getPoint2DProperty(ModelItem.PROPERTY_LOCATION);
				final Point2D theNewLocation = new Point2D.Double(theLocation.getX() + 20, theLocation.getY() + 20);
				theClone.getProperties().setPointProperty(ModelItem.PROPERTY_LOCATION, (int) theNewLocation.getX(), (int) theNewLocation.getY());

				JMenuItem theCloneItem = new JMenuItem();
				theCloneItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.CLONETABLE, theClone.getName()));
				theCloneItem.addActionListener(new EditTableCommand(theClone) {
					@Override
					protected void beforeRefresh() {
						ERDesignerComponent.getDefault().commandCreateTable(theClone, theNewLocation);
					}
				});

				aMenu.add(theCloneItem);

				JMenuItem theDataBrowserItem = new JMenuItem();
				theDataBrowserItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.SHOWDATAOF, theTable.getName()));
				theDataBrowserItem.addActionListener(new DataBrowserCommand(theTable));

				aMenu.add(theDataBrowserItem);

				if (aEditor.supportShowingAndHidingOfRelations()) {
					JMenuItem theHideRelationsItem = new JMenuItem();
					theHideRelationsItem.setText(theHelper.getFormattedText(
							ERDesignerBundle.HIDEALLRELATIONSFOR, theTable.getName()));
					theHideRelationsItem.addActionListener(new ShowHideTableRelationsCommand(theTable, false));

					aMenu.add(theHideRelationsItem);

					JMenuItem theShowRelationsItem = new JMenuItem();
					theShowRelationsItem.setText(theHelper.getFormattedText(
							ERDesignerBundle.SHOWALLRELATIONSFOR, theTable.getName()));
					theShowRelationsItem.addActionListener(new ShowHideTableRelationsCommand(theTable, true));

					aMenu.add(theShowRelationsItem);
				}

				theNewSubjectAreaItems.add(theTable);
				theItemsToBeDeleted.add(theTable);
			}
			if (theUserObject instanceof View) {

				View theView = (View) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITVIEW, theView.getName()));
				theEditItem.addActionListener(new EditViewCommand(theView));

				final View theClone = theView.createCopy();

				Point2D theLocation = theClone.getProperties().getPoint2DProperty(ModelItem.PROPERTY_LOCATION);
				final Point2D theNewLocation = new Point2D.Double(theLocation.getX() + 20, theLocation.getY() + 20);
				theClone.getProperties().setPointProperty(ModelItem.PROPERTY_LOCATION, (int) theNewLocation.getX(), (int) theNewLocation.getY());

				JMenuItem theCloneItem = new JMenuItem();
				theCloneItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.CLONEVIEW, theClone.getName()));
				theCloneItem.addActionListener(new EditViewCommand(theClone) {
					@Override
					protected void beforeRefresh() {
						ERDesignerComponent.getDefault().commandCreateView(theClone, theNewLocation);
					}
				});

				aMenu.add(theCloneItem);

				JMenuItem theDataBrowserItem = new JMenuItem();
				theDataBrowserItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.SHOWDATAOF, theView.getName()));
				theDataBrowserItem.addActionListener(new DataBrowserCommand(theView));

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
				theEditItem.addActionListener(new EditRelationCommand(theRelation));

				aMenu.add(theEditItem);
				theItemsToBeDeleted.add(theRelation);
			}
			if (theUserObject instanceof CustomType) {

				CustomType theCustomType = (CustomType) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITCUSTOMTYPE, theCustomType
						.getName()));
				theEditItem.addActionListener(new EditCustomTypesCommand(theCustomType));

				aMenu.add(theEditItem);

			}
			if (theUserObject instanceof Domain) {

				Domain theDomain = (Domain) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITDOMAIN, theDomain.getName()));
				theEditItem.addActionListener(new EditDomainCommand(theDomain));

				aMenu.add(theEditItem);

			}
			if (theUserObject instanceof SubjectArea) {

				final SubjectArea theSubjectArea = (SubjectArea) theUserObject;
				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITSUBJECTAREA, theSubjectArea
						.getName()));
				theEditItem.addActionListener(new EditSubjectAreaCommand(theSubjectArea));

				aMenu.add(theEditItem);

				if (theSubjectArea.isVisible()) {
					DefaultAction theHideAction = new DefaultAction(
							ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.HIDE);
					DefaultMenuItem theHideMenuItem = new DefaultMenuItem(theHideAction);
					theHideAction.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							ERDesignerComponent.getDefault().commandHideSubjectArea(theSubjectArea);
						}
					});

					aMenu.add(theHideMenuItem);
				}

			}

			if (theUserObject instanceof Attribute) {
				Attribute theAttribute = (Attribute) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(ERDesignerBundle.EDITATTRIBUTE, theAttribute.getName()));
				ModelItem theOwner = theAttribute.getOwner();
				if (theOwner instanceof Table) {
					theEditItem.addActionListener(new EditTableCommand((Table) theOwner, theAttribute));
				}

				aMenu.add(theEditItem);
			}

			if (theUserObject instanceof Index) {

				Index theIndex = (Index) theUserObject;

				JMenuItem theEditItem = new JMenuItem();
				theEditItem.setText(theHelper.getFormattedText(
						ERDesignerBundle.EDITINDEX, theIndex.getName()));
				theEditItem.addActionListener(new EditTableCommand(theIndex.getOwner(), theIndex));

				aMenu.add(theEditItem);
			}
		}

		if (theNewSubjectAreaItems.size() > 0 && aEditor.supportsSubjectAreas()) {

			DefaultAction theAddAction = new DefaultAction(
					ERDesignerBundle.BUNDLE_NAME,
					ERDesignerBundle.ADDTONEWSUBJECTAREA);
			DefaultMenuItem theAddItem = new DefaultMenuItem(theAddAction);
			theAddAction.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ERDesignerComponent.getDefault()
							.commandAddToNewSubjectArea(theNewSubjectAreaItems);
				}
			});

			aMenu.addSeparator();
			aMenu.add(theAddItem);
		}

		if (theItemsToBeDeleted.size() > 0 && aEditor.supportsDeletionOfObjects()) {

			DefaultAction theDeleteAction = new DefaultAction(
					ERDesignerBundle.BUNDLE_NAME, ERDesignerBundle.DELETE);
			DefaultMenuItem theDeleteItem = new DefaultMenuItem(theDeleteAction);
			theDeleteAction.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					ERDesignerComponent.getDefault().commandDelete(theItemsToBeDeleted);
				}
			});
			aMenu.addSeparator();
			aMenu.add(theDeleteItem);
		}
	}
}