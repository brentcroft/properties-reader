# properties-reader
JavaCC grammar and kit to generate SAX events describing a java properties file, and an XSL transform to regenerate the original file.

Following, and extending, on the decomposition of Java properties text specified by Apache PropertiesConfiguration,
this kit attempts to provide 100% round trip fidelity 
between Java properties text and a simple XML form (extending the Java properties XML specification) 
via a simple object model (PxrProperties).

The XML form is extended to support insert, update and delete actions on all parts of a properties text.

## JavaCC grammar

A JavaCC grammar generates the PxrParser which parses PxrProperties objects from properties text:

 - [Properties grammar file](src/main/resources/jjt/properties.jjt)

## PxrReader
A PxrReader generates emits SAX events from a PxrProperties object.

 - [PxrReader](src/main/java/com/brentcroft/pxr/PxrReader.java)

## PxrWriter
A PxrWriter handles SAX events to create or update a PxrProperties object.

 - [PxrWriter](src/main/java/com/brentcroft/pxr/PxrWriter.java)

## XSL Transform

An XSL transform generates a properties text from SAX events:

 - [Properties file builder](src/main/resources/xslt/properties.xslt)
