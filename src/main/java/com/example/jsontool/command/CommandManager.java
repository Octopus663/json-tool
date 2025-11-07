package com.example.jsontool.command;

import org.springframework.stereotype.Component;
import java.util.Stack;

@Component
public class CommandManager {
    private final Stack<Command> history = new Stack<>();

    public void executeCommand(Command command) {
        if (command.execute()) {
            history.push(command);
        }
    }

    public void undoLastCommand() {
        if (!history.isEmpty()) {
            Command lastCommand = history.pop();
            lastCommand.undo();
        }
    }
}