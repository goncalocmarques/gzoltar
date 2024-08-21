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

import com.gzoltar.core.model.Node;
import com.gzoltar.core.model.NodeType;
import com.gzoltar.irfl.nlp.NLPParser;
import com.gzoltar.irfl.nlp.ProcessedLine;

import java.util.ArrayList;
import java.util.List;

public class StatementDocument implements Document {
    private final NLPParser parser;

    public StatementDocument(NLPParser parser) {
        this.parser = parser;
    }

    @Override
    public List<ProcessedLine> createDocuments(Object input) {
        if (!(input instanceof List<?>)) {
            throw new IllegalArgumentException("Expected a List<Node> for StatementDocument");
        }
        List<Node> nodes = (List<Node>) input;
        List<ProcessedLine> processedLines = new ArrayList<>();
        for (Node node : nodes) {
            if(node.getNodeType() != NodeType.LINE) continue;
            String line = node.getContent();
            List<String> tokens = new ArrayList<>();
            tokens.add(line);
            List<String> processedTokens = parser.processTokens(tokens);
            if(!processedTokens.isEmpty()) {
                processedLines.add(new ProcessedLine(node.getNameWithLineNumber(), processedTokens));
            }

        }
        return processedLines;
    }
}
