/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.java;

import java.util.List;

import org.jboss.forge.parser.Internal;
import org.jboss.forge.parser.Origin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Annotation<O extends JavaSource<O>> extends Internal, Origin<O>
{
   boolean isSingleValue();

   boolean isMarker();

   boolean isNormal();

   String getName();

   String getQualifiedName();

   <T extends Enum<T>> T getEnumValue(Class<T> type);

   <T extends Enum<T>> T getEnumValue(Class<T> type, String name);

   String getLiteralValue();

   String getLiteralValue(String name);

   List<ValuePair> getValues();

   String getStringValue();

   String getStringValue(String name);

   Annotation<O> removeValue(String name);

   Annotation<O> removeAllValues();

   Annotation<O> setName(String className);

   Annotation<O> setEnumValue(String name, Enum<?> value);

   Annotation<O> setEnumValue(Enum<?>... value);

   Annotation<O> setLiteralValue(String value);

   Annotation<O> setLiteralValue(String name, String value);

   Annotation<O> setStringValue(String value);

   Annotation<O> setStringValue(String name, String value);
}
