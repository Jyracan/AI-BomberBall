package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.LinkedList;

public class FirstAI extends AbstractAI{


    private LinkedList<Node> OPEN;

    private final double BOX_DESTROYED = 0.1;
    private final double BONUS_BOX_DESTROYED = 0.2;
    private final double BONUS_TAKEN = 0.3;
    private final double BONUS_DESTROYED = -0.3;
    private final double PLAYER_KILLED = 1;



    public FirstAI(GameConfig config, String player_skin, int playerId) {
        super(config,"necromancer","FirstAi",playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) {
        OPEN = new LinkedList<Node>();
        System.out.println("Le joueur FirstIA joue ...");
        double score;
        Node firstNode;
        firstNode = new Node(gameState, this.getPlayerId());
        remplirOpen(firstNode);
        Node tmpNode;

        System.out.println("A la recherche du meilleur coup");
        while (! OPEN.isEmpty()){
            tmpNode = OPEN.pop();
            score = calculScore(tmpNode);
            System.out.println("Action étudié : " + tmpNode.getAction() + " score associé : " + score);
            if(tmpNode.update(score)){ // est vraie si il est interressant de faire une maj
                this.setMemorizedAction(firstNode.getBestSon().getAction());
            }
        }
        System.out.println("L'ia a pu terminer son calcul ! " );
        return this.getMemorizedAction();
    }


    /**
     * Method to fill Open with the node we are currently exploring
     * @param node The node we previously evaluate
     */
    private void remplirOpen(Node node){
        for (Action a : node.getState().getAllPossibleActions()) OPEN.addLast(new Node(a, node));
    }

    /**
     * A method used to calcul the score, return an heuristique if it's not the end, otherwise return the utility
     * @param n the node to evaluate
     * @return a score between -1 and 1
     */
    private double calculScore(Node n){
        if(isTerminal(n.getState())){
            return utilite(n.getState());
        }else {
            remplirOpen(n);
            return heuristique(n);
        }

    }

    private double heuristique(Node n) {
        double score;
        score = scoreDueToBomb(n);
        if(!n.isMax()) score= - score;
        return score;
    }

    /**
     * @param n Node
     * @return a float between 0 and 1 proportional to the number of crates destroyed
     */
    private double scoreDueToBomb(Node n){
        double score = 0;   // Le score que nous allons renvoyer
        double cellScore = 0;    // Le score associé à une cellule qui se trouve dans la range d'une bombe
        Maze maze = n.getState().getMaze();
        // TODO : es-ce vraiment necessaire de parcourir tout le labyrinthe peut être un peu trop gros ...
        for (int i=0;i<maze.getWidth();i++){          //on parcourt l'ensemble des cases du labyrinthe
            for (int j=0;j<maze.getHeight();j++){
                boolean bombFind = false;
//                System.out.println(i + " "+ j);
                ArrayList<GameObject> objects = maze.getCellAt(i,j).getGameObjects();
                // Boucle permettant de vérifier si il y a une bombe sur la case
                for(int iterateur = 0; iterateur<objects.size() && !bombFind; iterateur++) bombFind = (objects.get(iterateur) instanceof Bomb);
                if (bombFind){ //si la case contient une bombe
                    int range = n.getState().getCurrentPlayer().getBombRange(); //on recupere la range de la bombe
                    //HAUT
                    for(int c = 1;(c<range && cellScore==0 && j+c<maze.getHeight()); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i,j+c));
                        if(cellScore != -5) score += cellScore;
                    }
                    //BAS
                    for(int c = 1;(c<range && cellScore==0 && j-c<maze.getHeight()); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i,j-c));
                        if(cellScore != -5) score += cellScore;
                    }
                    //DROITE
                    for(int c = 1;(c<range && cellScore==0 && i+c<maze.getHeight()); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i+c,j));
                        if(cellScore != -5) score += cellScore;
                    }
                    //GAUCHE
                    for(int c = 1;(c<range && cellScore==0 && i-c>=0); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i-c,j));
                        if(cellScore != -5) score += cellScore;
                    }
                }
            }
        }
        return score;
    }

    private double scoreOfTheCell(Cell cell){
        double score = 0;
        ArrayList<GameObject> objects = cell.getGameObjects();
        for (GameObject object : objects) { // Checking every item on the cell
            if(object instanceof IndestructibleWall){
                score = -5;
            }else if (object instanceof BonusWall) {
                score += this.BONUS_BOX_DESTROYED;
            } else if (object instanceof DestructibleWall) {
                score += this.BOX_DESTROYED;
            } else if (object instanceof Bonus) {
                score += this.BONUS_DESTROYED;
            } else if (object instanceof Player) {
                score = - this.PLAYER_KILLED;
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
        //TODO : Il faut faire attention à ce que notre joueur soit toujours en vie
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
