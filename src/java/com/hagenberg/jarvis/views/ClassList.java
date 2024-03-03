package com.hagenberg.jarvis.views;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.interaction.CommandRegistry;
import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.entities.wrappers.JClassType;
import com.hagenberg.jarvis.models.entities.wrappers.JField;
import com.hagenberg.jarvis.models.entities.wrappers.JInterfaceType;
import com.hagenberg.jarvis.models.entities.wrappers.JLocalVariable;
import com.hagenberg.jarvis.models.entities.wrappers.JMethod;
import com.hagenberg.jarvis.models.entities.wrappers.JPackage;
import com.hagenberg.jarvis.models.entities.wrappers.JReferenceType;
import com.hagenberg.jarvis.util.History;
import com.hagenberg.jarvis.util.IndexedList;
import com.hagenberg.jarvis.util.Profiler;
import imgui.ImGui;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiTreeNodeFlags;

public class ClassList extends View {

  private ClassModel model = new ClassModel();
  private History<JReferenceType> selectedHistory = new History<>(10);
  private int[] width = { 200 };

  public ClassList(InteractionState interactionState) {
    setName("Class List");
    CommandRegistry.getInstance().registerCommand(JReferenceType.class, (JReferenceType clazz) -> {
      if (clazz instanceof JClassType || clazz instanceof JInterfaceType) {
        selectedHistory.push(clazz);
      }
    }, "Focus in Class List");
  }

  public ClassModel getModel() {
    return model;
  }

  @Override
  protected void renderWindow() {
    if (model == null) {
      ImGui.text("No clazz class model available");
      return;
    }

    float availableWidth = ImGui.getContentRegionAvailX();
    ImGui.setNextItemWidth(availableWidth);
    ImGui.sliderInt("width", width, 0, (int) availableWidth);
    if (ImGui.isItemHovered()) {
      ImGui.setMouseCursor(ImGuiMouseCursor.ResizeEW);
    }

    if (ImGui.arrowButton("back", ImGuiDir.Left)) {
      selectedHistory.back();
    }
    ImGui.sameLine();
    if (ImGui.arrowButton("forward", ImGuiDir.Right)) {
      selectedHistory.forward();
    }

    displayMasterPane();
    
    ImGui.sameLine();

    displayDetailPane();
  }

  private void displayDetailPane() {
    ImGui.beginChild("right pane", 0, 0, true);
    if (selectedHistory.current() != null) {
      displayRefType();
    }
    ImGui.endChild();
  }

  private void displayMasterPane() {
    ImGui.beginChild("left pane", width[0], 0, true);
    Profiler.start("cl.buildTree");
    if (model.tryLock(1, TimeUnit.MILLISECONDS)) {
      try {
        buildPackageTree(model.getRootPackages());
      } finally {
        model.unlockModel();
      }
    }
    ImGui.endChild();
    Profiler.stop("cl.buildTree");
  }

  private void buildPackageTree(Map<String, JPackage> packages) {
    for (var entry : packages.entrySet()) {
      JPackage pkg = entry.getValue();
      if (ImGui.treeNode(pkg.getName())) {
        buildPackageTree(pkg.getSubPackages());
        int treeNodeFlags = ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;

        for (JClassType clazz : pkg.getClasses()) {
          if (clazz == selectedHistory.current()) {
            ImGui.treeNodeEx(clazz.getSimpleName(), treeNodeFlags | ImGuiTreeNodeFlags.Selected);
          } else {
            ImGui.treeNodeEx(clazz.getSimpleName(), treeNodeFlags);
          }
          if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen()) {
            selectedHistory.push(clazz);
          }
        }

        for (JInterfaceType interfaze : pkg.getInterfaces()) {
          if (interfaze == selectedHistory.current()) {
            ImGui.treeNodeEx("I " + interfaze.getSimpleName(), treeNodeFlags | ImGuiTreeNodeFlags.Selected);
          } else {
            ImGui.treeNodeEx("I " + interfaze.getSimpleName(), treeNodeFlags);
          }
          if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen()) {
            selectedHistory.push(interfaze);
          }
        }
        ImGui.treePop();
      }
    }
  }

  private void displayRefType() {
    JReferenceType referenceType = selectedHistory.current();
    if (referenceType instanceof JClassType clazz) {
      displayClass(clazz);
    } else if (referenceType instanceof JInterfaceType interfaze) {
      displayInterface(interfaze);
    }
  }

  private void displayInterface(JInterfaceType interfaze) {
    ImGui.text("Interface:");
    ImGui.sameLine();
    Snippets.drawTypeWithTooltip(interfaze, tooltip, "thisInterface");
    ImGui.separator();
    ImGui.text("Superinterfaces: ");
    for (JInterfaceType superinterface : interfaze.superinterfaces()) {
      Snippets.drawTypeWithTooltip(superinterface, tooltip, superinterface.name());
    }
    ImGui.separator();
    methodSection(interfaze);
  }

  private void displayClass(JClassType clazz) {
    ImGui.text("Class:");
    ImGui.sameLine();
    Snippets.drawTypeWithTooltip(clazz, tooltip, "thisClass");
    ImGui.separator();
    ImGui.text("Superclass:");
    ImGui.sameLine();
    Snippets.drawTypeWithTooltip(clazz.superclass(), tooltip, "superClass");
    ImGui.separator();
    ImGui.text("Interfaces: ");
    for (JInterfaceType interfaze : clazz.interfaces()) {
      Snippets.drawTypeWithTooltip(interfaze, tooltip, interfaze.name());
    }
    ImGui.separator();
    ImGui.checkbox("Is abstract", clazz.isAbstract());
    ImGui.checkbox("Is final", clazz.isFinal());
    ImGui.checkbox("Is static", clazz.isStatic());
    ImGui.separator();
    ImGui.text("Generic signature: ");
    ImGui.sameLine();
    ImGui.textColored(Colors.Type, clazz.genericSignature());
    ImGui.separator();
    Profiler.start("cl.fields");
    fieldSection(clazz);
    Profiler.stop("cl.fields");
    Profiler.start("cl.methods");
    methodSection(clazz);
    Profiler.stop("cl.methods");
  }

  private void fieldSection(JClassType clazz) {
    if (ImGui.collapsingHeader("Fields")) {
      for (IndexedList<JReferenceType, JField> fieldList : clazz.allFields()) {
        if (!fieldList.isEmpty() && ImGui.treeNode("Fields of %s".formatted(fieldList.getIndex().name()))) {
          for (JField field : fieldList) {
            showField(field);
          }
          ImGui.treePop();
        }
      }
    }
  }

  private void showField(JField field) {
    ImGui.textColored(Colors.AccessModifier, field.modifiers().toString());
    ImGui.sameLine();
    if (field.typeIsGeneric()) {
      ImGui.textColored(Colors.Type, field.genericSignature());
      ImGui.sameLine();
    }
    Snippets.drawTypeWithTooltip(field.type(), tooltip);
    ImGui.sameLine();
    ImGui.text(field.name());
  }

  private void methodSection(JReferenceType clazz) {
    if (ImGui.collapsingHeader("Methods")) {
      for (IndexedList<JReferenceType, JMethod> methodList : clazz.allMethods()) {
        if (!methodList.isEmpty() && ImGui.treeNode("Methods of %s".formatted(methodList.getIndex().name()))) {
          for (JMethod method : methodList) {
            showMethod(method);
          }
          ImGui.treePop();
        }
      }
    }
  }

  private void showMethod(JMethod method) {
    ImGui.textColored(Colors.AccessModifier, method.modifiers().toString());
    ImGui.sameLine();

    Profiler.start("cl.methods.returnType");
    if (method.typeIsGeneric()) {
      ImGui.textColored(Colors.Type, method.genericSignature());
      ImGui.sameLine();
    }
    Snippets.drawTypeWithTooltip(method.returnType(), tooltip);
    Profiler.stop("cl.methods.returnType");
    ImGui.sameLine();

    ImGui.text(method.name() + "(");
    Profiler.start("cl.methods.params");
    int params = method.arguments().size();
    for (JLocalVariable param : method.arguments()) {
      ImGui.sameLine();
      if (param.typeIsGeneric()) {
        ImGui.textColored(Colors.Type, param.genericTypeName());
        ImGui.sameLine();
      }
      Snippets.drawTypeWithTooltip(param.getType(), tooltip);
      ImGui.sameLine();
      ImGui.text(param.name());
      if (params > 1) {
        ImGui.sameLine();
        ImGui.text(",");
      }
      params--;
    }
    Profiler.stop("cl.methods.params");
    ImGui.sameLine();
    ImGui.text(")");
  }
}
