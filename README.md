## CitizensModels

Minecraft Citizens NPC 插件 ModelEngine 模型支持。

## 简介

由于之前我服的管理员测试发现 ModelEngine [对于 Citizens 的支持](https://git.mythiccraft.io/mythiccraft/model-engine-4/-/wikis/Citizens/Commands-and-Permissions)不是那么好，有可能会把模型卡掉，所以打算写一个插件专门用来对接 Citizens 和 ModelEngine。

这样一来，出问题了就不需要去反馈给 ModelEngine，可以自己修。再说，ModelEngine [已经消极对待 Citizens 兼容了](https://git.mythiccraft.io/mythiccraft/model-engine-4/-/wikis/Citizens)，反馈大概率会得到 “请使用 MythicMobs 代替” 的回答。

在你为 Citizens NPC 添加模型后，本插件会在 NPC 的 `metadata` 中写入一个 `model-id`，指向一个蓝图。

在 NPC 生成时，插件调用 ModelEngine 接口，去应用蓝图指定的模型到 NPC 实体。

目前只支持一个模型，通常来说 Citizens NPC 都只是站岗，或者固定路线移动，应该不会有人需要给 NPC 挂两个或以上的模型吧。

## 版本支持

CitizensModels 至少需要 Java 17 才能运行。

目前支持搭配以下版本的插件使用，其它版本请自行尝试，理论上大版本相同可以通用。

支持的 Citizens 版本
+ `2.0.51`

支持的 ModelEngine 版本
+ `R4.1.0`
+ `R3.2.0`
+ *不会去支持v2及以下*

## 命令

所有命令仅管理员可用。  
根命令为 `/citizensmodels`，别名 `/npcmodels`, `/npcmodel`, `/npcm`, `/cm`。  
其中，`<>` 包裹的为必选参数，`[]` 包裹的为可选参数。

| 命令 | 说明 |
| --- | --- |
|  | (使用 Citizens 命令 `/npc sel` 选中 NPC) |
| `/npcm set <蓝图>` | 设置已选中的NPC的模型 |
| `/npcm reset` | 重置已选中的NPC的模型 |
| `/npcm ani <npcId> <动画>` | 为NPC播放模型动画 |
| `/npcm reload` | 重载插件配置文件（暂无） |

## 常见问题

Q: 我在用 ReplayMod 录制回放，剪辑的时候发现，只要不看向 NPC，模型就不动/不见了怎么办？  
> A: 到 ModelEngine 配置文件暂时关闭网络优化选项，录完再开回来。  
> + `Network-Optimization.Vertical-Render-Distance.Enabled`
> + `Network-Optimization.Skip-Models-Behind-Viewer.Enabled`

## 赞助

前往 [爱发电](https://afdian.com/a/mrxiaom) 支持我的工作。
