package org.kangel.kittenbox.lyric;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListContentProvider;
import org.kangel.kittenbox.playlist.PlayListLabelProvider;
import org.kitten.core.C;
import org.kitten.core.io.FileHelper;
import org.kitten.core.io.IFileProcessor;
import org.kitten.core.util.ErrorUtil;
import org.kitten.core.util.StringUtil;

public class LyricsShell {
    private static Logger log = Logger.getLogger(LyricsShell.class);
    private Text searchBox;
    private Shell shell;
    private Table table;
    private TableViewer tableViewer;
    private String lyricTitle;
    private PlayListBean playingBean;
    private Button mapBtn;

    public LyricsShell(String f, final PlayListBean playingBean) {
        this.lyricTitle = f;
        this.playingBean = playingBean;

        shell = new Shell();

        shell.setLayout(new GridLayout(5, false));

        // 第一行搜索框.
        createSearchBox();

        // 表格上面的那一行文字
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 5;
        Label follow = new Label(shell, SWT.NONE);
        follow.setText("从下列文件中选择一个进行关联:");
        follow.setLayoutData(gd);

        // 表格
        createTable();

        // 表格右边的四个按钮
        Button btnBrowse = new Button(shell, SWT.NONE);
        btnBrowse.setText("浏览");
        btnBrowse.setImage(new Image(Display.getDefault(), "icon/openInExplorer.gif"));
        btnBrowse.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                StructuredSelection s = (StructuredSelection) tableViewer.getSelection();
                PlayListBean bean = (PlayListBean) s.getFirstElement();
                try {
                    Runtime.getRuntime().exec(new String[] { "cmd", "/c", "explorer " + new File(bean.getPath()) });
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
            }

        });

        Button btnCtrlZ = new Button(shell, SWT.NONE);
        btnCtrlZ.setText("撤消关联");
        Button btnRename = new Button(shell, SWT.NONE);
        btnRename.setText(" 重命名");
        Button btnDelete = new Button(shell, SWT.NONE);
        btnDelete.setText("删除文件");
        new Label(shell, SWT.NONE).setText("");

        // 最下面一排按钮

        Button notMapBtn = new Button(shell, SWT.NONE);
        notMapBtn.setText("不关联歌词");
        notMapBtn.setImage(new Image(Display.getDefault(), "icon/lrc_no.jpg"));
        notMapBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                LyricMapping mapping = LyricMapping.getInstance();
                String lyricPath = mapping.get(playingBean.getName());
                if (lyricPath == null) {
                    MessageDialog.openInformation(getShell(), "kangel提醒您", "还没有歌词与【" + playingBean.getName() + "】关联");
                } else {
                    boolean sure = MessageDialog.openConfirm(getShell(), "kangel提醒您",
                            "确认要取消【" + playingBean.getName() + "】的歌词关联吗?\n" + lyricPath);
                    if (sure) {
                        mapping.remove(playingBean.getName());
                        playingBean.setLrcFile(null);
                        KittenBox.getApp().setLrcFile(null);
                        getShell().dispose();
                    }
                }
            }

        });

        GridData gd2 = new GridData();
        gd2.horizontalSpan = 3;
        gd2.horizontalAlignment = GridData.END;
        mapBtn = new Button(shell, SWT.NONE);
        mapBtn.setText("关 联");
        mapBtn.setImage(new Image(Display.getDefault(), "icon/lrc_yes.jpg"));
        mapBtn.setLayoutData(gd2);
        mapBtn.setEnabled(false);
        mapBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                LrcFile lrcFile = new LrcFile();
                IStructuredSelection s = (IStructuredSelection) tableViewer.getSelection();
                PlayListBean pb = (PlayListBean) s.getFirstElement();
                if (pb != null) {
                    log.info("从" + pb.getPath() + "读取");
                    lrcFile.read(pb.getPath() + C.FS + pb.getName());
                    playingBean.setLrcFile(lrcFile);
                    KittenBox.getApp().setLrcFile(lrcFile);
                    // 保存
                    LyricMapping mapping = LyricMapping.getInstance();
                    mapping.put(playingBean.getName(), pb.getPath() + C.FS + pb.getName());
                }
                getShell().dispose();
            }

        });

        Button cancelBtn = new Button(shell, SWT.NONE);
        cancelBtn.setText("取 消");
        cancelBtn.setImage(new Image(Display.getDefault(), "icon/lrc_cancel.jpg"));
        cancelBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                getShell().dispose();
            }

        });

        shell.setSize(400, 250);
        shell.setText("指定歌词文件");

        shell.layout();
    }

    public void open() {
        shell.open();
    }

    private void createSearchBox() {
        new Label(shell, SWT.NONE).setText("歌词标题:");

        searchBox = new Text(shell, SWT.SEARCH);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        // searchBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        searchBox.setLayoutData(gd);
        searchBox.setText(lyricTitle);

        final Button searchBtn = new Button(shell, SWT.NONE);
        searchBtn.setText("本地搜索");
        searchBtn.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                searchBtn.setEnabled(false);
                final String mypart = searchBox.getText();
                final List<PlayListBean> playList = new ArrayList<PlayListBean>();
                if (StringUtil.isNotBlank(mypart)) {
                    try {
                        IFileProcessor pro = new IFileProcessor() {
                            public void processFile(File ff) throws Exception {
                                String allLrc = ff.getName().substring(0, ff.getName().length() - 4).toUpperCase();
                                if (allLrc.indexOf(mypart.trim().toUpperCase()) != -1) {
                                    PlayListBean bean = new PlayListBean();
                                    bean.setName(ff.getName());
                                    bean.setPath(ff.getParent());
                                    playList.add(bean);
                                }
                            }
                        };
                        FileHelper.processFiles("E:/music", new String[] { ".lrc", ".LRC" }, null, pro, true);
                    } catch (Exception ex) {
                        log.error(ErrorUtil.getError(ex));
                    }
                }
                tableViewer.setInput(playList);
                tableViewer.refresh();
                searchBtn.setEnabled(true);
                mapBtn.setEnabled(false);
            }

        });
    }

    private void createTable() {

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 90;
        data.horizontalSpan = 4;
        data.verticalSpan = 5;
        table.setLayoutData(data);

        table.setLinesVisible(false);
        table.setHeaderVisible(true);

        TableColumn tcPrefix = new TableColumn(table, SWT.LEFT);
        tcPrefix.setText("文件名");

        TableColumn tcNumber = new TableColumn(table, SWT.NULL);
        tcNumber.setText("所在位置");

        tcPrefix.setWidth(120);
        tcNumber.setWidth(180);

        tableViewer = new TableViewer(table);
        tableViewer.setContentProvider(new PlayListContentProvider());
        tableViewer.setLabelProvider(new PlayListLabelProvider());
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                mapBtn.setEnabled(true);
            }

        });
    }

    public Shell getShell() {
        return shell;
    }

}
