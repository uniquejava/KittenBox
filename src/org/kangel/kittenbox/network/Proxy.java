package org.kangel.kittenbox.network;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.PreferenceStore;
import org.kangel.kittenbox.pref.PrefKeys;
import org.kitten.swt.util.ResourceManager;

/**
 * 
 * @author cyper.yin(uniquejava@gmail.com)
 * @version 1.0
 * @since 2010-1-9
 */
public class Proxy {
    private static Logger log = Logger.getLogger(Proxy.class);
    /**
     * 是否使用代理服务器
     */
    private boolean useProxy;
    private String ip;
    private int port;
    private String user;
    private static Proxy instance;

    private Proxy() {
    }

    public static Proxy getInstance() {
        if (instance == null) {
            reload();
        }
        return instance;
    }

    public static void reload() {
        instance = new Proxy();
        PreferenceStore ps = ResourceManager.getPreferenceStore();
        instance.setUseProxy(ps.getBoolean(PrefKeys.PROXY_ENABLE));
        instance.setIp(ps.getString(PrefKeys.PROXY_IP));
        instance.setPort(ps.getInt(PrefKeys.PROXY_PORT));
        instance.setUser(ps.getString(PrefKeys.PROXY_USER));
        instance.setPwd(ps.getString(PrefKeys.PROXY_PWD));
    }

    private String pwd;

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
