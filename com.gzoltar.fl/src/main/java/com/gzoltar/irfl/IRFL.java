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

import com.gzoltar.core.model.Node;
import com.gzoltar.core.model.NodeType;
import com.gzoltar.core.runtime.Probe;
import com.gzoltar.core.runtime.ProbeGroup;
import com.gzoltar.core.spectrum.ISpectrum;
import com.gzoltar.fl.IFaultLocalization;
import com.gzoltar.fl.IFormula;
import com.gzoltar.irfl.topicModeling.lda.Document;
import com.gzoltar.irfl.topicModeling.lda.DocumentFactory;
import com.gzoltar.irfl.topicModeling.lda.DocumentType;
import com.gzoltar.irfl.topicModeling.lda.LDAProcessor;
import com.gzoltar.irfl.topicModeling.nlp.NLPParser;
import com.gzoltar.irfl.topicModeling.nlp.ProcessedLine;
import com.gzoltar.sfl.SFL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IRFL<F extends IFormula> implements IFaultLocalization<F> {

    private final SFL<F> sfl;
    private final List<F> irflFormulas = new ArrayList<F>();
    private final String bugReportUrl;

    public IRFL(final List<F> irflFormulas, final List<F> sflFormulas, String bugReportURL) {
        sfl = new SFL<>(sflFormulas);
        this.irflFormulas.addAll(irflFormulas);
        this.bugReportUrl = bugReportURL;
    }

    @Override
    public void diagnose(ISpectrum spectrum) throws IOException {
        sfl.diagnose(spectrum); // spectrum based fault localization formulas
        computeSimilarity(spectrum); // Topic Modeling step
        this.compute(spectrum);
    }

    private void computeSimilarity(ISpectrum spectrum) throws IOException {
        NLPParser parser = new NLPParser();

        Document statementFactory = DocumentFactory.getDocumentFactory(DocumentType.STATEMENT, parser);

        List<Node> nodes = new ArrayList<>();
        for(ProbeGroup probeGroup : spectrum.getProbeGroups()) {
            for(Probe probe : probeGroup.getProbes()) {
                Node node = probe.getNode();
                if(node.getNodeType() != NodeType.LINE) continue;
                nodes.add(node);
            }
        }

        List<ProcessedLine> processedSourceFile = statementFactory.createDocuments(nodes);
        Document bugReportFactory = DocumentFactory.getDocumentFactory(DocumentType.BUGREPORT, parser);
        List<ProcessedLine> processedBugReport = bugReportFactory.createDocuments(bugReportUrl);
        List<ProcessedLine> allProcessedLines = new ArrayList<>(processedSourceFile);
        allProcessedLines.addAll(processedBugReport);

        // values that gave the best results, but adjustable
        // TODO, check if it makes sense to allow users to control these parameters
        int numTopics = 2;
        int numIterations = 100;
        int numThreads = 2;
        LDAProcessor ldaProcessor = new LDAProcessor(allProcessedLines, numTopics, numIterations, numThreads);
        ldaProcessor.run();

        Map<ProcessedLine, double[]> topicDistributionMap = ldaProcessor.getTopicDistributions();

        double[] queryDistribution = topicDistributionMap.get(processedBugReport.get(0));

        double[][] topicDistributions = topicDistributionMap.values().toArray(new double[0][0]);

        IRFLRanking irflRanking = new IRFLRanking(topicDistributions, queryDistribution, new ArrayList<>(processedSourceFile));
        Map<String, Double> suspiciousnessScores = irflRanking.getRanking();

        for(ProbeGroup probeGroup : spectrum.getProbeGroups()) {
            for(Probe probe : probeGroup.getProbes()) {
                Node node = probe.getNode();
                if(node.getNodeType() != NodeType.LINE) continue;
                if(!suspiciousnessScores.containsKey(node.getNameWithLineNumber())) continue;
                Double IRFLSuspiciouness = suspiciousnessScores.get(node.getNameWithLineNumber());
                node.addSuspiciousnessValue("Topic Modeling", IRFLSuspiciouness);
            }
        }
    }

    public void compute(ISpectrum spectrum) {
        for (F formula : irflFormulas) {
            formula.diagnose(spectrum);
        }
    }


}
