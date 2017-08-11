package org.kangel.kittenbox.pref;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;

public class WindowsPage extends FieldEditorPreferencePage {
    protected void createFieldEditors() {
        addField(new BooleanFieldEditor(PrefKeys.WINDOW_START_AS_TRAY, "总是以最小化方式启动", getFieldEditorParent()));
        addField(new BooleanFieldEditor(PrefKeys.RESUME_PLAY, "启动时继续播放上次未完成的歌曲", getFieldEditorParent()));
        addField(new BooleanFieldEditor(PrefKeys.WINDOW_SHOW_SYSTEMTRAY_ALLWAYS, "始终显示系统栏图标，最小化时隐藏任务栏按钮。",
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(PrefKeys.WINDOW_HIDE_ON_CLOSE, "点\"关闭窗口\"[X]按钮时，不退出", getFieldEditorParent()));
        addField(new BooleanFieldEditor(PrefKeys.WINDOW_CONFIRM_ON_CLOSE, "退出前显示确认对话框", getFieldEditorParent()));

    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getApplyButton().setVisible(false);
        getDefaultsButton().setVisible(false);
    }

}
