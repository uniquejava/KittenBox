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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;
import org.kitten.core.util.StringUtil;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

/**
 * Key listener for Windows. Based on JIntellitype (http://www.malloware.com)
 * JIntellitype.dll should be copied to Windows/System32 for development. The
 * dll is copied to required folder at user side by the installer.
 * 
 * @author Tomas Varaneckas
 * @version $Id: WinGlobalHotkeyManager.java 591 2009-06-07 15:18:50Z
 *          tomas.varaneckas $
 */
public class WinGlobalHotkeyManager extends GlobalHotkeyManager {

    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(WinGlobalHotkeyManager.class);

    /**
     * Lazy loading {@link HotkeyListener} that invokes Hawkscope menu at cursor
     * location
     */
    private HotkeyListener listener = null;

    /**
     * JIntellitype hotkey manager
     */
    private final JIntellitype manager;

    /**
     * Constructor that loads the {@link JIntellitype} object
     */
    public WinGlobalHotkeyManager() {
        this.manager = JIntellitype.getInstance();
    }

    /**
     * Gets the {@link HotkeyListener} for use
     * 
     * @return
     */
    protected HotkeyListener getListener() {
        if (listener == null) {
            synchronized (this) {
                if (listener == null) {
                    listener = new WinHotkeyListener();
                }
            }
        }
        return listener;
    }

    @Override
    protected void finalize() throws Throwable {
        manager.cleanUp();
    }

    @Override
    public void clearHotkey(int id) {
        manager.unregisterHotKey(id);
    }

    public void clearHotkeys() {
        if (listener != null) {
            manager.unregisterHotKey(1);
            manager.removeHotKeyListener(listener);
        }
    }

    @Override
    public void registerHotkey(final int[] identifier, final String[] key) {
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                try {
                    // manager.registerSwingHotKey(1, specKey, key);
                    for (int i = 0; i < identifier.length; i++) {
                        if (identifier[i] != -1 && StringUtil.isNotEmpty(key[i])) {
                            log.info("registerHotKey(" + identifier[i] + "," + key[i] + ")");
                            manager.registerHotKey(identifier[i], key[i]);
                        } else {
                            log.info("unregisterHotKey(" + identifier[i] + "," + key[i] + ")");
                            manager.unregisterHotKey(identifier[i]);
                        }
                    }
                    manager.removeHotKeyListener(getListener());
                    manager.addHotKeyListener(getListener());
                } catch (Exception e) {
                    log.error("Hotkey conflict!", e);
                    manager.cleanUp();
                }
            }
        });
    }

}
