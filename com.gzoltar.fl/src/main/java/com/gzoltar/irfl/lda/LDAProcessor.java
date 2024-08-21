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
package com.gzoltar.irfl.lda;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.TokenSequence;
import com.gzoltar.irfl.nlp.ProcessedLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LDAProcessor {
    private final ParallelTopicModel model;
    private final InstanceList instances;
    private final List<ProcessedLine> processedLines;

    public LDAProcessor(List<ProcessedLine> processedLines, int numTopics, int numIterations, int numThreads) {
        this.processedLines = processedLines;
        this.instances = prepareInstances(processedLines);
        this.model = new ParallelTopicModel(numTopics);
        model.addInstances(instances);
        model.setNumIterations(numIterations);
        model.setNumThreads(numThreads);
        model.setRandomSeed(42); // to reduce inconsistencies between different runs
    }

    private InstanceList prepareInstances(List<ProcessedLine> processedLines) {
        ArrayList<Pipe> pipeList = new ArrayList<>();

        pipeList.add(new TokenSequence2FeatureSequence());
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        for (ProcessedLine processedLine : processedLines) {
            TokenSequence tokenSequence = new TokenSequence(processedLine.getTokens().toArray(new String[0]));
            Instance instance = new Instance(tokenSequence, null, processedLine.getName(), null);
            instances.addThruPipe(instance);
        }

        return instances;
    }

    public void run() throws IOException {
        model.estimate();
    }

    public Map<ProcessedLine, double[]> getTopicDistributions() {
        Map<ProcessedLine, double[]> topicDistributionMap = new HashMap<>();
        for (int i = 0; i < instances.size(); i++) {
            double[] topicDistribution = model.getInferencer().getSampledDistribution(instances.get(i), 10, 1, 5);
            topicDistributionMap.put(processedLines.get(i), topicDistribution);
        }
        return topicDistributionMap;
    }

}
