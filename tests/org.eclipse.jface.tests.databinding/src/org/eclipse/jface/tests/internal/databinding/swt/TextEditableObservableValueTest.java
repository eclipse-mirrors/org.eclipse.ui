/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthew Hall - bug 213145, 194734, 195222
 *******************************************************************************/

package org.eclipse.jface.tests.internal.databinding.swt;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.conformance.ObservableDelegateTest;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableValueContractDelegate;
import org.eclipse.jface.databinding.conformance.swt.SWTMutableObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.swt.SWTObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.util.TestCollection;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Before;
import org.junit.Test;

/**
 * @since 1.1
 */
public class TextEditableObservableValueTest extends ObservableDelegateTest {

	private Delegate delegate;
	private Text text;
	private IObservableValue observable;

	public TextEditableObservableValueTest() {
		super(new Delegate());
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		delegate = (Delegate) getObservableContractDelegate();
		observable = (IObservableValue) getObservable();
		text = delegate.text;
	}

	@Override
	protected IObservable doCreateObservable() {
		return super.doCreateObservable();
	}

	@Test
	public void testGetValue() throws Exception {
		text.setEditable(false);
		assertEquals(Boolean.valueOf(text.getEditable()), observable.getValue());

		text.setEditable(true);
		assertEquals(Boolean.valueOf(text.getEditable()), observable.getValue());
	}

	@Test
	public void testSetValue() throws Exception {
		text.setEditable(false);
		observable.setValue(Boolean.TRUE);
		assertEquals(Boolean.TRUE, Boolean.valueOf(text.getEditable()));

		observable.setValue(Boolean.FALSE);
		assertEquals(Boolean.FALSE, Boolean.valueOf(text.getEditable()));
	}

	public static void addConformanceTest(TestCollection suite) {
		suite.addTest(SWTMutableObservableValueContractTest.class, new Delegate());
		suite.addTest(SWTObservableValueContractTest.class, new Delegate());
	}

	/* package */static class Delegate extends
			AbstractObservableValueContractDelegate {
		private Shell shell;
		Text text;

		@Override
		public void setUp() {
			shell = new Shell();
			text = new Text(shell, SWT.NONE);
		}

		@Override
		public void tearDown() {
			shell.dispose();
		}

		@Override
		public IObservableValue createObservableValue(Realm realm) {
			return WidgetProperties.editable().observe(realm, text);
		}

		@Override
		public Object getValueType(IObservableValue observable) {
			return Boolean.TYPE;
		}

		@Override
		public void change(IObservable observable) {
			IObservableValue observableValue = (IObservableValue) observable;
			observableValue.setValue(createValue(observableValue));
		}

		@Override
		public Object createValue(IObservableValue observable) {
			return (Boolean.TRUE.equals(observable.getValue()) ? Boolean.FALSE
					: Boolean.TRUE);
		}
	}
}
