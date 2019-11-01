package com.bk.karam.factory.http;

/**
 * @author daichangbo
 * @date 2019-10-30 19:06
 */
import okhttp3.FormBody;
import okhttp3.MediaType;
import io.netty.handler.codec.http.HttpMethod;
import lombok.NonNull;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author daichangbo
 * @date 2019-04-28 20:27
 * <p>
 * Http请求支持，get/post/applicatiopn.json/forme表单等请求方式
 */
public interface HttpClient {


    /**
     * 设置底层读超时,以毫秒为单位。值0指定无限超时。
     *
     * @see okhttp3.OkHttpClient.Builder#readTimeout(long, TimeUnit)
     */
    public void setReadTimeout ( int readTimeout );

    /**
     * 设置底层连接超时,以毫秒为单位。值0指定无限超时。
     *
     * @see okhttp3.OkHttpClient.Builder#connectTimeout(long, TimeUnit)
     */
    public void setConnectTimeout ( int connectTimeout );

    /**
     * 设置底层写超时,以毫秒为单位。值0指定无限超时。
     *
     * @see okhttp3.OkHttpClient.Builder#writeTimeout(long, TimeUnit)
     */
    public void setWriteTimeout ( int writeTimeout );


    /**
     * 发起一个GET请求
     *
     * @param url : 请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @return
     * @throws IOException
     */
    public String get ( @NonNull String url ) throws Exception;

    /**
     * 发起一个GET请求
     *
     * @param uri : 请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @return
     * @throws IOException
     */
    public String get ( URI uri ) throws Exception;

    /**
     * 发起一个GET请求
     *
     * @param url         请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @param headerName
     * @param headerValue
     * @return
     * @throws IOException
     */
    public String get ( @NonNull String url, String headerName, String headerValue ) throws Exception;

    /**
     * 发起一个GET请求
     *
     * @param uri         请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @param headerName
     * @param headerValue
     * @return
     * @throws IOException
     */
    public String get ( URI uri, String headerName, String headerValue ) throws Exception;

    /**
     * 发起一个GET请求
     *
     * @param url     请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @param headers
     * @return
     * @throws IOException
     */
    public String get ( @NonNull String url, Map<String, String> headers ) throws Exception;

    /**
     * 发起一个GET请求
     *
     * @param uri     请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @param headers
     * @return
     * @throws IOException
     */
    public String get ( @NonNull URI uri, Map<String, String> headers ) throws Exception;

    /**
     * 发起一个 自定义contentType的请求
     *
     * @param uri         : 请求地址
     * @param httpMethod  : 请求方法 {@link HttpMethod}
     * @param contentType : 请求类型
     * @param content     : 请求内容
     * @return
     * @throws IOException
     */
    public String post ( @NonNull URI uri, HttpMethod httpMethod, MediaType contentType, String content )
            throws Exception;

    /**
     * 发起一个 application/json; charset=utf-8 请求
     *
     * @param url     : 请求地址
     * @param content : 请求内容
     * @return
     * @throws IOException
     */
    public String post ( @NonNull String url, String content ) throws Exception;

    /**
     * 发起一个 application/json; charset=utf-8 请求
     *
     * @param uri        : 请求地址
     * @param httpMethod : 请求方法 {@link HttpMethod}
     * @param content    : 请求内容
     * @return
     * @throws IOException
     */
    public String postRequest ( @NonNull URI uri, HttpMethod httpMethod, String content ) throws Exception;

    /**
     * 发起一个 application/json; charset=utf-8 请求,并携带请求头
     *
     * @param url
     * @param content
     * @param headers
     * @return
     * @throws IOException
     */
    public String postWithHeader ( @NonNull String url, String content, Map<String, String> headers ) throws Exception;

    /**
     * 发起一个 application/json; charset=utf-8 请求,并携带请求头
     *
     * @param uri
     * @param httpMethod
     * @param content
     * @param headers
     * @return
     * @throws IOException
     */
    public String postWithHeader ( @NonNull URI uri, HttpMethod httpMethod, String content, Map<String, String> headers ) throws Exception;

    /**
     * 发起一个 application/json; charset=utf-8 请求,并携带请求头
     *
     * @param url
     * @param headers
     * @return
     * @throws IOException
     */
    public String postWithHeader ( @NonNull String url, Map<String, String> headers ) throws Exception;

    /**
     * 发起一个以 application/json; charset=utf-8 形式的请求
     *
     * @param uri
     * @param httpMethod
     * @param paramName
     * @param paramValue
     * @return
     * @throws IOException
     */
    public String postRequest ( @NonNull URI uri, HttpMethod httpMethod, String paramName, String paramValue ) throws IOException;

    /**
     * 发起一个以 application/x-www-form-urlencoded 形式的请求
     *
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public String postFromRequest ( @NonNull String url, Map<String, Object> params ) throws Exception;


    /**
     * 发起一个以 application/x-www-form-urlencoded 形式的请求
     *
     * @param uri
     * @param httpMethod
     * @param params
     * @return
     * @throws IOException
     */
    public String postFromRequest ( @NonNull URI uri, HttpMethod httpMethod, Map<String, Object> params ) throws Exception;

    /**
     * 发起一个form 表单提交
     * application/json
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public String postFrom ( @NonNull String url, Map<String, Object> params ) throws Exception;

    /**
     * 发起一个form表单提交
     *
     * @param url
     * @param formBody
     * @return
     * @throws Exception
     */
    public String postFrom ( @NonNull String url, FormBody formBody ) throws Exception;

    public String postFromRequest ( @NonNull String url, Map<String, Object> params,
                                    Map<String, String> headers ) throws Exception;

    public String postFromRequest ( @NonNull URI uri, HttpMethod httpMethod, Map<String, Object> params,
                                    Map<String, String> headers ) throws Exception;

    /**
     * 忽略SSL
     *
     * @see okhttp3.OkHttpClient.Builder# sslSocketFactory(SSLSocketFactory,
     * X509TrustManager)
     */
    public HttpFactory ignoreSslSocketFactory () throws Exception;
}

