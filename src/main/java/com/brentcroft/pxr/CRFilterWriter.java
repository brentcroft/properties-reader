package com.brentcroft.pxr;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

/**
 * Wrap a writer to remove all CRs.
 * <p>
 * see: https://stackoverflow.com/questions/53596202/javax-xml-transform-transformer-line-endings-no-longer-respect-system-property
 */
public class CRFilterWriter extends FilterWriter
{
    private final Set< Character > excludedCharacters;

    public CRFilterWriter( Writer out, char... characters )
    {
        super( out );

        excludedCharacters = ofNullable( characters )
                .filter( chars -> chars.length > 0 )
                .map( chars -> {
                    Set< Character > filters = new HashSet<>();
                    for ( char c : characters )
                    {
                        filters.add( c );
                    }
                    return filters;
                } )
                .orElse( null );
    }

    public static CRFilterWriter from( Writer out, char... characters )
    {
        return new CRFilterWriter( out, characters );
    }

    public Writer getWriter()
    {
        return out;
    }

    public String toString()
    {
        return getWriter().toString();
    }


    private boolean isExcluded( char c )
    {
        return nonNull( excludedCharacters ) && excludedCharacters.contains( c );
    }

    private boolean isIncluded( int c )
    {
        return isNull( excludedCharacters ) || ! excludedCharacters.contains( ( char ) c );
    }

    @Override
    public void write( int c ) throws IOException
    {
        if ( isIncluded( c ) )
        {
            super.write( c );
        }
    }

    @Override
    public void write( char[] cbuf, int offset, int length ) throws IOException
    {
        int localOffset = offset;
        for ( int i = localOffset, n = offset + length; i < n; ++ i )
        {
            if ( isExcluded( cbuf[ i ] ) )
            {
                if ( i > localOffset )
                {
                    super.write( cbuf, localOffset, i - localOffset );
                }
                localOffset = i + 1;
            }
        }
        if ( localOffset < offset + length )
        {
            super.write( cbuf, localOffset, offset + length - localOffset );
        }
    }

    @Override
    public void write( String str, int offset, int length ) throws IOException
    {
        int localOffset = offset;
        for ( int i = localOffset, n = offset + length; i < n; ++ i )
        {
            if ( isExcluded( str.charAt( i ) ) )
            {
                if ( i > localOffset )
                {
                    super.write( str, localOffset, i - localOffset );
                }
                localOffset = i + 1;
            }
        }
        if ( localOffset < offset + length )
        {
            super.write( str, localOffset, offset + length - localOffset );
        }
    }
}
