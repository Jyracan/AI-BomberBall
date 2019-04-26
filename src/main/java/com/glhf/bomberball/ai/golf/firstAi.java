package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.utils.Action;

import java.util.List;
import java.util.Random;

public class firstAi extends AbstractAI{


    public firstAi(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"FirstAi",playerId);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Action choosedAction(GameState gameState) { // TODO : à changer copy paste de ramdomAI
        Random rand = new Random();
        List<Action> possibleActions= gameState.getAllPossibleActions();
        int actionIndex=rand.nextInt(possibleActions.size());
        System.out.println(possibleActions.get(actionIndex));
        return  possibleActions.get(actionIndex);
    }

}
