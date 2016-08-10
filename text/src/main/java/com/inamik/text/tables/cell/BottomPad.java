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

public final class BottomPad extends FunctionWithHeight
{
    public static final BottomPad INSTANCE = new BottomPad();

    @Override
    public Collection<String> apply(Integer height, Collection<String> cell) {
        // Any padding needed?
        //
        if (cell.size() >= height) {
            return cell;
        }
        List<String> newCell = new ArrayList<String>(height);
        newCell.addAll(cell);
        while (newCell.size() < height) {
            newCell.add("");
        }
        return newCell;
    }

}
