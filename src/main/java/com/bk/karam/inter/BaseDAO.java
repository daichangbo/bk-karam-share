package com.bk.karam.inter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author daichangbo
 * @date 2019-05-09 19:44
 */
@Slf4j
public class BaseDAO {

    private static final String SYSTEM_USER = "system";

    /**
     * 补全创建人信息
     *
     * @param record
     * @param operatorUser
     */
    public static void markUpDaoCreator(IGMTRecord record,String userName) {
        record.setCreator(StringUtils.isNotEmpty(userName) ? userName : SYSTEM_USER);
        record.setGmtCreated(new Date());
        record.setModifier(StringUtils.isNotEmpty(userName) ? userName : SYSTEM_USER);
        record.setGmtModified(new Date());
    }


    /**
     * 补全创建人信息
     *
     * @param record
     * @param userName
     */
    public static void markInsertDaoCreator(IGMTRecord record, String userName) {
        record.setCreator(StringUtils.isNotEmpty(userName) ? userName : SYSTEM_USER);
        record.setGmtCreated(new Date());
        record.setModifier(StringUtils.isNotEmpty(userName) ? userName : SYSTEM_USER);
        record.setGmtModified(new Date());
    }

    /**
     * 补全创建人信息
     *
     * @param record
     * @param accountId
     */
    public static void markUpDaoCreator(IGMTRecord record, Long accountId) {
        record.setCreator(String.valueOf(accountId));
        record.setGmtCreated(new Date());
        record.setModifier(String.valueOf(accountId));
        record.setGmtModified(new Date());
    }


    /**
     * 补全修改人信息
     *
     * @param record
     * @param accountId
     */
    public static void markUpDaoModifier(IGMTRecord record, Long accountId) {
        record.setModifier(String.valueOf(accountId));
        record.setGmtModified(new Date());
    }

    /**
     * @param record
     * @param userName
     */
    public static void markUpDaoModifier(IGMTRecord record, String  userName) {
        record.setModifier(StringUtils.isNotEmpty(userName) ? userName : SYSTEM_USER);
        record.setGmtModified(new Date());
    }

    /**
     * 系统新增
     *
     * @param record
     */
    public static void markUpDaoCreatorBySystem(IGMTRecord record) {
        record.setCreator(SYSTEM_USER);
        record.setGmtCreated(new Date());
        record.setModifier(SYSTEM_USER);
        record.setGmtModified(new Date());
    }

    /**
     * 系统修改
     *
     * @param record
     */
    public static void markUpDaoModifierBySystem(IGMTRecord record) {
        record.setModifier(SYSTEM_USER);
        record.setGmtModified(new Date());
    }

    /**
     * 补全数据
     * @param t
     * @param userNmae
     * @return
     */
    public static  <T> T markUpCerator (T t,String userNmae) {
        try {
            BeanUtils.setProperty(t, "id", null);
            BeanUtils.setProperty(t, "isDeleted", "N");
            BeanUtils.setProperty(t, "gmtCreated", new Date());
            BeanUtils.setProperty(t, "gmtModified", new Date());
            String creator = StringUtils.isNotEmpty(userNmae) ? userNmae : SYSTEM_USER;
            BeanUtils.setProperty(t, "creator", creator);
            BeanUtils.setProperty(t, "modifier", creator);
        } catch (Exception e) {
            log.info("markUpCerator is error",e);
        }
        return t;
    }

}
