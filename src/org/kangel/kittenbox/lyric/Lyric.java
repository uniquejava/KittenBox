package org.kangel.kittenbox.lyric;

/**
 * 代表歌词中的一行.<br>
 * 实现了Comparable接口:<br>
 * 这样可以将所有的时间戳转成long型的毫秒数.放在list中，然后在list中可以根据Lyric对象从小到大排好序．<br>
 * [02:08.34][00:16.74]锁了门关了窗息了灯<br>
 * [02:11.43][00:19.39]闭了眼什么正走掉<br>
 * 
 * @author cyper.yin(uniquejava@gmail.com)
 * @version 1.0
 * @since 2009-12-31
 */
public class Lyric implements Comparable<Lyric> {
    private LrcFile lrcFile;
    /**
     * 原始的时间戳
     */
    private String originTs;

    /**
     * 自己在歌词list中的索引.
     */
    private int index;
    /**
     * 这一行歌词逝去了多少比
     */
    private float rate;

    /**
     * 偏移量
     */
    private int offset;
    /**
     * 歌词中的分钟
     */
    private int minute;
    /**
     * 歌词中的秒
     */
    private int second;
    /**
     * 歌词中的毫秒，点号后面的部分．
     */
    private int miniSecond;
    /**
     * 上一句的时间戳转化成的毫秒值.
     */
    private long prevLongms;
    /**
     * 歌词中的时间戳转化成的毫秒值.
     */
    private long longms;
    /**
     * 下一句的时间戳转化成的毫秒值.
     */
    private long nextLongms;
    /**
     * 歌词的内容.
     */
    private String content;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMiniSecond() {
        return miniSecond;
    }

    public void setMiniSecond(int miniSecond) {
        this.miniSecond = miniSecond;
    }

    public long getLongms() {
        return longms;
    }

    public void setLongms(long longms) {
        this.longms = longms;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int compareTo(Lyric o) {
        return (int) (getLongms() - o.getLongms());
    }

    public long getPrevLongms() {
        return prevLongms;
    }

    public void setPrevLongms(long prevLongms) {
        this.prevLongms = prevLongms;
    }

    public long getNextLongms() {
        return nextLongms;
    }

    public void setNextLongms(long nextLongms) {
        this.nextLongms = nextLongms;
    }

    public boolean in(float ms) {
        ms = ms - lrcFile.getOffset();
        boolean b = ms >= longms && ms < nextLongms;
        if (b) {
            rate = (ms - longms) * 1.0f / (nextLongms - longms);
        } else {
            rate = -1;
        }
        return b;
    }

    public LrcFile getLrcFile() {
        return lrcFile;
    }

    public void setLrcFile(LrcFile lrcFile) {
        this.lrcFile = lrcFile;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getOriginTs() {
        return originTs;
    }

    public void setOriginTs(String originTs) {
        this.originTs = originTs;
    }

    public String toTs() {
        int min = (int) (longms / 1000 / 60);
        int sec = (int) ((longms - min * 1000 * 60) / 1000);
        int mini = (int) (longms % 1000 / 10);
        String m = min < 10 ? ("0" + min) : String.valueOf(min);
        String s = sec < 10 ? ("0" + sec) : String.valueOf(sec);
        String mi = mini < 10 ? ("0" + mini) : String.valueOf(mini);
        return "[" + m + ":" + s + "." + mi + "]";
    }
}
