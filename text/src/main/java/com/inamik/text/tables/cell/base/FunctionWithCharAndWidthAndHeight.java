/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.cell.base;

import java.util.Collection;

public abstract class FunctionWithCharAndWidthAndHeight
{
    public abstract Collection<String> apply(Character character, Integer width, Integer height, Collection<String> cell);

    public static final FunctionWithCharAndWidthAndHeight IDENTITY = new FunctionWithCharAndWidthAndHeight() {
        @Override
        public FunctionWithWidthAndHeight withChar(char _1) { return FunctionWithWidthAndHeight.IDENTITY; }
        @Override
        public FunctionWithCharAndHeight withWidth(int _1) {
            return FunctionWithCharAndHeight.IDENTITY;
        }
        @Override
        public FunctionWithCharAndWidth withHeight(int _1) {
            return FunctionWithCharAndWidth.IDENTITY;
        }
        @Override
        public Collection<String> apply(Character _1, Integer _2, Integer _3, Collection<String> cell) { return cell; }
    };

    public FunctionWithWidthAndHeight withChar(final char character) {
        // curry(this, character)
        //
        final FunctionWithCharAndWidthAndHeight f = this;
        return new FunctionWithWidthAndHeight() {
            @Override
            public Collection<String> apply(Integer width, Integer height, Collection<String> cell) {
                return f.apply(character, width, height, cell);
            }
        };
    }

    public FunctionWithCharAndHeight withWidth (final int width) {
        // curry_2(this, width)
        //
        final FunctionWithCharAndWidthAndHeight f = this;
        return new FunctionWithCharAndHeight() {
            @Override
            public Collection<String> apply(Character character, Integer height, Collection<String> cell) {
                return f.apply(character, width, height, cell);
            }
        };
    }

    public FunctionWithCharAndWidth  withHeight(final int height) {
        // curry_3(this, height)
        //
        final FunctionWithCharAndWidthAndHeight f = this;
        return new FunctionWithCharAndWidth() {
            @Override
            public Collection<String> apply(Character character, Integer width, Collection<String> cell) {
                return f.apply(character, width, height, cell);
            }
        };
    }

}
