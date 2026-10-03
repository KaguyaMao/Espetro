import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VerifyClass {
	public static void main(String[] args) throws Exception {
		byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
		ClassReader cr = new ClassReader(bytes);
		CheckClassAdapter.verify(cr, false, new PrintWriter(System.out, true));
		System.out.println("VERIFY_OK");
	}
}