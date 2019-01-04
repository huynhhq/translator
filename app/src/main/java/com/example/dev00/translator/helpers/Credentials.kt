package com.example.dev00.translator.helpers

import android.net.Uri
import com.nuance.speechkit.PcmFormat

class Credentials {
    companion object {
        val APP_ID = "NMDPTRIAL_yncdbnap_gmail_com20190103201724"
        val SERVER_HOST = "sslsandbox-nmdp.nuancemobility.net"
        val SERVER_PORT = 443
        val SERVER_URI = Uri.parse("nmsps://$APP_ID@$SERVER_HOST:$SERVER_PORT")
        val APP_KEY = "1f49bae741351a2c831dede3dcbbe2da14dcbf22c6a8e26cc9ad82e1090f5a295814109a4a2ac6cd47b4b0236f92fd846ebca6356488a12d35230e3e3369678b"
        val PCM_FORMAT = PcmFormat(PcmFormat.SampleFormat.SignedLinear16, 16000, 1)
    }
}