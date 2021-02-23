package com.brentcroft.pxr.model;

import com.brentcroft.tools.materializer.Materializer;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.validation.Schema;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static java.lang.String.format;

public class PxrPropertiesRootTagTest
{
    String xsdUri = "src/test/resources/properties.xsd";

    String rootDir = "src/test/resources";

    String[] uris = {
            "test.xml"
    };


    @Test
    public void materialize() throws FileNotFoundException
    {
        //Schema schema = Materializer.getSchemas( xsdUri );

        Materializer< PxrProperties > materializer = new Materializer<>(
//                schema,
//                0,
                () -> PxrPropertiesRootTag.ROOT,
                PxrProperties::new
        );

        for ( String uri : uris )
        {
            PxrProperties pxrProperties = materializer
                    .apply(
                            new InputSource(
                                    new FileInputStream( format( "%s/%s", rootDir, uri ) ) ) );

            System.out.println( pxrProperties.jsonate() );
        }
    }
}