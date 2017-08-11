package org.kangel.kittenbox.hotkey;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class HotkeyLabelProvider implements ITableLabelProvider {
    public HotkeyLabelProvider() {

    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        ShortcutsBean bean = (ShortcutsBean) element;
        switch (columnIndex) {
        case 0:
            return bean.getKey();
        case 1:
            return bean.getModifiers();
        case 2:
            return bean.isGlobal() ? "yes" : "no";
        case 3:
            return bean.getAction();
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