package com.maikson.exportxpress.scan

import android.view.Display
import android.view.SurfaceView
import com.maikson.exportxpress.view.PaperRectangle

interface IScanView {
    interface Proxy {
        fun exit()
        fun getDisplay(): Display
        fun getSurfaceView(): SurfaceView
        fun getPaperRect(): PaperRectangle
    }
}