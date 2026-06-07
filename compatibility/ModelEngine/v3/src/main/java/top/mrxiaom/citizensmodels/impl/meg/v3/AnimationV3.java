package top.mrxiaom.citizensmodels.impl.meg.v3;

import com.ticxo.modelengine.api.animation.AnimationHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import top.mrxiaom.citizensmodels.api.IAnimation;

public class AnimationV3 implements IAnimation {
    private final AnimationHandler handler;
    private final IAnimationProperty animation;

    public AnimationV3(AnimationHandler handler, IAnimationProperty animation) {
        this.handler = handler;
        this.animation = animation;
    }

    @Override
    public void play(boolean force) {
        handler.playAnimation(animation, force);
    }
}
