package com.brentcroft.pxr.model;

import com.brentcroft.tools.materializer.Materializer;
import org.junit.Test;
import org.xml.sax.InputSource;

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

        PxrProperties pxrProperties = new PxrProperties();

        Materializer< PxrProperties > materializer = new Materializer<>(
//                schema,
//                0,
                () -> PxrPropertiesRootTag.ROOT,
                () -> pxrProperties
        );

        for ( String uri : uris )
        {
            String systemId = format( "%s/%s", rootDir, uri );

            pxrProperties.setSystemId( systemId );

            materializer
                    .apply(
                            new InputSource(
                                    new FileInputStream( format( "%s/%s", rootDir, uri ) ) ) );

            System.out.println( pxrProperties.jsonate() );
        }
    }
}