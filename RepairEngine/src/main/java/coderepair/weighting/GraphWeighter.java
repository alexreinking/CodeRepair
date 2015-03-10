package coderepair.weighting;

import coderepair.graph.SynthesisGraph;

@FunctionalInterface
public interface GraphWeighter {
    public void applyWeight(SynthesisGraph graph);
}
