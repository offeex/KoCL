package components

import interfaces.Identifiable
import org.lwjgl.opencl.CL30

class Device(id: Long) : AutoCloseable, Identifiable(id) {
	override fun close() {
		CL30.clReleaseDevice(id)
	}
}