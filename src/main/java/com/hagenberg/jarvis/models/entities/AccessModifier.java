package com.hagenberg.jarvis.models.entities;

/**
 * Enum for access modifiers. Same order as in JVM.
 * <p>
 *     PUBLIC = 0x0001
 *     PRIVATE = 0x0002
 *     PROTECTED = 0x0004
 *     STATIC = 0x0008
 *     FINAL = 0x0010
 *     VOLATILE = 0x0040
 *     TRANSIENT = 0x0080
 *     SYNTHETIC = 0x1000
 *     ENUM = 0x4000
 * </p>
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.5-200-A.1">JVM Spec</a>
 */
public enum AccessModifier {
    PUBLIC,
    PRIVATE,
    PROTECTED,
    STATIC,
    FINAL,
    VOLATILE,
    TRANSIENT,
    SYNTHETIC,
    ENUM;

    public boolean isPublic(int accessModifier) {
        return (accessModifier & 0x0001) != 0;
    }

    public boolean isPrivate(int accessModifier) {
        return (accessModifier & 0x0002) != 0;
    }

    public boolean isProtected(int accessModifier) {
        return (accessModifier & 0x0004) != 0;
    }

    public boolean isStatic(int accessModifier) {
        return (accessModifier & 0x0008) != 0;
    }

    public boolean isFinal(int accessModifier) {
        return (accessModifier & 0x0010) != 0;
    }

    public boolean isVolatile(int accessModifier) {
        return (accessModifier & 0x0040) != 0;
    }

    public boolean isTransient(int accessModifier) {
        return (accessModifier & 0x0080) != 0;
    }

    public boolean isSynthetic(int accessModifier) {
        return (accessModifier & 0x1000) != 0;
    }

    public boolean isEnum(int accessModifier) {
        return (accessModifier & 0x4000) != 0;
    }
}
