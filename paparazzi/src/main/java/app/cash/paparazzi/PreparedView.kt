package app.cash.paparazzi

import java.awt.image.BufferedImage
import java.io.Closeable

// Interface representing a prepared render pipeline that the SDK has initialized
// and is now ready to render frames.
interface PreparedView : Closeable {
  fun takeSnapshot(
    snapshotTimeMillis: Long = 0L
  ): BufferedImage
}
