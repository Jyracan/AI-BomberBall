package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.LinkedList;
import java.util.List;

public class firstAI extends AbstractAI{


    private static final int MAX_DEPTH = 2;
    private LinkedList<Node> OPEN = new LinkedList<Node>();
    private LinkedList<Node> CLOSE = new LinkedList<Node>();


    public firstAI(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"FirstAi",playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) { // TODO : à changer copy paste de ramdomAI
        System.out.println("Le joueur FirstIA joue ...");

        double alpha = -1, beta = 1, score;
        Node tmpNode;
        System.out.println("Création des noeuds possbile");


        System.out.println("A la recherche du meilleur coup");
        while (! OPEN.isEmpty()){
            tmpNode = OPEN.pop();
            score = heuristique(tmpNode);
            if(tmpNode.update(score)){
                // Les valeurs de alpha et beta ce sont croisé, on peut supprimer les autres fils.
                Node nodeTofind=tmpNode.getLastNode();
                for(int i=0; i<OPEN.size();i++){
                    if(OPEN.get(i).getLastNode() == nodeTofind) OPEN.remove(i);
                }
            }
            CLOSE.push(tmpNode);
        }

        System.out.println("L'ia a pu terminer son calcul ! " );
        return this.getMemorizedAction();
    }

    /**
     * Function to have the best score on a branch
     * @param n A state of the game
     * @return the score : 1 is a win for you, -1 for your opponent  and 0 for a draw
     */
    private int alphaBeta(Node n, int alpha, int beta, int rg){
        return 0;
    }

    private void remplirOpen(Node node){
        //TODO : Remplir OPEN
    }


    public static int heuristique(Node n) {
        //TODO
        return 0;
    }

    /**
     * Say if the state correspond to the end of a game
     * @param n State
     * @return true if it's the end of the game
     */
    private boolean isTerminal (GameState n){
        boolean bool =false;
        for (Player p: n.getPlayers()) {
            if(p.getCurrentPlayerId() == this.getPlayerId()) bool =true;
        }
        if(n.isOver() || bool) return true;
        return false;
    }

    /**
     * Return the score of a leaf
     * @param n Sate
     * @return the score : 1 is a win for you, -1 for your opponent  and 0 for a draw
     */
    private int utilite (GameState n){
        List<Player> lp = n.getPlayers();
        if(lp.size() ==0) return 0;
        else{
            for (Player p: lp) {
                if(p.getCurrentPlayerId() == this.getPlayerId()) return 1;
            }
        }
        return -1;
    }
}
