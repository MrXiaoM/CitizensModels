package top.mrxiaom.citizensmodels.impl.meg.v3;

import com.ticxo.modelengine.api.animation.AnimationHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.ActiveModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.citizensmodels.api.IActiveModel;
import top.mrxiaom.citizensmodels.api.IAnimation;

import java.util.List;

public class ActiveModelV3 implements IActiveModel {
    private final ActiveModel activeModel;
    public ActiveModelV3(ActiveModel activeModel) {
        this.activeModel = activeModel;
    }

    @Override
    public @NotNull List<String> getAnimationKeys() {
        return List.of();
    }

    @Override
    public @Nullable IAnimation getAnimation(String id) {
        AnimationHandler handler = activeModel.getAnimationHandler();
        IAnimationProperty animation = handler.getAnimation(id);
        return animation == null ? null : new AnimationV3(handler, animation);
    }
}
