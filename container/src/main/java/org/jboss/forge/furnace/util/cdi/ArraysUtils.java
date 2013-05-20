/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.forge.furnace.util.cdi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A collection of utilities for working with Arrays
 */
abstract class ArraysUtils
{
   private ArraysUtils()
   {
      // prevent instantiation
   }

   /**
    * Create a set from an array. If the array contains duplicate objects, the last object in the array will be placed
    * in resultant set.
    * 
    * @param <T> the type of the objects in the set
    * @param array the array from which to create the set
    * @return the created sets
    */
   public static <T> Set<T> asSet(T... array)
   {
      Set<T> result = new HashSet<T>();
      Collections.addAll(result, array);
      return result;
   }
}
