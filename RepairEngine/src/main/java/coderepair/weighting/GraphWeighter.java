package coderepair.weighting;

import coderepair.SynthesisGraph;

@FunctionalInterface
public interface GraphWeighter {
    public void applyWeight(SynthesisGraph graph);
}
