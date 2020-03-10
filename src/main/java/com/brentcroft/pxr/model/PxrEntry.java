package com.brentcroft.pxr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.List;

import static com.brentcroft.pxr.PxrUtils.isNull;
import static com.brentcroft.pxr.PxrUtils.nonNull;

@Getter
@Setter
@AllArgsConstructor
public class PxrEntry implements PxrItem
{
    private PxrComment comment;
    private int index;
    private String key;
    private String sep;
    private String value;
    private boolean eol;
    private List< PxrEntryContinuation > continuations;

    public void emitEntry( ContentHandler contentHandler ) throws SAXException
    {
        if ( nonNull( comment ) )
        {
            comment.emitEntry( contentHandler );
        }

        final TAG tag = TAG.ENTRY;
        final AttributesImpl atts = new AttributesImpl();

        if ( nonNull( key ) )
        {
            ATTR.KEY.setAttribute( atts, NAMESPACE_URI, key );
        }

        ATTR.INDEX.setAttribute( atts, NAMESPACE_URI, String.valueOf( index ) );

        if ( isNull( sep ))
        {
            sep = "";
        }

        // ignoring most common case
        if ( ! "=".equals( sep ) )
        {
            ATTR.SEP.setAttribute( atts, NAMESPACE_URI, sep );
        }

        // ignoring most common case
        if ( ! eol )
        {
            ATTR.EOL.setAttribute( atts, NAMESPACE_URI, "0" );
        }

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        if ( isNull( continuations ) )
        {
            if ( nonNull( value ) )
            {
                char[] characters = value.toCharArray();

                contentHandler.characters( characters, 0, characters.length );
            }
        }
        else
        {
            if ( nonNull( value ) )
            {
                emitValueAsZerothContinuation( contentHandler );
            }

            for ( PxrEntryContinuation rec : continuations )
            {
                rec.emitEntry( contentHandler );
            }
        }

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
    }

    private void emitValueAsZerothContinuation( ContentHandler contentHandler ) throws SAXException
    {
        final TAG tag = TAG.TEXT;
        final AttributesImpl atts = new AttributesImpl();

        ATTR.KEY.setAttribute( atts, NAMESPACE_URI, "0" );

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        char[] characters = value.toCharArray();

        contentHandler.characters( characters, 0, characters.length );

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
    }
}
