package org.kangel.kittenbox.lyric.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.lyric.LyricsShell;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kitten.swt.util.SwtUtil;

public class MapLyricAction extends Action {
    private static Logger log = Logger.getLogger(MapLyricAction.class);

    public MapLyricAction() {
        super();
        setText("关联歌词");
        setToolTipText("关联歌词");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/add.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        PlayListBean bean = box.getPlayListPanel().getPlayList().get(box.getPlayListPanel().getCurMusicIndex());
        String title = bean.getName();
        title = title.substring(0, title.length() - 4);
        LyricsShell shell = new LyricsShell(title, bean);
        SwtUtil.center(shell.getShell());
        shell.open();
    }
}
