package com.brentcroft.pxr.fixtures;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

public class GivenProperties extends Stage<GivenProperties>
{
    @ProvidedScenarioState
    String propertiesText;

    @ProvidedScenarioState
    String propertiesXml;

    @As("properties text: \n[$]\n")
    public GivenProperties properties_text( String propertiesText )
    {
        this.propertiesText = propertiesText;

        return self();
    }

    @As("properties file: \n[$]\n")
    public GivenProperties properties_file( String filename ) throws IOException
    {
        this.propertiesText = readFile( new FileInputStream( filename ) );

        return self();
    }


    @As("properties xml: \n[$]\n")
    public GivenProperties properties_xml( String propertiesXml )
    {
        this.propertiesXml = propertiesXml;

        return self();
    }


    public static String readFile( InputStream inputStream ) throws IOException
    {
        Scanner scanner = new Scanner( inputStream );

        try
        {
            scanner.useDelimiter( "\\A" );

            return scanner.hasNext() ? scanner.next() : "";
        }
        finally
        {
            scanner.close();
        }
    }
}
