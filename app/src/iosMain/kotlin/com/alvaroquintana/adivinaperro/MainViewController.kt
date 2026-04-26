package com.alvaroquintana.adivinaperro

import androidx.compose.ui.window.ComposeUIViewController
import com.alvaroquintana.adivinaperro.application.initKoinIos
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.experimental.ExperimentalNativeApi
import platform.Foundation.NSLog
import platform.UIKit.UIViewController

/**
 * Once-only flag so the unhandled exception hook is installed exactly
 * one time per process lifetime.
 */
@OptIn(ExperimentalAtomicApi::class)
private val hookInstalled = AtomicInt(0)

@OptIn(ExperimentalNativeApi::class, ExperimentalAtomicApi::class)
private fun installUncaughtKotlinExceptionHook() {
    if (!hookInstalled.compareAndSet(0, 1)) return
    setUnhandledExceptionHook { error: Throwable ->
        NSLog(
            "⚠️ Unhandled Kotlin exception in coroutine — swallowed to keep app alive.\n" +
                "  type: ${error::class.simpleName}\n" +
                "  message: ${error.message}\n" +
                "  stack:\n${error.stackTraceToString()}"
        )
    }
}

/**
 * iOS entry point consumed by the Xcode app shell (phase 6b).
 *
 * Swift usage:
 * ```
 * import AdivinaRazaShared
 *
 * struct ContentView: UIViewControllerRepresentable {
 *   func makeUIViewController(context: Context) -> UIViewController {
 *     return MainViewControllerKt.MainViewController()
 *   }
 *   func updateUIViewController(_ controller: UIViewController, context: Context) {}
 * }
 * ```
 */
fun MainViewController(): UIViewController {
    installUncaughtKotlinExceptionHook()
    initKoinIos()
    return ComposeUIViewController { App() }
}
