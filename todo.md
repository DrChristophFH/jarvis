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
- [x] add context menu for transformers back in
- [x] seems like constantly querying into JDI is not performant at all (cache everything for classList into own data structures)

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
- [x] empty template name results in crash :)
- [x] manual layouting in object graph
- [x] current line preview
- [x] loading and saving of templates
- [x] object list doesn't display array content
- [x] tie class model with object model
- [x] fix JType aquisition for non loaded classes
- [x] add context options (display in class list, filter in object list)
- [x] save breakpoints
- [x] make src path configurable as well as src.zip location

### Low Prio

- [x] better event logging (class prepare event)
- [x] logging with coloring
- [x] show number of objects in object list (changing on filters)
- [x] dynamic breakpoint adding/removing
- [x] save special classes, class path, src path, src.zip path etc.

### Future Work

- [ ] (xxl) Refactor and clean up code
  - [ ] Dependency Injection
  - [ ] Implement generic observable for configuration parameters => central management of save/load
  - [ ] Move to full observer pattern for breakpoint provider => makes it easier to update line display
  - [ ] Pattern for easy access to debuggee data (class model, object model, etc.)?
- [ ] ( m ) focus on object graph node
- [ ] ( l ) hide objects in graph (via ctx in graph or via local var list -> hide toggle)
- [ ] (xxl) move to imgui-node-editor for zoomable graph
- [ ] ( l ) template editor could use information from the class model for tab completion
- [ ] ( s ) Fix line display focus issue and auto select for new stack frames 
- [ ] ( m ) Breakpoint display in line display
- [ ] ( m ) Breakpoint adding with line display
- [ ] ( ? ) add directory selector?
- [ ] ( m ) Better Generic Type Information
  - [ ] Display actual type for generic typed fields and methods on typed classes  
  - [ ]  (fully resolve generic types on specified classes)
  - [ ]  `<T:Lcom/example/OOPSimple$HelloWorld;:Ljava/lang/Comparable<Ljava/lang/String;>;>Ljava/lang/Object;Lcom/example/OOPSimple$TestInterface;` -> `class Person<T extends HelloWorld & Comparable<String>> implements TestInterface`
         `<TypeIdentifier:ClassRestriction:InterfaceRestriction>BaseClass<Type1;Type2>;Interface1;Interface2;`