package com.brentcroft.pxr.fixtures;

import com.brentcroft.pxr.fixtures.GivenProperties;
import com.brentcroft.pxr.fixtures.ThenProperties;
import com.brentcroft.pxr.fixtures.WhenProperties;
import com.tngtech.jgiven.junit.ScenarioTest;

import javax.xml.transform.TransformerException;

public class AbstractScenarios extends ScenarioTest< GivenProperties, WhenProperties, ThenProperties >
{
    protected void runRoundTrip( String propertiesText, String propertiesXml ) throws TransformerException
    {
        given()
                .properties_text( propertiesText );
        when()
                .transform_to_xml_text();
        then()
                .xml_text_equals( propertiesXml );
        when()
                .transform_to_properties_text();
        then()
                .same_properties_text();
    }
}
