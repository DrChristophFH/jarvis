package com.hagenberg.jarvis.models.entities.wrappers;

import java.util.List;

import com.sun.jdi.ArrayReference;

public class JArrayReference extends JObjectReference {
  private final JContent[] contents;
  private final JType arrayContentType;

  public JArrayReference(ArrayReference jdiArrayReference, JType type, JType arrayContentType) {
    super(jdiArrayReference, type);
    contents = new JContent[jdiArrayReference.length()];
    this.arrayContentType = arrayContentType;
  }

  public void setContent(int index, JValue arrayMember) {
    JContent content = contents[index];
    if (content == null) {
      content = new JContent(index, arrayMember);
      contents[index] = content;
    } else {
      content.setValue(arrayMember);
    }
  }

  public JContent getContent(int index) {
    return contents[index];
  }

  public ArrayReference getJdiArrayReference() {
    return (ArrayReference) getJdiObjectReference();
  }

  public List<JContent> getContent() {
    return List.of(contents);
  }

  public JType getArrayContentType() {
    return arrayContentType;
  }
}
