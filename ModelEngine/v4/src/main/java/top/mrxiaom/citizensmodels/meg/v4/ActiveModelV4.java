package top.mrxiaom.citizensmodels.meg.v4;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.citizensmodels.api.IActiveModel;
import top.mrxiaom.citizensmodels.api.IAnimation;

import java.util.List;
import java.util.Map;

public class ActiveModelV4 implements IActiveModel {
    private final ActiveModel activeModel;

    public ActiveModelV4(ActiveModel activeModel) {
        this.activeModel = activeModel;
    }

    @Override
    public @NotNull List<String> getAnimationKeys() {
        return List.of();
    }

    @Override
    public @Nullable IAnimation getAnimation(String id) {
        AnimationHandler handler = this.activeModel.getAnimationHandler();
        Map<String, BlueprintAnimation> animations = this.activeModel.getBlueprint().getAnimations();
        return animations.get(id) == null ? null : new AnimationV4(handler, id);
    }
}
