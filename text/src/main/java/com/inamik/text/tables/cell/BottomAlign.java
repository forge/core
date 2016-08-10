/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.cell;

import com.inamik.text.tables.cell.base.FunctionWithHeight;

import java.util.Collection;

public final class BottomAlign extends FunctionWithHeight
{
    public static final BottomAlign INSTANCE = new BottomAlign();

    @Override
    public Collection<String> apply(Integer height, Collection<String> cell) {
        cell = TopTruncate.INSTANCE.apply(height, cell);
        cell = TopPad     .INSTANCE.apply(height, cell);
        return cell;
    }

}
