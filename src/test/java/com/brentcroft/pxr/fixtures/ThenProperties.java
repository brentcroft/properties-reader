package com.brentcroft.pxr.fixtures;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ThenProperties extends Stage< ThenProperties >
{
    @ScenarioState
    String propertiesText;

    @ScenarioState
    String propertiesXml;

    @ScenarioState
    Exception exception;

    @ScenarioState
    String transformResult;


    public String canonicaliseXmlText( String xmlText )
    {
        return xmlText
                .trim()
                .replaceAll( "\r", "" )
                .replaceAll( ">\\s*<", "><" )
                .replaceAll( "'", "\"" );
    }

    public String canonicalisePropertiesText( String propertiesText )
    {
        return propertiesText
                .replaceAll( "\r", "" );
    }

    public ThenProperties xml_text_is_not_null()
    {
        assertNotNull( propertiesXml );

        return this;
    }

    public ThenProperties xml_text_equals( String xml_text )
    {
        assertEquals(
                canonicaliseXmlText( xml_text ),
                canonicaliseXmlText( propertiesXml )
        );

        return self();
    }

    public ThenProperties same_properties_text()
    {
        assertEquals(
                canonicalisePropertiesText( propertiesText ),
                canonicalisePropertiesText( transformResult )
        );

        return self();
    }
}
