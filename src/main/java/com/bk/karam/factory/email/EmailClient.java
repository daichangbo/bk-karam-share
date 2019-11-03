package com.bk.karam.factory.email;


import com.bk.karam.dto.EmailDTO;

/**
 * @author daichangbo
 */
public interface EmailClient {

    /**
     *
     * @param emailDTO
     * @return
     */
    void sendEmail ( EmailDTO emailDTO ) throws Exception ;
}
