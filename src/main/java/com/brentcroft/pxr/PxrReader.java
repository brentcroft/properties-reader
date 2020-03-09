package com.brentcroft.pxr;


import com.brentcroft.pxr.model.AbstractXMLReader;
import com.brentcroft.pxr.model.PxrItem;
import com.brentcroft.pxr.model.PxrProperties;
import com.brentcroft.pxr.parser.ParseException;
import com.brentcroft.pxr.parser.PxrParser;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

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
        final ContentHandler contentHandler = getContentHandler();

        if ( contentHandler == null )
        {
            throw new SAXException( "No ContentHandler." );
        }

        try
        {
            PxrProperties rp = new PxrParser( inputSource.getByteStream() )
                    .parse();

            rp.setSystemId( systemId );

            if ( entriesOnly )
            {
                rp.emitEntries( getContentHandler() );
            }
            else
            {
                rp.emitProperties( getContentHandler() );
            }
        }
        catch ( ParseException e )
        {
            throw new SAXException( e );
        }
    }


    public static void propertiesTextToXmlText( InputStream inputStream, Writer writer, boolean omitXmlDeclaration ) throws TransformerException
    {
        Transformer transformer = SAXTransformerFactory
                .newInstance()
                .newTransformer();

        transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
        transformer.setOutputProperty( OutputKeys.INDENT, "yes" );

        if ( omitXmlDeclaration )
        {
            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
        }

        transformer.setOutputProperty( OutputKeys.CDATA_SECTION_ELEMENTS, PxrItem.TAGS_FOR_CATA_TEXT );
        transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );

        final int indent = 4;

        transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", Integer.toString( indent ) );

        transformer.transform(
                new SAXSource(
                        new PxrReader(),
                        new InputSource( inputStream )
                ),
                new StreamResult( writer )
        );
    }
}
