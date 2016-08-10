/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.line.base;

public abstract class FunctionWithCharAndWidth extends FunctionWithWidth
{
    public abstract String apply(Character character, Integer width, String line);

    public static final FunctionWithCharAndWidth IDENTITY =  new FunctionWithCharAndWidth() {
        @Override
        public FunctionWithWidth withChar(char _1) {
            return FunctionWithWidth.IDENTITY;
        }
        @Override
        public FunctionWithChar withWidth(int _1) {
            return FunctionWithChar.IDENTITY;
        }
        @Override
        public String apply(Character _1, Integer _2, String line) { return line; }
    };

    public FunctionWithWidth withChar(final char character) {
        // curry(this, character)
        //
        final FunctionWithCharAndWidth f = this;
        return new FunctionWithWidth() {
            @Override
            public String apply(Integer width, String line) {
                return f.apply(character, width, line);
            }
        };
    }

    public FunctionWithChar withWidth(final int width) {
        // curry_2(this, width)
        //
        final FunctionWithCharAndWidth f = this;
        return new FunctionWithChar() {
            @Override
            public String apply(Character character, String line) {
                return f.apply(character, width, line);
            }
        };
    }

    /*
     * Apply with ' '
     */
    @Override
    public String apply(Integer width, String line) { return apply(' ', width, line); }

}
