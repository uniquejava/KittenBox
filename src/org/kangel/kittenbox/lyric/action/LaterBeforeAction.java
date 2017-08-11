package org.kangel.kittenbox.lyric.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.lyric.Lyric;

public class LaterBeforeAction extends Action {
    private static Logger log = Logger.getLogger(LaterBeforeAction.class);

    public LaterBeforeAction() {
        super();
        setText("其后提前0.5秒");
        setToolTipText("其后提前0.5秒");
        // setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display
        // .getDefault(), "icon/add.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        Lyric lyric = box.getCurrentLyric();
        if (lyric != null) {
            lyric.getLrcFile().saveAfterLyric(lyric, -500);
            box.reloadLrcFile();
        }
    }
}
