/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecrell.terminalconsole.TerminalConsoleAppender
 *  org.jline.reader.Completer
 *  org.jline.reader.EndOfFileException
 *  org.jline.reader.LineReader
 *  org.jline.reader.LineReader$Option
 *  org.jline.reader.LineReaderBuilder
 *  org.jline.reader.UserInterruptException
 *  org.jline.terminal.Terminal
 */
package net.minecraftforge.server.console;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.server.console.ConsoleCommandCompleter;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

public final class TerminalHandler {
    private TerminalHandler() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean handleCommands(DedicatedServer server) {
        Terminal terminal = TerminalConsoleAppender.getTerminal();
        if (terminal == null) {
            return false;
        }
        LineReader reader = LineReaderBuilder.builder().appName("Forge").terminal(terminal).completer((Completer)new ConsoleCommandCompleter(server)).build();
        reader.setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION);
        reader.unsetOpt(LineReader.Option.INSERT_TAB);
        TerminalConsoleAppender.setReader((LineReader)reader);
        try {
            while (!server.m_129918_() && server.m_130010_()) {
                String line;
                try {
                    line = reader.readLine("> ");
                }
                catch (EndOfFileException ignored) {
                    continue;
                }
                if (line == null) {
                    break;
                }
                if ((line = line.trim()).isEmpty()) continue;
                server.m_139645_(line, server.m_129893_());
            }
        }
        catch (UserInterruptException e) {
            server.m_7570_(true);
        }
        finally {
            TerminalConsoleAppender.setReader(null);
        }
        return true;
    }
}

