package com.brentcroft.pxr.fixtures;

import com.brentcroft.pxr.PxrUtils;
import com.brentcroft.pxr.PxrWriter;
import com.brentcroft.pxr.model.PxrProperties;
import com.brentcroft.pxr.parser.ParseException;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import static com.brentcroft.pxr.PxrUtils.*;

public class WhenProperties extends Stage< WhenProperties >
{
    @ScenarioState
    String propertiesText;

    @ProvidedScenarioState
    String propertiesXml;

    @ProvidedScenarioState
    Exception exception;

    @ProvidedScenarioState
    String transformResult;

    @ProvidedScenarioState
    PxrProperties pxrProperties;


    public WhenProperties transform_text_to_xml_text( String encoding ) throws TransformerException
    {
        StringWriter baos = new StringWriter();

        propertiesTextToXmlText(
                new ByteArrayInputStream( propertiesText.getBytes() ),
                baos,
                encoding
        );

        propertiesXml = baos.toString();

        return self();
    }

    public WhenProperties transform_xml_to_properties_text() throws TransformerException
    {
        StringWriter baos = new StringWriter();

        xmlTextToPropertiesText(
                new ByteArrayInputStream( propertiesXml.getBytes() ),
                baos
        );

        transformResult = baos.toString();

        return self();
    }

    public WhenProperties parse_text_to_pxr_properties() throws ParseException
    {
        pxrProperties = getPxrProperties( new ByteArrayInputStream( propertiesText.getBytes() ) );

        return self();
    }

    public WhenProperties transform_pxr_to_properties_text() throws TransformerException
    {
        StringWriter baos = new StringWriter();

        PxrUtils.pxrPropertiesToText( pxrProperties, baos );

        transformResult = baos.toString();

        return self();
    }

    public WhenProperties transform_pxr_to_properties_xml() throws TransformerException
    {
        StringWriter baos = new StringWriter();

        PxrUtils.pxrPropertiesToXml( pxrProperties, baos );

        propertiesXml = baos.toString();

        return self();
    }

    public WhenProperties update_pxr_properties() throws ParserConfigurationException, SAXException, IOException
    {
        PxrWriter pxrWriter = new PxrWriter();
        pxrWriter.setPxrProperties( pxrProperties );

        SAXParserFactory
                .newInstance()
                .newSAXParser()
                .parse(
                        new ByteArrayInputStream( propertiesXml.getBytes() ),
                        pxrWriter
                );

        return self();
    }

    public WhenProperties apply_update() throws ParseException, IOException, SAXException, ParserConfigurationException, TransformerException
    {
        return self()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
    }
}
