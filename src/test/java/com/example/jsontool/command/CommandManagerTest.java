package com.example.jsontool.command;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandManagerTest {

    private final List<String> log = new ArrayList<>();
    private final CommandManager manager = new CommandManager();

    @Test
    void undoesCommandsInReverseOrder() {
        manager.executeCommand(new RecordingCommand("A", true));
        manager.executeCommand(new RecordingCommand("B", true));

        assertThat(manager.undoLastCommand()).isTrue();
        assertThat(manager.undoLastCommand()).isTrue();

        assertThat(log).containsExactly("do A", "do B", "undo B", "undo A");
        assertThat(manager.historySize()).isZero();
    }

    @Test
    void failedCommandIsNotAddedToHistory() {
        assertThat(manager.executeCommand(new RecordingCommand("A", false))).isFalse();

        assertThat(manager.historySize()).isZero();
        assertThat(manager.undoLastCommand()).isFalse();
    }

    private class RecordingCommand implements Command {
        private final String name;
        private final boolean succeeds;

        RecordingCommand(String name, boolean succeeds) {
            this.name = name;
            this.succeeds = succeeds;
        }

        @Override
        public boolean execute() {
            log.add("do " + name);
            return succeeds;
        }

        @Override
        public boolean undo() {
            log.add("undo " + name);
            return true;
        }
    }
}
