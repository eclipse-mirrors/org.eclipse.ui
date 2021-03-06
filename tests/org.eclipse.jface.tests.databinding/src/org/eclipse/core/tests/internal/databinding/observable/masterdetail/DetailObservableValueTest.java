/*******************************************************************************
 * Copyright (c) 2006, 2018 Brad Reynolds and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Brad Reynolds - initial API and implementation
 *     Brad Reynolds - bug 147515
 *     Matthew Hall - bugs 221351, 213145
 *     Ovidio Mallo - bug 241318
 *     Tom Schindl - bug 287601
 ******************************************************************************/

package org.eclipse.core.tests.internal.databinding.observable.masterdetail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.internal.databinding.observable.masterdetail.DetailObservableValue;
import org.eclipse.jface.databinding.conformance.MutableObservableValueContractTest;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableValueContractDelegate;
import org.eclipse.jface.databinding.conformance.util.TestCollection;
import org.eclipse.jface.databinding.conformance.util.CurrentRealm;
import org.eclipse.jface.databinding.conformance.util.DisposeEventTracker;
import org.eclipse.jface.databinding.conformance.util.RealmTester;
import org.eclipse.jface.tests.databinding.AbstractDefaultRealmTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @since 3.2
 */
public class DetailObservableValueTest extends AbstractDefaultRealmTestCase {
	private WritableValue outerObservable;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		outerObservable = new WritableValue();
	}

	@Test
	public void testSetValue() throws Exception {
		WritableValueFactory factory = new WritableValueFactory();
		outerObservable.setValue("");

		IObservableValue detailObservable = MasterDetailObservables
				.detailValue(outerObservable, factory, null);
		WritableValue innerObservable = factory.innerObservable;
		Object value = new Object();

		assertFalse(value.equals(innerObservable.getValue()));
		detailObservable.setValue(value);
		assertEquals("inner value", value, innerObservable.getValue());
	}

	@Test
	public void testGetValueType() throws Exception {
		DetailObservableValue detailObservable = new DetailObservableValue(
				outerObservable, null, String.class);
		assertEquals(String.class, detailObservable.getValueType());
	}

	/**
	 * Asserts that when a null value type is set for the detail observable no
	 * type checking is performed and the value type is always <code>null</code>
	 * .
	 */
	@Test
	public void testGetValueTypeNullValueType() throws Exception {
		WritableValueFactory factory = new WritableValueFactory();
		DetailObservableValue detailObservable = new DetailObservableValue(
				outerObservable, factory, null);
		assertNull(detailObservable.getValueType());
		factory.type = String.class;

		// force the inner observable to be recreated
		outerObservable.setValue("1");
		assertNull("value type should be ignored", detailObservable
				.getValueType());

		factory.type = Object.class;

		// force the inner observable to be recreated
		outerObservable.setValue("2");
		assertNull("value type should be ignored", detailObservable
				.getValueType());
	}

	/**
	 * Asserts that the master observable value is not disposed upon disposing
	 * its detail observable value (bug 241318).
	 */
	@Test
	public void testMasterNotDisposedWhenDetailDisposed() {
		class OuterObservable extends WritableValue {
			boolean disposed = false;

			@Override
			public synchronized void dispose() {
				disposed = true;
				super.dispose();
			}
		}

		OuterObservable outerObservable = new OuterObservable();
		WritableValueFactory factory = new WritableValueFactory();
		DetailObservableValue detailObservable = new DetailObservableValue(
				outerObservable, factory, null);

		assertFalse(outerObservable.disposed);

		detailObservable.dispose();
		assertFalse(outerObservable.disposed);
	}

	@Test
	public void testDisposeMasterDisposesDetail() {
		IObservableValue master = new WritableValue();
		WritableValueFactory factory = new WritableValueFactory();
		master.setValue("");

		IObservableValue detailObservable = MasterDetailObservables
				.detailValue(master, factory, null);
		DisposeEventTracker tracker = DisposeEventTracker
				.observe(detailObservable);

		master.dispose();

		assertEquals(1, tracker.count);
		assertTrue(detailObservable.isDisposed());
	}

	@Test
	public void testDisposeWhileFiringEvents() {
		IObservableValue master = new WritableValue();
		WritableValueFactory factory = new WritableValueFactory();
		master.setValue("");

		final IObservableValue[] detailObservable = new IObservableValue[1];

		master.addValueChangeListener(event -> detailObservable[0].dispose());

		detailObservable[0] = MasterDetailObservables.detailValue(master,
				factory, null);

		master.setValue("New Value");
	}

	/**
	 * Factory that creates WritableValues with the target as the value.
	 */
	static class WritableValueFactory implements IObservableFactory {
		Realm realm;
		WritableValue innerObservable;
		Object type;

		@Override
		public IObservable createObservable(Object target) {
			return innerObservable = new WritableValue(realm == null ? Realm
					.getDefault() : realm, target, type);
		}
	}

	public static void addConformanceTest(TestCollection suite) {
		suite.addTest(MutableObservableValueContractTest.class, new Delegate());
	}

	private static class DetailObservableValueStub extends
			DetailObservableValue {
		IObservableValue outerObservableValue;

		DetailObservableValueStub(IObservableValue outerObservableValue,
				IObservableFactory valueFactory, Object detailType) {
			super(outerObservableValue, valueFactory, detailType);
			this.outerObservableValue = outerObservableValue;
		}
	}

	private static class Delegate extends
			AbstractObservableValueContractDelegate {
		private Object valueType;
		private Realm previousRealm;

		@Override
		public void setUp() {
			super.setUp();
			valueType = new Object();
			previousRealm = Realm.getDefault();

			RealmTester.setDefault(new CurrentRealm());
		}

		@Override
		public void tearDown() {
			RealmTester.setDefault(previousRealm);
			super.tearDown();
		}

		@Override
		public IObservableValue createObservableValue(Realm realm) {
			WritableValueFactory valueFactory = new WritableValueFactory();
			valueFactory.realm = realm;
			valueFactory.type = valueType;
			WritableValue masterObservableValue = new WritableValue(realm,
					new Object(), null);
			return new DetailObservableValueStub(masterObservableValue,
					valueFactory, valueType);
		}

		@Override
		public Object createValue(IObservableValue observable) {
			return new Object();
		}

		@Override
		public Object getValueType(IObservableValue observable) {
			return valueType;
		}

		@Override
		public void change(IObservable observable) {
			DetailObservableValueStub value = (DetailObservableValueStub) observable;
			value.outerObservableValue.setValue(createValue(value));
		}
	}
}
