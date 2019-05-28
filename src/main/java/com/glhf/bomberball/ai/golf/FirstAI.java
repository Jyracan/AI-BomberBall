package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.utils.Action;

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
        double res = 0.1;
        if(!n.isMax()) res= - res;
        return res;
    }

    /**
     * @param n Node
     * @return a float between 0 and 1 proportional to the number of crates destroyed
     */
    public static double nbCaisseDetruite (Node n){
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
                    while (c<range && !cond) {
                        objects = m.getCellAt(i,j+c).getGameObjects();
                        c++;
                        it = 0;
                        while (!cond && it<objects.size()){
                            if (objects.get(it) instanceof DestructibleWall){    //verifier si la case contient une bombe
                                cond = true;
                            }else it++;
                        }
                    }
                    if (cond){score=score+0.03;};
                    //BAS
                    cond = false;
                    c=1;
                    while (c<range && !cond) {
                        objects = m.getCellAt(i,j-c).getGameObjects();
                        c++;
                        it = 0;
                        while (!cond && it<objects.size()){
                            if (objects.get(it) instanceof DestructibleWall){    //verifier si la case contient une bombe
                                cond = true;
                            }else{it++;}
                        }
                    }
                    if (cond){score=score+0.03;}
                    //DROITE
                    cond = false;
                    c=1;
                    while (c<range && !cond) {
                        objects = m.getCellAt(i+c,j).getGameObjects();
                        c++;
                        it = 0;
                        while (!cond && it<objects.size()){
                            if (objects.get(it) instanceof DestructibleWall){    //verifier si la case contient une bombe
                                cond = true;
                            }else it++;
                        }
                    }
                    if (cond){score=score+0.03;};
                    //GAUCHE
                    cond = false;
                    c=1;
                    while (c<range && !cond) {
                        objects = m.getCellAt(i-c,j).getGameObjects();
                        c++;
                        it = 0;
                        while (!cond && it<objects.size()){
                            if (objects.get(it) instanceof DestructibleWall){    //verifier si la case contient une bombe
                                cond = true;
                            }else it++;
                        }
                    }
                    if (cond){score=score+0.03;}
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
