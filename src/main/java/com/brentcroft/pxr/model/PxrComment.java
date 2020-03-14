package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import static com.brentcroft.pxr.PxrUtils.nonNull;


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

    private void maybeAppend( String text )
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
}