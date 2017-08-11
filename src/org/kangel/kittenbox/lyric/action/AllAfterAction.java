package org.kangel.kittenbox.lyric.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.lyric.LrcFile;

public class AllAfterAction extends Action {
    private static Logger log = Logger.getLogger(AllAfterAction.class);

    public AllAfterAction() {
        super();
        setText("全部延后0.5秒");
        setToolTipText("全部延后0.5秒");
        // setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display
        // .getDefault(), "icon/add.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        LrcFile lrcFile = box.getLrcFile();
        if (lrcFile != null) {
            lrcFile.incrementOffset(500);
            lrcFile.saveOffset();
            box.reloadLrcFile();
        }
    }
}
