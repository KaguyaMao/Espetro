/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  net.minecraft.server.dedicated.DedicatedServer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jline.reader.Candidate
 *  org.jline.reader.Completer
 *  org.jline.reader.LineReader
 *  org.jline.reader.ParsedLine
 */
package net.minecraftforge.server.console;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import java.util.concurrent.ExecutionException;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

final class ConsoleCommandCompleter
implements Completer {
    private static final Logger logger = LogManager.getLogger();
    private final DedicatedServer server;

    public ConsoleCommandCompleter(DedicatedServer server) {
        this.server = (DedicatedServer)Preconditions.checkNotNull((Object)server, (Object)"server");
    }

    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        boolean prefix;
        Object buffer = line.line();
        if (((String)buffer).isEmpty() || ((String)buffer).charAt(0) != '/') {
            buffer = "/" + (String)buffer;
            prefix = false;
        } else {
            prefix = true;
        }
        Object input = buffer;
        StringReader stringReader = new StringReader((String)input);
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        try {
            ParseResults results = this.server.m_129892_().m_82094_().parse(stringReader, (Object)this.server.m_129893_());
            Suggestions tabComplete = (Suggestions)this.server.m_129892_().m_82094_().getCompletionSuggestions(results).get();
            for (Suggestion suggestion : tabComplete.getList()) {
                String completion = suggestion.getText();
                if (completion.isEmpty()) continue;
                boolean hasPrefix = prefix || completion.charAt(0) != '/';
                Candidate candidate = new Candidate(hasPrefix ? completion : completion.substring(1));
                candidates.add(candidate);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException e) {
            logger.error("Failed to tab complete", (Throwable)e);
        }
    }
}

