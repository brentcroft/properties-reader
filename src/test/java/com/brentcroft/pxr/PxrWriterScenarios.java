package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import com.brentcroft.pxr.parser.ParseException;
import org.junit.Test;

import javax.xml.transform.TransformerException;

import static com.brentcroft.pxr.fixtures.Utils.EPILOG;
import static com.brentcroft.pxr.fixtures.Utils.PROLOG;

public class PxrWriterScenarios extends AbstractScenarios
{
    @Test
    public void round_trip() throws TransformerException, ParseException
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
                .parse_text_to_pxr_properties();
        then()
                .pxr_properties_is_not_null();
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
                .properties_text( text );


        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <act:comment key='size'/>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "" +
                        "# sample header\n" +
                        "\n" +
                        "#the color is red\n" +
                        "color=red\n" +
                        "size=large\n" +
                        "\n" +
                        "# sample footer" );


        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <act:entry key='color'/>\n" +
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
                        "<update>\n" +
                        "   <act:entry key='color'/>\n" +
                        "   <act:entry key='size'/>\n" +
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
                        "size=large\n" +
                        "#sample footer\n" );

        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <comment key='_footer' lines-before='1' eol='false'>sample footer</comment>\n" +
                        "</update>" + EPILOG );
        when()
                .apply_update();
        then()
                .transform_result_is( "" +
                        "size=large\n" +
                        "\n" +
                        "#sample footer" );

        given()
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "   <exp:comment key='_footer'/>\n" +
                        "</update>" + EPILOG );
        when()
                .apply_update();
        then()
                .transform_result_is( "size=large" );
    }


    @Test
    public void line_continuations() throws Exception
    {
        given()
                .properties_text( "color=red\nsize=large" )
                .properties_xml( PROLOG +
                        "<update>\n" +
                        "    <entry key='color'>\n" +
                        "        <text key='0'>blue, </text>\n" +
                        "        <text key='1' cont='\\' prefix='    ' >red</text>\n" +
                        "    </entry>\n" +
                        "</update>" + EPILOG );
        when()
                .parse_text_to_pxr_properties()
                .update_pxr_properties()
                .transform_pxr_to_properties_text();
        then()
                .transform_result_is( "" +
                        "color=blue, \\\n" +
                        "    red\nsize=large" );
    }

}
