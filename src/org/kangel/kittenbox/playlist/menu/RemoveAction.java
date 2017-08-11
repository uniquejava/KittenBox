package org.kangel.kittenbox.playlist.menu;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListPanel;
import org.kangel.kittenbox.util.ItemUtil;

public class RemoveAction extends Action {
    public RemoveAction() {
        super();
        setText("从播放列表中移除");
        setToolTipText("从播放列表中移除");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/subtract.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        PlayListPanel pp = box.getPlayListPanel();
        final List<PlayListBean> playList = pp.getPlayList();
        Tree t = pp.getTreeViewer().getTree();
        int xx[] = ItemUtil.getSelectionIndices(t);
        for (int i = 0; i < xx.length; i++) {
            playList.remove(xx[i] - i);
        }
        // while (tv.getTree().getSelectionCount() > 0) {
        // s = (StructuredSelection) tv.getSelection();
        // PlayListBean b = (PlayListBean) s.getFirstElement();
        // playList.remove(b);
        // tv.remove(b);
        // }
        if (xx.length > 0) {
            pp.setPlayListAndRefreshViewer(playList);
        }
    }
}
