/*
 * Copyright (c) 2008-2009 Tomas Varaneckas
 * http://www.varaneckas.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kangel.kittenbox.hotkey;

import javax.security.auth.login.Configuration;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.kangel.kittenbox.pref.PrefKeys;
import org.kitten.core.util.StringUtil;
import org.kitten.swt.util.ResourceManager;

// import com.varaneckas.hawkscope.cfg.Configuration;
// import com.varaneckas.hawkscope.cfg.ConfigurationFactory;
// import com.varaneckas.hawkscope.util.IOUtils;
// import com.varaneckas.hawkscope.util.OSUtils;
// import com.varaneckas.hawkscope.util.OSUtils.OS;

/**
 * Global hotkey listener for invoking hawkscope menu
 * 
 * TODO implementation for Mac OS X
 * 
 * @author Tomas Varaneckas
 * @version $Id: GlobalHotkeyManager.java 591 2009-06-07 15:18:50Z
 *          tomas.varaneckas $
 */
public abstract class GlobalHotkeyManager {

    /**
     * Global Hotkey Listener instance
     */
    private static GlobalHotkeyManager instance = null;

    /**
     * Loads the required instance if possible
     * 
     * @return instance
     */
    public static synchronized GlobalHotkeyManager getInstance() {
        if (instance == null) {
            instance = chooseImpl();
        }
        return instance;
    }

    public abstract void clearHotkey(int id);

    /**
     * Implementation should clear all defined hotkeys
     */
    public abstract void clearHotkeys();

    /**
     * Implementation should register AWT hotkey
     * 
     * @param specKey
     *            AWT modifier, like InputEvent.CRTL_MASK
     * @param key
     *            AWT key, like InputEvent.VK_SPACE
     */
    // public abstract void registerHotkey(int specKey, int key);
    public abstract void registerHotkey(int[] identifier, String[] combination);

    /**
     * Chooses {@link GlobalHotkeyManager} implementation according to OS
     * 
     * @return GlobalHotkeyManager or null in case OS does not support one
     */
    private static GlobalHotkeyManager chooseImpl() {
        // switch (OSUtils.CURRENT_OS) {
        // case UNIX:
        // return new X11GlobalHotkeyManager();
        // case WIN:
        return new WinGlobalHotkeyManager();
        // default:
        // return null;
        // }
    }

    /**
     * Loads native library located inside some jar in classpath
     * 
     * @param jarLib
     *            library name
     * @return success?
     */
    // protected boolean loadJarLibrary(final String jarLib) {
    // final String tempLib = System.getProperty("java.io.tmpdir")
    // + File.separator + jarLib;
    // boolean copied = IOUtils.copyFile(jarLib, tempLib);
    // if (!copied) {
    // return false;
    // }
    // System.load(tempLib);
    // return true;
    // }
    /**
     * Configures the GlobalHotkeyManager according to settings. Uses
     * {@link Configuration} to get required parameters.
     */
    public void configure() {
        // final Configuration cfg =
        // ConfigurationFactory.getConfigurationFactory()
        // .getConfiguration();
        // if (cfg.isHotkeyEnabled()) {
        // final int modifier = cfg.getHotkeyModifier();
        // final int key = cfg.getHotkey();
        // if (modifier > 0 && key > 0) {
        // registerHotkey(modifier, key);
        // }
        // }
        // int modifier=1;
        // int key = 1;
        PreferenceStore ps = ResourceManager.getPreferenceStore();
        int[] ids = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        String[] keys = new String[ids.length];

        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_TOGGLE_VALUE))) {
            ids[0] = PrefKeys.HOTKEY_TOGGLE_ID;
            keys[0] = ps.getString(PrefKeys.HOTKEY_TOGGLE_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_PLAY_VALUE))) {
            ids[1] = PrefKeys.HOTKEY_PLAY_ID;
            keys[1] = ps.getString(PrefKeys.HOTKEY_PLAY_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_PREV_VALUE))) {
            ids[2] = PrefKeys.HOTKEY_PREV_ID;
            keys[2] = ps.getString(PrefKeys.HOTKEY_PREV_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_NEXT_VALUE))) {
            ids[3] = PrefKeys.HOTKEY_NEXT_ID;
            keys[3] = ps.getString(PrefKeys.HOTKEY_NEXT_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_VOLUME_UP_VALUE))) {
            ids[4] = PrefKeys.HOTKEY_VOLUME_UP_ID;
            keys[4] = ps.getString(PrefKeys.HOTKEY_VOLUME_UP_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_VOLUME_DOWN_VALUE))) {
            ids[5] = PrefKeys.HOTKEY_VOLUME_DOWN_ID;
            keys[5] = ps.getString(PrefKeys.HOTKEY_VOLUME_DOWN_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_PREF_VALUE))) {
            ids[6] = PrefKeys.HOTKEY_PREF_ID;
            keys[6] = ps.getString(PrefKeys.HOTKEY_PREF_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_EXIT_VALUE))) {
            ids[7] = PrefKeys.HOTKEY_EXIT_ID;
            keys[7] = ps.getString(PrefKeys.HOTKEY_EXIT_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_FORWARD_VALUE))) {
            ids[8] = PrefKeys.HOTKEY_FORWARD_ID;
            keys[8] = ps.getString(PrefKeys.HOTKEY_FORWARD_VALUE);
        }
        if (StringUtil.isNotEmpty(ps.getString(PrefKeys.HOTKEY_BACKWARD_VALUE))) {
            ids[9] = PrefKeys.HOTKEY_BACKWARD_ID;
            keys[9] = ps.getString(PrefKeys.HOTKEY_BACKWARD_VALUE);
        }
        registerHotkey(ids, keys);
    }

    /**
     * Interprets KeyEvent and returns something like "Ctrl + H" or "Alt + Space".
     * Works only with ASCII keys from 32 to 126.
     * 
     * @param ev
     *            KeyEvent to interpret
     * @return interpretation string
     */
    public static String interpretKeyEvent(final KeyEvent ev) {
        String repr = "";
        ev.doit = false;
        int stateMask = ev.stateMask;

        if ((stateMask & SWT.CTRL) != 0)
            repr += "+Ctrl";
        if ((stateMask & SWT.ALT) != 0)
            repr += "+Alt";
        if ((stateMask & SWT.SHIFT) != 0)
            repr += "+Shift";
        if ((stateMask & SWT.COMMAND) != 0)
            repr += "+Win";
        if (repr.length() > 0) {
            repr = repr.substring(1);
        }

        System.out.println("repr=" + repr);
        // add by yinsb
        boolean arrowKey = false;
        switch (ev.keyCode) {
        case SWT.ARROW_UP:
            repr += "+Up";
            arrowKey = true;
            break;
        case SWT.ARROW_DOWN:
            repr += "+Down";
            arrowKey = true;
            break;
        case SWT.ARROW_LEFT:
            repr += "+Left";
            arrowKey = true;
            break;
        case SWT.ARROW_RIGHT:
            repr += "+Right";
            arrowKey = true;
            break;
        }
        if (arrowKey) {
            return repr;
        }
        // ~add

        if ((!arrowKey) && (ev.keyCode < 32 || ev.keyCode > 126)) {
            return "";
        }
        if (!arrowKey) {
            char c = (char) ev.keyCode;
            if (c == ' ') {
                repr += "Space";
            } else {
                repr += "+" + ("" + c).toUpperCase();
            }
        }
        return repr;
    }

}
