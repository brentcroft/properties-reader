package com.brentcroft.pxr;


import com.brentcroft.pxr.model.AbstractXMLReader;
import com.brentcroft.pxr.model.PxrEntry;
import com.brentcroft.pxr.model.PxrItem;
import com.brentcroft.pxr.model.PxrProperties;
import com.brentcroft.pxr.parser.ParseException;
import com.brentcroft.pxr.parser.PxrParser;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import static com.brentcroft.pxr.PxrUtils.isNull;
import static com.brentcroft.pxr.PxrUtils.nonNull;
import static com.brentcroft.pxr.model.PxrItem.NAMESPACE_URI;

/**
 * Parses a Properties Text InputStream into a sequence of SAX Events.
 */
@Getter
@Setter
public class PxrReader extends AbstractXMLReader
{
    private boolean systemIdAttribute = false;
    private boolean entriesOnly = false;

    private PxrProperties pxrProperties;


    public void parse( InputSource inputSource ) throws SAXException
    {
        try
        {
            if ( inputSource instanceof PxrInputSource )
            {
                pxrProperties = ( ( PxrInputSource ) inputSource ).getPxrProperties();
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
    }


    public void parse( PxrProperties pxrProperties ) throws SAXException
    {
        if ( isNull( getContentHandler() ) )
        {
            throw new RuntimeException( "No ContentHandler" );
        }

        if ( entriesOnly )
        {
            emitEntries( pxrProperties, getContentHandler() );
        }
        else
        {
            emitProperties( pxrProperties, getContentHandler() );
        }
    }

    private static void emitEntries( PxrProperties pxrProperties, ContentHandler contentHandler ) throws SAXException
    {
        for ( PxrEntry entry : pxrProperties.values() )
        {
            entry.emitEntry( contentHandler );
        }
    }

    private static void emitProperties( PxrProperties pxrProperties, ContentHandler contentHandler ) throws SAXException
    {
        PxrItem.TAG tag = PxrItem.TAG.PROPERTIES;
        AttributesImpl atts = new AttributesImpl();

        if ( nonNull( pxrProperties.getSystemId() ) )
        {
            PxrItem.ATTR.SYSTEM_ID.setAttribute( atts, NAMESPACE_URI, pxrProperties.getSystemId() );
        }

        contentHandler.startDocument();

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        if ( nonNull( pxrProperties.getHeader() ) )
        {
            pxrProperties.getHeader().emitEntry( contentHandler );
        }

        emitEntries( pxrProperties, contentHandler );

        if ( nonNull( pxrProperties.getFooter() ) )
        {
            pxrProperties.getFooter().emitEntry( contentHandler );
        }

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );

        contentHandler.endDocument();
    }
}
