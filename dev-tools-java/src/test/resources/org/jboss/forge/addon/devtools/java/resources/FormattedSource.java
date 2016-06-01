/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 * A sample source file for the code formatter preview
 */

package mypackage;

import java.util.LinkedList;

public class MyIntStack {
	private final LinkedList fStack;

	public MyIntStack() {
		fStack = new LinkedList();
	}

	public int pop() {
		return ((Integer) fStack.removeFirst()).intValue();
	}

	public void push(int elem) {
		fStack.addFirst(new Integer(elem));
	}

	public boolean isEmpty() {
		return fStack.isEmpty();
	}
}