package de.erdesignerng.dialect.msaccess;

import java.sql.Connection;

public final class MSAccessFormats {

    public static final int VERSION_UNKNOWN = 000; // unbekannt

    public static final int VERSION_2 = 200;       // 2

    public static final int VERSION_95 = 300;      // 95

    public static final int VERSION_97 = 400;      // 97

    public static final int VERSION_200X = 500;    // 2000 or 2002 or 2003

    public static final int VERSION_2000 = 510;    // 2000

    public static final int VERSION_2002 = 522;    // 2002

    public static final int VERSION_2003 = 523;    // 2003

    public static final int VERSION_2007 = 600;    // 2007

    private static final MSAccessFileFormat FORMAT_2 = new MSAccessFileFormat(MSAccessFileFormat.JET, 0, 1);

    private static final MSAccessFileFormat FORMAT_97 = new MSAccessFileFormat(MSAccessFileFormat.JET, 20, 0);

    private static final MSAccessFileFormat FORMAT_2000 = new MSAccessFileFormat(MSAccessFileFormat.JET, 20, 1, "MSysAccessObjects");

    private static final MSAccessFileFormat FORMAT_200X = new MSAccessFileFormat(MSAccessFileFormat.JET, 20, 1);

    private static final MSAccessFileFormat FORMAT_2007 = new MSAccessFileFormat(MSAccessFileFormat.ACE, 20, 2, "MSysComplexColumns");

    /**
     * Reads the version of the engine from the binary.
     *
     * Attention: Access 2000, 2002 and 2003 can *not* be devided!
     * 
     * @param aFileName - the file to examine
     */
    public static final int getVersion(String aFileName) {

        int theVersion = VERSION_UNKNOWN;

        if (aFileName != null) {
            if (FORMAT_2.matches(aFileName, true)) {
                theVersion = VERSION_2;
            } else if (FORMAT_97.matches(aFileName, true)) {
                theVersion = VERSION_97;
            } else if (FORMAT_2000.matches(aFileName, true)) {
                theVersion = VERSION_2000;
            } else if (FORMAT_200X.matches(aFileName, true)) {
                theVersion = VERSION_200X;
            } else if (FORMAT_2007.matches(aFileName, true) ) {
                theVersion = VERSION_2007;
            }
        }

        return theVersion;

    }

    /**
     * Reads the version of the engine from the binary and searches for
     * special system tables throuph the connection to classifie the
     * version more detailled.
     *
     * Attention: Access 2002 and 2003 can *not* be devided!
     *
     * @param aFileName - the file to examine
     */
    public static final int getVersion(Connection aConnection) {

        int theVersion = VERSION_UNKNOWN;

        if (aConnection != null) {
            if (FORMAT_2.matches(aConnection)) {
                theVersion = VERSION_2;
            } else if (FORMAT_97.matches(aConnection)) {
                theVersion = VERSION_97;
            } else if (FORMAT_2000.matches(aConnection)) {
                theVersion = VERSION_2000;
            } else if (FORMAT_200X.matches(aConnection)) {
                theVersion = VERSION_200X;
            } else if (FORMAT_2007.matches(aConnection) ) {
                theVersion = VERSION_2007;
            }
        }

        return theVersion;

    }

}