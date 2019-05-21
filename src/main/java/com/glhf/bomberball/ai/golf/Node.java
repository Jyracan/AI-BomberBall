package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.utils.Action;

import java.util.List;

public class Node {
    private List<Action> turn;
    private GameState state;
    private int depth;
    private double score;
    private double alpha, beta;
    private Node lastNode;

    public Node(GameState originalState, List<Action> turn, int depth, Node lastNode){
        this.lastNode=lastNode;
        this.depth=depth;
        this.turn=turn;

        GameState newState = originalState.clone();

        for (Action a : turn) {
            originalState.apply(a);
        }
        this.state = newState;
        score = firstAI.heuristique(this);
    }

    public boolean equals(Node node){
        // TODO : Tester égalité de l'état impossible sans modifier la classe état ...
        boolean res = false;

        if(node.getTurn().size() != this.getTurn().size()) return false;

        int nbAction = node.getTurn().size();
        // Test to know if the turns are equals
        for (int i =0; i<nbAction || !res; i++) {
            if(node.getTurn().get(i) != this.getTurn().get(i)) res = true;
        }
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
        this.lastNode.update(score);
        return res;
    }

    /**
     * @return true if this is a node Max
     */
    public boolean isMax(){return depth%2 ==0;}

    public GameState getState() {
        return state;
    }

    public List<Action> getTurn() {
        return turn;
    }
    public double getScore() {
        return score;
    }
    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
    public Node getLastNode() {
        return lastNode;
    }

    public int getDepth() { return depth; }
}
