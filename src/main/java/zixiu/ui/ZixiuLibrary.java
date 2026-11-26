package zixiu.ui;

import basemod.interfaces.PostUpdateSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBar;
import com.megacrit.cardcrawl.screens.mainMenu.ScrollBarListener;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButtonListener;
import zixiu.character.ZixiuCharacter;
import zixiu.character.ZixiuEnums;
import zixiu.core.ZixiuConfig;

import java.util.List;
import java.util.Map;

/**
 * 「新华字典」：局内 / 地图上的字修卡牌图鉴。
 *
 * <p>可用时机和顶栏右上角的**牌组按钮**完全一致（{@code TopPanel.updateDeckViewButtonLogic()}
 * 里那张「哪些界面不禁用牌组按钮」的清单）：战斗、地图、商店、事件、营火、各类奖励/选牌界面都能打开。
 *
 * <p>打开时和原版牌组界面一样**接管屏幕**：记下原来的 {@code screen} 再压成 {@code NONE}，
 * 底下那个界面的 update / render 都不再跑，鼠标不会点到它；收起时再把 {@code screen} 放回去。
 * 期间如果玩家点了顶栏的牌组 / 地图 / 设置按钮（它们也会把 screen 换掉），我们就安静退出，不跟它抢。
 *
 * <p>界面本身照抄原版图鉴（{@code CardLibraryScreen}）：
 * <ul>
 *   <li>深蓝的排序条（{@code COLOR_TAB_BAR} 用字修的主色染），成牌 5 列 0.75 倍；</li>
 *   <li>{@code START_X} / {@code SPACE_X} 的排序按钮：稀有度 / 类型 / 耗能 / A-Z，点一下切升降序；</li>
 *   <li>右侧「看升级版」勾选框（原版 {@code ColorTabBar} 的同款实现，直接切 {@code SingleCardViewPopup.isViewingUpgrade}）；</li>
 *   <li>按住任意空白处 1:1 拖动、滚轮、原版 {@code ScrollBar}，越界回弹。</li>
 * </ul>
 *
 * 绘制挂在 {@code AbstractDungeon.render} 之后（光标之前），所以界面底下就是鼠标指针。
 */
public class ZixiuLibrary implements PostUpdateSubscriber {

    // ---- 布局（照抄 CardLibraryScreen / ColorTabBar） ----

    private static final int COLS = 5;
    private static final float CARD_SCALE = 0.75F;
    /** 排序条比第一行牌高出多少（原版图鉴是 230 * yScale）。 */
    private static final float HEADER_LIFT = 230.0F;
    private static final float BAR_W = 1334.0F;
    private static final float BAR_H = 102.0F;

    /** 顶栏图标：和原版牌组 / 地图 / 设置一样是 64 * scale 的方形，间隔 74 * scale。 */
    private static final float ICON_SIZE = 64.0F;
    private static final float ICON_SLOT_SPACING = 74.0F;
    /** 排序条的颜色：和字修的卡框主色一致（{@code ZixiuMod.addColor} 里的那个深蓝灰）。 */
    private static final Color BAR_COLOR = new Color(0.14F, 0.16F, 0.22F, 1.0F);

    /** 原版「看升级版」勾选框的几何，抄自 ColorTabBar.update / renderViewUpgrade。 */
    private static final float UPGRADE_HB_X = 1410.0F;
    private static final float UPGRADE_HB_W = 360.0F;
    private static final float UPGRADE_HB_H = 48.0F;
    private static final float UPGRADE_LABEL_X = 1546.0F;
    private static final float UPGRADE_BOX_X = 1532.0F;

    /**
     * 布局每帧现算：模块加载时 {@code Settings.scale} 还是 0，写死在静态初始化里会得到 0 宽的格子。
     */
    private static float drawStartX;
    private static float drawStartY;
    private static float padX;
    private static float padY;

    // ---- 滚动状态 ----

    private static float currentDiffY;
    private static float grabStartY;
    private static boolean grabbedScreen;
    private static float scrollLowerBound;
    private static float scrollUpperBound;
    /** 刚排完序：这一帧把牌直接摆到新位置（原版图鉴的做法，不让牌横穿整屏）。 */
    private static boolean justSorted;

    private static AbstractCard hoveredCard;
    private static AbstractCard clickStartedCard;
    private static ScrollBar scrollBar;
    private static SortHeaderButton[] sortButtons;
    /** 每个排序按钮对应的种类（A-Z 可以在原版设置里关掉，所以按钮数量不固定）。 */
    private static int[] sortKinds;

    // 和原版图鉴一致：稀有度 / 类型 / 耗能 / A-Z
    private static final int SORT_RARITY = 0;
    private static final int SORT_TYPE = 1;
    private static final int SORT_COST = 2;
    private static final int SORT_NAME = 3;

    private static int sortKind = SORT_TYPE;

    // ---- 开关状态 ----

    private static boolean wantOpen;

    private static CardGroup allCards;

    private static final Hitbox ICON_HB = new Hitbox(ICON_SIZE, ICON_SIZE);
    private static final Hitbox UPGRADE_HB = new Hitbox(UPGRADE_HB_W, UPGRADE_HB_H);

    private static Texture iconTexture;
    /** 和原版顶栏按钮一致的晃动：悬停歪 15°，开着图鉴时按 sin 来回摇。 */
    private static float iconAngle;
    private static float iconRotateTimer;

    private static final ScrollBarListener BAR_LISTENER = new ScrollBarListener() {
        @Override
        public void scrolledUsingBar(float percent) {
            ZixiuLibrary.scrolledUsingBar(percent);
        }
    };

    private static final SortHeaderButtonListener SORT_LISTENER = new SortHeaderButtonListener() {
        @Override
        public void didChangeOrder(SortHeaderButton button, boolean ascending) {
            ZixiuLibrary.didChangeOrder(button, ascending);
        }
    };

    // ---- 查询 ----

    public static boolean isOpen() {
        return wantOpen;
    }

    /**
     * 能不能打开：用的是原版牌组按钮的禁用清单。
     *
     * <p>清单来自 {@code TopPanel.updateDeckViewButtonLogic()} —— 不在这些界面里时牌组按钮是灰的，
     * 这块「典」也一起不出现。
     */
    private static boolean deckButtonAvailable() {
        // 刚进游戏的那几帧 / 转场时 AbstractDungeon.screen 还是 null，switch 会直接 NPE
        AbstractDungeon.CurrentScreen screen = AbstractDungeon.screen;
        if (screen == null) {
            return false;
        }
        switch (screen) {
            case NONE:
            case GRID:
            case COMBAT_REWARD:
            case MASTER_DECK_VIEW:
            case DEATH:
            case VICTORY:
            case BOSS_REWARD:
            case SHOP:
            case SETTINGS:
            case INPUT_SETTINGS:
            case MAP:
            case HAND_SELECT:
            case CARD_REWARD:
                return true;
            default:
                return false;
        }
    }

    private static boolean canUseLibrary() {
        if (!ZixiuConfig.libraryEnabled()) {
            return false;
        }
        if (!(AbstractDungeon.player instanceof ZixiuCharacter)) {
            return false;
        }
        if (CardCrawlGame.mode != CardCrawlGame.GameMode.GAMEPLAY
                && CardCrawlGame.mode != CardCrawlGame.GameMode.DUNGEON_TRANSITION) {
            return false;
        }
        // 开局最初几帧房间还没建好；这时打开图鉴再收起会走到 closeCurrentScreen → getCurrRoom() 的 NPE
        if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom() == null) {
            return false;
        }
        return deckButtonAvailable();
    }

    // ---- 输入 ----

    @Override
    public void receivePostUpdate() {
        if (wantOpen) {
            updateOpen();
            return;
        }
        if (!canUseLibrary() || CardCrawlGame.isPopupOpen) {
            return;
        }
        updateLayout();
        updateIconHb();
        updateIconAngle();
        if (ICON_HB.hovered && InputHelper.justClickedLeft) {
            open();
        }
    }

    private static void updateOpen() {
        // 别的界面接管了 screen（比如点了顶栏的牌组/地图/设置按钮），或者已经不在能用的界面了：
        // 安静退出，screen 和遮罩都留给现在那个界面管。
        if (!canUseLibrary()
                || AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
            abandon();
            return;
        }

        updateLayout();
        updateIconHb();
        updateIconAngle();

        if (CardCrawlGame.isPopupOpen) {
            return;
        }

        // 先看「典」牌：它永远在最上层，点它就收起来，别让底下的卡再响应这一下
        if (ICON_HB.hovered && InputHelper.justClickedLeft) {
            close();
            return;
        }

        updateHeader();
        boolean barDragging = scrollBar.update();
        if (!barDragging) {
            updateScrolling();
        }
        updateCards();
        updateClicking();

        // 右键不再关闭图鉴（和原版看牌界面一致，只用 Esc / 「返回」/ 字典图标收起来）
        if (InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            close();
        }
    }

    private static void open() {
        wantOpen = true;
        updateLayout();
        calculateScrollBounds();

        grabbedScreen = false;
        hoveredCard = null;
        clickStartedCard = null;
        justSorted = false;
        currentDiffY = scrollLowerBound;
        grabStartY = currentDiffY;

        buildWidgets();
        snapCards();
        updateHeader();

        // 和原版牌组界面 MasterDeckViewScreen.open() 一模一样地接管屏幕：
        // 记住从哪来（previousScreen），占住 MASTER_DECK_VIEW，压黑、收战斗面板和「继续」，
        // 显示原版那个「返回」按钮。这样顶栏的地图 / 牌组 / 设置按钮都能直接把我们切走。
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.NONE
                && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
            AbstractDungeon.previousScreen = AbstractDungeon.screen;
        }
        AbstractDungeon.screen = AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW;
        AbstractDungeon.isScreenUp = true;

        AbstractDungeon.overlayMenu.showBlackScreen();
        AbstractDungeon.overlayMenu.hideCombatPanels();
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        AbstractDungeon.dynamicBanner.hide();

        updateBarPosition();
    }

    /** 收起：完全交给原版 closeCurrentScreen，遮罩 / 面板 / previousScreen 都由它还原。 */
    private static void close() {
        wantOpen = false;
        grabbedScreen = false;
        hoveredCard = null;
        clickStartedCard = null;
        // closeCurrentScreen 内部会拿房间；房间还没建好时（理论上打不开，保险起见）直接复位
        if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null) {
            AbstractDungeon.closeCurrentScreen();
        } else {
            AbstractDungeon.screen = AbstractDungeon.CurrentScreen.NONE;
            AbstractDungeon.isScreenUp = false;
            AbstractDungeon.overlayMenu.hideBlackScreen();
        }
    }

    /**
     * 给 {@code LibraryTopBarSwitch} 用：图鉴开着时点了顶栏的地图 / 牌组 / 设置，
     * 先把图鉴收起来（回到原界面），再让原版那一下点击去打开对应界面。
     */
    public static void closeForTopBarSwitch() {
        if (wantOpen) {
            close();
        }
    }

    /** 被别的界面抢了屏幕（原版自己关了 / 别的 mod 切了）：只清自己的状态。 */
    private static void abandon() {
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
            close();
            return;
        }
        wantOpen = false;
        grabbedScreen = false;
        hoveredCard = null;
        clickStartedCard = null;
    }

    // ---- 滚动：MasterDeckViewScreen 的模型 ----

    private static void updateScrolling() {
        int y = InputHelper.mY;
        if (!grabbedScreen) {
            if (InputHelper.scrolledDown) {
                currentDiffY += Settings.SCROLL_SPEED;
            } else if (InputHelper.scrolledUp) {
                currentDiffY -= Settings.SCROLL_SPEED;
            }
            if (InputHelper.justClickedLeft) {
                grabbedScreen = true;
                grabStartY = y - currentDiffY;
            }
        } else if (InputHelper.isMouseDown) {
            currentDiffY = y - grabStartY;
        } else {
            grabbedScreen = false;
        }
        resetScrolling();
        updateBarPosition();
    }

    private static void resetScrolling() {
        if (currentDiffY < scrollLowerBound) {
            currentDiffY = MathHelper.scrollSnapLerpSpeed(currentDiffY, scrollLowerBound);
        } else if (currentDiffY > scrollUpperBound) {
            currentDiffY = MathHelper.scrollSnapLerpSpeed(currentDiffY, scrollUpperBound);
        }
    }

    private static void scrolledUsingBar(float percent) {
        currentDiffY = MathHelper.valueFromPercentBetween(scrollLowerBound, scrollUpperBound, percent);
        updateBarPosition();
    }

    private static void updateBarPosition() {
        float percent = MathHelper.percentFromValueBetween(
                scrollLowerBound, scrollUpperBound, currentDiffY);
        scrollBar.parentScrolledToPercent(percent);
    }

    private static boolean shouldShowScrollBar() {
        return scrollUpperBound > 500.0F * Settings.scale;
    }

    // ---- 卡墙 ----

    private static void updateCards() {
        hoveredCard = null;
        List<AbstractCard> cards = allCardsArray();
        int row = 0;
        for (int i = 0; i < cards.size(); i++) {
            int col = i % COLS;
            if (col == 0 && i != 0) {
                row++;
            }
            AbstractCard card = cards.get(i);
            card.target_x = drawStartX + col * padX;
            card.target_y = drawStartY + currentDiffY - row * padY;
            card.update();
            card.updateHoverLogic();
            if (card.hb.hovered) {
                hoveredCard = card;
            }
        }
        if (justSorted) {
            // 原版 CardLibraryScreen.updateCards() 的做法：排完序直接落位，不滑
            for (AbstractCard card : cards) {
                card.current_x = card.target_x;
                card.current_y = card.target_y;
            }
            justSorted = false;
        }
    }

    private static void updateClicking() {
        if (hoveredCard == null) {
            clickStartedCard = null;
            return;
        }
        CardCrawlGame.cursor.changeType(GameCursor.CursorType.INSPECT);
        if (InputHelper.justClickedLeft) {
            clickStartedCard = hoveredCard;
        }
        if (InputHelper.justReleasedClickLeft
                && hoveredCard == clickStartedCard
                && !CardCrawlGame.isPopupOpen) {
            InputHelper.justReleasedClickLeft = false;
            CardCrawlGame.cardPopup.open(hoveredCard, allCards);
            clickStartedCard = null;
        }
    }

    /** 打开时把卡直接摆到位（原版 hideCards 的做法），不从别处飞过来。 */
    private static void snapCards() {
        List<AbstractCard> cards = allCardsArray();
        int row = 0;
        for (int i = 0; i < cards.size(); i++) {
            int col = i % COLS;
            if (col == 0 && i != 0) {
                row++;
            }
            AbstractCard card = cards.get(i);
            card.current_x = drawStartX + col * padX;
            card.current_y = drawStartY + currentDiffY - row * padY;
            card.target_x = card.current_x;
            card.target_y = card.current_y;
            card.drawScale = CARD_SCALE;
            card.targetDrawScale = CARD_SCALE;
            card.angle = 0.0F;
            card.targetAngle = 0.0F;
            card.transparency = 1.0F;
            card.targetTransparency = 1.0F;
        }
    }

    // ---- 排序条：排序按钮 + 看升级版 ----

    /**
     * 排序条跟着**第一张牌**走（原版 {@code CardLibSortHeader.updateScrollPositions()} 的算法：
     * {@code group.get(0).current_y + 230 * yScale}），所以滚动时它和卡墙一起进出屏幕。
     */
    private static float headerY() {
        List<AbstractCard> cards = allCardsArray();
        float topY = cards.isEmpty() ? drawStartY + currentDiffY : cards.get(0).current_y;
        return topY + HEADER_LIFT * Settings.yScale;
    }

    private static void updateHeader() {
        float y = headerY();
        for (SortHeaderButton button : sortButtons) {
            button.updateScrollPosition(y);
            button.update();
        }

        // 「看升级版」勾选框：和原版 ColorTabBar.update 一样，点一下切 isViewingUpgrade
        UPGRADE_HB.width = UPGRADE_HB_W * Settings.xScale;
        UPGRADE_HB.height = UPGRADE_HB_H * Settings.scale;
        UPGRADE_HB.move(UPGRADE_HB_X * Settings.xScale, y);
        UPGRADE_HB.update();
        if (UPGRADE_HB.hovered && InputHelper.justClickedLeft) {
            UPGRADE_HB.clickStarted = true;
        }
        if (UPGRADE_HB.clicked) {
            UPGRADE_HB.clicked = false;
            SingleCardViewPopup.isViewingUpgrade = !SingleCardViewPopup.isViewingUpgrade;
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.2F);
        }
    }

    /** 和原版 {@code CardLibSortHeader.didChangeOrder} 一致：先按种类排，再让未解锁的排到最后。 */
    private static void didChangeOrder(SortHeaderButton button, boolean ascending) {
        int kind = SORT_TYPE;
        for (int i = 0; i < sortButtons.length; i++) {
            if (sortButtons[i] == button) {
                kind = sortKinds[i];
            }
        }
        sortKind = kind;
        sortCards(ascending);
        allCards.sortByStatus(false);
        justSorted = true;
        for (int i = 0; i < sortButtons.length; i++) {
            sortButtons[i].setActive(sortKinds[i] == sortKind);
        }
    }

    private static void sortCards(boolean ascending) {
        switch (sortKind) {
            case SORT_RARITY:
                allCards.sortByRarity(ascending);
                break;
            case SORT_COST:
                allCards.sortByCost(ascending);
                break;
            case SORT_NAME:
                allCards.sortAlphabetically(ascending);
                break;
            default:
                allCards.sortByType(ascending);
                break;
        }
    }

    // ---- 绘制（由补丁在 AbstractDungeon.render 之后、光标之前调用） ----

    /** 顶栏那块「典」牌：收起时是入口，打开时是出口（金色高亮）。 */
    public static void renderIcon(SpriteBatch sb) {
        if (!canUseLibrary()) {
            return;
        }
        if (iconTexture == null) {
            iconTexture = ImageMaster.loadImage("zixiu/images/ui/dictionary.png");
        }
        if (iconTexture == null) {
            return;
        }

        float s = Settings.scale;
        // 原版顶栏图标的画法是 X - 32 + 32 * scale（未缩放的 32），不是按 hitbox 居中；
        // 窗口不是 1920 宽时这两者会差 32 * (1 - scale) 像素，不照抄就会比旁边三个图标偏高。
        float x = ICON_HB.cX - ICON_SIZE / 2.0F;
        float y = ICON_HB.cY - ICON_SIZE / 2.0F;

        sb.setColor(Color.WHITE);
        drawIcon(sb, x, y, s);

        // 悬停 / 开着图鉴时叠一层加色的白光，和原版顶栏图标一样
        if (ICON_HB.hovered || wantOpen) {
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            sb.setColor(1.0F, 1.0F, 1.0F, 0.25F);
            drawIcon(sb, x, y, s);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        sb.setColor(Color.WHITE);

        // 和原版顶栏按钮同款、同位置的悬浮说明（黄字标题 + 正文）
        if (ICON_HB.hovered && !CardCrawlGame.isPopupOpen) {
            TipHelper.renderGenericTip(1550.0F * Settings.scale,
                    Settings.HEIGHT - 120.0F * Settings.scale,
                    "字典",
                    "查看你角色的卡牌图鉴（" + allCardsArray().size() + "）");
        }
    }

    private static void drawIcon(SpriteBatch sb, float x, float y, float s) {
        sb.draw(iconTexture, x, y, ICON_SIZE / 2.0F, ICON_SIZE / 2.0F,
                ICON_SIZE, ICON_SIZE, s, s, iconAngle, 0, 0, 128, 128, false, false);
    }

    /** 图鉴内容：背景交给原版的 blackScreen，这里只画排序条、牌和滚动条。 */
    public static void renderPage(SpriteBatch sb) {
        if (!wantOpen || sortButtons == null) {
            return;
        }
        updateLayout();
        renderHeader(sb);

        allCards.renderInLibrary(sb);
        if (hoveredCard != null) {
            hoveredCard.renderHoverShadow(sb);
            hoveredCard.renderInLibrary(sb);
        }
        allCards.renderTip(sb);

        if (shouldShowScrollBar()) {
            scrollBar.render(sb);
        }
    }

    private static void renderHeader(SpriteBatch sb) {
        float y = headerY();
        // 原版 CardLibSortHeader.render() 也会在画之前重算一次纵向位置
        for (SortHeaderButton button : sortButtons) {
            button.updateScrollPosition(y);
        }

        // 原版图鉴的条子是用角色色染 COLOR_TAB_BAR；字修这边用主色（深蓝灰），和卡框同一套色
        sb.setColor(BAR_COLOR);
        sb.draw(ImageMaster.COLOR_TAB_BAR,
                Settings.WIDTH / 2.0F - BAR_W * Settings.scale / 2.0F,
                y - BAR_H * Settings.scale / 2.0F,
                BAR_W * Settings.scale, BAR_H * Settings.scale);
        sb.setColor(Color.WHITE);

        for (SortHeaderButton button : sortButtons) {
            button.render(sb);
        }
        renderViewUpgrade(sb, y);
    }

    /** 照抄 ColorTabBar.renderViewUpgrade：右边对齐的「看升级版」+ 左边的勾选框。 */
    private static void renderViewUpgrade(SpriteBatch sb, float y) {
        String label = CardLibraryScreen.TEXT[7];
        Color color = UPGRADE_HB.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR;
        FontHelper.renderFontRightAligned(sb, FontHelper.topPanelInfoFont, label,
                UPGRADE_LABEL_X * Settings.xScale, y, color);

        Texture box = SingleCardViewPopup.isViewingUpgrade
                ? ImageMaster.COLOR_TAB_BOX_TICKED
                : ImageMaster.COLOR_TAB_BOX_UNTICKED;
        float textWidth = FontHelper.getSmartWidth(
                FontHelper.topPanelInfoFont, label, 9999.0F, 0.0F);
        sb.setColor(color);
        sb.draw(box,
                UPGRADE_BOX_X * Settings.xScale - textWidth - 24.0F * Settings.scale,
                y - 24.0F * Settings.scale,
                48.0F * Settings.scale, 48.0F * Settings.scale);
        sb.setColor(Color.WHITE);
    }

    // ---- 布局 / 数据 ----

    private static void updateLayout() {
        drawStartY = Settings.HEIGHT * 0.66F;
        float w = AbstractCard.IMG_WIDTH * CARD_SCALE;
        padX = w + Settings.CARD_VIEW_PAD_X;
        padY = AbstractCard.IMG_HEIGHT * CARD_SCALE + Settings.CARD_VIEW_PAD_Y;
        drawStartX = (Settings.WIDTH - COLS * w - (COLS - 1) * Settings.CARD_VIEW_PAD_X) / 2.0F
                + w / 2.0F;
        scrollLowerBound = -Settings.DEFAULT_SCROLL_LIMIT;
    }

    private static void updateIconHb() {
        // 和原版顶栏那排按钮对齐：地图 / 牌组 / 设置分别占 WIDTH-222、WIDTH-148、WIDTH-74 起的 64 宽格子，
        // 这块「典」放在地图左边下一格（再减 74），即 WIDTH-296 起。
        float slot = (ICON_SIZE + 10.0F) * Settings.scale;
        ICON_HB.width = ICON_SIZE * Settings.scale;
        ICON_HB.height = ICON_SIZE * Settings.scale;
        ICON_HB.move(Settings.WIDTH - slot * 4.0F + ICON_SIZE * Settings.scale / 2.0F,
                Settings.HEIGHT - ICON_SIZE * Settings.scale / 2.0F);
        ICON_HB.update();
    }

    /**
     * 原版顶栏按钮的抖动：悬停时歪 15°，对应界面开着时按 {@code sin(t*4)*15} 来回摇。
     * 图鉴开着就等于「点了这个按钮」，所以这里是同款持续晃动。
     */
    private static void updateIconAngle() {
        float target;
        if (wantOpen) {
            iconRotateTimer += Gdx.graphics.getDeltaTime() * 4.0F;
            target = (float) Math.sin(iconRotateTimer) * 15.0F;
        } else if (ICON_HB.hovered) {
            target = 15.0F;
        } else {
            target = 0.0F;
        }
        iconAngle = MathHelper.angleLerpSnap(iconAngle, target);
    }

    private static void calculateScrollBounds() {
        int size = allCardsArray().size();
        if (size > COLS * 2) {
            int rows = size / COLS - 2;
            if (size % COLS != 0) {
                rows++;
            }
            scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT + rows * padY;
        } else {
            scrollUpperBound = Settings.DEFAULT_SCROLL_LIMIT;
        }
    }

    private static void buildWidgets() {
        scrollBar = new ScrollBar(BAR_LISTENER);
        scrollBar.move(0.0F, -30.0F * Settings.scale);

        // 原版图鉴的四个按钮：稀有度 / 类型 / 耗能 / A-Z（A-Z 可以在原版设置里关掉）
        sortKinds = Settings.removeAtoZSort
                ? new int[]{SORT_RARITY, SORT_TYPE, SORT_COST}
                : new int[]{SORT_RARITY, SORT_TYPE, SORT_COST, SORT_NAME};
        sortButtons = new SortHeaderButton[sortKinds.length];
        for (int i = 0; i < sortKinds.length; i++) {
            sortButtons[i] = new SortHeaderButton(sortLabel(sortKinds[i]), 0.0F, 0.0F, SORT_LISTENER);
            // 原版图鉴的位置：START_X 起步，每个按钮间隔 SPACE_X
            sortButtons[i].hb.move(
                    CardLibSortHeader.START_X + i * CardLibSortHeader.SPACE_X,
                    sortButtons[i].hb.cY);
            sortButtons[i].setActive(sortKinds[i] == sortKind);
        }
    }

    /** 按钮文案直接用原版本地化里的那几条，和原版图鉴的排序条一致。 */
    private static String sortLabel(int kind) {
        switch (kind) {
            case SORT_RARITY:
                return CardLibSortHeader.TEXT[0];
            case SORT_COST:
                return CardLibSortHeader.TEXT[3];
            case SORT_NAME:
                return CardLibSortHeader.TEXT[2];
            default:
                return CardLibSortHeader.TEXT[1];
        }
    }

    private static List<AbstractCard> allCardsArray() {
        if (allCards == null) {
            buildCards();
        }
        return allCards.group;
    }

    private static void buildCards() {
        allCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (Map.Entry<String, AbstractCard> entry : CardLibrary.cards.entrySet()) {
            AbstractCard card = entry.getValue();
            if (card != null && card.color == ZixiuEnums.ZIXIU_COLOR) {
                allCards.group.add(card);
            }
        }
        sortCards(true);
    }
}
