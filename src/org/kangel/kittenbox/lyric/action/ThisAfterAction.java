package org.kangel.kittenbox.lyric.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.lyric.Lyric;

public class ThisAfterAction extends Action {
    private static Logger log = Logger.getLogger(ThisAfterAction.class);

    public ThisAfterAction() {
        super();
        setText("本句延后0.5秒");
        setToolTipText("本句延后0.5秒");
        // setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display
        // .getDefault(), "icon/add.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        Lyric lyric = box.getCurrentLyric();
        if (lyric != null) {
            lyric.getLrcFile().saveThisLyric(lyric, 500);
            box.reloadLrcFile();
        }
    }
}
