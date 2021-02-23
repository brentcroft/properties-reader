package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Getter
@Setter
public class PxrComment
{
    private int linesBefore;
    private String key;
    private boolean eol;
    private boolean charactersWritten;

    private final StringBuilder value = new StringBuilder();

    public String getText()
    {
        return value.toString();
    }


    public void ingest( String space, String init, String text, boolean newEol )
    {
        maybeAppend( space );
        maybeAppend( init );
        maybeAppend( text );

        // absorb empty lines in linesBefore
        // until charactersWritten
        if ( newEol )
        {
            if ( charactersWritten )
            {
                switchEolOnText();
            }
            else if ( eol )
            {
                linesBefore++;
            }

            eol = true;
        }
    }

    public void maybeAppend( String text )
    {
        if ( nonNull( text ) )
        {
            switchEolOnText();

            value.append( text );

            charactersWritten = true;
        }
    }

    private void switchEolOnText()
    {
        if ( eol )
        {
            if ( charactersWritten )
            {
                value.append( "\n" );
            }
            else
            {
                linesBefore++;
            }
        }
        eol = false;
    }

    public String jsonate()
    {
        return format( "{ // comment%n  key: \"%s\"%s%s%s}",
                key,
                ( linesBefore > 0 ? format( ",%n  linesBefore: %s", linesBefore ) : "" ),
                ( eol ? "\n  eol: 1" : "" ),
                Optional
                        .of( value )
                        .map( StringBuilder::toString )
                        .map( c -> c.replaceAll( "\n", "\\\\n" ) )
                        .map( c -> format( "value: \"%s\"", c ) )
                        .map( PxrItem::offset )
                        .map( c -> ", \n" + c )
                        .orElse( "" )
        );
    }

}