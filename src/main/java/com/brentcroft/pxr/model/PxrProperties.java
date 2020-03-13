package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.brentcroft.pxr.PxrUtils.isNull;
import static com.brentcroft.pxr.PxrUtils.nonNull;


@Getter
@Setter
public class PxrProperties implements PxrItem
{
    private String systemId = null;
    private PxrComment header;
    private PxrComment footer;

    private final List< PxrEntry > entries = new ArrayList< PxrEntry >();
    private final Map< String, PxrEntry > entryMap = new HashMap< String, PxrEntry >();

    public boolean isEmpty()
    {
        return entries.isEmpty();
    }

    public void append( PxrEntry entry )
    {
        // maybe force line break ahead of new entry
        if ( entries.size() > 0 )
        {
            PxrEntry lastEntry = entries.get( entries.size() - 1 );
            if ( ! lastEntry.isEol() )
            {
                lastEntry.setEol( true );
            }
        }

        entries.add( entry );
        entryMap.put( entry.getKey(), entry );
    }

    public void remove( String key )
    {
        PxrEntry entry = entryMap.get( key );

        if ( nonNull( entry ) )
        {
            entryMap.remove( key );
            entries.remove( entry );
        }
    }


    public PxrComment getComment( String key )
    {
        if ( HEADER_KEY.equals( key ) )
        {
            return getHeader();
        }
        else if ( FOOTER_KEY.equals( key ) )
        {
            return getFooter();
        }

        PxrEntry entry = entryMap.get( key );

        return isNull( entry )
               ? null
               : entry.getComment();
    }


    public Properties getProperties()
    {
        Properties p = new Properties();

        for ( PxrEntry entry : entries )
        {
            p.put( entry.getKey(), entry.getValue() );
        }

        return p;
    }
}
