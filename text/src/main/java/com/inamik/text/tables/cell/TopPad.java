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

public final class TopPad extends FunctionWithHeight
{
    public static final TopPad INSTANCE = new TopPad();

    @Override
    public Collection<String> apply(Integer height, Collection<String> cell) {
        // Any padding needed?
        //
        if (cell.size() >= height) {
            return cell;
        }
        List<String> newCell = new ArrayList<String>(height);
        while (cell.size() + newCell.size() < height) {
            newCell.add("");
        }
        newCell.addAll(cell);
        return newCell;
    }

}
