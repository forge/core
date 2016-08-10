/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.cell.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Function
{
    public abstract Collection<String> apply(Collection<String> cell);

    public static final Function IDENTITY = new Function() {
        @Override
        public Collection<String> apply(Collection<String> cell) {
            return cell;
        }
    };

    /*
     * From line.Function
     */
    public static Function from(final com.inamik.text.tables.line.base.Function f) {
        return new Function() {
            @Override
            public Collection<String> apply(Collection<String> cell) {
                // map(cell, f)
                //
                final List<String> r = new ArrayList<String>(cell.size());
                for (String line: cell) { r.add(f.apply(line)); }
                return Collections.unmodifiableCollection(r);
            }
        };
    }

}
