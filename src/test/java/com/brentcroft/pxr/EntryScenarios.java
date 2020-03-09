package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

import javax.xml.transform.TransformerException;

public class EntryScenarios extends AbstractScenarios
{

    @Test
    public void single_key() throws TransformerException
    {
        runRoundTrip(

                "a",

                "<properties>\n" +
                        "    <entry key=\"a\" index=\"1\" eol=\"0\"/>\n" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_space() throws TransformerException
    {
        runRoundTrip(

                "a ",

                "<properties>\n" +
                        "    <entry key=\"a\" index=\"1\" sep=\" \" eol=\"0\"/>\n" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_space5() throws TransformerException
    {
        runRoundTrip(

                "a     ",

                "<properties>\n" +
                        "    <entry key=\"a\" index=\"1\" sep=\"     \" eol=\"0\"/>\n" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_value() throws TransformerException
    {
        runRoundTrip(

                "a=b",

                "<properties>\n" +
                        "    <entry key=\"a\" index=\"1\" sep=\"=\" eol=\"0\">b</entry>\n" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_value_fat_sep() throws TransformerException
    {
        runRoundTrip(

                "a = b",

                "<properties>\n" +
                        "    <entry key=\"a\" index=\"1\" sep=\" = \" eol=\"0\">b</entry>\n" +
                        "</properties>"
        );
    }

    @Test
    public void a_b_c() throws TransformerException
    {
        runRoundTrip(

                "a = b + c",

                "<properties>\n" +
                        "    <entry key=\"a\" index=\"1\" sep=\" = \" eol=\"0\">b + c</entry>\n" +
                        "</properties>"
        );
    }

    @Test
    public void lf_a_b_c() throws TransformerException
    {
        runRoundTrip(

                "\na = b + c",

                "<properties>\n" +
                        "    <comment key=\"a\" lines-before=\"1\" eol=\"0\"/>\n" +
                        "    <entry key=\"a\" index=\"1\" sep=\" = \" eol=\"0\">b + c</entry>\n" +
                        "</properties>"
        );
    }
}
