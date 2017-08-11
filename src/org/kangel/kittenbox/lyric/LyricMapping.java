package org.kangel.kittenbox.lyric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kitten.core.C;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

/**
 * 保存处理中的批次信息，以便下次启动时可以恢复.
 * 
 * @author cyper.yin(uniquejava@gmail.com)
 * @version 1.0
 * @since 2009-12-24
 */
public class LyricMapping {
    private static Logger log = Logger.getLogger(LyricMapping.class);
    public final static String File_PATH = C.UD + C.FS + "data/LyricMapping.xml";
    static {
        if (!new File(File_PATH).exists()) {
            try {
                new File(File_PATH).createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private static LyricMapping instance = null;
    private Map<String, String> maps = null;

    private LyricMapping() {
    }

    public static LyricMapping getInstance() {
        if (instance == null) {
            read();
        }
        return instance;
    }

    private static void read() {
        InputStream is = null;
        Reader r = null;
        try {
            is = new FileInputStream(File_PATH);
            // 不加编码会报错Caused by: org.filePath.v1.XmlPullParserException: only
            // whitespace content allowed before start tag and not C (position:
            // START_DOCUMENT seen C... @1:1)
            r = new InputStreamReader(is, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        XStream xs = new XStream();
        xs.alias("LyricMapping", LyricMapping.class);
        try {

            instance = (LyricMapping) xs.fromXML(r);
        } catch (StreamException e) {
            log.warn(e.getMessage());
            instance = new LyricMapping();
        }
    }

    public void save() throws Exception {
        Writer w;
        try {
            w = new OutputStreamWriter(new FileOutputStream(File_PATH), "UTF-8");
        } catch (Exception e) {
            throw e;
        }
        XStream xs = new XStream();
        xs.alias("LyricMapping", LyricMapping.class);
        xs.toXML(this, w);
        // String newS = FileHelper.getFileContent(new File(MSG_PATH), "UTF-8");
        // System.out.println(newS);
        // FileHelper.setFileContent(new File(MSG_PATH), newS, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            LyricMapping m = LyricMapping.getInstance();
            m.put("test2", "DFDFD22");
            m.save();

            System.out.println(m.get("test"));
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

    }

    public String get(String name) {
        if (maps != null) {
            return maps.get(name);
        }
        return null;
    }

    public void remove(String name) {
        if (maps != null) {
            maps.remove(name);
            try {
                save();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void put(String key, String value) {
        if (maps == null) {
            maps = new HashMap<String, String>();
        }
        maps.put(key, value);
        try {
            save();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getMappingCount() {
        if (maps != null) {
            return maps.size();
        } else {
            return 0;
        }
    }
}
