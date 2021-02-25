package com.brentcroft.pxr;

import com.brentcroft.pxr.fixtures.AbstractScenarios;
import org.junit.Test;

public class MultiEntryScenariosTest extends AbstractScenarios
{

    @Test
    public void lf_abc() throws Exception
    {
        runRoundTrip(

                "\na = b + c",

                "<properties>\n" +
                        "    <comment key='a'/>\n" +
                        "    <entry key='a' index='0' sep=' = ' eol='0'>b + c</entry>\n" +
                        "</properties>"
        );
    }


    @Test
    public void abc_def_lf() throws Exception
    {
        runRoundTrip(

                "a = b + c\nd = e + f\n",

                "<properties>\n" +
                        "    <entry key='a' index='0' sep=' = '>b + c</entry>\n" +
                        "    <entry key='d' index='1' sep=' = '>e + f</entry>\n" +
                        "</properties>"
        );
    }


    @Test
    public void lf2_xy_yz_za_ab_lf2() throws Exception
    {
        runRoundTrip(

                "\n\nx=y\ny=z\nz=a\na=b\n\n",

                "<properties>\n" +
                        "    <comment key='x' lines-before='1'/>\n" +
                        "    <entry key='x' index='0'>y</entry>\n" +
                        "    <entry key='y' index='1'>z</entry>\n" +
                        "    <entry key='z' index='2'>a</entry>\n" +
                        "    <entry key='a' index='3'>b</entry>\n" +
                        "    <comment key='_footer'/>\n" +
                        "</properties>"
        );
    }
}
