package org.kangel.kittenbox.hotkey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.kangel.kittenbox.pref.PrefKeys;
import org.kitten.core.util.StringUtil;
import org.kitten.swt.util.ResourceManager;

public class ShortcutsPanel extends Composite {
    private Logger log = Logger.getLogger(ShortcutsPanel.class);

    private Group group;
    private Table t;
    private TableViewer tv;
    private List<ShortcutsBean> playList = new ArrayList<ShortcutsBean>();
    private Group group2;
    private Text txtHotkey;
    private Button btnHotkey;

    public ShortcutsPanel(Composite parent) {
        super(parent, SWT.NONE);

        setLayout(new GridLayout());

        createGroup();
        createTable();
        createButtons();

        createGroup2();

        initPlayList(null);

        layout();
    }

    private void createGroup2() {
        group2 = new Group(this, SWT.NONE);
        group2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group2.setLayout(new GridLayout(2, true));
        group2.setText("键(立即生效)");

        txtHotkey = new Text(group2, SWT.BORDER);
        GridData data = new GridData();
        data.widthHint = 130;
        txtHotkey.setLayoutData(data);
        txtHotkey.setEnabled(false);
        txtHotkey.setToolTipText(
                "Type a \"Modifier + Key\" combination, " + "like CTRL + UP. Some keys may not be allowed.");
        txtHotkey.addKeyListener(new KeyAdapter() {
            public void keyPressed(final KeyEvent ev) {
                if (ev.keyCode == 262144 || ev.keyCode == 65536 || ev.keyCode == 131072) {
                    return;
                }
                System.out.println(ev.keyCode);
                System.out.println("char=" + ev.character);
                txtHotkey.setText(GlobalHotkeyManager.interpretKeyEvent(ev));

                PreferenceStore ps = ResourceManager.getPreferenceStore();
                StructuredSelection s = (StructuredSelection) tv.getSelection();
                ShortcutsBean sb = (ShortcutsBean) s.getFirstElement();
                String m = sb.getModifiers();
                txtHotkey.setEnabled(true);
                if (StringUtil.isNotEmpty(m)) {
                    txtHotkey.setText(m + "+" + sb.getKey());
                } else {
                    txtHotkey.setText(sb.getKey());
                }

                GlobalHotkeyManager.getInstance().registerHotkey(new int[] { sb.getId() },
                        new String[] { txtHotkey.getText() });

                switch (sb.getId()) {
                case PrefKeys.HOTKEY_TOGGLE_ID:
                    ps.setValue(PrefKeys.HOTKEY_TOGGLE_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_TOGGLE_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_PLAY_ID:
                    ps.setValue(PrefKeys.HOTKEY_PLAY_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_PLAY_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_PREV_ID:
                    ps.setValue(PrefKeys.HOTKEY_PREV_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_PREV_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_NEXT_ID:
                    ps.setValue(PrefKeys.HOTKEY_NEXT_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_NEXT_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_VOLUME_UP_ID:
                    ps.setValue(PrefKeys.HOTKEY_VOLUME_UP_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_VOLUME_UP_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_VOLUME_DOWN_ID:
                    ps.setValue(PrefKeys.HOTKEY_VOLUME_DOWN_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_VOLUME_DOWN_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_PREF_ID:
                    ps.setValue(PrefKeys.HOTKEY_PREF_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_PREF_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_EXIT_ID:
                    ps.setValue(PrefKeys.HOTKEY_EXIT_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_EXIT_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_FORWARD_ID:
                    ps.setValue(PrefKeys.HOTKEY_FORWARD_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_FORWARD_VALUE, txtHotkey.getText());
                    break;
                case PrefKeys.HOTKEY_BACKWARD_ID:
                    ps.setValue(PrefKeys.HOTKEY_BACKWARD_GLOBAL, true);
                    ps.setValue(PrefKeys.HOTKEY_BACKWARD_VALUE, txtHotkey.getText());
                    break;
                default:
                    break;
                }
                try {
                    ps.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        txtHotkey.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                String text = txtHotkey.getText();
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }
                playList.get(t.getSelectionIndex()).setModifiers(m);
                playList.get(t.getSelectionIndex()).setKey(key);
                tv.refresh();
            }
        });

        btnHotkey = new Button(group2, SWT.CHECK);
        btnHotkey.setText("全局热键");
        btnHotkey.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        btnHotkey.setEnabled(false);
        btnHotkey.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                playList.get(t.getSelectionIndex()).setGlobal(btnHotkey.getSelection());
                tv.refresh();
            }
        });
    }

    private void initPlayList(ShortcutsBean bean) {
        PreferenceStore ps = ResourceManager.getPreferenceStore();
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_TOGGLE_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_TOGGLE_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_TOGGLE_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);
                sb.setAction("打开/隐藏主界面");
            } else {
                sb.setId(PrefKeys.HOTKEY_TOGGLE_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);
                sb.setAction("打开/隐藏主界面");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_PLAY_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_PLAY_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_PLAY_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);
                sb.setAction("播放/暂停");
            } else {
                sb.setId(PrefKeys.HOTKEY_PLAY_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);
                sb.setAction("播放/暂停");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_PREV_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_PREV_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_PREV_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);
                sb.setAction("上一首");
            } else {
                sb.setId(PrefKeys.HOTKEY_PREV_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);
                sb.setAction("上一首");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_NEXT_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_NEXT_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_NEXT_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);
                sb.setAction("下一首");
            } else {
                sb.setId(PrefKeys.HOTKEY_NEXT_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);
                sb.setAction("下一首");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_FORWARD_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_FORWARD_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_FORWARD_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);

                sb.setAction("快进");
            } else {
                sb.setId(PrefKeys.HOTKEY_FORWARD_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);

                sb.setAction("快进");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_BACKWARD_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_BACKWARD_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_BACKWARD_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);

                sb.setAction("快退");
            } else {
                sb.setId(PrefKeys.HOTKEY_BACKWARD_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);

                sb.setAction("快退");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_VOLUME_UP_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_VOLUME_UP_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_VOLUME_UP_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);

                sb.setAction("增大音量");
            } else {
                sb.setId(PrefKeys.HOTKEY_VOLUME_UP_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);

                sb.setAction("增大音量");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_VOLUME_DOWN_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_VOLUME_DOWN_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_VOLUME_DOWN_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);

                sb.setAction("减小音量");
            } else {
                sb.setId(PrefKeys.HOTKEY_VOLUME_DOWN_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);

                sb.setAction("减小音量");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_PREF_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_PREF_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_PREF_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);

                sb.setAction("首选项");
            } else {
                sb.setId(PrefKeys.HOTKEY_PREF_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);

                sb.setAction("首选项");
            }
            playList.add(sb);
        }
        {
            ShortcutsBean sb = new ShortcutsBean();
            if (ps.getString(PrefKeys.HOTKEY_EXIT_VALUE) != null) {
                String text = ps.getString(PrefKeys.HOTKEY_EXIT_VALUE);
                String key = "";
                String m = "";
                int iPlus = text.lastIndexOf("+");
                if (iPlus != -1) {
                    m = text.substring(0, iPlus);
                    key = text.substring(iPlus + 1);
                } else {
                    m = "";
                    key = text;
                }

                sb.setId(PrefKeys.HOTKEY_EXIT_ID);
                sb.setKey(key);
                sb.setModifiers(m);
                sb.setGlobal(true);

                sb.setAction("退出");
            } else {
                sb.setId(PrefKeys.HOTKEY_EXIT_ID);
                sb.setKey("");
                sb.setModifiers("");
                sb.setGlobal(true);

                sb.setAction("退出");
            }
            playList.add(sb);
        }

        tv.setInput(playList);
        tv.refresh();

    }

    private void createGroup() {
        group = new Group(this, SWT.NONE);
        group.setLayout(new GridLayout());

        group.setText("已分配的快捷键");
    }

    private void createTable() {
        t = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 180;
        t.setLayoutData(data);

        t.setLinesVisible(true);
        t.setHeaderVisible(true);

        TableColumn tKey = new TableColumn(t, SWT.LEFT);
        tKey.setText("键");

        TableColumn tM = new TableColumn(t, SWT.NULL);
        tM.setText("控制键");

        TableColumn tGlobal = new TableColumn(t, SWT.NULL);
        tGlobal.setText("全局");

        TableColumn tAction = new TableColumn(t, SWT.NULL);
        tAction.setText("动作");

        tKey.setWidth(50);
        tM.setWidth(60);
        tGlobal.setWidth(50);
        tAction.setWidth(110);

        tv = new TableViewer(t);
        tv.setContentProvider(new HotkeyContentProvider());
        tv.setLabelProvider(new HotkeyLabelProvider());
        t.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StructuredSelection s = (StructuredSelection) tv.getSelection();
                ShortcutsBean sb = (ShortcutsBean) s.getFirstElement();
                String m = sb.getModifiers();
                txtHotkey.setEnabled(true);
                if (StringUtil.isNotEmpty(sb.getKey())) {
                    txtHotkey.setText(m + (m != null ? "+" : "") + sb.getKey());
                } else {
                    txtHotkey.setText("");
                }
                btnHotkey.setEnabled(true);
                btnHotkey.setSelection(sb.isGlobal());
            }

        });
    }

    public void clickMusicItem(int expectedIndex) {
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

        Button btnAdd = new Button(buttonComposite, SWT.PUSH);
        btnAdd.setText("添加新的");
        btnAdd.setLayoutData(new RowData(100, 23));
        btnAdd.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            }
        });

        {
            Button btnRemove = new Button(buttonComposite, SWT.PUSH);
            btnRemove.setText("取消绑定");
            btnRemove.setEnabled(false);
            btnRemove.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                }
            });
        }
        buttonComposite.layout();

    }

    public void setPlayListAndRefreshViewer(List<ShortcutsBean> playList) {
        this.playList = playList;
        tv.setInput(playList);
        tv.refresh();
    }

    public void setPlayList(List<ShortcutsBean> playList) {
        this.playList = playList;
    }

    public List<ShortcutsBean> getPlayList() {
        return playList;
    }

    public TableViewer getTableViewer() {
        return tv;
    }
}
