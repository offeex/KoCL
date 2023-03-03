package components

import interfaces.Identifiable
import org.lwjgl.opencl.CL30
import utils.CLUtil
import java.nio.Buffer

class Context(val device: Device) : AutoCloseable, Identifiable(CLUtil.createContext(device)) {
	val commandQueues: MutableList<CommandQueue> = mutableListOf()
	val programs: MutableList<Program> = mutableListOf()
	val memObjects: MutableList<MemObject> = mutableListOf()

	fun createCommandQueue(): CommandQueue {
		val cq = CommandQueue(this)
		commandQueues.add(cq)
		return cq
	}

	fun createProgram(source: String): Program {
		val p = Program(this, source)
		programs.add(p)
		return p
	}

	fun createMemObject(flags: Int, capacity: Int, byteMultiplier: Int, hb: Buffer): MemObject {
		val mo = MemObject(this, flags, capacity, byteMultiplier, hb)
		memObjects.add(mo)
		return mo
	}

	override fun close() {
		CL30.clReleaseContext(id)
		commandQueues.forEach { cq -> cq.close() }
		programs.forEach { p ->
			p.close()
			p.kernels.forEach { k -> k.close() }
		}
		memObjects.forEach { mo -> mo.close() }
	}
}