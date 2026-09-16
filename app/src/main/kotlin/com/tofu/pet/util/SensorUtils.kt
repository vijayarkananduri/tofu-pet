package com.tofu.pet.util

import kotlin.math.abs
import kotlin.math.sqrt

object SensorUtils {
    private const val GRAVITY = 9.81f

    fun calculateShakeLevel(x: Float, y: Float, z: Float): Int {
        val force = sqrt(x * x + y * y + z * z) - GRAVITY
        return (force / 2).toInt().coerceIn(1, 10)
    }

    fun isTilt(x: Float, y: Float): Boolean {
        return abs(x) > 5 || abs(y) > 5
    }

    fun getTiltDirection(x: Float, y: Float): Pair<Float, Float> {
        return Pair(
            x.coerceIn(-1f, 1f) / 10f,
            y.coerceIn(-1f, 1f) / 10f
        )
    }
}
