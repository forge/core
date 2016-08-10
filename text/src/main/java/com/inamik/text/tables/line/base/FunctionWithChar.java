/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.line.base;

public abstract class FunctionWithChar extends Function
{
    public abstract String apply(Character character, String line);

    public static final FunctionWithChar IDENTITY =  new FunctionWithChar() {
        @Override
        public Function withChar(char _1) {
            return Function.IDENTITY;
        }
        @Override
        public String apply(Character _1, String line) { return line; }
    };

    public Function withChar(final char character) {
        // curry(this, character)
        //
        final FunctionWithChar f = this;
        return new Function() { 
            @Override
            public String apply(String line) {
                return f.apply(character, line);
            }
        };
    }

    /*
     * Apply with ' '
     */
    @Override
    public String apply(String line) { return apply(' ', line); }

}
