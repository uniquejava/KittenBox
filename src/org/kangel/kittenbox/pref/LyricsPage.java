package org.kangel.kittenbox.pref;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;

public class LyricsPage extends FieldEditorPreferencePage {
    public LyricsPage() {
        super(GRID);
    }

    protected void createFieldEditors() {
        addField(new DirectoryFieldEditor(PrefKeys.LYRICS_DIR, "歌词存放目录", getFieldEditorParent()));
        addField(new IntegerFieldEditor(PrefKeys.LYRICS_OFFSET, "每次调整的歌词偏移量:", getFieldEditorParent()));
        addField(new BooleanFieldEditor(PrefKeys.LYRICS_DESKTOP, "是否开启桌面歌词", getFieldEditorParent()));
        addField(new IntegerFieldEditor(PrefKeys.LYRICS_X, "桌面歌词X坐标:", getFieldEditorParent()));
        addField(new IntegerFieldEditor(PrefKeys.LYRICS_Y, "桌面歌词Y坐标:", getFieldEditorParent()));
        addField(new IntegerFieldEditor(PrefKeys.LYRICS_FONTSIZE, "桌面歌词字体大小:", getFieldEditorParent()));
        addField(new IntegerFieldEditor(PrefKeys.LYRICS_FONTSTYLE, "桌面歌词字体颜色:", getFieldEditorParent()));
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getApplyButton().setVisible(false);
        getDefaultsButton().setVisible(false);
    }

}
