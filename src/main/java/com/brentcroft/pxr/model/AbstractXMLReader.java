package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;
import org.xml.sax.*;

@Getter
@Setter
public abstract class AbstractXMLReader implements XMLReader
{
    private ContentHandler contentHandler;
    private EntityResolver entityResolver;
    private ErrorHandler errorHandler;
    private DTDHandler dTDHandler;

    public boolean getFeature( String name )
    {
        return false;
    }

    public void setFeature( String name, boolean value )
    {
    }

    public Object getProperty( String name )
    {
        return null;
    }

    public void setProperty( String name, Object value )
    {
    }
}
