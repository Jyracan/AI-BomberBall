package com.glhf.bomberball.ai;

import java.util.Vector;

import com.glhf.bomberball.ai.golf.firstAI;
import com.glhf.bomberball.config.GameMultiConfig;
import com.glhf.bomberball.maze.cell.Cell;


public class FactoryMethod {

    Vector<String> vectAi;


    public FactoryMethod()
    {
        this.vectAi = new Vector<String>();
        this.vectAi.add("RandomAI");
        this.vectAi.add("RandomAI");

    }


    public AbstractAI getAI(String name, GameMultiConfig configMultiPlayers, String player_skin, Cell cell,int playerId) {
        try
        {
            switch (name) {
                case "RandomAI":
                    AbstractAI player=new RandomAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(player);
                    return player;

                case "VanillaAI":
                    AbstractAI player1=new VanillaAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(player1);
                    return player1;

                case "firstAI":
                    AbstractAI player2=new firstAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(player2);
                    return player2;

                default:
                    AbstractAI playerDefault=new RandomAI(configMultiPlayers,player_skin,playerId);
                    cell.addGameObject(playerDefault);
                    return playerDefault;


            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        return null;
    }








    public Vector<String> getVectAi()
    {
        return vectAi;
    }




}
