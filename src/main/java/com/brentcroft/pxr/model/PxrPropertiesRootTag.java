package com.brentcroft.pxr.model;

import com.brentcroft.tools.materializer.core.*;
import lombok.Getter;

import java.util.ArrayList;
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
            ( tag ) -> ElementMatcher
                    .getDefaultMatcher( tag.getTag() )
                    .andMatches( ElementMatcher.getNamespaceMatcher( PxrItem.EXPECTED_NAMESPACE_URI ) ),
            ( pxrProperties, event ) -> event.getAttributesMap(),
            ( pxrProperties, text, attr ) -> {

                String key = attr.getAttribute( "key" );

                switch ( key )
                {
                    case "_header":
                        pxrProperties.setHeader( null );
                        break;

                    case "_footer":
                        pxrProperties.setFooter( null );
                        break;

                    default:
                        ofNullable( pxrProperties.getEntryMap().get( key ) )
                                .orElseThrow( () -> new RuntimeException( "No entry for comment key: " + key ) )
                                .setComment( null );
                }
            }
    ),


    COMMENT(
            "comment",
            ( tag ) -> ElementMatcher
                    .getDefaultMatcher( tag.getTag() )
                    .andNotMatches( ElementMatcher.getNamespaceMatcher( PxrItem.EXPECTED_NAMESPACE_URI ) ),
            ( pxrProperties, event ) -> event.getAttributesMap(),
            ( pxrProperties, text, attr ) -> {

                PxrComment pxrComment = new PxrComment();

                attr.applyAttribute( "key", pxrComment::setKey );
                attr.applyAttribute( "eol", false,"1"::equals, pxrComment::setEol );
                attr.applyAttribute( "lines-before", false, Integer::parseInt, pxrComment::setLinesBefore );

                if ( nonNull( text ) )
                {
                    pxrComment.maybeAppend( text );
                }

                String key = pxrComment.getKey();

                switch ( key )
                {
                    case "_header":
                        pxrProperties.setHeader( pxrComment );
                        break;

                    case "_footer":
                        pxrProperties.setFooter( pxrComment );
                        break;

                    default:

                        boolean exists = pxrProperties.getEntryMap().containsKey( key );

                        PxrEntry entry = exists
                                         ? pxrProperties.getEntryMap().get( key )
                                         : new PxrEntry();

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
            ( tag ) -> ElementMatcher.getDefaultMatcher( tag.getTag() ),
            ( pxrProperties, event ) -> event.getAttributesMap(),
            ( pxrProperties, text, attr ) -> {

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
    ROOT( "", PROPERTIES );

    private final String tag;
    private final FlatTag< PxrProperties > self = this;
    private final boolean multiple;
    private final boolean choice;
    private final ElementMatcher elementMatcher;
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
            Function< Tag< ?, ? >, ElementMatcher > elementMatcher,
            BiFunction< PxrProperties, OpenEvent, AttributesMap > opener,
            TriConsumer< PxrProperties, String, AttributesMap > closer,
            Tag< ? super PxrProperties, ? >... children
    )
    {
        this.tag = tag;
        this.multiple = isNull( children ) || children.length == 0;
        this.elementMatcher = isNull( elementMatcher )
                              ? ElementMatcher.getDefaultMatcher( getTag() )
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
            ( tag ) -> ElementMatcher
                    .getDefaultMatcher( tag.getTag() )
                    .andMatches( ElementMatcher.getNamespaceMatcher( PxrItem.EXPECTED_NAMESPACE_URI ) ),
            ( pxrProperties, pxrEntry, event ) -> event.getAttributesMap(),
            ( pxrProperties, pxrEntry, text, attr ) -> pxrProperties.remove( attr.getAttribute( "key" ) )
    ),

    ENTRY(
            "entry",
            ( tag ) -> ElementMatcher
                    .getDefaultMatcher( tag.getTag() )
                    .andNotMatches( ElementMatcher.getNamespaceMatcher( PxrItem.EXPECTED_NAMESPACE_URI ) ),
            ( pxrProperties, pxrEntry, event ) -> event.getAttributesMap(),
            ( pxrProperties, pxrEntry, text, attr ) -> {

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
    private final StepTag< PxrProperties, PxrEntry > self = this;
    private final boolean multiple;
    private final boolean choice;
    private final ElementMatcher elementMatcher;
    private final Opener< PxrProperties, PxrEntry, OpenEvent, AttributesMap > opener;
    private final Closer< PxrProperties, PxrEntry, String, AttributesMap > closer;
    private final Tag< ? super PxrEntry, ? >[] children;


    @SafeVarargs
    EntryTag( String tag,
              Function< Tag< PxrProperties, PxrEntry >, ElementMatcher > elementMatcher,
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
        AttributesMap attr = event.getAttributesMap();

        String key = attr.getAttribute( "key" );

        boolean exists = pxrProperties
                .getEntryMap()
                .containsKey( key );

        PxrEntry pxrEntry = exists
                            ? pxrProperties.getEntryMap().get( key )
                            : new PxrEntry();

        attr.applyAttribute( "key", pxrEntry::setKey );
        attr.applyAttribute( "eol", false, true, "1"::equals, pxrEntry::setEol );
        attr.applyAttribute( "index", pxrProperties.getEntries().size(), Integer::parseInt, pxrEntry::setIndex );
        attr.applyAttribute( "sep", "=", pxrEntry::setSep );

        if ( ! exists )
        {
            pxrProperties.append( pxrEntry );
        }

        return pxrEntry;
    }
}


@Getter
enum TextTag implements FlatTag< PxrEntry >
{
    TEXT(
            "text",

            ( pxrEntry, event ) -> event.getAttributesMap(),
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
    private final FlatTag< PxrEntry > self = this;
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