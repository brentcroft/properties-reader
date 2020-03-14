# properties-reader
JavaCC grammar and kit to generate SAX events describing a java properties file, and an XSL transform to regenerate the original file.

Java 1.5+

Following, and extending, on the decomposition of Java properties text specified by Apache PropertiesConfiguration,
this kit attempts to provide 100% round trip fidelity 
between Java properties text and a simple XML form (extending the Java properties XML specification) 
via a simple object model (PxrProperties).

The XML form is extended to support insert, update and delete actions on all parts of a properties text.

Summary operations are provided in: 

 - [PxrUtils](src/main/java/com/brentcroft/pxr/PxrUtils.java)


## JavaCC grammar
A JavaCC grammar generates the PxrParser which parses PxrProperties objects from properties text:
 - [Properties grammar file](src/main/resources/jjt/properties.jjt)

## PxrReader
A PxrReader generates emits SAX events from a PxrProperties object.

For example, given the following properties text:

```properties
#sample header

color=red

#the size is large
size=large

#sample footer
```

when the properties text is parsed the  SAX events will be:

```xml
<properties>
    <comment key="_header">
        <![CDATA[#sample header]]>
    </comment>
    <comment key="color"/>
    <entry key="color" index="1">red</entry>
    <comment key="size" lines-before="1">
        <![CDATA[#the size is large]]>
    </comment>
    <entry key="size" index="2">large</entry>
    <comment key="_footer" lines-before="1" eol="0">
        <![CDATA[#sample footer]]>
    </comment>
</properties>
```



 - [PxrReader](src/main/java/com/brentcroft/pxr/PxrReader.java)

## PxrWriter
A PxrWriter handles SAX events to create or update a PxrProperties object.

For example, given the properties text:
```properties
color=red
size=large
```

when the following SAX events are handled:
```xml
<update>
    <entry key='color'>
        <text>blue, </text>
        <text cont='\' prefix='    ' >red</text>
    </entry>
</update>
```

Then the properties text will be:
```properties
color=blue, \
    red
size=large
```

 - [PxrWriter](src/main/java/com/brentcroft/pxr/PxrWriter.java)

## XSL Transform
An XSL transform generates a properties text from SAX events:
 - [Properties file builder](src/main/resources/xslt/properties.xslt)
