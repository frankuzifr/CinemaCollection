package space.frankuzi.cinemacollection.utils

import android.view.animation.Interpolator
import kotlin.math.cos
import kotlin.math.pow

class BounceInterpolator(
    private val amplitude: Double,
    private val frequency: Double
) : Interpolator {

    private var _amplitude = 1.0
    private var _frequency = 10.0

    override fun getInterpolation(input: Float): Float {
        return (-1 * Math.E.pow(-input / _amplitude) * cos(_frequency * input) + 1).toFloat()
    }

    init {
        _amplitude = amplitude
        _frequency = frequency
    }
}