package com.brentcroft.pxr.fixtures;

import com.brentcroft.pxr.CRFilterWriter;
import com.brentcroft.pxr.PxrUtils;
import com.brentcroft.pxr.PxrWriter;
import com.brentcroft.pxr.model.PxrProperties;
import com.brentcroft.pxr.parser.ParseException;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static com.brentcroft.pxr.PxrUtils.*;

public class WhenProperties extends Stage< WhenProperties >
{
    private String charset = "UTF-8";


    @ScenarioState
    private String propertiesText;

    @ProvidedScenarioState
    private String propertiesXml;

    @ProvidedScenarioState
    private Exception exception;

    @ProvidedScenarioState
    private String transformResult;

    @ProvidedScenarioState
    private PxrProperties pxrProperties;


    public WhenProperties transform_text_to_xml_text() throws TransformerException
    {
        StringWriter baos = new StringWriter();

        propertiesTextToXmlText(
                new ByteArrayInputStream( propertiesText.getBytes( StandardCharsets.ISO_8859_1 ) ),
                baos,
                "UTF-8"
        );

        propertiesXml = baos.toString();

        return self();
    }

    public WhenProperties transform_xml_to_properties_text() throws TransformerException, UnsupportedEncodingException
    {
        Writer baos = CRFilterWriter.from( new StringWriter() );

        xmlTextToPropertiesText(
                new ByteArrayInputStream( propertiesXml.getBytes( StandardCharsets.UTF_8 ) ),
                baos
        );

        transformResult = baos.toString();

        return self();
    }

    public WhenProperties parse_text_to_pxr_properties() throws ParseException, UnsupportedEncodingException
    {
        pxrProperties = getPxrProperties(
                new ByteArrayInputStream( propertiesText.getBytes( charset ) ) );

        return self();
    }

    public WhenProperties transform_pxr_to_properties_text() throws TransformerException
    {
        Writer baos = CRFilterWriter.from( new StringWriter() );

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

    public WhenProperties update_pxr_properties() throws IOException
    {
        PxrWriter pxrWriter = new PxrWriter();

        pxrWriter.setPxrProperties( pxrProperties );
        pxrWriter.parse( propertiesXml.getBytes( charset ) );

        return self();
    }

    public WhenProperties apply_update() throws ParseException, IOException, TransformerException
    {
        return self()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
    }
}
