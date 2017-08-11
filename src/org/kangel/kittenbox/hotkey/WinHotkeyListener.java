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

import org.eclipse.swt.widgets.Display;
import org.kangel.kittenbox.KittenBox;
import org.kangel.kittenbox.pref.PrefKeys;

import com.melloware.jintellitype.HotkeyListener;

/**
 * Windows {@link HotkeyListener}.
 * 
 * Displays Hawkscope menu at cursor location on any shortcut key
 * 
 * @author Tomas Varaneckas
 * @version $Id: WinHotkeyListener.java 591 2009-06-07 15:18:50Z
 *          tomas.varaneckas $
 */
public class WinHotkeyListener implements HotkeyListener {

    /**
     * Displays Hawkscope menu at cursor location
     */

    public void onHotKey(final int hk) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                switch (hk) {
                case PrefKeys.HOTKEY_TOGGLE_ID:
                    KittenBox.getApp().toggleDisplay();
                    break;
                case PrefKeys.HOTKEY_PLAY_ID:
                    KittenBox.getApp().pauseOrPlay();
                    break;
                case PrefKeys.HOTKEY_PREV_ID:
                    KittenBox.getApp().playPrevious();
                    break;
                case PrefKeys.HOTKEY_NEXT_ID:
                    KittenBox.getApp().playNext();
                    break;
                case PrefKeys.HOTKEY_FORWARD_ID:
                    KittenBox.getApp().forward();
                    break;
                case PrefKeys.HOTKEY_BACKWARD_ID:
                    KittenBox.getApp().backward();
                    break;
                case PrefKeys.HOTKEY_VOLUME_UP_ID:
                    KittenBox.getApp().volumeUp();
                    break;
                case PrefKeys.HOTKEY_VOLUME_DOWN_ID:
                    KittenBox.getApp().volumeDown();
                    break;
                case PrefKeys.HOTKEY_PREF_ID:
                    if (KittenBox.getApp().getPrefDialog() != null) {
                        KittenBox.getApp().getPrefDialog().close();
                    } else {
                        KittenBox.getApp().openPrefDialog();
                    }
                    break;
                case PrefKeys.HOTKEY_EXIT_ID:
                    KittenBox.getApp().exit();
                    break;
                default:
                    break;
                }
            }
        });

        // MenuFactory.getMainMenu().getSwtMenuObject().getDisplay().syncExec(
        // new Runnable() {
        // public void run() {
        // final StateEvent se = new StateEvent();
        // final Point loc = Display.getDefault()
        // .getCursorLocation();
        // se.setX(loc.x);
        // se.setY(loc.y);
        // MenuFactory.getMainMenu().getState().act(se);
        // }
        // });
    }
}
