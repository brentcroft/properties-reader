package com.brentcroft.pxr;

import com.brentcroft.pxr.model.PxrProperties;
import com.brentcroft.pxr.model.PxrPropertiesRootTag;
import com.brentcroft.tools.materializer.ContextValue;
import com.brentcroft.tools.materializer.Materializer;
import com.brentcroft.tools.materializer.core.TagHandler;
import lombok.Setter;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Setter
public class PxrWriter
{
    private PxrProperties pxrProperties;

    private ContextValue contextValue;

    public void parse( InputSource inputSource )
    {
        final Materializer< PxrProperties > materializer = new Materializer<>(
                () -> PxrPropertiesRootTag.ROOT,
                () -> pxrProperties
        );

        materializer.setContextValue( contextValue );

        materializer.apply( inputSource );
    }

    public void parse( InputStream inputStream )
    {
        parse( new InputSource( inputStream ) );
    }

    public void parse( byte[] bytes )
    {
        parse( new ByteArrayInputStream( bytes ) );
    }

    public TagHandler< PxrProperties > getDefaultHandler()
    {
        final Materializer< PxrProperties > materializer = new Materializer<>(
                () -> PxrPropertiesRootTag.ROOT,
                () -> pxrProperties
        );

        materializer.setContextValue( contextValue );

        return materializer.getDefaultHandler();
    }
}
