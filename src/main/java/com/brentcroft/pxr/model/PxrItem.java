package com.brentcroft.pxr.model;

import org.xml.sax.helpers.AttributesImpl;

import static java.util.Arrays.asList;

public interface PxrItem
{
    String NAMESPACE_URI = "";

    String TAGS_FOR_CATA_TEXT = String
            .join(
                    " ",
                    asList(
                            TAG.COMMENT.getTag()
                    ) );

    enum TAG
    {
        PROPERTIES( "properties" ),
        ENTRY( "entry" ),
        COMMENT( "comment" ),
        TEXT( "text" );

        private final String tag;

        TAG( String tag )
        {
            this.tag = tag;
        }

        public String getTag()
        {
            return tag;
        }
    }


    enum ATTR
    {
        EOL( "eol" ),
        LINES_BEFORE( "lines-before" ),
        KEY( "key" ),
        SYSTEM_ID( "system-id" ),
        PREFIX( "prefix" ),
        CONT( "cont" ),
        SEP( "sep" ),
        INDEX( "index" );

        private final String attr;


        ATTR( String attr )
        {
            this.attr = attr;
        }

        public void setAttribute( AttributesImpl atts, String namespaceUri, String value )
        {
            atts.addAttribute( namespaceUri, attr, attr, "", value == null ? "" : value );
        }
    }
}
