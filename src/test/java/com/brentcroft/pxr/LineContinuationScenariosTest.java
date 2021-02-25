package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

public class LineContinuationScenariosTest extends AbstractScenarios
{

    @Test
    public void double_continuation_with_illegal_comment_and_text() throws Exception
    {
        runRoundTrip(

                "# xxx\n\n" +
                        "# double continuation\n" +
                        "a = b \\\n" +
                        "  \\\n" +
                        "   c d e\n" +
                        "# that's it",

                "<properties>\n" +
                        "    <comment key='_header'><![CDATA[# xxx]]></comment>\n" +
                        "    <comment key='a' lines-before='1'><![CDATA[# double continuation]]></comment>\n" +
                        "    <entry key='a' index='0' sep=' = '>\n" +
                        "        <text key='0'>b </text>\n" +
                        "        <text key='1' prefix='  '/>\n" +
                        "        <text key='2' prefix='   '>c d e</text>\n" +
                        "    </entry>\n" +
                        "    <comment key='_footer' eol='0'><![CDATA[# that's it]]></comment>\n" +
                        "</properties>"
        );
    }

    @Test
    public void comments_with_separators() throws Exception
    {
        runRoundTrip(

                "# xx=x!x asdasd\n\n" +
                        "# double continuation\n" +
                        "a = b \\\n" +
                        "  \\\n" +
                        "   c d e\n" +
                        "# that's it",

                "<properties>\n" +
                        "    <comment key='_header'><![CDATA[# xx=x!x asdasd]]></comment>\n" +
                        "    <comment key='a' lines-before='1'><![CDATA[# double continuation]]></comment>\n" +
                        "    <entry key='a' index='0' sep=' = '>\n" +
                        "        <text key='0'>b </text>\n" +
                        "        <text key='1' prefix='  '/>\n" +
                        "        <text key='2' prefix='   '>c d e</text>\n" +
                        "    </entry>\n" +
                        "    <comment key='_footer' eol='0'><![CDATA[# that's it]]></comment>\n" +
                        "</properties>"
        );
    }
}
