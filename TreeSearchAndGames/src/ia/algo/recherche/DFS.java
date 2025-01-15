package ia.algo.recherche;

import java.util.ArrayList;

import ia.framework.common.State;
import ia.framework.common.Action;
import ia.framework.common.Misc;
import ia.framework.common.ArgParse;

import ia.framework.recherche.TreeSearch;
import ia.framework.recherche.SearchProblem;
import ia.framework.recherche.SearchNode;

public class DFS extends TreeSearch {
    public DFS(SearchProblem prob, State initial_state) {
        super(prob, initial_state);
    }

    @Override
    public boolean solve() {
        // On commence à létat initial
        SearchNode node = SearchNode.makeRootSearchNode(initial_state);
        State state = node.getState();

        if (ArgParse.DEBUG) {
            System.out.print("[\n" + state);
        }

        // Noeuds à explorer
        ArrayList<SearchNode> frontier = new ArrayList<SearchNode>();
        frontier.add(node);

        // Noeuds déjà explorés
        ArrayList<SearchNode> explored = new ArrayList<SearchNode>();

        while (!frontier.isEmpty()) {
            // On récupère le premier noeud de la frontière
            node = frontier.remove(0);
            state = node.getState();

            if (ArgParse.DEBUG) {
                System.out.print(" + " + node.getAction() + "] -> [" + state);
            }

            // Si on a atteint l'état final
            if (problem.isGoalState(state)) {
                end_node = node;
                if (ArgParse.DEBUG) {
                    System.out.println("]");
                }
                return true;
            }

            // On ajoute le noeud courant aux noeuds explorés
            explored.add(node);

            // Les actions possibles depuis cette état
            ArrayList<Action> actions = problem.getActions(state);

            if (ArgParse.DEBUG) {
                System.out.print("Actions Possibles : {");
                System.out.println(Misc.collection2string(actions, ','));
            }

            // Pour chaque action possible
            for (Action a : actions) {
                // On crée un nouveau noeud enfant
                SearchNode child = SearchNode.makeChildSearchNode(problem, node, a);

                // Si le noeud enfant n'a pas déjà été exploré
                if (!explored.contains(child) && !frontier.contains(child)) {
                    // On ajoute le noeud enfant à la frontière (début de la liste)
                    frontier.add(0, child);
                }
            }
        }

        if (ArgParse.DEBUG) {
            System.out.println("]");
        }

        return false;
    }
}