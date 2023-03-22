package app.cash.paparazzi

import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers

object InterceptorRegistrar {
  private val byteBuddy = ByteBuddy()
  private val methodInterceptors = mutableMapOf<String, () -> Unit>()

  fun addMethodInterceptor(
    receiver: Class<*>,
    methodName: String,
    interceptor: Class<*>
  ) = addMethodInterceptors(receiver, setOf(methodName to interceptor))

  fun addMethodInterceptors(
    receiver: Class<*>,
    methodNamesToInterceptors: Set<Pair<String, Class<*>>>
  ) {
    methodInterceptors += Pair(
      receiver.name
    ) {
      println("${receiver.name} redefine class")
      var builder = byteBuddy
        .redefine(receiver)
      println("${receiver.name} getting builder $builder")

      methodNamesToInterceptors.forEach {
        builder = builder
          .method(ElementMatchers.named(it.first))
          .intercept(MethodDelegation.to(it.second))
      }
      println("${receiver.name} registered method names to interceptors, load things")

      builder
        .make()
        .load(receiver.classLoader, ClassReloadingStrategy.fromInstalledAgent())
    }
  }

  fun registerMethodInterceptors() {
    methodInterceptors.forEach {
      println("register interceptor: ${it.key}")
      it.value.invoke()
    }
  }

  fun clearMethodInterceptors() {
    methodInterceptors.clear()
  }
}
