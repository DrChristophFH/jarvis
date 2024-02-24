package com.hagenberg.jarvis.views;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hagenberg.imgui.Colors;
import com.hagenberg.imgui.View;
import com.hagenberg.jarvis.models.CallStackModel;
import com.hagenberg.jarvis.models.entities.CallStackFrame;
import com.sun.jdi.AbsentInformationException;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

public class LinePreview extends View {

  private CallStackModel model;

  private List<ImString> sourcePaths = new ArrayList<>();
  private String jreDirectory = System.getProperty("java.home");
  private Path jdkDirectory = null;
  private ImString srcZipPath = null;

  private int[] width = { 200 };

  private List<String> viewerSource;
  private CallStackFrame selectedFrame;
  private boolean moveToLine = false;

  Set<String> javaKeywords = new HashSet<>(
      Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default",
          "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "if", "goto", "implements", "import", "instanceof",
          "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
          "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"));

  public LinePreview(CallStackModel model) {
    this.model = model;
    setName("Line Preview");
    sourcePaths.add(new ImString("src/java", 255));
    jdkDirectory = jreDirectory != null ? Paths.get(jreDirectory) : null;
    srcZipPath = new ImString(jdkDirectory != null ? jdkDirectory.toAbsolutePath().toString() + "\\lib\\src.zip" : "", 255);
  }

  public List<ImString> getSourcePaths() {
    return sourcePaths;
  }

  public ImString getSrcZipPath() {
    return srcZipPath;
  }

  @Override
  protected void renderWindow() {
    if (model.getCallStack().isEmpty()) {
      ImGui.text("No call stack available.");
      return;
    }

    float availableWidth = ImGui.getContentRegionAvailX();
    ImGui.setNextItemWidth(availableWidth);
    ImGui.sliderInt("width", width, 0, (int) availableWidth);

    if (ImGui.isItemHovered()) {
      ImGui.setMouseCursor(ImGuiMouseCursor.ResizeEW);
    }

    ImGui.beginChild("left pane", width[0], 0, true);
    showCallStackFileList();
    ImGui.endChild();

    ImGui.sameLine();

    ImGui.beginChild("right pane", 0, 0, true, ImGuiWindowFlags.AlwaysHorizontalScrollbar);
    showFile();
    ImGui.endChild();
  }

  private void showCallStackFileList() {
    for (CallStackFrame frame : model.getCallStack()) {
      try {
        if (ImGui.selectable(frame.getSimpleMethodHeader(), selectedFrame == frame)) {
          resolveSourcePath(frame.getMethod().getJdiMethod().location().sourcePath(),
              frame.getMethod().getJdiMethod().declaringType().module().name());
          selectedFrame = frame;
          moveToLine = true;
        }
      } catch (AbsentInformationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /**
   * Resolves the source code of a file by looking in the source paths and in the
   * src.zip file.
   * 
   * @param relativeSourcePath the relative path to the source file
   * @param moduleName         the name of the module the source file is in
   * @return the source code of the file or "Source not found." if the file could
   *         not be found
   */
  private void resolveSourcePath(String relativeSourcePath, String moduleName) {
    List<String> sourceCode = null;
    for (ImString sourcePath : sourcePaths) {
      Path srcFile = Paths.get(sourcePath.get(), relativeSourcePath);
      if (Files.exists(srcFile)) {
        try {
          sourceCode = Files.readAllLines(srcFile);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    if (sourceCode == null) {
      try (FileSystem zipFs = FileSystems.newFileSystem(Paths.get(srcZipPath.get()))) {
        Path filePathInZip = zipFs.getPath(moduleName + "/" + relativeSourcePath);
        if (Files.exists(filePathInZip)) {
          sourceCode = Files.readAllLines(filePathInZip);
        }
      } catch (IOException e) {
        // Handle I/O errors
        e.printStackTrace();
      }
    }

    viewerSource = sourceCode;
  }

  private void showFile() {
    if (viewerSource != null) {
      float lineHeight = ImGui.getTextLineHeight();
      int displayableLines = (int) (ImGui.getContentRegionAvailY() / lineHeight + 0.5f);
      int scrollIndex = (int) (ImGui.getScrollY() / ImGui.getScrollMaxY() * (viewerSource.size() - displayableLines));

      int buffer = 40;
      ImDrawList drawList = ImGui.getWindowDrawList();

      for (int i = 0; i < viewerSource.size(); i++) {
        if (i < scrollIndex - buffer) {
          ImGui.dummy(0, lineHeight);
        } else if (i >= scrollIndex + displayableLines + 2 * buffer) {
          ImGui.dummy(0, lineHeight);
        } else {
          if (selectedFrame.getLineNumber() == i + 1) {
            drawList.addRect(ImGui.getCursorScreenPos().x, ImGui.getCursorScreenPos().y - 2,
                ImGui.getCursorScreenPos().x + ImGui.getContentRegionAvailX(), ImGui.getCursorScreenPos().y + lineHeight + 2,
                Colors.CurrentLine, 0, 0, 2);
          }
          ImGui.text("%4d".formatted(i + 1));
          ImGui.sameLine();
          PrintHighlighted(viewerSource.get(i));
        }
        if (moveToLine && selectedFrame.getLineNumber() == i + 1) {
          ImGui.setScrollHereY();
          moveToLine = false;
        }
      }
    } else {
      ImGui.text("No source code available.");
    }
  }

  private boolean isBlockComment = false;

  private void PrintHighlighted(String line) {
    int drawn = 0;
    int current = 0;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      // block comment
      if (c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '*') {
        isBlockComment = true;
        ImGui.text(line.substring(drawn, i));
        drawn = i;
        ImGui.sameLine(0, 0);
      }

      if (c == '*' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
        isBlockComment = false;
        ImGui.textColored(Colors.Comments, line.substring(drawn, i + 2));
        ImGui.sameLine(0, 0);
        drawn = i + 2;
      }

      if (isBlockComment) {
        continue;
      }
      
      // single line comment
      if (c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
        ImGui.text(line.substring(drawn, i));
        ImGui.sameLine(0, 0);
        ImGui.textColored(Colors.Comments, line.substring(i, line.length()));
        return;
      }

      // keywords
      if (c == ' ' || c == '\t' || c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ';' || c == ',' 
        || c == '.' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '<' || c == '>' || c == '=' || c == '!' 
        || c == '&' || c == '|' || c == '^' || c == '~' || c == '?' || c == ':' || c == '@' || c == '#' || c == '$' || c == '_' 
        || c == '"' || c == '\'' || c == '\\' || c == '\n' || c == '\r' || c == '\f' || c == '\b' || c == '\0' || c == '/' 
      ) {
        if (javaKeywords.contains(line.substring(current, i))) {
          ImGui.text(line.substring(drawn, current));
          ImGui.sameLine(0, 0);
          drawn = i;
          ImGui.textColored(Colors.Keyword, line.substring(current, i));
          ImGui.sameLine(0, 0);
        }
        current = i + 1;
      }
    }
    if (isBlockComment) {
      ImGui.textColored(Colors.Comments, line.substring(drawn, line.length()));
    } else {
      ImGui.text(line.substring(drawn, line.length()));
    }
  }
}
