/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.cell.base;

import java.util.Collection;

public abstract class FunctionWithWidthAndHeight
{
    public abstract Collection<String> apply(Integer width, Integer height, Collection<String> cell);

    public static final FunctionWithWidthAndHeight IDENTITY = new FunctionWithWidthAndHeight() {
        @Override
        public FunctionWithHeight withWidth(int _1) {
            return FunctionWithHeight.IDENTITY;
        }
        @Override
        public FunctionWithWidth withHeight(int _1) {
            return FunctionWithWidth.IDENTITY;
        }
        @Override
        public Collection<String> apply(Integer _1, Integer _2, Collection<String> cell) { return cell; }
    };

    public FunctionWithHeight withWidth (final int width) {
        // curry(this, width)
        //
        final FunctionWithWidthAndHeight f = this;
        return new FunctionWithHeight() {
            @Override
            public Collection<String> apply(Integer height, Collection<String> cell) {
                return f.apply(width, height, cell);
            }
        };
    }

    public FunctionWithWidth  withHeight(final int height) {
        // curry_2(this, height)
        //
        final FunctionWithWidthAndHeight f = this;
        return new FunctionWithWidth() {
            @Override
            public Collection<String> apply(Integer width, Collection<String> cell) {
                return f.apply(width, height, cell);
            }
        };
    }

}
