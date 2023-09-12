package com.hagenberg.jarvis.views.components.graph;

import com.hagenberg.jarvis.models.entities.graph.GNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VisualizationRegistry {

    private static class VisualizationEntry {
        private Predicate<GNode> rule;
        private Function<GNode, NodeVisualization> factory;

        public VisualizationEntry(Predicate<GNode> rule, Function<GNode, NodeVisualization> factory) {
            this.rule = rule;
            this.factory = factory;
        }

        public boolean matches(GNode node) {
            return rule.test(node);
        }

        public NodeVisualization createVisualization(GNode node) {
            return factory.apply(node);
        }
    }

    private List<VisualizationEntry> entries = new ArrayList<>();

    public void register(Predicate<GNode> rule, Function<GNode, NodeVisualization> factory) {
        entries.add(new VisualizationEntry(rule, factory));
    }

    public List<NodeVisualization> getApplicableVisualizations(GNode node) {
        return entries.stream()
                .filter(entry -> entry.matches(node))
                .map(entry -> entry.createVisualization(node))
                .collect(Collectors.toList());
    }
}
