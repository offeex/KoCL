package me.offeex.kocl.components

import me.offeex.kocl.interfaces.Identifiable
import org.lwjgl.opencl.CL30
import me.offeex.kocl.utils.CLUtil

class Program(private val ctx: Context, source: String) : AutoCloseable, Identifiable(CLUtil.createProgram(ctx, source)) {
	val kernels: MutableList<Kernel> = mutableListOf()

	fun build(options: String = ""): Program {
		CLUtil.buildProgram(id, ctx.device, options)
		return this
	}

	fun createKernel(name: String): Kernel {
		val k = Kernel(this, name)
		kernels.add(k)
		return k
	}

	override fun close() {
		CL30.clReleaseProgram(id)
	}
}