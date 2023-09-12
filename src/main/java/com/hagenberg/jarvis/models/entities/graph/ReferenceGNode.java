package com.hagenberg.jarvis.models.entities.graph;

public class ReferenceGNode extends GNode {
    private final ObjectGNode object;

    public ReferenceGNode(ObjectGNode object) {
        super(object.getType());
        this.object = object;
    }

    public ObjectGNode getObject() {
        return object;
    }

    @Override
    public void loadContents() {
        if (!isLoaded) {
            // Implement logic to load data from JDI here

            // Example: pseudocode to demonstrate how to load data using JDI
            // This would be a call to JDI to get the object details and populate the ObjectNode
            /*
            ObjectReference objRef = ... // Get this from JDI
            this.object = new ObjectNode();
            this.object.setId(objRef.uniqueID());
            this.object.setType(objRef.referenceType().name());
            this.object.setMembers(...); // Populate members by querying JDI

            // Setting isLoaded to true to indicate that data has been loaded
            this.isLoaded = true;
            */
        }
    }

    // Getters, Setters, and other methods
}