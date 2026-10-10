import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Patches SuperbWarfare 0.8.9.1-hotfix so that fcp-1.1.1 (compiled against an
 * older SBW API) works: adds a bridge overload
 *   DamageModifier.custom(kotlin.jvm.functions.Function2)
 * which adapts the Kotlin Function2<DamageSource,Float,Float> lambda to the
 * current CustomDamageModifier interface, plus the adapter class
 * DamageModifier$Function2Adapter.
 */
public class PatchSbw {
    static final String DM = "com/atsuishio/superbwarfare/entity/vehicle/damage/DamageModifier";
    static final String ADAPTER = DM + "$Function2Adapter";
    static final String CUSTOM_IFACE = DM + "$CustomDamageModifier";
    static final String FN2 = "kotlin/jvm/functions/Function2";
    static final String ENTITY = "net/minecraft/world/entity/Entity";
    static final String DAMAGE_SOURCE = "net/minecraft/world/damagesource/DamageSource";

    public static void main(String[] args) throws Exception {
        Path in = Paths.get(args[0]);
        Path out = Paths.get(args[1]);
        Map<String, byte[]> entries = new LinkedHashMap<>();
        try (InputStream fis = Files.newInputStream(in);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                entries.put(e.getName(), zis.readAllBytes());
            }
        }
        byte[] dm = entries.get(DM + ".class");
        if (dm == null) throw new IllegalStateException("DamageModifier.class not found in " + in);
        entries.put(DM + ".class", patchDamageModifier(dm));
        entries.put(ADAPTER + ".class", makeAdapter());
        try (OutputStream fos = Files.newOutputStream(out);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (Map.Entry<String, byte[]> en : entries.entrySet()) {
                zos.putNextEntry(new ZipEntry(en.getKey()));
                zos.write(en.getValue());
                zos.closeEntry();
            }
        }
        System.out.println("patched: " + in + " -> " + out);
    }

    static byte[] patchDamageModifier(byte[] original) {
        ClassReader cr = new ClassReader(original);
        ClassWriter cw = new ClassWriter(cr, 0);
        cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public void visitEnd() {
                MethodVisitor mv = super.visitMethod(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                        "custom",
                        "(L" + FN2 + ";)" + "L" + DM + ";",
                        null, null);
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitTypeInsn(Opcodes.NEW, ADAPTER);
                mv.visitInsn(Opcodes.DUP);
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ADAPTER, "<init>", "(L" + FN2 + ";)V", false);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, DM, "custom",
                        "(L" + CUSTOM_IFACE + ";)" + "L" + DM + ";", false);
                mv.visitInsn(Opcodes.ARETURN);
                mv.visitMaxs(4, 2);
                mv.visitEnd();
                super.visitEnd();
            }
        }, 0);
        return cw.toByteArray();
    }

    static byte[] makeAdapter() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, ADAPTER,
                null, "java/lang/Object", new String[]{CUSTOM_IFACE});
        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, "fn", "L" + FN2 + ";", null, null).visitEnd();

        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(L" + FN2 + ";)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, ADAPTER, "fn", "L" + FN2 + ";");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "compute",
                "(L" + ENTITY + ";L" + DAMAGE_SOURCE + ";F)F", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, ADAPTER, "fn", "L" + FN2 + ";");
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitVarInsn(Opcodes.FLOAD, 3);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, FN2, "invoke",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
        mv.visitInsn(Opcodes.DUP);
        org.objectweb.asm.Label nullLabel = new org.objectweb.asm.Label();
        mv.visitJumpInsn(Opcodes.IFNULL, nullLabel);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
        mv.visitInsn(Opcodes.FRETURN);
        mv.visitLabel(nullLabel);
        mv.visitInsn(Opcodes.POP);
        mv.visitVarInsn(Opcodes.FLOAD, 3);
        mv.visitInsn(Opcodes.FRETURN);
        mv.visitMaxs(3, 4);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }
}
