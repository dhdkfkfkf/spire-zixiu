package zixiu.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zixiu.ZixiuMod;
import zixiu.character.ZixiuEnums;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 所有字修卡牌的公共基类：ID 自动加 zixiu: 前缀，名字与描述从 CardStrings.json 取。
 *
 * 卡图按 ID 找 zixiu/images/cards/&lt;ID&gt;.png（大图 &lt;ID&gt;_p.png），
 * 找不到就回退到统一占位图，所以美术可以逐张替换，不用改代码。
 */
public abstract class ZixiuCard extends CustomCard {

    public static final String PLACEHOLDER_IMG = "zixiu/images/cards/placeholder.png";

    private static final Logger logger = LogManager.getLogger(ZixiuCard.class);

    /**
     * localId → 实际用的贴图路径。**每个字只在真进程里判一次**。
     *
     * 求解器的镜像沙盒会把 {@code Gdx.files} 换成不碰磁盘的假实现，于是所有 exists() 都返回 false，
     * 卡图路径退成占位图；占位图又加载不出来，BaseMod 在 loadCardImage 里直接 NPE。
     * 卡池在模组加载时就构造过一遍，缓存下来之后沙盒只用路径，不再依赖 exists()。
     */
    private static final Map<String, String> IMAGE_PATHS = new HashMap<String, String>();

    /** 加载失败的路径只报一次。 */
    private static final Set<String> LOAD_FAILED = new HashSet<String>();

    protected ZixiuCard(String localId, int cost, CardType type, CardRarity rarity, CardTarget target) {
        super(ZixiuMod.makeID(localId), nameOf(localId), cardImage(localId), cost, descOf(localId),
                type, ZixiuEnums.ZIXIU_COLOR, rarity, target);
    }

    private static String cardImage(String localId) {
        String cached = IMAGE_PATHS.get(localId);
        if (cached != null) {
            return cached;
        }
        String resolved;
        try {
            String path = "zixiu/images/cards/" + localId + ".png";
            resolved = Gdx.files != null && Gdx.files.internal(path).exists() ? path : PLACEHOLDER_IMG;
        } catch (Throwable t) {
            resolved = PLACEHOLDER_IMG;
        }
        IMAGE_PATHS.put(localId, resolved);
        return resolved;
    }

    /**
     * 没有纹理的环境（求解器沙盒、无头测试）里 {@code ImageMaster.loadImage} 返回 null，
     * BaseMod 接着调 {@code texture.getWidth()} 就 NPE——一张牌造不出来，整条推演分支全崩。
     * 镜像不需要贴图，这里吞掉异常继续构造；真机上图正常时行为完全不变。
     */
    @Override
    public void loadCardImage(String img) {
        try {
            super.loadCardImage(img);
        } catch (Throwable t) {
            if (LOAD_FAILED.add(img)) {
                logger.warn("[zixiu] card image failed to load, continuing without texture: "
                        + img + " (" + t.getClass().getSimpleName() + ")");
            }
        }
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
