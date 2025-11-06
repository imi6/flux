package com.admin.common.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * 多级隧道转发的单个跳点节点配置
 * 用于定义多级隧道中的每一跳节点信息
 * 
 * @author QAQ
 * @since 2025-01-XX
 */
@Data
public class HopNodeDto {
    
    /**
     * 节点ID
     */
    @NotNull(message = "节点ID不能为空")
    private Long nodeId;
    
    /**
     * 节点名称（冗余字段，方便前端显示）
     */
    private String nodeName;
    
    /**
     * 节点IP地址
     * 如果为空或null，后端将自动使用节点的serverIp
     */
    private String nodeIp;

    /**
     * 中转端口
     * 该节点监听的端口，用于接收上一跳的连接
     * 如果为0或null，后端将自动从节点的端口范围中分配可用端口
     */
    @Min(value = 0, message = "端口号不能为负数")
    @Max(value = 65535, message = "端口号不能大于65535")
    private Integer port;
    
    /**
     * 协议类型
     * 支持: tls, quic, ws, wss等
     * 默认: tls
     */
    private String protocol = "tls";
    
    /**
     * 网络接口名称或IP
     * 可选，用于指定出口网卡
     */
    private String interfaceName;
    
    /**
     * 跳点顺序（从1开始）
     * 用于确定节点在链路中的位置
     */
    @NotNull(message = "跳点顺序不能为空")
    @Min(value = 1, message = "跳点顺序必须从1开始")
    private Integer hopOrder;
}

