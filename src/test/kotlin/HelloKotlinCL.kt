import me.offeex.kocl.CLRuntime
import me.offeex.kocl.utils.BuffUtil
import me.offeex.kocl.utils.CLUtil.tryValidate
import me.offeex.kocl.utils.CLUtil.validate
import me.offeex.kocl.utils.MemUtil
import org.lwjgl.BufferUtils
import org.lwjgl.opencl.CL30.*
import java.nio.IntBuffer

fun main() {
	HelloOpenCL.main()
}

object HelloOpenCL {
	private const val source =
		"kernel void sum(global const float *a, global const float *b, global float *c) {\n" +
				"unsigned int gid = get_global_id(0);\n" +
				"c[gid] = (cos(sin(sqrt(pow(a[gid], 2) / 0.8 + pow(b[gid], 2)))) / 0.4) / 0.3;\n" +
				"}"

	private const val n = 214748364
	private val a = BufferUtils.createFloatBuffer(n)
	private val b = BufferUtils.createFloatBuffer(n)
	private val c = BufferUtils.createFloatBuffer(n)

	private var device: Long = 0
	private var ctx: Long = 0
	private var cq: Long = 0
	private var kernel: Long = 0

	fun main() {
		var started: Long
		CLRuntime.use { rt ->
			setup(rt)
			init()

			val amo = rt.memObject { clCreateBuffer(ctx, CL_MEM_READ_ONLY.toLong(), MemUtil.intBytes(n), null) }
			val bmo = rt.memObject { clCreateBuffer(ctx, CL_MEM_READ_ONLY.toLong(), MemUtil.intBytes(n), null) }
			val cmo = rt.memObject { clCreateBuffer(ctx, CL_MEM_WRITE_ONLY.toLong(), MemUtil.intBytes(n), null) }
			clEnqueueWriteBuffer(cq, amo, true, 0, a, null, null)
			clEnqueueWriteBuffer(cq, bmo, true, 0, b, null, null)

			clSetKernelArg(kernel, 0, BuffUtil.pointer(amo))
			clSetKernelArg(kernel, 1, BuffUtil.pointer(bmo))
			clSetKernelArg(kernel, 2, BuffUtil.pointer(cmo))

			started = System.currentTimeMillis()
			clEnqueueNDRangeKernel(cq, kernel, 1, null, BuffUtil.pointer(n.toLong()), null, null, null)
			clEnqueueReadBuffer(cq, cmo, true, 0, c, null, null)
			clFinish(cq)

			for (i in 0..3) {
				println(i.toString() + ": " + c[i])
			}
			println("Time: " + (System.currentTimeMillis() - started) + " ms")
		}
	}

	private fun init() {
		for (i in 0 until n) {
			a.put(i.toFloat())
			b.put(i.toFloat())
		}
		a.flip()
		b.flip()
	}

	private fun setup(rt: CLRuntime) {
		val platforms = rt.stack.mallocPointer(1)
		validate { clGetPlatformIDs(platforms, null as IntBuffer?) }

		val devices = rt.stack.mallocPointer(1)
		validate { clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_GPU.toLong(), devices, null as IntBuffer?) }
		device = rt.device { devices[0] }

		ctx = rt.context { tryValidate { clCreateContext(null, devices, null, 0, it) } }
		cq = rt.commandQueue { tryValidate { clCreateCommandQueue(ctx, devices[0], CL_QUEUE_PROFILING_ENABLE.toLong(), it) } }

		val program = clCreateProgramWithSource(ctx, source, null)
		validate { clBuildProgram(program, devices, "", null, 0) }
		kernel = rt.kernel { tryValidate { clCreateKernel(program, "sum", it) } }

		clReleaseProgram(program)
	}
}