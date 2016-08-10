/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.line;

import com.inamik.text.tables.line.base.FunctionWithCharAndWidth;

public final class RightAlign extends FunctionWithCharAndWidth
{
    public static final RightAlign INSTANCE = new RightAlign();

    @Override
    public String apply(Character fill, Integer width, String line) {
        line = LeftTruncate.INSTANCE.apply(width, line);
        line = LeftPad     .INSTANCE.apply(fill, width, line);
        return line;
    }

}
