package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Getter
@Setter
public class PxrProperties implements PxrItem
{
    private String systemId = null;
    private PxrComment header;
    private final List< PxrEntry > entries = new ArrayList<>();
    private PxrComment footer;


    public void emitProperties( ContentHandler contentHandler ) throws SAXException
    {
        TAG tag = TAG.PROPERTIES;
        AttributesImpl atts = new AttributesImpl();

        if ( nonNull( systemId ) )
        {
            ATTR.SYSTEM_ID.setAttribute( atts, NAMESPACE_URI, systemId );
        }

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
    }


    public void emitEntries( ContentHandler contentHandler ) throws SAXException
    {
        for ( PxrEntry entry : entries )
        {
            entry.emitEntry( contentHandler );
        }
    }
}
