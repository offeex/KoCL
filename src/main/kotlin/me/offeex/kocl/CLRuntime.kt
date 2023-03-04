package me.offeex.kocl

import org.lwjgl.opencl.CL30.*
import org.lwjgl.system.MemoryStack

object CLRuntime : AutoCloseable {
	val stack: MemoryStack = MemoryStack.stackPush()
	private val objects: Map<ObjectType, MutableList<Long>> = mapOf(
		ObjectType.DEVICE to mutableListOf(),
		ObjectType.CONTEXT to mutableListOf(),
		ObjectType.COMMAND_QUEUE to mutableListOf(),
		ObjectType.MEM_OBJECT to mutableListOf(),
		ObjectType.PROGRAM to mutableListOf(),
		ObjectType.KERNEL to mutableListOf(),
		ObjectType.EVENT to mutableListOf(),
		ObjectType.SAMPLER to mutableListOf(),
	)

	private fun obj(type: ObjectType, callback: () -> Long): Long {
		val result = callback()
		objects[type]!!.add(result)
		return result
	}

	fun device(callback: () -> Long) = obj(ObjectType.DEVICE, callback)
	fun context(callback: () -> Long) = obj(ObjectType.CONTEXT, callback)
	fun commandQueue(callback: () -> Long) = obj(ObjectType.COMMAND_QUEUE, callback)
	fun memObject(callback: () -> Long) = obj(ObjectType.MEM_OBJECT, callback)
	fun program(callback: () -> Long) = obj(ObjectType.PROGRAM, callback)
	fun kernel(callback: () -> Long) = obj(ObjectType.KERNEL, callback)
	fun event(callback: () -> Long) = obj(ObjectType.EVENT, callback)
	fun sampler(callback: () -> Long) = obj(ObjectType.SAMPLER, callback)

	override fun close() {
		stack.close()
		objects.forEach { (type, list) ->
			list.forEach { id ->
				when (type) {
					ObjectType.DEVICE -> clReleaseDevice(id)
					ObjectType.CONTEXT -> clReleaseContext(id)
					ObjectType.COMMAND_QUEUE -> clReleaseCommandQueue(id)
					ObjectType.MEM_OBJECT -> clReleaseMemObject(id)
					ObjectType.PROGRAM -> clReleaseProgram(id)
					ObjectType.KERNEL -> clReleaseKernel(id)
					ObjectType.EVENT -> clReleaseEvent(id)
					ObjectType.SAMPLER -> clReleaseSampler(id)
				}
			}
		}
	}

	enum class ObjectType {
		DEVICE,
		CONTEXT,
		COMMAND_QUEUE,
		MEM_OBJECT,
		PROGRAM,
		KERNEL,
		EVENT,
		SAMPLER,
	}
}