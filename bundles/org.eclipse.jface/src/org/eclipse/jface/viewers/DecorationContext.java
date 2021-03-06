/*******************************************************************************
 * Copyright (c) 2006, 2017 IBM Corporation and others.
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
 *******************************************************************************/
package org.eclipse.jface.viewers;

import java.util.HashMap;
import java.util.Map;

/**
 * A concrete implementation of the {@link IDecorationContext} interface,
 * suitable for instantiating.
 * <p>
 * This class is not intended to be subclassed.
 * </p>
 * @since 3.2
 */
public class DecorationContext implements IDecorationContext {

	/**
	 * Constant that defines a default decoration context that has
	 * no context ids associated with it.
	 */
	public static final IDecorationContext DEFAULT_CONTEXT = new DecorationContext();

	private final Map<String, Object> properties = new HashMap<>();

	/**
	 * Create a decoration context.
	 */
	public DecorationContext() {
	}

	@Override
	public Object getProperty(String property) {
		return properties.get(property);
	}

	@Override
	public String[] getProperties() {
		return properties.keySet().toArray(new String[properties.size()]);
	}

	/**
	 * Set the given property to the given value. Setting the value of
	 * a property to <code>null</code> removes the property from
	 * the context.
	 * @param property the property
	 * @param value the value of the property or <code>null</code>
	 * if the property is to be removed.
	 */
	public void putProperty(String property, Object value) {
		if (value == null) {
			properties.remove(property);
		} else {
			properties.put(property, value);
		}
	}
}
