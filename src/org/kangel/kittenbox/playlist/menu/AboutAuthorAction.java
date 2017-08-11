package org.kangel.kittenbox.playlist.menu;

import org.eclipse.jface.action.Action;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.dialog.AboutDialog;

public class AboutAuthorAction extends Action {
    public AboutAuthorAction() {
        super();
        setText("关于作者");
        setToolTipText("关于作者");
        // setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display
        // .getDefault(), "icon/add.gif")));
    }

    public void run() {
        AboutDialog dialog = new AboutDialog(KittenBox.getApp().getShell());
        dialog.setTitle("kitten音乐盒");
        dialog.setAuthor("kangel && kitten");
        dialog.setEmail("uniquejava#gmail.com");
        dialog.setToolName("kittenbox v 0.3");
        dialog.setOther("(c) Copyright Cobe Code Studio, 2009-2017");
        dialog.open();
    }
}
