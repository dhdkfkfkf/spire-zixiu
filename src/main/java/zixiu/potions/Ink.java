package zixiu.potions;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;
import zixiu.lexicon.Component;

public class Ink extends AbstractPotion {


    public Ink() {
        super("墨汁", ZixiuMod.makeID("Ink"), PotionRarity.COMMON, PotionSize.S, PotionColor.BLUE);
        this.isThrown = false;
        this.tips.add(new PowerTip("部件牌", "0 费，可用于拼合。"));
    }

    @Override
    public void use(AbstractCreature target) {
        for (int i = 0; i < 2; i++) {
            Component component = Component.values()[AbstractDungeon.cardRandomRng.random(Component.values().length - 1)];
            AbstractCard card = CardComponents.createComponent(component);
            if (card != null) {
                addToBot(new MakeTempCardInHandAction(card, false));
            }
        }
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 2;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new Ink();
    }
}
