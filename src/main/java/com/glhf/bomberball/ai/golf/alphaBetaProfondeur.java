package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;

import java.util.ArrayList;
import java.util.List;

public class alphaBetaProfondeur extends AbstractAI{
    private final double BOX_DESTROYED = 0.1;
    private final double BONUS_BOX_DESTROYED = 0.2;
    private final double BONUS_TAKEN = 0.3;
    private final double BONUS_DESTROYED = -0.3;
    private final double PLAYER_KILLED = 1;
    private final double WALL = -2;

    private int maxDepth;
    private int INITIAL_DEPTH = 9;

    public alphaBetaProfondeur(GameConfig config, String player_skin, int playerId) {
        super(config,"wogol","alphaBetaProfondeur",playerId);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Action choosedAction(GameState gameState) {
        System.out.println("Le joueur IA joue ...");
        List<Action> listAction= gameState.getAllPossibleActions();
        System.out.println("Récupération de  : " + listAction.size() + " actions possible");
        GameState tmpState;
        double tmpScore;
        double alpha = -1, beta = 1;
        maxDepth = INITIAL_DEPTH;

        System.out.println("A la recherche du meilleur coup");
        // A loop to find the best action
        while(alpha != 1) {
            maxDepth ++;
            System.out.println("Meilleur profondeur étudié : " + maxDepth);
            for (Action tmpAction : listAction) {
                if (alpha >= beta) return this.getMemorizedAction();
                //System.out.println("Test de l'action x : " + tmpAction.getX() + " y  : " + tmpAction.getY());
                tmpState = gameState.clone();
                tmpState.apply(tmpAction);
                //System.out.println("Nouveau joueur : j" +tmpState.getIdJoueurCourant() );
                tmpScore = alphaBeta(tmpState, alpha, beta, 1);
                //System.out.println("Score associé à cette action : "+tmpScore);
                if (tmpScore > alpha) {
                    alpha = tmpScore;
                    this.setMemorizedAction(tmpAction);
                    System.out.println("Oh ! un meilleur coup a été trouvé " + tmpAction + " score associé : " + tmpScore);
                }
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
    private double alphaBeta(GameState n, double alpha, double beta, int profondeur){
        //System.out.println("Utilisation de alphaBeta !");

        GameState tmpState;
        double tmpScore;

        if(isTerminal(n)) {
            return utilite(n);
        }else {
            List<Action> listAction= n.getAllPossibleActions();
            if (n.getCurrentPlayerId() == this.getPlayerId()) { // Max is playing !
                //System.out.println("Max is playing");
                // Trying every action possible
                for (Action tmpAction : listAction) {
                    if (alpha > beta) return alpha;
                    if(profondeur>maxDepth) {
                        tmpScore = heuristique(n);
                    }else{
                        //System.out.println("Test de l'action x : " + tmpAction.getX() + " y  : " + tmpAction.getY());
                        tmpState = n.clone();
                        tmpState.apply(tmpAction);
                        //System.out.println("Nouveau joueur : j" +tmpState.getIdJoueurCourant() );

                        tmpScore = alphaBeta(tmpState, alpha, beta, profondeur+1);
                    }
                    // Update alpha
                    if (tmpScore > alpha) alpha = tmpScore;


                }
                return alpha;
            } else {  // Min is playing !
                //System.out.println("Min is playing");
                for (Action tmpAction : listAction) {
                    if (alpha > beta) return beta;
                    //System.out.println("Test de l'action x : " + tmpAction.getX() + " y  : " + tmpAction.getY());
                    if(profondeur>maxDepth) {
                        tmpScore = heuristique(n);
                    }else{
                        tmpState = n.clone();
                        tmpState.apply(tmpAction);
                        //System.out.println("Nouveau joueur : j" +tmpState.getIdJoueurCourant() );
                        tmpScore = alphaBeta(tmpState, alpha, beta, profondeur+1);
                    }
                    // Update beta
                    if (tmpScore < beta) beta = tmpScore;
                }
                return beta;
            }
        }

    }

    private double heuristique(GameState n) {
        double score=0;
        score += scoreDueToBomb(n);
        if(n.getCurrentPlayerId() != this.getPlayerId()) score= - score;
        return score;
    }

    /**
     * @param n Node
     * @return a float between 0 and 1 proportional to the number of crates destroyed
     */
    private double scoreDueToBomb(GameState n){
        double score = 0;   // Le score que nous allons renvoyer
        double cellScore;    // Le score associé à une cellule qui se trouve dans la range d'une bombe
        Maze maze = n.getMaze();
        // TODO : es-ce vraiment necessaire de parcourir tout le labyrinthe peut être un peu trop gros ...
        for (int i=0;i<maze.getWidth();i++){          //on parcourt l'ensemble des cases du labyrinthe
            for (int j=0;j<maze.getHeight();j++){
                boolean bombFind = false;
//                System.out.println(i + " "+ j);
                ArrayList<GameObject> objects = maze.getCellAt(i,j).getGameObjects();
                // Boucle permettant de vérifier si il y a une bombe sur la case
                for(int iterateur = 0; iterateur<objects.size() && !bombFind; iterateur++) bombFind = (objects.get(iterateur) instanceof Bomb);
                if (bombFind){ //si la case contient une bombe
                    int range = n.getCurrentPlayer().getBombRange(); //on recupere la range de la bombe
                    //HAUT
                    cellScore =0;
                    for(int c = 1;(c<range && cellScore==0 && j+c<maze.getHeight()); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i,j+c), n);
                        if(cellScore != this.WALL) score += cellScore;
                    }
                    cellScore =0;
                    //BAS
                    for(int c = 1;(c<range && cellScore==0 && j-c<maze.getHeight()); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i,j-c), n);
                        if(cellScore != this.WALL) score += cellScore;
                    }
                    cellScore =0;
                    //DROITE
                    for(int c = 1;(c<range && cellScore==0 && i+c<maze.getHeight()); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i+c,j), n);
                        if(cellScore != this.WALL) score += cellScore;
                    }
                    cellScore =0;
                    //GAUCHE
                    for(int c = 1;(c<range && cellScore==0 && i-c>=0); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i-c,j), n);
                        if(cellScore != this.WALL) score += cellScore;
                    }
                }
            }
        }
        return score;
    }

    private double scoreOfTheCell(Cell cell, GameState n){
        double score = 0;
        ArrayList<GameObject> objects = cell.getGameObjects();
        for (GameObject object : objects) { // Checking every item on the cell
            if(object instanceof IndestructibleWall){
                score = this.WALL;
            }else if (object instanceof BonusWall) {
                score += this.BONUS_BOX_DESTROYED;
            } else if (object instanceof DestructibleWall) {
                score += this.BOX_DESTROYED;
            } else if (object instanceof Bonus) {
                score += this.BONUS_DESTROYED;
            }else if (object instanceof Player) {
                if(n.getCurrentPlayer().getX() == object.getX() && n.getCurrentPlayer().getY() == object.getY()){
                    score -= this.PLAYER_KILLED;
                }else{
                    score = this.PLAYER_KILLED;
                }
            }
        }
        return score;
    }

    /**
     * Say if the state correspond to the end of a game
     * @param n State
     * @return true if it's the end of the game
     */
    private boolean isTerminal (GameState n){
        return n.gameIsOver();
    }

    /**
     * Return the score of a leaf
     * @param n Sate
     * @return the score : 1 is a win for you, -1 for your opponent  and 0 for a draw
     */
    private double utilite (GameState n){
        if(n.getWinner() == null) return 0; // Cas d'égalité
        else if(n.getWinner().getPlayerId() == this.getPlayerId()) return 1;
        else return -1;
    }
}
