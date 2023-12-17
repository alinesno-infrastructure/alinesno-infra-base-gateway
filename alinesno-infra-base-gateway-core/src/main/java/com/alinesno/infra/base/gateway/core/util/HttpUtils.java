package com.alinesno.infra.base.gateway.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @description Http请求工具类
 * @author JL
 * @date 2021/04/16
 * @version v1.0.0
 */
public class HttpUtils {
    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    static final int CONNECT_TIMEOUT_MILLES = 5000;
    static final Charset ENCODING;
    public static final String HTTP_GET = "GET";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_DELETE = "DELETE";
    static final String [] METHODS;

    static {
        ENCODING = Charset.forName("UTF-8");
        METHODS = new String[]{HTTP_GET, HTTP_PUT, HTTP_POST, HTTP_DELETE};
    }


    private static HttpURLConnection createConnection(String url, final int connectTimeout) throws IOException {
        logger.debug("http connection url:"+ url);
        HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
        setConfig(conn);
        conn.setConnectTimeout(connectTimeout >0?connectTimeout:CONNECT_TIMEOUT_MILLES);
        return conn;
    }

    private static void setMethod(HttpURLConnection conn, String method) throws IOException{
        Assert.isTrue(StringUtils.containsAny(method,METHODS),"只支持GET、POST、PUT、DELETE操作");
        conn.setRequestMethod(method);
    }

    public static HttpResponse doGet(String url, Map<String, String> headers, Map<String,String> params, String content) throws IOException {
        return doHttp(url, HTTP_GET, headers, params, content, CONNECT_TIMEOUT_MILLES);
    }

    public static HttpResponse doPost(String url, Map<String, String> headers, Map<String,String> params, String content) throws IOException {
        return doHttp(url, HTTP_POST, headers, params, content, CONNECT_TIMEOUT_MILLES);
    }

    public static HttpResponse doPut(String url, Map<String, String> headers, Map<String,String> params, String content) throws IOException {
        return doHttp(url, HTTP_PUT, headers, params, content, CONNECT_TIMEOUT_MILLES);
    }

    public static HttpResponse doDelete(String url, Map<String, String> headers, Map<String,String> params, String content) throws IOException {
        return doHttp(url, HTTP_DELETE, headers, params, content, CONNECT_TIMEOUT_MILLES);
    }

    public static HttpResponse doHttp(final String url,final String method,final Map<String, String> headers,final  Map<String,String> params,final  String content, final int connectTimeout) throws IOException {
        HttpURLConnection conn = null;
        HttpResponse response = new HttpResponse();
        try{
            conn = createConnection(setParams(url, params),connectTimeout);
            setMethod(conn, method);
            setHeaders(conn, headers);
            conn.connect();
            output(conn, content);
            response.setCode(conn.getResponseCode());
            response.setMessage(conn.getResponseMessage());
            response.setContent(input(conn));
            return response;
        }catch (SocketTimeoutException ste){
            throw ste;
        }catch(IOException ioe){
            //ioe.printStackTrace();
            throw ioe;
        }finally{
            connClose(conn);
        }
    }

    private static void connClose(HttpURLConnection conn){
        if (conn != null){
            conn.disconnect();
        }
    }

    private static String input(HttpURLConnection conn) throws IOException{
        int len ;
        char[] cbuf = new char[1024 * 8];
        StringBuilder buf = new StringBuilder();
        int status = conn.getResponseCode();
        InputStream in = null;
        BufferedReader reader = null;
        try{
            in = conn.getErrorStream();
            //400或以上表示：访问的页面域名不存在或者请求错误、服务端异常
            if (in == null && status < 400) {
                in = conn.getInputStream();
            }
            if (in != null){
                reader = new BufferedReader(new InputStreamReader(in, ENCODING));
                while ((len = reader.read(cbuf)) > 0){
                    buf.append(cbuf, 0 , len);
                }
            }else {
                logger.info("读取服务端响应数据为空或异常，InputStream=null!", conn.getPermission());
            }
        }catch (IOException ioe){
            logger.error("读取服务端响应数据发生异常，error:", ioe);
        }finally{
            if (reader != null){
                reader.close();
            }
            if (in != null){
                in.close();
            }
        }
        return buf.toString();
    }

    private static void output(HttpURLConnection conn, String content) throws IOException {
        if (StringUtils.isBlank(content)) {
            return;
        }
        OutputStream out = conn.getOutputStream();
        try{
            out.write(content.getBytes(ENCODING));
        } finally{
            if (out != null){
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 接收响应结果
     */
    public static class HttpResponse{
        public int code;
        public String message;
        public String content;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private static void setConfig(HttpURLConnection conn){
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Connection", "close");
        //这个根据需求，自已加，也可以放到headersc参数内
        //conn.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
        //是否启用输出流，method=get,请求参数是拼接在地址档，默认为false
        conn.setDoOutput(true);
        //是否启用输入流
        conn.setDoInput(true);
    }

    private static void setHeaders(HttpURLConnection conn, Map<String,String> headers){
        if (headers == null || headers.size() <= 0){
            return ;
        }
        //设置自定义header参数
        headers.forEach((k,v) -> conn.setRequestProperty(k,v));
    }

    private static String setParams(String url, Map<String, String> params){
        if (params == null || params.size() <= 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        params.forEach((k, v) -> {
            try {
                sb.append(k).append("=").append(URLEncoder.encode(v, HttpUtils.ENCODING.toString())).append("&");
            }catch(UnsupportedEncodingException uee){
                logger.error("set url param error", uee);
            }
        });
        return sb.substring(0, sb.length() - 1);
    }

    public static void main(String[] args) throws IOException {
//        https://www.baidu.com/s?wd=HttpURLConnection
        Map<String,String> params = new HashMap<String, String>();
        params.put("wd","HttpURLConnection");
        HttpResponse response = doGet("http://www.baidu.com/s",null, params, null);
        System.out.println(response.getContent());
    }

}
