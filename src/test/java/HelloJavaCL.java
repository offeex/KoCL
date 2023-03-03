
import me.offeex.kocl.RuntimeCL;
import me.offeex.kocl.components.*;
import me.offeex.kocl.enums.RuntimeMode;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opencl.CL10.CL_MEM_READ_ONLY;
import static org.lwjgl.opencl.CL10.CL_MEM_WRITE_ONLY;

public class HelloJavaCL {
    static String source = "kernel void sum(global const float *a, global const float *b, global unsigned char *answer) {\n" +
            "unsigned int gid = get_global_id(0);\n" +
            "answer[gid] = a[gid] + b[gid];\n" +
            "}";
    static int n = 10000000;
    static private FloatBuffer a = BufferUtils.createFloatBuffer(n), b = BufferUtils.createFloatBuffer(n);
    static private ByteBuffer c = BufferUtils.createByteBuffer(n);

    public static void main(String[] args) {
        try (RuntimeCL runtime = RuntimeCL.INSTANCE) {
            runtime.setMode(RuntimeMode.PERFORMANT);
            try (Context ctx = new Context(runtime.firstPlatform().firstDevice())) {
                CommandQueue queue = ctx.createCommandQueue();
                Program program = ctx.createProgram(source).build("");
                Kernel kernel = program.createKernel("sum");

                for (int i = 0; i < n; i++) {
                    a.put(i);
                    b.put(i);
                }
                a.flip();
                b.flip();

                MemObject amo = ctx.createMemObject(CL_MEM_READ_ONLY, n, Float.BYTES, a);
                MemObject bmo = ctx.createMemObject(CL_MEM_READ_ONLY, n, Float.BYTES, b);
                MemObject cmo = ctx.createMemObject(CL_MEM_WRITE_ONLY, n, Byte.BYTES, c);
                queue.enqWriteBuffer(amo, true, 0);
                queue.enqWriteBuffer(bmo, true, 0);

                kernel.arg(amo, 0).arg(bmo, 1).arg(cmo, 2);

                long started = System.currentTimeMillis();
                queue.enqNDRangeKernel(kernel, 1, c.capacity(), null, null, null, null);
                queue.enqReadBuffer(cmo, true, 0);

                for (int i = 0; i < 4; i++) {
                    System.out.println(i + ": " + c.get(i));
                }
                System.out.println("Time: " + (System.currentTimeMillis() - started) + " ms");
            }
        }
    }
}
