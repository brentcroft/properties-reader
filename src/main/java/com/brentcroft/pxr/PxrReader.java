package com.brentcroft.pxr;


import com.brentcroft.pxr.model.AbstractXMLReader;
import com.brentcroft.pxr.model.PxrProperties;
import com.brentcroft.pxr.parser.ParseException;
import com.brentcroft.pxr.parser.PxrParser;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.brentcroft.pxr.PxrUtils.isNull;
import static com.brentcroft.pxr.PxrUtils.nonNull;

/**
 * Parses a Properties Text InputStream into a sequence of events.
 */
@Getter
@Setter
public class PxrReader extends AbstractXMLReader
{
    private String systemId;
    private boolean systemIdAttribute = false;
    private boolean entriesOnly = false;

    private PxrProperties pxrProperties;


    public void parse( String uri ) throws SAXException
    {
        systemId = uri;

        try
        {
            parse( new InputSource( new URL( uri ).openStream() ) );

            return;
        }
        catch ( MalformedURLException e )
        {
            // ok - try file
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }

        try
        {
            parse( new InputSource( new FileInputStream( uri ) ) );
        }
        catch ( FileNotFoundException e )
        {
            throw new SAXException( e );
        }
    }

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

            pxrProperties.setSystemId( systemId );

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
            pxrProperties.emitEntries( getContentHandler() );
        }
        else
        {
            pxrProperties.emitProperties( getContentHandler() );
        }
    }
}
