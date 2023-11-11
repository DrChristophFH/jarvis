package com.hagenberg.jarvis.views;

import java.util.Map;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.ClassModel;
import com.hagenberg.jarvis.models.InteractionState;
import com.hagenberg.jarvis.models.entities.AccessModifier;
import com.hagenberg.jarvis.models.entities.JPackage;
import com.hagenberg.jarvis.util.Snippets;
import com.hagenberg.jarvis.util.TypeFormatter;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.ReferenceType;

import imgui.ImGui;

public class ClassList extends View {
  
  private ClassModel model = new ClassModel();
  private ReferenceType selectedClass;
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
    ImGui.sliderInt("width", width, 0, 1000);
    ImGui.beginChild("left pane", width[0], 0, true);
    model.lockModel();
    try {
      buildPackageTree(model.getRootPackages());
    } finally {
      model.unlockModel();
    }
    ImGui.endChild();
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
        for (ClassType clazz : pkg.getClasses()) {
          if (ImGui.selectable(TypeFormatter.getSimpleType(clazz.name()))) {
            selectedClass = clazz;
          }
        }
        for (InterfaceType interfaze : pkg.getInterfaces()) {
          if (ImGui.selectable(interfaze.name())) {
            selectedClass = interfaze;
          }
        }
        ImGui.treePop();
      }
    }
  }

  private void displayRefType(ReferenceType referenceType) {
    if (referenceType instanceof ClassType clazz) {
      displayClass(clazz);
    } else if (referenceType instanceof InterfaceType interfaze) {
      displayInterface(interfaze);
    }
  }

  private void displayInterface(InterfaceType interfaze) {
    ImGui.text("Interface: " + interfaze.name());
    ImGui.separator();
    ImGui.text("Superinterfaces: ");
    for (InterfaceType superinterface : interfaze.superinterfaces()) {
      ImGui.text(superinterface.name());
    }
    ImGui.separator();
    ImGui.text("Methods: ");
    for (var method : interfaze.allMethods()) {
      ImGui.text(method.name() + " : " + method.returnTypeName());
    }
  }

  private void displayClass(ClassType clazz) {
    ImGui.text("Class:");
    ImGui.sameLine();
    ImGui.textColored(Colors.Type, clazz.name());
    ImGui.separator();
    ImGui.text("Superclass:");
    ImGui.sameLine();
    ImGui.textColored(Colors.Type, clazz.superclass().name());
    ImGui.separator();
    ImGui.text("Interfaces: ");
    for (InterfaceType interfaze : clazz.interfaces()) {
      ImGui.textColored(Colors.Type, interfaze.name());
    }
    ImGui.separator();
    ImGui.checkbox("Is abstract", clazz.isAbstract());
    ImGui.checkbox("Is final", clazz.isFinal());
    ImGui.checkbox("Is static", clazz.isStatic());
    ImGui.separator();
    if (ImGui.collapsingHeader("Fields")) {
      for (var field : clazz.allFields()) {
        ImGui.textColored(Colors.AccessModifier, AccessModifier.toString(field.modifiers()));
        ImGui.sameLine();
        try {
          Snippets.drawTypeWithTooltip(field.type());
        } catch (ClassNotLoadedException e) {
          ImGui.sameLine();
          ImGui.text("<unknown>");
        }
        ImGui.sameLine();
        ImGui.text(field.name());
      }
    }
    if (ImGui.collapsingHeader("Methods")) {
      for (var method : clazz.allMethods()) {
        ImGui.textColored(Colors.AccessModifier, AccessModifier.toString(method.modifiers()));
        ImGui.sameLine();
        try {
          Snippets.drawTypeWithTooltip(method.returnType());
        } catch (ClassNotLoadedException e) {
          ImGui.sameLine();
          ImGui.text("<unknown>");
        }
        ImGui.sameLine();
        ImGui.text(method.name() + "(");
        try {
          int params = method.arguments().size();
          for (var param : method.arguments()) {
            ImGui.sameLine();
            Snippets.drawTypeWithTooltip(param.type());
            ImGui.sameLine();
            ImGui.text(param.name());
            if (params > 1) {
              ImGui.sameLine();
              ImGui.text(",");
            }
            params--;
          }
        } catch (AbsentInformationException | ClassNotLoadedException e) {
          ImGui.sameLine();
          ImGui.text("<unknown>");
        }
        ImGui.sameLine();
        ImGui.text(")");
      }
    }
  }
}
