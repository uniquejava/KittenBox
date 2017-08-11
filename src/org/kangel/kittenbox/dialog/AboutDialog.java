package org.kangel.kittenbox.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.kangel.kittenbox.KittenBox;
import org.kitten.core.util.StringUtil;

public class AboutDialog extends Dialog {
    private String title;
    private String toolName;
    private String author;
    private String email;
    private String other;

    public AboutDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createContents(Composite parent) {
        getShell().setSize(250, 200);
        getShell().setText(StringUtil.nvl(title, "关于我们"));
        getShell().setImage(new Image(Display.getDefault(), "icon/debug.gif"));
        KittenBox.center(getShell());
        parent.setLayout(new GridLayout());
        if (StringUtil.isNotEmpty(toolName)) {
            new Label(parent, SWT.CENTER).setText(toolName);
        }

        if (StringUtil.isNotEmpty(author)) {
            new Label(parent, SWT.CENTER).setText(author);
        }

        if (StringUtil.isNotEmpty(email)) {
            new Label(parent, SWT.CENTER).setText(email);
        }
        if (StringUtil.isNotEmpty(email)) {
            new Label(parent, SWT.CENTER).setText(other);
        }

        return parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

}
