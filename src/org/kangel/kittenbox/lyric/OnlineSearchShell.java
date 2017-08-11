package org.kangel.kittenbox.lyric;

import java.io.File;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.lyric.baidu.BaiduUtil;
import org.kangel.kittenbox.lyric.baidu.LyricAddress;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListContentProvider;
import org.kangel.kittenbox.playlist.PlayListLabelProvider;
import org.kangel.kittenbox.pref.PrefKeys;
import org.kangel.kittenbox.util.Hyperlink;
import org.kangel.kittenbox.util.ItemUtil;
import org.kitten.core.io.FileHelper;
import org.kitten.core.util.ErrorUtil;
import org.kitten.core.util.StringUtil;

/**
 * 在线搜索歌词窗口。
 * 
 * @author cyper.yin(uniquejava@gmail.com)
 * @version 1.0
 * @since 2010-1-9
 */
public class OnlineSearchShell {
    private static Logger log = Logger.getLogger(OnlineSearchShell.class);
    private Shell shell;
    private Combo serverCombo;
    private Text artistBox;
    private Text titleBox;
    private Text saveAsBox;
    private Table table;
    private TableViewer tableViewer;
    private Button btnDownload;
    private Button btnCheck;
    private PlayListBean playingBean;

    public OnlineSearchShell(final PlayListBean playingBean) {
        this.playingBean = playingBean;

        shell = new Shell();

        shell.setLayout(new GridLayout());

        // 第一行选择歌词服务器.
        Composite line1 = new Composite(shell, SWT.NONE);
        createServerCombo(line1);

        // 第二行输入歌手和歌名，包括搜索按钮.
        Composite line2 = new Composite(shell, SWT.NONE);
        createSearchButton(line2);

        // 第三行表格上面的那一行文字
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        Label follow = new Label(shell, SWT.NONE);
        follow.setText("选择搜索到的歌词文件进行下载:");
        follow.setLayoutData(gd);

        // 第四行表格
        createTable();

        // 第五行保存为
        Composite line5 = new Composite(shell, SWT.NONE);
        line5.setLayout(new GridLayout(2, false));
        line5.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        new Label(line5, SWT.NONE).setText("保存为:");
        saveAsBox = new Text(line5, SWT.BORDER);
        saveAsBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // 最下面一排按钮
        createBottomLine();

        shell.setSize(380, 370);
        shell.setText("在线搜索并下载歌词");

        shell.layout();
    }

    /**
     * 第一行选择歌词服务器.
     * 
     * @param line1
     */
    private void createServerCombo(Composite line1) {
        line1.setLayout(new GridLayout(3, false));
        line1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        new Label(line1, SWT.NONE).setText("歌词服务器:");

        serverCombo = new Combo(line1, SWT.READ_ONLY);
        serverCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        serverCombo.add("百度mp3搜索 (URL分析)");
        serverCombo.add("千千静听服务器");
        serverCombo.select(0);

        Hyperlink link = new Hyperlink(line1, SWT.NONE);
        link.setText("代理设置");
        link.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                KittenBox.getApp().openPrefDialog(getShell(), "networkPage");
            }
        });
    }

    /**
     * 第二行输入歌手和歌名，创建搜索按钮.
     * 
     * @param line2
     */
    private void createSearchButton(Composite line2) {
        line2.setLayout(new GridLayout(5, false));
        line2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        new Label(line2, SWT.NONE).setText("歌手:");
        artistBox = new Text(line2, SWT.BORDER);

        String name = playingBean.getName().substring(0, playingBean.getName().length() - 4);
        String ar = "";
        String title = name;
        if (name.indexOf("-") != -1) {
            ar = name.split("-")[0].trim();
            title = name.split("-")[1].trim();
        }

        artistBox.setText(ar);
        artistBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(line2, SWT.NONE).setText("歌名:");
        titleBox = new Text(line2, SWT.BORDER);
        titleBox.setText(title);
        titleBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        final Button searchBtn = new Button(line2, SWT.NONE);
        searchBtn.setText("搜索");
        searchBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                new Thread() {
                    String artist = null;
                    String title = null;
                    String key = null;

                    public void run() {
                        // [1]获取参数
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                artist = artistBox.getText();
                                title = titleBox.getText();
                                key = artist + " " + title;
                                searchBtn.setEnabled(false);
                            }
                        });
                        // [3]处理后事
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                // [2]处理耗时操作
                                String msg = null;
                                final List<PlayListBean> playList = new ArrayList<PlayListBean>();
                                try {
                                    List<LyricAddress> addrList = BaiduUtil.getBaiduLyricUrlByKey(key);
                                    for (LyricAddress addr : addrList) {
                                        PlayListBean bean = new PlayListBean();
                                        bean.setName(addr.getFileName());
                                        bean.setPath(addr.getDownloadUrl());
                                        playList.add(bean);
                                    }
                                } catch (NoRouteToHostException ex) {
                                    log.error(ex.getMessage());
                                    msg = "请检查网络或代理服务器设置\n" + ex.getMessage();
                                } catch (UnknownHostException ex) {
                                    log.error(ex.getMessage());
                                    msg = "请检查网络或代理服务器设置\n无法识别的主机地址:" + ex.getMessage();
                                } catch (Throwable ex) {
                                    log.error(ErrorUtil.getError(ex));
                                    msg = ex.getMessage();
                                }
                                if (msg != null) {
                                    MessageDialog.openInformation(shell, "提示", msg);
                                }
                                tableViewer.setInput(playList);
                                tableViewer.refresh();
                                searchBtn.setEnabled(true);
                                btnDownload.setEnabled(false);
                            }
                        });
                    }
                }.start();
            }
        });
    }

    /**
     * 第四行表格
     */
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
        tcPrefix.setText("歌曲");

        TableColumn tcNumber = new TableColumn(table, SWT.NULL);
        tcNumber.setText("下载地址");

        tcPrefix.setWidth(120);
        tcNumber.setWidth(220);

        tableViewer = new TableViewer(table);
        tableViewer.setContentProvider(new PlayListContentProvider());
        tableViewer.setLabelProvider(new PlayListLabelProvider());
        table.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                PlayListBean bean = ItemUtil.getSelectedBean(tableViewer);
                saveAsBox.setText(StringUtil.nvl(bean.getName()));
                btnDownload.setEnabled(true);
            }
        });
    }

    /**
     * 最下面一排按钮，包括"下载"和"关闭"
     */
    private void createBottomLine() {
        Composite line6 = new Composite(shell, SWT.NONE);
        line6.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        line6.setLayout(new GridLayout(4, false));

        btnCheck = new Button(line6, SWT.CHECK);
        btnCheck.setSelection(true);
        new Label(line6, SWT.NONE).setText("下载后与歌曲文件进行关联");

        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.horizontalAlignment = GridData.CENTER;
        btnDownload = new Button(line6, SWT.NONE);
        btnDownload.setText("下 载");
        btnDownload.setImage(new Image(Display.getDefault(), "icon/lrc_yes.jpg"));
        btnDownload.setLayoutData(gd2);
        btnDownload.setEnabled(false);
        btnDownload.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                new Thread() {
                    LrcFile lrcFile = null;
                    PlayListBean pb = null;
                    String dir = null;
                    String lrcSavePath = null;

                    public void run() {
                        // [1]获取参数
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                btnDownload.setEnabled(false);
                                if (StringUtil.isBlank(saveAsBox.getText())) {
                                    MessageDialog.openInformation(getShell(), "提示", "请先填写歌词文件的名字");
                                    saveAsBox.setFocus();
                                    return;
                                }
                                lrcFile = new LrcFile();
                                IStructuredSelection s = (IStructuredSelection) tableViewer.getSelection();
                                pb = (PlayListBean) s.getFirstElement();
                                dir = KittenBox.getApp().getPs().getString(PrefKeys.LYRICS_DIR);
                                lrcSavePath = dir + "/baidu/" + saveAsBox.getText();
                            }
                        });
                        // [2]处理耗时操作
                        if (pb != null) {
                            Display.getDefault().asyncExec(new Runnable() {
                                public void run() {
                                    String msg = null;

                                    try {
                                        String lyricStr = BaiduUtil.downloadLyricFromUrl(pb.getPath());
                                        new File(dir + "/baidu").mkdirs();

                                        FileHelper.setFileContent(new File(lrcSavePath), lyricStr, "GBK");
                                        if (btnCheck.getSelection()) {
                                            lrcFile.read(lrcSavePath);
                                            playingBean.setLrcFile(lrcFile);
                                            KittenBox.getApp().setLrcFile(lrcFile);
                                            // 保存
                                            LyricMapping mapping = LyricMapping.getInstance();
                                            mapping.put(playingBean.getName(), lrcSavePath);
                                        }
                                    } catch (Exception e1) {
                                        log.error(ErrorUtil.getError(e1));
                                        msg = e1.getMessage();
                                    }

                                    if (msg != null) {
                                        MessageDialog.openError(getShell(), "不好拉", msg);
                                    } else {
                                        MessageDialog.openInformation(getShell(), "恭喜", "歌词已成功保存至" + lrcSavePath);
                                    }
                                }

                            });
                        }
                        // [3]处理后事
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                btnDownload.setEnabled(true);
                            }
                        });
                    }
                }.start();

            }

        });

        Button cancelBtn = new Button(line6, SWT.NONE);
        cancelBtn.setText("关 闭");
        GridData gd3 = new GridData(GridData.FILL_HORIZONTAL);
        gd3.horizontalAlignment = GridData.CENTER;
        cancelBtn.setLayoutData(gd3);
        cancelBtn.setImage(new Image(Display.getDefault(), "icon/lrc_cancel.jpg"));
        cancelBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                getShell().dispose();
            }

        });
    }

    public void open() {
        shell.open();
    }

    public Shell getShell() {
        return this.shell;
    }
}
