/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables;

import com.inamik.text.tables.cell.base.Function;

import java.util.Collection;
import java.util.LinkedList;

/*
 * SimpleTable - Optimized for quick table construction without having to know the
 * final table dimensions beforehand.  It Allows you to build a table one row/cell at
 * a time.
 * 
 * Cell functions do not provide any global context.
 * 
 * You can use toGrid() to convert to GridTable for more complex manipulations.
 */
public class SimpleTable
{
    private LinkedList<LinkedList<Collection<String>>> table = new LinkedList<LinkedList<Collection<String>>>();
    private int numRows = 0;
    private int numCols = 0;
    
    public static SimpleTable of() { return new SimpleTable(); }

    public SimpleTable() { /* Empty */ }

    public SimpleTable nextRow() {
        LinkedList<Collection<String>> row = new LinkedList<Collection<String>>();
        table.add(row);
        numRows++;
        return this;
    }

    public SimpleTable nextCell() {
        if (table.isEmpty()) {
            throw new IllegalStateException("Table is empty. Call nextRow() first");
        }
        final LinkedList<Collection<String>> row = table.getLast();
        row.add(Cell.EMPTY);
        numCols = Math.max(numCols, row.size());
        return this;
    }

    public SimpleTable nextCell(String...lines) {
        return nextCell().addLines(lines);
    }
    
    public SimpleTable nextCell(Collection<String> lines) {
        return nextCell().addLines(lines);
    }

    public SimpleTable addLine(String line) {
        if (table.isEmpty()) {
            throw new IllegalStateException("Table is empty. Call nextRow() first");
        }
        LinkedList<Collection<String>> row = table.getLast();
        if (row.isEmpty()) {
            throw new IllegalStateException("Row is empty.  Call nextCell() first");
        }
        Collection<String> cell = row.removeLast();
        cell = Cell.append(cell, line);
        row.add(cell);
        return this;
    }

    public SimpleTable addLines(String...lines) {
        if (table.isEmpty()) {
            throw new IllegalStateException("Table is empty. Call nextRow() first");
        }
        LinkedList<Collection<String>> row = table.getLast();
        if (row.isEmpty()) {
            throw new IllegalStateException("Row is empty.  Call nextCell() first");
        }
        Collection<String> cell = row.removeLast();
        cell = Cell.append(cell, lines);
        row.add(cell);
        return this;
    }

    public SimpleTable addLines(Collection<String> lines) {
        if (table.isEmpty()) {
            throw new IllegalStateException("Table is empty. Call nextRow() first");
        }
        LinkedList<Collection<String>> row = table.getLast();
        if (row.isEmpty()) {
            throw new IllegalStateException("Row is empty.  Call nextCell() first");
        }
        Collection<String> cell = row.removeLast();
        cell = Cell.append(cell, lines);
        row.add(cell);
        return this;
    }

    public SimpleTable applyToCell(Function f) {
        if (table.isEmpty()) {
            throw new IllegalStateException("Table is empty. Call nextRow() first");
        }
        LinkedList<Collection<String>> row = table.getLast();
        if (row.isEmpty()) {
            throw new IllegalStateException("Row is empty.  Call nextCell() first");
        }
        Collection<String> cell = row.removeLast();
        cell = f.apply(cell);
        row.add(cell);
        return this;
    }

    public SimpleTable applyToCell(Cell.Function f) {
        if (table.isEmpty()) {
            throw new IllegalStateException("Table is empty. Call nextRow() first");
        }
        LinkedList<Collection<String>> row = table.getLast();
        if (row.isEmpty()) {
            throw new IllegalStateException("Row is empty.  Call nextCell() first");
        }
        Collection<String> cell = row.removeLast();
        cell = f.apply(cell);
        row.add(cell);
        return this;
    }

    public int nextRowNum() { return table.size(); }

    public int nextColNum() { return (table.isEmpty()) ? 0 : table.getLast().size(); }

    public int numRows() { return numRows; }

    public int numCols() { return numCols; }

    public GridTable toGrid() {
        GridTable grid = GridTable.of(numRows, numCols);
        int rowNum = 0;
        for (LinkedList<Collection<String>> row: table) {
            int colNum = 0;
            for (Collection<String> cell: row) {
                grid.put(rowNum, colNum, cell);
                colNum++;
            }
            rowNum++;
        }
        return grid;        
    }

}
