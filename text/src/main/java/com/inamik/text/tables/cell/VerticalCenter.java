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

public final class VerticalCenter extends FunctionWithHeight
{
    public static final VerticalCenter INSTANCE = new VerticalCenter();

    @Override
    public Collection<String> apply(Integer height, Collection<String> cell) {
        // Need to truncate?
        //
        if (cell.size() > height) {
            int over  = cell.size() - height;
            int carry = over % 2;
            int half  = (over - carry) / 2;
            cell = TopTruncate   .INSTANCE.apply(cell.size() + half + carry, cell);
            cell = BottomTruncate.INSTANCE.apply(cell.size() + half        , cell);
        }
        else {
            // Need to pad?
            //
            if (cell.size() < height) {
                int pad = height - cell.size();
                int carry = pad % 2;
                int half = (pad - carry) / 2;
                cell = TopPad   .INSTANCE.apply(cell.size() + half + carry, cell);
                cell = BottomPad.INSTANCE.apply(cell.size() + half        , cell);
            }
        }
        return cell;
    }

}
