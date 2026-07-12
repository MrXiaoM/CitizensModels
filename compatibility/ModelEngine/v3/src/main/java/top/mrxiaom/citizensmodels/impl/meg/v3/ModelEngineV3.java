package top.mrxiaom.citizensmodels.impl.meg.v3;

import com.google.common.collect.Lists;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.generator.model.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.mananger.ModelTicker;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.citizensmodels.api.IActiveModel;
import top.mrxiaom.citizensmodels.api.IModelEngine;

import java.util.List;
import java.util.function.Consumer;

public class ModelEngineV3 implements IModelEngine {
    private final Consumer<Runnable> runTask;
    public ModelEngineV3(Consumer<Runnable> runTask) {
        this.runTask = runTask;
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
        ModeledEntity old = ModelEngineAPI.getModeledEntity(entity.getUniqueId());
        if (old != null) destroy(old);
        ModeledEntity modeled = ModelEngineAPI.getOrCreateModeledEntity(entity);
        modeled.setBaseEntityVisible(false);
        modeled.addModel(model, false);

        ModelTicker modelTicker = ModelEngineAPI.getModelTicker();
        modelTicker.registerModeledEntity(modeled.getBase(), modeled);
        return true;
    }

    @Override
    public void resetModel(@NotNull NPC npc, boolean deSpawn) {
        Entity entity = npc.getEntity();
        if (entity == null) return;
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(entity.getUniqueId());
        if (modeled != null) {
            Location loc = entity.getLocation();
            destroy(modeled);
            if (!deSpawn) runTask.accept(() -> {
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
        modeled.destroy();
        List<String> keys = Lists.newArrayList(modeled.getModels().keySet());
        for (String key : keys) {
            modeled.removeModel(key);
        }
    }

    @Override
    public void markHurt(@NotNull NPC npc) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(npc.getEntity().getUniqueId());
        if (modeled != null) {
            modeled.hurt();
        }
    }

    @Override
    public void markDeath(@NotNull NPC npc) {
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(npc.getEntity().getUniqueId());
        if (modeled != null) {
            modeled.setState(ModelState.DEATH);
        }
    }

    @Override
    public @NotNull List<String> getOrderedModelIds() {
        return Lists.newArrayList(ModelEngineAPI.api.getModelRegistry().getAllBlueprintId());
    }

    @Override
    public @Nullable IActiveModel getActiveModel(@Nullable Entity entity, @Nullable String modelId) {
        if (entity == null || modelId == null) return null;
        ModeledEntity modeled = ModelEngineAPI.getModeledEntity(entity.getUniqueId());
        if (modeled == null) return null;
        ActiveModel model = modeled.getModel(modelId);
        if (model == null) return null;
        return new ActiveModelV3(model);
    }
}
