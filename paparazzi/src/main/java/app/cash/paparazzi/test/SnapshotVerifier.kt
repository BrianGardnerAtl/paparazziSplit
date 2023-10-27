/*
 * Copyright (C) 2020 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.paparazzi.test

import app.cash.paparazzi.ViewSnapshot
import app.cash.paparazzi.internal.ImageUtils
import java.io.File
import javax.imageio.ImageIO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SnapshotVerifier @JvmOverloads constructor(
  private val maxPercentDifference: Double,
  rootDirectory: File = File(System.getProperty("paparazzi.snapshot.dir"))
) : SnapshotHandler {
  private val imagesDirectory: File = File(rootDirectory, "images")
  private val videosDirectory: File = File(rootDirectory, "videos")

  init {
    imagesDirectory.mkdirs()
    videosDirectory.mkdirs()
  }

  override fun handleSnapshot(viewSnapshot: ViewSnapshot, testRecord: TestRecord) {
    // Note: does not handle videos or its frames at the moment
    val expected = File(imagesDirectory, testRecord.toFileName(extension = "png"))
    if (!expected.exists()) {
      throw AssertionError("File $expected does not exist")
    }

    val goldenImage = ImageIO.read(expected)
    val image = runBlocking {
      viewSnapshot.imageFlow.first()
    }
    ImageUtils.assertImageSimilar(
      relativePath = expected.path,
      image = image,
      goldenImage = goldenImage,
      maxPercentDifferent = maxPercentDifference
    )
  }

  override fun close() = Unit
}
