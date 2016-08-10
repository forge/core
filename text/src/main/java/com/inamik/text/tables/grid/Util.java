/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.grid;

import com.inamik.text.tables.Cell;
import com.inamik.text.tables.GridTable;

import java.io.PrintStream;

public final class Util
{
    public static void print(GridTable g) {
        print(g, System.out);
    }

    public static void print(GridTable g, PrintStream out) {
        // Apply final padding to ensure grid prints properly
        //
        g = g
            .apply(Cell.Functions.TOP_ALIGN)
            .apply(Cell.Functions.LEFT_ALIGN)
            ;

        // Convert the grid to a cell
        // then iterate over the lines and print
        //
        for (String line: g.toCell()) {
            out.println(line);
        }
    }

}
