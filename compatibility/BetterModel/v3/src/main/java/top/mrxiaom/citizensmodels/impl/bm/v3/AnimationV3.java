package top.mrxiaom.citizensmodels.impl.bm.v3;

import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.data.blueprint.BlueprintAnimation;
import kr.toxicity.model.api.tracker.EntityTracker;
import top.mrxiaom.citizensmodels.api.IAnimation;

public class AnimationV3 implements IAnimation {
    private final EntityTracker tracker;
    private final BlueprintAnimation ba;
    private final String id;
    public AnimationV3(EntityTracker tracker, BlueprintAnimation ba, String id) {
        this.tracker = tracker;
        this.ba = ba;
        this.id = id;
    }

    @Override
    public void play(boolean force) {
        if (force) tracker.stopAnimation(id);
        tracker.animate(ba, AnimationModifier.DEFAULT_WITH_PLAY_ONCE);
    }
}
