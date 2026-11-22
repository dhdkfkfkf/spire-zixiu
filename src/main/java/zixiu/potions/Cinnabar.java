package zixiu.potions;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import zixiu.ZixiuMod;
import zixiu.powers.BurnPower;

public class Cinnabar extends AbstractPotion {

    public Cinnabar() {
        super("朱砂", ZixiuMod.makeID("Cinnabar"), PotionRarity.UNCOMMON, PotionSize.S, PotionColor.FIRE);
        this.isThrown = true;
        this.tips.add(new PowerTip("灼烧", "回合结束时失去等量生命，不递减。"));
    }

    @Override
    public void use(AbstractCreature target) {
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (!monster.isDeadOrEscaped()) {
                BurnPower.apply(monster, 3);
            }
        }
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 3;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new Cinnabar();
    }
}
