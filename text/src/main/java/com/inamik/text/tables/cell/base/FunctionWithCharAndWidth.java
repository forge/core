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

public abstract class FunctionWithCharAndWidth
{
    public abstract Collection<String> apply(Character character, Integer width, Collection<String> cell);

    public static final FunctionWithCharAndWidth IDENTITY = new FunctionWithCharAndWidth() {
        @Override
        public FunctionWithWidth withChar(char _1) {return FunctionWithWidth.IDENTITY; }
        @Override
        public FunctionWithChar withWidth(int _1) {
            return FunctionWithChar.IDENTITY;
        }
        @Override
        public Collection<String> apply(Character _1, Integer _2, Collection<String> cell) {
            return cell;
        }
    };

    /*
     * From line.FunctionWithCharAndWidth
     */
    public static FunctionWithCharAndWidth from(final com.inamik.text.tables.line.base.FunctionWithCharAndWidth f) {
        return new FunctionWithCharAndWidth() {
            @Override
            public FunctionWithWidth withChar(char character) {
                // lift(curry(f, character))
                //
                return FunctionWithWidth.from(f.withChar(character));
            }
            @Override
            public FunctionWithChar withWidth(int width) {
                // lift(curry_2(f, character))
                //
                return FunctionWithChar.from(f.withWidth(width));
            }
            @Override
            public Collection<String> apply(Character character, Integer width, Collection<String> cell) {
                // map(cell, curry(curry(f, character), width))
                //
                final List<String> r = new ArrayList<String>(cell.size());
                for (String line: cell) { r.add(f.apply(character, width, line)); }
                return Collections.unmodifiableCollection(r);
            }
        };
    }

    public FunctionWithWidth withChar(final char character) {
        // curry(this, character)
        //
        final FunctionWithCharAndWidth f = this;
        return new FunctionWithWidth() {
            @Override
            public Collection<String> apply(Integer width, Collection<String> cell) {
                return f.apply(character, width, cell);
            }
        };
    }

    public FunctionWithChar withWidth(final int width) {
        // curry_2(this, width)
        //
        final FunctionWithCharAndWidth f = this;
        return new FunctionWithChar() {
            @Override
            public Collection<String> apply(Character character, Collection<String> cell) {
                return f.apply(character, width, cell);
            }
        };
    }

}
