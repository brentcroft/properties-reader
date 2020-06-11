package com.brentcroft.pxr.fixtures;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

public class GivenProperties extends Stage<GivenProperties>
{
    @ProvidedScenarioState
    String propertiesText;

    @ProvidedScenarioState
    String propertiesXml;

    @ScenarioState
    String transformResult;

    @As("properties text: \n[$]\n")
    public GivenProperties properties_text( String propertiesText )
    {
        this.propertiesText = propertiesText;

        return self();
    }

    public GivenProperties properties_text_from_transform_result( )
    {
        this.propertiesText = transformResult;

        return self();
    }

    @As("properties file: \n[$]\n")
    public GivenProperties properties_file( String filename  ) throws IOException
    {
        this.propertiesText = readFile( new FileInputStream( filename ), "ISO_8859_1" );

        return self();
    }


    @As("properties xml: \n[$]\n")
    public GivenProperties properties_xml( String propertiesXml )
    {
        this.propertiesXml = propertiesXml;

        return self();
    }


    public static String readFile( InputStream inputStream, String charset )
    {
        Scanner scanner = new Scanner( inputStream, charset );

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
