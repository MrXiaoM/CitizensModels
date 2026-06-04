package top.mrxiaom.citizensmodels.impl.bm;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bukkit.BetterModelBukkit;
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.platform.PlatformEntity;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.citizensmodels.api.IActiveModel;
import top.mrxiaom.citizensmodels.api.IModelEngine;

import java.util.*;
import java.util.function.Consumer;

public class BetterModelV3 implements IModelEngine {
    private final BetterModelBukkit platform = BetterModelBukkit.platform();
    // TODO: 暂时没有找到哪里有受伤动画名称定义，暂时使用缺省值
    private static final String ANIMATION_HURT = "hurt";
    // https://github.com/toxicity188/BetterModel/wiki/Animating-your-own-model
    private static final String ANIMATION_DEATH = "death";

    private final Consumer<Runnable> runTask;
    public BetterModelV3(Consumer<Runnable> runTask) {
        this.runTask = runTask;
    }

    @Override
    public boolean applyModel(@NotNull NPC npc, @NotNull String modelId) {
        if (modelId.startsWith("bm:")) {
            modelId = modelId.substring(3);
        }
        ModelRenderer renderer = platform.modelManager().model(modelId);
        if (renderer == null) {
            return false;
        }
        PlatformEntity entity = BukkitAdapter.adapt(npc.getEntity());
        BetterModel.registry(entity).ifPresent(EntityTrackerRegistry::close);
        renderer.create(entity);
        return true;
    }

    @Override
    public void resetModel(@NotNull NPC npc, boolean deSpawn) {
        Entity entity = npc.getEntity();
        EntityTrackerRegistry registry = BetterModel.registry(BukkitAdapter.adapt(entity)).orElse(null);
        if (registry != null && !registry.isClosed()) {
            Location loc = entity.getLocation();
            registry.close();
            if (!deSpawn) runTask.accept(() -> {
                npc.despawn();
                npc.spawn(loc);
            });
        }
    }

    @Override
    public boolean destroy(@NotNull Entity entity) {
        EntityTrackerRegistry registry = BetterModel.registry(BukkitAdapter.adapt(entity)).orElse(null);
        if (registry == null || registry.isClosed()) return false;
        registry.close();
        return true;
    }

    @Override
    public void markHurt(@NotNull NPC npc) {
        EntityTrackerRegistry registry = BetterModel
                .registry(BukkitAdapter.adapt(npc.getEntity()))
                .orElse(null);
        if (registry == null || registry.isClosed()) return;
        for (EntityTracker tracker : registry.trackers()) {
            tracker.animate(ANIMATION_HURT, AnimationModifier.DEFAULT_WITH_PLAY_ONCE);
        }
    }

    @Override
    public void markDeath(@NotNull NPC npc) {
        EntityTrackerRegistry registry = BetterModel
                .registry(BukkitAdapter.adapt(npc.getEntity()))
                .orElse(null);
        if (registry == null || registry.isClosed()) return;
        for (EntityTracker tracker : registry.trackers()) {
            tracker.animate(ANIMATION_DEATH, AnimationModifier.DEFAULT_WITH_PLAY_ONCE);
        }
    }

    @Override
    public @NotNull List<String> getOrderedModelIds() {
        List<String> list = new ArrayList<>(platform.modelManager().modelKeys());
        Collections.sort(list);
        return list;
    }

    @Override
    public @Nullable IActiveModel getActiveModel(@Nullable Entity entity, @Nullable String modelId) {
        if (entity == null || modelId == null) return null;
        EntityTrackerRegistry registry = BetterModel
                .registry(BukkitAdapter.adapt(entity))
                .orElse(null);
        if (registry == null) return null;
        EntityTracker tracker = registry.tracker(modelId);
        if (tracker == null) return null;
        return new ActiveModelV3(tracker);
    }
}
