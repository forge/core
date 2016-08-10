/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.cell;

import com.inamik.text.tables.cell.base.FunctionWithHeight;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class BottomTruncate extends FunctionWithHeight
{
    public static final BottomTruncate INSTANCE = new BottomTruncate();

    @Override
    public Collection<String> apply(Integer height, Collection<String> cell) {
        // Any truncating needed?
        //
        if (cell.size() <= height) {
            return cell;
        }
        List<String> newCell = new ArrayList<String>(height);
        for (String line: cell) {
            // Skip last n lines
            //
            if (newCell.size() >= height) { break; }
            newCell.add(line);
        }
        return newCell;
    }

}
