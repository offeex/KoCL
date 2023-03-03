package me.offeex.kocl.components

import me.offeex.kocl.interfaces.Identifiable
import org.lwjgl.opencl.CL30
import me.offeex.kocl.utils.CLUtil

class Platform(id: Long) : Identifiable(id) {
	val devices: MutableList<Device> = mutableListOf()

	init {
		val ds = CLUtil.getDeviceIDs(id, CL30.CL_DEVICE_TYPE_ALL.toLong())
		for (i in 0 until ds.capacity()) {
			devices.add(Device(ds[i]))
		}
	}

	fun firstDevice(): Device = devices[0]
}