package com.hagenberg.jarvis.views.components.graph;

import com.hagenberg.jarvis.models.entities.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VisualizationRegistry {

    private static class VisualizationEntry {
        private Predicate<Node> rule;
        private Function<Node, NodeVisualization> factory;

        public VisualizationEntry(Predicate<Node> rule, Function<Node, NodeVisualization> factory) {
            this.rule = rule;
            this.factory = factory;
        }

        public boolean matches(Node node) {
            return rule.test(node);
        }

        public NodeVisualization createVisualization(Node node) {
            return factory.apply(node);
        }
    }

    private List<VisualizationEntry> entries = new ArrayList<>();

    public void register(Predicate<Node> rule, Function<Node, NodeVisualization> factory) {
        entries.add(new VisualizationEntry(rule, factory));
    }

    public List<NodeVisualization> getApplicableVisualizations(Node node) {
        return entries.stream()
                .filter(entry -> entry.matches(node))
                .map(entry -> entry.createVisualization(node))
                .collect(Collectors.toList());
    }
}
