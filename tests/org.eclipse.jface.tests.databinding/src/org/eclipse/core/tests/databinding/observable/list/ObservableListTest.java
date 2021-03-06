/*******************************************************************************
 * Copyright (c) 2006, 2018 IBM Corporation and others.
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
 *     Brad Reynolds - bug 167204
 *     Matthew Hall - bugs 208858, 213145
 *******************************************************************************/

package org.eclipse.core.tests.databinding.observable.list;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.jface.databinding.conformance.ObservableListContractTest;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableCollectionContractDelegate;
import org.eclipse.jface.databinding.conformance.util.TestCollection;
import org.eclipse.jface.databinding.conformance.util.CurrentRealm;
import org.eclipse.jface.databinding.conformance.util.RealmTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @since 3.2
 */
public class ObservableListTest {
	private ObservableListStub<Object> list;

	@Before
	public void setUp() throws Exception {
		RealmTester.setDefault(new CurrentRealm(true));

		list = new ObservableListStub<>(new ArrayList<>(0), Object.class);
	}

	@After
	public void tearDown() throws Exception {
		RealmTester.setDefault(null);
	}

	@Test
	public void testIsStaleRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(() -> list.isStale());
	}

	@Test
	public void testSetStaleRealmChecks() throws Exception {
		RealmTester.exerciseCurrent(() -> list.setStale(false));
	}

	@Test
	public void testMove_FiresListChanges() throws Exception {
		list = new MutableObservableListStub<>();
		final Object element = new Object();
		list.add(0, element);
		list.add(1, new Object());

		final List<ListDiffEntry<?>> diffEntries = new ArrayList<>();
		list.addListChangeListener(event -> diffEntries.addAll(Arrays.asList(event.diff.getDifferences())));

		list.move(0, 1);

		assertEquals(2, diffEntries.size());

		ListDiffEntry<?> entry = diffEntries.get(0);
		assertEquals(element, entry.getElement());
		assertEquals(false, entry.isAddition());
		assertEquals(0, entry.getPosition());

		entry = diffEntries.get(1);
		assertEquals(element, entry.getElement());
		assertEquals(true, entry.isAddition());
		assertEquals(1, entry.getPosition());
	}

	@Test
	public void testMove_MovesElement() throws Exception {
		list = new MutableObservableListStub<>();
		final Object element0 = new Object();
		final Object element1 = new Object();
		list.add(0, element0);
		list.add(1, element1);

		list.move(0, 1);

		assertEquals(element1, list.get(0));
		assertEquals(element0, list.get(1));
	}

	public static void addConformanceTest(TestCollection suite) {
		suite.addTest(ObservableListContractTest.class, new Delegate());
	}

	/* package */ static class Delegate extends AbstractObservableCollectionContractDelegate<String> {
		@Override
		public IObservableCollection<String> createObservableCollection(Realm realm, final int elementCount) {
			List<String> wrappedList = new ArrayList<>();
			for (int i = 0; i < elementCount; i++) {
				wrappedList.add(String.valueOf(i));
			}

			return new MutableObservableListStub<>(realm, wrappedList, String.class);
		}

		@Override
		public void change(IObservable observable) {
			@SuppressWarnings("unchecked")
			ObservableListStub<String> list = (ObservableListStub<String>) observable;
			String element = "element";
			list.wrappedList.add(element);
			list.fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(list.size(), true, element)));
		}

		@Override
		public Object getElementType(IObservableCollection<String> collection) {
			return String.class;
		}
	}

	/* package */static class ObservableListStub<E> extends ObservableList<E> {
		@SuppressWarnings("hiding")
		List<E> wrappedList;

		ObservableListStub(Realm realm, List<E> wrappedList, Object elementType) {
			super(realm, wrappedList, elementType);
			this.wrappedList = wrappedList;
		}

		ObservableListStub(List<E> wrappedList, Object elementType) {
			super(wrappedList, elementType);
			this.wrappedList = wrappedList;
		}

		@Override
		protected void fireListChange(ListDiff<E> diff) {
			super.fireListChange(diff);
		}
	}

	/* package */static class MutableObservableListStub<E> extends ObservableListStub<E> {
		MutableObservableListStub() {
			this(Realm.getDefault(), new ArrayList<>(), null);
		}

		MutableObservableListStub(Realm realm, List<E> wrappedList,
				Object elementType) {
			super(realm, wrappedList, elementType);
		}

		@Override
		public void add(int index, E element) {
			checkRealm();
			wrappedList.add(index, element);
			fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
					index, true, element)));
		}

		@Override
		public E remove(int index) {
			checkRealm();
			E element = wrappedList.remove(index);
			fireListChange(Diffs.createListDiff(Diffs.createListDiffEntry(
					index, false, element)));
			return element;
		}
	}
}
