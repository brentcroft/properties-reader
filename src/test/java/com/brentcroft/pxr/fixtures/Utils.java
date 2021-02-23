package com.brentcroft.pxr.fixtures;

public class Utils
{
    public static final String PROLOG = "\n";
    public static final String EPILOG = "\n";

    /**
     * Remove all CRs.
     * Replace whitespace between elements.
     * Replace single quotes with double quotes.
     *
     * @param xmlText the XML text to be canonicalised
     * @return the canonicalised XML text
     */
    public static String canonicaliseXmlText( String xmlText )
    {
        return xmlText
                .trim()
                .replaceAll( "\r", "" )
                .replaceAll( ">\\s*<", "><" )
                .replaceAll( "'", "\"" );
    }

    public static String canonicalisePropertiesText( String propertiesText )
    {
//        return propertiesText;
        return propertiesText
                .replaceAll( "\r", "" );
    }

}
