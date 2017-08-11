package org.kangel.kittenbox.pref;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ComponentsPage extends PreferencePage {

    public ComponentsPage() {
        super();
    }

    protected Control createContents(Composite arg0) {

        Composite bottom = new Composite(arg0, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_VERTICAL);
        gd.verticalAlignment = GridData.BEGINNING;
        bottom.setLayoutData(gd);
        bottom.setLayout(new GridLayout());

        new Label(bottom, SWT.NONE).setText("kittenbox v0.2科比超帅绝杀版");
        new Label(bottom, SWT.NONE).setText("lastUpdate: 2010-01-22 22:45");
        new Label(bottom, SWT.NONE).setText("based on: SWT vs. JFace (Eclipse 3.5)");
        new Label(bottom, SWT.NONE).setText("written by: kangel");
        new Label(bottom, SWT.NONE).setText("thanks to: kobe,wangna,ycq and my parents");
        new Label(bottom, SWT.NONE).setText("(c) Copyright 2009-2010 Cobe Code Studio ");
        new Label(bottom, SWT.NONE).setText("");
        return arg0;
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getApplyButton().setVisible(false);
        getDefaultsButton().setVisible(false);
    }

}
