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

public abstract class FunctionWithChar
{
    public abstract Collection<String> apply(Character character, Collection<String> cell);

    public static final FunctionWithChar IDENTITY = new FunctionWithChar() {
        @Override
        public Function withChar(char _1) {
            return Function.IDENTITY;
        }
        @Override
        public Collection<String> apply(Character _1, Collection<String> cell) {
            return cell;
        }
    };

    /*
     * From line.FunctionWithChar
     */
    public static FunctionWithChar from(final com.inamik.text.tables.line.base.FunctionWithChar f) {
        return new FunctionWithChar() {
            @Override
            public Function withChar(char character) {
                // lift(curry(f, character))
                //
                return Function.from(f.withChar(character));
            }
            @Override
            public Collection<String> apply(Character character, Collection<String> cell) {
                // map(cell, curry(f, character))
                //
                final List<String> r = new ArrayList<String>(cell.size());
                for (String line: cell) { r.add(f.apply(character, line)); }
                return Collections.unmodifiableCollection(r);
            }
        };
    }

    public Function withChar(final char character) {
        // curry(this, character)
        //
        final FunctionWithChar f = this;
        return new Function() {
            @Override
            public Collection<String> apply(Collection<String> cell) {
                return f.apply(character, cell);
            }
        };
    }

}
