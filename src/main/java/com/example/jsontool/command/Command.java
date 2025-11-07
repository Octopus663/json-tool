package com.example.jsontool.command;

public interface Command {

    boolean execute();
    boolean undo();
}