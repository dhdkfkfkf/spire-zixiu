package zixiu.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import zixiu.ZixiuMod;
import zixiu.character.ZixiuEnums;

/**
 * 所有字修卡牌的公共基类：ID 自动加 zixiu: 前缀，名字与描述从 CardStrings.json 取。
 *
 * 卡图按 ID 找 zixiu/images/cards/&lt;ID&gt;.png（大图 &lt;ID&gt;_p.png），
 * 找不到就回退到统一占位图，所以美术可以逐张替换，不用改代码。
 */
public abstract class ZixiuCard extends CustomCard {

    public static final String PLACEHOLDER_IMG = "zixiu/images/cards/placeholder.png";

    protected ZixiuCard(String localId, int cost, CardType type, CardRarity rarity, CardTarget target) {
        super(ZixiuMod.makeID(localId), nameOf(localId), cardImage(localId), cost, descOf(localId),
                type, ZixiuEnums.ZIXIU_COLOR, rarity, target);
    }

    private static String cardImage(String localId) {
        String path = "zixiu/images/cards/" + localId + ".png";
        if (Gdx.files != null && Gdx.files.internal(path).exists()) {
            return path;
        }
        return PLACEHOLDER_IMG;
    }

    protected static CardStrings strings(String localId) {
        return CardCrawlGame.languagePack.getCardStrings(ZixiuMod.makeID(localId));
    }

    protected static String nameOf(String localId) {
        return strings(localId).NAME;
    }

    protected static String descOf(String localId) {
        return strings(localId).DESCRIPTION;
    }
}
