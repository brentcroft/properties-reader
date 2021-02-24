package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;


@Getter
@Setter
public class PxrProperties implements PxrItem
{
    private String systemId = null;
    private PxrComment header;
    private PxrComment footer;

    private final List< PxrEntry > entries = new ArrayList<>();
    private final Map< String, PxrEntry > entryMap = new HashMap<>();

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

        reIndex();

        if ( nonNull( entry.getKey() ) )
        {
            entryMap.put( entry.getKey(), entry );
        }
    }

    public void remove( String key )
    {
        PxrEntry entry = entryMap.get( key );

        if ( nonNull( entry ) )
        {
            entryMap.remove( key );
            entries.remove( entry );
        }

        reIndex();
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

    public String jsonate()
    {
        return format( "%s%s%s",
                ofNullable( header )
                        .map( PxrComment::jsonate )
                        .map( PxrItem::offset )
                        .orElse( "" ),
                "[ \n" + getEntries()
                        .stream()
                        .map( PxrEntry::jsonate )
                        .map( PxrItem::offset )
                        .collect( Collectors.joining( ", \n" ) ) + "\n]",
                ofNullable( footer )
                        .map( PxrComment::jsonate )
                        .map( PxrItem::offset )
                        .orElse( "" )
        );
    }

    public void reIndex()
    {
        int[] index = {0};
        entries
                .sort( Comparator.comparingInt( PxrEntry::getIndex ) );
        entries
                .forEach( e -> e.setIndex( index[ 0 ]++ ) );
    }
}
