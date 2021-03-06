/*******************************************************************************
 * Copyright (c) 2012 R?diger Herrmann and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     R?diger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.jface.tests.dialogs;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class StatusDialogTest extends TestCase {

	private static final String PLUGIN_ID = "org.eclipse.ui.tests";

	private Shell shell;

	public void testEscapeAmpesandInStatusLabelBug395426() {
		TestableStatusDialog dialog = new TestableStatusDialog(shell);
		dialog.open();
		dialog.updateStatus(new Status(IStatus.ERROR, PLUGIN_ID, "&"));
		CLabel statusLabel = findStatusLabel(dialog.getShell());
		assertEquals( "&&", statusLabel.getText());
	}

	@Override
	protected void setUp() throws Exception {
		shell = new Shell();
	}

	@Override
	protected void tearDown() throws Exception {
		shell.dispose();
	}

	private CLabel findStatusLabel(Composite parent) {
		CLabel result = null;
		Control[] children = parent.getChildren();
		for (Control child : children) {
			if (child instanceof CLabel) {
				result = (CLabel) child;
			}
		}
		if (result == null) {
			for (Control child : children) {
				if (child instanceof Composite) {
					result = findStatusLabel((Composite) child);
				}
			}
		}
		return result;
	}

	public class TestableStatusDialog extends StatusDialog {

		public TestableStatusDialog(Shell parent) {
			super(parent);
			setBlockOnOpen(false);
		}

		@Override
		protected void updateStatus(IStatus status) {
			super.updateStatus(status);
		}
	}
}
