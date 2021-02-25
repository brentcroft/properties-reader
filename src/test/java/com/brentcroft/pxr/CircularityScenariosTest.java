package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import com.brentcroft.pxr.model.PxrProperties;
import com.brentcroft.pxr.model.PxrPropertiesRootTag;
import com.brentcroft.tools.materializer.Materializer;
import com.brentcroft.tools.materializer.TagHandlerException;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.FileInputStream;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

public class CircularityScenariosTest extends AbstractScenarios
{
    String rootDir = "src/test/resources";

    @Test
    public void circularity() throws Exception
    {
        String uri = "circularity.xml";

        String systemId = format( "%s/%s", rootDir, uri );

        PxrProperties pxrProperties = new PxrProperties();
        pxrProperties.setSystemId( systemId );

        try
        {
            new Materializer<>(
                    () -> PxrPropertiesRootTag.ROOT,
                    () -> pxrProperties )
                    .apply(
                            new InputSource(
                                    new FileInputStream( systemId ) ) );
        }
        catch ( TagHandlerException e )
        {
            assertTrue( e.getMessage().contains( "circularity" ) );
        }
    }


    @Test
    public void circularity2() throws Exception
    {
        String uri = "circularity2.xml";

        String systemId = format( "%s/%s", rootDir, uri );

        PxrProperties pxrProperties = new PxrProperties();
        pxrProperties.setSystemId( systemId );

        try
        {
            new Materializer<>(
                    () -> PxrPropertiesRootTag.ROOT,
                    () -> pxrProperties )
                    .apply(
                            new InputSource(
                                    new FileInputStream( systemId ) ) );
        }
        catch ( TagHandlerException e )
        {
            assertTrue( e.getMessage().contains( "circularity" ) );
        }
    }
}
