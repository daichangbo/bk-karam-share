package com.bk.karam.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author daichangbo
 */
@Data
public class BaseDTO implements Serializable {


    private static final long serialVersionUID = -6453376581809717848L;

    private int pageNo = 1;

    private int pageSize = 20;

    private String attributes;


}
