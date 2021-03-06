package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

public class EscapeScenariosTest extends AbstractScenarios
{

    @Test
    public void escape_key_other_q() throws Exception
    {
        given()
                .properties_text( "a\\qb=c\n" );

        when()
                .transform_text_to_xml_text();

        then()
                .xml_text_equals( "<properties>\n" +
                        "    <entry key='aqb' index='0'>c</entry>\n" +
                        "</properties>" );

        when()
                .transform_xml_to_properties_text();

        then()
                .transform_result_is( "aqb=c\n" );

        when()
                .transform_text_to_xml_text();

        then()
                .xml_text_equals( "<properties>\n" +
                        "    <entry key='aqb' index='0'>c</entry>\n" +
                        "</properties>" );
    }

    @Test
    public void escape_key_other_backslash() throws Exception
    {
        given()
                .properties_text( "a\\/b=c\n" );

        when()
                .transform_text_to_xml_text();

        then()
                .xml_text_equals( "<properties>\n" +
                        "    <entry key='a/b' index='0'>c</entry>\n" +
                        "</properties>" );

        when()
                .transform_xml_to_properties_text();

        then()
                .transform_result_is( "a/b=c\n" );

        when()
                .transform_text_to_xml_text();

        then()
                .xml_text_equals( "<properties>\n" +
                        "    <entry key='a/b' index='0'>c</entry>\n" +
                        "</properties>" );
    }

    @Test
    public void escape_key_equals() throws Exception
    {
        runRoundTrip(
                "a\\=b=c\n",
                "<properties>\n" +
                        "    <entry key='a=b' index='0'>c</entry>\n" +
                        "</properties>" );
    }


    @Test
    public void escape_key_colon() throws Exception
    {
        runRoundTrip(
                "a\\:b=c\n",
                "<properties>\n" +
                        "    <entry key='a:b' index='0'>c</entry>\n" +
                        "</properties>" );
    }


    @Test
    public void escape_key_space() throws Exception
    {
        runRoundTrip(
                "a\\ b=c\n",
                "<properties>\n" +
                        "    <entry key='a b' index='0'>c</entry>\n" +
                        "</properties>" );
    }
}
