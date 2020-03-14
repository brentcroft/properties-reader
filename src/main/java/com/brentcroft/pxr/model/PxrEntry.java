package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.brentcroft.pxr.PxrUtils.nonNull;

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

        if ( nonNull( continuations ) )
        {
            for ( PxrEntryContinuation pec : continuations )
            {
                b.append( pec.getValue() );
                if ( pec.isEol() )
                {
                    b.append( "\n" );
                }
            }
        }

        return b.toString();
    }
}
