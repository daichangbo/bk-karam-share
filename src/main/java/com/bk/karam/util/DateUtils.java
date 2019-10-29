package com.bk.karam.util;

import com.bk.karam.constant.BaseConstant;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @author daichangbo
 * @date 2019-10-29 20:40
 */
public class DateUtils implements Serializable, BaseConstant {

    private static final long serialVersionUID = 4921818892578066954L;

    /**
     * 字符串转日期
     * @param param
     * @return
     * @throws Exception
     */
    public static Date parseDate (String param) throws Exception {
        if (StringUtils.isEmpty ( param ))
            return null;
        return new SimpleDateFormat ( DEFAULT ).parse ( param );
    }
}
