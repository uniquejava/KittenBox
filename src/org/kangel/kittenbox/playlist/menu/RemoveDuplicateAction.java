package org.kangel.kittenbox.playlist.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.config.Default;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListPanel;

public class RemoveDuplicateAction extends Action {
    private Logger log = Logger.getLogger(RemoveDuplicateAction.class);

    public RemoveDuplicateAction() {
        super();
        setText("移除重复的");
        setToolTipText("移除重复的");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/remove_du.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        PlayListPanel pp = box.getPlayListPanel();
        final List<PlayListBean> playList = pp.getPlayList();
        final List<PlayListBean> clearedList = new ArrayList<PlayListBean>();

        Map<String, PlayListBean> m = new HashMap<String, PlayListBean>();

        for (Iterator it = playList.iterator(); it.hasNext();) {
            PlayListBean playListBean = (PlayListBean) it.next();
            if (!m.containsKey(playListBean.getPath())) {
                clearedList.add(playListBean);
                m.put(playListBean.getPath(), null);
            }
        }
        int removecount = playList.size() - clearedList.size();
        if (removecount > 0) {
            MessageDialog.openInformation(box.getShell(), Default.PRODUCT_NAME, "共移除" + removecount + "首重复歌曲.");
            pp.setPlayListAndRefreshViewer(clearedList);
        }
    }
}
