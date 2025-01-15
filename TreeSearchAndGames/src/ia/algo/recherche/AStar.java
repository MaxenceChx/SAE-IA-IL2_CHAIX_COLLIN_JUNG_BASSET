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

public class AStar extends TreeSearch {
    public AStar(SearchProblem prob, State initial_state) {
        super(prob, initial_state);

        // Vérifie si l'état initial implémente l'interface HasHeuristic
        if (!(initial_state instanceof HasHeuristic)) {
            throw new IllegalArgumentException("Les états doivent implémenter HasHeuristic pour utiliser A*.");
        }
    }

    @Override
    public boolean solve() {
        // Création d'une file de priorité triée par f(n) = g(n) + h(n)
        PriorityQueue<SearchNode> frontier = new PriorityQueue<>(Comparator.comparingDouble(
            node -> node.getCost() + ((HasHeuristic) node.getState()).getHeuristic()
        ));

        // Création du nœud racine
        SearchNode root = SearchNode.makeRootSearchNode(initial_state);
        frontier.add(root);

        // Liste des nœuds explorés
        ArrayList<SearchNode> explored = new ArrayList<>();

        while (!frontier.isEmpty()) {
            // Récupère le nœud avec le plus faible coût total f(n)
            SearchNode node = frontier.poll();
            State state = node.getState();

            if (ArgParse.DEBUG) {
                double g = node.getCost();
                double h = ((HasHeuristic) state).getHeuristic();
                System.out.println("Exploring node with state: " + state +
                                   ", g(n): " + g + ", h(n): " + h + ", f(n): " + (g + h));
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

                // Si le nœud enfant n'a pas déjà été exploré ou ajouté à la frontière
                if (!explored.contains(child) && !frontier.contains(child)) {
                    frontier.add(child);
                } else if (frontier.contains(child)) {
                    // Met à jour le coût du nœud dans la frontière si un meilleur chemin est trouvé
                    for (SearchNode frontierNode : frontier) {
                        if (frontierNode.equals(child) && child.getCost() < frontierNode.getCost()) {
                            frontier.remove(frontierNode);
                            frontier.add(child);
                            break;
                        }
                    }
                }
            }
        }

        return false; // Aucun chemin trouvé
    }
}

