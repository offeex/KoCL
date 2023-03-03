package me.offeex.kocl.components

import me.offeex.kocl.interfaces.Identifiable
import org.lwjgl.opencl.CL30
import me.offeex.kocl.utils.CLUtil
import java.nio.Buffer

class MemObject(
	private val ctx: Context,
	private val flags: Int,
	private val capacity: Int,
	private val bm: Int,
	val hostBuffer: Buffer
) : AutoCloseable, Identifiable(CLUtil.createBuffer(ctx, flags, capacity * bm)) {

	override fun close() {
		CL30.clReleaseMemObject(id)
	}
}