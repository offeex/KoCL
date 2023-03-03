package components

import interfaces.Identifiable
import org.lwjgl.opencl.CL30
import utils.CLUtil

class Kernel(program: Program, name: String) : AutoCloseable, Identifiable(CLUtil.createKernel(program, name)) {
	val args: MutableList<MemObject> = mutableListOf()
	var argIndex = 0

	fun arg(mo: MemObject, index: Int? = null): Kernel {
		val i = index ?: argIndex++
		CL30.clSetKernelArg(id, i, CLUtil.toPointerBuffer(mo.id))
		args.add(i, mo)
		return this
	}

	override fun close() {
		CL30.clReleaseKernel(id)
	}
}