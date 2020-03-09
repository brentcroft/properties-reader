package com.brentcroft.pxr.fixtures;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import java.io.FileWriter;
import java.io.IOException;

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

    @As("properties xml: \n[$]\n")
    public GivenProperties properties_xml( String propertiesXml )
    {
        this.propertiesXml = propertiesXml;

        return self();
    }


    public GivenProperties write_text_file( String path, String text ) throws IOException
    {
        try ( FileWriter fw = new FileWriter( path ) )
        {
            fw.write( text );
        }

        return self();
    }

}
