/**
 * Copyright (C) 2020 GZoltar contributors.
 *
 * This file is part of GZoltar.
 *
 * GZoltar is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * GZoltar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with GZoltar. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.gzoltar.irfl;

import com.gzoltar.irfl.topicModeling.nlp.ProcessedLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IRFLRanking {

    private final double[][] topicDistribution;
    private final double[] query;
    private final List<ProcessedLine> documents;

    public IRFLRanking(double[][] topicDistribution, double[] query, List<ProcessedLine> documents) {
        this.topicDistribution = topicDistribution;
        this.query = query;
        this.documents = documents;
    }

    /**
     * Computes the suspiciousness scores for each statement using the dot product.
     * @return An array of suspiciousness scores, one for each document.
     */
    public double[] computeSuspiciousnessScores() {
        int numDocs = topicDistribution.length;
        double[] suspiciousnessScores = new double[numDocs];

        for (int i = 0; i < numDocs; i++) {
            suspiciousnessScores[i] = computeDotProduct(topicDistribution[i], query);
        }

        return suspiciousnessScores;
    }

    private double computeDotProduct(double[] vector1, double[] vector2) {
        double dotProduct = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
        }
        return dotProduct;
    }

    public Map<String, Double> getRanking() {
        double[] suspiciousnessScores = computeSuspiciousnessScores();
        Map<String, Double> docs = new HashMap<>();

        for (int i = 0; i < documents.size(); i++) {
            docs.put(documents.get(i).getName(), suspiciousnessScores[i]);
        }

        return docs;
    }
}