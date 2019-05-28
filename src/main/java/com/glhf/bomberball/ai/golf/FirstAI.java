package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.utils.Directions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FirstAI extends AbstractAI{


    private LinkedList<Node> OPEN = new LinkedList<Node>();
    private static  Node firstNode;
    private static  Node nextNode=null;
    private static LinkedList<Node> CLOSE = new LinkedList<Node>();


    public FirstAI(GameConfig config, String player_skin, int playerId) {
        super(config,"elf_m","FirstAi",playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) {
        System.out.println("Le joueur FirstIA joue ...");
        double score;
        if(nextNode != null){
            firstNode = nextNode;
        }else{
            firstNode = new Node(gameState);
        }
        OPEN.push(firstNode);
        Node tmpNode;


        System.out.println("A la recherche du meilleur coup");
        while (! OPEN.isEmpty()){
            tmpNode = OPEN.pop();
            System.out.println("Examen de "+ tmpNode.getAction());
            score = calculScore(tmpNode);
            System.out.println("Examen de "+ tmpNode.getAction() + " score associé : "+ score);
            if(tmpNode.update(score)){ // est vraie si les valeurs de alpha et beta ce sont croisé, on peut supprimer les autres fils.
                Node nodeTofind=tmpNode.getFather();
                for (Node nodeIt: OPEN) {
                    if (nodeIt.getFather() == nodeTofind) OPEN.remove(nodeIt);
                }
                nextNode = firstNode.getBestSon();
                System.out.println("On mémorise l'action : " + nextNode.getAction());
                this.setMemorizedAction(nextNode.getAction());
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
        List<Action> listAction = node.getState().getAllPossibleActions();
        for (Action a : listAction) {
            //System.out.println("Ajout de " + a);
            OPEN.addLast(new Node(a, node));
        }
    }

    /**
     * A method used to calcul the score, return an heuristique if it's not the end, return the utility otherwise
     * @param n the node to evaluate
     * @return a score between -1 and 1
     */
    private double calculScore(Node n){
        if(isTerminal(n.getState())){
            System.out.println("Utilité action " + n.getAction());
            return utilite(n.getState());
        }else {
            remplirOpen(n);
            CLOSE.push(n);
            return heuristique(n);
        }

    }

    public double heuristique(Node n) {
        double res = nbCaisseDetruite(n, 0.03);
        if(!n.isMax()) res= - res;
        return res;
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
    public static double nbCaisseDetruite (Node n, double poids){
        double score = 0;
        Maze m = n.getState().getMaze();
        for (int i=0;i<m.getWidth();i++){          //on parcourt l'ensemble des cases du labyrinthe
            for (int j=0;j<m.getHeight();j++){
                boolean cond = false;
                int it = 0;
//                System.out.println(i + " "+ j);
                ArrayList<GameObject> objects = m.getCellAt(i,j).getGameObjects();
                while (!cond && it<objects.size()){
                    if (objects.get(it) instanceof Bomb){    //verifier si la case contient une bombe
                        cond = true;
                    }else it++;
                }
                if (cond){  //si elle contient une bombe    //si la case contient une bombe
                    int range = n.getState().getCurrentPlayer().getBombRange(); //on recupere la range de la bombe
                    //HAUT
                    cond = false;
                    int c=1;
                    while (c<range && !cond && j+c<m.getHeight()) {
                        objects = m.getCellAt(i,j+c).getGameObjects();
                        c++;
                        it = 0;
                        while (!cond && it<objects.size()){
                            if (objects.get(it) instanceof DestructibleWall){    //verifier si la case contient une bombe
                                cond = true;
                            }else it++;
                        }
                    }
                    if (cond){score=score+poids;};
                    //BAS
                    cond = false;
                    c=1;
                    while (c<range && !cond && j-c>=0) {
                        objects = m.getCellAt(i,j-c).getGameObjects();
                        c++;
                        it = 0;
                        while (!cond && it<objects.size()){
                            if (objects.get(it) instanceof DestructibleWall){    //verifier si la case contient une bombe
                                cond = true;
                            }else{it++;}
                        }
                    }
                    if (cond){score=score+poids;}
                    //DROITE
                    cond = false;
                    c=1;
                    while (c<range && !cond && i+c<m.getWidth()) {
                        objects = m.getCellAt(i+c,j).getGameObjects();
                        c++;
                        it = 0;
                        while (!cond && it<objects.size()){
                            if (objects.get(it) instanceof DestructibleWall){    //verifier si la case contient une bombe
                                cond = true;
                            }else it++;
                        }
                    }
                    if (cond){score=score+poids;};
                    //GAUCHE
                    cond = false;
                    c=1;
                    while (c<range && !cond && i-c>=0) {
                        objects = m.getCellAt(i-c,j).getGameObjects();
                        c++;
                        it = 0;
                        while (!cond && it<objects.size()){
                            if (objects.get(it) instanceof DestructibleWall){    //verifier si la case contient une bombe
                                cond = true;
                            }else it++;
                        }
                    }
                    if (cond){score=score+poids;}
                }
            }
        }
        return Math.min(score,0.99);
    }


    /**
     * Say if the state correspond to the end of a game
     * @param n State
     * @return true if it's the end of the game
     */
    private boolean isTerminal (GameState n){
        //TODO : Il faut faire attention à ce que notre joueur soit toujours en vie
//        boolean weAreDead = true;
//        for (Player p: n.getPlayers()) {
//          // If our player isn't in the remaining player it's the end for him ...
//            if(p.getPlayerId() == this.getPlayerId()) {
//                weAreDead = !(p.isAlive());
//            }
//        }
//        if(weAreDead) System.out.println("On est mort! :D");
        return n.gameIsOver();
    }

    /**
     * Return the score of a leaf
     * @param n Sate
     * @return the score : 1 is a win for you, -1 for your opponent  and 0 for a draw
     */
    private double utilite (GameState n){
        if(n.getWinner().getPlayerId() == this.getPlayerId()) return 1;

        else return -1;
    }
}
