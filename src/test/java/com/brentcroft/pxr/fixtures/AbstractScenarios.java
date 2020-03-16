package com.brentcroft.pxr.fixtures;

import com.tngtech.jgiven.junit.ScenarioTest;

import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;

public class AbstractScenarios extends ScenarioTest< GivenProperties, WhenProperties, ThenProperties >
{
    protected void runRoundTrip( String propertiesText, String propertiesXml ) throws TransformerException, UnsupportedEncodingException
    {
        // either seems to produce same result
        //runRoundTrip( propertiesText, propertiesXml, null );
        runRoundTrip( propertiesText, propertiesXml, "UTF-8" );
    }

    protected void runRoundTrip( String propertiesText, String propertiesXml, String encoding ) throws TransformerException, UnsupportedEncodingException
    {
        given()
                .properties_text( propertiesText );
        when()
                .transform_text_to_xml_text( encoding );
        then()
                .xml_text_equals( propertiesXml );
        when()
                .transform_xml_to_properties_text();
        then()
                .same_properties_text();
    }
}
