package com.mykogroup.riskclone.engine;

import com.mykogroup.riskclone.model.GameState;
import com.mykogroup.riskclone.model.Move;
import com.mykogroup.riskclone.model.Province;

import java.util.*;

public class ResolutionEngine {

    // A mutable wrapper for processing combat without breaking the pure state records
    private static class MarchingArmy {
        String playerId;
        String fromId;
        String toId;
        int armies;

        MarchingArmy(Move move) {
            this.playerId = move.playerId();
            this.fromId = move.fromId();
            this.toId = move.toId();
            this.armies = move.armies();
        }
    }

    public List<ResolutionResult> processTurn(GameState state) {
        List<MarchingArmy> activeArmies = new ArrayList<>();
        for (Move m : state.getQueuedMoves()) {
            activeArmies.add(new MarchingArmy(m));
        }

        List<ResolutionResult> results = new ArrayList<>();

        // ---------------------------------------------------------
        // PHASE 1: DEPARTURES
        // ---------------------------------------------------------
        for (MarchingArmy army : activeArmies) {
            state.getProvince(army.fromId).ifPresent(p -> {
                p.setArmyCount(p.getArmyCount() - army.armies);
            });
        }

        // ---------------------------------------------------------
        // PHASE 2: CROSSFIRES (Head-to-Head on the road)
        // ---------------------------------------------------------
        for (int i = 0; i < activeArmies.size(); i++) {
            for (int j = i + 1; j < activeArmies.size(); j++) {
                MarchingArmy a1 = activeArmies.get(i);
                MarchingArmy a2 = activeArmies.get(j);

                if (a1.fromId.equals(a2.toId) && a1.toId.equals(a2.fromId) && !a1.playerId.equals(a2.playerId)) {
                    if (a1.armies > a2.armies) {
                        a1.armies -= a2.armies;
                        a2.armies = 0;
                    } else if (a2.armies > a1.armies) {
                        a2.armies -= a1.armies;
                        a1.armies = 0;
                    } else {
                        a1.armies = 0;
                        a2.armies = 0;
                    }
                }
            }
        }
        activeArmies.removeIf(a -> a.armies <= 0);

        // ---------------------------------------------------------
        // PHASE 3: CONVERGENCE & CLASHES
        // ---------------------------------------------------------
        Map<String, List<MarchingArmy>> arrivals = new HashMap<>();
        for (MarchingArmy a : activeArmies) {
            arrivals.computeIfAbsent(a.toId, k -> new ArrayList<>()).add(a);
        }

        for (Province p : state.getProvinces()) {
            List<MarchingArmy> arrivingHere = arrivals.getOrDefault(p.getId(), Collections.emptyList());
            if (arrivingHere.isEmpty()) continue;

            Map<String, Integer> forcesByPlayer = new HashMap<>();
            List<String> involvedPlayers = new ArrayList<>();

            if (p.getOwnerId() != null && p.getArmyCount() > 0) {
                forcesByPlayer.put(p.getOwnerId(), p.getArmyCount());
                involvedPlayers.add(p.getOwnerId());
            }

            for (MarchingArmy a : arrivingHere) {
                forcesByPlayer.put(a.playerId, forcesByPlayer.getOrDefault(a.playerId, 0) + a.armies);
                if (!involvedPlayers.contains(a.playerId)) involvedPlayers.add(a.playerId);
            }

            String description;
            if (forcesByPlayer.size() == 1) {
                String singlePlayer = forcesByPlayer.keySet().iterator().next();
                p.setOwnerId(singlePlayer);
                p.setArmyCount(forcesByPlayer.get(singlePlayer));
                description = "Reinforced / Peaceful move";
            } else {
                List<Map.Entry<String, Integer>> sortedForces = new ArrayList<>(forcesByPlayer.entrySet());
                sortedForces.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

                var firstPlace = sortedForces.get(0);
                var secondPlace = sortedForces.get(1);

                if (firstPlace.getValue().equals(secondPlace.getValue())) {
                    String originalOwner = p.getOwnerId();
                    if (forcesByPlayer.containsKey(originalOwner) && forcesByPlayer.get(originalOwner).equals(firstPlace.getValue())) {
                        p.setArmyCount(1);
                        description = "Defender tied for victory. Held with 1 troop.";
                    } else {
                        p.setOwnerId(null);
                        p.setArmyCount(0);
                        description = "Mutual destruction. Province is now neutral.";
                    }
                } else {
                    p.setOwnerId(firstPlace.getKey());
                    p.setArmyCount(firstPlace.getValue() - secondPlace.getValue());
                    description = "Conquered by " + firstPlace.getKey();
                }
            }

            results.add(new ResolutionResult(
                p.getId(),
                p.getOwnerId(),
                p.getArmyCount(),
                involvedPlayers,
                description
            ));
        }

        state.clearQueuedMoves();
        state.resetReadyStates();
        return results;
    }
}