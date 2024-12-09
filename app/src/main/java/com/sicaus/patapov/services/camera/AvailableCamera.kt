package com.sicaus.patapov.services.camera

import android.util.Size

/**
 * Describes one available camera, some of its attributes,
 * and the list of available output sizes.
 */
class AvailableCamera(
    val cameraId: String,
    val facing: CameraSelectionCriteria.Facing,
    val orientation: Int,
    val minFocalLength: Float,
    val availableOutputSizes: List<Size>)

