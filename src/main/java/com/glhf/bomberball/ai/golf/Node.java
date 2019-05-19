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

    public Node(GameState originalState, List<Action> turn, GameState state, int depth, Node lastNode){
        this.lastNode=lastNode;
        this.depth=depth;
        this.turn=turn;
        GameState newState = originalState.clone();
        for (Action a : turn) {
            originalState.apply(a);
        }
        this.state = newState;
        score = firstAI.heuristique(newState);
    }

    public boolean equal(Node node){
        //TODO
        return false;
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
}
