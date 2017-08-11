package org.kangel.kittenbox.lyric.baidu;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.log4j.Logger;
import org.kangel.kittenbox.network.Proxy;
import org.kitten.core.io.FileHelper;
import org.kitten.core.io.ILineProcessor;
import org.kitten.core.util.ErrorUtil;
import org.kitten.core.util.REUtil;

/**
 * 一个工具类，主要负责分析歌词 并找到歌词下载下来，然后保存成标准格式的文件 还有一些常用的方法
 * 
 * @author cyper.yin(uniquejava@gmail.com)
 * @version 1.0
 * @since 2010-1-9
 */
public final class BaiduUtil {
    private static Logger log = Logger.getLogger(BaiduUtil.class);

    private BaiduUtil() {
    }

    /**
     * 从一个流里面得到这个流的字符串 表现形式
     * 
     * @param is
     *            流
     * @return 字符串
     */
    private static String getString(InputStream is, String encoding) {
        InputStreamReader r = null;
        try {
            StringBuilder sb = new StringBuilder();
            // TODO 这里是固定把网页内容的编码写在GBK,应该是可设置的
            r = new InputStreamReader(is, encoding);
            char[] buffer = new char[128];
            int length = -1;
            while ((length = r.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length));
            }
            return sb.toString();
        } catch (Exception ex) {
            log.error(ErrorUtil.getError(ex));
            return "";
        } finally {
            try {
                r.close();
            } catch (Exception ex) {
                log.error(ErrorUtil.getError(ex));
            }
        }
    }

    /**
     * 去除HTML标记
     * 
     * @param str1
     *            含有HTML标记的字符串
     * @return 去除掉相关字符串
     */
    public static String htmlTrim(String str1) {
        String str = "";
        str = str1;
        // 剔出了<html>的标签
        str = str.replaceAll("</?[^>]+>", "");
        // 去除空格
        str = str.replaceAll("\\s", "");
        str = str.replaceAll("&nbsp;", "");
        str = str.replaceAll("&amp;", "&");
        str = str.replace(".", "");
        str = str.replace("\"", "‘");
        str = str.replace("'", "‘");
        return str;
    }

    private static String htmlTrim2(String str1) {
        String str = "";
        str = str1;
        // 剔出了<html>的标签
        str = str.replaceAll("<BR>", "\n");
        str = str.replaceAll("<br>", "\n");
        str = str.replaceAll("</?[^>]+>", "");
        return str;
    }

    /**
     * 得到在百度上搜索到的歌词的内容
     * 
     * @param key
     *            关键内容
     * @return 内容
     * @throws java.lang.Exception
     */
    public static List<LyricAddress> getBaiduLyricUrlByKey(String key) throws Exception {
        final HttpClient http = getHttpClient();
        GetMethod method = new GetMethod("http://mp3.baidu.com/m?f=ms&tn=baidump3lyric&ct=150994944&lf=2&rn=10&word="
                + URLEncoder.encode(key, "GBK"));
        method.addRequestHeader("Host", "www.baidu.com");
        method.addRequestHeader("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
        method.addRequestHeader("Accept",
                "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        method.addRequestHeader("Accept-Language", "zh-cn,zh;q=0.5");
        method.addRequestHeader("Keep-Alive", "300");
        method.addRequestHeader("Referer", "http://www.baidu.com/");
        method.addRequestHeader("Connection", "keep-alive");
        int i = http.executeMethod(method);
        final String temp = getString(method.getResponseBodyAsStream(), "GBK");
        // log.info(temp);
        method.releaseConnection();
        FileHelper.setFileContent(new File("c:/all.txt"), temp, "GBK");
        final List<LyricAddress> list = new ArrayList<LyricAddress>();
        FileHelper.processFileByLine(new File("c:/all.txt"), "GBK", new ILineProcessor() {
            public boolean processLine(String str) {
                String urls[] = REUtil.getREGroupSet(str, ".*LRC歌词来自：([^<]+)</a>.*");
                if (urls.length > 0) {
                    LyricAddress addr = new LyricAddress();
                    addr.setFileName(analyzeFilename(urls[0], http));
                    addr.setDownloadUrl(urls[0]);
                    list.add(addr);
                }
                return FileHelper.CONTINUE;
            }
        });
        return list;
    }

    /**
     * 从待下载的地址中预先分析出文件名，这样下载的用户可以通过文件名来识别该歌词是不是它想要的.
     * 
     * @param url
     * @param http
     * @return
     */
    public static String analyzeFilename(String url, HttpClient http) {
        HeadMethod method = new HeadMethod();
        try {
            method.setURI(new URI(url, false, "GBK"));
            http.executeMethod(method);
            Header header = method.getResponseHeader("Content-Disposition");
            if (header == null) {
                log.warn("无法取得文件名[header==null],url=" + url);
                // Header[] headers = method.getResponseHeaders();
                // for (int i = 0; i < headers.length; i++) {
                // log.info("headers="+headers[i]);
                // }
            } else {
                String value = header.getValue();
                int pos = value.indexOf("filename=");
                if (pos != -1) {
                    return value.substring(pos + 9);
                } else {
                    log.warn("无法取得文件名,url=" + url);
                }
            }
        } catch (Throwable e) {
            log.error(ErrorUtil.getError(e));
        }
        return null;
    }

    /**
     * 从给定的url地址下载所需的歌词文件.
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static String downloadLyricFromUrl(String url) throws Exception {
        log.info("正在下载歌词,来自" + url);
        HttpClient http = getHttpClient();
        GetMethod method = new GetMethod();
        URI uri = new URI(url, false, "GBK");
        method.setURI(uri);
        http.executeMethod(method);
        InputStream is = method.getResponseBodyAsStream();
        return getString(is, "GBK");
    }

    public static HttpClient getHttpClient() {
        HttpClient http = new HttpClient();
        Proxy proxy = Proxy.getInstance();
        if (proxy.isUseProxy()) {
            if (proxy.getUser() != null && proxy.getPwd() != null) {
                http.getState().setProxyCredentials(new AuthScope(proxy.getIp(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUser(), proxy.getPwd()));
            }
            http.getHostConfiguration().setProxy(proxy.getIp(), proxy.getPort());
        }
        http.getParams().setContentCharset("GBK");
        http.getParams().setHttpElementCharset("GBK");
        return http;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://lrc.aspxp.net/lrc.asp?id=291415&id1=174&t=lrc&ac=dl";
        log.info("正在下载歌词,来自" + url);
        HttpClient http = new HttpClient();
        http.getHostConfiguration().setProxy("128.96.176.200", Integer.parseInt("8089"));
        http.getParams().setContentCharset("UTF-8");
        GetMethod method = new GetMethod();
        URI uri = new URI(url, false, "GBK");
        method.setURI(uri);
        http.executeMethod(method);
        System.out.println(method.getResponseCharSet());

        Header header = method.getResponseHeader("Content-Disposition");
        System.out.println(header);// 打印出

    }
}
