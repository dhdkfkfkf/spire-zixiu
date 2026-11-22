package zixiu.potions;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import zixiu.ZixiuMod;
import zixiu.cards.CardComponents;

/**
 * 拓片：复制手牌中一张成品牌。
 */
public class Rubbing extends AbstractPotion {

    public Rubbing() {
        super("拓片", ZixiuMod.makeID("Rubbing"), PotionRarity.RARE, PotionSize.S, PotionColor.BLUE);
        this.isThrown = false;
        this.tips.add(new PowerTip("成品牌", "由部件拼合而来的卡牌。"));
    }

    @Override
    public void use(AbstractCreature target) {
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (CardComponents.glyphOf(card) != null) {
                AbstractCard copy = card.makeStatEquivalentCopy();
                addToBot(new MakeTempCardInHandAction(copy, false));
                return;
            }
        }
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 1;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new Rubbing();
    }
}
