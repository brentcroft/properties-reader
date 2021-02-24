package com.brentcroft.pxr;

import com.brentcroft.pxr.model.*;
import com.brentcroft.pxr.parser.ParseException;
import com.brentcroft.tools.materializer.ContextValue;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


/**
 * Handles a stream of SAX (Update) Events by creating or updating a PxrProperties Object.
 * <p>
 * Elements are assumed to be additions, or confirmations, but if the "act" prefix is detected the following applies:
 *
 * <ul>
 *     <li>Delete an entry: &lt;act:entry key="abc"/&gt;</li>
 *     <li>Delete a comment: &lt;act:comment key="abc"/&gt;</li>
 * </ul>
 *
 * <ul>
 *     <li>Add/update an entry: &lt;entry key="abc"&gt;xyz&lt;/entry&gt;</li>
 *     <li>Add/update an entry, but only if the existing value is expected:
 *     <br/>
 *     &lt;entry key="abc" act:_text="pqr"&gt;xyz&lt;/entry&gt;</li>
 * </ul>
 */
public class ExPxrWriter extends DefaultHandler implements PxrItem
{
    @Setter
    @Getter
    private Locator documentLocator;

    @Setter
    @Getter
    private ContextValue contextValue;

    @Setter
    @Getter
    private PxrProperties pxrProperties;

    @Setter
    @Getter
    private InputStream inputStream;

    @Setter
    @Getter
    private String encoding = "UTF-8";


    // comments typically occur before their parent entry
    // and so can't be bound until all entries are read
    private final List< PxrComment > forwardComments = new ArrayList<>();
    private final List< PxrEntryContinuation > pxrEntryContinuations = new ArrayList<>();

    private final StringBuilder entryText = new StringBuilder();
    private final StringBuilder continuationText = new StringBuilder();

    private String tag;
    private String sep;
    private String cont;
    private String prefix;
    private String entryKey;
    private String expected;
    private int linesBefore;
    private boolean eol = false;
    private PxrEntry entryForUpdate;


    @Override
    public void startDocument() throws SAXException
    {
        if ( nonNull( getInputStream() ) )
        {
            try
            {
                pxrProperties = PxrUtils.getPxrProperties( getInputStream(), encoding );
            }
            catch ( ParseException e )
            {
                throw new SAXException( e );
            }
        }
        else if ( isNull( pxrProperties ) )
        {
            pxrProperties = new PxrProperties();
        }

        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException
    {
        for ( PxrComment comment : forwardComments )
        {
            PxrEntry entry = pxrProperties.getEntryMap().get( comment.getKey() );

            if ( isNull( entry ) )
            {
                throw new SAXParseException( "Orphan comment; no entry with key: " + comment.getKey(), getDocumentLocator() );
            }

            if ( isNull( entry.getComment() ) )
            {
                entry.setComment( comment );
            }
            else
            {
                PxrComment entryComment = entry.getComment();

                entryComment.getValue().setLength( 0 );
                entryComment.getValue().append( comment.getValue() );
            }
        }

        super.endDocument();
    }


    @Override
    public void startElement( String uri, String localName, String qName, Attributes attributes )
    {
        if ( isUpdatableTag( qName ) )
        {
            tag = qName;

            if ( ! TAG.TEXT.isTag( qName ) )
            {
                entryKey = ATTR.KEY.getAttribute( attributes );
            }

            expected = ATTR.EXPECTED_TEXT.getAttribute( attributes );

            String linesBeforeText = ATTR.LINES_BEFORE.getAttribute( attributes );

            linesBefore = isNull( linesBeforeText )
                          ? 0
                          : Integer.parseInt( linesBeforeText );

            String eolText = ATTR.EOL.getAttribute( attributes );

            // eol is true unless explicitly set to false
            eol = ( isNull( eolText ) || eolText.trim().length() == 0 ) || Boolean.parseBoolean( eolText );

            sep = ATTR.SEP.hasAttribute( attributes ) ? ATTR.SEP.getAttribute( attributes ) : "=";

            cont = ATTR.CONT.getAttribute( attributes );
            prefix = ATTR.PREFIX.getAttribute( attributes );

            if ( TAG.ENTRY.isTag( qName ) || TAG.COMMENT.isTag( qName ) )
            {
                entryText.setLength( 0 );
                pxrEntryContinuations.clear();
            }
            else if ( TAG.TEXT.isTag( qName ) )
            {
                continuationText.setLength( 0 );
            }
        }
    }

    @Override
    public void characters( char[] ch, int start, int length )
    {
        if ( ( TAG.ENTRY.isTag( tag )
                || TAG.COMMENT.isTag( tag ) ) )
        {
            entryText.append( ch, start, length );
        }
        else if ( TAG.TEXT.isTag( tag ) )
        {
            continuationText.append( ch, start, length );
        }
    }


    @Override
    public void endElement( String uri, String localName, String qName ) throws SAXParseException
    {
        if ( isUpdatableTag( qName ) )
        {
            processUpdate( qName, entryKey );
        }
    }


    private void processUpdate( String tag, String key ) throws SAXParseException
    {
        if ( ! isUpdatableTag( tag ) )
        {
            return;
        }

        if ( TAG.TEXT.isTag( tag ) )
        {
            PxrEntryContinuation pec = new PxrEntryContinuation();
            pec.setIndex( pxrEntryContinuations.size() );
            pec.setCont( cont );
            pec.setPrefix( prefix );
            pec.setEol( eol );

            pec.setValue( nonNull( contextValue )
                          ? contextValue
                                  .inContext()
                                  .put( "$index", pec.getIndex() )
                                  .map( key, continuationText.toString() )
                          : continuationText.toString() );
            continuationText.setLength( 0 );

            pxrEntryContinuations.add( pec );

            return;
        }
        else if ( key == null )
        {
            throw new SAXParseException(
                    format( "Element <%s> has empty or missing attribute @key", tag ), getDocumentLocator() );
        }

        entryForUpdate = pxrProperties.getEntryMap().get( key );

        final String actualValue = getActualValue( key );

        ACT action = TAG.DELETE_ENTRY.isTag( tag ) || TAG.DELETE_COMMENT.isTag( tag )
                     ? ACT.DELETE
                     : nonNull( entryForUpdate )
                       ? ACT.UPDATE
                       : ACT.INSERT;

        final String newValue = mapKeyValue( key, getNewValue(), actualValue );

        switch ( action )
        {
            case INSERT:

                insertAtKey( tag, key, newValue );
                break;

            case UPDATE:

                checkExpected( action, key, expected, actualValue );
                updateAtKey( tag, key, newValue, actualValue );
                break;

            case DELETE:

                checkExpected( action, key, expected, actualValue );
                deleteAtKey( tag, key );
                break;
        }
    }

    private String getActualValue( String key )
    {
        return nonNull( entryForUpdate )

               ? TAG.ENTRY.isTag( tag ) || TAG.DELETE_ENTRY.isTag( tag )

                 // the current entry value
                 ? entryForUpdate.getValue()

                 : ( TAG.COMMENT.isTag( tag ) || TAG.DELETE_COMMENT.isTag( tag ) ) && nonNull( entryForUpdate.getComment() )

                   // the current entry comment value
                   ? entryForUpdate.getComment().getText()

                   : null

               : ( TAG.COMMENT.isTag( tag ) || TAG.DELETE_COMMENT.isTag( tag ) ) && isHeaderOrFooterKey( key )

                 ? HEADER_KEY.equals( key )

                   ? nonNull( pxrProperties.getHeader() )

                     // the header value
                     ? pxrProperties.getHeader().getText()
                     : null

                   : nonNull( pxrProperties.getFooter() )

                     // the header value
                     ? pxrProperties.getFooter().getText()
                     : null
                 : null;
    }


    private String getNewValue()
    {
        PxrEntry pxrEntry = new PxrEntry();
        pxrEntry.setContinuations( pxrEntryContinuations );
        pxrEntry.setValue( entryText.toString() );
        return pxrEntry.getText();
    }

    private void setEntryText( PxrEntry entry, String newValue )
    {
        // overwriting any previous text contributors
        if ( pxrEntryContinuations.size() > 0 )
        {
            entry.setValue( null );
            entry.setContinuations( new ArrayList<>( pxrEntryContinuations ) );
        }
        else
        {
            entry.setContinuations( null );
            entry.setValue( newValue );
        }

        entryText.setLength( 0 );
        pxrEntryContinuations.clear();
    }

    private void insertAtKey( String tag, String key, String newValue ) throws SAXParseException
    {
        if ( TAG.ENTRY.isTag( tag ) )
        {
            if ( isHeaderOrFooterKey( key ) )
            {
                throw new SAXParseException( format( "An entry cannot have a key of: '%s' or '%s'", HEADER_KEY, FOOTER_KEY ), getDocumentLocator() );
            }

            PxrComment comment = null;

            if ( linesBefore > 0 )
            {
                comment = new PxrComment();
                comment.setLinesBefore( 1 );
                comment.setKey( key );
                comment.setEol( eol );
            }

            PxrEntry entry = new PxrEntry();

            entry.setComment( comment );
            entry.setIndex( pxrProperties.getEntries().size() );
            entry.setKey( key );
            entry.setSep( sep );
            entry.setEol( eol );

            setEntryText( entry, newValue );

            pxrProperties.append( entry );
        }
        else if ( TAG.COMMENT.isTag( tag ) )
        {
            if ( HEADER_KEY.equals( key ) )
            {
                PxrComment comment = new PxrComment();
                comment.setLinesBefore( linesBefore );
                comment.setKey( HEADER_KEY );

                entryText.setLength( 0 );

                // ensure header creates two line breaks
                if ( ! newValue.endsWith( "\n" ) )
                {
                    newValue += "\n";
                }

                comment.ingest( null, null, newValue, true );

                pxrProperties.setHeader( comment );
            }
            else if ( FOOTER_KEY.equals( key ) )
            {
                PxrComment comment = new PxrComment();

                comment.setLinesBefore( linesBefore );
                comment.setKey( FOOTER_KEY );

                if ( ! pxrProperties.endsWithEol() )
                {
                    pxrProperties.appendEol();
                }

                entryText.setLength( 0 );

                comment.ingest( null, null, newValue, eol );

                pxrProperties.setFooter( comment );
            }
            else
            {
                PxrComment comment = new PxrComment();

                comment.setLinesBefore( linesBefore );
                comment.setKey( key );

                entryText.setLength( 0 );

                comment.ingest( null, null, newValue, eol );

                forwardComments.add( comment );
            }
        }
    }


    private String mapKeyValue( String key, String newValue, String oldValue )
    {
        return nonNull( contextValue )
               ? contextValue
                       .inContext()
                       .put( "$value", oldValue )
                       .map( key, newValue )
               : newValue;
    }


    private void updateAtKey( String tag, String key, String newValue, String oldValue ) throws SAXParseException
    {
        // ignore if no change
        if ( ! newValue.equals( oldValue ) )
        {
            if ( TAG.ENTRY.isTag( tag ) )
            {
                if ( isNull( entryForUpdate ) )
                {
                    PxrEntry entry = new PxrEntry();

                    entry.setIndex( pxrProperties.getEntries().size() );
                    entry.setKey( key );
                    entry.setSep( sep );
                    entry.setEol( eol );

                    setEntryText( entry, newValue );

                    pxrProperties.append( entry );
                }
                else
                {
                    setEntryText( entryForUpdate, newValue );
                }
            }
            else if ( TAG.COMMENT.isTag( tag ) )
            {
                if ( isNull( entryForUpdate ) )
                {
                    throw new SAXParseException( "No property to update with key: " + key, getDocumentLocator() );
                }
                else if ( isNull( entryForUpdate.getComment() ) )
                {
                    entryForUpdate.setValue( newValue );
                }
                else
                {
                    PxrComment comment = entryForUpdate.getComment();
                    comment.getValue().setLength( 0 );
                    comment.getValue().append( newValue );
                }
            }
        }
    }


    private void deleteAtKey( String tag, String key )
    {
        if ( TAG.DELETE_ENTRY.isTag( tag ) )
        {
            pxrProperties.remove( key );
        }
        else if ( TAG.DELETE_COMMENT.isTag( tag ) )
        {
            if ( HEADER_KEY.equals( key ) )
            {
                pxrProperties.setHeader( null );
            }
            else if ( FOOTER_KEY.equals( key ) )
            {
                pxrProperties.setFooter( null );
            }
            else if ( nonNull( entryForUpdate ) )
            {
                entryForUpdate.setComment( null );
            }
        }
    }

    public void checkExpected( ACT action, String key, String expected, String actual ) throws ACTException
    {
        if ( expected != null && ! expected.equals( actual ) )
        {
            throw new ACTException( action, key, expected, actual, getDocumentLocator() );
        }
    }

    public static boolean isHeaderOrFooterKey( String key )
    {
        return HEADER_KEY.equals( key ) || FOOTER_KEY.equals( key );
    }


    public static boolean isUpdatableTag( String tag )
    {
        return ( TAG.ENTRY.isTag( tag )
                || TAG.COMMENT.isTag( tag )
                || TAG.DELETE_ENTRY.isTag( tag )
                || TAG.DELETE_COMMENT.isTag( tag )
                || TAG.TEXT.isTag( tag )
        );
    }
}
