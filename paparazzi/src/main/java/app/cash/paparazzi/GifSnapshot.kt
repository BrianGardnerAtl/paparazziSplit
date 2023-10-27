package app.cash.paparazzi

import android.view.View
import kotlinx.coroutines.flow.flow

class GifSnapshot(
  val sdk: PaparazziSdk
) {

  @JvmOverloads
  fun snapshot(
    view: View,
    start: Long = 0L,
    end: Long = 500L,
    fps: Int = 30
  ): ViewSnapshot {
    val preparedView = sdk.prepareView(view)
    return ViewSnapshot(
      flow {
        preparedView.use {
          // Add one to the frame count so we get the last frame. Otherwise a 1 second, 60 FPS animation
          // our 60th frame will be at time 983 ms, and we want our last frame to be 1,000 ms. This gets
          // us 61 frames for a 1 second animation, 121 frames for a 2 second animation, etc.
          val durationMillis = (end - start).toInt()
          val frameCount = (durationMillis * fps) / 1000 + 1
          for (frame in 0 until frameCount) {
            val nowMillis = start + (frame * 1000 / fps)
            emit(it.takeSnapshot(nowMillis).formatImage(sdk.renderingMode, sdk.deviceConfig))
          }
        }
      },
      fps
    )
  }
}
