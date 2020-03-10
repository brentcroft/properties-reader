package com.brentcroft.pxr;

import com.brentcroft.pxr.model.PxrItem;
import com.brentcroft.pxr.model.PxrProperties;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

public class PxrUtils
{
    private static final String pxrTemplatesUri = "xslt/properties.xslt";

    private static Templates PXR_TEMPLATES;


    public static boolean isNull( Object o )
    {
        return o == null;
    }

    public static boolean nonNull( Object o )
    {
        return ! isNull( o );
    }


    private static void loadRixTemplates() throws TransformerConfigurationException
    {
        PXR_TEMPLATES = TransformerFactory
                .newInstance()
                .newTemplates(
                        new StreamSource(
                                PxrUtils
                                        .class
                                        .getClassLoader()
                                        .getResourceAsStream( pxrTemplatesUri ) ) );
    }


    public static void xmlTextToPropertiesText( InputStream inputStream, StringWriter stringWriter ) throws TransformerException
    {
        if ( isNull( PXR_TEMPLATES ) )
        {
            loadRixTemplates();
        }

        PXR_TEMPLATES
                .newTransformer()
                .transform(
                        new StreamSource( inputStream ),
                        new StreamResult( stringWriter )
                );
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

    public static PxrProperties getPxrProperties( InputStream inputStream ) throws SAXException
    {
        PxrReader reader = new PxrReader();

        reader.parse( new InputSource( inputStream ) );

        return reader.getPxrProperties();
    }

    public static void pxrPropertiesToText( PxrProperties pxrProperties, Writer writer ) throws TransformerException
    {
        if ( isNull( PXR_TEMPLATES ) )
        {
            loadRixTemplates();
        }

        SAXSource saxSource = new SAXSource(
                new PxrReader(),
                new PxrInputSource( pxrProperties )
        );

        PXR_TEMPLATES
                .newTransformer()
                .transform(
                        saxSource,
                        new StreamResult( writer )
                );
    }
}