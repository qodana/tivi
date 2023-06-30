//
//  TiviApp.swift
//  Tivi
//
//  Created by Chris Banes on 28/06/2023.
//

import SwiftUI
import TiviKt

@main
struct TiviApp: App {
    let applicationComponent = IosApplicationComponent.companion.create()

    init() {
        applicationComponent.initializers.initialize()
    }

    var body: some Scene {
        WindowGroup {
            let uiComponent = HomeUiControllerComponent.companion.create(
                applicationComponent: applicationComponent
            )
            ContentView(component: uiComponent)
        }
    }
}
