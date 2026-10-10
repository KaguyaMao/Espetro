/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.api.ITransformer
 *  cpw.mods.modlauncher.api.ITransformer$Target
 *  cpw.mods.modlauncher.api.ITransformerVotingContext
 *  cpw.mods.modlauncher.api.TransformerVoteResult
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FieldNode
 *  org.objectweb.asm.tree.InsnNode
 *  org.objectweb.asm.tree.MethodInsnNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.VarInsnNode
 */
package LOL_141.vehicle_addition.coremod;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class SbwTerrainCompactTransformer
implements ITransformer<ClassNode> {
    public static final String TARGET_VEHICLE = "com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity";
    public static final String TARGET_MOTION = "com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleMotionUtils";
    public static final String TARGET_SEAT = "com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo";
    private static final String SEAT_FIELD_NAME = "stabilizer";
    private static final String[] METHODS_TO_HOLLOW = new String[]{"terrainCompact", "handleClientSync"};
    private static final String RELAX_METHOD = "checkBottomSupportRatio";
    private static final String RELAX_OWNER = "LOL_141/vehicle_addition/coremod/SbwCollisionRelax";
    private static final String RELAX_NAME = "checkBottomSupportRatio";
    private static final String RELAX_DESC = "(Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;Lcom/atsuishio/superbwarfare/tools/OBB;)Lkotlin/Pair;";

    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean anyPatched = false;
        StringBuilder patchedLog = new StringBuilder();
        if (TARGET_SEAT.equals(input.name)) {
            boolean has = false;
            for (FieldNode f : input.fields) {
                if (!SEAT_FIELD_NAME.equals(f.name)) continue;
                has = true;
                break;
            }
            if (!has) {
                input.fields.add(new FieldNode(1, SEAT_FIELD_NAME, "Z", null, (Object)1));
                anyPatched = true;
                patchedLog.append("+").append(SEAT_FIELD_NAME).append(' ');
            }
        }
        for (MethodNode method : input.methods) {
            boolean matched = false;
            for (String methodName : METHODS_TO_HOLLOW) {
                if (!methodName.equals(method.name)) continue;
                SbwTerrainCompactTransformer.hollowOut(method);
                matched = true;
                break;
            }
            if (!matched && "checkBottomSupportRatio".equals(method.name)) {
                SbwTerrainCompactTransformer.replaceWithDelegate(method, RELAX_OWNER, "checkBottomSupportRatio", RELAX_DESC);
                matched = true;
            }
            if (!matched) continue;
            anyPatched = true;
            patchedLog.append(method.name).append(' ');
        }
        if (anyPatched) {
            String msg = "[VehicleTerrain] ASM patched " + input.name + ": [" + patchedLog.toString().trim() + "]";
            System.out.println(msg);
            SbwTerrainCompactTransformer.writeMarker(input.name, patchedLog.toString().trim());
        } else {
            System.out.println("[VehicleTerrain] ASM no target methods in " + input.name);
        }
        return input;
    }

    private static void hollowOut(MethodNode method) {
        method.instructions.clear();
        method.instructions.add((AbstractInsnNode)new InsnNode(177));
        method.tryCatchBlocks.clear();
        method.localVariables.clear();
        method.visibleLocalVariableAnnotations = null;
        method.invisibleLocalVariableAnnotations = null;
        method.maxStack = 0;
        method.maxLocals = 0;
    }

    private static void replaceWithDelegate(MethodNode method, String owner, String name, String desc) {
        int n = SbwTerrainCompactTransformer.paramCount(desc);
        method.instructions.clear();
        for (int i = 0; i < n; ++i) {
            method.instructions.add((AbstractInsnNode)new VarInsnNode(25, i));
        }
        method.instructions.add((AbstractInsnNode)new MethodInsnNode(184, owner, name, desc, false));
        method.instructions.add((AbstractInsnNode)new InsnNode(SbwTerrainCompactTransformer.returnOpcode(desc)));
        method.tryCatchBlocks.clear();
        method.localVariables.clear();
        method.visibleLocalVariableAnnotations = null;
        method.invisibleLocalVariableAnnotations = null;
        method.maxStack = Math.max(n + 1, 1);
        method.maxLocals = n;
    }

    private static int paramCount(String desc) {
        int end = desc.indexOf(41);
        if (end < 0) {
            return 0;
        }
        String params = desc.substring(1, end);
        int count = 0;
        for (int i = 0; i < params.length(); ++i) {
            char c = params.charAt(i);
            if (c == '[') continue;
            if (c == 'L') {
                ++count;
                if ((i = params.indexOf(59, i)) >= 0) continue;
                break;
            }
            ++count;
        }
        return count;
    }

    private static int returnOpcode(String desc) {
        return switch (desc.charAt(desc.length() - 1)) {
            case 'V' -> 177;
            case 'D' -> 175;
            case 'F' -> 174;
            case 'J' -> 173;
            default -> 176;
        };
    }

    private static void writeMarker(String className, String methods) {
        try {
            String safe = className.replace('/', '_').replace('.', '_');
            File marker = new File(System.getProperty("java.io.tmpdir"), "vehicle_addition_asm_" + safe + ".txt");
            Files.writeString(marker.toPath(), (CharSequence)("patched=" + className + "\nmethods=" + methods + "\ntime=" + System.currentTimeMillis() + "\n"), new OpenOption[0]);
        }
        catch (Exception e) {
            System.out.println("[VehicleTerrain] ASM marker write failed: " + e);
        }
    }

    public TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    public Set<ITransformer.Target> targets() {
        return Set.of(ITransformer.Target.targetClass((String)TARGET_VEHICLE), ITransformer.Target.targetClass((String)TARGET_MOTION), ITransformer.Target.targetClass((String)TARGET_SEAT));
    }
}

