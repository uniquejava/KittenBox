package org.kangel.kittenbox.util;

import org.apache.log4j.Logger;
import org.kitten.core.util.ErrorUtil;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
/**
 * Created at 2017-08. <br>
 * see https://github.com/cnfree/SWT-Win32-Extension/issues/2
 * 
 * @author cyper
 *
 */
public class VolumeManager {
    private static Logger log = Logger.getLogger(VolumeManager.class);

    private VolumeManager() {

    }

    public static void setVolume(BasicPlayer bp, int selection) {
        // Mixer.setMixerVolume(0, (int) ((((float) (volume.getSelection())) / 100) *
        // Mixer.MAX_VOL_VALUE), Mixer.TYPE_VOLUMECONTROL);
        // Set Volume (0 to 1.0).
        // setGain should be called after control.play().
        try {
            double gain = selection / 100.0;
            bp.setGain(gain);
        } catch (BasicPlayerException e) {
            log.error(ErrorUtil.getError(e));
        }
    }

}
