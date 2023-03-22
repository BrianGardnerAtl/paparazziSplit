package app.cash.paparazzi.internal

import android.content.Context
import android.graphics.Typeface

object ResourcesInterceptor {
  @JvmStatic
  fun intercept(
    context: Context,
    resId: Int
  ): Typeface? {
    println("Resources interceptor, swap get font function")
    return context.resources.getFont(resId)
  }
}
