package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

import static com.brentcroft.pxr.model.PxrItem.offset;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Getter
@Setter
public class PxrEntry
{
    private PxrComment comment;
    private int index;
    private String key;
    private String sep;
    private String value;
    private boolean eol;
    private List< PxrEntryContinuation > continuations;

    public String getText()
    {
        StringBuilder b = new StringBuilder();

        if ( nonNull( value ) )
        {
            b.append( value );
        }

        if ( eol )
        {
            b.append( "\n" );
        }

        if ( nonNull( continuations ) )
        {
            for ( PxrEntryContinuation pec : continuations )
            {
                if ( nonNull( pec.getValue() ) )
                {
                    b.append( pec.getValue() );
                }

                if ( pec.isEol() )
                {
                    b.append( "\n" );
                }
            }
        }

        return b.toString();
    }

    public String jsonate()
    {
        return format( "%s{ // entry %n  key: \"%s\",%n  index: %s%s%s%s%s}",
                ( isNull( comment ) ? "" : comment.jsonate() + ",\n" ),
                key,
                index,
                ofNullable( sep )
                        .map( c -> c.replaceAll( "\n", "\\\\n" ) )
                        .map( c -> format( "sep: \"%s\"", c ) )
                        .map( PxrItem::offset )
                        .map( c -> ", \n" + c )
                        .orElse( "" ),
                ( eol ? ",\n  eol: 1" : "" ),
                ofNullable( value )
                        .map( c -> c.replaceAll( "\n", "\\\\n" ) )
                        .map( c -> format( "value: \"%s\"", c ) )
                        .map( PxrItem::offset )
                        .map( c -> ", \n" + c )
                        .orElse( "" ),
                isNull( getContinuations() )
                ? ""
                : "[ \n" + offset( getContinuations()
                        .stream()
                        .map( PxrEntryContinuation::jsonate )
                        .collect( Collectors.joining( ", \n" ) ) ) + "\n]"
        );
    }
}
