package top.mrxiaom.citizensmodels.impl.meg.v4;

import com.google.common.collect.Lists;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModelRegistry;
import com.ticxo.modelengine.api.model.ModelUpdaters;
import com.ticxo.modelengine.api.model.ModeledEntity;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.citizensmodels.api.IActiveModel;
import top.mrxiaom.citizensmodels.api.IModelEngine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ModelEngineV4 implements IModelEngine {
    private final Consumer<Runnable> runTask;
    private Method getOrderedId;

    public ModelEngineV4(Consumer<Runnable> runTask) {
        this.runTask = runTask;
        try {
            this.getOrderedId = ModelRegistry.class.getDeclaredMethod("getOrderedId");
        } catch (Throwable ignored) {
        }
    }

    @Override
    public boolean hasModel(@NotNull NPC npc) {
        Entity entity = npc.getEntity();
        return ModelEngineAPI.getModeledEntity(entity.getUniqueId()) != null;
    }

    @Override
    public boolean applyModel(@NotNull NPC npc, @NotNull String modelId) {
        if (modelId.startsWith("meg:")) {
            modelId = modelId.substring(4);
        }
        ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(modelId);
        if (blueprint == null) {
            return false;
        }
        ActiveModel model = ModelEngineAPI.createActiveModel(blueprint);

        Entity entity = npc.getEntity();
        ModeledEntity old = ModelEngineAPI.getModeledEntity(entity);
        if (old != null) this.destroy(old);
        ModeledEntity modeled = ModelEngineAPI.getOrCreateModeledEntity(entity);
        modeled.setBaseEntityVisible(false);
        modeled.addModel(model, false);
        ModelUpdaters updaters = ModelEngineAPI.getAPI().getModelUpdaters();
        updaters.registerModeledEntity(modeled.getBase(), modeled);
        return true;
    }

    @Override
    public void resetModel(@NotNull NPC npc, boolean deSpawn) {
        Entity entity = npc.getEntity();
        if (entity == null) return;
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(entity);
        if (modeled != null) {
            Location loc = entity.getLocation();
            this.destroy(modeled);
            if (!deSpawn) this.runTask.accept(() -> {
                npc.despawn();
                npc.spawn(loc);
            });
        }
    }

    @Override
    public boolean destroy(@NotNull Entity entity) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(entity.getUniqueId());
        if (modeled != null) {
            destroy(modeled);
            return true;
        }
        return false;
    }

    private void destroy(ModeledEntity modeled) {
        modeled.restore();
        modeled.destroy();
        List<String> keys = Lists.newArrayList(modeled.getModels().keySet());
        for (String key : keys) {
            modeled.removeModel(key);
        }
    }

    @Override
    public void markHurt(@NotNull NPC npc) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(npc.getEntity());
        if (modeled != null) {
            modeled.markHurt();
        }
    }

    @Override
    public void markDeath(@NotNull NPC npc) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(npc.getEntity());
        if (modeled != null) {
            modeled.markRemoved();
        }
    }

    @Override
    public @NotNull List<String> getOrderedModelIds() {
        try {
            return Lists.newArrayList(ModelEngineAPI.getAPI().getModelRegistry().getOrderedId());
        } catch (Throwable t) {
            try {
                Object object = this.getOrderedId.invoke(ModelEngineAPI.getAPI().getModelRegistry());
                if (object instanceof Collection<?>) {
                    List<String> list = new ArrayList<>();
                    for (Object o : ((Collection<?>) object)) {
                        list.add(String.valueOf(o));
                    }
                    return list;
                }
            } catch (Throwable ignored) {
            }
            throw t;
        }
    }

    @Override
    public @Nullable IActiveModel getActiveModel(@Nullable Entity entity, @Nullable String modelId) {
        if (entity == null || modelId == null) return null;
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(entity);
        if (modeled == null) return null;
        ActiveModel model = modeled.getModel(modelId).orElse(null);
        if (model == null) return null;
        return new ActiveModelV4(model);
    }
}
