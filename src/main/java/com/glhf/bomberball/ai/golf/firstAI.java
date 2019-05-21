package com.glhf.bomberball.ai.golf;

import com.glhf.bomberball.ai.AbstractAI;
import com.glhf.bomberball.ai.GameState;
import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.LinkedList;
import java.util.List;

public class firstAI extends AbstractAI{


    private LinkedList<Node> OPEN = new LinkedList<Node>();
    private LinkedList<Node> CLOSE = new LinkedList<Node>();


    public firstAI(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"FirstAi",playerId);
    }

    @Override
    public Action choosedAction(GameState gameState) { // TODO : à changer copy paste de ramdomAI
        System.out.println("Le joueur FirstIA joue ...");
        double score;
        Node firstNode = new Node(gameState);
        OPEN.push(firstNode);
        Node tmpNode;

        System.out.println("Création des noeuds possbile");
        System.out.println("A la recherche du meilleur coup");
        while (! OPEN.isEmpty()){
            tmpNode = OPEN.pop();
            score = heuristique(tmpNode);
            if(tmpNode.update(score)){ // is true if
                // Les valeurs de alpha et beta ce sont croisé, on peut supprimer les autres fils.
                Node nodeTofind=tmpNode.getFather();
                for(int i=0; i<OPEN.size();i++){
                    if(OPEN.get(i).getFather() == nodeTofind) OPEN.remove(i);
                }
            }
            CLOSE.push(tmpNode);
            this.setMemorizedAction(firstNode.getBestSon().getAction());
        }

        System.out.println("L'ia a pu terminer son calcul ! " );
        return this.getMemorizedAction();
    }


    private void remplirOpen(Node node){
        List<Action> listAction = node.getState().getAllPossibleActions();
        GameState tmpState;
        for (Action a : listAction) {
            if(a == Action.ENDTURN){
                OPEN.push(new Node(a, node)); // Indiquer le next player ?
            }

        }
    }
    /*private void remplirOpen(Node node) {
        GameState state = node.getState();
        List<Action> l = state.getAllPossibleActions();
        l.remove(Action.ENDTURN);
        List<Action> tmpl = new ArrayList<Action>();
        tmpl.add(l.get(0));
        Action lastAction = l.get(l.size()-1);
        System.out.println(l.toString())

        while(tmpl.size() != l.size()+2){;
            int j = 0;
            while(j <= tmpl.size() && tmpl.get(j) == lastAction){
                tmpl.remove(j);
                tmpl.add(j, l.get(0));
                j++;
            }
            if(j == tmpl.size()){
                tmpl.add(l.get(0));
            } else {
                tmpl.add(j, l.get(l.indexOf(tmpl.get(j)) + 1));
                tmpl.remove(j + 1);
            }
            tmpl.add(Action.ENDTURN);
            Node tmp = new Node(node.getState(), l, node.getDepth(), node.getLastNode());
            System.out.println(tmp.toString());
            OPEN.add(tmp);
        }
    }*/

    private double calculScore(Node n){
        if(isTerminal(n.getState())){
            return utilite(n.getState());
        }else return heuristique(n);
    }

    public double heuristique(Node n) {

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
    private double utilite (GameState n){
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
