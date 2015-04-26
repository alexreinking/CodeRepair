package coderepair.weighting;

import coderepair.graph.SynthesisGraph;

@FunctionalInterface
public interface GraphWeighter {
    void applyWeight(SynthesisGraph graph);
}
