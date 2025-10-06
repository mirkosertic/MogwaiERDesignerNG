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
package de.erdesignerng.visual.editor.table;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DataType;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.Domain;
import de.erdesignerng.model.Table;
import de.erdesignerng.visual.editor.ModelItemTableModel;

public class AttributeTableModel extends ModelItemTableModel<Attribute<Table>> {

	public AttributeTableModel() {
		columnNames.add(HELPER.getText(ERDesignerBundle.NAME));
		columnNames.add(HELPER.getText(ERDesignerBundle.DATATYPE));
		columnNames.add(HELPER.getText(ERDesignerBundle.SIZE));
		columnNames.add(HELPER.getText(ERDesignerBundle.FRACTION));
		columnNames.add(HELPER.getText(ERDesignerBundle.SCALE));
		columnNames.add(HELPER.getText(ERDesignerBundle.NULLABLE));
		columnNames.add(HELPER.getText(ERDesignerBundle.DEFAULT));
		columnNames.add(HELPER.getText(ERDesignerBundle.EXTRA));
		columnNames.add(HELPER.getText(ERDesignerBundle.COMMENTS));
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                // name
                    String.class;
            case 1 ->
                // type
                    DataType.class;
            case 2 ->
                // size
                    Integer.class;
            case 3 ->
                // fraction
                    Integer.class;
            case 4 ->
                // scale
                    Integer.class;
            case 5 ->
                // nullable
                    Boolean.class;
            case 6 ->
                // default
                    String.class;
            case 7 ->
                // extra
                    String.class;
            case 8 ->
                // comment
                    String.class;
            default -> throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
        };
    }

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		final Attribute<Table> theAttribute = rowData.get(rowIndex);
		final DataType theDataType = theAttribute.getDatatype();
		switch (columnIndex) {
			case 0:
				// name
				return true;
			case 1:
				// type
				return true;
			case 2:
				// size
				if (theDataType == null) {
					// In case of an new attribute disable
					// this until a datatype was chosen
					return false;
				}
				if (theDataType.supportsSize() && !theDataType.isDomain()) {
					return true;
				}
				return false;
			case 3:
				// fraction
				if (theDataType == null) {
					// In case of an new attribute disable
					// this until a datatype was chosen
					return false;
				}
				if (theDataType.supportsFraction() && !theDataType.isDomain()) {
					return true;
				}
				return false;
			case 4:
				// scale
				if (theDataType == null) {
					// In case of an new attribute disable
					// this until a datatype was chosen
					return false;
				}
				if (theDataType.supportsScale() && !theDataType.isDomain()) {
					return true;
				}
				return false;
			case 5:
				// nullable
				return true;
			case 6:
				// default
				return true;
			case 7:
				// extra
				if (theDataType == null) {
					// In case of an new attribute disable
					// this until a datatype was chosen
					return false;
				}

				if (theDataType.supportsExtra()) {
					return true;
				}
				return false;
			case 8:
				// comment
				return true;
		}
		throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final Attribute<Table> theAttribute = rowData.get(rowIndex);
		final DataType theDataType = theAttribute.getDatatype();
		switch (columnIndex) {
			case 0:
				// name
				return theAttribute.getName();
			case 1:
				// type
				return theAttribute.getDatatype();
			case 2:
				// size
				if (theDataType == null) {
					// In case of an new attribute show nothing
					// until a datatype was chosen
					return null;
				}

				if (theDataType.supportsSize() && !theDataType.isDomain()) {
					return theAttribute.getSize();
				}
				return null;
			case 3:
				// fraction
				if (theDataType == null) {
					// In case of an new attribute show nothing
					// until a datatype was chosen
					return null;
				}

				if (theDataType.supportsFraction() && !theDataType.isDomain()) {
					return theAttribute.getFraction();
				}
				return null;
			case 4:
				// scale
				if (theDataType == null) {
					// In case of an new attribute show nothing
					// until a datatype was chosen
					return null;
				}

				if (theDataType.supportsScale() && !theDataType.isDomain()) {
					return theAttribute.getScale();
				}
				return null;
			case 5:
				// nullable
				return theAttribute.isNullable();
			case 6:
				// default
				return theAttribute.getDefaultValue();
			case 7:
				// extra
				if (theDataType == null) {
					// In case of an new attribute show nothing
					// until a datatype was chosen
					return null;
				}

				if (theDataType.supportsExtra()) {
					return theAttribute.getExtra();
				}
				return null;
			case 8:
				// comment
				return theAttribute.getComment();
		}
		throw new IllegalArgumentException("Wrong columnIndex : " + columnIndex);
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		final Attribute<Table> theAttribute = rowData.get(rowIndex);
		switch (columnIndex) {
			case 0:
				// name
				theAttribute.setName((String) aValue);
				break;
			case 1:
				// type
				final DataType theType = (DataType) aValue;
				theAttribute.setDatatype(theType);
				if (theType != null && theType.isDomain()) {
					// If the new datatype is a domain, set the nullable flag according to the domain
					theAttribute.setNullable(((Domain) theType).isNullable());
				}
				break;
			case 2:
				// size
				theAttribute.setSize((Integer) aValue);
				break;
			case 3:
				// fraction
				theAttribute.setFraction((Integer) aValue);
				break;
			case 4:
				// scale
				theAttribute.setScale((Integer) aValue);
				break;
			case 5:
				// nullable
				theAttribute.setNullable((Boolean) aValue);
				break;
			case 6:
				// default
				theAttribute.setDefaultValue((String) aValue);
				break;
			case 7:
				// extra
				theAttribute.setExtra((String) aValue);
				break;
			case 8:
				// comment
				theAttribute.setComment((String) aValue);
				break;
		}
	}
}