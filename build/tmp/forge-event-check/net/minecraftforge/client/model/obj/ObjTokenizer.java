/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  joptsimple.internal.Strings
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.obj;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import joptsimple.internal.Strings;
import org.jetbrains.annotations.Nullable;

public class ObjTokenizer
implements AutoCloseable {
    private final BufferedReader lineReader;

    public ObjTokenizer(InputStream inputStream) {
        this.lineReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
    }

    @Nullable
    public String[] readAndSplitLine(boolean ignoreEmptyLines) throws IOException {
        do {
            String currentLine;
            if ((currentLine = this.lineReader.readLine()) == null) {
                return null;
            }
            ArrayList lineParts = new ArrayList();
            if (currentLine.startsWith("#")) {
                currentLine = "";
            }
            if (currentLine.length() > 0) {
                boolean hasContinuation;
                do {
                    String tmp = (hasContinuation = currentLine.endsWith("\\")) ? currentLine.substring(0, currentLine.length() - 1) : currentLine;
                    Arrays.stream(tmp.split("[\t ]+")).filter(s -> !Strings.isNullOrEmpty((String)s)).forEach(lineParts::add);
                } while ((!hasContinuation || (currentLine = this.lineReader.readLine()) != null && currentLine.length() != 0 && !currentLine.startsWith("#")) && hasContinuation);
            }
            if (lineParts.size() <= 0) continue;
            return lineParts.toArray(new String[0]);
        } while (ignoreEmptyLines);
        return new String[0];
    }

    @Override
    public void close() throws IOException {
        this.lineReader.close();
    }
}

