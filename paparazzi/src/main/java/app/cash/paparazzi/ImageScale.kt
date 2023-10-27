package app.cash.paparazzi

import app.cash.paparazzi.internal.ImageUtils
import java.awt.image.BufferedImage

object ImageScale {
  fun scaleImage(
    image: BufferedImage
  ): BufferedImage {
    val scale = ImageUtils.getThumbnailScale(image)
    // Only scale images down so we don't waste storage space enlarging smaller layouts.
    return if (scale < 1f) ImageUtils.scale(image, scale, scale) else image
  }
}
