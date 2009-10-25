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
package de.erdesignerng.visual.editor;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import sun.util.resources.LocaleData;
/*
 * @see http://java.sun.com/products/jdk/faq/faq-sun-packages.html
 *
 * Why Developers Should Not Write Programs That Call 'sun' Packages
 *
 * <snip/>
 *
 * The sun.* packages are not part of the supported, public interface.
 * A Java program that directly calls into sun.* packages is not guaranteed to
 * work on all Java-compatible platforms. In fact, such a program is not
 * guaranteed to work even in future versions on the same platform.
 *
 * For these reasons, there is no documentation available for the sun.* classes.
 * Platform-independence is one of the great advantages of developing in the
 * Java programming language. Furthermore, Sun and our licensees of Java
 * technology are committed to maintaining backward compatibility of the APIs
 * for future versions of the Java platform. (Except for code that relies on
 * serious bugs that we later fix.) This means that once your program is
 * written, the class files will work in future releases.
 *
 * <snip/>
 *
 */

//TODO [mirkosertic] handle setting the size to null here
public class NullsafeSpinnerEditor extends DefaultEditor {

    private static String getDefaultPattern(Locale locale) {
        // Get the pattern for the default locale.
        ResourceBundle rb = LocaleData.getNumberFormatData(locale);
        String[] all = rb.getStringArray("NumberPatterns");
        return all[0];
    }

    public NullsafeSpinnerEditor(JSpinner spinner, String decimalFormatPattern) {
        this(spinner, new DecimalFormat(decimalFormatPattern));
    }

    public NullsafeSpinnerEditor(JSpinner spinner) {
        this(spinner, getDefaultPattern(spinner.getLocale()));
    }

    private NullsafeSpinnerEditor(JSpinner spinner, DecimalFormat format) {
        super(spinner);

        NumberFormatter formatter = new NumberFormatter(format);
        DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);

        JFormattedTextField ftf = getTextField();
        ftf.setEditable(true);
        ftf.setFormatterFactory(factory);
        ftf.setHorizontalAlignment(JTextField.RIGHT);
        ftf.setColumns(("" + Integer.MAX_VALUE).length());
    }
}