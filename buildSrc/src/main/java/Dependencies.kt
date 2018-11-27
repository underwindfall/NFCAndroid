object Versions {
    const val support_lib = "28.0.0"
    const val constraint_lib = "1.1.3"
    const val nfc_reader_lib = "1.0.3"
    //test unit version
    const val junit_lib = "4.12"
    const val testRunner = "1.0.2"
    const val testEspresso = "3.0.2"
}

object Libs {
    val support_appcompat_v7 = "com.android.support:appcompat-v7:${Versions.support_lib}"
    val support_design = "com.android.support:design:${Versions.support_lib}"
    val support_constraint_layout = "com.android.support.constraint:constraint-layout:${Versions.constraint_lib}"
    val nfc_emv_lib = "com.github.pro100svitlo:creditCardNfcReader:${Versions.nfc_reader_lib}"
    //Test unit
    val junit = "junit:junit:${Versions.junit_lib}"
    val test_runner = "com.android.support.test:runner:${Versions.testRunner}"
    val espresso_core = "com.android.support.test.espresso:espresso-core:${Versions.testEspresso}"
}