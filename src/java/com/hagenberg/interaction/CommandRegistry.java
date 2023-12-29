package com.hagenberg.interaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
  private Map<Class<?>, List<Command<?>>> actionsByType = new HashMap<>();
  private static CommandRegistry instance;

  private CommandRegistry() {
  }

  public static CommandRegistry getInstance() {
    if (instance == null) {
      instance = new CommandRegistry();
    }
    return instance;
  }


  /**
   * Register a command that is available for all objects of a given type
   * @param type the type of objects for which the command is available
   * @param action the action to be executed
   * @param name the name of the command
   */
  public void registerCommand(Class<?> type, Action<?> action, String name) {
    Command<?> command = new Command<>(name, action);
    actionsByType.computeIfAbsent(type, k -> new ArrayList<>()).add(command);
  }

  public <T> List<Command<T>> getCommandsForObject(Class<T> clazz) {
    List<Command<?>> commands = actionsByType.getOrDefault(clazz, Collections.emptyList());

    List<Command<T>> typedCommands = new ArrayList<>();
    for (Command<?> command : commands) {
        // Unchecked cast - assumes that the commands are correctly registered
        @SuppressWarnings("unchecked")
        Command<T> typedCommand = (Command<T>) command;
        typedCommands.add(typedCommand);
    }
    return typedCommands;
  }
}