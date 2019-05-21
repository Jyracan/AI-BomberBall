package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.utils.Action;

public class Node {
    private Action action;
    private GameState state;
    private int depth;
    private double alpha, beta;
    private Node father;
    private Node bestSon;


    /**
     * Use this constr to create the first node
     * @param state
     */
    public Node(GameState state){
        this.father =null;
        this.depth =0;
        this.action =null;
        this.state = state.clone();
        this.alpha = -1;
        this.beta = 1;
    }

    /**
     * Use this node to create node using a father
     * @param action A list of action
     * @param father
     */
    public Node(Action action, Node father){
        this.father = father;
        this.depth = father.getDepth() + 1;
        this.action = action;
        GameState newState = father.getState().clone();
        newState.apply(action);
        this.state = newState;
        this.alpha = father.alpha;
        this.beta = father.beta;
    }

    public boolean equals(Node node){
        boolean res = true;
        if(this.action != node.getAction()) res = false;

        if(node.getState().getMaze().toString() == this.getState().getMaze().toString()) res = true; // On utilise le fait que la méthode toString renvoit un JSON pour comparer les deux labyrinthes

        return res;
    }

    /**
     * Function to update the node
     * @param score
     */
    public boolean update(double score){
        boolean res = false;
        if(this.isMax()) this.alpha = score;
        else this.beta = score;
        if(alpha>beta) res = true;
        if(this.father != null){
            this.father.update(score);
            this.father.setBestSon(this); // On dit à notre père qu'on est son meilleur fils
        }

        return res;
    }

    /**
     * @return true if this is a node Max
     */
    public boolean isMax(){return depth %2 ==0;}

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
        this.bestSon = bestSon;
    }

    public int getDepth() { return depth; }
}
