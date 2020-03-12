package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Properties;

import static com.brentcroft.pxr.PxrUtils.isNull;


@Getter
@Setter
public class PxrProperties extends LinkedHashMap< String, PxrEntry > implements PxrItem
{
    private String systemId = null;
    private PxrComment header;
    private PxrComment footer;

    public PxrComment getComment( String key )
    {
        if ( HEADER_KEY.equals( key ) )
        {
            return getHeader();
        }
        if ( FOOTER_KEY.equals( key ) )
        {
            return getFooter();
        }

        PxrEntry entry = get( key );

        return isNull( entry ) ? null : entry.getComment();
    }


    public Properties getProperties()
    {
        Properties p = new Properties();

        for ( PxrEntry entry : values() )
        {
            p.put( entry.getKey(), entry.getValue() );
        }
        return p;
    }
}
