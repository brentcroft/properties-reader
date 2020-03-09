package com.brentcroft.pxr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
public class PxrEntryContinuation implements PxrItem
{
    private int index;
    private String cont;
    private String prefix;
    private String value;
    private boolean eol;

    public void emitEntry( ContentHandler contentHandler ) throws SAXException
    {
        TAG tag = TAG.TEXT;
        AttributesImpl atts = new AttributesImpl();

        ATTR.KEY.setAttribute( atts, NAMESPACE_URI, String.valueOf( index ) );

        if ( nonNull( cont ) )
        {
            ATTR.CONT.setAttribute( atts, NAMESPACE_URI, cont );
        }

        if ( nonNull( prefix ) )
        {
            ATTR.PREFIX.setAttribute( atts, NAMESPACE_URI, prefix );
        }

        if ( ! eol )
        {
            ATTR.EOL.setAttribute( atts, NAMESPACE_URI, String.valueOf( eol ) );
        }

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        if ( nonNull( value ) )
        {
            char[] characters = value.toCharArray();

            contentHandler.characters( characters, 0, characters.length );
        }

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
    }
}
