package zixiu.core;

import basemod.interfaces.OnPlayerTurnStartSubscriber;

public class TurnReset implements OnPlayerTurnStartSubscriber {

    @Override
    public void receiveOnPlayerTurnStart() {
        FusionTracker.clear();
    }
}
