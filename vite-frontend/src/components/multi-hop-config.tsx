import { useState } from "react";
import { Button } from "@heroui/button";
import { Input } from "@heroui/input";
import { Select, SelectItem } from "@heroui/select";
import { Card, CardBody } from "@heroui/card";
import { Chip } from "@heroui/chip";

interface HopNode {
  nodeId: number;
  nodeName: string;
  nodeIp: string;
  port: number;
  protocol: string;
  interfaceName?: string;
  hopOrder: number;
}

interface MultiHopConfigProps {
  nodes: Array<{ id: number; name: string; status: number }>;
  value: HopNode[];
  onChange: (hopNodes: HopNode[]) => void;
  inNodeId?: number | null;
  outNodeId?: number | null;
}

export default function MultiHopConfig({ nodes, value, onChange, inNodeId, outNodeId }: MultiHopConfigProps) {
  const [tempHopNode, setTempHopNode] = useState<Partial<HopNode>>({
    protocol: 'tls',
    port: 8080
  });

  // 过滤可用节点（排除入口和出口节点，以及已选择的节点）
  const getAvailableNodes = () => {
    const usedNodeIds = value.map(h => h.nodeId);
    return nodes.filter(node => 
      node.status === 1 && // 只显示在线节点
      node.id !== inNodeId && 
      node.id !== outNodeId &&
      !usedNodeIds.includes(node.id)
    );
  };

  const handleAddHopNode = () => {
    if (!tempHopNode.nodeId || !tempHopNode.nodeIp || !tempHopNode.port) {
      return;
    }

    const selectedNode = nodes.find(n => n.id === tempHopNode.nodeId);
    if (!selectedNode) return;

    const newHopNode: HopNode = {
      nodeId: tempHopNode.nodeId,
      nodeName: selectedNode.name,
      nodeIp: tempHopNode.nodeIp || '',
      port: tempHopNode.port,
      protocol: tempHopNode.protocol || 'tls',
      interfaceName: tempHopNode.interfaceName,
      hopOrder: value.length + 1
    };

    onChange([...value, newHopNode]);
    setTempHopNode({ protocol: 'tls', port: 8080 });
  };

  const handleRemoveHopNode = (index: number) => {
    const newValue = value.filter((_, i) => i !== index);
    // 重新排序
    const reorderedValue = newValue.map((hop, i) => ({ ...hop, hopOrder: i + 1 }));
    onChange(reorderedValue);
  };

  const handleMoveUp = (index: number) => {
    if (index === 0) return;
    const newValue = [...value];
    [newValue[index - 1], newValue[index]] = [newValue[index], newValue[index - 1]];
    // 重新排序
    const reorderedValue = newValue.map((hop, i) => ({ ...hop, hopOrder: i + 1 }));
    onChange(reorderedValue);
  };

  const handleMoveDown = (index: number) => {
    if (index === value.length - 1) return;
    const newValue = [...value];
    [newValue[index], newValue[index + 1]] = [newValue[index + 1], newValue[index]];
    // 重新排序
    const reorderedValue = newValue.map((hop, i) => ({ ...hop, hopOrder: i + 1 }));
    onChange(reorderedValue);
  };

  return (
    <div className="space-y-4">
      <div className="text-sm text-gray-600 dark:text-gray-400">
        配置多级中转节点，流量将按顺序经过这些节点：入口节点 → 中转节点1 → 中转节点2 → ... → 出口节点
      </div>

      {/* 已添加的中转节点列表 */}
      {value.length > 0 && (
        <div className="space-y-2">
          <div className="text-sm font-medium">中转节点链路 ({value.length}个节点)</div>
          {value.map((hop, index) => (
            <Card key={index} className="border-2 border-primary-200 dark:border-primary-800">
              <CardBody className="p-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3 flex-1">
                    <Chip color="primary" size="sm" variant="flat">
                      第{hop.hopOrder}跳
                    </Chip>
                    <div className="flex-1">
                      <div className="font-medium">{hop.nodeName}</div>
                      <div className="text-xs text-gray-500">
                        {hop.nodeIp}:{hop.port} ({hop.protocol})
                        {hop.interfaceName && ` - 网卡: ${hop.interfaceName}`}
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-1">
                    <Button
                      size="sm"
                      variant="flat"
                      isIconOnly
                      isDisabled={index === 0}
                      onPress={() => handleMoveUp(index)}
                    >
                      ↑
                    </Button>
                    <Button
                      size="sm"
                      variant="flat"
                      isIconOnly
                      isDisabled={index === value.length - 1}
                      onPress={() => handleMoveDown(index)}
                    >
                      ↓
                    </Button>
                    <Button
                      size="sm"
                      color="danger"
                      variant="flat"
                      isIconOnly
                      onPress={() => handleRemoveHopNode(index)}
                    >
                      ×
                    </Button>
                  </div>
                </div>
              </CardBody>
            </Card>
          ))}
        </div>
      )}

      {/* 添加新的中转节点 */}
      <Card>
        <CardBody className="p-4 space-y-3">
          <div className="text-sm font-medium">添加中转节点</div>

          <Select
            label="选择节点"
            placeholder="请选择中转节点"
            selectedKeys={tempHopNode.nodeId ? [tempHopNode.nodeId.toString()] : []}
            onChange={(e) => {
              const nodeId = parseInt(e.target.value);
              const node = nodes.find(n => n.id === nodeId);
              setTempHopNode(prev => ({
                ...prev,
                nodeId,
                nodeName: node?.name || ''
              }));
            }}
            variant="bordered"
          >
            {getAvailableNodes().map((node) => (
              <SelectItem key={node.id.toString()}>
                {node.name}
              </SelectItem>
            ))}
          </Select>

          <Input
            label="节点IP地址"
            placeholder="请输入节点IP地址"
            value={tempHopNode.nodeIp || ''}
            onChange={(e) => setTempHopNode(prev => ({ ...prev, nodeIp: e.target.value }))}
            variant="bordered"
            description="该节点的公网IP或域名"
          />

          <Input
            label="中转端口"
            type="number"
            placeholder="请输入端口号"
            value={tempHopNode.port?.toString() || ''}
            onChange={(e) => setTempHopNode(prev => ({ ...prev, port: parseInt(e.target.value) || 0 }))}
            variant="bordered"
            description="该节点监听的端口，用于接收上一跳的连接"
          />

          <Select
            label="协议类型"
            placeholder="选择协议"
            selectedKeys={tempHopNode.protocol ? [tempHopNode.protocol] : ['tls']}
            onChange={(e) => setTempHopNode(prev => ({ ...prev, protocol: e.target.value }))}
            variant="bordered"
          >
            <SelectItem key="tls">TLS</SelectItem>
            <SelectItem key="quic">QUIC</SelectItem>
            <SelectItem key="ws">WebSocket</SelectItem>
            <SelectItem key="wss">WebSocket Secure</SelectItem>
          </Select>

          <Input
            label="网卡名或IP（可选）"
            placeholder="例如: eth0 或 192.168.1.1"
            value={tempHopNode.interfaceName || ''}
            onChange={(e) => setTempHopNode(prev => ({ ...prev, interfaceName: e.target.value }))}
            variant="bordered"
            description="指定出口网卡，留空则自动选择"
          />

          <Button
            color="primary"
            onPress={handleAddHopNode}
            isDisabled={!tempHopNode.nodeId || !tempHopNode.nodeIp || !tempHopNode.port}
            fullWidth
          >
            添加到链路
          </Button>
        </CardBody>
      </Card>
    </div>
  );
}

