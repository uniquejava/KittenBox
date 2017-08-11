package org.kangel.kittenbox;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Tree;
import org.kangel.kittenbox.config.Default;
import org.kangel.kittenbox.hotkey.GlobalHotkeyManager;
import org.kangel.kittenbox.lyric.LrcFile;
import org.kangel.kittenbox.lyric.Lyric;
import org.kangel.kittenbox.lyric.LyricMapping;
import org.kangel.kittenbox.playlist.PlayListBean;
import org.kangel.kittenbox.playlist.PlayListPanel;
import org.kangel.kittenbox.playlist.SearchShell;
import org.kangel.kittenbox.playlist.menu.AboutAuthorAction;
import org.kangel.kittenbox.playlist.menu.OpenInExplorerAction;
import org.kangel.kittenbox.pref.ComponentsPage;
import org.kangel.kittenbox.pref.LyricsPage;
import org.kangel.kittenbox.pref.NetworkPage;
import org.kangel.kittenbox.pref.PlayPage;
import org.kangel.kittenbox.pref.PrefKeys;
import org.kangel.kittenbox.pref.ShortcutsPage;
import org.kangel.kittenbox.pref.WindowsPage;
import org.kangel.kittenbox.util.VolumeManager;
import org.kitten.core.C;
import org.kitten.core.io.FileHelper;
import org.kitten.core.io.ILineProcessor;
import org.kitten.core.util.ErrorUtil;
import org.kitten.core.util.Log4jUtil;
import org.kitten.core.util.StringUtil;
import org.kitten.swt.util.ResourceManager;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 * 参考链接：http://hi.baidu.com/hbeing/blog/item/1d142a13f96725d5f6039e5b.html
 * 
 * 
 * @author cyper
 * @version 1.0
 */
public class KittenBox extends ApplicationWindow implements BasicPlayerListener {
    private static Logger log = Logger.getLogger(KittenBox.class);
    private final Display display = Display.getDefault();
    public final static String env = C.UD + C.FS + "data";
    /*
     * 托盘
     */
    private final Tray tray = display.getSystemTray();
    /*
     * 唯一实例
     */
    private static KittenBox app;
    /*
     * bp表示BasicController的实现类,比接口多个了addBasicPlayerListener方法
     */
    private BasicPlayer bp;

    /*
     * player表示底层的播放控件接口
     */
    private BasicController player;

    /*
     * coolBar表示播放列表控件Composite
     */
    private CoolBar coolBar;
    /*
     * playListPanel表示播放列表控件Composite
     */
    private PlayListPanel playListPanel;

    /*
     * audioInfo表示在music被open时从中获取的音频信息。
     */
    private Map<String, Object> audioInfo;

    /*
     * scale表示播放进度条
     */
    private Slider scale;
    /*
     * voume表示音量控制
     */
    private Slider volume;

    /*
     * totalbytes表示music的总字节数
     */
    private int totalbytes = -1;

    /*
     * bytesread表示music当前已读(播放)字节数
     */
    private int bytesread;

    /*
     * 表示是否可操控移动进度条[当在拖动进度条时,程序本身不能让进度条随歌曲向前移动]
     */
    private boolean canMoveScale = true;

    private String lastContent;
    /*
     * myTrayItem表示托盘图标，提示文字应该始终和歌曲的名字保持一致。
     */
    TrayItem myTrayItem = null;

    public static final int INIT = 0;
    public static final int OPEN = 1;
    public static final int PLAY = 2;
    public static final int PAUSE = 3;
    public static final int STOP = 4;
    protected static boolean SHOW_LYRICS = false;
    protected static boolean SHOW_DESKTOP_LYRICS = false;

    /*
     * currentStatus表示当前播放器的播放状态
     */
    public int currentStatus = INIT;

    /*
     * prefDialog表示首选项对话框
     */
    private PreferenceDialog prefDialog;

    /*
     * ps表示首选项中保存的配置文件信息
     */
    private PreferenceStore ps;

    private int coolItemHeight = 0;
    private Combo comboOrder;
    private long timeLength;
    private String timeLengthStr;
    private LrcFile lrcFile;
    /**
     * 表示当前歌曲是否播放完成,解决当一首歌曲播放完成后progress()会被多次调用的问题
     */
    private boolean playComplete = false;
    /**
     * 显示/隐藏歌词按钮.
     */
    private ToolItem btnShowLyrics;
    /**
     * 显示/隐藏歌词按钮.
     */
    private ToolItem btnSlowLyrics;
    /**
     * 显示/隐藏歌词按钮.
     */
    private ToolItem btnFastLyrics;
    protected int sum;
    private int lastLrcIndex = 0;
    protected Lyric currentLyric;
    protected Process p;
    protected boolean first = true;;

    /**
     * private,只允许启动一个实例。
     */
    private KittenBox() {
        super(null);
        app = this;

        // 初始化log4j
        Log4jUtil.init();

        // 读入配置文件
        ps = ResourceManager.getPreferenceStore();

        SHOW_DESKTOP_LYRICS = ps.getBoolean(PrefKeys.LYRICS_DESKTOP);

        // 初始化底层播放控件
        bp = new BasicPlayer();
        player = (BasicController) bp;
        bp.addBasicPlayerListener(this);

        // 加上状态栏
        addStatusLine();
    }

    private Menu createTrayMenu(Display display, final Shell shell) {
        final Menu trayMenu = new Menu(shell, SWT.POP_UP);
        {
            MenuItem item = new MenuItem(trayMenu, SWT.PUSH);
            item.setText("科比\r\n超帅绝杀版");
            item.setImage(new Image(Display.getDefault(), "icon/kobebryant.jpg"));
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    new AboutAuthorAction().run();
                }

            });
        }

        MenuItem mainItem = new MenuItem(trayMenu, SWT.PUSH);
        mainItem.setText("显示主界面");
        mainItem.setImage(new Image(Display.getDefault(), "icon/debug.gif"));
        mainItem.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                toggleDisplay(shell, tray);
            }

        });

        {
            MenuItem item = new MenuItem(trayMenu, SWT.PUSH);
            item.setText("停止");
            item.setImage(new Image(Display.getDefault(), "icon/stop.gif"));
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    stop();
                }

            });
        }
        {
            MenuItem item = new MenuItem(trayMenu, SWT.PUSH);
            item.setText("暂停");
            item.setImage(new Image(Display.getDefault(), "icon/pause.gif"));
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    pauseOrPlay();
                }

            });
        }

        {
            MenuItem item = new MenuItem(trayMenu, SWT.PUSH);
            item.setText("播放");
            item.setImage(new Image(Display.getDefault(), "icon/play.gif"));
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    play();
                }
            });
        }
        {
            MenuItem item = new MenuItem(trayMenu, SWT.PUSH);
            item.setText("上一首");
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    playPrevious();
                }
            });
        }
        {
            MenuItem item = new MenuItem(trayMenu, SWT.PUSH);
            item.setText("下一首");
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    playNext();
                }
            });
        }
        {
            MenuItem item = new MenuItem(trayMenu, SWT.PUSH);
            item.setText("随机");
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    playRandom();
                }
            });
        }
        new MenuItem(trayMenu, SWT.SEPARATOR);
        {
            MenuItem item = new MenuItem(trayMenu, SWT.PUSH);
            item.setText("配置");
            item.setImage(new Image(Display.getDefault(), "icon/preference.gif"));
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    openPrefDialog();
                }
            });
        }
        new MenuItem(trayMenu, SWT.SEPARATOR);

        {
            MenuItem aboutItem = new MenuItem(trayMenu, SWT.PUSH);
            aboutItem.setText("退出");
            aboutItem.setImage(new Image(Display.getDefault(), "icon/exit.gif"));
            aboutItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    exit();
                }
            });
        }

        trayMenu.setDefaultItem(mainItem);
        return trayMenu;
    }

    public PreferenceDialog getPrefDialog() {
        return prefDialog;
    }

    public void openPrefDialog() {
        openPrefDialog(getShell(), null);
    }

    public void openPrefDialog(final Shell shell, String pageId) {
        try {
            if (prefDialog != null) {
                prefDialog.close();
                return;
            }

            ShortcutsPage shortcutsPage = new ShortcutsPage();
            shortcutsPage.setTitle("快捷键设置");
            PlayPage playPage = new PlayPage();
            playPage.setTitle("播放设置");
            playPage.setPreferenceStore(ResourceManager.getPreferenceStore());

            LyricsPage lyricsPage = new LyricsPage();
            lyricsPage.setTitle("歌词设置");
            lyricsPage.setPreferenceStore(ResourceManager.getPreferenceStore());
            NetworkPage networkPage = new NetworkPage();
            networkPage.setTitle("网络设置");
            networkPage.setPreferenceStore(ResourceManager.getPreferenceStore());

            WindowsPage windowsPage = new WindowsPage();
            windowsPage.setTitle("启动和托盘图标");
            // windowsPage.setImageDescriptor(ImageDescriptor
            // .createFromImage(new Image(Display.getDefault(),
            // "icon/exit.gif")));
            windowsPage.setPreferenceStore(ResourceManager.getPreferenceStore());
            ComponentsPage componentsPage = new ComponentsPage();
            componentsPage.setTitle("技术支持");

            PreferenceNode componentsNode = new PreferenceNode("componentsPage", componentsPage);
            PreferenceNode playNode = new PreferenceNode("playPage", playPage);
            PreferenceNode lyricsNode = new PreferenceNode("lyricsPage", lyricsPage);
            PreferenceNode networkNode = new PreferenceNode("networkPage", networkPage);
            PreferenceNode shortcutsNode = new PreferenceNode("shortcutsPage", shortcutsPage);
            PreferenceNode windowsNode = new PreferenceNode("windowsPage", windowsPage);

            PreferenceManager pm = new PreferenceManager();
            pm.addToRoot(shortcutsNode);
            pm.addToRoot(playNode);
            pm.addToRoot(lyricsNode);
            pm.addToRoot(networkNode);
            pm.addToRoot(windowsNode);
            pm.addToRoot(componentsNode);

            prefDialog = new PreferenceDialog(shell, pm);
            if (pageId != null) {
                prefDialog.setSelectedNode(pageId);
            }
            prefDialog.create();
            prefDialog.getShell().setText("配置");
            prefDialog.getShell().setImage(new Image(Display.getDefault(), "icon/preference.gif"));
            prefDialog.getShell().setBounds(getShell().getLocation().x, getShell().getLocation().y, Default.SHELL_WIDTH,
                    Default.SHELL_HEIGHT);
            int ok = prefDialog.open();

            prefDialog = null;
        } catch (Exception e1) {
            log.error(ErrorUtil.getError(e1));
        }
    }

    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        shell.setText(Default.PRODUCT_NAME);
        shell.setImage(new Image(Display.getDefault(), "icon/debug.gif"));
        if (ps.getInt(PrefKeys.WINDOW_X) != 0) {
            shell.setSize(ps.getInt(PrefKeys.WINDOW_WIDTH), ps.getInt(PrefKeys.WINDOW_HEIGHT));
            shell.setLocation(ps.getInt(PrefKeys.WINDOW_X), ps.getInt(PrefKeys.WINDOW_Y));
        } else {
            shell.setSize(Default.SHELL_WIDTH, Default.SHELL_HEIGHT);
            center(shell);
        }
        shell.forceFocus();
        // 注册窗口事件监听器
        shell.addShellListener(new ShellAdapter() {
            // 点击窗口最小化按钮时，窗口隐藏，系统栏显示图标
            public void shellIconified(ShellEvent e) {
                minimizeWindow(shell, tray);
            }

            // 点击窗口关闭按钮时，并不终止程序，而时隐藏窗口，同时系统栏显示图标
            public void shellClosed(ShellEvent e) {
                e.doit = false; // 消耗掉原本系统来处理的事件
                if (ps.getBoolean(PrefKeys.WINDOW_HIDE_ON_CLOSE)) {
                    toggleDisplay(shell, tray);
                } else {
                    exit();
                }
            }
        });
        shell.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.stateMask == SWT.CTRL && e.keyCode == 102) {
                    SearchShell searchShell = new SearchShell(getPlayListPanel().getPlayList());
                    searchShell.open();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

        });
        if (ps.getBoolean(PrefKeys.WINDOW_START_AS_TRAY)) {
            minimizeWindow(shell, tray);
        }
    }

    public boolean close() {
        return false;
    }

    /**
     * 窗口是可见状态时，则隐藏窗口，同时把系统栏中图标删除 窗口是隐藏状态时，则显示窗口，并且在系统栏中显示图标
     * 
     * @param shell
     *            窗口
     * @param tray
     *            系统栏图标控件
     */
    private void minimizeWindow(Shell shell, Tray tray) {
        try {
            shell.setVisible(false);
            shell.setMinimized(true);
            if (!ps.getBoolean(PrefKeys.WINDOW_SHOW_SYSTEMTRAY_ALLWAYS)) {
                tray.getItem(0).setVisible(!shell.isVisible());
            }
        } catch (Exception e) {
            log.error(ErrorUtil.getError(e));
        }
    }

    public void toggleDisplay() {
        toggleDisplay(getShell(), tray);
    }

    public void toggleDisplay(Shell shell, Tray tray) {
        try {
            shell.setVisible(!shell.isVisible());
            if (!ps.getBoolean(PrefKeys.WINDOW_SHOW_SYSTEMTRAY_ALLWAYS)) {
                tray.getItem(0).setVisible(!shell.isVisible());
            }
            if (shell.getVisible()) {
                shell.setMinimized(false);
                shell.setActive();
            }
        } catch (Exception e) {
            log.error(ErrorUtil.getError(e));
        }
    }

    /**
     * 窗口居中显示
     * 
     * @param shell
     *            要显示的窗口
     */
    public static void center(Shell shell) {
        Monitor monitor = shell.getMonitor();
        Rectangle bounds = monitor.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

    protected Control createContents(Composite parent) {
        final Composite c = new Composite(parent, SWT.NONE);
        // c.setLayout(new GridLayout());
        c.setLayout(new FormLayout());
        //
        TabFolder folder = new TabFolder(c, SWT.NONE);
        TabItem tt = new TabItem(folder, SWT.NONE);
        tt.setText("Default");
        //
        playListPanel = new PlayListPanel(folder);

        final CoolBar coolBar = new CoolBar(c, SWT.FLAT | SWT.WRAP);
        this.coolBar = coolBar;

        // file
        addFileMenuToCoolbar(coolBar);
        // scale
        addSliderToCoolbar(coolBar);
        // music controls
        addMusicControlsToCoolbar(coolBar);
        addPlaylistControlsToCoolbar(coolBar);
        addVolumeToCoolbar(coolBar);
        addOrderToCoolbar(coolBar);

        coolBar.setWrapIndices(new int[] { 2 });
        // coolBar.pack();
        FormData coolData = new FormData();
        coolData.left = new FormAttachment(0);
        coolData.right = new FormAttachment(100);
        coolData.top = new FormAttachment(0);
        coolBar.setLayoutData(coolData);
        coolBar.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                c.layout();
            }

        });
        // restore coolbar
        String strItemOrder = ps.getString(PrefKeys.COOLBAR_ITEM_ORDER);
        String strItemSize = ps.getString(PrefKeys.COOLBAR_ITEM_SIZE);
        String strWrapIndex = ps.getString(PrefKeys.COOLBAR_WRAP_INDICES);
        boolean ifLock = ps.getBoolean(PrefKeys.COOLBAR_LOCK);
        restoreCoolbar(coolBar, strItemOrder, strItemSize, strWrapIndex, ifLock);

        final Menu coolbarMenu = new Menu(getShell(), SWT.POP_UP);
        {
            MenuItem item = new MenuItem(coolbarMenu, SWT.CHECK);
            item.setText("锁定工具栏");
            item.setSelection(ps.getBoolean(PrefKeys.COOLBAR_LOCK));
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    coolBar.setLocked(!coolBar.getLocked());
                }

            });
        }
        {
            MenuItem item = new MenuItem(coolbarMenu, SWT.PUSH);
            item.setText("恢复默认布局");
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    restoreCoolbar(coolBar, Default.ITEM_ORDER, Default.ITEM_SIZE, Default.Wrap_Indices, false);
                    coolbarMenu.getItem(0).setSelection(false);
                    getShell().setSize(Default.SHELL_WIDTH, Default.SHELL_HEIGHT);
                    center(getShell());
                }

            });
        }
        coolBar.setMenu(coolbarMenu);

        tt.setControl(playListPanel);

        FormData textData = new FormData();
        textData.left = new FormAttachment(0);
        textData.right = new FormAttachment(100);
        textData.top = new FormAttachment(coolBar);
        textData.bottom = new FormAttachment(100);
        folder.setLayoutData(textData);

        final Menu trayMenu = createTrayMenu(display, getShell());
        myTrayItem = new TrayItem(tray, SWT.None);
        myTrayItem.setToolTipText(Default.PRODUCT_NAME);
        myTrayItem.setImage(new Image(Display.getDefault(), "icon/debug.gif"));
        Listener listener = new Listener() {
            public void handleEvent(Event e) {
                if (e.type == SWT.Show) {
                } else if (e.type == SWT.Hide) {
                } else if (e.type == SWT.Selection) {
                    toggleDisplay(getShell(), tray);
                } else if (e.type == SWT.DefaultSelection) {
                    toggleDisplay(getShell(), tray);
                } else if (e.type == SWT.MenuDetect) {
                    if (trayMenu != null) {
                        trayMenu.setVisible(true);
                    }
                }
            }
        };
        myTrayItem.addListener(SWT.Show, listener);
        myTrayItem.addListener(SWT.Hide, listener);
        myTrayItem.addListener(SWT.Selection, listener);
        myTrayItem.addListener(SWT.DefaultSelection, listener);
        myTrayItem.addListener(SWT.MenuDetect, listener);

        // 初始化显示隐藏歌词按钮
        showLyrics(ps.getBoolean(PrefKeys.LYRICS_SHOW));

        if (ps.getBoolean(PrefKeys.RESUME_PLAY)) {
            if (getPlayListPanel().getPlayList().size() > 0) {
                try {
                    playByIndex(ps.getInt(PrefKeys.PL_LAST_MUSIC_INDEX));
                } catch (ArrayIndexOutOfBoundsException e) {
                    playByIndex(0);
                }
                try {
                    int lastBytes = ps.getInt(PrefKeys.PL_LAST_READ_BYTES);
                    BasicController bc = getPlayer();
                    bc.seek(lastBytes);
                } catch (BasicPlayerException e1) {
                    log.error(e1.getMessage());
                } catch (NullPointerException e1) {
                    log.error(ErrorUtil.getError(e1));
                    playNext();
                } catch (Throwable e1) {
                    log.error(ErrorUtil.getError(e1));
                }
            }
        }
        // ShellWrapper wrapper = new ShellWrapper(getShell());
        // //style
        // if (Win32.getWin32Version() >= Win32.VERSION(5, 0))
        // {
        // wrapper.installTheme(ThemeConstants.STYLE_VISTA);
        // }
        //
        return c;
    }

    private void addOrderToCoolbar(CoolBar coolBar) {
        comboOrder = new Combo(coolBar, SWT.READ_ONLY);
        comboOrder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        comboOrder.add("循环全部");
        comboOrder.add("随机播放");
        comboOrder.add("就听它了");
        comboOrder.select(ps.getInt(PrefKeys.ORDER_INDEX));
        comboOrder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            }
        });

        Point size = comboOrder.getSize();
        CoolItem scaleItem = new CoolItem(coolBar, SWT.NONE);
        scaleItem.setControl(comboOrder);
        Point preferred = scaleItem.computeSize(size.x, size.y);
        scaleItem.setPreferredSize(preferred);

    }

    private void restoreCoolbar(final CoolBar coolBar, String strItemOrder, String strItemSize, String strWrapIndex,
            boolean ifLock) {
        if (strItemOrder != null && strItemOrder.length() > 0) {
            String[] strItemOrders = strItemOrder.split(",");
            int[] itemOrder = new int[strItemOrders.length];
            for (int i = 0; i < strItemOrders.length; i++) {
                itemOrder[i] = Integer.parseInt(strItemOrders[i]);
            }
            String[] strWrapIndices = strWrapIndex.split(",");
            int[] wrapIndices = new int[strWrapIndices.length];
            for (int i = 0; i < strWrapIndices.length; i++) {
                wrapIndices[i] = Integer.parseInt(strWrapIndices[i]);
            }

            String[] strItemSizes = strItemSize.split(";");
            Point[] sizes = new Point[strItemSizes.length];
            for (int i = 0; i < strItemSizes.length; i++) {
                String[] p = strItemSizes[i].split(",");
                sizes[i] = new Point(Integer.valueOf(p[0]), Integer.valueOf(p[1]));
            }
            coolBar.setItemLayout(itemOrder, wrapIndices, sizes);
        }
        coolBar.setLocked(ifLock);
    }

    private void addPlaylistControlsToCoolbar(CoolBar coolBar) {
        ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT | SWT.WRAP);

        {
            final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setToolTipText("打开歌曲所在目录");
            item.setImage(new Image(Display.getDefault(), "icon/openInExplorer.gif"));
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    new OpenInExplorerAction().run();
                }
            });
        }
        {

            final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setToolTipText("打开播放列表");
            item.setImage(new Image(Display.getDefault(), "icon/load.gif"));
            item.addSelectionListener(new SelectionAdapter() {
                final PlayListPanel pp = getPlayListPanel();
                final TreeViewer tv = pp.getTreeViewer();
                final List<PlayListBean> playList = pp.getPlayList();

                public void widgetSelected(SelectionEvent e) {
                    FileDialog fd = new FileDialog(KittenBox.getApp().getShell(), SWT.OPEN);
                    fd.setFilterNames(new String[] { "KittenBox PlayList(*.kpl)" });
                    fd.setFilterExtensions(new String[] { "*.kpl" });
                    String f = fd.open();
                    if (f != null) {
                        try {
                            File ff = new File(f);
                            FileHelper.processFileByLine(ff, "UTF-8", new ILineProcessor() {
                                public boolean processLine(String str) {
                                    String[] strs = str.split(",");
                                    if (strs != null && strs.length == 2) {
                                        PlayListBean bean = new PlayListBean();
                                        bean.setName(strs[0]);
                                        bean.setPath(strs[1]);
                                        playList.add(bean);
                                    }
                                    return FileHelper.CONTINUE;
                                }

                            });
                            tv.setInput(playList);
                            tv.refresh();
                        } catch (Exception ex) {
                            log.error(ErrorUtil.getError(ex));
                        }
                    }
                }
            });
        }
        {
            final PlayListPanel pp = getPlayListPanel();
            final List<PlayListBean> playList = pp.getPlayList();

            final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setToolTipText("保存当前播放列表");
            item.setImage(new Image(Display.getDefault(), "icon/save.gif"));
            item.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    FileDialog fd = new FileDialog(KittenBox.getApp().getShell(), SWT.SAVE);
                    fd.setFilterNames(new String[] { "Kitten PlayList(*.kpl)" });
                    fd.setFilterExtensions(new String[] { "*.kpl" });
                    String f = fd.open();
                    if (f != null) {
                        try {
                            File ff = new File(f);
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < playList.size(); i++) {
                                PlayListBean bean = playList.get(i);
                                sb.append(bean.getName() + "," + bean.getPath());
                                sb.append("\r\n");
                            }
                            FileHelper.setFileContent(ff, sb.toString(), "UTF-8");
                            FileHelper.setFileContent(new File("c:/KittenBox.dat"), f, "UTF-8");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
        {
            final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setToolTipText("在播放列表中搜索歌曲");
            item.setImage(new Image(Display.getDefault(), "icon/search.gif"));
            item.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    SearchShell searchShell = new SearchShell(getPlayListPanel().getPlayList());
                    searchShell.open();
                }
            });
        }
        {
            btnShowLyrics = new ToolItem(toolBar, SWT.CHECK);
            btnShowLyrics.setToolTipText("显示/隐藏歌词");
            btnShowLyrics.setImage(new Image(Display.getDefault(), "icon/show_lrc.gif"));
            btnShowLyrics.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    showLyrics(!SHOW_LYRICS);
                }
            });
        }
        {
            btnSlowLyrics = new ToolItem(toolBar, SWT.NONE);
            btnSlowLyrics.setToolTipText("歌词慢了!");
            btnSlowLyrics.setImage(new Image(Display.getDefault(), "icon/slow.gif"));
            btnSlowLyrics.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    if (lrcFile != null) {
                        lrcFile.subtractOffset(ps.getInt(PrefKeys.LYRICS_OFFSET));
                        btnSlowLyrics.setToolTipText("歌词慢了![offset:" + lrcFile.getOffset() + "]");
                        btnFastLyrics.setToolTipText("歌词快了![offset:" + lrcFile.getOffset() + "]");
                        lrcFile.saveOffset();
                    }
                }
            });
        }
        {
            btnFastLyrics = new ToolItem(toolBar, SWT.NONE);
            btnFastLyrics.setToolTipText("歌词快了!");
            btnFastLyrics.setImage(new Image(Display.getDefault(), "icon/fast.gif"));
            btnFastLyrics.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    if (lrcFile != null) {
                        lrcFile.incrementOffset(ps.getInt(PrefKeys.LYRICS_OFFSET));
                        btnSlowLyrics.setToolTipText("歌词慢了![offset:" + lrcFile.getOffset() + "]");
                        btnFastLyrics.setToolTipText("歌词快了![offset:" + lrcFile.getOffset() + "]");
                        lrcFile.saveOffset();
                    }
                }
            });
        }
        toolBar.pack();

        Point size = toolBar.getSize();
        // toolBar.setBounds(0, size.y, size.x, size.y);

        CoolItem musicItem = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN | SWT.WRAP);
        musicItem.setControl(toolBar);
        Point preferred = musicItem.computeSize(size.x, size.y);
        musicItem.setPreferredSize(preferred);
    }

    private void showLyrics(boolean ifShow) {
        SHOW_LYRICS = ifShow;
        btnShowLyrics.setSelection(ifShow);
        if (!ifShow) {
            reloadDesktopLyricSetting();
            KittenBox.getApp().setStatus("");
            btnShowLyrics.setToolTipText("显示歌词");
        } else {
            btnShowLyrics.setToolTipText("隐藏歌词");
        }
    }

    public void reloadDesktopLyricSetting() {
        first = true;
        asyncKill();
        SHOW_DESKTOP_LYRICS = ps.getBoolean(PrefKeys.LYRICS_DESKTOP);
    }

    private void addMusicControlsToCoolbar(CoolBar coolBar) {
        ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT | SWT.WRAP);

        ToolItem stopItem = new ToolItem(toolBar, SWT.PUSH);
        stopItem.setToolTipText("停止");
        stopItem.setImage(new Image(getShell().getDisplay(), "icon/stop.gif"));
        stopItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                stop();
            }

        });

        ToolItem playItem = new ToolItem(toolBar, SWT.PUSH);
        playItem.setToolTipText("播放");
        playItem.setImage(new Image(getShell().getDisplay(), "icon/play.gif"));
        playItem.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                play();
            }

        });
        {
            final ToolItem pauseItem = new ToolItem(toolBar, SWT.PUSH);
            pauseItem.setToolTipText("暂停/播放");
            pauseItem.setImage(new Image(getShell().getDisplay(), "icon/pause.gif"));
            pauseItem.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    pauseOrPlay();
                }

            });
        }
        {
            final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setToolTipText("上一首");
            item.setImage(new Image(getShell().getDisplay(), "icon/previous.gif"));
            item.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    playPrevious();
                }

            });
        }
        {
            final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setToolTipText("下一首");
            item.setImage(new Image(getShell().getDisplay(), "icon/next.gif"));
            item.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    playNext();
                }

            });
        }
        {
            final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setToolTipText("随机");
            item.setImage(new Image(getShell().getDisplay(), "icon/random.gif"));
            item.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    playRandom();
                }

            });
        }
        {
            final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
            item.setToolTipText("定位到正在播放的歌曲");
            item.setImage(new Image(getShell().getDisplay(), "icon/gotoplay.gif"));
            item.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    PlayListPanel pp = getPlayListPanel();
                    List<PlayListBean> playList = pp.getPlayList();
                    int curIndex = pp.getCurMusicIndex();
                    if (curIndex < playList.size()) {
                        TreeViewer tv = getPlayListPanel().getTreeViewer();
                        tv.setSelection(new StructuredSelection(playList.get(curIndex)), true);
                    }
                }

            });
        }
        toolBar.pack();
        Point size = toolBar.getSize();

        CoolItem musicItem = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN | SWT.WRAP);
        musicItem.setControl(toolBar);
        Point preferred = musicItem.computeSize(size.x, size.y);
        musicItem.setPreferredSize(preferred);
    }

    private void addSliderToCoolbar(CoolBar coolBar) {
        scale = new Slider(coolBar, SWT.FLAT);
        scale.setThumb(1);
        scale.setIncrement(1);
        scale.setPageIncrement(10);
        scale.setMaximum(100);
        scale.setCapture(false);
        scale.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                canMoveScale = false;
            }
        });
        scale.addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            public void mouseDown(MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            public void mouseUp(MouseEvent arg0) {
                double rate = ((double) scale.getSelection()) / ((double) scale.getMaximum());
                try {
                    getApp().getPlayer().seek((int) (rate * totalbytes));
                } catch (BasicPlayerException e) {
                    log.error(ErrorUtil.getError(e));
                }
                canMoveScale = true;
            }

        });

        scale.pack();
        Point size = scale.getSize();
        CoolItem scaleItem = new CoolItem(coolBar, SWT.NONE);
        scaleItem.setControl(scale);
        Point preferred = scaleItem.computeSize(size.x, size.y);
        scaleItem.setPreferredSize(preferred);
    }

    private void addVolumeToCoolbar(CoolBar coolBar) {
        // volume = new Scale(coolBar, SWT.HORIZONTAL);
        // volume.setIncrement(1);
        // volume.setPageIncrement(2);
        // volume.setMaximum(6);
        // volume.addListener(SWT.Selection, new Listener() {
        // public void handleEvent(Event event) {
        // double rate = ((double) volume.getSelection())
        // / ((double) volume.getMaximum());
        // try {
        // log.info("pan="+1.0 * rate);
        // getApp().getPlayer().setGain(1.0 * rate);
        // } catch (BasicPlayerException e) {
        // log.error(ErrorUtil.getError(e));
        // }
        // }
        // });

        volume = new Slider(coolBar, SWT.HORIZONTAL);
        volume.setThumb(1);
        volume.setMinimum(1);
        volume.setMaximum(100);
        volume.setPageIncrement(5);
        volume.setSelection(ps.getInt(PrefKeys.VOLUME_VALUE));

        volume.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
               VolumeManager.setVolume(bp, volume.getSelection());
            }

        });

        volume.pack();

        Point size = volume.getSize();
        CoolItem scaleItem = new CoolItem(coolBar, SWT.NONE);
        scaleItem.setControl(volume);
        Point preferred = scaleItem.computeSize(size.x, size.y);
        scaleItem.setPreferredSize(preferred);
        // scaleItem.setSize(new Point(98, 41));
    }

    private void addFileMenuToCoolbar(CoolBar coolBar) {
        final ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);

        final ToolItem fileItem = new ToolItem(toolBar, SWT.PUSH);
        fileItem.setText("文件");

        final Menu helpMenu = new Menu(getShell(), SWT.POP_UP);
        MenuItem openItem = new MenuItem(helpMenu, SWT.PUSH);
        openItem.setText("&打开\tCTRL+O");
        new MenuItem(helpMenu, SWT.SEPARATOR);

        MenuItem configItem = new MenuItem(helpMenu, SWT.PUSH);
        configItem.setText("配置");
        configItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                openPrefDialog();
            }
        });

        new MenuItem(helpMenu, SWT.SEPARATOR);
        MenuItem addFolder = new MenuItem(helpMenu, SWT.PUSH);
        addFolder.setText("&退出");
        addFolder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                exit();
            }
        });

        fileItem.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event e) {
                if (e.detail == SWT.NONE) {
                    Rectangle rect = fileItem.getBounds();
                    Point pt = new Point(rect.x, rect.y + rect.height);
                    pt = toolBar.toDisplay(pt);
                    helpMenu.setLocation(pt);
                    helpMenu.setVisible(true);
                }
            }

        });

        ToolItem editItem = new ToolItem(toolBar, SWT.PUSH);
        editItem.setText("视图");

        ToolItem viewItem = new ToolItem(toolBar, SWT.PUSH);
        viewItem.setText("操作");

        ToolItem helpItem = new ToolItem(toolBar, SWT.PUSH);
        helpItem.setText("关于");
        helpItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                openPrefDialog(getShell(), "componentsPage");
            }
        });

        toolBar.pack();
        Point size = toolBar.getSize();
        coolItemHeight = size.y;
        CoolItem commonsItem = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
        commonsItem.setControl(toolBar);
        Point preferred = commonsItem.computeSize(size.x, coolItemHeight);
        commonsItem.setPreferredSize(preferred);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        KittenBox box = new KittenBox();
        box.setBlockOnOpen(true);

        final GlobalHotkeyManager keyman = GlobalHotkeyManager.getInstance();
        System.out.println(keyman);
        if (keyman != null) {
            keyman.configure();
        }

        System.out.println("1111111=" + box.getShell());

        box.open();//

        // 接下来这句永远也不会执行
        System.out.println("222222=" + box.getShell());
        // open会造成阻塞，之后的所有操作都无效！但是open之前shell又不存在.ApplicationWIndow真难用
        // open之后，getShell()才不为null,所以对shell的操作要放在open之后
        // open后，对shell的所有操作无效？？？？

        // try {
        // System.out.println("3333333="+box.getShell());
        // Display.getCurrent().dispose();
        // } catch (Exception e) {
        // log.warn(e.getMessage());
        // } finally {
        // System.out.println("44444444="+box.getShell());
        // System.exit(0);
        // }
    }

    public static KittenBox getApp() {
        return app;
    }

    public BasicController getPlayer() {
        return this.player;
    }

    public PlayListPanel getPlayListPanel() {
        return playListPanel;
    }

    public Map getAudioInfo() {
        return audioInfo;
    }

    public void opened(Object stream, Map properties) {
        sum = 0;
        playComplete = false;
        audioInfo = properties;
        totalbytes = (Integer) (audioInfo.get("audio.length.bytes"));
        timeLength = getTimeLengthEstimation(properties);

        int min = (int) timeLength / 1000 / 60;
        int sec = (int) Math.round(timeLength / 1000.0) % 60;
        timeLengthStr = min + ":" + ((sec < 10) ? ("0" + sec) : sec);

        reloadLrcFile();
    }

    /**
     * 重新读取歌词文件.
     */
    public void reloadLrcFile() {
        PlayListBean bean = getPlayListPanel().getPlayList().get(getPlayListPanel().getCurMusicIndex());

        lrcFile = new LrcFile();
        LyricMapping m = LyricMapping.getInstance();
        String fromMapping = null;
        if (m != null) {
            fromMapping = m.get(bean.getName());
        }

        String fromCurrentDir = bean.getPath().replace(".mp3", ".lrc");

        String fromLyricsDir = ps.getString(PrefKeys.LYRICS_DIR) + C.FS + (bean.getName().replace(".mp3", ".lrc"));
        if (fromMapping != null && new File(fromMapping).exists()) {
            log.info("从当前映射文件中读取");
            lrcFile.read(fromMapping);
            bean.setLrcFile(lrcFile);
        } else if (new File(fromCurrentDir).exists()) {
            log.info("从当前目录中读取");
            lrcFile.read(fromCurrentDir);
            bean.setLrcFile(lrcFile);
        } else if (new File(fromLyricsDir).exists()) {
            log.info("从歌词目录中读取");
            lrcFile.read(fromLyricsDir);
            bean.setLrcFile(lrcFile);
        } else {
            lrcFile = null;
            setStatus("");
        }

        btnSlowLyrics.setToolTipText("歌词慢了![offset:" + (lrcFile == null ? 0 : lrcFile.getOffset()) + "]");
        btnFastLyrics.setToolTipText("歌词快了![offset:" + (lrcFile == null ? 0 : lrcFile.getOffset()) + "]");
    }

    public long getTimeLengthEstimation(Map properties) {
        long milliseconds = -1;
        int byteslength = -1;
        if (properties != null) {
            if (properties.containsKey("audio.length.bytes")) {
                byteslength = ((Integer) properties.get("audio.length.bytes")).intValue();
            }
            if (properties.containsKey("duration")) {
                milliseconds = (int) (((Long) properties.get("duration")).longValue()) / 1000;
            } else {
                // Try to compute duration
                int bitspersample = -1;
                int channels = -1;
                float samplerate = -1.0f;
                int framesize = -1;
                if (properties.containsKey("audio.samplesize.bits")) {
                    bitspersample = ((Integer) properties.get("audio.samplesize.bits")).intValue();
                }
                if (properties.containsKey("audio.channels")) {
                    channels = ((Integer) properties.get("audio.channels")).intValue();
                }
                if (properties.containsKey("audio.samplerate.hz")) {
                    samplerate = ((Float) properties.get("audio.samplerate.hz")).floatValue();
                }
                if (properties.containsKey("audio.framesize.bytes")) {
                    framesize = ((Integer) properties.get("audio.framesize.bytes")).intValue();
                }
                if (bitspersample > 0) {
                    milliseconds = (int) (1000.0f * byteslength / (samplerate * channels * (bitspersample / 8)));
                } else {
                    milliseconds = (int) (1000.0f * byteslength / (samplerate * framesize));
                }
            }
        }
        return milliseconds;
    }

    public void progress(final int bytesread, final long microseconds, final byte[] pcmdata, final Map properties) {
        processProgress(bytesread, microseconds, pcmdata, properties);
    }

    public void processProgress(final int bytesread, final long microseconds, final byte[] pcmdata,
            final Map properties) {
        if (playComplete) {
            return;
        }
        this.bytesread = bytesread;
        // System.out.println(bytesread+","+totalbytes+","+timeLength);
        Shell shell = getShell();
        if (shell != null && !shell.isDisposed()) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    if (currentStatus == PAUSE) {
                        setStatus("paused.");
                        return;
                    } else if (currentStatus == STOP) {
                        setStatus("stopped.");
                        return;
                    } else {
                        setStatus("");
                    }

                    float rate = (float) ((bytesread - 2000) * 1.00000 / totalbytes);

                    int min = (int) (timeLength * rate / 1000.0000 / 60);
                    int sec = (int) (timeLength * rate / 1000.0f % 60);
                    String ms = min + ":" + ((sec < 10) ? ("0" + sec) : sec);
                    // + ":" + microseconds;

                    if (SHOW_LYRICS) {
                        showLyrics(rate, ms);
                    }

                    if (canMoveScale) {
                        scale.setSelection((int) (scale.getMaximum() * rate));
                    }

                    // 播放完成后自动播放下一首
                    if (bytesread == totalbytes) {
                        playComplete = true;
                        try {
                            Thread.sleep(500);

                            log.info(bytesread + "==" + totalbytes + "播放完成后自动播放下一首");
                            playNext();
                        } catch (Exception e) {
                            log.error(ErrorUtil.getError(e));
                        }
                    }
                }

                private void showLyrics(float rate, String ms) {
                    if (lrcFile != null) {
                        List<Lyric> lyrics = lrcFile.getLyrics();
                        int size = lyrics.size();
                        if (size > 0) {
                            Lyric playingLyric = null;
                            // 是否还在播放该句呢
                            if (lastLrcIndex < size - 1 && lyrics.get(lastLrcIndex).in(timeLength * rate)) {
                                playingLyric = lyrics.get(lastLrcIndex);
                                // 是否直接取下一句呢
                            } else if (lastLrcIndex < size - 1 && lyrics.get(lastLrcIndex + 1).in(timeLength * rate)) {
                                lastLrcIndex++;
                                playingLyric = lyrics.get(lastLrcIndex);
                            } else {
                                // 都不是,则遍历所有的歌词
                                for (Lyric yy : lyrics) {
                                    if (yy.in(timeLength * rate)) {
                                        lastLrcIndex = yy.getIndex();
                                        playingLyric = yy;
                                        break;
                                    } else {
                                        // i++;
                                    }
                                }
                            }

                            if (playingLyric != null) {
                                currentLyric = playingLyric;
                                // 备用
                                float ratio = playingLyric.getRate();
                                String content = playingLyric.getContent();
                                String next = "";
                                // content = playingLyric.toTs() + content;
                                // int length = content.length();
                                // content = content
                                // .substring((int) (length * ratio));
                                // if (xx.getIndex() < size - 1) {
                                // next = lyrics.get(xx.getIndex() + 1)
                                // .getContent();
                                // content += " "
                                // + next.substring(0, (int) (next
                                // .length() * ratio));
                                // }
                                KittenBox.getApp().setStatus(ms + "/" + timeLengthStr + " " + content + "\r\n" + next);
                                // ysb
                                if (SHOW_DESKTOP_LYRICS) {
                                    if (first) {
                                        first = false;
                                        p = showDesktopLyric(content, false);
                                    } else {
                                        // 歌词变了
                                        if (lastContent != null && !lastContent.equals(content)) {
                                            p = showDesktopLyric(content, true);
                                        }
                                    }
                                    lastContent = content;
                                }
                            } else {
                                KittenBox.getApp().setStatus(ms + "/" + timeLengthStr);
                            }
                        }
                    } else {
                        if (SHOW_DESKTOP_LYRICS) {
                            if (lastContent != null) {
                                lastContent = null;
                                asyncKill();
                            }
                        }
                        KittenBox.getApp().setStatus(ms + "/" + timeLengthStr + " 没有找到歌词.");
                    }
                }
            });
        }
    }

    public void setController(BasicController arg0) {

    }

    public void stateUpdated(BasicPlayerEvent e) {
        log.info(e.getSource());
        log.info(e.getCode());
        log.info(e.getValue());
        log.info(e.getPosition());
    }

    public void stop() {
        try {
            KittenBox.getApp().getPlayer().stop();
            getApp().currentStatus = STOP;
        } catch (BasicPlayerException e1) {
            log.error(ErrorUtil.getError(e1));
        }
    }

    public void pauseOrPlay() {
        try {
            if (getApp().currentStatus == PLAY) {
                KittenBox.getApp().getPlayer().pause();
                getApp().currentStatus = PAUSE;
            } else if (getApp().currentStatus == PAUSE) {
                KittenBox.getApp().getPlayer().resume();
                getApp().currentStatus = PLAY;
            } else {
                if (getPlayListPanel().getPlayList().size() > 0) {
                    int curIndex = getPlayListPanel().getCurMusicIndex();
                    if (curIndex == -1) {
                        curIndex = 0;
                    }
                    Tree t = getPlayListPanel().getTreeViewer().getTree();
                    t.setSelection(t.getItem(curIndex));
                    getPlayListPanel().clickMusicItem(curIndex);
                }
            }
        } catch (BasicPlayerException e1) {
            log.error(ErrorUtil.getError(e1));
        }
    }

    public void asyncKill() {
        String str = "taskkill /IM Desktoplyricshow.exe";
        try {
            Runtime.getRuntime().exec("cmd /c " + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Process showDesktopLyric(String content, boolean kill) {
        if (kill) {
            String str = "taskkill /IM Desktoplyricshow.exe";
            try {
                Process child = Runtime.getRuntime().exec("cmd /c " + str);
                while (true) {
                    if (child.waitFor() == 0 || child.waitFor() == 128)
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String newcmd = "DesktoplyricShow.exe " + content.replaceAll(",", " ") + ",200,600,53,3,1";
        try {
            return Runtime.getRuntime().exec("cmd /c " + newcmd, null, new File(env));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void play() {
        if (currentStatus == PAUSE) {
            pauseOrPlay();
        } else {
            try {
                KittenBox.getApp().getPlayer().seek(0);
                KittenBox.getApp().getPlayer().play();
                getApp().currentStatus = PLAY;
            } catch (BasicPlayerException e1) {
                log.error(ErrorUtil.getError(e1));
            }
        }
    }

    public void playPrevious() {
        if (getPlayListPanel().getPlayList().size() > 0) {
            int curIndex = getPlayListPanel().getCurMusicIndex();
            if (curIndex == 0) {
                curIndex = getPlayListPanel().getPlayList().size();
            }
            Tree t = getPlayListPanel().getTreeViewer().getTree();
            t.setSelection(t.getItem(curIndex - 1));
            getPlayListPanel().clickMusicItem(-1);
        }
    };

    public void playNext() {
        int playWay = comboOrder.getSelectionIndex();
        switch (playWay) {
        case 0:
            playNextIndex();
            break;
        case 1:
            playRandom();
            break;
        case 2:
            playByIndex(getPlayListPanel().getCurMusicIndex());
            break;
        default:
            playNextIndex();
            break;
        }
    }

    private void playNextIndex() {
        if (getPlayListPanel().getPlayList().size() > 0) {
            int curIndex = getPlayListPanel().getCurMusicIndex();
            if (curIndex == getPlayListPanel().getPlayList().size() - 1) {
                curIndex = -1;
            }
            Tree t = getPlayListPanel().getTreeViewer().getTree();
            t.setSelection(t.getItem(curIndex + 1));
            getPlayListPanel().clickMusicItem(-1);
        }
    };

    public void playByIndex(int expectedIndex) {
        getPlayListPanel().clickMusicItem(expectedIndex);
    }

    public void playRandom() {
        int musicSize = getPlayListPanel().getPlayList().size();
        if (musicSize > 0) {
            int newIndex = new Random().nextInt(musicSize);
            Tree t = getPlayListPanel().getTreeViewer().getTree();
            t.setSelection(t.getItem(newIndex));
            getPlayListPanel().clickMusicItem(-1);
        }
    };

    public void volumeUp() {
        int newVolume = (volume.getSelection() + 1) > 100 ? 100 : (volume.getSelection() + 1);
        volume.setSelection(newVolume);
        VolumeManager.setVolume(bp, newVolume);
    }

    public void volumeDown() {
        int newVolume = (volume.getSelection() - 1) <= 0 ? 1 : (volume.getSelection() - 1);
        volume.setSelection(newVolume);
        VolumeManager.setVolume(bp, newVolume);
    }

    public void forward() {
        canMoveScale = false;
        double rate = ((double) scale.getSelection() + 5) / ((double) scale.getMaximum());
        rate = rate > 1 ? 1 : rate;

        try {
            getApp().getPlayer().seek((int) (rate * totalbytes));
        } catch (BasicPlayerException e) {
            log.error(ErrorUtil.getError(e));
        }
        canMoveScale = true;
    }

    public void backward() {
        canMoveScale = false;
        double rate = ((double) scale.getSelection() - 5) / ((double) scale.getMaximum());
        rate = rate < 0 ? 0 : rate;
        try {
            getApp().getPlayer().seek((int) (rate * totalbytes));
        } catch (BasicPlayerException e) {
            log.error(ErrorUtil.getError(e));
        }
        canMoveScale = true;
    }

    public void exit() {
        if (ps.getBoolean(PrefKeys.WINDOW_CONFIRM_ON_CLOSE)) {
            boolean sure = MessageDialog.openConfirm(getShell(), Default.PRODUCT_NAME, "确认要退出吗?");
            if (!sure) {
                return;
            }
        }
        asyncKill();

        System.out.println("222222=" + KittenBox.getApp().getShell());
        // TODO 加上退出时保存数据的进度条
        try {
            // 工具栏锁定
            ps.setValue(PrefKeys.COOLBAR_LOCK, coolBar.getLocked());
            // 保存当前工具栏布局
            int[] itemOrder = coolBar.getItemOrder();
            Point[] point = coolBar.getItemSizes();
            StringBuffer strPoint = new StringBuffer();
            for (int i = 0; i < point.length; i++) {
                Point p = point[i];
                strPoint.append(p.x + "," + p.y + ";");
            }
            if (strPoint.length() > 0) {
                strPoint.setLength(strPoint.length() - 1);
            }
            int[] wrapIndices = coolBar.getWrapIndices();
            ps.setValue(PrefKeys.COOLBAR_ITEM_ORDER, StringUtil.join(itemOrder, ","));
            ps.setValue(PrefKeys.COOLBAR_ITEM_SIZE, strPoint.toString());
            ps.setValue(PrefKeys.COOLBAR_WRAP_INDICES, StringUtil.join(wrapIndices, ","));
            // 窗口大小和位置
            ps.setValue(PrefKeys.WINDOW_X, getShell().getLocation().x);
            ps.setValue(PrefKeys.WINDOW_Y, getShell().getLocation().y);
            ps.setValue(PrefKeys.WINDOW_WIDTH, getShell().getBounds().width);
            ps.setValue(PrefKeys.WINDOW_HEIGHT, getShell().getBounds().height);
            // 音量
            ps.setValue(PrefKeys.VOLUME_VALUE, volume.getSelection());
            // 播放顺序
            ps.setValue(PrefKeys.ORDER_INDEX, comboOrder.getSelectionIndex());
            // 显示/隐藏歌词
            ps.setValue(PrefKeys.LYRICS_SHOW, SHOW_LYRICS);

            // 保存播放信息
            int curMusicIndex = getPlayListPanel().getCurMusicIndex();

            ps.setValue(PrefKeys.PL_LAST_MUSIC_INDEX, curMusicIndex);
            ps.setValue(PrefKeys.PL_LAST_READ_BYTES, bytesread);
            ps.save();
            // 保存列表
            StringBuffer sb = new StringBuffer();
            List<PlayListBean> playList = getApp().getPlayListPanel().getPlayList();
            for (int i = 0; i < playList.size(); i++) {
                PlayListBean bean = playList.get(i);
                sb.append(bean.getName() + "," + bean.getPath());
                sb.append("\r\n");
            }
            FileHelper.setFileContent(new File("c:/last.kpl"), sb.toString(), "UTF-8");

            Display.getCurrent().dispose();

            System.out.println("3333333=" + KittenBox.getApp().getShell());
        } catch (Exception e) {
            log.warn("Exit Error:" + e.getMessage());
        } finally {
            System.out.println("4444444=" + KittenBox.getApp().getShell());
            System.exit(0);
        }
    }

    public TrayItem getMyTrayItem() {
        return myTrayItem;
    }

    public LrcFile getLrcFile() {
        return lrcFile;
    }

    public void setLrcFile(LrcFile lrcFile) {
        this.lrcFile = lrcFile;
    }

    public Lyric getCurrentLyric() {
        return currentLyric;
    }

    public PreferenceStore getPs() {
        return ps;
    }

}
