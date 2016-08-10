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

public class SimpleTableTest
{
    private static final String[] FULL_TABLE = {
        "┌──────────┬──────────┬──────────┐",
        "│Left^^^^^^│  Center  │     Right│",
        "│Top^^^^^^^│   Top    │       Top│",
        "│^^^^^^^^^^│          │          │",
        "│^^^^^^^^^^│          │          │",
        "│^^^^^^^^^^│          │          │",
        "│^^^^^^^^^^│          │          │",
        "├──────────┼──────────┼──────────┤",
        "│          │..........│          │",
        "│          │..........│          │",
        "│Left      │..Center..│     Right│",
        "│Center    │..Center..│    Center│",
        "│          │..........│          │",
        "│          │..........│          │",
        "├──────────┼──────────┼──────────┤",
        "│          │          │__________│",
        "│          │          │__________│",
        "│          │          │__________│",
        "│          │          │__________│",
        "│Left      │  Center  │_____Right│",
        "│Bottom    │  Bottom  │____Bottom│",
        "└──────────┴──────────┴──────────┘",
    };

    @Test
    public void testFullTable() {
        final int width  = 10;
        final int height = 6;

        // Build simple table
        // Each cell will be $width chars x $height chars
        //
        // NOTE: Apply vertical alignment FIRST !
        //
        SimpleTable s = SimpleTable.of()
            .nextRow()
                .nextCell()
                    .addLine("Left")
                    .addLine("Top")
                    .applyToCell(TOP_ALIGN .withHeight(height))
                    .applyToCell(LEFT_ALIGN.withWidth (width ).withChar('^'))
                .nextCell()
                    .addLine("Center")
                    .addLine("Top")
                    .applyToCell(TOP_ALIGN        .withHeight(height))
                    .applyToCell(HORIZONTAL_CENTER.withWidth (width ))
                .nextCell()
                    .addLine("Right")
                    .addLine("Top")
                    .applyToCell(TOP_ALIGN  .withHeight(height))
                    .applyToCell(RIGHT_ALIGN.withWidth (width ))
            .nextRow()
                .nextCell()
                    .addLine("Left")
                    .addLine("Center")
                    .applyToCell(VERTICAL_CENTER.withHeight(height))
                    .applyToCell(LEFT_ALIGN     .withWidth (width ))
                .nextCell()
                    .addLine("Center")
                    .addLine("Center")
                    .applyToCell(VERTICAL_CENTER  .withHeight(height))
                    .applyToCell(HORIZONTAL_CENTER.withWidth (width ).withChar('.'))
                .nextCell()
                    .addLine("Right")
                    .addLine("Center")
                    .applyToCell(VERTICAL_CENTER.withHeight(height))
                    .applyToCell(RIGHT_ALIGN    .withWidth (width ))
            .nextRow()
                .nextCell()
                    .addLine("Left")
                    .addLine("Bottom")
                    .applyToCell(BOTTOM_ALIGN.withHeight(height))
                    .applyToCell(LEFT_ALIGN  .withWidth (width ))
                .nextCell()
                    .addLine("Center")
                    .addLine("Bottom")
                    .applyToCell(BOTTOM_ALIGN     .withHeight(height))
                    .applyToCell(HORIZONTAL_CENTER.withWidth (width ))
                .nextCell()
                    .addLine("Right")
                    .addLine("Bottom")
                    .applyToCell(BOTTOM_ALIGN.withHeight(height))
                    .applyToCell(RIGHT_ALIGN .withWidth (width ).withChar('_'))
            ;

        Assert.assertEquals(s.numRows(), 3);
        Assert.assertEquals(s.numCols(), 3);

        // Convert to grid
        //
        GridTable g = s.toGrid();

        Assert.assertEquals(g.numRows(), 3);
        Assert.assertEquals(g.numCols(), 3);
        Assert.assertEquals(g.width()  , 30);
        Assert.assertEquals(g.height() , 18);

        // Add border
        //
        g = Border.SINGLE_LINE.apply(g);

        Assert.assertEquals(g.numRows(), 7);
        Assert.assertEquals(g.numCols(), 7);
        Assert.assertEquals(g.width()  , 34);
        Assert.assertEquals(g.height() , 22);

        Assert.assertArrayEquals(FULL_TABLE, g.toCell().toArray());
        
    }

}
