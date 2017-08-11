package org.kangel.kittenbox.playlist.menu;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.util.ItemUtil;

public class CopyFileAction extends Action {
    public CopyFileAction() {
        super();
        setText("复制文件");
        setToolTipText("复制文件");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/copy.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        // StructuredSelection s = (StructuredSelection) box.getPlayListPanel()
        // .getTreeViewer().getSelection();
        // PlayListBean bean = (PlayListBean) s.getFirstElement();
        Clipboard clipboard = new Clipboard(box.getShell().getDisplay());

        FileTransfer transfer = FileTransfer.getInstance();
        clipboard.setContents(new Object[] { ItemUtil.getSelectionPaths(box.getPlayListPanel().getTreeViewer()) },
                new Transfer[] { transfer });
        clipboard.dispose();
    }
}
