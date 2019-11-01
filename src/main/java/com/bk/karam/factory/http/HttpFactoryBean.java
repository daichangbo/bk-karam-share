package com.bk.karam.factory.http;

import com.bk.karam.constant.BaseConstant;
import com.netflix.ribbon.proxy.annotation.Http;
import lombok.Data;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.FactoryBean;

import java.util.concurrent.TimeUnit;

/**
 * @author daichangbo
 * 使用方式：
 *      一：使用注解方式注入
 *      @Bean(value = "httpClient")
 *      public HttpClient init() throws Exception{
 *          HttpFactoryBean httpFactoryBean = new HttpFactoryBean();
 *          httpFactoryBean.setConnectTime(3000);
 *          .
 *          .
 *          .
 *          return httpFactoryBean.getObject();
 *      }
 */
@Data
public class HttpFactoryBean implements FactoryBean<HttpClient> {

    private long connectTime ;

    private long writeTime ;

    private long readTime ;

    private OkHttpClient okHttpClient ;


    @Override
    public HttpClient getObject () throws Exception {
        if (null == okHttpClient) {
            synchronized (HttpFactory.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder ()
                            .connectTimeout(connectTime != 0 ? connectTime : BaseConstant.DEFAULT_TIME, TimeUnit.MILLISECONDS)
                            .writeTimeout(writeTime != 0 ? writeTime : BaseConstant.DEFAULT_TIME ,TimeUnit.MILLISECONDS)
                            .readTimeout(readTime != 0 ? readTime : BaseConstant.DEFAULT_TIME,TimeUnit.MILLISECONDS)
                            .build();
                }
            }
        }
        return new HttpFactory ( okHttpClient );
    }

    public HttpClient init () throws Exception {
        return getObject ();
    }

    @Override
    public Class<?> getObjectType () {
        return HttpClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
