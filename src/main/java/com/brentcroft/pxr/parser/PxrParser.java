/* PxrParser.java */
/* Generated By:JavaCC: Do not edit this line. PxrParser.java */
package com.brentcroft.pxr.parser;

import static com.brentcroft.pxr.PxrUtils.nonNull;
import static com.brentcroft.pxr.PxrUtils.isNull;

import com.brentcroft.pxr.model.PxrComment;
import com.brentcroft.pxr.model.PxrEntry;
import com.brentcroft.pxr.model.PxrEntryContinuation;
import com.brentcroft.pxr.model.PxrProperties;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class PxrParser implements PxrParserConstants {

  final public PxrProperties parse() throws ParseException {String space = null;
    String init = null;
    String text = null;
    boolean eol = false;
    String key = null;
    String sep = null;
    PxrComment comment = null;
    List<PxrEntryContinuation> cont = null;
    PxrProperties rp = new PxrProperties();
    int entryIndex = 1;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case LF:
    case CR:
    case SPACE:
    case BANG:
    case HASH:
    case CHAR:{
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case LF:
        case CR:{
          eol = eol();
          break;
          }
        case SPACE:{
          space = space();
          break;
          }
        case BANG:
        case HASH:{
          init = initiator();
          if (jj_2_1(2)) {
            text = text();
          } else {
            ;
          }
          if (jj_2_2(2)) {
            eol = eol();
          } else {
            ;
          }
          break;
          }
        case CHAR:{
          key = key();
          if (jj_2_4(2)) {
            sep = sep();
            if (jj_2_3(2)) {
              text = text();
              switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
              case CONT:{
                cont = continuations();
                break;
                }
              default:
                jj_la1[0] = jj_gen;
                ;
              }
            } else {
              ;
            }
          } else {
            ;
          }
          if (jj_2_5(2)) {
            eol = eol();
          } else {
            ;
          }
          break;
          }
        default:
          jj_la1[1] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
if ( isNull( key ) )
                {
                    if (isNull( comment ))
                    {
                        comment = new PxrComment( 0, "_footer", false );
                    }
                    else if ( isNull( rp.getHeader() )
                            && rp.getEntries().isEmpty()
                            && comment.isEol()
                            && eol
                            && isNull( space )
                            && isNull( init ) )
                    {
                        comment.setKey( "_header" );

                        rp.setHeader( comment );

                        comment = new PxrComment( 0, "_footer", false );
                    }

                    comment.ingest( space,  init, text, eol );
                }
                else
                {
                    if ( nonNull( comment ))
                    {
                        comment.setKey( key );
                    }

                    rp.getEntries().add( new PxrEntry( comment, entryIndex++, key, sep, text, eol, cont ) );

                    comment = null;
                }
                space = null;
                init = null;
                text = null;
                eol = false;
                key= null;
                cont = null;
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case LF:
        case CR:
        case SPACE:
        case BANG:
        case HASH:
        case CHAR:{
          ;
          break;
          }
        default:
          jj_la1[2] = jj_gen;
          break label_1;
        }
      }
      break;
      }
    case 0:{
      jj_consume_token(0);
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
rp.setFooter( comment );

        {if ("" != null) return rp;}
    throw new Error("Missing return statement in function");
}

  final public String initiator() throws ParseException {Token t = null;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case BANG:{
      t = jj_consume_token(BANG);
      break;
      }
    case HASH:{
      t = jj_consume_token(HASH);
      break;
      }
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return t.image;}
    throw new Error("Missing return statement in function");
}

  final public String text() throws ParseException {StringBuilder b = new StringBuilder( );
    Token t = null;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case CHAR:{
        t = jj_consume_token(CHAR);
        break;
        }
      case SPACE:{
        t = jj_consume_token(SPACE);
        break;
        }
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
b.append( t.image );
      if (jj_2_6(2)) {
        ;
      } else {
        break label_2;
      }
    }
{if ("" != null) return b.toString();}
    throw new Error("Missing return statement in function");
}

  final public List<PxrEntryContinuation> continuations() throws ParseException {Token t = null;
    boolean eol = false;
    String space = null;
    String value = null;
    List<PxrEntryContinuation> cont = new ArrayList<PxrEntryContinuation>();
    int index = 1;
    label_3:
    while (true) {
      t = jj_consume_token(CONT);
      if (jj_2_8(2)) {
        eol = eol();
        space = space();
        if (jj_2_7(2)) {
          value = text();
        } else {
          ;
        }
      } else {
        ;
      }
cont.add( new PxrEntryContinuation( index++, t.image, space, value, eol ) );
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case CONT:{
        ;
        break;
        }
      default:
        jj_la1[6] = jj_gen;
        break label_3;
      }
    }
{if ("" != null) return cont;}
    throw new Error("Missing return statement in function");
}

  final public String key() throws ParseException {Token t = null;
    t = jj_consume_token(CHAR);
{if ("" != null) return t.image;}
    throw new Error("Missing return statement in function");
}

  final public String sep() throws ParseException {String spaceBefore = null;
    String spaceAfter = null;
    Token t = null;
    if (jj_2_11(2)) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case SPACE:{
        spaceBefore = space();
        break;
        }
      default:
        jj_la1[7] = jj_gen;
        ;
      }
      t = jj_consume_token(EQUALS);
      if (jj_2_9(2)) {
        spaceAfter = space();
      } else {
        ;
      }
    } else if (jj_2_12(2)) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case SPACE:{
        spaceBefore = space();
        break;
        }
      default:
        jj_la1[8] = jj_gen;
        ;
      }
      t = jj_consume_token(COLON);
      if (jj_2_10(2)) {
        spaceAfter = space();
      } else {
        ;
      }
    } else {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case SPACE:{
        spaceBefore = space();
        break;
        }
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
{if ("" != null) return format(
                "%s%s%s",
                nonNull( spaceBefore ) ? spaceBefore : "",
                nonNull( t ) ? t.image : "",
                nonNull( spaceAfter ) ? spaceAfter : ""
        );}
    throw new Error("Missing return statement in function");
}

  final public String space() throws ParseException {Token t = null;
    t = jj_consume_token(SPACE);
{if ("" != null) return t.image;}
    throw new Error("Missing return statement in function");
}

  final public boolean eol() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case LF:{
      jj_consume_token(LF);
      break;
      }
    case CR:{
      jj_consume_token(CR);
      jj_consume_token(LF);
      break;
      }
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return true;}
    throw new Error("Missing return statement in function");
}

  private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_1()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_2()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_3()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_4()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_5()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_6()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_7()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_8()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_9()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_2_10(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_10()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  private boolean jj_2_11(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_11()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  private boolean jj_2_12(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_12()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  private boolean jj_3_5()
 {
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3R_4()
 {
    Token xsp;
    if (jj_3_6()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_6()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_5()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (!jj_scan_token(1)) return false;
    jj_scanpos = xsp;
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3_2()
 {
    if (jj_3R_5()) return true;
    return false;
  }

  private boolean jj_3_7()
 {
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3_8()
 {
    if (jj_3R_5()) return true;
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_10()
 {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_9()
 {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_8()
 {
    if (jj_scan_token(SPACE)) return true;
    return false;
  }

  private boolean jj_3_3()
 {
    if (jj_3R_4()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_6()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3_10()
 {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_14()
 {
    if (jj_scan_token(CONT)) return true;
    return false;
  }

  private boolean jj_3_9()
 {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_12()
 {
    Token xsp;
    if (jj_3R_14()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_14()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_13()
 {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3_12()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_10()) jj_scanpos = xsp;
    if (jj_scan_token(COLON)) return true;
    xsp = jj_scanpos;
    if (jj_3_10()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3_11()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_9()) jj_scanpos = xsp;
    if (jj_scan_token(EQUALS)) return true;
    xsp = jj_scanpos;
    if (jj_3_9()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3R_6()
 {
    if (jj_3R_12()) return true;
    return false;
  }

  private boolean jj_3R_7()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (!jj_3_11()) return false;
    jj_scanpos = xsp;
    if (!jj_3_12()) return false;
    jj_scanpos = xsp;
    if (jj_3R_13()) return true;
    return false;
  }

  private boolean jj_3_1()
 {
    if (jj_3R_4()) return true;
    return false;
  }

  private boolean jj_3R_11()
 {
    if (jj_scan_token(CR)) return true;
    if (jj_scan_token(LF)) return true;
    return false;
  }

  private boolean jj_3_4()
 {
    if (jj_3R_7()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_3()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3_6()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (!jj_scan_token(9)) return false;
    jj_scanpos = xsp;
    if (jj_scan_token(3)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public PxrParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[11];
  static private int[] jj_la1_0;
  static {
	   jj_la1_init_0();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x100,0x23e,0x23e,0x23f,0x30,0x208,0x100,0x8,0x8,0x8,0x6,};
	}
  final private JJCalls[] jj_2_rtns = new JJCalls[12];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public PxrParser(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public PxrParser(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new PxrParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 11; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 11; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public PxrParser(java.io.Reader stream) {
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new PxrParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 11; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new PxrParserTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 11; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public PxrParser(PxrParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 11; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(PxrParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 11; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   if (++jj_gc > 100) {
		 jj_gc = 0;
		 for (int i = 0; i < jj_2_rtns.length; i++) {
		   JJCalls c = jj_2_rtns[i];
		   while (c != null) {
			 if (c.gen < jj_gen) c.first = null;
			 c = c.next;
		   }
		 }
	   }
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error {
    @Override
    public Throwable fillInStackTrace() {
      return this;
    }
  }
  static private final LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
	 if (jj_scanpos == jj_lastpos) {
	   jj_la--;
	   if (jj_scanpos.next == null) {
		 jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
	   } else {
		 jj_lastpos = jj_scanpos = jj_scanpos.next;
	   }
	 } else {
	   jj_scanpos = jj_scanpos.next;
	 }
	 if (jj_rescan) {
	   int i = 0; Token tok = token;
	   while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
	   if (tok != null) jj_add_error_token(kind, i);
	 }
	 if (jj_scanpos.kind != kind) return true;
	 if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
	 return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
	 if (pos >= 100) {
		return;
	 }

	 if (pos == jj_endpos + 1) {
	   jj_lasttokens[jj_endpos++] = kind;
	 } else if (jj_endpos != 0) {
	   jj_expentry = new int[jj_endpos];

	   for (int i = 0; i < jj_endpos; i++) {
		 jj_expentry[i] = jj_lasttokens[i];
	   }

	   for (int[] oldentry : jj_expentries) {
		 if (oldentry.length == jj_expentry.length) {
		   boolean isMatched = true;

		   for (int i = 0; i < jj_expentry.length; i++) {
			 if (oldentry[i] != jj_expentry[i]) {
			   isMatched = false;
			   break;
			 }

		   }
		   if (isMatched) {
			 jj_expentries.add(jj_expentry);
			 break;
		   }
		 }
	   }

	   if (pos != 0) {
		 jj_lasttokens[(jj_endpos = pos) - 1] = kind;
	   }
	 }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[10];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 11; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 10; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 jj_endpos = 0;
	 jj_rescan_token();
	 jj_add_error_token(0, 0);
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  private boolean trace_enabled;

/** Trace enabled. */
  final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
	 jj_rescan = true;
	 for (int i = 0; i < 12; i++) {
	   try {
		 JJCalls p = jj_2_rtns[i];

		 do {
		   if (p.gen > jj_gen) {
			 jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
			 switch (i) {
			   case 0: jj_3_1(); break;
			   case 1: jj_3_2(); break;
			   case 2: jj_3_3(); break;
			   case 3: jj_3_4(); break;
			   case 4: jj_3_5(); break;
			   case 5: jj_3_6(); break;
			   case 6: jj_3_7(); break;
			   case 7: jj_3_8(); break;
			   case 8: jj_3_9(); break;
			   case 9: jj_3_10(); break;
			   case 10: jj_3_11(); break;
			   case 11: jj_3_12(); break;
			 }
		   }
		   p = p.next;
		 } while (p != null);

		 } catch(LookaheadSuccess ls) { }
	 }
	 jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
	 JJCalls p = jj_2_rtns[index];
	 while (p.gen > jj_gen) {
	   if (p.next == null) { p = p.next = new JJCalls(); break; }
	   p = p.next;
	 }

	 p.gen = jj_gen + xla - jj_la; 
	 p.first = token;
	 p.arg = xla;
  }

  static final class JJCalls {
	 int gen;
	 Token first;
	 int arg;
	 JJCalls next;
  }

}
