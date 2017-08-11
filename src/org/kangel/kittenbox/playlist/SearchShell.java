package org.kangel.kittenbox.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.util.ItemUtil;
import org.kitten.core.util.StringUtil;

public class SearchShell {
    private Text searchBox;
    private Shell shell;
    private Table table;
    private TableViewer tableViewer;
    private List<PlayListBean> fullPlayList = new ArrayList<PlayListBean>();
    private List<PlayListBean> playList = new ArrayList<PlayListBean>();

    public SearchShell(final List<PlayListBean> fullPlayList) {
        this.fullPlayList = fullPlayList;

        shell = new Shell();

        shell.setLayout(new GridLayout());

        createSearchBox();
        createTable();
        setPlayList(null);
        shell.setSize(450, 400);
        shell.setText("在播放列表中查找");

        shell.layout();
    }

    public void open() {
        shell.open();
    }

    private void createSearchBox() {
        searchBox = new Text(shell, SWT.SEARCH);
        searchBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // 天啦，这里要用ModifyListener，搞死我了.
        searchBox.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String str = searchBox.getText();
                playList = new ArrayList<PlayListBean>();
                if (StringUtil.isNotEmpty(str)) {
                    for (Iterator it = fullPlayList.iterator(); it.hasNext();) {
                        PlayListBean bean = (PlayListBean) it.next();
                        if (bean.getName().toLowerCase().indexOf(str.toLowerCase()) != -1) {
                            playList.add(bean);
                        }
                    }
                }
                tableViewer.setInput(playList);
                tableViewer.refresh();
            }
        });
    }

    private void createTable() {

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 90;
        table.setLayoutData(data);

        table.setLinesVisible(false);
        table.setHeaderVisible(true);

        TableColumn tcPrefix = new TableColumn(table, SWT.LEFT);
        tcPrefix.setText("name");

        TableColumn tcNumber = new TableColumn(table, SWT.NULL);
        tcNumber.setText("path");

        tcPrefix.setWidth(150);
        tcNumber.setWidth(250);

        tableViewer = new TableViewer(table);
        tableViewer.setContentProvider(new PlayListContentProvider());
        tableViewer.setLabelProvider(new PlayListLabelProvider());
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                StructuredSelection s = (StructuredSelection) tableViewer.getSelection();
                PlayListBean bean = (PlayListBean) s.getFirstElement();

                PlayListPanel pp = KittenBox.getApp().getPlayListPanel();
                pp.getTreeViewer().setSelection(new StructuredSelection(bean), true);
                KittenBox.getApp().getPlayListPanel()
                        .clickMusicItem(ItemUtil.getSelectionIndex(pp.getTreeViewer().getTree()));
            }
        });
    }

    private void setPlayList(List<PlayListBean> bean) {
        if (bean == null) {
            String f = "c:/KittenBox.dat";
            String str = "";

            if (StringUtil.isNotEmpty(str)) {

            } else {
                tableViewer.setInput(Collections.EMPTY_LIST);
            }
            tableViewer.refresh();
        } else {
            tableViewer.setInput(bean);
        }
        tableViewer.refresh();
    }

}
