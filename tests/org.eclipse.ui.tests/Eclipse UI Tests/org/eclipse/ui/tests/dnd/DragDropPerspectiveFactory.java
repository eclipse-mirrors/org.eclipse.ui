/*******************************************************************************
 * Copyright (c) 2004, 2021 IBM Corporation and others.
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
package org.eclipse.ui.tests.dnd;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

/**
 * @since 3.0
 */
public class DragDropPerspectiveFactory implements IPerspectiveFactory {

	// Id's needed by the 'Detached Window' Drag / Drop tests
	public static final String viewFolderId = "oorg.eclipse.ui.test.dnd.detached.MockFolder1";

	public static final String dropViewId1 = "org.eclipse.ui.tests.api.MockViewPart";
	public static final String dropViewId2 = "org.eclipse.ui.tests.api.MockViewPart2";
	public static final String dropViewId3 = "org.eclipse.ui.tests.api.MockViewPart3";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String folderId = "org.eclipse.ui.test.dnd.mystack";

		IFolderLayout folder = layout.createFolder(folderId,
				IPageLayout.BOTTOM, 0.5f, IPageLayout.ID_EDITOR_AREA);
		folder.addView(IPageLayout.ID_OUTLINE);
		folder.addView(IPageLayout.ID_PROBLEM_VIEW);
		folder.addView(IPageLayout.ID_PROP_SHEET);

		layout.addView(IPageLayout.ID_PROBLEM_VIEW, IPageLayout.LEFT, 0.5f,
				IPageLayout.ID_EDITOR_AREA);

		// Extra stacks and views that will be shown and detached during the 'Detached Window' tests
		IPlaceholderFolderLayout folder2 = layout.createPlaceholderFolder(viewFolderId,
				IPageLayout.RIGHT, 0.5f, IPageLayout.ID_EDITOR_AREA);
		folder2.addPlaceholder(dropViewId1);
		folder2.addPlaceholder(dropViewId2);

		layout.addPlaceholder(dropViewId3, IPageLayout.BOTTOM, 0.5f, viewFolderId);
	}
}
