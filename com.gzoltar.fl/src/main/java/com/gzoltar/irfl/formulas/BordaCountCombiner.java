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
package com.gzoltar.irfl.formulas;

import com.gzoltar.core.model.Node;
import com.gzoltar.core.model.NodeType;
import com.gzoltar.core.runtime.Probe;
import com.gzoltar.core.runtime.ProbeGroup;
import com.gzoltar.core.spectrum.ISpectrum;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class BordaCountCombiner extends AbstractIRFLCombiner {

    @Override
    public String getName() {
        return "Borda Count Combiner";
    }

    @Override
    public void combine(ISpectrum spectrum) {
        for(ProbeGroup g : spectrum.getProbeGroups()) {
            for(Probe probe : g.getProbes()) {
                Node node = probe.getNode();
                if(node.getNodeType() != NodeType.LINE) continue; // Topic Modeling only computes the similarity of each line with the bug report

                double irflSuspiciouness = node.getSuspiciousnessValue("Topic Modeling");
                Map<String, Double> map = new HashMap<>();

                for(Map.Entry<String, Double> entry : node.getSuspiciousnessValues().entrySet()) {
                    if(Objects.equals(entry.getKey(), "Topic Modeling")) continue;
                    // TODO, implement Borda Count
                    //map.put(entry.getKey() + "borda count combination with Topic Modeling", (entry.getValue() + irflSuspiciouness) / 2);
                }

                // in order to avoid iterating over a map while adding elements to it
                for(Map.Entry<String, Double> entry : map.entrySet()) {
                    node.addSuspiciousnessValue(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
