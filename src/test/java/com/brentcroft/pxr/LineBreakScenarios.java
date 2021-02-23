package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

public class LineBreakScenarios extends AbstractScenarios
{
    @Test
    public void lb_space_5() throws Exception
    {
        runRoundTrip(
                "\n       ",
                "<properties>\n" +
                        "    <comment key=\"_footer\" lines-before=\"1\" eol=\"0\"><![CDATA[       ]]></comment>\n" +
                        "</properties>" );
    }

    @Test
    public void lb_space() throws Exception
    {
        runRoundTrip(
                "\n ",
                "<properties>\n" +
                        "    <comment key=\"_footer\" lines-before=\"1\" eol=\"0\"><![CDATA[ ]]></comment>\n" +
                        "</properties>" );
    }


    @Test
    public void lb_tag_space() throws Exception
    {
        runRoundTrip(
                "\n\t ",
                "<properties>\n" +
                        "    <comment key=\"_footer\" lines-before=\"1\" eol=\"0\"><![CDATA[\t ]]></comment>\n" +
                        "</properties>" );
    }

    @Test
    public void lb() throws Exception
    {
        runRoundTrip(
                "\n",
                "<properties>\n" +
                        "    <comment key=\"_footer\"/>\n" +
                        "</properties>" );
    }

    @Test
    public void space_lb_space() throws Exception
    {
        runRoundTrip(
                " \n ",
                "<properties>\n" +
                        "    <comment key=\"_footer\" eol=\"0\"><![CDATA[ \n" +
                        " ]]></comment>\n" +
                        "</properties>" );
    }

    @Test
    public void space_5_lb() throws Exception
    {
        runRoundTrip(
                "    \n",
                "<properties>\n" +
                        "    <comment key=\"_footer\"><![CDATA[    ]]></comment>\n" +
                        "</properties>" );
    }
}
