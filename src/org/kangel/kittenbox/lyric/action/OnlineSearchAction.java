package org.kangel.kittenbox.lyric.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.lyric.OnlineSearchShell;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kitten.swt.util.SwtUtil;

public class OnlineSearchAction extends Action {
    public OnlineSearchAction() {
        super();
        setText("在线搜索");
        setToolTipText("在线搜索");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/element.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        PlayListBean bean = box.getPlayListPanel().getPlayList().get(box.getPlayListPanel().getCurMusicIndex());
        OnlineSearchShell shell = new OnlineSearchShell(bean);
        SwtUtil.center(shell.getShell());
        shell.open();
    }
}
