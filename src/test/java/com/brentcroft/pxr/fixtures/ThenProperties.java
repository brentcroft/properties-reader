package com.brentcroft.pxr.fixtures;

import com.brentcroft.pxr.model.PxrProperties;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.ScenarioState;

import java.io.FileInputStream;
import java.io.IOException;

import static com.brentcroft.pxr.fixtures.GivenProperties.readFile;
import static com.brentcroft.pxr.fixtures.Utils.canonicalisePropertiesText;
import static com.brentcroft.pxr.fixtures.Utils.canonicaliseXmlText;
import static org.junit.Assert.*;

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

    @ScenarioState
    PxrProperties pxrProperties;


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

    public ThenProperties xml_text_not_equals( String xml_text )
    {
        assertNotEquals(
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

    public ThenProperties different_properties_text()
    {
        assertNotEquals(
                canonicalisePropertiesText( propertiesText ),
                canonicalisePropertiesText( transformResult )
        );

        return self();
    }


    @As( "transform result: \n[$]\n" )
    public ThenProperties transform_result_is( String expected )
    {
        assertEquals(
                canonicalisePropertiesText( expected ),
                canonicalisePropertiesText( transformResult )
        );

        return self();
    }


    public ThenProperties pxr_properties_is_not_null()
    {
        assertNotNull( pxrProperties );
        return self();
    }

    public ThenProperties transformer_exception()
    {
        return self();
    }

    public ThenProperties transform_result_equals_file( String filename ) throws IOException
    {
        String expected = readFile(
                new FileInputStream( filename ),
                "UTF-8" );

        transform_result_is( expected );

        return self();
    }

    public ThenProperties xml_text_equals_file( String filename ) throws IOException
    {
        String expected = readFile(
                new FileInputStream( filename ),
                "UTF-8" );

        xml_text_equals( expected );

        return self();
    }
}
