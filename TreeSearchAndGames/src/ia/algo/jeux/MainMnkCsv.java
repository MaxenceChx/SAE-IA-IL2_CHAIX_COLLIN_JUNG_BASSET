package ia.algo.jeux;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ia.framework.jeux.Game;
import ia.framework.jeux.GameState;
import ia.framework.jeux.Player;
import ia.problemes.MnkGame;
import ia.algo.jeux.MinMaxPlayer;
import ia.algo.jeux.AlphaBetaPlayer;
import ia.algo.jeux.BasicHeuristic;
import ia.algo.jeux.AdvancedHeuristic;
import ia.algo.jeux.HeuristicFunction;

public class MainMnkCsv {

    // Structure pour accumuler les résultats détaillés pour une combinaison (config;algorithm;depth)
    static class Result {
        double totalTimeMs = 0;   // somme du temps de tous les coups (en ms)
        int totalMoves = 0;       // nombre total de coups joués
        long totalStates = 0;     // somme des états visités
        double scoreSum = 0;      // somme des scores (1 victoire, 0.5 match nul, 0 défaite)
        int games = 0;            // nombre de parties jouées
        int wins = 0;             // nombre de victoires (score == 1)
    }

    // Structure pour stocker les métriques d'un match (une partie)
    static class MatchMetrics {
        double timeP1 = 0;   // temps total pour joueur 1 en ms
        double timeP2 = 0;   // temps total pour joueur 2 en ms
        int movesP1 = 0;     // coups joués par joueur 1
        int movesP2 = 0;     // coups joués par joueur 2
        long statesP1 = 0;   // états visités par joueur 1
        long statesP2 = 0;   // états visités par joueur 2
        double scoreP1 = 0;  // score de joueur 1 (1 victoire, 0.5 match nul, 0 défaite)
        double scoreP2 = 0;  // score de joueur 2
    }

    // Interface fonctionnelle pour fabriquer un joueur avec une profondeur donnée
    interface PlayerFactory {
        Player create(Game game, boolean isP1, int depth);
    }

    // Classe pour représenter un type de joueur (algorithme)
    static class PlayerType {
        String baseName; // ex. "MinMax" ou "AlphaBeta"
        String variant;  // "Advanced" ou "Basic"
        PlayerFactory factory;
        public PlayerType(String baseName, String variant, PlayerFactory factory) {
            this.baseName = baseName;
            this.variant = variant;
            this.factory = factory;
        }
        // Renvoie le nom complet, par ex. "MinMax (Advanced)"
        public String getName() {
            return baseName + " (" + variant + ")";
        }
    }

    public static void main(String[] args) {
        // Configurations MNK à tester : {rows, cols, streak}
        int[][] configs = {
                {3, 3, 3},
                {5, 5, 4},
                {5, 5, 5},
                {7, 7, 5},
                {9, 9, 5}
        };

        // Profondeurs à tester (de 1 à 7)
        int[] depths = {1, 2, 3, 4, 5, 6, 7};

        // Définition des types de joueurs à tester (excluant Random et Human)
        List<PlayerType> playerTypes = new ArrayList<>();
        playerTypes.add(new PlayerType("Random", "", (game, isP1, depth) -> new RandomPlayer(game, isP1)));
        playerTypes.add(new PlayerType("MinMax", "Advanced", (game, isP1, depth) -> new MinMaxPlayer(game, isP1, depth, new AdvancedHeuristic())));
        playerTypes.add(new PlayerType("MinMax", "Basic", (game, isP1, depth) -> new MinMaxPlayer(game, isP1, depth, new BasicHeuristic())));
        playerTypes.add(new PlayerType("AlphaBeta", "Advanced", (game, isP1, depth) -> new AlphaBetaPlayer(game, isP1, depth, new AdvancedHeuristic())));
        playerTypes.add(new PlayerType("AlphaBeta", "Basic", (game, isP1, depth) -> new AlphaBetaPlayer(game, isP1, depth, new BasicHeuristic())));

        // Map pour accumuler les résultats détaillés : clé = "config;algorithm;depth"
        Map<String, Result> detailedResults = new HashMap<>();

        // Nombre de matchs par paire
        int gamesPerPair = 1;

        // Simulation : pour chaque configuration, pour chaque profondeur, pour chaque paire d'algorithmes distincts
        for (int[] config : configs) {

            System.out.println("Configuration : " + config[0] + "x" + config[1] + ", streak=" + config[2]);
            int rows = config[0], cols = config[1], streak = config[2];
            String configName = rows + "x" + cols + ", streak=" + streak;
            for (int d : depths) {
                for (int i = 0; i < playerTypes.size(); i++) {
                    for (int j = 0; j < playerTypes.size(); j++) {
                        if (i == j) continue;
                        PlayerType pt1 = playerTypes.get(i);
                        PlayerType pt2 = playerTypes.get(j);
                        for (int g = 0; g < gamesPerPair; g++) {
                            Game game = new MnkGame(rows, cols, streak);
                            Player p1, p2;
                            if (g % 2 == 0) {
                                p1 = pt1.factory.create(game, true, d);
                                p2 = pt2.factory.create(game, false, d);
                            } else {
                                p1 = pt2.factory.create(game, true, d);
                                p2 = pt1.factory.create(game, false, d);
                            }
                            MatchMetrics metrics = simulateMatch(game, p1, p2);
                            // Accumuler les résultats pour chaque algorithme en fonction du match (inversion des rôles)
                            if (g % 2 == 0) {
                                accumulateDetailed(detailedResults, configName, pt1.getName(), d, metrics.timeP1, metrics.movesP1, metrics.statesP1, metrics.scoreP1, metrics, pt1);
                                accumulateDetailed(detailedResults, configName, pt2.getName(), d, metrics.timeP2, metrics.movesP2, metrics.statesP2, metrics.scoreP2, metrics, pt2);
                            } else {
                                accumulateDetailed(detailedResults, configName, pt1.getName(), d, metrics.timeP2, metrics.movesP2, metrics.statesP2, metrics.scoreP2, metrics, pt1);
                                accumulateDetailed(detailedResults, configName, pt2.getName(), d, metrics.timeP1, metrics.movesP1, metrics.statesP1, metrics.scoreP1, metrics, pt2);
                            }
                        }
                    }
                }
            }
        }

        // Générer le fichier XLSX
        String xlsxFile = "tournament_results.xlsx";
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Tournament Results");

            // Styles pour l'en-tête fusionné et les titres de colonnes
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(BorderStyle.THIN);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);

            int rowNum = 0;
            // Regrouper les résultats par configuration et profondeur
            SortedMap<String, SortedMap<Integer, Map<String, Result>>> grouped = new TreeMap<>();
            for (String key : detailedResults.keySet()) {
                String[] parts = key.split(";");
                String config = parts[0];
                int depth = Integer.parseInt(parts[2]);
                String algo = parts[1];
                grouped.computeIfAbsent(config, k -> new TreeMap<>())
                        .computeIfAbsent(depth, k -> new HashMap<>())
                        .put(algo, detailedResults.get(key));
            }

            // Pour chaque configuration
            for (String config : grouped.keySet()) {
                SortedMap<Integer, Map<String, Result>> depthsMap = grouped.get(config);
                for (Integer d : depthsMap.keySet()) {
                    // Ligne d'en-tête fusionnée
                    XSSFRow headerRow = sheet.createRow(rowNum++);
                    Cell headerCell = headerRow.createCell(0);
                    headerCell.setCellValue("MNK: " + config + "  |  Depth: " + d);
                    headerCell.setCellStyle(headerStyle);
                    // Fusionner les cellules de la ligne d'en-tête (sur 7 colonnes, de 0 à 6)
                    sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 0, 6));

                    // Ligne des titres de colonnes
                    XSSFRow titleRow = sheet.createRow(rowNum++);
                    String[] titles = {"Algorithm", "AvgTime(sec)", "TotalStates", "AvgStatesPerGame", "ScoreSum", "Games", "WinPercentage"};
                    for (int col = 0; col < titles.length; col++) {
                        Cell cell = titleRow.createCell(col);
                        cell.setCellValue(titles[col]);
                        cell.setCellStyle(titleStyle);
                    }

                    // Lignes de données pour chaque algorithme
                    Map<String, Result> algoResults = depthsMap.get(d);
                    for (String algo : algoResults.keySet()) {
                        Result r = algoResults.get(algo);
                        double avgTimeSec = (r.totalMoves > 0) ? (r.totalTimeMs / r.totalMoves) / 1000.0 : 0;
                        double avgStatesPerGame = (r.games > 0) ? ((double) r.totalStates / r.games) : 0;
                        double winPct = (r.games > 0) ? (r.wins * 100.0 / r.games) : 0;

                        XSSFRow dataRow = sheet.createRow(rowNum++);
                        dataRow.createCell(0).setCellValue(algo);
                        dataRow.createCell(1).setCellValue(avgTimeSec);
                        dataRow.createCell(2).setCellValue(r.totalStates);
                        dataRow.createCell(3).setCellValue(avgStatesPerGame);
                        dataRow.createCell(4).setCellValue(r.scoreSum);
                        dataRow.createCell(5).setCellValue(r.games);
                        dataRow.createCell(6).setCellValue(winPct);
                    }

                    // Ligne vide pour séparer les tableaux
                    rowNum++;
                }
            }

            // Ajuster la largeur des colonnes
            for (int col = 0; col < 7; col++) {
                sheet.autoSizeColumn(col);
            }

            try (FileOutputStream fos = new FileOutputStream(xlsxFile)) {
                workbook.write(fos);
            }
            System.out.println("Résultats écrits dans le fichier XLSX : " + xlsxFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Accumule les résultats détaillés pour une clé donnée : "config;algorithm;depth"
    private static void accumulateDetailed(Map<String, Result> results, String config, String algo, int depth,
                                           double timeMs, int moves, long states, double score, MatchMetrics m, PlayerType pt) {
        String key = config + ";" + algo + ";" + depth;
        Result r = results.getOrDefault(key, new Result());
        r.totalTimeMs += timeMs;
        r.totalMoves += moves;
        r.totalStates += states;
        r.scoreSum += score;
        if (score == 1) {
            r.wins++;
        }
        r.games++;
        results.put(key, r);
    }

    // Simulation d'un match entre deux joueurs sur un jeu donné, retourne les métriques
    private static MatchMetrics simulateMatch(Game game, Player p1, Player p2) {
        MatchMetrics metrics = new MatchMetrics();
        GameState state = game.init();
        long start, end;
        while (!game.endOfGame(state)) {
            start = System.nanoTime();
            var move1 = p1.getMove(state);
            end = System.nanoTime();
            if (move1 == null) break;
            metrics.timeP1 += (end - start) / 1e6;
            metrics.movesP1++;
            state = (GameState) game.doAction(state, move1);
            if (game.endOfGame(state)) break;
            start = System.nanoTime();
            var move2 = p2.getMove(state);
            end = System.nanoTime();
            if (move2 == null) break;
            metrics.timeP2 += (end - start) / 1e6;
            metrics.movesP2++;
            state = (GameState) game.doAction(state, move2);
        }
        metrics.statesP1 = p1.getStateCounter();
        metrics.statesP2 = p2.getStateCounter();
        var winner = getWinner(state,p1,p2);
        if (winner == null) {
            metrics.scoreP1 = 0.5;
            metrics.scoreP2 = 0.5;
        } else {
            if (winner.getName().equals(p1.getName())) {
                metrics.scoreP1 = 1;
                metrics.scoreP2 = 0;
            } else {
                metrics.scoreP1 = 0;
                metrics.scoreP2 = 1;
            }
        }
        if (metrics.movesP1 == 0) metrics.movesP1 = 1;
        if (metrics.movesP2 == 0) metrics.movesP2 = 1;
        return metrics;
    }

    public static Player getWinner(GameState s, Player player1, Player player2) {
        double value = s.getGameValue();

        if(value == GameState.P1_WIN)
            return player1;
        if(value == GameState.P2_WIN)
            return player2;

        return null;
    }
}
