package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;


@Getter
@Setter
public class PxrEntryContinuation
{
    private int index;
    private String cont;
    private String prefix;
    private String value;
    private boolean eol;

    public String jsonate()
    {
        final String indent = "  ";
        return format( "{ // text %n%s%s%s%s%s}",
                indent + "index: " + index,
                ofNullable( cont )
                        .map( c -> c.replaceAll( "\n", "\\\\n" ) )
                        .map( c -> format( "cont: \"%s\"", c ) )
                        .map( PxrItem::offset )
                        .map( c -> ", \n" + c )
                        .orElse( "" ),
                ( eol ? "eol: 1\n" : "" ),
                ofNullable( prefix )
                        .map( c -> c.replaceAll( "\n", "\\\\n" ) )
                        .map( c -> format( "prefix: \"%s\"", c ) )
                        .map( PxrItem::offset )
                        .map( c -> ", \n" + c )
                        .orElse( "" ),
                ofNullable( value )
                        .map( c -> c.replaceAll( "\n", "\\\\n" ) )
                        .map( c -> format( "value: \"%s\"", c ) )
                        .map( PxrItem::offset )
                        .map( c -> ", \n" + c )
                        .orElse( "" )
        );
    }
}
