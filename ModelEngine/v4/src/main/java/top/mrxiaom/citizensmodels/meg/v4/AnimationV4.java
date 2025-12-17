package top.mrxiaom.citizensmodels.meg.v4;

import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import top.mrxiaom.citizensmodels.api.IAnimation;

public class AnimationV4 implements IAnimation {
    private final AnimationHandler handler;
    private final String animation;

    public AnimationV4(AnimationHandler handler, String animation) {
        this.handler = handler;
        this.animation = animation;
    }

    @Override
    public void play(boolean force) {
        this.handler.playAnimation(this.animation, 0, 0.25, 1, force);
    }
}
