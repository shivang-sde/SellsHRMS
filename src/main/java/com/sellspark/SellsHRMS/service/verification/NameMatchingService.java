package com.sellspark.SellsHRMS.service.verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Fuzzy name matching service for document verification.
 * Compares names from PAN/GST API responses against organisation name.
 *
 * Uses a combination of:
 * - Exact match (case-insensitive)
 * - Contains/substring match
 * - Levenshtein distance for fuzzy matching
 * - Token-based comparison (handles word reordering)
 */
@Slf4j
@Service
public class NameMatchingService {

    private static final int MATCH_THRESHOLD = 70; // minimum score to consider a match

    /**
     * Checks if two names are a "good enough" match.
     * 
     * @return true if score >= threshold
     */
    public boolean isMatch(String name1, String name2) {
        int score = calculateMatchScore(name1, name2);
        log.info("[NAME_MATCH] '{}' vs '{}' → Score: {} (threshold: {})", name1, name2, score, MATCH_THRESHOLD);
        return score >= MATCH_THRESHOLD;
    }

    /**
     * Returns match score (0-100) between two names.
     */
    public int calculateMatchScore(String name1, String name2) {
        if (name1 == null || name1.isBlank()) {
            log.warn("[NAME_MATCH] name1 is null/empty");
            return 0;
        }
        if (name2 == null || name2.isBlank()) {
            log.warn("[NAME_MATCH] name2 is null/empty");
            return 0;
        }

        String n1 = normalize(name1);
        String n2 = normalize(name2);

        if (n1.isEmpty() || n2.isEmpty())
            return 0;

        // Exact match
        if (n1.equals(n2))
            return 100;

        // Contains match (one contains the other)
        if (n1.contains(n2) || n2.contains(n1))
            return 90;

        // Token-based comparison (handles word reordering)
        int tokenScore = tokenBasedScore(n1, n2);

        // Levenshtein-based score
        int levenScore = levenshteinScore(n1, n2);

        // Return the best score
        return Math.max(tokenScore, levenScore);
    }

    // ─── Private Helpers ─────────────────────────────────────────────

    private String normalize(String name) {
        if (name == null)
            return "";

        String cleaned = name.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ");

        // Remove common company suffixes
        cleaned = cleaned.replaceAll(
                "\\b(private|limited|ltd|pvt|company|co|services)\\b", "").replaceAll("\\s+", " ").trim();

        return cleaned;
    }

    /**
     * Token-based comparison: splits into words, counts matching tokens.
     * Handles name word reordering (e.g. "JOHN DOE" vs "DOE JOHN").
     */
    private int tokenBasedScore(String n1, String n2) {
        String[] tokens1 = n1.split("\\s+");
        String[] tokens2 = n2.split("\\s+");

        int matches = 0;
        int total = Math.max(tokens1.length, tokens2.length);

        for (String t1 : tokens1) {
            for (String t2 : tokens2) {
                if (t1.equals(t2) || levenshteinDistance(t1, t2) <= 2) {
                    matches++;
                    break;
                }
            }
        }

        return total == 0 ? 0 : (int) ((double) matches / total * 100);
    }

    /**
     * Levenshtein distance-based score.
     */
    private int levenshteinScore(String s1, String s2) {
        int dist = levenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0)
            return 100;
        return (int) ((1.0 - (double) dist / maxLen) * 100);
    }

    /**
     * Standard Levenshtein distance calculation.
     */
    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++)
            dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++)
            dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }

        return dp[a.length()][b.length()];
    }
}
