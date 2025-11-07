package com.admin.common.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.DecimalMax;
import java.math.BigDecimal;

@Data
public class TunnelDto {

    @NotBlank(message = "隧道名称不能为空")
    private String name;

    @NotNull(message = "入口节点不能为空")
    private Long inNodeId;

    // 出口节点ID，当type=1时可以为空，会自动设置为入口节点ID
    private Long outNodeId;

    @NotNull(message = "隧道类型不能为空")
    private Integer type;

    @NotNull(message = "流量计算类型不能为空")
    private Integer flow;

    // 流量倍率，默认为1.0
    @DecimalMin(value = "0.0", inclusive = false, message = "流量倍率必须大于0.0")
    @DecimalMax(value = "100.0", message = "流量倍率不能大于100.0")
    private BigDecimal trafficRatio;

    private String interfaceName;

    // 协议类型，默认为tls
    private String protocol;

    // TCP监听地址，默认为0.0.0.0
    private String tcpListenAddr = "0.0.0.0";

    // UDP监听地址，默认为0.0.0.0
    private String udpListenAddr = "0.0.0.0";

    // SS节点配置（端口复用时使用）
    private String ssConfig;

    // 多级节点配置（多级隧道转发时使用）
    // JSON格式: [{"nodeId":1,"nodeIp":"192.168.1.1","port":8080,"protocol":"tls","interfaceName":"eth0"}]
    private String hopNodes;

    /**
     * 自定义hopNodes的setter，处理前端可能发送数组的情况
     * 如果前端发送的是数组，将其转换为JSON字符串
     * 如果是字符串，直接使用
     * 如果是null或空数组，设置为null
     */
    @JsonSetter("hopNodes")
    public void setHopNodesFromJson(JsonNode node) {
        if (node == null || node.isNull()) {
            this.hopNodes = null;
        } else if (node.isArray()) {
            // 如果是空数组，设置为null
            if (node.size() == 0) {
                this.hopNodes = null;
            } else {
                // 如果是非空数组，转换为JSON字符串
                this.hopNodes = node.toString();
            }
        } else if (node.isTextual()) {
            // 如果是字符串，直接使用
            String text = node.asText();
            this.hopNodes = (text == null || text.trim().isEmpty()) ? null : text;
        } else {
            // 其他情况，转换为字符串
            this.hopNodes = node.toString();
        }
    }
}