package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.utils.Action;

import java.util.List;

public class firstAi extends AbstractAI{


    public firstAi(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"FirstAi",playerId);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Action choosedAction(GameState gameState) { // TODO : à changer copy paste de ramdomAI
        System.out.println("Le joueur IA joue ...");
        List<Action> listAction= gameState.getAllPossibleActions();
        System.out.println("Récupération de  : " + listAction.size() + " actions possible");
        GameState tmpState;
        int tmpScore;
        int alpha = -1; int beta = 1;
        System.out.println("A la recherche du meilleur coup");
        // A loop to find the best action
        for (Action tmpAction : listAction) {
            if(alpha > beta) return this.getMemorizedAction();
            System.out.println("Test de l'action" + tmpAction);
            tmpState = gameState.clone();
            tmpState.apply(tmpAction);
            tmpState.setCurrentPlayerId(tmpState.getCurrentPlayerId()+1); // we switch player manually
            //System.out.println("Nouveau joueur : j" +tmpState.getIdJoueurCourant() );
            tmpScore = alphaBeta(tmpState, alpha, beta, 1);
            //System.out.println("Score associé à cette action : "+tmpScore);
            if(tmpScore>alpha) {
                alpha=tmpScore;
                this.setMemorizedAction(tmpAction);
            }
        }
        System.out.println("L'ia a pu terminer son calcul ! " );
        return this.getMemorizedAction();
    }
    /**
     * Function to have the best score on a branch
     * @param n A state of the game
     * @return the score : 1 is a win for you, -1 for your opponent  and 0 for a draw
     */
    private int alphaBeta(GameState n, int alpha, int beta, int rg){
        //System.out.println("Utilisation de alphaBeta !");
        if(rg>5) {
            return heuristique(n);
        } else {
            GameState tmpState;
            List<Action> listAction= n.getAllPossibleActions();
            int tmpScore;

            if(isTerminal(n)) {
                return utilite(n);
            }else {
                if (n.getCurrentPlayerId() == this.getPlayerId()) { // Max is playing !
                    //System.out.println("Max is playing");
                    // Trying every action possible
                    for (Action tmpAction : listAction) {
                        if (alpha > beta) return alpha;

                        //System.out.println("Test de l'action x : " + tmpAction.getX() + " y  : " + tmpAction.getY());
                        tmpState = n.clone();
                        tmpState.apply(tmpAction);
                        tmpState.setCurrentPlayerId(tmpState.getCurrentPlayerId()+1); // we switch player manually
                        //System.out.println("Nouveau joueur : j" +tmpState.getIdJoueurCourant() );
                        tmpScore = alphaBeta(tmpState, alpha, beta, rg+1);
                        // Update alpha
                        if (tmpScore > alpha) alpha = tmpScore;


                    }
                    return alpha;
                } else {  // Min is playing !
                    //System.out.println("Min is playing");
                    for (Action tmpAction : listAction) {
                        if (alpha > beta) return beta;
                        //System.out.println("Test de l'action x : " + tmpAction.getX() + " y  : " + tmpAction.getY());
                        tmpState = n.clone();
                        tmpState.apply(tmpAction);
                        tmpState.setCurrentPlayerId(tmpState.getCurrentPlayerId()+1); // we switch player manually
                        //System.out.println("Nouveau joueur : j" +tmpState.getIdJoueurCourant() );
                        tmpScore = alphaBeta(tmpState, alpha, beta, rg+1);
                        // Update beta
                        if (tmpScore < beta) beta = tmpScore;


                    }
                    return beta;
                }
            }
        }
    }

    private int heuristique(GameState n) {
        //TODO
        return 0;
    }

    /**
     * Say if the state correspond to the end of a game
     * @param n State
     * @return if it's the end of the game
     */
    private boolean isTerminal (GameState n){
        //TODO
        return false;
    }

    /**
     * Return the score of a leaf
     * @param n Sate
     * @return the score : 1 is a win for you, -1 for your opponent  and 0 for a draw
     */
    private int utilite (GameState n){
        //TODO
        return 0;
    }
}
