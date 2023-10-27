package app.cash.paparazzi

import android.view.View
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import app.cash.paparazzi.test.HtmlReportWriter
import app.cash.paparazzi.test.SnapshotHandler
import app.cash.paparazzi.test.SnapshotVerifier
import app.cash.paparazzi.test.TestName
import app.cash.paparazzi.test.TestRecord
import com.android.ide.common.rendering.api.SessionParams
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.Date

class Paparazzi @JvmOverloads constructor(
  environment: Environment = detectEnvironment(),
  deviceConfig: DeviceConfig = DeviceConfig.NEXUS_5,
  theme: String = "android:Theme.Material.NoActionBar.Fullscreen",
  renderingMode: SessionParams.RenderingMode = SessionParams.RenderingMode.NORMAL,
  appCompatEnabled: Boolean = true,
  renderExtensions: Set<RenderExtension> = setOf(),
  supportsRtl: Boolean = false,
  showSystemUi: Boolean = false,
  validateAccessibility: Boolean = false,
  private val maxPercentDifference: Double = 0.1,
  private val snapshotHandler: SnapshotHandler = determineHandler(maxPercentDifference)
) : TestRule {
  private val paparazziSdk = PaparazziSdk(
    environment,
    deviceConfig,
    theme,
    renderingMode,
    appCompatEnabled,
    renderExtensions,
    supportsRtl,
    showSystemUi,
    validateAccessibility
  )
  private var testName: TestName? = null

  override fun apply(base: Statement, description: Description): Statement {
    val statement = object : Statement() {
      override fun evaluate() {
        prepare(description)
        try {
          base.evaluate()
        } finally {
          close()
        }
      }
    }
    return statement
  }

  val context
    get() = paparazziSdk.context
  val resources
    get() = paparazziSdk.resources
  val layoutInflater
    get() = paparazziSdk.layoutInflater

  fun <V : View> inflate(@LayoutRes layoutId: Int): V = paparazziSdk.inflate(layoutId)

  fun unsafeUpdateConfig(
    deviceConfig: DeviceConfig? = null,
    theme: String? = null,
    renderingMode: SessionParams.RenderingMode? = null
  ) = paparazziSdk.unsafeUpdateConfig(deviceConfig, theme, renderingMode)

  fun snapshot(name: String? = null, composable: @Composable () -> Unit) {
    val deviceSnapshot = DeviceSnapshot(paparazziSdk)
    val testRecord = createTestRecord(name)
    snapshotHandler.handleSnapshot(deviceSnapshot.snapshot(composable), testRecord)
  }

  @JvmOverloads
  fun snapshot(view: View, name: String? = null, offsetMillis: Long = 0L) {
    val deviceSnapshot = DeviceSnapshot(paparazziSdk)
    val testRecord = createTestRecord(name)
    snapshotHandler.handleSnapshot(deviceSnapshot.snapshot(view, offsetMillis), testRecord)
  }

  @JvmOverloads
  fun gif(view: View, name: String? = null, start: Long = 0L, end: Long = 500L, fps: Int = 30) {
    val gifSnapshot = GifSnapshot(paparazziSdk)
    val testRecord = createTestRecord(name)
    snapshotHandler.handleSnapshot(gifSnapshot.snapshot(view, start, end, fps), testRecord)
  }

  private fun createTestRecord(name: String?) = TestRecord(
    name = name,
    testName = testName!!,
    timestamp = Date()
  )

  private fun prepare(description: Description) {
    testName = description.toTestName()
    paparazziSdk.prepare()
  }

  private fun close() {
    paparazziSdk.close()
  }

  private fun Description.toTestName(): TestName {
    val fullQualifiedName = className
    val packageName = fullQualifiedName.substringBeforeLast('.', missingDelimiterValue = "")
    val className = fullQualifiedName.substringAfterLast('.')
    return TestName(packageName, className, methodName)
  }

  companion object {
    private val isVerifying: Boolean =
      System.getProperty("paparazzi.test.verify")?.toBoolean() == true

    private fun determineHandler(maxPercentDifference: Double): SnapshotHandler =
      if (isVerifying) {
        SnapshotVerifier(maxPercentDifference)
      } else {
        HtmlReportWriter()
      }
  }
}
