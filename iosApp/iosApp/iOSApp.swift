import SwiftUI
import AdivinaRazaShared
import FirebaseCore
import FirebaseAuth

@main
struct iOSApp: SwiftUI.App {
    init() {
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            FirebaseApp.configure()
            signInAnonymouslyIfNeeded()
        } else {
            print("⚠️ GoogleService-Info.plist not bundled — Firestore/Crashlytics calls will fail at runtime. Drop it into iosApp/iosApp/ and re-run `xcodegen generate`.")
        }
    }

    /// Mirrors what Android does in MainActivity.onStart() — without an
    /// authenticated user, Firestore rules that require `request.auth != null`
    /// reject every read with PERMISSION_DENIED.
    private func signInAnonymouslyIfNeeded() {
        if Auth.auth().currentUser != nil { return }
        Auth.auth().signInAnonymously { result, error in
            if let error = error {
                print("⚠️ Firebase anonymous sign-in failed: \(error.localizedDescription)")
            } else if let uid = result?.user.uid {
                print("✅ Signed in anonymously: \(uid)")
            }
        }
    }

    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea(.all)
                .preferredColorScheme(nil)
        }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
