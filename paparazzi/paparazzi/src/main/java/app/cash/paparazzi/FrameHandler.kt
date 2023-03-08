package app.cash.paparazzi

import java.awt.image.BufferedImage
import java.io.Closeable

interface FrameHandler : Closeable {
  fun handle(image: BufferedImage)
}
