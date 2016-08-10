/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.line.base;

public abstract class Function
{
    public abstract String apply(String line);

    public static final Function IDENTITY = new Function() {
        @Override
        public String apply(String line) {
            return line;
        }
    };

}
