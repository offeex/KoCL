package me.offeex.kocl.utils

import me.offeex.kocl.CLRuntime
import org.lwjgl.opencl.CL30.*
import java.nio.IntBuffer

object CLUtil {
	fun <T>tryValidate(callback: (IntBuffer) -> T): T {
		val err = CLRuntime.stack.mallocInt(1)
		val result = callback(err)
		validate { err.get(0) }
		return result
	}

	fun validate(callback: () -> Int) {
		val result = callback()
		if (result != CL_SUCCESS) throw RuntimeException("OpenCL error: $result")
	}

	/**
	 * Get the device type.
	 *
	 * @param i The device type id.
	 * @return The device type.
	 */
	fun getDeviceType(i: Int): String {
		return when (i) {
			CL_DEVICE_TYPE_DEFAULT -> "DEFAULT"
			CL_DEVICE_TYPE_CPU -> "CPU"
			CL_DEVICE_TYPE_GPU -> "GPU"
			CL_DEVICE_TYPE_ACCELERATOR -> "ACCELERATOR"
			else -> "?"
		}
	}
}