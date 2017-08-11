package org.kangel.kittenbox.seek;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.kangel.kittenbox.KittenBox;

public class SeekPanel extends Composite {

    private Slider scale;

    public SeekPanel(Composite parent) {
        super(parent, SWT.NONE);
        setLayout(new GridLayout());

        scale = new Slider(this, SWT.SMOOTH);
        scale.setMaximum(100);
        scale.setPageIncrement(2);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 20;
        scale.setLayoutData(data);

        // Event processing for scale handle movements
        scale.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        seek();
                    }
                });
            }
        });

        layout();
    }

    public void seek() {
        try {
            double position = ((double) scale.getSelection()) / ((double) scale.getMaximum());
            KittenBox.getApp().getPlayer()
                    .seek((int) (position * (Integer) KittenBox.getApp().getAudioInfo().get("audio.length.bytes")));
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    public Slider getScale() {
        return scale;
    }

}
