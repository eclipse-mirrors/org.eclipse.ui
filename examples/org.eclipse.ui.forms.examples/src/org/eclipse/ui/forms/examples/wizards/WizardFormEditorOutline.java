/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.ui.forms.examples.wizards;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.help.*;
import org.eclipse.help.internal.context.IStyledContext;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.*;
import org.eclipse.ui.forms.examples.internal.ExamplesPlugin;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.forms.WizardFormEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


/**
 * @author dejan
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class WizardFormEditorOutline implements IContentOutlinePage {
	private ScrolledForm form;

	private Label title;

	private Text phraseText;

	private FormText text;

	private String defaultText;

	private static final String HELP_KEY = "org.eclipse.ui.help"; //$NON-NLS-1$

	private WizardFormEditor editor;

	public WizardFormEditorOutline(WizardFormEditor editor) {
		this.editor = editor;
	}

	public void createControl(Composite parent) {
		FormToolkit toolkit = editor.getToolkit();
		// parent form
		form = toolkit.createScrolledForm(parent);
		TableWrapLayout layout = new TableWrapLayout();
		form.getBody().setLayout(layout);
		//Util.highlight(form.getBody(), SWT.COLOR_YELLOW);
		// help container. Has three colums (search, text, go)
		Composite helpContainer = toolkit.createComposite(form.getBody());
		GridLayout glayout = new GridLayout();
		glayout.numColumns = 3;
		glayout.marginWidth = glayout.marginHeight = 1;
		helpContainer.setLayout(glayout);
		helpContainer.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		toolkit.paintBordersFor(helpContainer);
		Label label = toolkit.createLabel(helpContainer, "Search");
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		phraseText = toolkit.createText(helpContainer, ""); //$NON-NLS-1$
		phraseText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button button = toolkit.createButton(helpContainer,
				"Go", SWT.PUSH); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				doSearch(phraseText.getText());
			}
		});
		button.setEnabled(false);
		phraseText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String text = phraseText.getText();
				button.setEnabled(text.length() > 0);
			}
		});
		phraseText.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				if (e.character == '\r') {
					if (button.isEnabled())
						doSearch(phraseText.getText());
				}
			}
		});
		title = toolkit.createLabel(form.getBody(), null, SWT.WRAP);
		title.setText("Context Help");
		title.setFont(JFaceResources.getHeaderFont());
		title.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		text = toolkit.createFormText(form.getBody(), true);
		text.setImage(ExamplesPlugin.IMG_HELP_TOPIC, 
				ExamplesPlugin.getDefault().getImage(ExamplesPlugin.IMG_HELP_TOPIC));
		text.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				openLink(e.getHref());
			}
		});
		text.setLayoutData(new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL));
		text.setText(defaultText, false, false);
		toolkit.paintBordersFor(form.getBody());
	}

	public Control getControl() {
		return form;
	}

	private void doSearch(String phrase) {
		try {
			String ephrase = URLEncoder.encode(phrase, "UTF-8"); //$NON-NLS-1$
			String query = "tab=search&searchWord=" + ephrase; //$NON-NLS-1$
			WorkbenchHelp.displayHelpResource(query);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e);
		}
	}

	private void handlePageActivation(Control page) {
		if (text.isDisposed())
			return;
		//title.setText("What is"	+ " \"" + part.getSite().getRegisteredName() + "\"?"); //$NON-NLS-1$ //$NON-NLS-2$
		String helpText = createContextHelp(page);
		text.setText(helpText != null ? helpText : "", helpText != null, //$NON-NLS-1$
					false);
		//form.getBody().layout();
		form.reflow(true);
	}

	private String createContextHelp(Control page) {
		String text = null;
		if (page != null) {
			if (page != null && page.isVisible() && !page.isDisposed()) {
				IContext helpContext = findHelpContext(page);
				if (helpContext != null) {
					text = formatHelpContext(helpContext);
				}
			}
		}
		return text;
	}

	private IContext findHelpContext(Control c) {
		String contextId = null;
		Control node = c;
		do {
			contextId = (String) node.getData(HELP_KEY);
			if (contextId != null)
				break;
			node = node.getParent();
		} while (node != null);
		if (contextId != null) {
			return HelpSystem.getContext(contextId);
		}
		return null;
	}

	private String formatHelpContext(IContext context) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<form>"); //$NON-NLS-1$
		sbuf.append("<p>"); //$NON-NLS-1$
		sbuf.append(decodeContextBoldTags(context));
		sbuf.append("</p>"); //$NON-NLS-1$
		IHelpResource[] links = context.getRelatedTopics();
		if (links.length > 0) {
			for (int i = 0; i < links.length; i++) {
				IHelpResource link = links[i];
				sbuf.append("<li style=\"text\" indent=\"2\">"); //$NON-NLS-1$
				sbuf.append("<img href=\""); //$NON-NLS-1$
				sbuf.append(ExamplesPlugin.getDefault().getImage(ExamplesPlugin.IMG_HELP_TOPIC));
				sbuf.append("\"/> "); //$NON-NLS-1$
				sbuf.append("<a href=\""); //$NON-NLS-1$
				sbuf.append(link.getHref());
				sbuf.append("\">"); //$NON-NLS-1$
				sbuf.append(link.getLabel());
				sbuf.append("</a>"); //$NON-NLS-1$
				sbuf.append("</li>"); //$NON-NLS-1$
			}
		}
		sbuf.append("</form>"); //$NON-NLS-1$
		return sbuf.toString();
	}

	/**
	 * Make sure to support the Help system bold tag. Help systen returns a
	 * regular string for getText(). Use internal apis for now to get bold.
	 * 
	 * @param context
	 * @return
	 */
	private String decodeContextBoldTags(IContext context) {
		String styledText;
		if (context instanceof IStyledContext) {
			styledText = ((IStyledContext) context).getStyledText();
		} else {
			styledText = context.getText();
		}
		String decodedString = styledText.replaceAll("<@#\\$b>", "<b>"); //$NON-NLS-1$ //$NON-NLS-2$
		decodedString = decodedString.replaceAll("</@#\\$b>", "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		return decodedString;
	}

	private void openLink(Object href) {
		String url = (String) href;
		if (url != null)
			WorkbenchHelp.displayHelpResource(url);
	}

	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.intro.impl.parts.IStandbyContentPart#setFocus()
	 */
	public void setFocus() {
		phraseText.setFocus();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#setActionBars(org.eclipse.ui.IActionBars)
	 */
	public void setActionBars(IActionBars actionBars) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
	}
	
	public void update() {
		IWizardPage page = editor.getCurrentPage();
		handlePageActivation(page!=null?page.getControl():null);
	}
}
