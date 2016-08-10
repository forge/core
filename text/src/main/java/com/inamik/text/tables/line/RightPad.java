/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.line;

import com.inamik.text.tables.line.base.FunctionWithCharAndWidth;

public final class RightPad extends FunctionWithCharAndWidth
{
    public static final RightPad INSTANCE = new RightPad();

    @Override
    public String apply(Character fill, Integer width, String line) {
        if (line.length() >= width) {
            return line;
        }
        StringBuilder sb = new StringBuilder(width);
        sb.append(line);
        while (sb.length() < width) {
            sb.append(fill);
        }
        return sb.toString();
    }

}
