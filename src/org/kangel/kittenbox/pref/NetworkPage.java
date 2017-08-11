package org.kangel.kittenbox.pref;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.kangel.kittenbox.network.Proxy;
import org.kitten.core.util.ErrorUtil;
import org.kitten.swt.util.ResourceManager;

public class NetworkPage extends PreferencePage {
    private static Logger log = Logger.getLogger(NetworkPage.class);
    private Button noProxyRadio;
    private Button proxyRadio;
    private Composite proxyComposite;
    private Text ipText;
    private Text portText;
    private Text userText;
    private Text pwdText;

    protected Control createContents(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayout(new GridLayout(3, false));

        IPreferenceStore ps = getPreferenceStore();

        noProxyRadio = new Button(c, SWT.RADIO);
        noProxyRadio.setSelection(!ps.getBoolean(PrefKeys.PROXY_ENABLE));
        new Label(c, SWT.LEFT).setText("不使用代理服务器");
        new Label(c, SWT.LEFT).setText("");

        proxyRadio = new Button(c, SWT.RADIO);
        proxyRadio.setSelection(ps.getBoolean(PrefKeys.PROXY_ENABLE));
        proxyRadio.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                enableProxy(proxyRadio.getSelection());
            }

        });
        new Label(c, SWT.LEFT).setText("使用自定义的代理服务器");
        new Label(c, SWT.LEFT).setText("");

        new Label(c, SWT.LEFT).setText("");
        proxyComposite = new Composite(c, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        proxyComposite.setLayoutData(gd);
        proxyComposite.setLayout(new GridLayout(4, false));
        new Label(proxyComposite, SWT.NONE).setText("服务器:");
        ipText = new Text(proxyComposite, SWT.BORDER);
        ipText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ipText.setText(ps.getString(PrefKeys.PROXY_IP));

        new Label(proxyComposite, SWT.NONE).setText("端口:");
        portText = new Text(proxyComposite, SWT.BORDER);
        portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        portText.setText(ps.getString(PrefKeys.PROXY_PORT));

        new Label(proxyComposite, SWT.NONE).setText("用户名:");
        userText = new Text(proxyComposite, SWT.BORDER);
        userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        userText.setText(ps.getString(PrefKeys.PROXY_USER));

        new Label(proxyComposite, SWT.NONE).setText("密码:");
        pwdText = new Text(proxyComposite, SWT.BORDER);
        pwdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        pwdText.setText(ps.getString(PrefKeys.PROXY_PWD));

        enableProxy(proxyRadio.getSelection());
        return c;
    }

    private void enableProxy(boolean b) {
        if (b) {
            ipText.setEditable(true);
            ipText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            portText.setEditable(true);
            portText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            userText.setEditable(true);
            userText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            pwdText.setEditable(true);
            pwdText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        } else {
            ipText.setEditable(false);
            ipText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            portText.setEditable(false);
            portText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            userText.setEditable(false);
            userText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            pwdText.setEditable(false);
            pwdText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        }
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getApplyButton().setVisible(false);
        getDefaultsButton().setVisible(false);
    }

    public boolean performOk() {
        // 当此页没有点击时，proxyRadio还是null
        if (proxyRadio != null) {
            IPreferenceStore ps = getPreferenceStore();
            ps.setValue(PrefKeys.PROXY_ENABLE, proxyRadio.getSelection());
            if (proxyRadio.getSelection()) {
                ps.setValue(PrefKeys.PROXY_IP, ipText.getText());
                ps.setValue(PrefKeys.PROXY_PORT, portText.getText());
                ps.setValue(PrefKeys.PROXY_USER, userText.getText());
                ps.setValue(PrefKeys.PROXY_PWD, pwdText.getText());
            }
            try {
                ResourceManager.getPreferenceStore().save();
            } catch (Exception e) {
                log.error(ErrorUtil.getError(e));
            }
            Proxy.reload();
        }
        return true;
    }

    public void performApply() {
        this.performOk();
    }
}
