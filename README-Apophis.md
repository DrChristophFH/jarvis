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
- [x] ? fix concurrent modification exception with models
- [ ] object graph rendering
- [ ] specific renderers

### Med Prio

- [ ] object tree list indentation fucked
- [ ] object tree list -> toString() for objects
- [ ] object graph settings in own dock
- [ ] class list
- [ ] move from strings for types to actual Type Objects
- [ ] context menu in object/local var list to focus OG to node
- [ ] better breakpoint selection and adding with line display

### Low Prio

- [ ] better event logging (class prepare event)
- [ ] logging with coloring
- [ ] fuzzy search known classes
- [ ] add directory selector


## Renderer

Render local vars via VarRenderer, registered to LocalVariables Map<LocalVariable, VarRenderer>
  
List<VarRenderer> varRenderers;
List<PrimNodeRenderer>
List<ObjNodeRenderer>
List<ArrayNodeRenderer>

select List of renderers based on node type. Then check conditions of renderers.

Every renderer: boolean isApplicable();

VarRenderer:
Rect with name, and rect with either edge or prim value.

Every Node has a Renderer component. -> strategy pattern

ObjNodeRenderer default, renders all members with default VarRenderer.

<T extends GNode> Renderer(T node)

setRenderer(Renderer)