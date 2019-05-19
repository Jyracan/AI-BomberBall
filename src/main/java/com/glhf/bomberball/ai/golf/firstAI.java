package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;
import org.lwjgl.Sys;

import java.util.List;

public class firstAI extends AbstractAI{

    private static final int MAX_DEPTH = 2;

    public firstAI(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"FirstAi",playerId);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Action choosedAction(GameState gameState) { // TODO : à changer copy paste de ramdomAI
        System.out.println("Le joueur FirstIA joue ...");
        List<Action> listAction= gameState.getAllPossibleActions();
        System.out.println("Récupération de  : " + listAction.size() + " actions possible");
        System.out.println("Liste d\'ation ");
        for (Action a : listAction) {
            System.out.println(a);

        }
        GameState tmpState;
        int tmpScore;
        int alpha = -1; int beta = 1;
        System.out.println("A la recherche du meilleur coup");
        // A loop to find the best action
        for (Action tmpAction : listAction) {
            if(alpha > beta) return this.getMemorizedAction();
            System.out.println("Test de l'action " + tmpAction);
            tmpState = gameState.clone();
            tmpState.apply(tmpAction);
            //System.out.println("Nouveau joueur : j" +tmpState.getIdJoueurCourant() );
            tmpScore = alphaBeta(tmpState, alpha, beta, 1);
            //System.out.println("Score associé à cette action : "+tmpScore);
            if(tmpScore>alpha) {
                alpha=tmpScore;
                System.out.println("On a trouvé une meilleur action :" + tmpAction + " score associé : " + tmpScore);
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

            GameState tmpState;
            List<Action> listAction= n.getAllPossibleActions();
            int tmpScore;

            if(isTerminal(n)) {
                return utilite(n);
            }else {
                if (n.getCurrentPlayerId() == this.getPlayerId()) { // Max is playing !
                    // Trying every action possible
                    for (Action tmpAction : listAction) {
                        if (alpha > beta) return alpha;
                        if(rg>MAX_DEPTH) {
                            return heuristique(n);
                        } else {
                            tmpState = n.clone();
                            tmpState.apply(tmpAction);
                            tmpScore = alphaBeta(tmpState, alpha, beta, rg + 1);
                            // Update alpha
                            if (tmpScore > alpha) alpha = tmpScore;
                        }
                    }
                    return alpha;
                } else {  // Min is playing !
                    //System.out.println("Min is playing");
                    for (Action tmpAction : listAction) {
                        if (alpha > beta) return beta;
                        if(rg>MAX_DEPTH) {
                            return heuristique(n);
                        } else {
                            tmpState = n.clone();
                            tmpState.apply(tmpAction);
                            tmpState.setCurrentPlayerId(tmpState.getCurrentPlayerId() + 1); // we switch player manually
                            tmpScore = alphaBeta(tmpState, alpha, beta, rg + 1);
                            // Update beta
                            if (tmpScore < beta) beta = tmpScore;
                        }
                    }
                    return beta;
                }
            }
    }

    public static int heuristique(GameState n) {
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
