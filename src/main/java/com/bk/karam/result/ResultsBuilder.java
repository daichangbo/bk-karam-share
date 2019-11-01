package com.bk.karam.result;

import com.bk.karam.enums.BaseMessage;
import com.google.common.base.Optional;
import java.util.Collections;
import java.util.List;

/**
 * @author daichangbo
 * 用于构建返回结果集
 */
public class ResultsBuilder {

    private  ResultsBuilder () {
        /**
         *
         */
    }

    public static <T> Builder<T> creater () {
        return new Builder<>();
    }

    public static <T> ResultsBase<T> fail (List<T> list ) {
        return builder(false, BaseMessage.FAIL.getCode(),BaseMessage.FAIL.getMessage() ,list ) ;
    }

    public static <T> ResultsBase<T> success (List<T> list) {
        return builder(true, BaseMessage.SUCCESS.getCode(),BaseMessage.SUCCESS.getMessage() ,list ) ;
    }

    public static <T> ResultsBase<T> builder (BaseMessage baseMessage,List<T> list ) {
        return builder(baseMessage.isSuccess(), baseMessage.getCode(), baseMessage.getMessage() ,list) ;
    }

    public static <T> ResultsBase<T> builder (boolean success ,List<T> list ) {
        return builder(success, BaseMessage.SUCCESS.getCode(),BaseMessage.SUCCESS.getMessage() ,list) ;
    }

    public static <T> ResultsBase<T> builder (boolean success ,int returnCode,String returnMsg ,List<T> list ) {
        return builder(success,returnCode,returnMsg,list ,null) ;
    }

    public static <T> ResultsBase<T> builder (boolean success ,int returnCode,String returnMsg ,List<T> list ,String attributes) {
        return builder(success,returnCode,returnMsg,list ,(null == list ? 0 : list.size()) ,null == list ? 0 : list.size() ,attributes) ;
    }

    public static <T> ResultsBase<T> builder (boolean success ,int returnCode,String returnMsg ,List<T> list , long count, int limit ,String attributes) {
        return ResultsBuilder.<T>creater()
                .setSuccess(success)
                .setReturnCode(returnCode)
                .setReturnMsg(returnMsg)
                .setObj((Optional.fromNullable(list).or(Collections.<T>emptyList())))
                .setStart(0)
                .setCount(count)
                .setLimit(limit == 0 ? 0 : limit)
                .setSize(limit == 0 ? 0 : limit)
                .setAttributes(attributes)
                .build();

    }

    /**
     * Builder
     * @param <T>
     */
    public static class Builder<T> implements IResults<T> {

        private ResultsBase<T> resultsBase;

        public Builder() {
            resultsBase = new ResultsBase<>();
        }

        @Override
        public Builder<T> setLimit(int limit) {
            resultsBase.setLimit(limit);
            return this;
        }

        @Override
        public Builder<T> setStart(int start) {
            resultsBase.setStart(start);
            return this;
        }

        @Override
        public Builder<T> setCount(long count) {
            resultsBase.setCount(count);
            return this;
        }

        @Override
        public Builder<T> setSize(int size) {
            resultsBase.setSize(size);
            return this;
        }

        @Override
        public Builder<T> setObj(List<T> obj) {
            resultsBase.setObj(obj);
            return this;
        }

        @Override
        public Builder<T> setReturnCode(int returnCode) {
            resultsBase.setReturnCode(returnCode);
            return this;
        }

        @Override
        public Builder<T> setReturnMsg(String returnMsg) {
            resultsBase.setReturnMsg(returnMsg);
            return this;
        }

        @Override
        public Builder<T> setSuccess(boolean success) {
            resultsBase.setSuccess(success);
            return this;
        }

        @Override
        public Builder<T> setAttributes(String attributes) {
            resultsBase.setAttributes(attributes);
            return this;
        }

        public ResultsBase<T> build () {
            return resultsBase;
        }
    }
}
