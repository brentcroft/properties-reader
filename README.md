# properties-reader
JavaCC grammar and kit to generate SAX events describing a java properties file, and an XSL transform to regenerate the original file.


JavaCC File used to produce the parser:

 - [Properties grammar file](src/main/resources/jjt/properties.jjt)

 - [Properties EBNF file](src/main/resources/jjt/properties.html)

Xslt used to generate properties text files:

 - [Properties file builder](src/main/resources/xslt/properties.xslt)
