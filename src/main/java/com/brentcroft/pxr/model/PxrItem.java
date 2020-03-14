package com.brentcroft.pxr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import static java.lang.String.format;

public interface PxrItem
{
    String NAMESPACE_URI = "";
    String EXPECTED_NAMESPACE_PREFIX = "act";
    String EXPECTED_NAMESPACE_URI = "expected";

    String TAGS_FOR_CATA_TEXT = TAG.COMMENT.getTag();

    String HEADER_KEY = "_header";
    String FOOTER_KEY = "_footer";


    enum ACT
    {
        INSERT,
        UPDATE,
        DELETE
    }


    @Getter
    @AllArgsConstructor
    class ACTException extends RuntimeException
    {
        private final ACT action;
        private final String key;
        private final String expected;
        private final String actual;

        private static final String MSG_ACTION_FAILED = "DENIED [%s] : expected != actual : key=[%s], expected=[%s], actual=[%s]";

        public String getMessage()
        {
            return format( MSG_ACTION_FAILED, action, key, expected, actual );
        }
    }



    enum TAG
    {
        PROPERTIES( "properties" ),
        ENTRY( "entry" ),
        COMMENT( "comment" ),
        TEXT( "text" ),

        DELETE_ENTRY( format( "%s:entry", EXPECTED_NAMESPACE_PREFIX ) ),
        DELETE_COMMENT( format( "%s:comment", EXPECTED_NAMESPACE_PREFIX ) ),
        ;

        private final String tag;

        TAG( String tag )
        {
            this.tag = tag;
        }

        public String getTag()
        {
            return tag;
        }

        public boolean isTag( String tagName )
        {
            return tag.equals( tagName );
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
        INDEX( "index" ),
        SRC( "src" ),
        TRUNCATED("truncated"),
        EXPECTED( format( "%s:_text", EXPECTED_NAMESPACE_PREFIX ) ),
        ;

        private final String attr;


        ATTR( String attr )
        {
            this.attr = attr;
        }

        public boolean isAttribute( String name )
        {
            return attr.equals( name );
        }

        public boolean hasAttribute( Attributes atts )
        {
            return atts.getIndex( attr ) > - 1;
        }

        public String getAttribute( Attributes atts )
        {
            return atts.getValue( attr );
        }

        public String getAttribute()
        {
            return attr;
        }

        public void setAttribute( AttributesImpl atts, String namespaceUri, String value )
        {
            atts.addAttribute( namespaceUri, attr, attr, "", value == null ? "" : value );
        }

        public boolean hasAttribute( Element element )
        {
            return element.hasAttribute( attr );
        }

        public String getAttribute( Element element )
        {
            return hasAttribute( element ) ? element.getAttribute( attr ) : null;
        }

        public void setAttribute( Element element, String value )
        {
            element.setAttribute( getAttribute(), value );
        }
    }
}
