package de.erdesignerng.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.prefs.BackingStoreException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.dialect.DefaultValueNamingEnum;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DomainNamingEnum;
import de.erdesignerng.dialect.ReverseEngineeringNotifier;
import de.erdesignerng.dialect.ReverseEngineeringOptions;
import de.erdesignerng.dialect.ReverseEngineeringStrategy;
import de.erdesignerng.dialect.SchemaEntry;
import de.erdesignerng.dialect.TableNamingEnum;
import de.erdesignerng.exception.ReverseEngineeringException;
import de.erdesignerng.io.ModelIOUtilities;
import de.erdesignerng.model.Model;
import de.erdesignerng.util.ApplicationPreferences;
import de.mogwai.common.i18n.ResourceHelper;

public class ReverseEnginneringTester extends TestCase {

    protected void runRETest(String aFileName, String aDestname) throws BackingStoreException,
            SAXException, IOException, ParserConfigurationException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException, ReverseEngineeringException, TransformerException {

        ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();

        Model theModel = ModelIOUtilities.getInstance().deserializeModelFromXML(new FileInputStream(aFileName));

        Dialect theDialect = theModel.getDialect();
        ReverseEngineeringStrategy theStrategy = theDialect.getReverseEngineeringStrategy();

        System.out.println("Running test with dialect " + theDialect);

        Connection theConnection = theModel.createConnection(thePreferences);

        ReverseEngineeringOptions theOptions = new ReverseEngineeringOptions();
        theOptions.setDefaultValueNaming(DefaultValueNamingEnum.STANDARD);
        theOptions.setDomainNaming(DomainNamingEnum.STANDARD);
        theOptions.setTableNaming(TableNamingEnum.STANDARD);

        if (theDialect.supportsSchemaInformation()) {
            List<SchemaEntry> theEntryList = theStrategy.getSchemaEntries(theConnection);
            System.out.println("Following schema information found " + theEntryList);

            SchemaEntry theSelected = theEntryList.get(0);
            System.out.println("Selected entry is " + theSelected);

            theOptions.getSchemaEntries().add(theSelected);
        }

        theModel = theStrategy.createModelFromConnection(theConnection, theOptions, new ReverseEngineeringNotifier() {

            public void notifyMessage(String aResourceKey, String... aValues) {
                ResourceHelper theHelper = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);
                System.out.println(theHelper.getFormattedText(aResourceKey, aValues));
            }

        });

        ModelIOUtilities theUtilities = ModelIOUtilities.getInstance();
        theUtilities.serializeModelToXML(theModel, new FileOutputStream(aDestname));
        theModel = theUtilities.deserializeModelFromXML(new FileInputStream(aDestname));
        theUtilities.serializeModelToXML(theModel, new FileOutputStream(aDestname));
    }

    public void xtestReverseEngineeringMSSQL() throws BackingStoreException, SAXException,
            IOException, ParserConfigurationException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException, ReverseEngineeringException, TransformerException {
        runRETest("u:\\Eigene Dateien\\empty_model_steze_mssql.mxm", "c:\\temp\\steze.xml");
    }

    public void xtestReverseEngineeringMySQL() throws BackingStoreException, SAXException,
            IOException, ParserConfigurationException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException, ReverseEngineeringException, TransformerException {
        runRETest("u:\\Eigene Dateien\\empty_model_sp_mysql.mxm", "c:\\temp\\mitversicherung.xml");
    }

    public void xtestReverseEngineeringOracle() throws BackingStoreException, SAXException,
            IOException, ParserConfigurationException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException, ReverseEngineeringException, TransformerException {
        runRETest("u:\\Eigene Dateien\\empty_model_amis_oracle.mxm", "c:\\temp\\amis.xml");
    }

    public void xtestReverseEngineeringDB2() throws BackingStoreException, SAXException,
            IOException, ParserConfigurationException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException, ReverseEngineeringException, TransformerException {
        runRETest("u:\\Eigene Dateien\\empty_model_ccis_db2.mxm", "c:\\temp\\ccis.xml");
    }

}
