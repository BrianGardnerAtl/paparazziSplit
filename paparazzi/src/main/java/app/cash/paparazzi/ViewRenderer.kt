package app.cash.paparazzi

import android.view.View
import androidx.compose.runtime.Composable

interface ViewRenderer {
  fun prepareView(composable: @Composable () -> Unit): PreparedView
  fun prepareView(view: View): PreparedView
}
