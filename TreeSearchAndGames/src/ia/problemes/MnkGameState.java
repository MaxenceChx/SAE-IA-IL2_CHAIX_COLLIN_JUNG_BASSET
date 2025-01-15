package ia.problemes;

import java.util.Arrays;
import java.util.ArrayList;

import ia.framework.common.Action;
import ia.framework.common.State;
import ia.framework.common.Misc;
import ia.framework.jeux.GameState;

/**
 * Représente un état d'un jeu générique m,n,k Game 
 */

public class MnkGameState extends AbstractMnkGameState {

   
    /**
     * Construire une grille vide de la bonne taille
     *
     * @param r nombre de lignes
     * @param c nombre de colonnes 
     */
    public MnkGameState(int r, int c, int s) {
        super(r,c,s);
    }

    public MnkGameState cloneState() {
        MnkGameState new_s = new MnkGameState(this.rows, this.cols, this.streak);
        new_s.board = this.board.clone();
        new_s.player_to_move = player_to_move;
        new_s.game_value = game_value;
        if(this.last_action != null)
            new_s.last_action = this.last_action.clone();
        for (Pair p: this.winning_move)
            new_s.winning_move.add(p.clone());
        return new_s;
	}
    /**
     * Un fonction d'évaluation pour cet état du jeu. 
     * Permet de comparer différents états dans le cas ou on ne  
     * peut pas développer tout l'arbre. Le joueur 1 (X) choisira les
     * actions qui mènent au état de valeur maximal, Le joueur 2 (O)
     * choisira les valeurs minimal.
     * 
     * Cette fonction dépend du jeu.
     * 
     * @return la valeur du jeux
     **/
    protected double evaluationFunction() {
        int pos_x = this.possibleLines(X);
        int pos_o = this.possibleLines(O);
        return pos_x - pos_o;
    }

    // Compte le nombre total de lignes possibles pour un joueur
    private int possibleLines(int player) {
        return possibleVerticalLines(player) +
               possibleHorizontalLines(player) +
               possibleDiagonalLines(player);
    }

    // Compte les lignes verticales possibles
    private int possibleVerticalLines(int player) {
        int res = 0;
        for(int c = 0; c < this.cols; c++) {
            for(int r = 0; r <= this.rows - this.streak; r++) {
                int counter = 0;
                for(int k = 0; k < this.streak; k++) {
                    if(this.getValueAt(r+k, c) == this.otherPlayer(player)) {
                        counter = 0;
                        break;
                    }
                    else if(this.getValueAt(r+k, c) == player || this.getValueAt(r+k, c) == EMPTY) {
                        counter++;
                    }
                }
                if(counter == this.streak) {
                    res++;
                }
            }
        }
        return res;
    }

    // Compte les lignes horizontales possibles
    private int possibleHorizontalLines(int player) {
        int res = 0;
        for(int r = 0; r < this.rows; r++) {
            for(int c = 0; c <= this.cols - this.streak; c++) {
                int counter = 0;
                for(int k = 0; k < this.streak; k++) {
                    if(this.getValueAt(r, c+k) == this.otherPlayer(player)) {
                        counter = 0;
                        break;
                    }
                    else if(this.getValueAt(r, c+k) == player || this.getValueAt(r, c+k) == EMPTY) {
                        counter++;
                    }
                }
                if(counter == this.streak) {
                    res++;
                }
            }
        }
        return res;
    }

    // Compte les lignes diagonales possibles (dans les deux directions)
    private int possibleDiagonalLines(int player) {
        int res = 0;
        
        // Diagonales descendantes (45 degrés)
        for(int c = 0; c <= this.cols - this.streak; c++) {
            for(int r = 0; r <= this.rows - this.streak; r++) {
                int counter = 0;
                for(int k = 0; k < this.streak; k++) {
                    if(this.getValueAt(r+k, c+k) == this.otherPlayer(player)) {
                        counter = 0;
                        break;
                    }
                    else if(this.getValueAt(r+k, c+k) == player || this.getValueAt(r+k, c+k) == EMPTY) {
                        counter++;
                    }
                }
                if(counter == this.streak) {
                    res++;
                }
            }
        }
        
        // Diagonales montantes (-45 degrés)
        for(int c = 0; c <= this.cols - this.streak; c++) {
            for(int r = this.streak - 1; r < this.rows; r++) {
                int counter = 0;
                for(int k = 0; k < this.streak; k++) {
                    if(this.getValueAt(r-k, c+k) == this.otherPlayer(player)) {
                        counter = 0;
                        break;
                    }
                    else if(this.getValueAt(r-k, c+k) == player || this.getValueAt(r-k, c+k) == EMPTY) {
                        counter++;
                    }
                }
                if(counter == this.streak) {
                    res++;
                }
            }
        }
        
        return res;
    }
}
