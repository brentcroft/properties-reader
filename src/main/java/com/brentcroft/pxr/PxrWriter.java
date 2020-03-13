package com.brentcroft.pxr;

import com.brentcroft.pxr.model.PxrComment;
import com.brentcroft.pxr.model.PxrEntry;
import com.brentcroft.pxr.model.PxrItem;
import com.brentcroft.pxr.model.PxrProperties;
import com.brentcroft.pxr.parser.ParseException;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.brentcroft.pxr.PxrUtils.isNull;
import static com.brentcroft.pxr.PxrUtils.nonNull;
import static java.lang.String.format;


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
public class PxrWriter extends DefaultHandler implements PxrItem
{
    @Setter
    @Getter
    private ContextValueMapper contextValueMapper;

    @Getter
    private PxrProperties pxrProperties;

    @Setter
    @Getter
    private InputStream inputStream;


    // comments typically occur before their parent entry
    // and so can't be bound until all entries are read
    private List< PxrComment > forwardComments = new ArrayList< PxrComment >();
    private StringBuilder stringBuilder = new StringBuilder();
    private String tag;
    private String key;
    private String expected;
    private int linesBefore;
    private boolean eol = false;


    @Override
    public void startDocument() throws SAXException
    {
        if ( nonNull( getInputStream() ) )
        {
            try
            {
                pxrProperties = PxrUtils.getPxrProperties( getInputStream() );
            }
            catch ( ParseException e )
            {
                throw new SAXException( e );
            }
        }
        else
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
                throw new RuntimeException( "Orphan comment; no entry with key: " + comment.getKey() );
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
        if ( ( TAG.ENTRY.isTag( qName )
                || TAG.COMMENT.isTag( qName )
                || TAG.DELETE_ENTRY.isTag( qName )
                || TAG.DELETE_COMMENT.isTag( qName ) ) )
        {
            tag = qName;
            key = ATTR.KEY.getAttribute( attributes );
            expected = ATTR.EXPECTED.getAttribute( attributes );

            String linesBeforeText = ATTR.LINES_BEFORE.getAttribute( attributes );

            linesBefore = isNull( linesBeforeText )
                          ? 0
                          : Integer.parseInt( linesBeforeText );

            String eolText = PxrItem.ATTR.EOL.getAttribute( attributes );

            eol = ( isNull( eolText ) || eolText.trim().length() == 0 ) || Boolean.parseBoolean( eolText );

            stringBuilder.setLength( 0 );
        }
    }

    @Override
    public void characters( char[] ch, int start, int length )
    {
        if ( ( TAG.ENTRY.isTag( tag )
                || TAG.COMMENT.isTag( tag ) ) )
        {
            stringBuilder.append( ch, start, length );
        }
    }


    @Override
    public void endElement( String uri, String localName, String qName )
    {
        if ( ( TAG.ENTRY.isTag( qName )
                || TAG.COMMENT.isTag( qName )
                || TAG.DELETE_ENTRY.isTag( qName )
                || TAG.DELETE_COMMENT.isTag( qName ) ) )
        {
            processUpdate( stringBuilder.toString() );

            stringBuilder.setLength( 0 );
        }
    }


    private void processUpdate( String newCharacters )
    {
        if ( ! ( TAG.ENTRY.isTag( tag )
                || TAG.COMMENT.isTag( tag )
                || TAG.DELETE_ENTRY.isTag( tag )
                || TAG.DELETE_COMMENT.isTag( tag ) ) )
        {
            return;
        }

        if ( key == null )
        {
            throw new RuntimeException(
                    format( "Element <%s> has empty or missing attribute @key", tag ) );
        }

        PxrEntry entry = pxrProperties.getEntryMap().get( key );

        final String actual = TAG.ENTRY.isTag( tag ) && nonNull( entry )
                              ? entry.getValue()
                              : ( TAG.COMMENT.isTag( tag ) && nonNull( pxrProperties.getComment( key ) ) )
                                ? pxrProperties.getComment( key ).getText()
                                : null;

        final String newValue = isNull( contextValueMapper )
                                ? newCharacters
                                : contextValueMapper
                                        .inContext()
                                        .put( "$value", actual )
                                        .map( key, newCharacters );


        // might switch action from update to confirm later!
        final ACT[] action = {
                TAG.DELETE_ENTRY.isTag( tag ) || TAG.DELETE_COMMENT.isTag( tag )
                ? ACT.DELETE
                : pxrProperties.getEntryMap().containsKey( key )
                  ? ACT.UPDATE
                  : ACT.INSERT
        };


        switch ( action[ 0 ] )
        {
            case INSERT:

                insertAtKey( pxrProperties, tag, key, newValue, linesBefore, eol );

                break;


            case UPDATE:

                if ( expected != null && ! expected.equals( actual ) )
                {
                    handleError( action[ 0 ], key, expected, actual );
                }
                else if ( newValue.equals( actual ) )
                {
                    // switch action
                    action[ 0 ] = ACT.CONFIRM;
                }
                else
                {
                    // not the exp:namespace
                    if ( TAG.ENTRY.isTag( tag ) )
                    {
                        PxrEntry entryForUpdate = pxrProperties.getEntryMap().get( key );

                        if ( isNull( entryForUpdate ) )
                        {
                            pxrProperties.append( new PxrEntry(
                                    null,
                                    pxrProperties.getEntries().size(),
                                    key,
                                    "=",
                                    newValue,
                                    true,
                                    null ) );
                        }
                        else
                        {
                            entryForUpdate.setValue( newValue );
                        }

                    }
                    else if ( TAG.COMMENT.isTag( tag ) )
                    {
                        PxrEntry entryForUpdate = pxrProperties.getEntryMap().get( key );

                        if ( isNull( entryForUpdate ) )
                        {
                            // not found
                            throw new RuntimeException( "No property to update with key: " + key );
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

                break;

            case DELETE:

                // 2014-10-21: allow if expected is null
                // for delete, the "newValue" is expected
                // unless its a sensitive key
                if ( expected != null && ! newValue.equals( actual ) )
                {
                    handleError( action[ 0 ], key, newValue, actual );
                }

                deleteAtKey( pxrProperties, tag, key );

                break;
        }
    }


    private void insertAtKey( PxrProperties properties, String tag, String key, String newValue, int linesBefore, boolean eol )
    {
        if ( TAG.ENTRY.isTag( tag ) )
        {
            if ( isHeaderOrFooterKey( key ) )
            {
                throw new IllegalStateException( format( "An entry cannot have a key of: '%s' or '%s'", HEADER_KEY, FOOTER_KEY ) );
            }

            PxrComment comment = null;

            if ( linesBefore > 0 )
            {
                comment = new PxrComment(
                        linesBefore - 1,
                        key,
                        eol,
                        false
                );
            }

            PxrEntry entry = new PxrEntry(
                    comment,
                    properties.getEntries().size(),
                    key,
                    "=",
                    newValue,
                    eol,
                    null
            );

            properties.append( entry );
        }
        else if ( TAG.COMMENT.isTag( tag ) )
        {
            if ( HEADER_KEY.equals( key ) )
            {
                PxrComment comment = null;

                if ( linesBefore > 0 )
                {
                    comment = new PxrComment(
                            linesBefore,
                            key,
                            eol,
                            false
                    );
                }

                properties.setHeader( comment );
            }
            else if ( FOOTER_KEY.equals( key ) )
            {
                PxrComment comment = null;

                if ( linesBefore > 0 )
                {
                    comment = new PxrComment(
                            linesBefore,
                            key,
                            eol,
                            false
                    );
                }

                properties.setFooter( comment );
            }
            else
            {
                PxrComment comment = new PxrComment( linesBefore, key, false, false );

                comment.ingest( null, null, newValue, eol );

                forwardComments.add( comment );
            }
        }
    }


    private void deleteAtKey( PxrProperties properties, String tag, String key )
    {
        if ( TAG.DELETE_ENTRY.isTag( tag ) )
        {
            properties.remove( key );
        }
        else if ( TAG.DELETE_COMMENT.isTag( tag ) )
        {
            if ( HEADER_KEY.equals( key ) )
            {
                properties.setHeader( null );
            }
            else if ( FOOTER_KEY.equals( key ) )
            {
                properties.setFooter( null );
            }
            else
            {
                PxrEntry entry = properties.getEntryMap().get( key );

                if ( nonNull( entry ) )
                {
                    entry.setComment( null );
                }
            }
        }
    }


    public static boolean isHeaderOrFooterKey( String key )
    {
        return HEADER_KEY.equals( key ) || FOOTER_KEY.equals( key );
    }

    private static void handleError( ACT action, String key, String expected, String actual )
    {
        throw new ACTException( action, key, expected, actual );
    }
}
