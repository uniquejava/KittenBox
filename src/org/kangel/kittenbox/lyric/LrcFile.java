package org.kangel.kittenbox.lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.kitten.core.io.FileHelper;
import org.kitten.core.io.ILineProcessor;
import org.kitten.core.util.ErrorUtil;
import org.kitten.core.util.REUtil;
import org.kitten.core.util.StringUtil;

/**
 * 将所有的时间戳转成long型的毫秒数.放在list中，然后按时间戳在list中将Lyric对象从小到大排好序．
 * 
 * @author cyper.yin(uniquejava@gmail.com)
 * @version 1.0
 * @since 2009-12-31
 */
public class LrcFile {
    private static Logger log = Logger.getLogger(LrcFile.class);
    private List<Lyric> lyrics = new ArrayList<Lyric>();
    private int currentIndex = 0;
    private String encoding;
    private String ti;
    private String ar;
    private String al;
    private String by;
    private int offset;
    private int originOffset;
    private String filePath;
    private LrcFile xx;

    public LrcFile() {
        xx = this;
    }

    public String getLyricEncoding(final String strFile) {
        String encoding[] = new String[] { "GBK", "UTF-8", "UTF-16", "ISO8859-1" };
        for (int i = 0; i < encoding.length; i++) {
            if (tryOneEncoding(new File(strFile), encoding[i]) != null) {
                return encoding[i];
            }
        }
        log.warn("找不到编码方式.");
        return null;
    }

    private String tryOneEncoding(final File f, final String currentEncoding) {
        FileInputStream fr = null;
        BufferedReader br = null;
        String ret = null;
        if (f.length() > 0) {
            try {
                fr = new FileInputStream(f);
                if (currentEncoding != null && !currentEncoding.equals("")) {
                    br = new BufferedReader(new InputStreamReader(fr, currentEncoding));
                } else {
                    br = new BufferedReader(new InputStreamReader(fr));
                }
                String tempStr = "";
                if ((tempStr = br.readLine()) != null) {
                    log.info(currentEncoding + "==>" + tempStr);
                    int cnt = REUtil.getStrCount(tempStr, "(\\[\\w+:.*\\])");
                    if (cnt > 0) {
                        ret = currentEncoding;
                    }
                }
            } catch (Exception e) {
                log.error(ErrorUtil.getError(e));
            } finally {
                FileHelper.close(fr);
                FileHelper.close(br);
            }
        }
        return ret;
    }

    public void saveOffset() {
        try {
            String origin = FileHelper.getFileContent(new File(this.filePath), this.encoding);
            Pattern p = Pattern.compile("\\[offset:-?[0-9]*\\]");
            if (p.matcher(origin).find()) {
                origin = p.matcher(origin).replaceFirst("\\[offset:" + this.offset + "\\]");
                FileHelper.setFileContent(new File(this.filePath), origin, this.encoding);
            } else {// 本来就没有offset的情况
                Pattern p2 = Pattern.compile("\\[[0-9]+:", Pattern.MULTILINE | Pattern.DOTALL);
                Matcher m = p2.matcher(origin);
                if (m.find()) {
                    // 插入offset标记到第一个时间戳之前.
                    origin = m.replaceFirst("\\[offset:" + this.offset + "\\]\r\n" + m.group(0));
                    FileHelper.setFileContent(new File(this.filePath), origin, this.encoding);
                }
            }
        } catch (Throwable e) {
            log.error(ErrorUtil.getError(e));
        }
    }

    /**
     * 
     * @param c
     *            要修改的句子
     * @param offset
     *            修改的偏移量
     */
    public void saveThisLyric(Lyric c, int offset) {
        try {
            String origin = FileHelper.getFileContent(new File(this.filePath), this.encoding);
            log.info(c.toTs() + "==>" + toTs(c.getLongms() + offset));
            origin = origin.replace(c.toTs(), toTs(c.getLongms() + offset));
            FileHelper.setFileContent(new File(this.filePath), origin, this.encoding);
        } catch (Throwable e) {
            log.error(ErrorUtil.getError(e));
        }
    }

    public void saveAfterLyric(Lyric cc, int offset) {
        try {
            String origin = FileHelper.getFileContent(new File(this.filePath), this.encoding);
            int size = this.lyrics.size();
            for (int i = cc.getIndex() + 1; i < size; i++) {
                // 之后的都要改
                Lyric c = lyrics.get(i);
                log.info(c.toTs() + "==>" + toTs(c.getLongms() + offset));
                origin = origin.replace(c.toTs(), toTs(c.getLongms() + offset));
            }
            FileHelper.setFileContent(new File(this.filePath), origin, this.encoding);
        } catch (Throwable e) {
            log.error(ErrorUtil.getError(e));
        }
    }

    public List<Lyric> read(final String f) {
        this.filePath = f;
        this.encoding = getLyricEncoding(f);

        final List<Lyric> lrcs = new ArrayList<Lyric>();
        try {
            FileHelper.processFileByLine(new File(f), encoding, new ILineProcessor() {
                public boolean processLine(String str) {
                    log.info(str);
                    String[] off = REUtil.getREGroupVector(str, "\\[offset:(-?[0-9]+)\\]");
                    if (off != null && off.length > 0) {
                        offset = StringUtil.nvl(off[0], 0);
                        originOffset = offset;
                    } else {
                        // 有的没有毫秒数，比如:
                        // [04:51.30][03:43.89][02:47][01:07.20]伤神的 伤人的
                        // 太伤心
                        // [02:11][00:52]才发现 爱情竟是一场残酷的考验
                        // []的对数就是时间戳的个数
                        int timestampCount = REUtil.getStrCount(str, "\\[[0-9]+:[^]]*\\]");
                        // log.info("timestampCount=" + timestampCount);

                        StringBuffer sb = new StringBuffer("\\[([0-9]+):([0-9]+)(?:\\.([0-9]+))?\\]");
                        String token = "(?:\\[([0-9]+):([0-9]+)(?:\\.([0-9]+))?\\])?";
                        for (int i = 0; i < timestampCount - 1; i++) {
                            sb.append(token);
                        }
                        sb.append("(.*)");

                        String[] strs = REUtil.getREGroupVector(str, sb.toString());
                        if (strs.length > 0) {
                            for (int i = 0; i < timestampCount; i++) {
                                Lyric lrc = new Lyric();
                                lrc.setMinute(StringUtil.nvl(strs[i * 3], 0));
                                lrc.setSecond(StringUtil.nvl(strs[i * 3 + 1], 0));
                                lrc.setMiniSecond(StringUtil.nvl(strs[i * 3 + 2], 0));
                                lrc.setLongms(lrc.getMinute() * 60 * 1000 + lrc.getSecond() * 1000
                                        + lrc.getMiniSecond() * 10);
                                lrc.setContent(strs[strs.length - 1]);
                                lrc.setLrcFile(xx);
                                lrcs.add(lrc);
                            }
                        }
                    }
                    return FileHelper.CONTINUE;
                }
            });
            Collections.sort(lrcs);
            int size = lrcs.size();
            for (int i = 0; i < size; i++) {
                // 设置上一句的下一句开始时间
                if (i != 0) {
                    lrcs.get(i - 1).setNextLongms(lrcs.get(i).getLongms());
                }
                lrcs.get(i).setIndex(i);
            }

        } catch (Throwable e) {
            log.error(ErrorUtil.getError(e));
        }
        this.lyrics = lrcs;
        return lrcs;
    }

    public int getCurrentIndex() {
        if (currentIndex >= lyrics.size()) {
            currentIndex = lyrics.size() - 1;
        }
        return currentIndex;
    }

    public void increCurrentIndex() {
        this.currentIndex++;
    }

    public List<Lyric> getLyrics() {
        return lyrics;
    }

    public int getOffset() {
        return offset;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getTi() {
        return ti;
    }

    public String getAr() {
        return ar;
    }

    public String getAl() {
        return al;
    }

    public String getBy() {
        return by;
    }

    public String getFilePath() {
        return filePath;
    }

    public static void main(String[] args) {
        // String str = "[02:08.34][00:16.74]锁了门关了窗息了灯";
        // // String[] strs =
        // //
        // REUtil.getREGroupVector(str,"\\[([0-9]+):([0-9]+)\\.([0-9]+)\\]\\[([0-9]+):([0-9]+)\\.([0-9]+)\\](.*)");
        // String token = "(?:\\[([0-9]+):([0-9]+)\\.([0-9]+)\\])?";
        // String[] strs = REUtil.getREGroupVector(str,
        // "\\[([0-9]+):([0-9]+)\\.([0-9]+)\\]" + token + token + "(.*)");
        //
        // // 时间戳的个数
        // int timestampCount = 0;
        // int nullCount = 0;
        // for (int i = 0; i < strs.length; i++) {
        // System.out.println(strs[i]);
        // if (strs[i] == null) {
        // nullCount++;
        // }
        // }
        // timestampCount = (strs.length - 1 - nullCount) / 3;
        //
        // final List<Lyric> lrcs = new ArrayList<Lyric>();
        // for (int i = 0; i < timestampCount; i++) {
        // Lyric lrc = new Lyric();
        // lrc.setMinute(StringUtil.nvl(strs[i * 3], 0));
        // lrc.setSecond(StringUtil.nvl(strs[i * 3 + 1], 0));
        // lrc.setMiniSecond(StringUtil.nvl(strs[i * 3 + 2], 0));
        // lrc.setLongms(lrc.getMinute() * 60 * 1000 + lrc.getSecond() * 1000
        // + lrc.getMiniSecond() * 10);
        // lrc.setContent(strs[strs.length - 1]);
        // lrcs.add(lrc);
        // }
        //
        // Collections.sort(lrcs);
        LrcFile lr = new LrcFile();
        String encoding = lr.getLyricEncoding("E:/music/lyrics/徐怀钰 - 分飞.lrc");
        System.out.println(encoding);
        List<Lyric> lrcs = lr.read("E:/music/lyrics/徐怀钰 - 分飞.lrc");
        System.out.println(lr.getOffset());
        for (Lyric lrc : lrcs) {
            System.out.println(lrc.getMinute() + ":" + lrc.getSecond() + "." + lrc.getMiniSecond() + "("
                    + (lrc.getLongms() + lrc.getOffset()) + "ms)=" + lrc.getContent());
        }
        // int ms = 247123;
        // int min = (int) (ms / 1000 / 60);
        // int sec = (int) ((ms - min * 1000 * 60) / 1000);
        // int mini = ms % 1000;
        // System.out.println(new LrcFile().toTs(ms));
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void incrementOffset(int incre) {
        this.offset += incre;
    }

    public void subtractOffset(int offset) {
        this.offset -= offset;
    }

    /**
     * 将毫秒数转成时间戳.
     * 
     * @param longms
     * @return
     */
    public String toTs(long longms) {
        int min = (int) (longms / 1000 / 60);
        int sec = (int) ((longms - min * 1000 * 60) / 1000);
        int mini = (int) (longms % 1000 / 10);
        String m = min < 10 ? ("0" + min) : String.valueOf(min);
        String s = sec < 10 ? ("0" + sec) : String.valueOf(sec);
        String mi = mini < 10 ? ("0" + mini) : String.valueOf(mini);
        return "[" + m + ":" + s + "." + mi + "]";
    }
}
