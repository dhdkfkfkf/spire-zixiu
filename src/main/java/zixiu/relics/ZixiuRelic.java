package zixiu.relics;

import basemod.abstracts.CustomRelic;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import zixiu.character.ZixiuEnums;
import zixiu.core.ZixiuConfig;

/**
 * 字修遗物的公共基类。
 *
 * BaseMod 的 `addRelicToCustomPool` 在本版本里是坏的：它依赖的补丁挂在
 * `RelicLibrary.addClassSpecificRelics` 上，而游戏本体从不调用这个方法，所以自定义池
 * 里的遗物永远不会进入实际奖励池。这里改成 SHARED 注册，再用 canSpawn() 做角色隔离——
 * 游戏在生成遗物奖励时会调用 canSpawn()。
 */
public abstract class ZixiuRelic extends CustomRelic {

    /** 子类传进来的已经是完整 ID（{@code ZixiuMod.makeID(...)} 之后的结果），这里不再加前缀。 */
    protected ZixiuRelic(String relicId, String img, RelicTier tier, LandingSound sound) {
        super(relicId, img, tier, sound);
    }

    @Override
    public boolean canSpawn() {
        if (!ZixiuConfig.isolation()) {
            return true;
        }
        if (AbstractDungeon.player == null) {
            // 建池阶段还没有角色，先放行；真正的过滤发生在抽奖励时。
            return true;
        }
        return AbstractDungeon.player.getCardColor() == ZixiuEnums.ZIXIU_COLOR;
    }

    @Override
    public abstract AbstractRelic makeCopy();
}
