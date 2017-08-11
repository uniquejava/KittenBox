package org.kangel.kittenbox.playlist.menu;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListPanel;
import org.kitten.core.io.FileHelper;
import org.kitten.core.io.IFileProcessor;
import org.kitten.core.io.MyFilenameFilter;

public class AddFolderAction extends Action {
    public AddFolderAction() {
        super();
        setText("添加目录");
        setToolTipText("添加目录");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/addfolder.gif")));
    }

    public void run() {
        PlayListPanel pp = KittenBox.getApp().getPlayListPanel();
        final List<PlayListBean> playList = pp.getPlayList();
        TreeViewer tv = pp.getTreeViewer();

        DirectoryDialog fd = new DirectoryDialog(KittenBox.getApp().getShell(), SWT.OPEN);
        String f = fd.open();
        if (f != null) {
            try {
                MyFilenameFilter mff = new MyFilenameFilter(MyFilenameFilter.ENDS_WITH, true);
                mff.addExtension(".mp3");
                IFileProcessor p = new IFileProcessor() {
                    public void processFile(File ff) throws Exception {
                        PlayListBean bean = new PlayListBean();
                        bean.setName(ff.getName());
                        bean.setPath(ff.getAbsolutePath());
                        playList.add(bean);
                    }
                };
                FileHelper.processFiles(f, mff, p, true);

            } catch (Exception e) {
                e.printStackTrace();
            }
            tv.setInput(playList);
            tv.refresh();
        }
    }
}
