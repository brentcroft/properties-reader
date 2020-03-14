package com.brentcroft.pxr.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PxrEntryContinuation
{
    private int index;
    private String cont;
    private String prefix;
    private String value;
    private boolean eol;
}
