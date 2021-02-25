package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import com.brentcroft.pxr.parser.ParseException;
import org.junit.Test;

import javax.xml.transform.TransformerException;

import java.io.UnsupportedEncodingException;

import static com.brentcroft.pxr.fixtures.Utils.EPILOG;
import static com.brentcroft.pxr.fixtures.Utils.PROLOG;

public class PxrWriterScenariosTest extends AbstractScenarios
{
    @Test
    public void round_trip() throws TransformerException, ParseException, UnsupportedEncodingException
    {
        given()
                .properties_text( "" +
                        "#sample header\n" +
                        "\n" +
                        "color=red\n" +
                        "\n" +
                        "#the size is large\n" +
                        "size=large\n" +
                        "\n" +
                        "#sample footer" );
        when()
                .parse_text_to_pxr_properties()
                .transform_pxr_to_properties_xml();
        then()
                .pxr_properties_is_not_null()
                .xml_text_equals( PROLOG +
                        "<properties>\n" +
                        "    <comment key='_header'>\n" +
                        "        <![CDATA[#sample header]]>\n" +
                        "    </comment>\n" +
                        "    <comment key='color'/>\n" +
                        "    <entry key='color' index='0'>red</entry>\n" +
                        "    <comment key='size' lines-before='1'>\n" +
                        "        <![CDATA[#the size is large]]>\n" +
                        "    </comment>\n" +
                        "    <entry key='size' index='1'>large</entry>\n" +
                        "    <comment key='_footer' lines-before='1' eol='0'>\n" +
                        "        <![CDATA[#sample footer]]>\n" +
                        "    </comment>\n" +
                        "</properties>" + EPILOG );

        when()
                .transform_pxr_to_properties_text();
        then()
                .same_properties_text();
    }

    @Test
    public void creates_entry() throws Exception
    {
        given()
                .properties_text( "" )
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <entry key='color'>red</entry>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "color=red\n" );
    }

    @Test
    public void append_entry() throws Exception
    {
        given()
                .properties_text( "color=red\n" )
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <entry key='size'>large</entry>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "color=red\nsize=large\n" );
    }

    @Test
    public void append_entry_inserting_line_break() throws Exception
    {
        given()
                .properties_text( "color=red" )
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <entry key='size'>large</entry>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "color=red\nsize=large\n" );
    }

    @Test
    public void append_entry_with_comment() throws Exception
    {
        given()
                .properties_text( "color=red\n" )
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <comment key='size'>the size is large</comment>\n" +
                        "   <entry key='size'>large</entry>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "color=red\n#the size is large\nsize=large\n" );
    }


    @Test
    public void delete_entry_and_comment() throws Exception
    {
        String text = "" +
                "# sample header\n" +
                "\n" +
                "#the color is red\n" +
                "color=red\n" +
                "\n" +
                "#the size is large\n" +
                "size=large\n" +
                "\n" +
                "# sample footer";

        given()
                .properties_text( text )
                .properties_xml( PROLOG +
                        "<update xmlns:del=\"remove\">\n" +
                        "   <del:comment key='size'/>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
//        then()
//                .transform_result_is( "" +
//                        "# sample header\n" +
//                        "\n" +
//                        "#the color is red\n" +
//                        "color=red\n" +
//                        "size=large\n" +
//                        "\n" +
//                        "# sample footer" );


        given()
                .properties_xml( PROLOG +
                        "<update xmlns:del=\"remove\">\n" +
                        "   <del:entry key='color'/>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "" +
                        "# sample header\n" +
                        "\n" +
                        "#the size is large\n" +
                        "size=large\n" +
                        "\n" +
                        "# sample footer" );


        given()
                .properties_xml( PROLOG +
                        "<update xmlns:del=\"remove\">\n" +
                        "   <del:entry key='color'/>\n" +
                        "   <del:entry key='size'/>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "" +
                        "# sample header\n" +
                        "\n" +
                        "# sample footer" );
    }


    @Test
    public void add_header() throws Exception
    {
        String text = "" +
                "color=red\n" +
                "\n" +
                "#the size is large\n" +
                "size=large\n";

        given()
                .properties_text( text );

        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <comment key='_header'>sample header</comment>\n" +
                        "</update>" + EPILOG );

        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is(
                        "#sample header\n" +
                                "\n" +
                                "color=red\n" +
                                "\n" +
                                "#the size is large\n" +
                                "size=large\n" );

        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <comment key='_header'>new header</comment>\n" +
                        "</update>" + EPILOG );

        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is(
                        "#new header\n" +
                                "\n" +
                                "color=red\n" +
                                "\n" +
                                "#the size is large\n" +
                                "size=large\n" );

        given()
                .properties_xml( PROLOG +
                        "<update xmlns:act='remove'>\n" +
                        "   <act:comment key='_header'/>\n" +
                        "</update>" + EPILOG );

        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is(
                        "color=red\n" +
                                "\n" +
                                "#the size is large\n" +
                                "size=large\n" );
    }


    @Test
    public void add_footer() throws Exception
    {
        String text = "size=large";

        given()
                .properties_text( text );


        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <comment key='_footer'>sample footer</comment>\n" +
                        "</update>" + EPILOG );
        when()
                .apply_update();
        then()
                .transform_result_is( "" +
                        "size=large\n\n" +
                        "#sample footer" );

        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <comment key='_footer' lines-before='1' eol='0'>sample footer</comment>\n" +
                        "</update>" + EPILOG );
        when()
                .apply_update();
        then()
                .transform_result_is( "" +
                        "size=large\n\n" +
                        "#sample footer" );

        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <comment key='_footer' lines-before='2' eol='0'>new footer</comment>\n" +
                        "</update>" + EPILOG );
        when()
                .apply_update();
        then()
                .transform_result_is( "" +
                        "size=large\n\n" +
                        "#new footer" );


        given()
                .properties_xml( PROLOG +
                        "<update xmlns:exp='remove'>\n" +
                        "   <exp:comment key='_footer'/>\n" +
                        "</update>" + EPILOG );
        when()
                .apply_update();
        then()
                .transform_result_is( "size=large" );
    }


    @Test
    public void line_continuation() throws Exception
    {
        given()
                .properties_text( "color=red\nsize=large" )
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "    <entry key='color'>\n" +
                        "        <text>blue, </text>\n" +
                        "        <text prefix='    ' >red</text>\n" +
                        "    </entry>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "" +
                        "color=blue, \\\n" +
                        "    red\n" +
                        "size=large" );

        given()
                .properties_text_from_transform_result()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "    <entry key='color'>green</entry>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "" +
                        "color=green\n" +
                        "size=large" );
    }
}
