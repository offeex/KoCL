package me.offeex.kocl.utils

import me.offeex.kocl.CLRuntime
import org.lwjgl.PointerBuffer
import org.lwjgl.opencl.CLBufferRegion
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

object BuffUtil {
	fun pointer(pointer: Long): PointerBuffer {
		return CLRuntime.stack.mallocPointer(1).put(0, pointer)
	}

	fun buffer(stack: MemoryStack, value: Byte): ByteBuffer {
		return CLRuntime.stack.malloc(1).put(0, value)
	}

	fun buffer(stack: MemoryStack, value: Short): ShortBuffer {
		return CLRuntime.stack.mallocShort(1).put(0, value)
	}

	fun buffer(stack: MemoryStack, value: Int): IntBuffer {
		return CLRuntime.stack.mallocInt(1).put(0, value)
	}

	fun buffer(stack: MemoryStack, value: Float): FloatBuffer {
		return CLRuntime.stack.mallocFloat(1).put(0, value)
	}

	fun bufferRegion(offset: Int, size: Int): CLBufferRegion {
		return CLBufferRegion.create().set(offset.toLong(), size.toLong())
	}
}