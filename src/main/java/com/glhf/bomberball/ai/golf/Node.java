package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.utils.Action;
import org.lwjgl.Sys;

public class Node {
    private Action action;
    private GameState state;
    private double alpha, beta;
    private Node father;
    private Node bestSon;
    private boolean max;
    private int idPlayer;


    /**
     * Use this constr to create the first node
     * @param state
     */
    public Node(GameState state){
        this.father =null;
        this.action =null;
        this.state = state.clone();
        this.idPlayer = state.getCurrentPlayerId();
        this.alpha = -1;
        this.beta = 1;
        this.max=true;
    }

    /**
     * Use this node to create node using a father
     * @param action A list of action
     * @param father
     */
    public Node(Action action, Node father){
        this.father = father;
        this.action = action;
        GameState newState = father.getState().clone();
        max = (newState.getCurrentPlayerId() == this.idPlayer);
        newState.apply(action);
        this.state = newState;
        this.alpha = father.alpha;
        this.beta = father.beta;
    }

    public boolean equals(Node node){
        boolean res = true;
        if(this.action != node.getAction()) res = false;
        if(node.getState().getMaze().toString().equals(this.getState().getMaze().toString())) res = true; // On utilise le fait que la méthode toString renvoit un JSON pour comparer les deux labyrinthes

        return res;
    }

    /**
     * Function to update the node
     * @param score
     * @return majPossible boolean to indicate if it's possible to update the other node
     */
    public boolean update(double score){
        boolean majPossible = false;
        if(this.isMax()) if(score > this.alpha){
            if(this.getFather() == null) System.out.println("Action : " + this.getBestSon().getAction() + "score =" + score + " alpha : " +alpha);
            this.alpha = score;
            majPossible = true;
        }
        else if(score < this.beta) this.beta = score;
//        if(alpha>=beta) {
//            majPossible = true;
//        }
        if(majPossible && this.father != null){
                if(this.father.getAlpha() < this.alpha) {
                    this.father.setBestSon(this); // On dit à notre père qu'on est son meilleur fils
                    this.father.update(score);
                }
        }
        return majPossible;
    }

    /**
     * @return true if this is a node Max
     */
    public boolean isMax(){
        return this.max;
    }

    public GameState getState() {
        return state;
    }

    public Action getAction() {
        return action;
    }

    public Node getFather() {
        return father;
    }

    public Node getBestSon(){
        return bestSon;
    }

    public void setBestSon(Node bestSon) {
//        if(this.getFather() == null) System.out.println("New best action " + this.getBestSon().getAction() + " score associé : " + bestSon.alpha);
        this.bestSon = bestSon;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }
}
