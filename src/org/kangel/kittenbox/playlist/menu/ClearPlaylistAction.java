package org.kangel.kittenbox.playlist.menu;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListPanel;

public class ClearPlaylistAction extends Action {
    public ClearPlaylistAction() {
        super();
        setText("清空播放列表");
        setToolTipText("清空播放列表");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/clear.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        boolean sure = MessageDialog.openConfirm(box.getShell(), "kangle提示",
                "确认要清空播放列表吗?\n清空后您可以添加文件或目录或直接拖拽mp3文件到列表框.");
        if (sure) {
            PlayListPanel pp = box.getPlayListPanel();
            pp.setPlayListAndRefreshViewer(new ArrayList<PlayListBean>());
        }
    }
}
