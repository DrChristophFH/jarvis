package com.hagenberg.jarvis.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

public class ServiceProvider {
    private final Map<Class<?>, Object> dependencies = new HashMap<>();
    private final Set<Object> resolutionStack = new HashSet<>();

    private static ServiceProvider instance;

    private ServiceProvider() {
    }

    public static ServiceProvider getInstance() {
        if (instance == null) {
            instance = new ServiceProvider();
        }
        return instance;
    }

    public <T> void registerDependency(Class<T> type, Class<? extends T> implementationClass) {
        T instance = createInstance(implementationClass);
        dependencies.put(type, instance);
    }

    public <T> void registerDependency(Class<T> type, Supplier<T> factoryMethod) {
        dependencies.put(type, factoryMethod.get());
    }

    public <T> void registerDependency(Class<T> type, T instance) {
        dependencies.put(type, instance);
    }

    public <T> T getDependency(Class<T> type) {
        Object dependency = dependencies.get(type);
        if (dependency == null) {
            registerDependency(type, type);
            dependency = dependencies.get(type);
        }
        return type.cast(dependency);
    }

    public void removeDependency(Class<?> type) {
        dependencies.remove(type);
    }

    public <T> T createInstance(Class<? extends T> implementationClass) {
        try {
            Constructor<? extends T> constructor = findSuitableConstructor(implementationClass);
            if (constructor != null) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Object[] parameters = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    parameters[i] = dependencies.get(parameterType);
                }
                return constructor.newInstance(parameters);
            } else {
                throw new IllegalArgumentException("Failed to instantiate dependency for %s: No suitable constructor found"
                        .formatted(implementationClass.getName()));
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Failed to instantiate dependency for %s: Constructor threw exception"
                    .formatted(implementationClass.getName()), e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to instantiate dependency for %s: Constructor is from abstract class"
                    .formatted(implementationClass.getName()), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to instantiate dependency for %s: Constructor violates access"
                    .formatted(implementationClass.getName()), e);
        }
    }

    private <T> Constructor<? extends T> findSuitableConstructor(Class<? extends T> implementationClass) {
        Constructor<?>[] constructors = implementationClass.getDeclaredConstructors();
        List<Constructor<? extends T>> suitableConstructors = new ArrayList<>();
        for (Constructor<?> constructor : constructors) {
            if (isSuitableConstructor(constructor)) {
                suitableConstructors.add((Constructor<? extends T>) constructor);
            }
        }
        return suitableConstructors.isEmpty() ? null : suitableConstructors.get(0);
    }

    private boolean isSuitableConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            if (!parameterType.equals(ServiceProvider.class) && !dependencies.containsKey(parameterType)) {
                resolveDependency(parameterType);
            }
        }
        return true;
    }

    private <T> void resolveDependency(Class<T> type) {
        if (resolutionStack.contains(type)) {
            throw new CircularDependencyException("Circular dependency detected for " + type);
        }
        // keep track of the dependencies we are currently resolving
        resolutionStack.add(type);

        registerDependency(type, type);

        // remove the type from the resolution stack
        resolutionStack.remove(type);
    }

    public void clearDependencies() {
        dependencies.clear();
    }
}