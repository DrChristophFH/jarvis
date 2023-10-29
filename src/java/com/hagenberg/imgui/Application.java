package com.hagenberg.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

public class Application {
  private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
  private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

  private String glslVersion = null;

  /**
   * Pointer to the native GLFW window.
   */
  protected long handle;

  /**
   * Method to initialize application.
   *
   * @param config configuration object with basic window information
   */
  protected final void init(final Configuration config) {
    initWindow(config);
    initImGui(config);

    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer comp = stack.mallocInt(1);

      // Load image using STB
      CharSequence path = new File("resources\\icon.png").getAbsolutePath();
      ByteBuffer image = STBImage.stbi_load(path, w, h, comp, 4);
      if (image == null) {
        throw new RuntimeException("Failed to load the icon image: " + STBImage.stbi_failure_reason());
      }

      // Create a GLFWImage and set its width, height, and pixels data
      GLFWImage icon = GLFWImage.malloc();
      icon.set(w.get(0), h.get(0), image);

      // Set the GLFWImage as the icon for the window
      GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
      iconBuffer.put(0, icon);
      
      imGuiGlfw.setIcon(iconBuffer);
    }
    
    imGuiGlfw.init(handle, true);
    imGuiGl3.init(glslVersion);
  }

  /**
   * Method to create and initialize GLFW window.
   *
   * @param config configuration object with basic window information
   */
  protected void initWindow(final Configuration config) {
    GLFWErrorCallback.createPrint(System.err).set();

    if (!GLFW.glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    decideGlGlslVersions();

    GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
    GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
    GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY, GLFW.GLFW_FALSE);
    handle = GLFW.glfwCreateWindow(config.getWidth(), config.getHeight(), config.getTitle(), MemoryUtil.NULL,
        MemoryUtil.NULL);

    if (handle == MemoryUtil.NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    try (MemoryStack stack = MemoryStack.stackPush()) {
      final IntBuffer pWidth = stack.mallocInt(1); // int*
      final IntBuffer pHeight = stack.mallocInt(1); // int*

      GLFW.glfwGetWindowSize(handle, pWidth, pHeight);
      final GLFWVidMode vidmode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
      GLFW.glfwSetWindowPos(handle, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
    }

    GLFW.glfwMakeContextCurrent(handle);
    GLFW.glfwSwapInterval(0);

    if (config.isFullScreen()) {
      GLFW.glfwMaximizeWindow(handle);
    }

    GL.createCapabilities();
  }

  private void decideGlGlslVersions() {
    final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
    if (isMac) {
      glslVersion = "#version 150";
      GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
      GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
      GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); // 3.2+ only
      GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE); // Required on Mac
    } else {
      glslVersion = "#version 130";
      GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
      GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
    }
  }

  /**
   * Method to initialize Dear ImGui context. Could be overridden to do custom
   * Dear ImGui setup before application start.
   *
   * @param config configuration object with basic window information
   */
  protected void initImGui(final Configuration config) {
    ImGui.createContext();

    final ImGuiIO io = ImGui.getIO();
    io.addConfigFlags(ImGuiConfigFlags.DockingEnable); // Enable Docking
    io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable); // Enable Multi-Viewport / Platform Windows
    io.setConfigViewportsNoAutoMerge(true);
  }

  /**
   * Method called once, before application run loop.
   */
  protected void preRun() {
  }

  /**
   * Main application loop.
   */
  protected final void run() {
    while (!GLFW.glfwWindowShouldClose(handle)) {
      startFrame();
      preProcess();
      process();
      postProcess();
      endFrame();
    }
  }

  /**
   * Method called at the beginning of the main cycle.
   * It clears OpenGL buffer and starts an ImGui frame.
   */
  protected void startFrame() {
    GL32.glClearColor(0, 0, 0, 1);
    GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
    imGuiGlfw.newFrame();
    ImGui.newFrame();
  }

  /**
   * Method called every frame, before calling {@link #process()} method.
   */
  protected void preProcess() {
  }

  /**
   * Main application logic, called every frame.
   */
  public void process() {

  }

  /**
   * Method called every frame, after calling {@link #process()} method.
   */
  protected void postProcess() {
  }

  /**
   * Method called in the end of the main cycle.
   * It renders ImGui and swaps GLFW buffers to show an updated frame.
   */
  protected void endFrame() {
    ImGui.render();
    imGuiGl3.renderDrawData(ImGui.getDrawData());

    if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
      final long backupWindowPtr = GLFW.glfwGetCurrentContext();
      ImGui.updatePlatformWindows();
      ImGui.renderPlatformWindowsDefault();
      GLFW.glfwMakeContextCurrent(backupWindowPtr);
    }

    GLFW.glfwSwapBuffers(handle);
    GLFW.glfwPollEvents();
  }

  /**
   * Method called once, after application run loop.
   */
  protected void postRun() {
  }

  /**
   * Method to dispose all used application resources and destroy its window.
   */
  protected final void dispose() {
    imGuiGl3.dispose();
    imGuiGlfw.dispose();
    disposeImGui();
    disposeWindow();
  }

  /**
   * Method to destroy Dear ImGui context.
   */
  protected void disposeImGui() {
    ImGui.destroyContext();
  }

  /**
   * Method to destroy GLFW window.
   */
  protected void disposeWindow() {
    Callbacks.glfwFreeCallbacks(handle);
    GLFW.glfwDestroyWindow(handle);
    GLFW.glfwTerminate();
    Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
  }

  /**
   * @return pointer to the native GLFW window
   */
  public final long getHandle() {
    return handle;
  }

  /**
   * Entry point of any ImGui application. Use it to start the application loop.
   *
   * @param app application instance to run
   */
  public static void launch(final Application app, final Configuration config) {
    app.init(config);
    app.preRun();
    app.run();
    app.postRun();
    app.dispose();
  }
}