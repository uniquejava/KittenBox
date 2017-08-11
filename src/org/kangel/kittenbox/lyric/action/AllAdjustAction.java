package org.kangel.kittenbox.lyric.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.kangel.kittenbox.lyric.AllAdjustShell;
import org.kitten.swt.util.SwtUtil;

public class AllAdjustAction extends Action {
    private static Logger log = Logger.getLogger(AllAdjustAction.class);

    public AllAdjustAction() {
        super();
        setText("全部调整");
        setToolTipText("全部调整");
        // setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display
        // .getDefault(), "icon/add.gif")));
    }

    public void run() {
        AllAdjustShell s = new AllAdjustShell();
        s.getShell().setText("调整全部歌词时间");
        SwtUtil.center(s.getShell());
        s.open();
    }
}
