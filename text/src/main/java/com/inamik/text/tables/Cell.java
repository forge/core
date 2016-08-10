/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.inamik.text.tables;

import com.inamik.text.tables.cell.BottomAlign;
import com.inamik.text.tables.cell.BottomPad;
import com.inamik.text.tables.cell.BottomTruncate;
import com.inamik.text.tables.cell.base.FunctionWithChar;
import com.inamik.text.tables.cell.base.FunctionWithCharAndHeight;
import com.inamik.text.tables.cell.base.FunctionWithCharAndWidth;
import com.inamik.text.tables.cell.base.FunctionWithCharAndWidthAndHeight;
import com.inamik.text.tables.cell.base.FunctionWithHeight;
import com.inamik.text.tables.cell.base.FunctionWithWidth;
import com.inamik.text.tables.cell.base.FunctionWithWidthAndHeight;
import com.inamik.text.tables.cell.TopAlign;
import com.inamik.text.tables.cell.TopPad;
import com.inamik.text.tables.cell.TopTruncate;
import com.inamik.text.tables.cell.VerticalCenter;
import com.inamik.text.tables.line.HorizontalCenter;
import com.inamik.text.tables.line.LeftAlign;
import com.inamik.text.tables.line.LeftPad;
import com.inamik.text.tables.line.LeftTruncate;
import com.inamik.text.tables.line.RightAlign;
import com.inamik.text.tables.line.RightPad;
import com.inamik.text.tables.line.RightTruncate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Cell
{
    public static final Collection<String> EMPTY = Collections.<String>emptyList();

    public static  Collection<String> of() { return EMPTY; }

    public static Collection<String> of(String...cell) {
        return Arrays.asList(cell);
    }

    public static Collection<String> append(Collection<String> cell, String...lines) {
        List<String> r = new ArrayList<String>(cell.size() + lines.length);
        r.addAll(cell);
        for (String line: lines) {
            r.add(line);
        }
        return r;
    }

    public static Collection<String> append(Collection<String> cell, Collection<String> lines) {
        List<String> r = new ArrayList<String>(cell.size() + lines.size());
        r.addAll(cell);
        r.addAll(lines);
        return r;
    }

    /*
     * Function
     */
    public static abstract class Function
    {
        public abstract Collection<String> apply(Collection<String> cell);
        public abstract Collection<String> apply(Integer width, Integer height, Collection<String> cell);
        public abstract Collection<String> apply(Character character, Integer width, Integer height, Collection<String> cell);

        public abstract Function withChar  (char character);
        public abstract Function withWidth (int  width );
        public abstract Function withHeight(int  height);

        public static final Function IDENTITY = new Function() {
            @Override
            public Function withChar(char _1) { return this; }
            @Override
            public Function withWidth(int _1) { return this; }
            @Override
            public Function withHeight(int _1) { return this; }
            @Override
            public Collection<String> apply(Collection<String> cell) { return cell; }
            @Override
            public Collection<String> apply(Integer _1, Integer _2, Collection<String> cell) { return cell; }
            @Override
            public Collection<String> apply(Character _1, Integer _2, Integer _3, Collection<String> cell) { return cell; }
        };

        /*
         * From cell.Function
         */
        public static Function from(final com.inamik.text.tables.cell.base.Function f) {
            return of(
                new FunctionWithCharAndWidthAndHeight() {
                    @Override
                    public Collection<String> apply(Character _1, Integer _2, Integer _3, Collection<String> cell) {
                        return f.apply(cell);
                    }
                },
                null, null, null
            );
        }

        /*
         * From cell.FunctionWithChar
         */
        public static Function from(final FunctionWithChar f) {
            return of(
                new FunctionWithCharAndWidthAndHeight() {
                    @Override
                    public Collection<String> apply(Character character, Integer _2, Integer _3, Collection<String> cell) {
                        return f.apply(character, cell);
                    }
                },
                null, null, null
            );
        }

        /*
         * From cell.FunctionWithWidth
         */
        public static Function from(final FunctionWithWidth f) {
            return of(
                new FunctionWithCharAndWidthAndHeight() {
                    @Override
                    public Collection<String> apply(Character _1, Integer width, Integer _3, Collection<String> cell) {
                        return f.apply(width, cell);
                    }
                },
                null, null, null
            );
        }

        /*
         * From cell.FunctionWithHeight
         */
        public static Function from(final FunctionWithHeight f) {
            return of(
                new FunctionWithCharAndWidthAndHeight() {
                    @Override
                    public Collection<String> apply(Character _1, Integer _2, Integer height, Collection<String> cell) {
                        return f.apply(height, cell);
                    }
                },
                null, null, null
            );
        }

        /*
         * From cell.FunctionWithCharAndWidth
         */
        public static Function from(final FunctionWithCharAndWidth f) {
            return of(
                new FunctionWithCharAndWidthAndHeight() {
                    @Override
                    public Collection<String> apply(Character character, Integer width, Integer _3, Collection<String> cell) {
                        return f.apply(character, width, cell);
                    }
                },
                null, null, null
            );
        }

        /*
         * From cell.FunctionWithCharAndHeight
         */
        public static Function from(final FunctionWithCharAndHeight f) {
            return of(
                new FunctionWithCharAndWidthAndHeight() {
                    @Override
                    public Collection<String> apply(Character character, Integer _2, Integer height, Collection<String> cell) {
                        return f.apply(character, height, cell);
                    }
                },
                null, null, null
            );
        }

        /*
         * From cell.FunctionWithWidthAndHeight
         */
        public static Function from(final FunctionWithWidthAndHeight f) {
            return of(
                new FunctionWithCharAndWidthAndHeight() {
                    @Override
                    public Collection<String> apply(Character _1, Integer width, Integer height, Collection<String> cell) {
                        return f.apply(width, height, cell);
                    }
                },
                null, null, null
            );
        }

        /*
         * From cell.FunctionWithCharAndWidthAndHeight
         */
        public static Function from(FunctionWithCharAndWidthAndHeight f) {
            return of(f, null, null, null);
        }

        private static Function of(final FunctionWithCharAndWidthAndHeight f, final Character character, final Integer width, final Integer height) {
            return new Function() {
                @Override
                public Function withChar(char character) { return of(f, character, width, height); }
                @Override
                public Function withWidth(int width) { return of(f, character, width, height); }
                @Override
                public Function withHeight(int height) { return of(f, character, width, height); }
                @Override
                public Collection<String> apply(Collection<String> cell) {
                    return apply(' ' , null, null, cell);
                }
                @Override
                public Collection<String> apply(Integer w, Integer h, Collection<String> cell) {
                    return apply(' ' , w, h, cell);
                }
                @Override
                public Collection<String> apply(Character c, Integer w, Integer h, Collection<String> cell) {
                    c = (null == character) ? c : character;
                    w = (null == width    ) ? w : width;
                    h = (null == height   ) ? h : height;
                    return f.apply(c, w, h, cell);
                }
            };
        }

    }
    
    public static abstract class FullPadding extends Function
    {
        public abstract FullPadding fullPad  (int pad      );
        public abstract FullPadding leftPad  (int leftPad  );
        public abstract FullPadding rightPad (int rightPad );
        public abstract FullPadding topPad   (int topPad   );
        public abstract FullPadding bottomPad(int bottomPad);

        public static final FullPadding INSTANCE = of(0,0,0,0, null, null, null);
        
        public static FullPadding of(final int leftPad, final int rightPad, final int topPad, final int bottomPad, final Character character, final Integer width, final Integer height) {
            return new FullPadding() {
                @Override
                public FullPadding fullPad   (int pad       ) { return of (   pad,      pad,    pad,       pad, character, width, height); }
                @Override
                public FullPadding leftPad   (int leftPad   ) { return of(leftPad, rightPad, topPad, bottomPad, character, width, height); }
                @Override
                public FullPadding rightPad  (int rightPad  ) { return of(leftPad, rightPad, topPad, bottomPad, character, width, height); }
                @Override
                public FullPadding topPad    (int topPad    ) { return of(leftPad, rightPad, topPad, bottomPad, character, width, height); }
                @Override
                public FullPadding bottomPad (int bottomPad ) { return of(leftPad, rightPad, topPad, bottomPad, character, width, height); }
                @Override
                public FullPadding withChar  (char character) { return of(leftPad, rightPad, topPad, bottomPad, character, width, height); }
                @Override
                public FullPadding withWidth (int width     ) { return of(leftPad, rightPad, topPad, bottomPad, character, width, height); }
                @Override
                public FullPadding withHeight(int height    ) { return of(leftPad, rightPad, topPad, bottomPad, character, width, height); }
                @Override
                public Collection<String> apply(Collection<String> cell) {
                    return apply(' ', null, null, cell);
                }
                @Override
                public Collection<String> apply(Integer w, Integer h, Collection<String> cell) {
                    return apply(' ', w, h, cell);
                }
                @Override
                public Collection<String> apply(Character c, Integer w, Integer h, Collection<String> cell) {
                    c = (null == character) ? ' ' : character;
                    w = (null == width    ) ? w   : width;
                    h = (null == height   ) ? h   : height;

                    cell = FunctionWithWidth       .from(LeftTruncate .INSTANCE).apply(Math.max(0, w - leftPad           ), cell);
                    cell = FunctionWithWidth       .from(RightTruncate.INSTANCE).apply(Math.max(0, w - leftPad - rightPad), cell);

                    cell = FunctionWithCharAndWidth.from(LeftPad      .INSTANCE).apply(c, Math.max(leftPad , w - rightPad), cell);
                    cell = FunctionWithCharAndWidth.from(RightPad     .INSTANCE).apply(c, Math.max(rightPad, w)           , cell);

                    cell = TopTruncate   .INSTANCE.apply(Math.max(0, h - topPad            ), cell);
                    cell = BottomTruncate.INSTANCE.apply(Math.max(0, h - topPad - bottomPad), cell);

                    cell = TopPad        .INSTANCE.apply(Math.max(topPad   , h - bottomPad), cell);
                    cell = BottomPad     .INSTANCE.apply(Math.max(bottomPad, h)            , cell);

                    return cell;
                }
            };
        }

    }

    /*
     * Functions
     * isolated in separate class so you can statically import them
     */
    public static final class Functions
    {
        public static final FullPadding FULL_PADDING = FullPadding.INSTANCE;

        public static final Function VERTICAL_CENTER = Function.from(VerticalCenter.INSTANCE);
        public static final Function BOTTOM_ALIGN    = Function.from(BottomAlign   .INSTANCE);
        public static final Function BOTTOM_PAD      = Function.from(BottomPad     .INSTANCE);
        public static final Function TOP_ALIGN       = Function.from(TopAlign      .INSTANCE);
        public static final Function TOP_PAD         = Function.from(TopPad        .INSTANCE);
        public static final Function BOTTOM_TRUNCATE = Function.from(BottomTruncate.INSTANCE);
        public static final Function TOP_TRUNCATE    = Function.from(TopTruncate   .INSTANCE);

        public static final Function HORIZONTAL_CENTER = Function.from(FunctionWithCharAndWidth.from(HorizontalCenter.INSTANCE));
        public static final Function LEFT_ALIGN        = Function.from(FunctionWithCharAndWidth.from(LeftAlign       .INSTANCE));
        public static final Function RIGHT_ALIGN       = Function.from(FunctionWithCharAndWidth.from(RightAlign      .INSTANCE));
        public static final Function LEFT_PAD          = Function.from(FunctionWithCharAndWidth.from(LeftPad         .INSTANCE));
        public static final Function RIGHT_PAD         = Function.from(FunctionWithCharAndWidth.from(RightPad        .INSTANCE));
        public static final Function LEFT_TRUNCATE     = Function.from(FunctionWithWidth       .from(LeftTruncate    .INSTANCE));
        public static final Function RIGHT_TRUNCATE    = Function.from(FunctionWithWidth       .from(RightTruncate   .INSTANCE));
    }

}
