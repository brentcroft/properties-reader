package com.brentcroft.pxr;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Wrap a writer to remove all CRs.
 *
 * see: https://stackoverflow.com/questions/53596202/javax-xml-transform-transformer-line-endings-no-longer-respect-system-property
 */
public class CRFilterWriter extends FilterWriter
{
    public CRFilterWriter( Writer out )
    {
        super( out );
    }

    public static CRFilterWriter from( Writer out)
    {
        return new CRFilterWriter( out );
    }

    public Writer getWriter()
    {
        return out;
    }

    public String toString()
    {
        return getWriter().toString();
    }

    @Override
    public void write(int c) throws IOException
    {
        if (c != (int)('\r')) {
            super.write(c);
        }
    }

    @Override
    public void write(char[] cbuf, int offset, int length) throws IOException {
        int localOffset = offset;
        for (int i = localOffset; i < offset + length; ++i) {
            if (cbuf[i] == '\r') {
                if (i > localOffset) {
                    super.write(cbuf, localOffset, i - localOffset);
                }
                localOffset = i + 1;
            }
        }
        if (localOffset < offset + length) {
            super.write(cbuf, localOffset, offset + length - localOffset);
        }
    }

    @Override
    public void write(String str, int offset, int length) throws IOException {
        int localOffset = offset;
        for (int i = localOffset; i < offset + length; ++i) {
            if (str.charAt(i) == '\r') {
                if (i > localOffset) {
                    super.write(str, localOffset, i - localOffset);
                }
                localOffset = i + 1;
            }
        }
        if (localOffset < offset + length) {
            super.write(str, localOffset, offset + length - localOffset);
        }
    }
}
