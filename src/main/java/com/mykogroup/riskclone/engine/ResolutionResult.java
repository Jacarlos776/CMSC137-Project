package com.mykogroup.riskclone.engine;

import java.util.List;

/**
 * Represents the outcome of combat or movement at a specific province.
 */
public record ResolutionResult(
    String provinceId,
    String ownerId,
    int armyCount,
    List<String> involvedPlayerIds,
    String description
) {}
