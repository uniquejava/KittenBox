package org.kangel.kittenbox.playlist.menu;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;

public class PlayAction extends Action {

    public PlayAction() {
        super();
        setText("播放");
        setToolTipText("播放");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/play.gif")));
    }

    public void run() {
        KittenBox.getApp().getPlayListPanel().clickMusicItem(-1);
    }
}
