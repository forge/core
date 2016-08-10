/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.line.base;

public abstract class FunctionWithWidth
{
    public abstract String apply(Integer width, String line);

    public static final FunctionWithWidth IDENTITY =  new FunctionWithWidth() {
        @Override
        public Function withWidth(int _1) {
            return Function.IDENTITY;
        }
        @Override
        public String apply(Integer _1, String line) { return line; }
    };

    public Function withWidth(final int width) {
        // curry(this, width)
        //
        final FunctionWithWidth f = this;
        return new Function() {
            @Override
            public String apply(String line) {
                return f.apply(width, line);
            }
        };
    }

}
