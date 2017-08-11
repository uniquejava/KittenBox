package org.kangel.kittenbox.playlist;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.config.Default;
import org.kangel.kittenbox.playlist.menu.AddFilesAction;
import org.kangel.kittenbox.playlist.menu.RemoveAction;
import org.kangel.kittenbox.playlist.menu.RemovePhysicalAction;
import org.kangel.kittenbox.util.ItemUtil;
import org.kitten.core.io.FileHelper;
import org.kitten.core.io.ILineProcessor;
import org.kitten.core.util.StringUtil;

import javazoom.jlgui.basicplayer.BasicPlayerException;

public class PlayListPanel extends Composite {
    private Logger log = Logger.getLogger(PlayListPanel.class);
    private boolean firstPlay = true;
    private int lastMusicIndex = -1;
    private int curMusicIndex = -1;

    private Composite group;
    private Tree tree;
    private TreeViewer tv;
    private List<PlayListBean> playList = new ArrayList<PlayListBean>();
    public static String musicPath;

    // dnd
    private static int[] draggedItemIndex = null;// 被拖对象的下标

    public PlayListPanel(Composite parent) {
        super(parent, SWT.NONE);

        setLayout(new FillLayout());

        createGroup();
        createTree();
        // createButtons();

        initPlayList(null);

        MyActionGroup actionGroup = new MyActionGroup(tv);
        actionGroup.fillContextMenu(new MenuManager());

        layout();
    }

    private void initPlayList(PlayListBean bean) {
        if (bean == null) {
            // tv.setInput(Collections.EMPTY_LIST);
            // String f = "c:/KittenBox.dat";
            // String str = "";
            // try {
            // str = FileHelper.getFileContent(new File(f), "UTF-8");
            // } catch (Exception e1) {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // }
            String str = "c:/last.kpl";

            if (StringUtil.isNotEmpty(str)) {
                File ff = new File(str);
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    tv.setInput(Collections.EMPTY_LIST);
                }
            } else {
                tv.setInput(Collections.EMPTY_LIST);
            }
        } else {
            tv.setInput(bean);
        }
        // tv.refresh();

    }

    private void createGroup() {
        group = new Composite(this, SWT.NONE);
        group.setLayout(new GridLayout());
        // group.setText("");
    }

    private void createTree() {
        tree = new Tree(group, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 180;
        tree.setLayoutData(data);

        tree.setLinesVisible(true);
        tree.setHeaderVisible(false);

        TreeColumn tcPrefix = new TreeColumn(tree, SWT.LEFT);
        tcPrefix.setText("name");

        TreeColumn tcNumber = new TreeColumn(tree, SWT.NULL);
        tcNumber.setText("path");

        tcPrefix.setWidth(190);
        tcNumber.setWidth(310);

        tv = new TreeViewer(tree);
        tv.setContentProvider(new PlayListContentProvider());
        tv.setLabelProvider(new PlayListLabelProvider());
        tv.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                clickMusicItem(-1);
            }
        });

        tree.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                if (e.stateMask == SWT.CTRL && e.keyCode == 102) {
                    SearchShell searchShell = new SearchShell(playList);
                    searchShell.open();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

        });

        // 以下是对playlist拖放操作的完美支持[2009年8月22日完美收工]
        final Transfer[] transfer = new Transfer[] { LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() };

        // 血的教训，这里不能加DROP, 不然拖出就变成DROP了。
        tv.addDragSupport(DND.DROP_DEFAULT | DND.DROP_COPY, transfer, new DragSourceAdapter() {
            public void dragSetData(DragSourceEvent event) {
                System.out.println("dragSetData");
                if (transfer[0].isSupportedType(event.dataType)) {
                    // 加了 LocalSelectionTransfer.getTransfer()，就要写这个if
                    // 内部拖动的时候会优先选择LocalSelectionTransfer
                    ((LocalSelectionTransfer) transfer[0]).setSelection((IStructuredSelection) tv.getSelection());
                    draggedItemIndex = ItemUtil.getSelectionIndices(tree);
                }

                if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
                    // 只在拖出列表中的item到windows中时触发
                    // 如果Transfer里没有LocalSelectionTransfer，在列表内部拖动也会使用这个if
                    event.data = ItemUtil.getSelectionPaths(tv);
                }
            }

            @Override
            public void dragFinished(DragSourceEvent event) {
                System.out.println("dragFinished");
            }

            @Override
            public void dragStart(DragSourceEvent event) {
                System.out.println("dragStart");
            }
        });

        tv.addDropSupport(DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE, transfer, new ViewerDropAdapter(tv) {
            int pos = 0;

            protected Object determineTarget(DropTargetEvent event) {
                if (event.detail == DND.DROP_DEFAULT || event.detail == DND.DROP_MOVE) {
                    // 从windows拖动文件进来是DROP_MOVE事件，在这里需要将其改变成DROP_COPY
                    event.detail = DND.DROP_COPY;
                }
                System.out.println("determineTarget");
                TreeItem item = (TreeItem) event.item;
                if (item != null) {
                    pos = ItemUtil.getTreeItemIndex(item);
                    return item.getData();
                } else {
                    if (tree.getItemCount() > 0) {// 默认的target是最后一个item.
                        return tree.getItem(tree.getItemCount() - 1).getData();
                    }
                }

                // 当列表内啥也没有的时候才为null
                return null;
            }

            protected int determineLocation(DropTargetEvent event) {
                // 当列表内啥也没有的时候才为LOCATION_NONE
                if (!(event.item instanceof Item)) {
                    return LOCATION_NONE;
                }
                Item item = (Item) event.item;
                PlayListBean bean = (PlayListBean) item.getData();
                Point coordinates = new Point(event.x, event.y);
                coordinates = tv.getControl().toControl(coordinates);
                if (item != null) {
                    Rectangle bounds = getBounds(item);
                    if (bounds == null) {
                        return LOCATION_NONE;
                    }
                    if ((coordinates.y - bounds.y) < 10) {
                        return LOCATION_BEFORE;
                    } else {
                        return LOCATION_AFTER;
                    }
                }
                return LOCATION_ON;
            }

            @Override
            public boolean performDrop(Object data) {
                System.out.println("performDrop");

                if (getCurrentLocation() == LOCATION_NONE) {
                    pos = tree.getItemCount();
                } else {
                    pos = pos + (getCurrentLocation() - 1);
                }

                System.out.println("pos=" + pos);
                PlayListBean target = (PlayListBean) getCurrentTarget();
                if (data instanceof IStructuredSelection) {
                    // 一旦使用了LocalSelectionTransfer，就要写这个if
                    IStructuredSelection selection = (IStructuredSelection) data;
                    PlayListBean[] dragItem = (PlayListBean[]) selection.toList()
                            .toArray(new PlayListBean[selection.size()]);
                    int upcount = 0;// upcount表示相对于pos向前移动了的item的个数,就是位置>=pos的
                    int downcount = 0;// downcount表示相对于pos向后移动了的item的个数

                    for (int i = 0; i < dragItem.length; i++) {
                        PlayListBean obj = dragItem[i];
                        playList.add(pos + i, obj);
                        if (draggedItemIndex[i] >= pos) {
                            upcount++;
                        } else {
                            downcount++;
                        }
                    }

                    int i2 = 0; // i2表示向前移动了的item的下标[第一个向前移动的i2=0]
                    for (int i = 0; i < draggedItemIndex.length; i++) {
                        if (draggedItemIndex[i] < pos) {// 在pos前的
                            playList.remove(draggedItemIndex[i] - i);
                        } else if (draggedItemIndex[i] >= pos) {// 在pos后的
                            playList.remove(draggedItemIndex[i] + upcount - (i2++));
                        }
                    }
                    tv.setInput(playList);
                    tv.refresh();
                    return true;

                } else {
                    String[] files = (String[]) data;
                    TreeItem[] itemAdded = new TreeItem[files.length];
                    for (int i = 0; i < files.length; i++) {
                        File ff = new File(files[i]);
                        PlayListBean bean = new PlayListBean();
                        bean.setName(ff.getName());
                        bean.setPath(ff.getAbsolutePath());
                        playList.add(pos + i, bean);
                        tv.setInput(playList);
                    }

                    // 保持拖进来的item为选中状态（笨方法一个）
                    for (int i = 0; i < files.length; i++) {
                        tv.setSelection(new StructuredSelection(playList.get(pos + i)));
                        itemAdded[i] = tree.getSelection()[0];
                    }
                    tree.setSelection(itemAdded);

                    tv.refresh();
                    return true;
                }
            }

            @Override
            public boolean validateDrop(Object target, int operation, TransferData transferType) {
                PlayListBean bean = (PlayListBean) target;
                return true;
            }

        });

    }

    public void clickMusicItem(int expectedIndex) {
        log.info("clickMusicItem,expectedIndex=" + expectedIndex);

        KittenBox box = KittenBox.getApp();
        if (expectedIndex == -1) {
            expectedIndex = ItemUtil.getSelectionIndex(tree);
        } else {
            if (expectedIndex >= tree.getItemCount()) {
                expectedIndex = 0;
            }
            tree.setSelection(tree.getItem(expectedIndex));
        }
        if (expectedIndex == -1) {
            expectedIndex = 0;
            tree.setSelection(tree.getItem(0));
        }

        StructuredSelection s = (StructuredSelection) box.getPlayListPanel().getTreeViewer().getSelection();
        PlayListBean bean = (PlayListBean) s.getFirstElement();
        try {
            // highlight
            if (curMusicIndex != -1) {
                setLastMusicIndex(curMusicIndex);
            } else {
                setLastMusicIndex(expectedIndex);
            }
            setCurMusicIndex(expectedIndex);

            Tree tree = tv.getTree();

            if (!firstPlay && playList.size() > 1) {
                TreeItem lastItem = tree.getItem(getLastMusicIndex()); // 恢复之前行为默认颜色
                lastItem.setImage(new Image[] { null });
                Color color2 = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
                for (int i = 0; i < tree.getColumnCount(); i++) {
                    lastItem.setBackground(i, color2);
                }
            }

            TreeItem item = tree.getItem(getCurMusicIndex()); // 设置新播放行背景色
            item.setImage(new Image(getShell().getDisplay(), "icon/playingMarker.gif"));
            Color color = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);// 红色
            for (int i = 0; i < tree.getColumnCount(); i++) {
                item.setBackground(i, color); // 设置此行的第2列为红色
            }

            firstPlay = false;
            tree.redraw();

            // play

            box.getPlayer().open(new File(bean.getPath()));
            box.getPlayer().play();
            box.currentStatus = box.PLAY;

            String musicName = bean.getName().substring(0, bean.getName().length() - 4);
            this.musicPath = bean.getPath();
            box.getMyTrayItem().setToolTipText(musicName);
            box.getShell().setText(musicName + Default.PRODUCT_NAME);

        } catch (BasicPlayerException e1) {
            // log.error(ErrorUtil.getError(e1));
        } catch (Throwable e1) {
            // log.error(ErrorUtil.getError(e1));
        }
    }

    private void createButtons() {
        Composite buttonComposite;
        GridData gridData;
        RowLayout layout;

        buttonComposite = new Composite(group, SWT.NONE);
        gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.END;
        buttonComposite.setLayoutData(gridData);
        layout = new RowLayout();
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        buttonComposite.setLayout(layout);

        Button addPhoneNumberButton = new Button(buttonComposite, SWT.FLAT);
        addPhoneNumberButton.setToolTipText("add");
        addPhoneNumberButton.setImage(new Image(Display.getDefault(), "icon/add.gif"));
        addPhoneNumberButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                new AddFilesAction().run();
            }
        });

        {
            Button btnRemove = new Button(buttonComposite, SWT.FLAT);
            btnRemove.setToolTipText("remove");
            btnRemove.setImage(new Image(Display.getDefault(), "icon/subtract.gif"));
            btnRemove.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    new RemoveAction().run();
                }
            });
        }
        {
            Button btnDelete = new Button(buttonComposite, SWT.FLAT);
            btnDelete.setToolTipText("delete");
            btnDelete.setImage(new Image(Display.getDefault(), "icon/trash.gif"));
            btnDelete.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    new RemovePhysicalAction().run();
                }
            });
        }

        Button btnLoad = new Button(buttonComposite, SWT.FLAT);
        btnLoad.setToolTipText("load");
        btnLoad.setImage(new Image(Display.getDefault(), "icon/load.gif"));
        btnLoad.addSelectionListener(new SelectionAdapter() {
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
                        ex.printStackTrace();
                    }
                }
            }
        });

        Button btnSave = new Button(buttonComposite, SWT.FLAT);
        btnSave.setToolTipText("save");
        btnSave.setImage(new Image(Display.getDefault(), "icon/save.gif"));
        btnSave.addSelectionListener(new SelectionAdapter() {
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

        buttonComposite.layout();
    }

    public void setPlayListAndRefreshViewer(List<PlayListBean> playList) {
        this.playList = playList;
        tv.setInput(playList);
        tv.refresh();
    }

    public void setPlayList(List<PlayListBean> playList) {
        this.playList = playList;
    }

    public List<PlayListBean> getPlayList() {
        return playList;
    }

    public TreeViewer getTreeViewer() {
        return tv;
    }

    public int getLastMusicIndex() {
        return lastMusicIndex;
    }

    public void setLastMusicIndex(int lastMusicIndex) {
        this.lastMusicIndex = lastMusicIndex;
    }

    public int getCurMusicIndex() {
        return curMusicIndex;
    }

    public void setCurMusicIndex(int curMusicIndex) {
        this.curMusicIndex = curMusicIndex;
    }

}
