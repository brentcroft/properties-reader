package com.brentcroft.pxr;


import lombok.Getter;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Getter
public class ExpectationException extends RuntimeException
{
    private final String key;
    private final String expected;
    private final String actual;

    public ExpectationException( String key, String expected, String actual )
    {
        super( format(
                "Invalid expectation: key='%s', actual='%s', expected='%s'",
                key,
                isNull( actual ) ? "null" : actual.trim(),
                expected ) );
        this.key = key;
        this.expected = expected;
        this.actual = isNull( actual ) ? null : actual.trim();
    }

    public static void validateExpected( String key, String expected, String actual )
    {
        if ( nonNull( expected ) )
        {
            ofNullable( actual )
                    .map( String::trim )
                    .filter( expected::equals )
                    .orElseThrow( () -> new ExpectationException( key, expected, actual ) );
        }
    }
}
