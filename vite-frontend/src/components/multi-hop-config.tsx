import { useState } from "react";
import { Button } from "@heroui/button";
import { Select, SelectItem } from "@heroui/select";
import { Card, CardBody } from "@heroui/card";
import { Chip } from "@heroui/chip";

interface HopNode {
  nodeId: number;
  nodeName: string;
  nodeIp: string;
  port: number;
  protocol: string;
  hopOrder: number;
}

interface Node {
  id: number;
  name: string;
  status: number;
  serverIp?: string;
  portSta?: number;
  portEnd?: number;
}

interface MultiHopConfigProps {
  nodes: Array<Node>;
  value: HopNode[];
  onChange: (hopNodes: HopNode[]) => void;
  inNodeId?: number | null;
  outNodeId?: number | null;
}

export default function MultiHopConfig({ nodes, value, onChange, inNodeId, outNodeId }: MultiHopConfigProps) {
  const [tempHopNode, setTempHopNode] = useState<Partial<HopNode>>({
    protocol: 'tls'
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
    if (!tempHopNode.nodeId) {
      return;
    }

    const selectedNode = nodes.find(n => n.id === tempHopNode.nodeId);
    if (!selectedNode) return;

    // 使用节点的serverIp作为nodeIp，port设置为0表示由后端自动分配
    const newHopNode: HopNode = {
      nodeId: tempHopNode.nodeId,
      nodeName: selectedNode.name,
      nodeIp: selectedNode.serverIp || '', // 使用节点的serverIp
      port: 0, // 0表示由后端自动分配端口
      protocol: tempHopNode.protocol || 'tls',
      hopOrder: value.length + 1
    };

    onChange([...value, newHopNode]);
    setTempHopNode({ protocol: 'tls' });
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
            description="节点IP和端口将由后端自动匹配"
          >
            {getAvailableNodes().map((node) => (
              <SelectItem key={node.id.toString()}>
                {node.name}
              </SelectItem>
            ))}
          </Select>

          <Select
            label="协议类型"
            placeholder="请选择协议类型"
            selectedKeys={tempHopNode.protocol ? [tempHopNode.protocol] : ['tls']}
            onChange={(e) => setTempHopNode(prev => ({ ...prev, protocol: e.target.value }))}
            variant="bordered"
          >
            <SelectItem key="tls">TLS</SelectItem>
            <SelectItem key="wss">WSS</SelectItem>
            <SelectItem key="tcp">TCP</SelectItem>
            <SelectItem key="mtls">MTLS</SelectItem>
            <SelectItem key="mwss">MWSS</SelectItem>
            <SelectItem key="mtcp">MTCP</SelectItem>
          </Select>

          <Button
            color="primary"
            onPress={handleAddHopNode}
            isDisabled={!tempHopNode.nodeId}
            fullWidth
          >
            添加到链路
          </Button>
        </CardBody>
      </Card>
    </div>
  );
}

