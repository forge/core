/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
