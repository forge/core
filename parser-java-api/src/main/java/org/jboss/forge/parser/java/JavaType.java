/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.java;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@SuppressWarnings("rawtypes")
public interface JavaType<T extends JavaSource<T>> extends JavaSource<T>,
         InterfaceCapable<T>,
         MemberHolder<T, Member>,
         FieldHolder<T>,
         MethodHolder<T>,
         Genericized<T>
{

}