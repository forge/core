/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * GridTable - Offers a more powerful table builder (compared to SimpleTable), 
 * but requires knowledge of the final table dimensions at construction,
 * along with specifying coordinates when manipulating cells.
 */
public final class GridTable
{
    private final int      numRows;
    private final int      numCols;

    private final Collection<String>[][] table;

    private final int[]    colWidths;
    private final int[]    rowHeights;

    private       int      tableWidth  = 0;
    private       int      tableHeight = 0;

    public static GridTable of(int numRows, int numCols) { return new GridTable(numRows, numCols); }

    /*
     * Constructor
     */
    @SuppressWarnings("unchecked")
    public GridTable(int numRows, int numCols) {
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException();
        }
        this.numRows = numRows;
        this.numCols = numCols;

        this.table  = (Collection<String>[][]) new Collection[numRows][numCols];
        // Fill table with empty cells,
        //
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                this.table [r][c] = Cell.EMPTY;
            }
        }

        this.colWidths  = new int[numCols];
        this.rowHeights = new int[numRows];
    }

    /*
     * put
     */
    public GridTable put(int row, int col, Collection<String> cell) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("row");
        }
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("col");
        }
        _put(row, col, cell);
        updateRowRanges(row);
        updateColRanges(col);
        return this;
    }

    /*
     * apply
     */
    public GridTable apply(int row, int col, Cell.Function f) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("row");
        }
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("col");
        }
        _apply(row, col, f);
        updateRowRanges(row);
        updateColRanges(col);
        return this;
    }

    /*
     * apply (to all)
     */
    public GridTable apply(Cell.Function f) {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                _apply(row, col, f);
                updateColRanges(col);
            }
            updateRowRanges(row);
        }
        return this;
    }

    /*
     * applyToCol
     */
    public GridTable applyToCol(int col, Cell.Function f) {
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("col");
        }
        for (int row = 0; row < numRows; row++) {
            _apply(row, col, f);
            updateRowRanges(row);
        }
        updateColRanges(col);
        return this;
    }

    /*
     * applyToRow
     */
    public GridTable applyToRow(int row, Cell.Function f) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("row");
        }
        for (int col = 0; col < numCols; col++) {
            _apply(row, col, f);
            updateColRanges(col);
        }
        updateRowRanges(row);
        return this;
    }

    /*
     * cell
     */
    public Collection<String> cell(int row, int col) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("row");
        }
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("col");
        }
        return _cell(row, col);
    }

    public int width() { return tableWidth; }

    public int height() { return tableHeight; }

    public int numRows() { return numRows; }

    public int numCols() { return numCols; }

    public int colWidth(int col) {
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("col");
        }
        return colWidths[col];
    }

    public int rowHeight(int row) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("row");
        }
        return rowHeights[row];
    }
    
    public Collection<String> toCell() {
        List<String> cell = new ArrayList<String>(tableHeight);
        // foreach row
        //
        for (int row = 0; row < numRows; row++) {
            final int rowHeight = rowHeights[row];
            // Build row line buffers
            //
            StringBuilder[] rowBuffers = new StringBuilder[rowHeight];
            for (int b = 0; b < rowHeight; b++) {
                rowBuffers[b] = new StringBuilder();
            }
            // foreach col
            //
            for (int col = 0; col < numCols; col++) {
                final Iterator<String> cellLines = _cell(row, col).iterator();
                // Append col lines to row line buffers
                //
                for (StringBuilder buffer: rowBuffers) {
                    if (cellLines.hasNext()) {
                        buffer.append(cellLines.next());
                    }
                }
            }
            // Output whole row at once
            //
            for (StringBuilder b: rowBuffers) {
                cell.add(b.toString());
            }
        }
        return Collections.unmodifiableCollection(cell);
    }
    
    /* ****************************************************************************************************************
     * Private Methods
     * ***************************************************************************************************************/

    /*
     * updateRowRanges
     */
    private void updateRowRanges(int row) {
        int height = 0;
        for (int col = 0; col < numCols; col++) {
            height = Math.max(height, table[row][col].size());
        }

        final int oldHeight = rowHeights[row];

        rowHeights[row] = height;

        tableHeight = tableHeight - oldHeight + height;
    }

    /*
     * updateColRanges
     */
    private void updateColRanges(int col) {
        int width = 0;
        for (int row = 0; row < numRows; row++) {
            for (String line: table[row][col]) {
                width = Math.max(width, line.length());
            }
        }

        final int oldWidth  = colWidths [col];

        colWidths[col] = width;

        tableWidth  = tableWidth  - oldWidth  + width;
    }

    /*
     * _put
     */
    private void _put(int row, int col, Collection<String> cell) {
        table[row][col] = Collections.unmodifiableCollection(cell);
    }

    /*
     * _apply
     */
    private void _apply(int row, int col, Cell.Function f) {
        _put(row, col, f.apply(colWidths[col], rowHeights[row], table[row][col]));
    }

    /*
     * _cell
     */
    private Collection<String> _cell(int row, int col) {
        return table[row][col];
    }

}
