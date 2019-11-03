package com.bk.karam.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @autor daichangbo
 */
@Slf4j
public class ProfilingUtils {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 转换为格式化的json
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static Object invokeAndLog(Invoke invoke, String signature, Object[] args,long useTime) throws Throwable {
        long start = System.currentTimeMillis();
        boolean success = true;
        Object result = null;
        Throwable ex = null;
        try {
            result = invoke.invoke(args);
            return result;
        } catch (Throwable e) {
            ex = e;
            success = false;
            throw e;
        } finally {
            long finish = System.currentTimeMillis();
            long last = finish - start;
            ProfilingData data = new ProfilingData();
            data.setSuccess(success);
            try {
                data.setArguments(objectMapper.writeValueAsString(args));
            } catch (JsonProcessingException e) {
              log.info("Unable to initialize");
            }
            data.setSignature(signature);
            if (null != ex) {
                Throwable throwable = Throwables.getRootCause(ex);
                StringBuilder sbd = new StringBuilder();
                sbd.append(throwable.getClass().getName()).append(": ");
                sbd.append(throwable.getLocalizedMessage()).append("\n");
                StackTraceElement[] trace = throwable.getStackTrace();
                for (StackTraceElement traceElement : trace) {
                    if (traceElement.getClassName().startsWith("com.jk") || traceElement.getClassName().startsWith("com.baturu")) {
                        sbd.append("\tat ").append(traceElement).append("\n");
                    }
                }
                data.setException(sbd.toString());
            }
            data.setInvokeResults(result);
            data.setTime(dateFormat.format(new Date()));
            data.setUseTime(last);
            data.setThread(Thread.currentThread().getName());
            log.info(objectMapper.writeValueAsString(data));

            // 查询时间超过阈值，会丢一条ERROR
            if (last >= useTime) {
                log.error(objectMapper.writeValueAsString(data));
            }
        }
    }

    public interface Invoke {
        Object invoke ( Object[] args ) throws Throwable;
    }
}
