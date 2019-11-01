package com.bk.karam.enums;

/**
 * @author daichangbo
 */
public enum BaseMessage {

    SUCCESS (200,"成功","SUCCESS",true),
    BAD_REQUEST(400,"请求格式错误","Request format error",false),
    BAD_REQUEST_PARAM(401,"请求参数有误","Request parameter is incorrect",false),
    MANY_REQUEST(429,"请求过于频繁","Request too frequently",false),
    PARAM_CHECK_ERROR(422,"参数校验不通过","Parameter verification does not pass",false),
    FAIL(500,"系统异常","System Error",false);

    private int code ;

    private String message;

    private String englishMesg;

    private boolean isSuccess;

    private BaseMessage ( int code , String message , String englisgMsg , boolean isSuccess) {
        this.code = code;
        this.message = message;
        this.englishMesg = englisgMsg;
        this.isSuccess = isSuccess;
    }


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

    public String getEnglishMesg() {
        return englishMesg;
    }

    public void setEnglishMesg(String englishMesg) {
        this.englishMesg = englishMesg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    private  BaseMessage getBaseMessage(int code) {
        for (BaseMessage bm : BaseMessage.values()) {
            if (bm.getCode() == code){
                return bm;
            }
        }
        return SUCCESS;
    }
}
