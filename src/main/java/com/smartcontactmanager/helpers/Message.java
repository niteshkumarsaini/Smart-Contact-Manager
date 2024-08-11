package com.smartcontactmanager.helpers;

public class Message {
    private String name;
    private String type;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Message(String name, String type) {
        this.name = name;
        this.type = type;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Message() {
    }
    @Override
    public String toString() {
        return "Message [name=" + name + ", type=" + type + "]";
    }
    

    
}
