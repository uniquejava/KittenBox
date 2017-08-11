package org.kangel.kittenbox.pref;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PlayPage extends PreferencePage {
    private Text ipText;

    protected Control createContents(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new GridLayout(2, false));

        IPreferenceStore ps = getPreferenceStore();

        new Label(c, SWT.NONE).setText("每次快退/快进毫秒数:");
        ipText = new Text(c, SWT.BORDER);
        ipText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ipText.setText(ps.getString(PrefKeys.PLAY_BACKWARD_MS));

        new Label(c, SWT.NONE).setText(" ");
        new Label(c, SWT.NONE).setText("（有歌词时会自动快退到上一句)");
        return c;
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getApplyButton().setVisible(false);
        getDefaultsButton().setVisible(false);
    }

    public boolean performOk() {
        if (ipText != null) {
            IPreferenceStore ps = getPreferenceStore();
            ps.setValue(PrefKeys.PLAY_BACKWARD_MS, ipText.getText());
        }
        return true;
    }

    public void performApply() {
        this.performOk();
    }
}
