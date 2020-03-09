package com.brentcroft.pxr.fixtures;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import static com.brentcroft.pxr.PxrUtils.propertiesTextToXmlText;
import static com.brentcroft.pxr.PxrUtils.xmlTextToPropertiesText;

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


    public WhenProperties transform_to_xml_text() throws TransformerException
    {
        StringWriter baos = new StringWriter();

        propertiesTextToXmlText(
                new ByteArrayInputStream( propertiesText.getBytes() ),
                baos,
                true
        );

        propertiesXml = baos.toString();

        return self();
    }

    public void transform_to_properties_text() throws TransformerException
    {
        StringWriter baos = new StringWriter();

        xmlTextToPropertiesText(
                new ByteArrayInputStream( propertiesXml.getBytes() ),
                baos
        );

        transformResult = baos.toString();
    }
}
