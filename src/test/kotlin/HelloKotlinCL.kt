import components.Context
import enums.RuntimeMode
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL30.CL_MEM_READ_ONLY
import org.lwjgl.opencl.CL30.CL_MEM_WRITE_ONLY

fun main() {
	HelloOpenCL.main()
}

object HelloOpenCL {
	private const val source =
		"kernel void sum(global const short *a, global const short *b, global unsigned char *answer) {\n" +
				"unsigned int gid = get_global_id(0);\n" +
				"answer[gid] = a[gid] + b[gid];\n" +
				"}"

	private const val n = 10000
	private val a = BufferUtils.createFloatBuffer(n)
	private val b = BufferUtils.createFloatBuffer(n)
	private val c = BufferUtils.createByteBuffer(100)

	fun main() {
		var started = -1L
		RuntimeCL.mode = RuntimeMode.PERFORMANT
		RuntimeCL.use {
			Context(it.firstPlatform().firstDevice()).use { ctx ->
				val queue = ctx.createCommandQueue()
				val program = ctx.createProgram(source).build()
				val kernel = program.createKernel("sum")

				for (i in 0 until n) {
					a.put(i.toFloat())
					b.put(i.toFloat())
				}
				a.flip()
				b.flip()

				val amo = ctx.createMemObject(CL_MEM_READ_ONLY, n, Float.SIZE_BYTES, a)
				val bmo = ctx.createMemObject(CL_MEM_READ_ONLY, n, Float.SIZE_BYTES, b)
				val cmo = ctx.createMemObject(CL_MEM_WRITE_ONLY, c.capacity(), Byte.SIZE_BYTES, c)
				queue.enqWriteBuffer(amo)
				queue.enqWriteBuffer(bmo)

				kernel.arg(amo).arg(bmo).arg(cmo)

				started = System.currentTimeMillis()
				queue.enqNDRangeKernel(kernel, 1, c.capacity().toLong())
				queue.enqReadBuffer(cmo)
				queue.finish()

				for (i in 0..3) {
					println(i.toString() + ": " + c[i])
				}
				println("Time: " + (System.currentTimeMillis() - started) + " ms")
			}
		}
	}
}