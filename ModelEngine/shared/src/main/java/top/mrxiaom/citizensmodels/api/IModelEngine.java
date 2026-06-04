package top.mrxiaom.citizensmodels.api;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Internal
@SuppressWarnings("UnusedReturnValue")
public interface IModelEngine {
    String MODEL_ID_KEY = "model-id";
    default boolean applyModel(@NotNull NPC npc) {
        String modelId = npc.data().get(IModelEngine.MODEL_ID_KEY, null);
        if (modelId == null) return false;
        return applyModel(npc, modelId);
    }
    boolean applyModel(@NotNull NPC npc, @NotNull String modelId);
    void resetModel(@NotNull NPC npc, boolean deSpawn);
    boolean destroy(@NotNull Entity entity);

    void markHurt(@NotNull NPC npc);
    void markDeath(@NotNull NPC npc);

    @NotNull List<String> getOrderedModelIds();
    @Nullable IActiveModel getActiveModel(@Nullable Entity entity, @Nullable String modelId);
}
