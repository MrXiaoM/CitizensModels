package top.mrxiaom.citizensmodels.wrapper;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import top.mrxiaom.citizensmodels.api.IActiveModel;
import top.mrxiaom.citizensmodels.api.IModelEngine;

import java.util.ArrayList;
import java.util.List;

public class ModelApiWrapper implements IModelEngine {
    private final List<IModelEngine> list = new ArrayList<>();
    public ModelApiWrapper() {}

    public void register(@Nullable IModelEngine api) {
        if (api != null) {
            list.add(api);
        }
    }

    @Override
    public boolean hasModel(@NotNull NPC npc) {
        for (IModelEngine api : list) {
            if (api.hasModel(npc)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean applyModel(@NotNull NPC npc, @NotNull String modelId) {
        for (IModelEngine api : list) {
            if (api.applyModel(npc, modelId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resetModel(@NotNull NPC npc, boolean deSpawn) {
        for (IModelEngine api : list) {
            api.resetModel(npc, deSpawn);
        }
    }

    @Override
    public boolean destroy(@NotNull Entity entity) {
        boolean respawn = false;
        for (IModelEngine api : list) {
            if (api.destroy(entity)) {
                respawn = true;
            }
        }
        return respawn;
    }

    @Override
    public void markHurt(@NonNull NPC npc) {
        for (IModelEngine api : list) {
            api.markHurt(npc);
        }
    }

    @Override
    public void markDeath(@NotNull NPC npc) {
        for (IModelEngine api : list) {
            api.markDeath(npc);
        }
    }

    @Override
    public @NotNull List<String> getOrderedModelIds() {
        List<String> ids = new ArrayList<>();
        for (IModelEngine api : list) {
            ids.addAll(api.getOrderedModelIds());
        }
        return ids;
    }

    @Override
    public @Nullable IActiveModel getActiveModel(@Nullable Entity entity, @Nullable String modelId) {
        for (IModelEngine api : list) {
            IActiveModel activeModel = api.getActiveModel(entity, modelId);
            if (activeModel != null) {
                return activeModel;
            }
        }
        return null;
    }
}
