/*******************************************************************************
 * Copyright (c) 2008, 2009 Code 9 Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Code 9 Corporation - initial API and implementation
 *     Chris Aniszczyk <zx@code9.com> - bug 131435
 *     Matthew Hall - bug 194734, 195222
 *         (through StyledTextObservableValueFocusOutText.java)
 *     Matthew Hall - bug 256543
 *******************************************************************************/

package org.eclipse.jface.tests.internal.databinding.swt;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableValueContractDelegate;
import org.eclipse.jface.databinding.conformance.swt.SWTMutableObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.swt.SWTObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.util.TestCollection;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;

/**
 * Tests for the DefaultSelection version of StyledTextObservableValue.
 */
public class StyledTextObservableValueDefaultSelectionTest {
	public static void addConformanceTest(TestCollection suite) {
		suite.addTest(SWTMutableObservableValueContractTest.class, new Delegate());
		suite.addTest(SWTObservableValueContractTest.class, new Delegate());
	}

	/* package */static class Delegate extends
			AbstractObservableValueContractDelegate {
		private Shell shell;

		private StyledText text;

		@Override
		public void setUp() {
			shell = new Shell();
			text = new StyledText(shell, SWT.NONE);
		}

		@Override
		public void tearDown() {
			shell.dispose();
		}

		@Override
		public IObservableValue createObservableValue(Realm realm) {
			return WidgetProperties.text(SWT.DefaultSelection).observe(realm, text);
		}

		@Override
		public Object getValueType(IObservableValue observable) {
			return String.class;
		}

		@Override
		public void change(IObservable observable) {
			IObservableValue observableValue = (IObservableValue) observable;
			text.setText((String) createValue(observableValue));

			text.notifyListeners(SWT.DefaultSelection, null);
		}

		@Override
		public Object createValue(IObservableValue observable) {
			String value = (String) observable.getValue();
			return value + "a";
		}
	}
}
