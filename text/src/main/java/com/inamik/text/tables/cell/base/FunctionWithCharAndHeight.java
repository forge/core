/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.cell.base;

import java.util.Collection;

public abstract class FunctionWithCharAndHeight
{
    public abstract Collection<String> apply(Character character, Integer height, Collection<String> cell);

    public static final FunctionWithCharAndHeight IDENTITY = new FunctionWithCharAndHeight() {
        @Override
        public FunctionWithHeight withChar(char _1) {return FunctionWithHeight.IDENTITY; }
        @Override
        public FunctionWithChar withHeight(int _1) {
            return FunctionWithChar.IDENTITY;
        }
        @Override
        public Collection<String> apply(Character _1, Integer _2, Collection<String> cell) {
            return cell;
        }
    };

    public FunctionWithHeight withChar(final char character) {
        // curry(this, character)
        //
        final FunctionWithCharAndHeight that = this;
        return new FunctionWithHeight() {
            @Override
            public Collection<String> apply(Integer height, Collection<String> cell) {
                return that.apply(character, height, cell);
            }
        };
    }

    public FunctionWithChar withHeight(final int width) {
        // curry_2(this, width)
        //
        final FunctionWithCharAndHeight f = this;
        return new FunctionWithChar() {
            @Override
            public Collection<String> apply(Character character, Collection<String> cell) {
                return f.apply(character, width, cell);
            }
        };
    }

}
