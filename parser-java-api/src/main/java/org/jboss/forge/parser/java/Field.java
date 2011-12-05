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

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface Field<O extends JavaSource<O>> extends Member<O, Field<O>>
{
   Field<O> setName(String name);

   /**
    * Get this field's type.
    */
   String getType();

   /**
    * Get this field's fully qualified type.
    */
   String getQualifiedType();

   /**
    * Get this field's {@link Type}
    */
   Type<O> getTypeInspector();

   /**
    * Attempt to determine if this field is of the same type as the given type.
    */
   boolean isType(Class<?> type);

   /**
    * Attempt to determine if this field is of the same type as the given type.
    */
   boolean isType(String type);

   /**
    * Set the type of this {@link Field} to the given {@link Class} type. Attempt to add an import statement to this
    * field's base {@link O} if required.
    */
   Field<O> setType(Class<?> clazz);

   /**
    * Set the type of this {@link Field} to the given type. Attempt to add an import statement to this field's base
    * {@link O} if required. (Note that the given className must be fully-qualified in order to properly import required
    * classes)
    */
   Field<O> setType(String type);

   /**
    * Set the type of this {@link Field} to the given {@link JavaSource<?>} type. Attempt to add an import statement to
    * this field's base {@link O} if required.
    */
   Field<O> setType(JavaSource<?> entity);

   String getStringInitializer();

   String getLiteralInitializer();

   Field<O> setLiteralInitializer(String value);

   Field<O> setStringInitializer(String value);

   boolean isPrimitive();
}
