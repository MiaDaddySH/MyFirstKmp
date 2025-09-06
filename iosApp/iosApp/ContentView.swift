import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    // var body: some View {
    //     ComposeView()
    //         .ignoresSafeArea()
    // }
    var body: some View {
        VStack(spacing: 16) {
            ComposeView() // 现有的 Compose 界面
            Button("仅 iOS 功能") {
                // TODO: iOS 专属行为
            }
            .buttonStyle(.borderedProminent)
        }
        .padding()
    }
}



