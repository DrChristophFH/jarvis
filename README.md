# JARVIS (Java Application Runtime Visualization and Inspection System)

This is the repository for my bachelors thesis at the University of Applied Sciences Upper Austria, for the Bachelor Degree in Software Engineering.

## Initial Proposal

The field of program runtime visualization is a critical component in teaching programming concepts and supporting developers in understanding program behaviour. Existing tools, however, often fall short when tasked with representing larger, more complex programs that involve a multitude of objects and larger call stack depths. This proposal outlines a project aimed at developing a sophisticated visualization system using Java, capable of addressing the visualization challenges associated with larger programs.

Objectives:

- Investigate Existing Program Runtime Visualization Tools: This serves as the groundwork for the project. It involves reviewing and understanding the capabilities and limitations of current tools, especially concerning larger, more complex programs.
- Design and Develop a New Visualization System: Leveraging the knowledge gained from the investigation, a new visualization system will be developed with a focus on information abstraction and intuitive interaction, enabling clear understanding and overview of complex programs on different layers and from different perspectives. The system will most likely build on the JDPAs capabilities to achieve this goal.
- Optimize Data Gathering and Display: The proposed system seeks to maintain performance and manage memory usage efficiently. The exploration of techniques for data filtering at the point of data gathering from the debuggee, not just at the display level, will be a key consideration.
- Incorporate Modern UI and Interactive Design: The system will aim to exploit modern UI capabilities to selectively hide and display desired information, facilitating a more engaging and user-friendly experience.
- Incorporate Program Flow Visualization: Crucial to the proposed system is a visualization of the flow of the program, showing method calls to other objects, parameter values, return values, and other necessary information. Additional visual components such as the currently executed line, the call stack, and a scene graph for the objects will be incorporated.

Secondary Objective:

Domain-Specific Visualization Capabilities: While developing and designing JARVIS the area of domain-specific visualization will be explored and evaluated. Optimally incorporating a flexible architecture open to custom visualizations of objects regarding their actual domain (for example depicting a car object as a car, and it's members like wheels etc also as such). Although full implementation might exceed the scope of this bachelor thesis, the practicality, usefulness, and other related aspects of this feature will be evaluated.

This thesis aims to push the boundaries of current program runtime visualization by developing a system that addresses the limitations of existing tools and introduces a more sophisticated, user-friendly, modern and efficient way to visualize larger, more complex programs. It promises to contribute significantly to the field, offering an effective tool for both educators, students and developers.


## TODO

### High Prio

- [x] add call stack view back
- [x] static vs dynamic binding 
- [x] parameters in stack frames should be local vars...
- [x] incremental model updates
- [x] add shutdown button
- [x] object graph rendering
- [x] ? fix concurrent modification exception with models
- [x] base class fields not showing up?
- [x] root springs -> convert to simple fixed root layout (springs dumb idea)
- [x] fix arrays? see hashmap demo with hashmap entry not getting links?
- [x] tie RenderGraph to ObjectGraph -> needed for incremental node layouting (cannot simply discard nodes) -> either update old nodes or steal position from old nodes
- [x] repair graph layouting -> nodes have to track in and out neighbours (determine during connection pass)
- [x] add template based rendering back in
- [x] add manual node dragging back in
- [ ] add context menu for transformers back in
- [ ] specific renderers

### Med Prio

- [x] object tree list -> toString() for objects
- [x] object graph settings in own dock
- [x] Adjust root spring force
- [x] Option to disable root spring force for manual positioning
- [x] add toString representation to default object renderer
- [x] move from strings for types to actual Type Objects
- [x] Adjust spring length based on node size
- [x] context menu in object/local var list to focus OG to node -> simple with ImNodes
- [x] MethodParameter class link with Object Graph (already got deleted is now done via LocalVar)
- [x] node based settings, the easiest with shortcuts or button context menu
- [x] Class List (see methods, fields (also static fields), inheritance, interfaces)
- [x] object tree list indentation fucked
- [x] window display controls 
- [x] call stack type to simple with hint
- [x] generic signature display and resolving
- [x] sort methods in class list based on declaring Type -> ordered list of JRefType and List<Method>
- [x] refactor layouting stuff into own class
- [x] object graph transformation only enumerate whole graph with unique ids
- [x] renderers populate links and add to layout queue for layouter -> renderers determine layout
- [x] local var list to standard debugger tree list
- [x] object type filtering in object list
- [ ] manual layouting in object graph -> determines spring sizes to neighbours
- [ ] add context options (display in class list, filter in object list)
- [ ] current line preview

### Low Prio

- [x] better event logging (class prepare event)
- [x] seems like constantly querying into JDI is not performant at all (cache everything for classList into own data structures)
- [ ] better breakpoint selection and adding with line display
- [ ] tie class model with object model
- [ ] logging with coloring
- [ ] fuzzy search known classes
- [ ] add directory selector


## Notes

With more time an extensive template based rendering could be built into the system, with features like saving and loading templates and while building them having a preview as well as automatic type safety. This can be achieved by specifying the renderer for a class and then using this class in the background to provide options for the paths to take in the template. This is not trivial as upon restart we'd need to recreate those renderer objects and also try to resolve the paths again against the new class model. For now the pathing is done with strings and relies on the user to provide the correct pathing. This is not ideal but provides a lot of flexibility and is easy to implement. Additionally it is also serializable and can be saved and loaded.

Each rendering step the path is tried to resolve from the current object. When the path cannot be resolved at some point a simple string with an error message is rendered.