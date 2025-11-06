package com.admin.common.utils;

import com.admin.common.dto.GostConfigDto;
import com.admin.common.dto.GostDto;
import com.admin.entity.Tunnel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.apache.bcel.generic.RET;

import java.util.Objects;

public class GostUtil {


    public static GostDto AddLimiters(Long node_id, Long name, String speed) {
        JSONObject data = createLimiterData(name, speed);
        return WebSocketServer.send_msg(node_id, data, "AddLimiters");
    }

    public static GostDto UpdateLimiters(Long node_id, Long name, String speed) {
        JSONObject data = createLimiterData(name, speed);
        JSONObject req = new JSONObject();
        req.put("limiter", name + "");
        req.put("data", data);
        return WebSocketServer.send_msg(node_id, req, "UpdateLimiters");
    }

    public static GostDto DeleteLimiters(Long node_id, Long name) {
        JSONObject req = new JSONObject();
        req.put("limiter", name + "");
        return WebSocketServer.send_msg(node_id, req, "DeleteLimiters");
    }

    public static GostDto AddService(Long node_id, String name, Integer in_port, Integer limiter, String remoteAddr, Integer fow_type, Tunnel tunnel, String strategy, String interfaceName) {
        JSONArray services = new JSONArray();
        String[] protocols = {"tcp", "udp"};
        for (String protocol : protocols) {
            JSONObject service = createServiceConfig(name, in_port, limiter, remoteAddr, protocol, fow_type, tunnel, strategy, interfaceName);
            services.add(service);
        }
        return WebSocketServer.send_msg(node_id, services, "AddService");
    }

    public static GostDto UpdateService(Long node_id, String name, Integer in_port, Integer limiter, String remoteAddr, Integer fow_type, Tunnel tunnel, String strategy, String interfaceName) {
        JSONArray services = new JSONArray();
        String[] protocols = {"tcp", "udp"};
        for (String protocol : protocols) {
            JSONObject service = createServiceConfig(name, in_port, limiter, remoteAddr, protocol, fow_type, tunnel, strategy, interfaceName);
            services.add(service);
        }
        return WebSocketServer.send_msg(node_id, services, "UpdateService");
    }

    public static GostDto DeleteService(Long node_id, String name) {
        JSONObject data = new JSONObject();
        JSONArray services = new JSONArray();
        services.add(name + "_tcp");
        services.add(name + "_udp");
        data.put("services", services);
        return WebSocketServer.send_msg(node_id, data, "DeleteService");
    }

    public static GostDto AddRemoteService(Long node_id, String name, Integer out_port, String remoteAddr,  String protocol, String strategy, String interfaceName) {
        JSONObject data = new JSONObject();
        data.put("name", name + "_tls");
        data.put("addr", ":" + out_port);

        if (StringUtils.isNotBlank(interfaceName)) {
            JSONObject metadata = new JSONObject();
            metadata.put("interface", interfaceName);
            data.put("metadata", metadata);
        }


        JSONObject handler = new JSONObject();
        handler.put("type", "relay");
        data.put("handler", handler);
        JSONObject listener = new JSONObject();
        listener.put("type", protocol);
        data.put("listener", listener);
        JSONObject forwarder = new JSONObject();
        JSONArray nodes = new JSONArray();

        String[] split = remoteAddr.split(",");
        int num = 1;
        for (String addr : split) {
            JSONObject node = new JSONObject();
            node.put("name", "node_" + num );
            node.put("addr", addr);
            nodes.add(node);
            num ++;
        }
        if (strategy == null || strategy.equals("")){
            strategy = "fifo";
        }
        forwarder.put("nodes", nodes);
        JSONObject selector = new JSONObject();
        selector.put("strategy", strategy);
        selector.put("maxFails", 1);
        selector.put("failTimeout", "600s");
        forwarder.put("selector", selector);

        data.put("forwarder", forwarder);
        JSONArray services = new JSONArray();
        services.add(data);
        return WebSocketServer.send_msg(node_id, services, "AddService");
    }

    public static GostDto UpdateRemoteService(Long node_id, String name, Integer out_port, String remoteAddr,String protocol, String strategy, String interfaceName) {
        JSONObject data = new JSONObject();
        data.put("name", name + "_tls");
        data.put("addr", ":" + out_port);

        if (StringUtils.isNotBlank(interfaceName)) {
            JSONObject metadata = new JSONObject();
            metadata.put("interface", interfaceName);
            data.put("metadata", metadata);
        }


        JSONObject handler = new JSONObject();
        handler.put("type", "relay");
        data.put("handler", handler);
        JSONObject listener = new JSONObject();
        listener.put("type", protocol);
        data.put("listener", listener);
        JSONObject forwarder = new JSONObject();
        JSONArray nodes = new JSONArray();

        String[] split = remoteAddr.split(",");
        int num = 1;
        for (String addr : split) {
            JSONObject node = new JSONObject();
            node.put("name", "node_" + num );
            node.put("addr", addr);
            nodes.add(node);
            num ++;
        }
        if (strategy == null || strategy.equals("")){
            strategy = "fifo";
        }
        forwarder.put("nodes", nodes);
        JSONObject selector = new JSONObject();
        selector.put("strategy", strategy);
        selector.put("maxFails", 1);
        selector.put("failTimeout", "600s");
        forwarder.put("selector", selector);

        data.put("forwarder", forwarder);
        JSONArray services = new JSONArray();
        services.add(data);
        return WebSocketServer.send_msg(node_id, services, "UpdateService");
    }

    public static GostDto DeleteRemoteService(Long node_id, String name) {
        JSONArray data = new JSONArray();
        data.add(name + "_tls");
        JSONObject req = new JSONObject();
        req.put("services", data);
        return WebSocketServer.send_msg(node_id, req, "DeleteService");
    }

    public static GostDto PauseService(Long node_id, String name) {
        JSONObject data = new JSONObject();
        JSONArray services = new JSONArray();
        services.add(name + "_tcp");
        services.add(name + "_udp");
        data.put("services", services);
        return WebSocketServer.send_msg(node_id, data, "PauseService");
    }

    public static GostDto ResumeService(Long node_id, String name) {
        JSONObject data = new JSONObject();
        JSONArray services = new JSONArray();
        services.add(name + "_tcp");
        services.add(name + "_udp");
        data.put("services", services);
        return WebSocketServer.send_msg(node_id, data, "ResumeService");
    }

    public static GostDto PauseRemoteService(Long node_id, String name) {
        JSONObject data = new JSONObject();
        JSONArray services = new JSONArray();
        services.add(name + "_tls");
        data.put("services", services);
        return WebSocketServer.send_msg(node_id, data, "PauseService");
    }

    public static GostDto ResumeRemoteService(Long node_id, String name) {
        JSONObject data = new JSONObject();
        JSONArray services = new JSONArray();
        services.add(name + "_tls");
        data.put("services", services);
        return WebSocketServer.send_msg(node_id, data, "ResumeService");
    }

    public static GostDto AddChains(Long node_id, String name, String remoteAddr, String protocol, String interfaceName) {
        JSONObject dialer = new JSONObject();
        dialer.put("type", protocol);
        if (Objects.equals(protocol, "quic")){
            JSONObject metadata = new JSONObject();
            metadata.put("keepAlive", true);
            metadata.put("ttl", "10s");
            dialer.put("metadata", metadata);
        }




        JSONObject connector = new JSONObject();
        connector.put("type", "relay");

        JSONObject node = new JSONObject();
        node.put("name", "node-" + name);
        node.put("addr", remoteAddr);
        node.put("connector", connector);
        node.put("dialer", dialer);

        if (StringUtils.isNotBlank(interfaceName)) {
            node.put("interface", interfaceName);
        }


        JSONArray nodes = new JSONArray();
        nodes.add(node);

        JSONObject hop = new JSONObject();
        hop.put("name", "hop-" + name);
        hop.put("nodes", nodes);

        JSONArray hops = new JSONArray();
        hops.add(hop);

        JSONObject data = new JSONObject();
        data.put("name", name + "_chains");
        data.put("hops", hops);

        return WebSocketServer.send_msg(node_id, data, "AddChains");
    }

    public static GostDto UpdateChains(Long node_id, String name, String remoteAddr, String protocol, String interfaceName) {
        JSONObject dialer = new JSONObject();
        dialer.put("type", protocol);

        if (Objects.equals(protocol, "quic")){
            JSONObject metadata = new JSONObject();
            metadata.put("keepAlive", true);
            metadata.put("ttl", "10s");
            dialer.put("metadata", metadata);
        }


        JSONObject connector = new JSONObject();
        connector.put("type", "relay");

        JSONObject node = new JSONObject();
        node.put("name", "node-" + name);
        node.put("addr", remoteAddr);
        node.put("connector", connector);
        node.put("dialer", dialer);

        if (StringUtils.isNotBlank(interfaceName)) {
            node.put("interface", interfaceName);
        }

        JSONArray nodes = new JSONArray();
        nodes.add(node);

        JSONObject hop = new JSONObject();
        hop.put("name", "hop-" + name);
        hop.put("nodes", nodes);

        JSONArray hops = new JSONArray();
        hops.add(hop);

        JSONObject data = new JSONObject();
        data.put("name", name + "_chains");
        data.put("hops", hops);
        JSONObject req = new JSONObject();
        req.put("chain", name + "_chains");
        req.put("data", data);
       return WebSocketServer.send_msg(node_id, req, "UpdateChains");
    }

    public static GostDto DeleteChains(Long node_id, String name) {
        JSONObject data = new JSONObject();
        data.put("chain", name + "_chains");
        return WebSocketServer.send_msg(node_id, data, "DeleteChains");
    }

    /**
     * 添加SS代理链
     * @param node_id 节点ID
     * @param name 服务名称
     * @param ssConfig SS节点配置（ss://链接格式）
     * @return 操作结果
     */
    public static GostDto AddSSChain(Long node_id, String name, String ssConfig) {
        JSONArray hops = new JSONArray();

        // 解析SS配置，支持多行
        String[] ssLines = ssConfig.split("\n");
        int hopNum = 0;

        for (String ssLine : ssLines) {
            ssLine = ssLine.trim();
            if (ssLine.isEmpty() || !ssLine.startsWith("ss://")) {
                continue;
            }

            // 解析SS链接: ss://method:password@server:port
            try {
                String ssData = ssLine.substring(5); // 移除 "ss://"
                String[] parts = ssData.split("@");
                if (parts.length != 2) {
                    continue;
                }

                String[] authParts = parts[0].split(":");
                if (authParts.length != 2) {
                    continue;
                }

                String method = authParts[0];
                String password = authParts[1];
                String serverAddr = parts[1];

                // 创建SS节点
                JSONObject node = new JSONObject();
                node.put("name", "ss-node-" + hopNum);
                node.put("addr", serverAddr);

                // SS connector配置
                JSONObject connector = new JSONObject();
                connector.put("type", "ss");

                // SS认证配置
                JSONObject auth = new JSONObject();
                auth.put("username", method);
                auth.put("password", password);
                connector.put("auth", auth);

                node.put("connector", connector);

                // Dialer配置
                JSONObject dialer = new JSONObject();
                dialer.put("type", "tcp");
                node.put("dialer", dialer);

                // 创建hop
                JSONArray nodes = new JSONArray();
                nodes.add(node);

                JSONObject hop = new JSONObject();
                hop.put("name", "hop-" + hopNum);
                hop.put("nodes", nodes);

                hops.add(hop);
                hopNum++;
            } catch (Exception e) {
                // 解析失败，跳过该行
                continue;
            }
        }

        JSONObject data = new JSONObject();
        data.put("name", name + "_ss_chain");
        data.put("hops", hops);

        return WebSocketServer.send_msg(node_id, data, "AddChains");
    }

    /**
     * 更新SS代理链
     */
    public static GostDto UpdateSSChain(Long node_id, String name, String ssConfig) {
        JSONArray hops = new JSONArray();

        // 解析SS配置，支持多行
        String[] ssLines = ssConfig.split("\n");
        int hopNum = 0;

        for (String ssLine : ssLines) {
            ssLine = ssLine.trim();
            if (ssLine.isEmpty() || !ssLine.startsWith("ss://")) {
                continue;
            }

            // 解析SS链接
            try {
                String ssData = ssLine.substring(5);
                String[] parts = ssData.split("@");
                if (parts.length != 2) {
                    continue;
                }

                String[] authParts = parts[0].split(":");
                if (authParts.length != 2) {
                    continue;
                }

                String method = authParts[0];
                String password = authParts[1];
                String serverAddr = parts[1];

                JSONObject node = new JSONObject();
                node.put("name", "ss-node-" + hopNum);
                node.put("addr", serverAddr);

                JSONObject connector = new JSONObject();
                connector.put("type", "ss");

                JSONObject auth = new JSONObject();
                auth.put("username", method);
                auth.put("password", password);
                connector.put("auth", auth);

                node.put("connector", connector);

                JSONObject dialer = new JSONObject();
                dialer.put("type", "tcp");
                node.put("dialer", dialer);

                JSONArray nodes = new JSONArray();
                nodes.add(node);

                JSONObject hop = new JSONObject();
                hop.put("name", "hop-" + hopNum);
                hop.put("nodes", nodes);

                hops.add(hop);
                hopNum++;
            } catch (Exception e) {
                continue;
            }
        }

        JSONObject data = new JSONObject();
        data.put("name", name + "_ss_chain");
        data.put("hops", hops);

        JSONObject req = new JSONObject();
        req.put("chain", name + "_ss_chain");
        req.put("data", data);

        return WebSocketServer.send_msg(node_id, req, "UpdateChains");
    }

    /**
     * 删除SS代理链
     */
    public static GostDto DeleteSSChain(Long node_id, String name) {
        JSONObject data = new JSONObject();
        data.put("chain", name + "_ss_chain");
        return WebSocketServer.send_msg(node_id, data, "DeleteChains");
    }

    /**
     * 添加多级隧道转发链
     * 支持多个节点的级联转发: A -> B -> C -> ... -> 目标
     *
     * @param node_id 入口节点ID
     * @param name 服务名称
     * @param hopNodesJson 多级节点配置JSON字符串
     * @return 操作结果
     */
    public static GostDto AddMultiHopChains(Long node_id, String name, String hopNodesJson) {
        try {
            JSONArray hopNodesArray = JSONArray.parseArray(hopNodesJson);
            if (hopNodesArray == null || hopNodesArray.isEmpty()) {
                GostDto error = new GostDto();
                error.setCode(-1);
                error.setMsg("多级节点配置不能为空");
                return error;
            }

            JSONArray hops = new JSONArray();

            // 按hopOrder排序，确保节点顺序正确
            hopNodesArray.sort((o1, o2) -> {
                JSONObject j1 = (JSONObject) o1;
                JSONObject j2 = (JSONObject) o2;
                return j1.getInteger("hopOrder") - j2.getInteger("hopOrder");
            });

            // 为每个节点创建一个hop
            for (int i = 0; i < hopNodesArray.size(); i++) {
                JSONObject hopNode = hopNodesArray.getJSONObject(i);

                String nodeIp = hopNode.getString("nodeIp");
                Integer port = hopNode.getInteger("port");
                String protocol = hopNode.getString("protocol");
                String interfaceName = hopNode.getString("interfaceName");

                if (protocol == null || protocol.isEmpty()) {
                    protocol = "tls";
                }

                // 构建远程地址
                String remoteAddr = nodeIp + ":" + port;
                if (nodeIp.contains(":")) {
                    remoteAddr = "[" + nodeIp + "]:" + port;
                }

                // 创建dialer
                JSONObject dialer = new JSONObject();
                dialer.put("type", protocol);

                if ("quic".equals(protocol)) {
                    JSONObject metadata = new JSONObject();
                    metadata.put("keepAlive", true);
                    metadata.put("ttl", "10s");
                    dialer.put("metadata", metadata);
                }

                // 创建connector
                JSONObject connector = new JSONObject();
                connector.put("type", "relay");

                // 创建node
                JSONObject node = new JSONObject();
                node.put("name", "hop-node-" + (i + 1));
                node.put("addr", remoteAddr);
                node.put("connector", connector);
                node.put("dialer", dialer);

                if (StringUtils.isNotBlank(interfaceName)) {
                    node.put("interface", interfaceName);
                }

                // 创建hop
                JSONArray nodes = new JSONArray();
                nodes.add(node);

                JSONObject hop = new JSONObject();
                hop.put("name", "hop-" + name + "-" + (i + 1));
                hop.put("nodes", nodes);

                hops.add(hop);
            }

            // 构建chain数据
            JSONObject data = new JSONObject();
            data.put("name", name + "_multi_hop_chains");
            data.put("hops", hops);

            return WebSocketServer.send_msg(node_id, data, "AddChains");

        } catch (Exception e) {
            GostDto error = new GostDto();
            error.setCode(-1);
            error.setMsg("解析多级节点配置失败: " + e.getMessage());
            return error;
        }
    }

    /**
     * 更新多级隧道转发链
     */
    public static GostDto UpdateMultiHopChains(Long node_id, String name, String hopNodesJson) {
        try {
            JSONArray hopNodesArray = JSONArray.parseArray(hopNodesJson);
            if (hopNodesArray == null || hopNodesArray.isEmpty()) {
                GostDto error = new GostDto();
                error.setCode(-1);
                error.setMsg("多级节点配置不能为空");
                return error;
            }

            JSONArray hops = new JSONArray();

            // 按hopOrder排序
            hopNodesArray.sort((o1, o2) -> {
                JSONObject j1 = (JSONObject) o1;
                JSONObject j2 = (JSONObject) o2;
                return j1.getInteger("hopOrder") - j2.getInteger("hopOrder");
            });

            // 为每个节点创建一个hop
            for (int i = 0; i < hopNodesArray.size(); i++) {
                JSONObject hopNode = hopNodesArray.getJSONObject(i);

                String nodeIp = hopNode.getString("nodeIp");
                Integer port = hopNode.getInteger("port");
                String protocol = hopNode.getString("protocol");
                String interfaceName = hopNode.getString("interfaceName");

                if (protocol == null || protocol.isEmpty()) {
                    protocol = "tls";
                }

                String remoteAddr = nodeIp + ":" + port;
                if (nodeIp.contains(":")) {
                    remoteAddr = "[" + nodeIp + "]:" + port;
                }

                JSONObject dialer = new JSONObject();
                dialer.put("type", protocol);

                if ("quic".equals(protocol)) {
                    JSONObject metadata = new JSONObject();
                    metadata.put("keepAlive", true);
                    metadata.put("ttl", "10s");
                    dialer.put("metadata", metadata);
                }

                JSONObject connector = new JSONObject();
                connector.put("type", "relay");

                JSONObject node = new JSONObject();
                node.put("name", "hop-node-" + (i + 1));
                node.put("addr", remoteAddr);
                node.put("connector", connector);
                node.put("dialer", dialer);

                if (StringUtils.isNotBlank(interfaceName)) {
                    node.put("interface", interfaceName);
                }

                JSONArray nodes = new JSONArray();
                nodes.add(node);

                JSONObject hop = new JSONObject();
                hop.put("name", "hop-" + name + "-" + (i + 1));
                hop.put("nodes", nodes);

                hops.add(hop);
            }

            JSONObject data = new JSONObject();
            data.put("name", name + "_multi_hop_chains");
            data.put("hops", hops);

            JSONObject req = new JSONObject();
            req.put("chain", name + "_multi_hop_chains");
            req.put("data", data);

            return WebSocketServer.send_msg(node_id, req, "UpdateChains");

        } catch (Exception e) {
            GostDto error = new GostDto();
            error.setCode(-1);
            error.setMsg("更新多级节点配置失败: " + e.getMessage());
            return error;
        }
    }

    /**
     * 删除多级隧道转发链
     */
    public static GostDto DeleteMultiHopChains(Long node_id, String name) {
        JSONObject data = new JSONObject();
        data.put("chain", name + "_multi_hop_chains");
        return WebSocketServer.send_msg(node_id, data, "DeleteChains");
    }

    private static JSONObject createLimiterData(Long name, String speed) {
        JSONObject data = new JSONObject();
        data.put("name", name.toString());
        JSONArray limits = new JSONArray();
        limits.add("$ " + speed + "MB " + speed + "MB");
        data.put("limits", limits);
        return data;
    }

    private static JSONObject createServiceConfig(String name, Integer in_port, Integer limiter, String remoteAddr, String protocol, Integer fow_type, Tunnel tunnel, String strategy, String interfaceName) {
        JSONObject service = new JSONObject();
        service.put("name", name + "_" + protocol);
        if (Objects.equals(protocol, "tcp")){
            service.put("addr", tunnel.getTcpListenAddr() + ":" + in_port);
        }else {
            service.put("addr", tunnel.getUdpListenAddr() + ":" + in_port);
        }

        if (StringUtils.isNotBlank(interfaceName)) {
            JSONObject metadata = new JSONObject();
            metadata.put("interface", interfaceName);
            service.put("metadata", metadata);
        }


        // 添加限流器配置
        if (limiter != null) {
            service.put("limiter", limiter.toString());
        }

        // 配置处理器
        JSONObject handler = createHandler(protocol, name, fow_type, tunnel);
        service.put("handler", handler);

        // 配置监听器
        JSONObject listener = createListener(protocol);
        service.put("listener", listener);

        // 端口转发需要配置转发器
        if (isPortForwarding(fow_type)) {
            JSONObject forwarder = createForwarder(remoteAddr, strategy);
            service.put("forwarder", forwarder);
        }

        // 端口复用需要配置转发器（转发到目标服务器）
        if (isPortReuse(fow_type) && StringUtils.isNotBlank(remoteAddr)) {
            JSONObject forwarder = createForwarder(remoteAddr, strategy);
            service.put("forwarder", forwarder);
        }
        return service;
    }

    private static JSONObject createHandler(String protocol, String name, Integer fow_type, Tunnel tunnel) {
        JSONObject handler = new JSONObject();

        // 端口复用：使用 TCP handler + SS chain
        if (isPortReuse(fow_type)) {
            handler.put("type", protocol);
            handler.put("chain", name + "_ss_chain");
        } else {
            handler.put("type", protocol);

            // 隧道转发需要添加链配置
            if (isTunnelForwarding(fow_type)) {
                handler.put("chain", name + "_chains");
            }

            // 多级隧道转发需要添加多级链配置
            if (isMultiHopTunnelForwarding(fow_type)) {
                handler.put("chain", name + "_multi_hop_chains");
            }
        }

        return handler;
    }

    private static JSONObject createListener(String protocol) {
        JSONObject listener = new JSONObject();
        listener.put("type", protocol);
        if (Objects.equals(protocol, "udp")){
            JSONObject metadata = new JSONObject();
            metadata.put("keepAlive", true);
            listener.put("metadata", metadata);
        }
        return listener;
    }

    private static JSONObject createForwarder(String remoteAddr, String strategy) {
        JSONObject forwarder = new JSONObject();
        JSONArray nodes = new JSONArray();

        String[] split = remoteAddr.split(",");
        int num = 1;
        for (String addr : split) {
            JSONObject node = new JSONObject();
            node.put("name", "node_" + num );
            node.put("addr", addr);
            nodes.add(node);
            num ++;
        }

        if (strategy == null || strategy.equals("")){
            strategy = "fifo";
        }

        forwarder.put("nodes", nodes);

        JSONObject selector = new JSONObject();
        selector.put("strategy", strategy);
        selector.put("maxFails", 1);
        selector.put("failTimeout", "600s");
        forwarder.put("selector", selector);
        return forwarder;
    }

    private static boolean isPortForwarding(Integer fow_type) {
        return fow_type != null && fow_type == 1;
    }

    private static boolean isTunnelForwarding(Integer fow_type) {
        return fow_type != null && fow_type == 2;
    }

    private static boolean isPortReuse(Integer fow_type) {
        return fow_type != null && fow_type == 3;
    }

    private static boolean isMultiHopTunnelForwarding(Integer fow_type) {
        return fow_type != null && fow_type == 4;
    }

}
