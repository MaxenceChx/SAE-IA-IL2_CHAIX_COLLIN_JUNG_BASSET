package ia.algo.recherche;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

import ia.framework.common.State;
import ia.framework.common.Action;
import ia.framework.common.ArgParse;

import ia.framework.recherche.TreeSearch;
import ia.framework.recherche.SearchProblem;
import ia.framework.recherche.HasHeuristic;
import ia.framework.recherche.SearchNode;

public class GFS extends TreeSearch {
    public GFS(SearchProblem prob, State initial_state) {
        super(prob, initial_state);

        // Vérifie si l'état initial implémente l'interface HasHeuristic
        if (!(initial_state instanceof HasHeuristic)) {
            throw new IllegalArgumentException("Les états doivent implémenter HasHeuristic pour utiliser GFS.");
        }
    }

    @Override
    public boolean solve() {
        // Création d'une file de priorité triée par la valeur heuristique
        PriorityQueue<SearchNode> frontier = new PriorityQueue<>(Comparator.comparingDouble(
            node -> ((HasHeuristic) node.getState()).getHeuristic()
        ));

        // Création du nœud racine
        SearchNode root = SearchNode.makeRootSearchNode(initial_state);
        frontier.add(root);

        // Liste des nœuds explorés
        ArrayList<SearchNode> explored = new ArrayList<>();

        while (!frontier.isEmpty()) {
            // Récupère le nœud avec la plus petite valeur heuristique
            SearchNode node = frontier.poll();
            State state = node.getState();

            if (ArgParse.DEBUG) {
                double heuristicValue = ((HasHeuristic) state).getHeuristic();
                System.out.println("Exploring node with state: " + state + ", heuristic: " + heuristicValue);
            }

            // Vérifie si l'état est l'état objectif
            if (problem.isGoalState(state)) {
                end_node = node;
                if (ArgParse.DEBUG) {
                    System.out.println("Goal reached.");
                }
                return true;
            }

            // Marque le nœud comme exploré
            explored.add(node);

            // Explore les actions possibles à partir de cet état
            ArrayList<Action> actions = problem.getActions(state);
            for (Action action : actions) {
                // Crée un nouveau nœud enfant
                SearchNode child = SearchNode.makeChildSearchNode(problem, node, action);

                // Ajoute le nœud enfant à la frontière s'il n'a pas déjà été exploré
                if (!explored.contains(child) && !frontier.contains(child)) {
                    frontier.add(child);
                }
            }
        }

        return false; // Aucun chemin trouvé
    }
}