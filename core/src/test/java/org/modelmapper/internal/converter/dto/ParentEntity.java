package org.modelmapper.internal.converter.dto;

public class ParentEntity {

    private String name;
    private String secondName;
    private ChildEntity child;

    public ParentEntity() {
    }

    public ParentEntity(String name, String secondName, ChildEntity child) {
        this.name = name;
        this.secondName = secondName;
        this.child = child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public ChildEntity getChild() {
        return child;
    }

    public void setChild(ChildEntity child) {
        this.child = child;
    }

}
