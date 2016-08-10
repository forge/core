/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables;

import com.inamik.text.tables.grid.Border;
import org.junit.Assert;
import org.junit.Test;

import static com.inamik.text.tables.Cell.Functions.*;

public class GridTableTest
{
    private static final String[] FULL_TABLE = {
        "╔══════════╦══════════╦══════════╗",
        "║Left^^^^^^║  Center  ║     Right║",
        "║Top^^^^^^^║   Top    ║       Top║",
        "║^^^^^^^^^^║          ║          ║",
        "║^^^^^^^^^^║          ║          ║",
        "║^^^^^^^^^^║          ║          ║",
        "║^^^^^^^^^^║          ║          ║",
        "╠══════════╬══════════╬══════════╣",
        "║          ║..........║          ║",
        "║          ║..........║          ║",
        "║Left      ║..Center..║     Right║",
        "║Center    ║..Center..║    Center║",
        "║          ║..........║          ║",
        "║          ║..........║          ║",
        "╠══════════╬══════════╬══════════╣",
        "║          ║          ║__________║",
        "║          ║          ║__________║",
        "║          ║          ║__________║",
        "║          ║          ║__________║",
        "║Left      ║  Center  ║_____Right║",
        "║Bottom    ║  Bottom  ║____Bottom║",
        "╚══════════╩══════════╩══════════╝",
    };

    @Test
    public void testFullTable() {
        final int width  = 10;
        final int height = 6;

        // Build grid table
        // Each cell will be $width chars x $height chars
        //
        // NOTE: Apply vertical alignment FIRST !
        //
        GridTable g = GridTable.of(3, 3)
            .put(0, 0, Cell.of("Left"  , "Top"   ))
            .put(0, 1, Cell.of("Center", "Top"   ))
            .put(0, 2, Cell.of("Right" , "Top"   ))

            .put(1, 0, Cell.of("Left"  , "Center"))
            .put(1, 1, Cell.of("Center", "Center"))
            .put(1, 2, Cell.of("Right" , "Center"))

            .put(2, 0, Cell.of("Left"  , "Bottom"))
            .put(2, 1, Cell.of("Center", "Bottom"))
            .put(2, 2, Cell.of("Right" , "Bottom"))

            .applyToRow(0, TOP_ALIGN      .withHeight(height))
            .applyToRow(1, VERTICAL_CENTER.withHeight(height))
            .applyToRow(2, BOTTOM_ALIGN   .withHeight(height))

            .apply(0, 0, LEFT_ALIGN.withWidth(width).withChar('^'))
            .apply(1, 0, LEFT_ALIGN)
            .apply(2, 0, LEFT_ALIGN)

            .apply(0, 1, HORIZONTAL_CENTER.withWidth(width))
            .apply(1, 1, HORIZONTAL_CENTER.withChar('.'))
            .apply(2, 1, HORIZONTAL_CENTER)

            .apply(0, 2, RIGHT_ALIGN.withWidth(width))
            .apply(1, 2, RIGHT_ALIGN)
            .apply(2, 2, RIGHT_ALIGN.withChar('_'))
            ;

        Assert.assertEquals(g.numRows(), 3);
        Assert.assertEquals(g.numCols(), 3);
        Assert.assertEquals(g.width()  , 30);
        Assert.assertEquals(g.height() , 18);

        // Add border
        //
        g = Border.DOUBLE_LINE.apply(g);

        Assert.assertEquals(g.numRows(), 7);
        Assert.assertEquals(g.numCols(), 7);
        Assert.assertEquals(g.width()  , 34);
        Assert.assertEquals(g.height() , 22);

        Assert.assertArrayEquals(FULL_TABLE, g.toCell().toArray());
    }

}
