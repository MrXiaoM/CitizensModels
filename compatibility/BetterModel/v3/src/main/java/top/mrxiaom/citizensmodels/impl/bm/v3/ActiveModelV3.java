package top.mrxiaom.citizensmodels.impl.bm.v3;

import kr.toxicity.model.api.data.blueprint.BlueprintAnimation;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.citizensmodels.api.IActiveModel;
import top.mrxiaom.citizensmodels.api.IAnimation;

import java.util.ArrayList;
import java.util.List;

public class ActiveModelV3 implements IActiveModel {
    private final EntityTracker tracker;
    public ActiveModelV3(EntityTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public @NotNull List<String> getAnimationKeys() {
        return new ArrayList<>(tracker.renderer().animations().keySet());
    }

    @Override
    public @Nullable IAnimation getAnimation(String id) {
        BlueprintAnimation ba = tracker.renderer().animation(id).orElse(null);
        if (ba == null) return null;
        return new AnimationV3(tracker, ba, id);
    }
}
