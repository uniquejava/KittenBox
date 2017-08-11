package org.kangel.kittenbox.playlist.menu;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.config.Default;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListPanel;
import org.kangel.kittenbox.util.ItemUtil;

public class RemovePhysicalAction extends Action {
    public RemovePhysicalAction() {
        super();
        setText("从硬盘上永久删除");
        setToolTipText("从硬盘上永久删除");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/trash.gif")));

    }

    public void run() {

        KittenBox box = KittenBox.getApp();
        PlayListPanel pp = box.getPlayListPanel();
        final List<PlayListBean> playList = pp.getPlayList();
        TreeViewer tv = pp.getTreeViewer();
        StructuredSelection s = (StructuredSelection) box.getPlayListPanel().getTreeViewer().getSelection();
        PlayListBean bean = (PlayListBean) s.getFirstElement();
        int curMusicIndex = pp.getCurMusicIndex();

        boolean deleteFlag = MessageDialog.openQuestion(KittenBox.getApp().getShell(), Default.PRODUCT_NAME,
                "确认从硬盘上删除<" + bean.getPath() + ">");
        if (deleteFlag) {
            if (curMusicIndex == ItemUtil.getSelectionIndex(tv.getTree())) {
                MessageDialog.openInformation(KittenBox.getApp().getShell(), Default.PRODUCT_NAME, "不能删除正在播放的文件!");
            } else {
                boolean bb = new File(bean.getPath()).delete();
                if (!bb) {
                    MessageDialog.openError(KittenBox.getApp().getShell(), Default.PRODUCT_NAME, "文件删除失败.");
                } else {
                    playList.remove(bean);
                    tv.remove(bean);
                }
            }
        }

    }
}
