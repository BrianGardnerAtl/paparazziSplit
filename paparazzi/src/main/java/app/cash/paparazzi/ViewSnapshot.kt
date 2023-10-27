package app.cash.paparazzi

import java.awt.image.BufferedImage
import kotlinx.coroutines.flow.Flow

data class ViewSnapshot(
  val imageFlow: Flow<BufferedImage>,
  val fps: Int = 1
)
