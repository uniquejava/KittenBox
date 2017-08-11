package org.kangel.kittenbox.playlist;

import org.kangel.kittenbox.lyric.LrcFile;

public class PlayListBean {
    private String name;
    private String path;
    private LrcFile lrcFile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LrcFile getLrcFile() {
        return lrcFile;
    }

    public void setLrcFile(LrcFile lrcFile) {
        this.lrcFile = lrcFile;
    }
    // public int compareTo(PlayListBean o) {
    // if (MyViewerSorter.column == 0) {
    // if (MyViewerSorter.order == 0) {
    // return this.getName().compareTo(o.getName());
    // } else {
    // return (-1)*this.getName().compareTo(o.getName());
    // }
    // } else {
    // if (MyViewerSorter.order == 0) {
    // return this.getPath().compareTo(o.getPath());
    // } else {
    // return (-1)*this.getPath().compareTo(o.getPath());
    // }
    // }
    // }
}
