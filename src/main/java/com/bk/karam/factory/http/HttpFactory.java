package com.bk.karam.factory.http;

import com.alibaba.fastjson.JSONObject;
import com.bk.karam.constant.BaseConstant;
import io.netty.handler.codec.http.HttpMethod;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author daichangbo
 * @date 2019-10-30 19:22
 * http客户端封装
 */
@Slf4j
public class HttpFactory implements HttpClient {


    private OkHttpClient okHttpClient ;

    public HttpFactory (OkHttpClient client) {
        this.okHttpClient = client;
    }

    /**
     * 设置底层读超时，单位毫秒，0为无限超时
     * @param readTimeout
     */
    @Override
    public void setReadTimeout ( int readTimeout ) {
           okHttpClient.newBuilder ().readTimeout ( readTimeout, TimeUnit.MILLISECONDS ).build ();
    }

    /**
     * 设置底层连接超时，以毫秒为单位，0为无限超时
     * @param connectTimeout
     */
    @Override
    public void setConnectTimeout ( int connectTimeout ) {
           okHttpClient.newBuilder ().connectTimeout ( connectTimeout,TimeUnit.MILLISECONDS ).build ();
    }

    /**
     * 设置底层写超时，以毫秒为单位，0为无限超时
     *
     * @param writeTimeout
     */
    @Override
    public void setWriteTimeout ( int writeTimeout ) {
          okHttpClient.newBuilder ().writeTimeout ( writeTimeout,TimeUnit.MILLISECONDS ).build ();
    }

    /**
     * @param url : 请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @return
     * @throws Exception
     */
    @Override
    public String get ( @NonNull String url ) throws Exception {
        return get(new URI(url));
    }

    /**
     * @param uri : 请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @return
     * @throws Exception
     */
    @Override
    public String get ( URI uri ) throws Exception {
        log.info( BaseConstant.REQUEST_URL + uri.toURL());
        Request request = new Request.Builder ().url ( uri.toURL () ).get ().build ();
        Response response = okHttpClient.newCall ( request ).execute ();
        return response.body ().string ();
    }

    /**
     * @param url         请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @param headerName
     * @param headerValue
     * @return
     * @throws Exception
     */
    @Override
    public String get ( @NonNull String url, String headerName, String headerValue ) throws Exception {
        return get(new URI(url),headerName,headerValue);
    }

    /**
     * @param uri         请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @param headerName
     * @param headerValue
     * @return
     * @throws Exception
     */
    @Override
    public String get ( URI uri, String headerName, String headerValue ) throws Exception {
        log.info(BaseConstant.REQUEST_URL + uri.toURL() +":" + BaseConstant.REQUEST_HEADER + headerName + headerValue);
        Request request = new Request.Builder ().url ( uri.toURL () ).addHeader ( headerName,headerValue ).get ().build ();
        Response response = okHttpClient.newCall ( request ).execute ();
        return response.body ().string ();
    }

    /**
     * @param url     请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @param headers
     * @return
     * @throws Exception
     */
    @Override
    public String get ( @NonNull String url, Map<String, String> headers ) throws Exception {
        return get(new URI(url), headers);
    }

    /**
     * 发送一个GET请求
     * 支持headers传参
     *
     * @param uri     请求地址,需要用 {@link URI} 封装,这样如果地址错了可以在请求前解析出来
     * @param headers
     * @return
     * @throws Exception
     */
    @Override
    public String get ( @NonNull URI uri, Map<String, String> headers ) throws Exception {
        log.info(BaseConstant.REQUEST_URL + uri.toURL() + BaseConstant.REQUEST_HEADER + JSONObject.toJSONString(headers));
        Request.Builder builder = new Request.Builder ();
        for (String name : headers.keySet ()) {
            builder.addHeader ( name,headers.get ( name ) );
        }
        Request request = builder.url ( uri.toURL () ).get ().build ();
        try {
            Response response = okHttpClient.newCall ( request ).execute ();
            return response.body ().string ();
        } catch (IOException e) {
            log.error("get request is error", e);
        }
        return null;
    }

    /**
     * 发起一个 自定义contentType的请求
     * @param uri         : 请求地址
     * @param httpMethod  : 请求方法 {@link HttpMethod}
     * @param contentType : 请求类型
     * @param content     : 请求内容
     * @return
     * @throws Exception
     */
    @Override
    public String post ( @NonNull URI uri, HttpMethod httpMethod, MediaType contentType, String content ) throws Exception {
        log.info(BaseConstant.REQUEST_URL, uri.toURL() + "请求content" + content);
        RequestBody body = RequestBody.create ( contentType,content );
        Request request = new Request.Builder ().url ( uri.toURL () ).method ( httpMethod.name (),body ).build ();
        try (Response response = okHttpClient.newCall ( request ).execute ()) {
            return response.body ().string ();
        }
    }

    /**
     * 发起一个 application/json; charset=utf-8 请求
     * @param url
     * @param content    : 请求内容
     * @return
     * @throws Exception
     */
    @Override
    public String post ( @NonNull String url, String content ) throws Exception {
        return postRequest ( new URI(url),HttpMethod.POST,content );
    }

    /**
     * 发起一个 post 请求 application/json; charset=utf-8 请求
     * @param uri        : 请求地址
     * @param httpMethod : 请求方法 {@link HttpMethod}
     * @param content    : 请求内容
     * @return
     * @throws Exception
     */
    @Override
    public String postRequest ( @NonNull URI uri, HttpMethod httpMethod, String content ) throws Exception {
        log.info(BaseConstant.REQUEST_URL + uri.toURL() + BaseConstant.REQUEST_PARAMETER + content);
        RequestBody body = RequestBody.create ( BaseConstant.APPLICATION_JSON_UTF8_VALUE,content );
        Request request = new Request.Builder ().url ( uri.toURL () ).method ( "POST" ,body).build ();
        Response response = okHttpClient.newCall ( request ).execute ();
        return response.body ().string ();
    }

    /**
     * 发起一个 application/json; charset=utf-8 请求,并携带请求头
     * @param url
     * @param content
     * @param headers
     * @return
     * @throws Exception
     */
    @Override
    public String postWithHeader ( @NonNull String url, String content, Map<String, String> headers ) throws Exception {
        return postWithHeader(new URI(url),HttpMethod.POST,content,headers);
    }

    /**
     * 发起一个 application/json; charset=utf-8 请求,并携带请求头
     * @param uri
     * @param httpMethod
     * @param content
     * @param headers
     * @return
     * @throws Exception
     */
    @Override
    public String postWithHeader ( @NonNull URI uri, HttpMethod httpMethod, String content, Map<String, String> headers ) throws Exception {
        log.info(BaseConstant.REQUEST_URL, uri.toURL() + BaseConstant.REQUEST_PARAMETER + content + BaseConstant.REQUEST_HEADER +  headers);
        RequestBody body = RequestBody.create(BaseConstant.APPLICATION_JSON_UTF8_VALUE, StringUtils.isEmpty(content) ? "{}" : content);
        Request.Builder builder = new Request.Builder().url(uri.toURL()).method(httpMethod.name(), body);
        for (String name : headers.keySet()) {
            builder.addHeader(name,headers.get(name));
        }
        Request request = builder.build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    /**
     * 发起一个没有参数的post请求 application/json; charset=utf-8 请求,并携带请求头
     * @param url
     * @param headers
     * @return
     * @throws Exception
     */
    @Override
    public String postWithHeader ( @NonNull String url, Map<String, String> headers ) throws Exception {
        return postWithHeader(new URI(url), HttpMethod.POST,null, headers);
    }

    /**
     * 发起一个 application/json; charset=utf-8 请求,并携带请求头
     * @param uri
     * @param httpMethod
     * @param paramName
     * @param paramValue
     * @return
     * @throws IOException
     */
    @Override
    public String postRequest ( @NonNull URI uri, HttpMethod httpMethod, String paramName, String paramValue ) throws IOException {
        log.info(BaseConstant.REQUEST_URL , uri.toURL() + BaseConstant.REQUEST_PARAMETER + paramName + paramValue) ;
        FormBody body = new FormBody.Builder().addEncoded(paramName, paramValue).build();
        Request request = new Request.Builder().url(uri.toURL()).method(httpMethod.name(), body).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 发起一个以 application/x-www-form-urlencoded 形式的post请求
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public String postFromRequest ( @NonNull String url, Map<String, Object> params ) throws Exception {
        return postFromRequest(new URI(url), HttpMethod.POST, params);
    }

    /**
     * 发起一个以 application/x-www-form-urlencoded 形式的post请求
     * @param uri
     * @param httpMethod
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public String postFromRequest ( @NonNull URI uri, HttpMethod httpMethod, Map<String, Object> params ) throws Exception {
        log.info(BaseConstant.REQUEST_URL , uri.toURL() + BaseConstant.REQUEST_PARAMETER + params) ;
        FormBody.Builder body = new FormBody.Builder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            body.addEncoded(param.getKey(), JSONObject .toJSONString(param.getValue()));
        }
        Request request = new Request.Builder().url(uri.toURL()).method(httpMethod.name(), body.build()).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 发起一个form 表单提交方式
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public String postFrom ( @NonNull String url, Map<String, Object> params ) throws Exception {
        log.info(BaseConstant.REQUEST_URL , url + BaseConstant.REQUEST_PARAMETER + params) ;
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getValue() instanceof String) {
                formBody.addEncoded(param.getKey(), (String)param.getValue());
            } else {
                formBody.addEncoded(param.getKey(), JSONObject .toJSONString(param.getValue()));
            }
        }
        return postFrom(url,formBody.build());
    }

    /**
     * 发起一个form 表单提交方式
     * @param url
     * @param formBody
     * @return
     * @throws Exception
     */
    @Override
    public String postFrom ( @NonNull String url, FormBody formBody ) throws Exception {
        log.info(BaseConstant.REQUEST_URL , url) ;
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try (Response responsebank = okHttpClient.newCall(request).execute()) {
            return responsebank.body().string();
        }
    }

    @Override
    public String postFromRequest ( @NonNull String url, Map<String, Object> params, Map<String, String> headers ) throws Exception {
        return postFromRequest(new URI(url), HttpMethod.POST, params, headers);
    }

    @Override
    public String postFromRequest ( @NonNull URI uri, HttpMethod httpMethod, Map<String, Object> params, Map<String, String> headers ) throws Exception {
        log.info(BaseConstant.REQUEST_URL , uri.toURL() + BaseConstant.REQUEST_PARAMETER + params + BaseConstant.REQUEST_HEADER + headers) ;
        FormBody.Builder body = new FormBody.Builder();
        if (null != params && !params.isEmpty()) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                body.addEncoded(param.getKey(), JSONObject.toJSONString(param.getValue()));
            }
        }

        final Request.Builder builder = new Request.Builder().url(uri.toURL());
        for (Map.Entry<String, String> param : headers.entrySet()) {
            builder.header(param.getKey(), param.getValue());
        }
        Request request = builder.method(httpMethod.name(), body.build()).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    public HttpFactory ignoreSslSocketFactory () throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                .getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { trustManager }, null);
        javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        okHttpClient.newBuilder().sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(this.getHostnameVerifier()).build();
        return this;
    }

    private HostnameVerifier getHostnameVerifier() {
        return DefaultHostnameVerifier.instance;
    }

    private enum DefaultHostnameVerifier implements HostnameVerifier {
        instance;
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }
}
