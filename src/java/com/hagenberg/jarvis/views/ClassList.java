package com.hagenberg.jarvis.views;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.Snippets;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.entities.AccessModifier;
import com.hagenberg.jarvis.models.entities.classList.JClass;
import com.hagenberg.jarvis.models.entities.classList.JField;
import com.hagenberg.jarvis.models.entities.classList.JInterface;
import com.hagenberg.jarvis.models.entities.classList.JLocalVariable;
import com.hagenberg.jarvis.models.entities.classList.JMethod;
import com.hagenberg.jarvis.models.entities.classList.JPackage;
import com.hagenberg.jarvis.models.entities.classList.JReferenceType;
import com.hagenberg.jarvis.util.IndexedList;
import com.hagenberg.jarvis.util.Profiler;
import com.hagenberg.jarvis.util.TypeFormatter;
import imgui.ImGui;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiTreeNodeFlags;

public class ClassList extends View {

  private ClassModel model = new ClassModel();
  private JReferenceType selectedClass;
  private int[] width = { 200 };

  public ClassList(InteractionState interactionState) {
    setName("Class List");
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
    ImGui.sameLine();
    ImGui.beginChild("right pane", 0, 0, true);
    if (selectedClass != null) {
      displayRefType(selectedClass);
    }
    ImGui.endChild();
  }

  private void buildPackageTree(Map<String, JPackage> packages) {
    for (var entry : packages.entrySet()) {
      JPackage pkg = entry.getValue();
      if (ImGui.treeNode(pkg.getName())) {
        buildPackageTree(pkg.getSubPackages());
        for (JClass clazz : pkg.getClasses()) {
          ImGui.treeNodeEx(TypeFormatter.getSimpleType(clazz.name()), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen);
          if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen()) {
            selectedClass = clazz;
          }
        }
        for (JInterface interfaze : pkg.getInterfaces()) {
          ImGui.treeNodeEx("I " + TypeFormatter.getSimpleType(interfaze.name()), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen);
          if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen()) {
            selectedClass = interfaze;
          }
        }
        ImGui.treePop();
      }
    }
  }

  private void displayRefType(JReferenceType referenceType) {
    if (referenceType instanceof JClass clazz) {
      displayClass(clazz);
    } else if (referenceType instanceof JInterface interfaze) {
      displayInterface(interfaze);
    }
  }

  private void displayInterface(JInterface interfaze) {
    ImGui.text("Interface: " + interfaze.name());
    ImGui.separator();
    ImGui.text("Superinterfaces: ");
    for (JInterface superinterface : interfaze.superinterfaces()) {
      ImGui.text(superinterface.name());
    }
    ImGui.separator();
    ImGui.text("Methods: ");
    for (IndexedList<JReferenceType, JMethod> methodList : interfaze.allMethods()) {
      ImGui.text(methodList.getIndex().name());
    }
  }

  private void displayClass(JClass clazz) {
    ImGui.text("Class:");
    ImGui.sameLine();
    ImGui.textColored(Colors.Type, clazz.name());
    ImGui.separator();
    ImGui.text("Superclass:");
    ImGui.sameLine();
    ImGui.textColored(Colors.Type, clazz.superclass().name());
    ImGui.separator();
    ImGui.text("Interfaces: ");
    for (JInterface interfaze : clazz.interfaces()) {
      ImGui.textColored(Colors.Type, interfaze.name());
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

  private void fieldSection(JClass clazz) {
    if (ImGui.collapsingHeader("Fields")) {
      for (IndexedList<JReferenceType, JField> fieldList : clazz.allFields()) {
        if (ImGui.treeNode("Fields of %s".formatted(fieldList.getIndex().name()))) {
          for (JField field : fieldList) {
            showField(field);
          }
          ImGui.treePop();
        }
      }
    }
  }

  private void showField(JField field) {
    ImGui.textColored(Colors.AccessModifier, AccessModifier.toString(field.modifiers()));
    ImGui.sameLine();
    if (field.typeIsGeneric()) {
      ImGui.textColored(Colors.Type, field.genericSignature());
      ImGui.sameLine();
    }
    Snippets.drawTypeWithTooltip(field.typeName(), tooltip);
    ImGui.sameLine();
    ImGui.text(field.name());
  }

  private void methodSection(JClass clazz) {
    if (ImGui.collapsingHeader("Methods")) {
      for (IndexedList<JReferenceType, JMethod> methodList : clazz.allMethods()) {
        if (ImGui.treeNode("Methods of %s".formatted(methodList.getIndex().name()))) {
          for (JMethod method : methodList) {
            showMethod(method);
          }
          ImGui.treePop();
        }
      }
    }
  }

  private void showMethod(JMethod method) {
    Profiler.start("cl.methods.modifiers");
    int modifiers = method.modifiers();
    Profiler.stop("cl.methods.modifiers");
    ImGui.textColored(Colors.AccessModifier, AccessModifier.toString(modifiers));
    ImGui.sameLine();
    Profiler.start("cl.methods.returnType");
    if (method.typeIsGeneric()) {
      ImGui.textColored(Colors.Type, method.genericSignature());
      ImGui.sameLine();
    }
    Snippets.drawTypeWithTooltip(method.returnTypeName(), tooltip);
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
      Snippets.drawTypeWithTooltip(param.typeName(), tooltip);
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
