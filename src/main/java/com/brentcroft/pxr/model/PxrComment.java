package com.brentcroft.pxr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import static com.brentcroft.pxr.PxrUtils.nonNull;


@Getter
@Setter
@AllArgsConstructor
public class PxrComment implements PxrItem
{
    private int linesBefore;
    private String key;
    private boolean eol;
    private boolean charactersWritten;

    private final StringBuilder b = new StringBuilder();

    public void emitEntry( ContentHandler contentHandler ) throws SAXException
    {
        final TAG tag = TAG.COMMENT;
        final AttributesImpl atts = new AttributesImpl();

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

            charactersWritten = true;
        }

        if ( nonNull( init ) )
        {
            switchEolOnText();

            b.append( init );

            charactersWritten = true;
        }

        if ( nonNull( text ) )
        {
            switchEolOnText();

            b.append( text );

            charactersWritten = true;
        }

        if ( newEol )
        {
            if ( b.length() == 0 )
            {
                if ( eol )
                {
                    // absorb empty lines in linesBefore
                    // until text is committed
                    linesBefore++;
                }
            }
            else
            {
                switchEolOnText();
            }

            eol = true;
        }
    }

    private void switchEolOnText()
    {
        if ( eol )
        {
            if ( b.length() == 0 )
            {
                linesBefore++;
            }
            else
            {
                b.append( "\n" );
            }
        }
        eol = false;
    }
}