package me.offeex.kocl.utils

import me.offeex.kocl.RuntimeCL
import me.offeex.kocl.components.Context
import me.offeex.kocl.components.Device
import me.offeex.kocl.components.Program
import me.offeex.kocl.enums.RuntimeMode
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CL30.*
import java.nio.IntBuffer

object CLUtil {
	fun getPlatformIDs(): PointerBuffer {
		val ps: PointerBuffer = if (RuntimeCL.mode == RuntimeMode.PERFORMANT) RuntimeCL.stack.mallocPointer(1)
		else {
			val n = RuntimeCL.stack.mallocInt(1)
			validate { clGetPlatformIDs(null, n) }
			if (n.get(0) == 0) throw RuntimeException("No OpenCL platforms found")
			RuntimeCL.stack.mallocPointer(n.get(0))
		}
		validate { clGetPlatformIDs(ps, null as IntBuffer?) }
		return ps
	}

	fun getDeviceIDs(platform: Long, type: Long): PointerBuffer {
		val ds: PointerBuffer = if (RuntimeCL.mode == RuntimeMode.PERFORMANT) RuntimeCL.stack.mallocPointer(1)
		else {
			val n = RuntimeCL.stack.mallocInt(1)
			validate {clGetDeviceIDs(platform, type, null, n) }
			if (n.get(0) == 0) throw RuntimeException("No OpenCL devices found")
			RuntimeCL.stack.mallocPointer(n.get(0))
		}
		validate {clGetDeviceIDs(platform, type, ds, null as IntBuffer?) }
		return ds
	}

	fun createContext(device: Device): Long {
		var id: Long = 0
		check { id = clCreateContext(null, device.id, null, 0, it) }
		return id
	}

	fun createCommandQueue(ctx: Context): Long {
		var id: Long = 0
		check { id = clCreateCommandQueue(ctx.id, ctx.device.id, 0, it) }
		return id
	}

	fun createProgram(ctx: Context, source: String): Long {
		var id: Long = 0
		check { id = clCreateProgramWithSource(ctx.id, source, it) }
		return id
	}

	fun buildProgram(id: Long, device: Device, options: String = "") {
		validate {clBuildProgram(id, device.id, options, null, 0) }
	}

	fun createKernel(program: Program, name: String): Long {
		var id: Long = 0
		check { id = clCreateKernel(program.id, name, it) }
		return id
	}

	fun createBuffer(ctx: Context, flags: Int, capacity: Int): Long {
		var id: Long = 0
		check { id = clCreateBuffer(ctx.id, flags.toLong(), capacity.toLong(), it) }
		return id
	}

	private fun check(callback: (IntBuffer?) -> Unit) {
		if (RuntimeMode.PERFORMANT == RuntimeCL.mode) callback(null as IntBuffer?)
		else {
			val err = RuntimeCL.stack.mallocInt(1)
			callback(err)
			validate { err[0] }
		}
	}
	
	fun validate(callback: () -> Int) {
		val result = callback()
		if (RuntimeMode.PERFORMANT == RuntimeCL.mode) return
		if (result != CL_SUCCESS) throw RuntimeException("OpenCL error: $result")
	}

	fun toPointerBuffer(pointer: Long): PointerBuffer {
		return RuntimeCL.stack.mallocPointer(1).put(0, pointer)
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