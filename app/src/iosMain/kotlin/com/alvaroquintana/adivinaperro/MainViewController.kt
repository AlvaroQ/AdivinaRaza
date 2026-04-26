package com.alvaroquintana.adivinaperro

import androidx.compose.ui.window.ComposeUIViewController
import com.alvaroquintana.adivinaperro.application.initKoinIos
import platform.UIKit.UIViewController

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
    initKoinIos()
    return ComposeUIViewController { App() }
}
