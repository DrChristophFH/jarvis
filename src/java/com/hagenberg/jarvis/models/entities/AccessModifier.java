package com.hagenberg.jarvis.models.entities;

/**
 * Enum for access modifiers. Same order as in JVM.
 * <p>
 * PUBLIC = 0x0001 PRIVATE = 0x0002 PROTECTED = 0x0004 STATIC = 0x0008 FINAL = 0x0010 VOLATILE = 0x0040 TRANSIENT = 0x0080
 * SYNTHETIC = 0x1000 ENUM = 0x4000
 * </p>
 * 
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.5-200-A.1">JVM Spec</a>
 */
public class AccessModifier {
  private final int accessModifier;

  public AccessModifier(int accessModifier) {
    this.accessModifier = accessModifier;
  }

  public int getNumber() {
    return accessModifier;
  }

  public boolean isPublic() {
    return (accessModifier & 0x0001) != 0;
  }

  public boolean isPrivate() {
    return (accessModifier & 0x0002) != 0;
  }

  public boolean isProtected() {
    return (accessModifier & 0x0004) != 0;
  }

  public boolean isStatic() {
    return (accessModifier & 0x0008) != 0;
  }

  public boolean isFinal() {
    return (accessModifier & 0x0010) != 0;
  }

  public boolean isVolatile() {
    return (accessModifier & 0x0040) != 0;
  }

  public boolean isTransient() {
    return (accessModifier & 0x0080) != 0;
  }

  public boolean isSynthetic() {
    return (accessModifier & 0x1000) != 0;
  }

  public boolean isEnum() {
    return (accessModifier & 0x4000) != 0;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (isPublic()) {
      sb.append("public ");
    }
    if (isPrivate()) {
      sb.append("private ");
    }
    if (isProtected()) {
      sb.append("protected ");
    }
    if (isStatic()) {
      sb.append("static ");
    }
    if (isFinal()) {
      sb.append("final ");
    }
    if (isVolatile()) {
      sb.append("volatile ");
    }
    if (isTransient()) {
      sb.append("transient ");
    }
    if (isSynthetic()) {
      sb.append("synthetic ");
    }
    if (isEnum()) {
      sb.append("enum ");
    }

    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    } else {
      sb.append("package-private");
    }

    return sb.toString();
  }

  public static String toString(int accessModifier) {
    return new AccessModifier(accessModifier).toString();
  }
}
