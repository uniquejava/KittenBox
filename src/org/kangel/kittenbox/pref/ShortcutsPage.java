package org.kangel.kittenbox.pref;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.kangel.kittenbox.hotkey.ShortcutsPanel;

public class ShortcutsPage extends PreferencePage {
    protected Control createContents(Composite c) {
        ShortcutsPanel sp = new ShortcutsPanel(c);
        return sp;
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getApplyButton().setVisible(false);
        getDefaultsButton().setVisible(false);
    }

}
