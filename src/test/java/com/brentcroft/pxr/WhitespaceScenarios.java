package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

import javax.xml.transform.TransformerException;

public class WhitespaceScenarios extends AbstractScenarios
{
    @Test
    public void empty() throws TransformerException
    {
        runRoundTrip( "", "<properties/>" );
    }

    @Test
    public void space_one() throws TransformerException
    {
        runRoundTrip(
                " ",
                "<properties>\n" +
                        "    <comment key=\"_footer\" eol=\"0\"><![CDATA[ ]]></comment>\n" +
                        "</properties>"
        );
    }

    @Test
    public void space_eight() throws TransformerException
    {
        runRoundTrip(
                "        ",
                "<properties>\n" +
                        "    <comment key=\"_footer\" eol=\"0\"><![CDATA[        ]]></comment>\n" +
                        "</properties>"
        );
    }
}
