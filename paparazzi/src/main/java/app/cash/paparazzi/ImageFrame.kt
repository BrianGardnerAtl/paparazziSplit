package app.cash.paparazzi

import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import com.android.resources.ScreenRound
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage

object ImageFrame {
  fun frameImage(
    image: BufferedImage,
    renderingMode: RenderingMode,
    deviceConfig: DeviceConfig
  ): BufferedImage {
    // On device sized screenshot, we should apply any device specific shapes.
    if (renderingMode == RenderingMode.NORMAL && deviceConfig.screenRound == ScreenRound.ROUND) {
      val newImage = BufferedImage(image.width, image.height, image.type)
      val g = newImage.createGraphics()
      g.clip = Ellipse2D.Float(0f, 0f, image.height.toFloat(), image.width.toFloat())
      g.drawImage(image, 0, 0, image.width, image.height, null)
      return newImage
    }

    return image
  }
}
