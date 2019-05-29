package com.rcp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.part.MultiPageEditorPart;

public class MultiWinTest extends MultiPageEditorPart {

	public static final String ID = "com.MultiWinTest"; //$NON-NLS-1$

	public MultiWinTest() {
	}

	@Override
	protected void createPages() {

	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
	}

}
