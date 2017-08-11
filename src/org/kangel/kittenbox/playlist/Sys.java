package org.kangel.kittenbox.playlist;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kitten.core.util.FileUtil;

/**
 * 公共参数配置文件.会尝试读取user.dir下的config.properties<br>
 * 使用方法:<br>
 * 
 * <pre>
 * Sys as = Sys.getInstance();
 * String pageSize = as.get(&quot;page.size.defaultValue&quot;);
 * </pre>
 * 
 * 
 * @author cyper
 * @version 1.0
 */

public class Sys {
    private static Logger log = Logger.getLogger(Sys.class);
    private static Properties p = new Properties();
    private static Sys instance = new Sys();

    private Sys() {
        InputStream is = null;
        try {
            is = new FileInputStream(System.getProperty("user.dir") + "/config.properties");
            p.load(is);
        } catch (Exception e) {
            log.error("在用户目录下没有发现【config.properties】属性文件");
        } finally {
            FileUtil.close(is);
        }
        log.info("config.properties is reloaded.");
    }

    public static Sys getInstance() {
        if (instance == null) {
            instance = new Sys();
        }
        return instance;
    }

    public static void reload() {
        p.clear();
        instance = null;
        getInstance();
    }

    /**
     * 根据传入的键值从accaud.properties中读取参数
     * 
     * @param key
     * @return
     */
    public String get(String key) {
        return p.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return p.getProperty(key, defaultValue);
    }

    public static Properties getP() {
        return p;
    }

}
