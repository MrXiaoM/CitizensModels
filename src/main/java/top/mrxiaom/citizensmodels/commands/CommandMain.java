package top.mrxiaom.citizensmodels.commands;
        
import com.google.common.collect.Lists;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.NPCSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.citizensmodels.Messages;
import top.mrxiaom.citizensmodels.api.IActiveModel;
import top.mrxiaom.citizensmodels.api.IAnimation;
import top.mrxiaom.citizensmodels.func.NPCListener;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.citizensmodels.CitizensModels;
import top.mrxiaom.citizensmodels.func.AbstractModule;
import top.mrxiaom.pluginbase.utils.Pair;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.*;

import static top.mrxiaom.citizensmodels.api.IModelEngine.MODEL_ID_KEY;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(CitizensModels plugin) {
        super(plugin);
        registerCommand("citizensmodels", this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            if (args.length == 2 && "set".equalsIgnoreCase(args[0])) {
                NPCSelector selector = ((Citizens) CitizensAPI.getPlugin()).getNPCSelector();
                NPC npc = selector.getSelected(sender);
                if (npc == null) {
                    return Messages.npc__not_selected.tm(sender);
                }
                String modelId = args[1];
                if (!plugin.getModelApi().getOrderedModelIds().contains(modelId)) {
                    return Messages.npc__blueprint_not_found.tm(sender);
                }
                NPCListener.inst().setNPCModel(npc, modelId);
                return Messages.npc__model_set.tm(sender,
                        Pair.of("%npc_name%", npc.getFullName()),
                        Pair.of("%npc_id%", npc.getId()),
                        Pair.of("%model%", modelId));
            }
            if (args.length == 1 && "reset".equalsIgnoreCase(args[0])) {
                NPCSelector selector = ((Citizens) CitizensAPI.getPlugin()).getNPCSelector();
                NPC npc = selector.getSelected(sender);
                if (npc == null) {
                    return Messages.npc__not_selected.tm(sender);
                }
                NPCListener.inst().setNPCModel(npc, null);
                return Messages.npc__model_reset.tm(sender,
                        Pair.of("%npc_name%", npc.getFullName()),
                        Pair.of("%npc_id%", npc.getId()));
            }
            if (args.length >= 3 && ("ani".equalsIgnoreCase(args[0]) || "animation".equalsIgnoreCase(args[0]))) {
                Integer npcId = Util.parseInt(args[1]).orElse(null);
                NPC npc = npcId == null ? null : CitizensAPI.getNPCRegistry().getById(npcId);
                if (npc == null) {
                    return Messages.npc__not_found.tm(sender,
                            Pair.of("%npc_id%", npcId));
                }
                String modelId = npc.data().get(MODEL_ID_KEY);
                Entity entity = npc.getEntity();
                IActiveModel model = plugin.getModelApi().getActiveModel(entity, modelId);
                if (model == null) {
                    return Messages.npc__model_not_found.tm(sender,
                            Pair.of("%npc_name%", npc.getFullName()),
                            Pair.of("%npc_id%", npc.getId()));
                }
                IAnimation animation = model.getAnimation(args[2]);
                if (animation == null) {
                    return Messages.npc__animation_not_found.tm(sender,
                            Pair.of("%npc_name%", npc.getFullName()),
                            Pair.of("%npc_id%", npc.getId()),
                            Pair.of("%animation%", args[2]));
                }
                animation.play(true);
                return true;
            }
            if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
                plugin.reloadConfig();
                NPCListener.inst().applyForAllNPCs();
                return Messages.commands__reload.tm(sender);
            }
            return Messages.commands__help.tm(sender);
        }
        return true;
    }

    private static final List<String> emptyList = Collections.emptyList();
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                List<String> list = new ArrayList<>();
                NPCSelector selector = ((Citizens) CitizensAPI.getPlugin()).getNPCSelector();
                if (selector.getSelected(sender) != null) {
                    list.add("set");
                    list.add("reset");
                }
                list.add("ani");
                list.add("animation");
                list.add("reload");
                return startsWith(list, args[0]);
            }
            return emptyList;
        }
        if (args.length == 2) {
            if (sender.isOp()) {
                if ("set".equalsIgnoreCase(args[0])) {
                    NPCSelector selector = ((Citizens) CitizensAPI.getPlugin()).getNPCSelector();
                    if (selector.getSelected(sender) != null) {
                        return startsWith(plugin.getModelApi().getOrderedModelIds(), args[1]);
                    } else {
                        return emptyList;
                    }
                }
            }
        }
        if (args.length == 3) {
            if (sender.isOp()) {
                if ("ani".equalsIgnoreCase(args[0]) || "animation".equalsIgnoreCase(args[0])) {
                    Integer npcId = Util.parseInt(args[1]).orElse(null);
                    NPC npc = npcId == null ? null : CitizensAPI.getNPCRegistry().getById(npcId);
                    String modelId = npc == null ? null : npc.data().get(MODEL_ID_KEY);
                    Entity entity = npc == null ? null : npc.getEntity();
                    IActiveModel model = plugin.getModelApi().getActiveModel(entity, modelId);
                    if (model != null) {
                        return startsWith(model.getAnimationKeys(), args[2]);
                    }
                }
            }
        }
        return emptyList;
    }

    public List<String> startsWith(Collection<String> list, String s) {
        return startsWith(null, list, s);
    }
    public List<String> startsWith(String[] addition, Collection<String> list, String s) {
        String s1 = s.toLowerCase();
        List<String> stringList = new ArrayList<>(list);
        if (addition != null) stringList.addAll(0, Lists.newArrayList(addition));
        stringList.removeIf(it -> !it.toLowerCase().startsWith(s1));
        return stringList;
    }
}
