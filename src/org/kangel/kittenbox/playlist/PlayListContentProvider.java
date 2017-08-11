package org.kangel.kittenbox.playlist;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PlayListContentProvider implements ITreeContentProvider {

    public Object[] getElements(Object inputElement) {
        return ((List) inputElement).toArray();
    }

    public void dispose() {

    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    public Object[] getChildren(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getParent(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasChildren(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}