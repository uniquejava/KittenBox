package org.kangel.kittenbox.playlist.menu;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.playlist.PlayListBean;

public class OpenInExplorerAction extends Action {
    private static Logger log = Logger.getLogger(OpenInExplorerAction.class);

    public OpenInExplorerAction() {
        super();
        setText("打开目录");
        setToolTipText("打开文件所在的目录");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/openInExplorer.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        StructuredSelection s = (StructuredSelection) box.getPlayListPanel().getTreeViewer().getSelection();
        PlayListBean bean = (PlayListBean) s.getFirstElement();
        try {
            Runtime.getRuntime().exec(new String[] { "cmd", "/c", "explorer " + new File(bean.getPath()).getParent() });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
