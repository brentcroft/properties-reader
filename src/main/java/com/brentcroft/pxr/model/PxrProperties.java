package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.LinkedHashMap;

import static com.brentcroft.pxr.PxrUtils.isNull;
import static com.brentcroft.pxr.PxrUtils.nonNull;


@Getter
@Setter
public class PxrProperties extends LinkedHashMap< String, PxrEntry > implements PxrItem
{
    private String systemId = null;
    private PxrComment header;
    private PxrComment footer;

    public PxrComment getComment( String key )
    {
        if ( HEADER_KEY.equals( key ) )
        {
            return getHeader();
        }
        if ( FOOTER_KEY.equals( key ) )
        {
            return getFooter();
        }

        PxrEntry entry = get( key );

        return isNull( entry ) ? null : entry.getComment();
    }

    public void emitProperties( ContentHandler contentHandler ) throws SAXException
    {
        TAG tag = TAG.PROPERTIES;
        AttributesImpl atts = new AttributesImpl();

        if ( nonNull( systemId ) )
        {
            ATTR.SYSTEM_ID.setAttribute( atts, NAMESPACE_URI, systemId );
        }

        contentHandler.startDocument();

        contentHandler.startElement( NAMESPACE_URI, tag.getTag(), tag.getTag(), atts );

        if ( nonNull( header ) )
        {
            header.emitEntry( contentHandler );
        }

        emitEntries( contentHandler );

        if ( nonNull( footer ) )
        {
            footer.emitEntry( contentHandler );
        }

        contentHandler.endElement( NAMESPACE_URI, tag.getTag(), tag.getTag() );

        contentHandler.endDocument();

    }


    public void emitEntries( ContentHandler contentHandler ) throws SAXException
    {
        for ( PxrEntry entry : values() )
        {
            entry.emitEntry( contentHandler );
        }
    }
}
