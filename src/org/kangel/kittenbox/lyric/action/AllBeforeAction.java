package org.kangel.kittenbox.lyric.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.lyric.LrcFile;

public class AllBeforeAction extends Action {
    private static Logger log = Logger.getLogger(AllBeforeAction.class);

    public AllBeforeAction() {
        super();
        setText("全部提前0.5秒");
        setToolTipText("全部提前0.5秒");
        // setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display
        // .getDefault(), "icon/add.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        LrcFile lrcFile = box.getLrcFile();
        if (lrcFile != null) {
            lrcFile.subtractOffset(500);
            lrcFile.saveOffset();
            box.reloadLrcFile();
        }
    }
}
