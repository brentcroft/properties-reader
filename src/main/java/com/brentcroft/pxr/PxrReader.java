package com.brentcroft.pxr;


import com.brentcroft.pxr.model.*;
import com.brentcroft.pxr.parser.ParseException;
import com.brentcroft.pxr.parser.PxrParser;
import com.brentcroft.pxr.parser.TokenMgrError;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import static com.brentcroft.pxr.PxrUtils.isNull;
import static com.brentcroft.pxr.PxrUtils.nonNull;

/**
 * Parses a Properties Text InputStream into a sequence of SAX Events.
 */
@Getter
@Setter
public class PxrReader extends AbstractXMLReader implements PxrItem
{
    private boolean systemIdAttribute = false;
    private boolean entriesOnly = false;

    private String encoding = "UTF-8";

    private PxrProperties pxrProperties;


    public void parse( InputSource inputSource ) throws SAXException
    {
        try
        {
            if ( inputSource instanceof PxrInputSource )
            {
                pxrProperties = ( ( PxrInputSource ) inputSource ).getPxrProperties();
            }
            else if ( nonNull( encoding ) )
            {
                pxrProperties = new PxrParser( inputSource.getByteStream(), encoding )
                        .parse();
            }
            else
            {
                pxrProperties = new PxrParser( inputSource.getByteStream() )
                        .parse();
            }

            pxrProperties.setSystemId( getSystemId() );

            if ( nonNull( getContentHandler() ) )
            {
                parse( pxrProperties );
            }
        }
        catch ( ParseException e )
        {
            throw new SAXException( e );
        }
        catch ( TokenMgrError e )
        {
            throw new RuntimeException( e );
        }
    }

    public void parse( PxrProperties pxrProperties ) throws SAXException
    {
        if ( isNull( getContentHandler() ) )
        {
            throw new RuntimeException( "No ContentHandler" );
        }

        if ( entriesOnly )
        {
            emitEntries( pxrProperties );
        }
        else
        {
            emitProperties( pxrProperties );
        }
    }

    private void emitProperties( PxrProperties pxrProperties ) throws SAXException
    {
        final ContentHandler contentHandler = getContentHandler();

        final TAG tag = PxrItem.TAG.PROPERTIES;
        final AttributesImpl atts = new AttributesImpl();

        if ( nonNull( pxrProperties.getSystemId() ) )
        {
            PxrItem.ATTR.SYSTEM_ID.setAttribute( atts, NAMESPACE_URI, pxrProperties.getSystemId() );
        }

        contentHandler.startDocument();
        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        emitEntries( pxrProperties );

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
        contentHandler.endDocument();
    }

    private void emitEntries( PxrProperties pxrProperties ) throws SAXException
    {
        if ( nonNull( pxrProperties.getHeader() ) )
        {
            emitComment( pxrProperties.getHeader() );
        }
        for ( PxrEntry entry : pxrProperties.getEntries() )
        {
            emitEntry( entry );
        }
        if ( nonNull( pxrProperties.getFooter() ) )
        {
            emitComment( pxrProperties.getFooter() );
        }
    }

    private void emitComment( PxrComment comment ) throws SAXException
    {
        final ContentHandler contentHandler = getContentHandler();

        final PxrItem.TAG tag = PxrItem.TAG.COMMENT;
        final AttributesImpl atts = new AttributesImpl();

        PxrItem.ATTR.KEY.setAttribute( atts, NAMESPACE_URI, comment.getKey() );

        if ( comment.getLinesBefore() > 0 )
        {
            PxrItem.ATTR.LINES_BEFORE.setAttribute( atts, NAMESPACE_URI, String.valueOf( comment.getLinesBefore() ) );
        }

        if ( ! comment.isEol() )
        {
            PxrItem.ATTR.EOL.setAttribute( atts, NAMESPACE_URI, "0" );
        }

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        if ( nonNull( comment.getValue() ) && comment.getValue().length() > 0 )
        {
            char[] characters = comment.getValue().toString().toCharArray();

            contentHandler.characters( characters, 0, characters.length );
        }

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
    }

    private void emitEntry( PxrEntry entry ) throws SAXException
    {
        if ( nonNull( entry.getComment() ) )
        {
            emitComment( entry.getComment() );
        }

        final ContentHandler contentHandler = getContentHandler();

        final TAG tag = PxrItem.TAG.ENTRY;
        final AttributesImpl atts = new AttributesImpl();

        ATTR.KEY.setAttribute( atts, NAMESPACE_URI, entry.getKey() );
        ATTR.INDEX.setAttribute( atts, NAMESPACE_URI, String.valueOf( entry.getIndex() ) );

        // ignoring most common case
        if ( ! "=".equals( entry.getSep() ) )
        {
            ATTR.SEP.setAttribute(
                    atts,
                    NAMESPACE_URI,
                    isNull( entry.getSep() ) ? "" : entry.getSep() );
        }

        // ignoring most common case
        if ( ! entry.isEol() )
        {
            ATTR.EOL.setAttribute( atts, NAMESPACE_URI, "0" );
        }

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        if ( isNull( entry.getContinuations() ) )
        {
            if ( nonNull( entry.getValue() ) )
            {
                char[] characters = entry.getValue().toCharArray();

                contentHandler.characters( characters, 0, characters.length );
            }
        }
        else
        {
            if ( nonNull( entry.getValue() ) )
            {
                emitValueAsZerothContinuation( entry.getValue() );
            }

            for ( PxrEntryContinuation rec : entry.getContinuations() )
            {
                emitContinuation( rec );
            }
        }

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
    }

    private void emitValueAsZerothContinuation( String value ) throws SAXException
    {
        final ContentHandler contentHandler = getContentHandler();
        final TAG tag = PxrItem.TAG.TEXT;
        final AttributesImpl atts = new AttributesImpl();

        ATTR.KEY.setAttribute( atts, NAMESPACE_URI, "0" );

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        char[] characters = value.toCharArray();

        contentHandler.characters( characters, 0, characters.length );

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
    }


    public void emitContinuation( PxrEntryContinuation continuation ) throws SAXException
    {
        final ContentHandler contentHandler = getContentHandler();
        TAG tag = TAG.TEXT;
        AttributesImpl atts = new AttributesImpl();

        ATTR.KEY.setAttribute( atts, NAMESPACE_URI, String.valueOf( continuation.getIndex() ) );

        if ( nonNull( continuation.getCont() ) )
        {
            ATTR.CONT.setAttribute( atts, NAMESPACE_URI, continuation.getCont() );
        }

        if ( nonNull( continuation.getPrefix() ) )
        {
            ATTR.PREFIX.setAttribute( atts, NAMESPACE_URI, continuation.getPrefix() );
        }

        if ( ! continuation.isEol() )
        {
            ATTR.EOL.setAttribute( atts, NAMESPACE_URI, "false" );
        }

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        if ( nonNull( continuation.getValue() ) )
        {
            char[] characters = continuation.getValue().toCharArray();

            contentHandler.characters( characters, 0, characters.length );
        }

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );
    }
}
