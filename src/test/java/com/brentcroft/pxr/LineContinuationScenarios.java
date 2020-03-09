package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

import javax.xml.transform.TransformerException;

public class LineContinuationScenarios extends AbstractScenarios
{

    @Test
    public void double_continuation_with_illegal_comment_and_text() throws TransformerException
    {
        runRoundTrip(

                "# xxx\n\n" +
                        "# double continuation\n" +
                        "a = b \\ # illegal comment \n" +
                        "  \\ no value, just more illegal text \n" +
                        "   c d e\n" +
                        "# that's it",

                "<properties>\n" +
                        "    <comment key='_header'><![CDATA[# xxx]]></comment>\n" +
                        "    <comment key='a' lines-before='1'><![CDATA[# double continuation]]></comment>\n" +
                        "    <entry key='a' index='1' sep=' = '>\n" +
                        "        <text key='0'>b </text>\n" +
                        "        <text key='1' cont='\\ # illegal comment ' prefix='  '/>\n" +
                        "        <text key='2' cont='\\ no value, just more illegal text ' prefix='   '>c d e</text>\n" +
                        "    </entry>\n" +
                        "    <comment key='_footer' eol='0'><![CDATA[# that's it]]></comment>\n" +
                        "</properties>"
        );
    }
}
