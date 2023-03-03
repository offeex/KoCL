package components

import interfaces.Identifiable
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL30.*
import utils.CLUtil
import java.nio.*

class CommandQueue(context: Context) : AutoCloseable, Identifiable(CLUtil.createCommandQueue(context)) {

	fun enqWriteBuffer(mo: MemObject, block: Boolean = true, offset: Long = 0): CommandQueue {
		when (mo.hostBuffer) {
			is ByteBuffer -> clEnqueueWriteBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
			is ShortBuffer -> clEnqueueWriteBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
			is IntBuffer -> clEnqueueWriteBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
			is FloatBuffer -> clEnqueueWriteBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
			is DoubleBuffer -> clEnqueueWriteBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
		}
		return this
	}

	fun enqReadBuffer(mo: MemObject, block: Boolean = true, offset: Long = 0): CommandQueue {
		when (mo.hostBuffer) {
			is ByteBuffer -> clEnqueueReadBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
			is ShortBuffer -> clEnqueueReadBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
			is IntBuffer -> clEnqueueReadBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
			is FloatBuffer -> clEnqueueReadBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
			is DoubleBuffer -> clEnqueueReadBuffer(id, mo.id, block, offset, mo.hostBuffer, null, null)
		}
		return this
	}

	fun enqNDRangeKernel(
		kernel: Kernel,
		wDim: Int,
		gws: Long,
		lws: PointerBuffer? = null,
		gwo: PointerBuffer? = null,
		eWaitList: PointerBuffer? = null,
		e: PointerBuffer? = null
	): CommandQueue {
		val ws = CLUtil.toPointerBuffer(gws)
		CLUtil.validate { clEnqueueNDRangeKernel(id, kernel.id, wDim, gwo, ws, lws, eWaitList, e) }
		return this
	}

	fun flush(): CommandQueue {
		clFlush(id)
		return this
	}

	fun finish(): CommandQueue {
		clFinish(id)
		return this
	}

	override fun close() {
		clReleaseCommandQueue(id)
	}
}