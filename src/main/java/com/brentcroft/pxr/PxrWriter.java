package com.brentcroft.pxr;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringWriter;

import static java.util.Objects.isNull;

public class PxrWriter
{
    private static Templates PXR_TEMPLATES;

    private static final String pxrTemplatesUri = "xslt/properties.xslt";

    private static void loadRixTemplates() throws TransformerConfigurationException
    {
        PXR_TEMPLATES = TransformerFactory
                .newInstance()
                .newTemplates(
                        new StreamSource(
                                PxrWriter
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
}
