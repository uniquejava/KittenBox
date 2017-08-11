package org.kangel.kittenbox.lyric;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.widgets.Spinner;
import org.kangel.kittenbox.KittenBox;

public class AllAdjustShell {
    private static Logger log = Logger.getLogger(AllAdjustShell.class);
    private Shell shell;
    private Button mapBtn;
    private Spinner spinner;

    public AllAdjustShell() {

        shell = new Shell();

        shell.setLayout(new GridLayout(4, false));

        // 第一行搜索框.
        Label l = new Label(shell, SWT.NONE);
        l.setText("请输入要调整歌词的偏移时间:");
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        l.setLayoutData(gd);

        spinner = new Spinner(shell, SWT.NONE);
        spinner.setIncrement(100);
        spinner.setMaximum(30000);
        spinner.setMinimum(-30000);
        spinner.setSelection(1000);
        new Label(shell, SWT.NONE).setText("毫秒");
        // searchBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // 表格上面的那一行文字
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 4;
        Label follow = new Label(shell, SWT.NONE);
        follow.setText("提示:负数表示提前,正数表示延后");
        follow.setLayoutData(gd);

        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        gd2.horizontalAlignment = GridData.END;
        mapBtn = new Button(shell, SWT.NONE);
        mapBtn.setText("确定");
        mapBtn.setImage(new Image(Display.getDefault(), "icon/lrc_yes.jpg"));
        mapBtn.setLayoutData(gd2);
        mapBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                KittenBox box = KittenBox.getApp();
                LrcFile lrcFile = box.getLrcFile();
                if (lrcFile != null) {
                    lrcFile.incrementOffset(spinner.getSelection());
                    lrcFile.saveOffset();
                    KittenBox.getApp().reloadLrcFile();
                }
                getShell().dispose();
            }

        });
        gd2 = new GridData();
        gd2.horizontalSpan = 2;
        gd2.horizontalAlignment = GridData.BEGINNING;
        Button cancelBtn = new Button(shell, SWT.NONE);
        cancelBtn.setText("取 消");
        cancelBtn.setLayoutData(gd2);
        cancelBtn.setImage(new Image(Display.getDefault(), "icon/lrc_cancel.jpg"));
        cancelBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                getShell().dispose();
            }

        });

        shell.setSize(280, 100);
        shell.setText("指定歌词文件");

        shell.layout();
    }

    public void open() {
        shell.open();
    }

    public Shell getShell() {
        return shell;
    }

}
