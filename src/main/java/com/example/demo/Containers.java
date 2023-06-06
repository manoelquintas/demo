/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.example.demo;


import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class Containers {
  private Containers() {
  }

  public static boolean isEmpty(Collection<?> c) {
    return c == null || c.size() == 0;
  }
  
  public static <T, C extends Collection<T>> void ifNotEmpty(C c, Consumer<C> consumer) {
    if (!isEmpty(c)) consumer.accept(c);
  }

  public static boolean isEmpty(Map<?, ?> m) {
    return m == null || m.isEmpty();
  }

  public static boolean isEmpty(Set<?> set) {
    return set == null || set.isEmpty();
  }

  public static <T> List<T> toList(Set<T> set) {
    return isEmpty(set) ? emptyList() : new ArrayList<>(set);
  }
  
  public static <T> List<T> toUnmodifiableList(Set<T> set) {
    return isEmpty(set) ? emptyList() : unmodifiableList(new ArrayList<>(set));
  }


  public static boolean isEmpty(Enumeration<?> e) {
    return e == null || !e.hasMoreElements();
  }

  public static <T> boolean isEmpty(T[] value) {
    return value == null || value.length == 0;
  }
  
  public static boolean isEmpty(byte[] value) {
    return value == null || value.length == 0;    
  }
  
  public static boolean isEmpty(int[] value) {
    return value == null || value.length == 0;    
  }

  public static boolean isEmpty(float[] value) {
    return value == null || value.length == 0;    
  }

  public static boolean isEmpty(double[] value) {
    return value == null || value.length == 0;    
  }

  public static boolean isEmpty(long[] value) {
    return value == null || value.length == 0;    
  }


  
  @SafeVarargs
  public static <T> List<T> arrayList(Predicate<T> filter, T... array) {
    return Arrays.asList(array).stream().filter(filter).collect(Collectors.toList());
  }

  
  public static <T> List<T> nonNull(List<T> list) {
    return list == null ? Collections.emptyList() : list;
  }
}
