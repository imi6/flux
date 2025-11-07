package com.admin.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author QAQ
 * @since 2025-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Forward extends BaseEntity{

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String userName;

    private String name;

    private Integer tunnelId;

    private Integer inPort;

    private Integer outPort;

    private String remoteAddr;

    private String interfaceName;

    private String strategy;

    private Long inFlow;

    private Long outFlow;

    private Integer inx;

    /**
     * 中转节点配置（仅用于多级隧道转发）
     * 存储该转发实际使用的中转节点端口配置
     */
    private String hopNodesConfig;

}
