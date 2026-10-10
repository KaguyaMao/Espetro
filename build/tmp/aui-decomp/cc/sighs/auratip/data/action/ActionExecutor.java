/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package cc.sighs.auratip.data.action;

import cc.sighs.auratip.api.action.ActionHandlers;
import cc.sighs.auratip.api.action.Actions;
import cc.sighs.auratip.data.action.Action;
import java.util.HashMap;
import net.minecraft.client.Minecraft;

public enum ActionExecutor implements Action.ActionVisitor<Void>
{
    INSTANCE;


    public static void execute(Action action) {
        if (action == null) {
            return;
        }
        if (action instanceof Action.RunCommand) {
            Action.RunCommand rc = (Action.RunCommand)action;
            INSTANCE.visitRunCommand(rc);
            return;
        }
        if (action instanceof Action.SimulateKey) {
            Action.SimulateKey sk = (Action.SimulateKey)action;
            INSTANCE.visitSimulateKey(sk);
            return;
        }
        if (action instanceof Action.ScriptAction) {
            Action.ScriptAction sa = (Action.ScriptAction)action;
            INSTANCE.visitScript(sa);
            return;
        }
        Actions.executeTyped(action);
    }

    @Override
    public Void visitRunCommand(Action.RunCommand action) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ != null) {
            String command = action.command();
            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            minecraft.f_91074_.f_108617_.m_246623_(command);
        }
        return null;
    }

    @Override
    public Void visitSimulateKey(Action.SimulateKey action) {
        int keyCode = action.keyCode();
        Minecraft minecraft = Minecraft.m_91087_();
        if (keyCode == 256) {
            minecraft.m_91152_(null);
        }
        return null;
    }

    @Override
    public Void visitScript(Action.ScriptAction action) {
        HashMap params = new HashMap();
        if (action.params() != null) {
            params.putAll(action.params());
        }
        ActionHandlers.execute(action.type(), params);
        return null;
    }
}

