package com.example.jsontool.command;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Invoker патерну Command. Історія спільна для всього застосунку,
 * тому доступ до неї синхронізовано.
 */
@Component
public class CommandManager {
    private final Deque<Command> history = new ArrayDeque<>();

    public synchronized boolean executeCommand(Command command) {
        if (command.execute()) {
            history.push(command);
            return true;
        }
        return false;
    }

    public synchronized boolean undoLastCommand() {
        Command lastCommand = history.poll();
        return lastCommand != null && lastCommand.undo();
    }

    public synchronized int historySize() {
        return history.size();
    }
}
