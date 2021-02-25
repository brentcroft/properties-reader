package com.brentcroft.pxr.model;

import com.brentcroft.pxr.ExpectationException;
import com.brentcroft.tools.materializer.Materializer;
import com.brentcroft.tools.materializer.core.*;
import com.brentcroft.tools.materializer.model.*;
import lombok.Getter;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Getter
public enum PxrPropertiesRootTag implements FlatTag< PxrProperties >
{
    COMMENT_REMOVE(
            "comment",
            ( tag ) -> EventMatcher
                    .getDefaultMatcher( tag.getTag() )
                    .andMatches( EventMatcher.getNamespaceMatcher( PxrItem.EXPECTED_NAMESPACE_URI ) ),
            ( pxrProperties, event ) -> event,
            ( pxrProperties, text, attr ) -> {

                String key = attr.getAttribute( "key" );
                String expected = attr.getAttribute( "_text", false );

                switch ( key )
                {
                    case "_header":

                        ExpectationException
                                .validateExpected(
                                        key,
                                        expected,
                                        ofNullable( pxrProperties.getHeader() )
                                                .map( PxrComment::getText )
                                                .orElse( null ) );


                        pxrProperties.setHeader( null );
                        break;

                    case "_footer":

                        ExpectationException
                                .validateExpected(
                                        key,
                                        expected,
                                        ofNullable( pxrProperties.getFooter() )
                                                .map( PxrComment::getText )
                                                .orElse( null ) );

                        pxrProperties.setFooter( null );
                        break;

                    default:

                        PxrEntry entry = ofNullable( pxrProperties.getEntryMap().get( key ) )
                                .orElseThrow( () -> new RuntimeException( "No entry for comment key: " + key ) );

                        ExpectationException
                                .validateExpected(
                                        key,
                                        expected,
                                        ofNullable( entry.getComment() )
                                                .map( PxrComment::getText )
                                                .orElse( null ) );

                        entry.setComment( null );
                }
            }
    ),


    COMMENT(
            "comment",
            ( tag ) -> EventMatcher
                    .getDefaultMatcher( tag.getTag() )
                    .andNotMatches( EventMatcher.getNamespaceMatcher( PxrItem.EXPECTED_NAMESPACE_URI ) ),
            ( pxrProperties, event ) -> event,
            ( pxrProperties, text, attr ) -> {

                PxrComment pxrComment = new PxrComment();

                attr.applyAttribute( "key", pxrComment::setKey );
                attr.applyAttribute( "eol", false, "1"::equals, pxrComment::setEol );
                attr.applyAttribute( "lines-before", false, Integer::parseInt, pxrComment::setLinesBefore );

                if ( nonNull( text ) )
                {
                    pxrComment.maybeAppend( text );
                }

                String key = pxrComment.getKey();
                String expected = attr.getAttribute( "_text", false );

                switch ( key )
                {
                    case "_header":

                        ExpectationException
                                .validateExpected(
                                        key,
                                        expected,
                                        ofNullable( pxrProperties.getHeader() )
                                                .map( PxrComment::getText )
                                                .orElse( null ) );

                        pxrProperties.setHeader( pxrComment );
                        break;

                    case "_footer":

                        ExpectationException
                                .validateExpected(
                                        key,
                                        expected,
                                        ofNullable( pxrProperties.getFooter() )
                                                .map( PxrComment::getText )
                                                .orElse( null ) );

                        pxrProperties.setFooter( pxrComment );
                        break;

                    default:

                        boolean exists = pxrProperties.getEntryMap().containsKey( key );

                        PxrEntry entry = exists
                                         ? pxrProperties.getEntryMap().get( key )
                                         : new PxrEntry();

                        ExpectationException
                                .validateExpected(
                                        key,
                                        expected,
                                        ofNullable( entry.getComment() )
                                                .map( PxrComment::getText )
                                                .orElse( null ) );

                        if ( ! exists )
                        {
                            entry.setKey( key );
                            pxrProperties.append( entry );
                        }

                        pxrComment.setEol( true );

                        entry.setComment( pxrComment );
                }
            }
    ),
    PROPERTIES(
            "*",
            ( tag ) -> EventMatcher.getDefaultMatcher( tag.getTag() ),
            ( pxrProperties, event ) -> event,
            ( pxrProperties, text, event ) -> {

                // ensure there are two line breaks after any header
                ofNullable( pxrProperties.getHeader() )
                        .ifPresent( header -> {
                            header.setEol( true );
                            if ( pxrProperties.getEntries().size() > 0 )
                            {
                                PxrEntry entry = pxrProperties.getEntries().get( 0 );
                                if ( isNull( entry.getComment() ) )
                                {
                                    PxrComment spacer = new PxrComment();
                                    spacer.setKey( entry.getKey() );
                                    spacer.setLinesBefore( 1 );
                                    entry.setComment( spacer );
                                }
                                else if ( entry.getComment().getLinesBefore() < 1 )
                                {
                                    entry.getComment().setLinesBefore( 1 );
                                }
                            }
                        } );

                // ensure footer has two line breaks preceding
                ofNullable( pxrProperties.getFooter() )
                        .ifPresent( footer -> {
                            if ( pxrProperties.getEntries().size() > 0 && footer.getLinesBefore() < 2 )
                            {
                                footer.setLinesBefore( 1 );
                                pxrProperties.getEntries().get( pxrProperties.getEntries().size() - 1 ).setEol( true );
                            }
                            else if ( footer.getLinesBefore() < 1 )
                            {
                                footer.setLinesBefore( 1 );
                            }
                        } );
            },
            EntryTag.ENTRY_REMOVE,
            EntryTag.ENTRY,
            COMMENT_REMOVE,
            COMMENT
    ),
    ROOT( "", PxrJumpTag.PROPERTIES, PROPERTIES );

    private final String tag;
    private final boolean multiple;
    private final boolean choice;
    private final EventMatcher elementMatcher;
    private final FlatCacheOpener< PxrProperties, OpenEvent, ? > opener;
    private final FlatCacheCloser< PxrProperties, String, ? > closer;
    private final Tag< ? super PxrProperties, ? >[] children;

    @SafeVarargs
    PxrPropertiesRootTag( String tag, Tag< ? super PxrProperties, ? >... children )
    {
        this( tag, null, null, null, children );
    }

    @SafeVarargs
    PxrPropertiesRootTag(
            String tag,
            Function< Tag< ?, ? >, EventMatcher > elementMatcher,
            BiFunction< PxrProperties, OpenEvent, AttributesMap > opener,
            TriConsumer< PxrProperties, String, AttributesMap > closer,
            Tag< ? super PxrProperties, ? >... children
    )
    {
        this.tag = tag;
        this.multiple = isNull( children ) || children.length == 0;
        this.elementMatcher = isNull( elementMatcher )
                              ? EventMatcher.getDefaultMatcher( getTag() )
                              : elementMatcher.apply( this );
        this.opener = Opener.flatCacheOpener( opener );
        this.closer = Closer.flatCacheCloser( closer );
        this.choice = nonNull( children ) && children.length > 0;
        this.children = children;
    }
}

@Getter
enum EntryTag implements StepTag< PxrProperties, PxrEntry >
{
    ENTRY_REMOVE(
            "entry",
            ( tag ) -> EventMatcher
                    .getDefaultMatcher( tag.getTag() )
                    .andMatches( EventMatcher.getNamespaceMatcher( PxrItem.EXPECTED_NAMESPACE_URI ) ),
            ( pxrProperties, pxrEntry, event ) -> event,
            ( pxrProperties, pxrEntry, text, attr ) -> {

                String key = attr.getAttribute( "key" );
                String expected = attr.getAttribute( "_text", false );
                ExpectationException.validateExpected( key, expected, pxrEntry.getText() );

                pxrProperties.remove( key );
            }
    ),

    ENTRY(
            "entry",

            ( tag ) -> EventMatcher
                    .getDefaultMatcher( tag.getTag() )
                    .andNotMatches( EventMatcher.getNamespaceMatcher( PxrItem.EXPECTED_NAMESPACE_URI ) ),

            ( pxrProperties, pxrEntry, event ) -> {
                event.putContextValue( "$value", pxrEntry.getText() );
                return event;
            },

            ( pxrProperties, pxrEntry, text, event ) -> {

                String key = event.getAttribute( "key" );
                String expected = event.getAttribute( "_text", false );
                ExpectationException.validateExpected( key, expected, pxrEntry.getText() );

                if ( nonNull( text ) && text.length() > 0 && text.trim().length() > 0 )
                {
                    pxrEntry.setValue( text );
                    pxrEntry.setContinuations( null );
                }
                else if ( nonNull( pxrEntry.getContinuations() ) )
                {
                    pxrEntry.setValue( null );
                }
            },

            TextTag.TEXT );

    private final String tag;
    private final boolean multiple;
    private final boolean choice;
    private final EventMatcher elementMatcher;
    private final Opener< PxrProperties, PxrEntry, OpenEvent, AttributesMap > opener;
    private final Closer< PxrProperties, PxrEntry, String, AttributesMap > closer;
    private final Tag< ? super PxrEntry, ? >[] children;


    @SafeVarargs
    EntryTag( String tag,
              Function< Tag< PxrProperties, PxrEntry >, EventMatcher > elementMatcher,
              Opener< PxrProperties, PxrEntry, OpenEvent, AttributesMap > opener,
              Closer< PxrProperties, PxrEntry, String, AttributesMap > closer,
              Tag< ? super PxrEntry, ? >... children )
    {
        this.tag = tag;
        this.elementMatcher = elementMatcher.apply( this );
        this.multiple = isNull( children ) || children.length == 0;
        this.opener = opener;
        this.closer = closer;
        this.choice = nonNull( children ) && children.length > 0;
        this.children = children;
    }

    @Override
    public PxrEntry getItem( PxrProperties pxrProperties, OpenEvent event )
    {
        String key = event.getAttribute( "key" );

        boolean exists = pxrProperties
                .getEntryMap()
                .containsKey( key );

        PxrEntry pxrEntry = exists
                            ? pxrProperties.getEntryMap().get( key )
                            : new PxrEntry();

        event.applyAttribute( "key", pxrEntry::setKey );
        event.applyAttribute( "eol", false, true, "1"::equals, pxrEntry::setEol );
        event.applyAttribute(
                "index",
                exists ? pxrEntry.getIndex() : pxrProperties.getEntries().size(),
                Integer::parseInt,
                pxrEntry::setIndex );

        event.applyAttribute( "sep", "=", pxrEntry::setSep );

        if ( ! exists )
        {
            pxrProperties.append( pxrEntry );
        }

        pxrProperties.reIndex();

        return pxrEntry;
    }
}


@Getter
enum TextTag implements FlatTag< PxrEntry >
{
    TEXT(
            "text",

            ( pxrEntry, event ) -> event,
            ( pxrEntry, text, attr ) -> {

                PxrEntryContinuation pec = new PxrEntryContinuation();

                attr.applyAttribute( "prefix", false, pec::setPrefix );
                attr.applyAttribute( "eol", false, "1"::equals, pec::setEol );
                attr.applyAttribute( "cont", false, pec::setCont );

                pec.setValue( text );

                if ( isNull( pxrEntry.getContinuations() ) )
                {
                    pxrEntry.setContinuations( new ArrayList<>() );
                    pxrEntry.setValue( null );
                }

                attr.applyAttribute( "key", pxrEntry.getContinuations().size(), pec::setIndex );

                pxrEntry.getContinuations().add( pec );
            }
    );

    private final String tag;
    private final boolean multiple = true;
    private final FlatCacheOpener< PxrEntry, OpenEvent, ? > opener;
    private final FlatCacheCloser< PxrEntry, String, ? > closer;

    TextTag( String tag,
             BiFunction< PxrEntry, OpenEvent, AttributesMap > opener,
             TriConsumer< PxrEntry, String, AttributesMap > closer )
    {
        this.tag = tag;
        this.opener = Opener.flatCacheOpener( opener );
        this.closer = Closer.flatCacheCloser( closer );
    }
}


@Getter
enum PxrJumpTag implements JumpTag< PxrProperties, PxrProperties >
{
    PROPERTIES(
            "properties",
            ( pxr, event ) -> event
                    .onHasAttributeAnd(
                            "src",
                            () -> event.notOnStack( PxrPropertiesRootTag.ROOT ),
                            () -> new Materializer<>(
                                    () -> PxrPropertiesRootTag.ROOT,
                                    () -> pxr,
                                    event.stackInContext( PxrPropertiesRootTag.ROOT ) )
                                    .apply( getInputSource( pxr, event ) ) ),
            PxrPropertiesRootTag.PROPERTIES
    );

    private final String tag;
    private final FlatOpener< PxrProperties, OpenEvent > opener;
    private final Tag< ? super PxrProperties, ? >[] children;

    @SafeVarargs
    PxrJumpTag(
            String tag,
            BiConsumer< PxrProperties, OpenEvent > opener,
            Tag< ? super PxrProperties, ? >... children )
    {
        this.tag = tag;
        this.opener = Opener.flatOpener( opener );
        this.children = children;
    }

    @Override
    public PxrProperties getItem( PxrProperties topology, OpenEvent event )
    {
        return topology;
    }

    static InputSource getInputSource( PxrProperties pxr, OpenEvent event )
    {
        try
        {
            File directory = isNull( pxr.getSystemId() )
                             ? null
                             : new File( pxr.getSystemId() ).getParentFile();

            String filename = event.getAttribute( "src" );

            return new InputSource(
                    new FileInputStream(
                            new File( directory, filename ) ) );
        }
        catch ( FileNotFoundException e )
        {
            throw new RuntimeException( e );
        }
    }
}
