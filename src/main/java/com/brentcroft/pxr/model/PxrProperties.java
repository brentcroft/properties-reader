package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

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

    public Properties getProperties()
    {
        Properties p = new Properties();

        for ( PxrEntry entry : entries )
        {
            p.put( entry.getKey(), entry.getValue() );
        }

        return p;
    }

    public boolean endsWithEol()
    {
        if ( nonNull( getFooter() ) )
        {
            return getFooter().isEol();
        }
        else if ( getEntries().size() > 0 )
        {
            return getEntries().get( getEntries().size() - 1 ).isEol();
        }
        else if ( nonNull( getHeader() ) )
        {
            return getHeader().isEol();
        }

        return false;
    }

    public void appendEol()
    {
        if ( nonNull( getFooter() ) )
        {
            getFooter().setEol( true );
        }
        else if ( getEntries().size() > 0 )
        {
            getEntries().get( getEntries().size() - 1 ).setEol( true );
        }
    }
}
