/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables.cell.base;

import java.util.Collection;

public abstract class FunctionWithHeight
{
    public abstract Collection<String> apply(Integer height, Collection<String> cell);

    public static final FunctionWithHeight IDENTITY = new FunctionWithHeight() {
        @Override
        public Function withHeight(int _1) {
            return Function.IDENTITY;
        }
        @Override
        public Collection<String> apply(Integer _1, Collection<String> cell) {
            return cell;
        }
    };

    public Function withHeight(final int height) {
        // curry(this, height)
        //
        final FunctionWithHeight f = this;
        return new Function() {
            @Override
            public Collection<String> apply(Collection<String> cell) {
                return f.apply(height, cell);
            }
        };
    }

}
