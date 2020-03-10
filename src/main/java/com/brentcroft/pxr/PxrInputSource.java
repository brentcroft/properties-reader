package com.brentcroft.pxr;

import com.brentcroft.pxr.model.PxrProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.InputSource;

@AllArgsConstructor
public class PxrInputSource extends InputSource
{
    @Getter
    @Setter
    private PxrProperties pxrProperties;
}
