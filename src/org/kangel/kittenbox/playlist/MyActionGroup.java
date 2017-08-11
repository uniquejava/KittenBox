package org.kangel.kittenbox.playlist;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionGroup;
import org.kangel.kittenbox.lyric.action.AllAdjustAction;
import org.kangel.kittenbox.lyric.action.AllAfterAction;
import org.kangel.kittenbox.lyric.action.AllBeforeAction;
import org.kangel.kittenbox.lyric.action.LaterAfterAction;
import org.kangel.kittenbox.lyric.action.LaterBeforeAction;
import org.kangel.kittenbox.lyric.action.MapLyricAction;
import org.kangel.kittenbox.lyric.action.OnlineSearchAction;
import org.kangel.kittenbox.lyric.action.ThisAfterAction;
import org.kangel.kittenbox.lyric.action.ThisBeforeAction;
import org.kangel.kittenbox.playlist.menu.AddFilesAction;
import org.kangel.kittenbox.playlist.menu.AddFolderAction;
import org.kangel.kittenbox.playlist.menu.ClearPlaylistAction;
import org.kangel.kittenbox.playlist.menu.CopyFileAction;
import org.kangel.kittenbox.playlist.menu.OpenInExplorerAction;
import org.kangel.kittenbox.playlist.menu.PlayAction;
import org.kangel.kittenbox.playlist.menu.RemoveAction;
import org.kangel.kittenbox.playlist.menu.RemoveDuplicateAction;
import org.kangel.kittenbox.playlist.menu.RemoveInvalidAction;
import org.kangel.kittenbox.playlist.menu.RemovePhysicalAction;

public class MyActionGroup extends ActionGroup {
    private TreeViewer tv;

    public MyActionGroup(TreeViewer tv) {
        this.tv = tv;
    }

    @Override
    public void fillContextMenu(IMenuManager mgr) {
        MenuManager mm = (MenuManager) mgr;
        mm.add(new PlayAction());
        mm.add(new OnlineSearchAction());
        mm.add(new MapLyricAction());

        MenuManager lyricMenu = new MenuManager("调整歌词",
                ImageDescriptor.createFromImage(new Image(Display.getDefault(), "icon/show_lrc.gif")), "lyricMenu");
        lyricMenu.add(new ThisBeforeAction());
        lyricMenu.add(new ThisAfterAction());
        lyricMenu.add(new LaterBeforeAction());
        lyricMenu.add(new LaterAfterAction());
        lyricMenu.add(new AllBeforeAction());
        lyricMenu.add(new AllAfterAction());
        lyricMenu.add(new AllAdjustAction());
        mm.add(lyricMenu);

        mm.add(new RemoveDuplicateAction());
        mm.add(new RemoveInvalidAction());
        mm.add(new ClearPlaylistAction());
        mm.add(new AddFilesAction());
        mm.add(new AddFolderAction());
        mm.add(new CopyFileAction());
        mm.add(new OpenInExplorerAction());
        mm.add(new RemoveAction());
        mm.add(new RemovePhysicalAction());

        Tree table = tv.getTree();
        Menu menu = mm.createContextMenu(table);
        table.setMenu(menu);

    }

}
