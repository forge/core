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

public abstract class FunctionWithWidth
{
    public abstract Collection<String> apply(Integer width, Collection<String> cell);

    public static final FunctionWithWidth IDENTITY = new FunctionWithWidth() {
        @Override
        public Function withWidth(int _1) {
            return Function.IDENTITY;
        }
        @Override
        public Collection<String> apply(Integer height, Collection<String> cell) {
            return cell;
        }
    };

    /*
     * From line.FunctionWithWidth
     */
    public static FunctionWithWidth from(final com.inamik.text.tables.line.base.FunctionWithWidth f) {
        return new FunctionWithWidth() {
            @Override
            public Function withWidth(int width) {
                // lift(curry(f, width))
                //
                return Function.from(f.withWidth(width));
            }
            @Override
            public Collection<String> apply(Integer width, Collection<String> cell) {
                // map(cell, f)
                //
                final List<String> r = new ArrayList<String>(cell.size());
                for (String line: cell) { r.add(f.apply(width, line)); }
                return Collections.unmodifiableCollection(r);
            }
        };
    }

    public Function withWidth(final int width) {
        // curry(this, width)
        //
        final FunctionWithWidth f = this;
        return new Function() {
            @Override
            public Collection<String> apply(Collection<String> cell) {
                return f.apply(width, cell);
            }
        };
    }

}
