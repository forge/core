/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.line;

import com.inamik.text.tables.line.base.FunctionWithCharAndWidth;

public final class HorizontalCenter extends FunctionWithCharAndWidth
{
    public static final HorizontalCenter INSTANCE = new HorizontalCenter();

    @Override
    public String apply(Character fill, Integer width, String line) {
        // Need to truncate?
        //
        if (line.length() > width) {
            int over  = line.length() - width;
            int carry = over % 2;
            int half  = (over - carry) / 2;
            line = RightTruncate.INSTANCE.apply(line.length() + half + carry, line);
            line = LeftTruncate .INSTANCE.apply(line.length() + half        , line);
        }
        else {
            // Need to pad?
            //
            if (line.length() < width) {
                int pad = width - line.length();
                int carry = pad % 2;
                int half = (pad - carry) / 2;
                line = RightPad.INSTANCE.apply(fill, line.length() + half + carry, line);
                line = LeftPad .INSTANCE.apply(fill, line.length() + half        , line);
            }
        }
        return line;
    }

}
