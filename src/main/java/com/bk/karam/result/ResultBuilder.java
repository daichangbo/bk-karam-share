package com.bk.karam.result;


import com.bk.karam.enums.BaseMessage;

/**
 * @author daichangbo
 */
public class ResultBuilder {

    private ResultBuilder () {
        /**
         *
         */
    }

    public static <T> Builder<T> create() {
        return new Builder<>();
    }

    public static <T> ResultBase<T> fail (T obj) {
        return builder(obj);
    }

    public static <T> ResultBase<T> success (T obj) {
        return builder(true,obj);
    }

    public static <T> ResultBase<T> builder (boolean success) {
        return builder(success, null);
    }

    public static <T> ResultBase<T> builder ( BaseMessage baseMessage, T obj) {
        return builder(baseMessage.isSuccess(), baseMessage.getCode(),baseMessage.getMessage(), obj);
    }

    public static <T> ResultBase<T> builder (boolean success, T obj) {
        return builder(success,  BaseMessage.SUCCESS.getMessage(),obj);
    }

    private static <T> ResultBase<T> builder ( T obj) {
        return builderfail(false,  BaseMessage.FAIL.getMessage(),obj);
    }

    private static <T> ResultBase<T> builderfail (boolean success,String returnMsg,T obj) {
        return builder(false, BaseMessage.FAIL.getCode(), returnMsg,obj);
    }


    public static <T> ResultBase<T> builder (boolean success, String returnMsg,T obj) {
        return builder(success, BaseMessage.SUCCESS.getCode(), returnMsg,obj);
    }


    public static <T> ResultBase<T> builder (boolean success, int returnCode,String returnMsg,T obj) {
      return builder(success,returnCode,returnMsg,obj,null);
    }

    public static <T> ResultBase<T> builder (boolean success, int returnCode,String returnMsg,T obj,String attributes) {
     return ResultBuilder.<T>create()
             .setSuccess(success)
             .setReturnCode(returnCode)
             .setReturnMsg(returnMsg)
             .setObj(obj)
             .setAttributes(attributes)
             .build();

    }

    public static class Builder<T> implements IResult<T> {


        private ResultBase<T> resultBase;

        public Builder(){
            resultBase = new ResultBase<>();
        }

        @Override
        public Builder<T> setReturnCode(int returnCode) {
            resultBase.setReturnCode(returnCode);
            return this;
        }

        @Override
        public Builder<T> setReturnMsg(String returnMsg) {
            resultBase.setReturnMsg(returnMsg);
            return this;
        }

        @Override
        public Builder<T> setObj(T obj) {
            resultBase.setObj(obj);
            return this;
        }

        @Override
        public Builder<T> setSuccess(boolean success) {
            resultBase.setSuccess(success);
            return this;
        }

        @Override
        public Builder<T> setAttributes(String attributes) {
            resultBase.setAttributes(attributes);
            return this;
        }

        public ResultBase<T> build () {
            return resultBase;
        }
    }
}
