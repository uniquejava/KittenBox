package org.kangel.kittenbox.playlist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.sf.feeling.swt.win32.extension.sound.Mixer;

public class VolumeShell {
    private Shell shell;
    private Scale xx;

    public VolumeShell(Point size, Point p) {

        shell = new Shell(SWT.SHELL_TRIM);

        shell.setLayout(new GridLayout());

        createSearchBox();
        shell.setSize(size.x, 100);
        shell.setText("Volume");
        shell.setLocation(p);
        shell.layout();
    }

    public void open() {
        shell.open();
    }

    private void createSearchBox() {
        xx = new Scale(shell, SWT.VERTICAL);
        xx.setMinimum(1);
        xx.setMaximum(100);
        xx.setPageIncrement(10);
        xx.setSelection(Mixer.getMixerVolume(0, Mixer.TYPE_VOLUMECONTROL));
        xx.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Mixer.setMixerVolume(0, (int) ((((float) (xx.getSelection())) / 100) * Mixer.MAX_VOL_VALUE),
                        Mixer.TYPE_VOLUMECONTROL);
            }

        });
    }

}
