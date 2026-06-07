package top.mrxiaom.citizensmodels.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IActiveModel {
    @NotNull List<String> getAnimationKeys();
    @Nullable IAnimation getAnimation(String id);
}
