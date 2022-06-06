package edu.practice.utils.shared

object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    external fun ip(): String
    external fun db(): String
    external fun user(): String
    external fun pass(): String
    external fun blob(): String
    external fun clientId(): String
    external fun token(): String
}