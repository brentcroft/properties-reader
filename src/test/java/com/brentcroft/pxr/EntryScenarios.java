package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class EntryScenarios extends AbstractScenarios
{

    @Test
    public void single_key() throws Exception
    {
        runRoundTrip(

                "a",

                "<properties>\n" +
                        "    <entry key='a' index='1' sep='' eol='0'/>\n" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_space() throws Exception
    {
        runRoundTrip(

                "a ",

                "<properties>\n" +
                        "    <entry key='a' index='1' sep=' ' eol='0'/>\n" +
                        "</properties>"
        );
    }

    @Test
    public void space_single_key() throws Exception
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
    public void single_key_space5() throws Exception
    {
        runRoundTrip(

                "a     ",

                "<properties>" +
                        "    <entry key='a' index='1' sep='     ' eol='0'/>" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_value() throws Exception
    {
        runRoundTrip(

                "a=b",

                "<properties>" +
                        "    <entry key='a' index='1' eol='0'>b</entry>" +
                        "</properties>"
        );
    }

    @Test
    public void single_key_value_fat_sep() throws Exception
    {
        runRoundTrip(

                "a = b",

                "<properties>" +
                        "    <entry key='a' index='1' sep=' = ' eol='0'>b</entry>" +
                        "</properties>"
        );
    }

    @Test
    public void a_b_c() throws Exception
    {
        runRoundTrip(

                "a = b + c",

                "<properties>" +
                        "    <entry key='a' index='1' sep=' = ' eol='0'>b + c</entry>" +
                        "</properties>"
        );
    }

    @Test
    public void lf_a_b_c() throws Exception
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
    public void weird_text() throws Exception
    {
        runRoundTrip(

                "# the “allow” to",

                "<properties>" +
                        "    <comment key='_footer' eol='0'><![CDATA[# the “allow” to]]></comment>" +
                        "</properties>",

                "UTF-8"
        );
    }

    @Test
    public void weird_text_wrong_encoding() throws Exception
    {
        given()
                .properties_text( "# the “allow” to" );

        when()
                .transform_text_to_xml_text( "ISO-8859-1" );

        then()
                .xml_text_not_equals( "<properties>" +
                        "    <comment key='_footer' eol='0'><![CDATA[# the “allow” to]]></comment>" +
                        "</properties>" );
        when()
                .transform_xml_to_properties_text();
        then()
                .different_properties_text();
    }

    @Test
    public void large_text() throws IOException, Exception
    {
        given()
                .properties_file( "src/test/resources/test.properties" );

        when()
                .transform_text_to_xml_text( "UTF-8" );

        then()
                .xml_text_equals_file( "src/test/resources/test.xml" );

        when()
                .transform_xml_to_properties_text();
        then()
                .same_properties_text();
    }
}
