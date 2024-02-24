package com.hagenberg.jarvis.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {
  public enum Level {
    INFO, WARNING, ERROR
  }

  public record LogEntry(String time, String message, Level level) {
    public String fullMessage() {
      return String.format("%s [%s] %s", time, level, message);
    }
  }

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
  private List<LogEntry> buffer = new ArrayList<>();
  private static Logger instance;

  public static Logger getInstance() {
    return instance == null ? instance = new Logger() : instance;
  }

  public void log(Level level, String text, Object... args) {
    String formattedText = String.format(text, args);
    String time = LocalDateTime.now().format(formatter);
    synchronized (buffer) {
      buffer.add(new LogEntry(time, formattedText, level));
    }
  }

  public void logInfo(String text, Object... args) {
    log(Level.INFO, text, args);
  }

  public void logWarning(String text, Object... args) {
    log(Level.WARNING, text, args);
  }

  public void logError(String text, Object... args) {
    log(Level.ERROR, text, args);
  }

  public List<LogEntry> getBuffer() {
    return buffer;
  }
}
