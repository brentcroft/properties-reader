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
public class PxrComment implements PxrItem
{
    private int linesBefore;
    private String key;
    private boolean eol;
    private final StringBuilder b = new StringBuilder();

    public void emitEntry( ContentHandler contentHandler ) throws SAXException
    {
        TAG tag = TAG.COMMENT;
        AttributesImpl atts = new AttributesImpl();

        if ( nonNull( key ) )
        {
            ATTR.KEY.setAttribute( atts, NAMESPACE_URI, key );
        }

        if ( linesBefore > 0 )
        {
            ATTR.LINES_BEFORE.setAttribute( atts, NAMESPACE_URI, String.valueOf( linesBefore ) );
        }

        if ( ! eol )
        {
            ATTR.EOL.setAttribute( atts, NAMESPACE_URI, "0" );
        }

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        if ( b.length() > 0 )
        {
            char[] characters = b.toString().toCharArray();

            contentHandler.characters( characters, 0, characters.length );
        }

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
    }


    public void ingest( String space, String init, String text, boolean newEol )
    {
        if ( nonNull( space ) )
        {
            switchEolOnText();

            b.append( space );
        }

        if ( nonNull( init ) )
        {
            switchEolOnText();

            b.append( init );
        }

        if ( nonNull( text ) )
        {
            switchEolOnText();

            b.append( text );
        }

        if ( newEol )
        {
            if ( b.length() == 0 )
            {
                // absorb empty lines in linesBefore
                // until text is committed
                linesBefore++;
            }
            else if ( ! eol )
            {
                // absorb one more empty line as (temp) local eol
                eol = true;
            }
            else
            {
                // no where else to go
                switchEolOnText();
            }
        }
    }

    private void switchEolOnText()
    {
        if ( eol )
        {
            eol = false;
            b.append( "\n" );
        }
    }
}