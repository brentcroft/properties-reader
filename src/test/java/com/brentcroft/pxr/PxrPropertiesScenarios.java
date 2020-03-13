package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import com.brentcroft.pxr.parser.ParseException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;

import static com.brentcroft.pxr.fixtures.GivenProperties.readFile;

public class PxrPropertiesScenarios extends AbstractScenarios
{

    @Test
    public void pxr_properties() throws TransformerException, ParseException
    {
        given()
                .properties_text( "color = red\n" );

        when()
                .transform_text_to_pxr_properties();

        then()
                .pxr_properties_is_not_null();

        when()
                .transform_pxr_to_properties_text();

        then()
                .same_properties_text();
    }
}
