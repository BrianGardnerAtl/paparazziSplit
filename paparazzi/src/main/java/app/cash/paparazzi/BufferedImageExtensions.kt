package app.cash.paparazzi

import com.android.ide.common.rendering.api.SessionParams
import java.awt.image.BufferedImage

fun BufferedImage.formatImage(renderingMode: SessionParams.RenderingMode, deviceConfig: DeviceConfig) =
  ImageScale.scaleImage(ImageFrame.frameImage(this, renderingMode, deviceConfig))
