package org.kangel.kittenbox.playlist;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class PlayListLabelProvider implements ITableLabelProvider {
    public PlayListLabelProvider() {

    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        PlayListBean bean = (PlayListBean) element;
        switch (columnIndex) {
        case 0:
            return bean.getName();
        case 1:
            return bean.getPath();
        default:
            return "";
        }
    }

    public void addListener(ILabelProviderListener listener) {

    }

    public void dispose() {

    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {

    }
}