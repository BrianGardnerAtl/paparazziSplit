package app.cash.paparazzi

import android.view.View
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.flow

class DeviceSnapshot(
  val sdk: PaparazziSdk
) {

  fun snapshot(composable: @Composable () -> Unit): ViewSnapshot {
    val preparedView = sdk.prepareView(composable)
    return ViewSnapshot(
      flow {
        preparedView.use {
          emit(it.takeSnapshot().formatImage(sdk.renderingMode, sdk.deviceConfig))
        }
      }
    )
  }

  fun snapshot(view: View, offsetMillis: Long = 0L): ViewSnapshot {
    val preparedView = sdk.prepareView(view)
    return ViewSnapshot(
      flow {
        preparedView.use {
          emit(it.takeSnapshot(offsetMillis).formatImage(sdk.renderingMode, sdk.deviceConfig))
        }
      }
    )
  }
}
