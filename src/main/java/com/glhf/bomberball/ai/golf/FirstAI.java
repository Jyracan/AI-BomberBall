package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;

import java.util.ArrayList;
import java.util.LinkedList;

public class FirstAI extends AbstractAI{


    private LinkedList<Node> OPEN;
    private static Action lastAction = null;

    private final double BOX_DESTROYED = 0.1;
    private final double BONUS_BOX_DESTROYED = 0.2;
    private final double BONUS_TAKEN = 0.3;
    private final double BONUS_DESTROYED = -0.5;
    private final double PLAYER_KILLED = 1;
    private final double WALL = -2;
    private final double BADMOVE = -2;
    private boolean flag_box_destroyed = false;

    public FirstAI(GameConfig config, String player_skin, int playerId) {
        super(config,"necromancer","FirstAi",playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) {
        OPEN = new LinkedList<Node>();
        System.out.println("Le joueur FirstIA joue ...");
        this.setMemorizedAction(Action.ENDTURN); // De base on finit notre tour pour éviter de jouer des actions aléatoire si l'o ne trouve pas mieux
        double score;
        Node firstNode;
        firstNode = new Node(gameState, this.getPlayerId());
        remplirOpen(firstNode);
        Node tmpNode;
        int bestDepth =0;
        System.out.println("A la recherche du meilleur coup");
        while (! OPEN.isEmpty()){
            tmpNode = OPEN.pop();
            if(tmpNode.getDepth() > bestDepth) {
                bestDepth = tmpNode.getDepth();
                System.out.println("Nouvelle meilleure profondeure " + bestDepth);
            }
            score = calculScore(tmpNode);
            //System.out.println("Action étudié : " + tmpNode.getAction() + " score associé : " + score);
            if (score != BADMOVE){
                if(tmpNode.update(score)){ // est vraie si il est interressant de faire une maj
                    if(firstNode.getBestSon() != null){
                        lastAction = firstNode.getBestSon().getAction();
    //                        System.out.println("Mise à jour de la nouvelle meilleure action : " + lastAction + " score associé : " + score );
                        this.setMemorizedAction(lastAction);
                    }
                }
                if( !isTerminal(tmpNode.getState())) {
                    //System.out.println("On remplis OPEN");
                    remplirOpen(tmpNode);
                }
            }//else System.out.println("Detection d'un mauvais coup " + tmpNode.getAction());
        }
        System.out.println("L'ia a pu terminer son calcul ! " );
        return this.getMemorizedAction();
    }


    /**
     * Method to fill Open with the node we are currently exploring
     * @param node The node we previously evaluate
     */
    private void remplirOpen(Node node){
        Action forbiden = forbiddenAction();
        for (Action a : node.getState().getAllPossibleActions()) {
            if(a != forbiden){
                OPEN.addLast(new Node(a, node));
            }
        }
    }

    /**
     * Empêche l'IA de revenir sur ses pas
     * @return Action that is the opposite to the last Action (if it is a Move, else return null)
     */
    public Action forbiddenAction (){
        if(lastAction !=null){
            switch (lastAction){
                case MOVE_DOWN:
                    return Action.MOVE_UP;
                case MOVE_UP:
                    return Action.MOVE_DOWN;
                case MOVE_RIGHT:
                    return Action.MOVE_LEFT;
                case MOVE_LEFT:
                    return Action.MOVE_RIGHT;
            }
        }
        return null;
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
            return heuristique(n);
        }

    }

    private double heuristique(Node n) {
        double score=0;
        score += scoreDueToBomb(n);
        if(score ==0 && aUtiliseUneBombe(n) ) score = BADMOVE;  //TODO : Mal implémenté fait buguer le player, il ne va plus tuer (décommenter l51 et l62)
        else {
            score += bonusGrabbed(n);
            double tmpScore = scoreOfTheArround(n);
            if(!flag_box_destroyed) {
                score += tmpScore;
            }
            if(!n.isMax()) score= - score;
        }
        if(inRangeOfOpponent(n) && n.getState().getCurrentPlayer().getMovesRemaining() == 0){
            score = BADMOVE;
        }
        return score;
    }

    /**
     * @return If the current player have used a bomb
     */
    private boolean aUtiliseUneBombe(Node n){
        return ((n.getState().getCurrentPlayer().bonus_bomb_number + n.getState().getCurrentPlayer().getNumberBombRemaining()) != 1);
    }

    /**
     * @param n Node
     * @return a float between 0 and 1 proportional to the number of bonus the IA has taken this turn
     **/
    private double bonusGrabbed(Node n){
        int nbBonus = 0;
        nbBonus += n.getState().getCurrentPlayer().bonus_moves;
        nbBonus += n.getState().getCurrentPlayer().bonus_bomb_number;
        nbBonus += n.getState().getCurrentPlayer().bonus_bomb_range;
        int oldNbBonus = 0;
        oldNbBonus += n.getFather().getState().getCurrentPlayer().bonus_moves;
        oldNbBonus += n.getFather().getState().getCurrentPlayer().bonus_bomb_number;
        oldNbBonus += n.getFather().getState().getCurrentPlayer().bonus_bomb_range;
        return (nbBonus - oldNbBonus) * this.BONUS_TAKEN;
    }


    /**
     * @param n Node
     * @return true if opponent can kill IA in his next turn, else false
     */
    public static boolean inRangeOfOpponent (Node n){
        int id = n.getState().getCurrentPlayerId();
        int idOpponent = 1 - id;
        Player ai = n.getState().getPlayers().get(id);
        Player opponent = n.getState().getPlayers().get(idOpponent);
        int range = opponent.getBombRange()+opponent.getNumberMoveRemaining();
        return possiblePath(n,ai.getCell(),opponent.getCell(),range);
    }

    /**
     * @param n Node
     * @return true if there is a walkable path between 2 cells, path must be shorter than range
     */
    public static boolean possiblePath (Node n, Cell a, Cell b, int range) {
        if (a.equals(b)) {
            return true;
        } else if (range == 0 || !a.isWalkable()) {
            return false;
        } else {
            int nbAdj = a.getAdjacentCellsInMaze().size();
            for (int i=0;i<nbAdj;i++) {
                if (possiblePath(n, a.getAdjacentCellsInMaze().get(i), b, range - 1)){
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @param n Node
     * @return a float between 0 and 1 proportional to the number of crates destroyed
     */
    private double scoreDueToBomb(Node n){
        double score = 0;   // Le score que nous allons renvoyer
        double cellScore;    // Le score associé à une cellule qui se trouve dans la range d'une bombe
        boolean bombFind;
        Maze maze = n.getState().getMaze();
        // TODO : es-ce vraiment necessaire de parcourir tout le labyrinthe peut être un peu trop gros ... Idée d'amélioration ?
        for (int i=0;i<maze.getWidth();i++){          //on parcourt l'ensemble des cases du labyrinthe
            for (int j=0;j<maze.getHeight();j++){
                bombFind = false;
//                System.out.println(i + " "+ j);
                ArrayList<GameObject> objects = maze.getCellAt(i,j).getGameObjects();
                // Boucle permettant de vérifier si il y a une bombe sur la case
                for(int iterateur = 0; iterateur<objects.size() && !bombFind; iterateur++) bombFind = (objects.get(iterateur) instanceof Bomb);
                if (bombFind){ //si la case contient une bombe
                    int range = n.getState().getCurrentPlayer().getBombRange(); //on recupere la range de la bombe
                    //HAUT
                    cellScore =0;
                    for(int c = 1;(c<range && cellScore==0 && j+c<maze.getHeight()); c++ ){
                        cellScore = scoreOfTheCell(maze.getCellAt(i,j+c), n);
                        if(cellScore != this.WALL) score += cellScore;
                    }
                    cellScore =0;
                    //BAS
                    for(int c = 1;(c<range && cellScore==0 && j-c>=0); c++ ){
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

    // TODO : Lui dire de se tenir à une case d'espace d'une caisse BONUS pour l'exploser sans perdre une action
    private double scoreOfTheArround(Node n){
        //flag_box_destroyed = false;
        ArrayList<Cell> adjacentCells;
        ArrayList<Cell> tmpCells;
        Maze maze = n.getState().getMaze();
        double score = 0;
        ArrayList<GameObject> gameObjects;
        adjacentCells = maze.getCellAt(n.getState().getCurrentPlayer().getX(), n.getState().getCurrentPlayer().getY() ).getAdjacentCellsInMaze();
        int size = adjacentCells.size();
        for (int i =0; i<size; i++) {
            if(! adjacentCells.get(i).hasInstanceOf(Wall.class)){
                tmpCells = adjacentCells.get(i).getAdjacentCellsInMaze();
                for (Cell tmpCell: tmpCells) {
                    if(! adjacentCells.contains(tmpCell)){
                        adjacentCells.add(tmpCell);
                    }
                }
            }
        }
        for (Cell cell: adjacentCells) {
            gameObjects = cell.getGameObjects();
            for (GameObject object:gameObjects) {
                if (object instanceof Bonus) {
                    score += this.BONUS_TAKEN / 2;
                }else if(object instanceof BonusWall ){
                    score += this.BONUS_BOX_DESTROYED / 2;
                    //flag_box_destroyed = true;
                }else if(object instanceof Player){
                    if(n.getState().getCurrentPlayer().getX() != object.getX() || n.getState().getCurrentPlayer().getY() != object.getY()){
                        score -= this.PLAYER_KILLED/2;
                    }
                }
            }
        }
        return score;
    }

    private double scoreOfTheCell(Cell cell, Node node){
        double score = 0;
        if(cell != null){
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
                    if(node.getState().getCurrentPlayer().getX() == object.getX() && node.getState().getCurrentPlayer().getY() == object.getY()){
                        score -= this.PLAYER_KILLED;
                    }else{
                        score = this.PLAYER_KILLED;
                    }
                }
            }
        }else{
            System.err.println("Attention case vide");
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
