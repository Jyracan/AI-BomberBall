package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.LinkedList;
import java.util.List;

public class FirstAI extends AbstractAI{


    private LinkedList<Node> OPEN = new LinkedList<Node>();
    private LinkedList<Node> CLOSE = new LinkedList<Node>();


    public FirstAI(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"FirstAi",playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) {
        System.out.println("Le joueur FirstIA joue ...");
        double score;
        Node firstNode = new Node(gameState);
        OPEN.push(firstNode);
        Node tmpNode;


        System.out.println("A la recherche du meilleur coup");
        while (! OPEN.isEmpty()){
            tmpNode = OPEN.pop();
            score = calculScore(tmpNode);
            if(tmpNode.update(score)){ // is true if
                // Les valeurs de alpha et beta ce sont croisé, on peut supprimer les autres fils.
                Node nodeTofind=tmpNode.getFather();
                for(int i=0; i<OPEN.size();i++) {
                    if (OPEN.get(i).getFather() == nodeTofind) OPEN.remove(i);
                }
            }
            remplirOpen(tmpNode);
            CLOSE.push(tmpNode);
            this.setMemorizedAction(firstNode.getBestSon().getAction());
        }

        System.out.println("L'ia a pu terminer son calcul ! " );
        return this.getMemorizedAction();
    }


    /**
     * Method to fill Open with the node we are currently exploring
     * @param node The node we previously evaluate
     */
    private void remplirOpen(Node node){
        List<Action> listAction = node.getState().getAllPossibleActions();
        for (Action a : listAction) {
            if(a == Action.ENDTURN){
                OPEN.push(new Node(a, node)); // Indiquer le next player ?
            }
        }
    }

    /**
     * A method used to calcul the score, return an heuristique if it's not the end, return the utility otherwise
     * @param n the node to evaluate
     * @return a score between -1 and 1
     */
    private double calculScore(Node n){
        if(isTerminal(n.getState())){
            return utilite(n.getState());
        }else return heuristique(n);
    }

    public double heuristique(Node n) {

        return 0;
    }

    /**
     * Say if the state correspond to the end of a game
     * @param n State
     * @return true if it's the end of the game
     */
    private boolean isTerminal (GameState n){
        boolean jCourantMort =true;
        for (Player p: n.getPlayers()) {
            // If our player isn't in the remaining player it's the end for him ...
            if(p.getCurrentPlayerId() == this.getPlayerId()) jCourantMort =false;
        }
        return n.isOver() || !jCourantMort;
    }

    /**
     * Return the score of a leaf
     * @param n Sate
     * @return the score : 1 is a win for you, -1 for your opponent  and 0 for a draw
     */
    private double utilite (GameState n){
        List<Player> lp = n.getPlayers();
        if(lp.size() == 0) return 0;
        else{
            for (Player p: lp) {
                if(p.getCurrentPlayerId() == this.getPlayerId()) return 1;
            }
        }
        return -1;
    }
}
