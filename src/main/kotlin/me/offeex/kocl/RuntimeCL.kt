package me.offeex.kocl

import me.offeex.kocl.components.Platform
import me.offeex.kocl.enums.RuntimeMode
import org.lwjgl.system.MemoryStack
import me.offeex.kocl.utils.CLUtil

object RuntimeCL : AutoCloseable {
	val stack = MemoryStack.stackPush()
	val platforms: MutableList<Platform> = mutableListOf()
	var mode: Int = RuntimeMode.SAFE

	init {
		val ps = CLUtil.getPlatformIDs()
		for (i in 0 until ps.capacity()) {
			platforms.add(Platform(ps[i]))
		}
	}

	fun firstPlatform(): Platform = platforms[0]

	override fun close() {
		stack.close()
		platforms.forEach { p ->
			p.devices.forEach { d -> d.close() }
		}
	}
}