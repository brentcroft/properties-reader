package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

public class WhitespaceScenariosTest extends AbstractScenarios
{
    @Test
    public void empty() throws Exception
    {
        runRoundTrip( "", "<properties/>" );
    }

    @Test
    public void space_one() throws Exception
    {
        runRoundTrip(
                " ",
                "<properties>\n" +
                        "    <comment key=\"_footer\" eol=\"0\"><![CDATA[ ]]></comment>\n" +
                        "</properties>"
        );
    }

    @Test
    public void space_eight() throws Exception
    {
        runRoundTrip(
                "        ",
                "<properties>\n" +
                        "    <comment key=\"_footer\" eol=\"0\"><![CDATA[        ]]></comment>\n" +
                        "</properties>"
        );
    }


    @Test
    public void tab() throws Exception
    {
        runRoundTrip(
                "\t",
                "<properties>\n" +
                        "    <comment key=\"_footer\" eol=\"0\"><![CDATA[\t]]></comment>\n" +
                        "</properties>"
        );
    }
}
