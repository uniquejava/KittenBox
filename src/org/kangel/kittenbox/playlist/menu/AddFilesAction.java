package org.kangel.kittenbox.playlist.menu;

import java.io.File;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListPanel;

public class AddFilesAction extends Action {
    public AddFilesAction() {
        super();
        setText("添加文件");
        setToolTipText("添加文件");
        setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/add.gif")));
    }

    public void run() {
        KittenBox box = KittenBox.getApp();
        PlayListPanel pp = box.getPlayListPanel();
        final List<PlayListBean> playList = pp.getPlayList();
        TreeViewer tv = pp.getTreeViewer();

        FileDialog fd = new FileDialog(KittenBox.getApp().getShell(), SWT.OPEN | SWT.MULTI);
        fd.setFilterNames(new String[] { "mp3 files(*.mp3)" });
        fd.setFilterExtensions(new String[] { "*.mp3" });

        String fn = fd.open();
        if (fn != null) {
            try {
                String[] files = fd.getFileNames();
                if (files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        StringBuffer buf = new StringBuffer();
                        buf.append(fd.getFilterPath());
                        if (buf.charAt(buf.length() - 1) != File.separatorChar) {
                            buf.append(File.separatorChar);
                        }
                        buf.append(files[i]);

                        File ff = new File(buf.toString());
                        PlayListBean bean = new PlayListBean();
                        bean.setName(ff.getName());
                        bean.setPath(ff.getAbsolutePath());
                        playList.add(bean);
                    }

                    tv.setInput(playList);
                    tv.refresh();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
