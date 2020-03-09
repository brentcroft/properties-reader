package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

import javax.xml.transform.TransformerException;

public class MultiEntryScenarios extends AbstractScenarios
{

    @Test
    public void lf_abc() throws TransformerException
    {
        runRoundTrip(

                "\na = b + c",

                "<properties>\n" +
                        "    <comment key=\"a\" lines-before=\"1\" eol=\"0\"/>\n" +
                        "    <entry key=\"a\" index=\"1\" sep=\" = \" eol=\"0\">b + c</entry>\n" +
                        "</properties>"
        );
    }


    @Test
    public void abc_def_lf() throws TransformerException
    {
        runRoundTrip(

                "a = b + c\nd = e + f\n",

                "<properties>\n" +
                        "    <entry key=\"a\" index=\"1\" sep=\" = \">b + c</entry>\n" +
                        "    <entry key=\"d\" index=\"2\" sep=\" = \">e + f</entry>\n" +
                        "</properties>"
        );
    }


    @Test
    public void lf2_xy_yz_za_ab_lf2() throws TransformerException
    {
        runRoundTrip(

                "\n\nx=y\ny=z\nz=a\na=b\n\n",

                "<properties>\n" +
                        "    <comment key=\"x\" lines-before=\"2\" eol=\"0\"/>\n" +
                        "    <entry key=\"x\" index=\"1\" sep=\"=\">y</entry>\n" +
                        "    <entry key=\"y\" index=\"2\" sep=\"=\">z</entry>\n" +
                        "    <entry key=\"z\" index=\"3\" sep=\"=\">a</entry>\n" +
                        "    <entry key=\"a\" index=\"4\" sep=\"=\">b</entry>\n" +
                        "    <comment key=\"_footer\" lines-before=\"1\" eol=\"0\"/>\n" +
                        "</properties>"
        );
    }
}
