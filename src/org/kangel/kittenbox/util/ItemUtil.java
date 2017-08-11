package org.kangel.kittenbox.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.kangel.kittenbox.playlist.PlayListBean;

public class ItemUtil {
    public static int getSelectionIndex(Tree t) {
        return getTreeItemIndex(t.getSelection()[0]);
    }

    public static int[] getSelectionIndices(Tree t) {
        TreeItem[] items = t.getSelection();
        int[] ret = null;
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < items.length; i++) {
            list.add(getTreeItemIndex(items[i]));
        }
        ret = new int[list.size()];
        int j = 0;
        for (Iterator<Integer> it = list.iterator(); it.hasNext();) {
            Integer integer = it.next();
            ret[j] = integer;
            j++;
        }
        return ret;

    }

    public static int getTreeItemIndex(TreeItem item) {
        Tree t = item.getParent();
        for (int i = 0; i < t.getItemCount(); i++) {
            if (item == t.getItem(i)) {
                return i;
            }
        }
        return 0;
    }

    public static String[] getSelectionPaths(TreeViewer tv) {
        TreeSelection s = (TreeSelection) tv.getSelection();
        String[] ret = null;
        List<String> list = new ArrayList<String>();
        for (Iterator it = s.iterator(); it.hasNext();) {
            PlayListBean bean = (PlayListBean) it.next();
            list.add(bean.getPath());
        }
        ret = new String[list.size()];
        list.toArray(ret);
        return ret;

    }

    public static PlayListBean getSelectedBean(TableViewer tv) {
        IStructuredSelection s = (IStructuredSelection) tv.getSelection();
        PlayListBean pb = (PlayListBean) s.getFirstElement();
        return pb;
    }

    public static PlayListBean getSelectedBean(TreeViewer tv) {
        IStructuredSelection s = (IStructuredSelection) tv.getSelection();
        PlayListBean pb = (PlayListBean) s.getFirstElement();
        return pb;
    }

    public static int getTableItemIndex(TableItem item) {
        Table t = item.getParent();
        for (int i = 0; i < t.getItemCount(); i++) {
            if (item == t.getItem(i)) {
                return i;
            }
        }
        return 0;
    }
}
