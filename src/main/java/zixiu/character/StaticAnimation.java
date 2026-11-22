package zixiu.character;

import basemod.animations.AbstractAnimation;

/**
 * 占位动画：不驱动 Spine 或模型，让 CustomPlayer 走静态贴图渲染分支。
 * 定稿后换成真正的 Spine 角色。
 */
public class StaticAnimation extends AbstractAnimation {

    @Override
    public Type type() {
        return Type.NONE;
    }
}
