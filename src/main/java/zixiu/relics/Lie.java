package zixiu.relics;

import basemod.BaseMod;
import com.megacrit.cardcrawl.relics.AbstractRelic;

/** 列：手牌上限 +2。 */
public class Lie extends InscriptionRelic {

    public static final String ID = "Lie";
    public static final int EXTRA_HAND = 2;

    private boolean applied = false;

    public Lie() {
        super(ID, "lie.png");
    }

    @Override
    public void onEquip() {
        applied = true;
        BaseMod.MAX_HAND_SIZE += EXTRA_HAND;
    }

    @Override
    public void onUnequip() {
        if (applied) {
            applied = false;
            BaseMod.MAX_HAND_SIZE -= EXTRA_HAND;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Lie();
    }
}
