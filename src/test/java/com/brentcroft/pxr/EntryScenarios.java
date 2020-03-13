package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Ignore;
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
                        "    <entry key='a' index='1' sep='' eol='0'/>\n" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_space() throws TransformerException
    {
        runRoundTrip(

                "a ",

                "<properties>\n" +
                        "    <entry key='a' index='1' sep=' ' eol='0'/>\n" +
                        "</properties>"
        );
    }

    @Test
    public void space_single_key() throws TransformerException
    {
        runRoundTrip(

                " a",

                "<properties>" +
                        "    <comment key='a' eol='0'><![CDATA[ ]]></comment>" +
                        "    <entry key='a' index='1' sep='' eol='0'/>" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_space5() throws TransformerException
    {
        runRoundTrip(

                "a     ",

                "<properties>" +
                        "    <entry key='a' index='1' sep='     ' eol='0'/>" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_value() throws TransformerException
    {
        runRoundTrip(

                "a=b",

                "<properties>" +
                        "    <entry key='a' index='1' eol='0'>b</entry>" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_value_fat_sep() throws TransformerException
    {
        runRoundTrip(

                "a = b",

                "<properties>" +
                        "    <entry key='a' index='1' sep=' = ' eol='0'>b</entry>" +
                        "</properties>"
        );
    }

    @Test
    public void a_b_c() throws TransformerException
    {
        runRoundTrip(

                "a = b + c",

                "<properties>" +
                        "    <entry key='a' index='1' sep=' = ' eol='0'>b + c</entry>" +
                        "</properties>"
        );
    }

    @Test
    public void lf_a_b_c() throws TransformerException
    {
        runRoundTrip(

                "\na = b + c",

                "<properties>" +
                        "    <comment key='a'/>" +
                        "    <entry key='a' index='1' sep=' = ' eol='0'>b + c</entry>" +
                        "</properties>"
        );
    }

    @Test
    @Ignore
    public void weird_text() throws TransformerException
    {
        runRoundTrip(

                "# the “allow” to",

                "<properties>" +
                        "    <comment key='_footer' eol='0'><![CDATA[# the “allow” to]]></comment>" +
                        "</properties>",

                "UTF-8"
        );
    }
}
