package com.maikson.exportxpress.crop

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import com.maikson.exportxpress.base.BaseActivity
import com.maikson.exportxpress.view.MenuSelectionActivity
import com.maikson.exportxpress.view.PaperRectangle
import kotlinx.android.synthetic.main.activity_crop.*
import maikson.ExportXpressDEMO.R

class CropActivity : BaseActivity(), ICropView.Proxy {

    private lateinit var mPresenter: CropPresenter

    override fun prepare() {
        crop.setOnClickListener { mPresenter.crop()
            enhance.visibility = View.VISIBLE
            save.visibility = View.VISIBLE
        }
        enhance.setOnClickListener { mPresenter.enhance() }
        save.setOnClickListener { mPresenter.save()

            var handler = Handler()

            var runnable = Runnable {
                val intent = Intent(this, MenuSelectionActivity::class.java)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    ContextCompat.startActivity(this, intent, null)

                } else {
                    startActivity(intent)
                }
            }

            handler.postDelayed(runnable, 2000)


        }
    }

    override fun provideContentViewId(): Int = R.layout.activity_crop


    override fun initPresenter() {
        mPresenter = CropPresenter(this, this)
    }

    override fun getPaper(): ImageView = paper

    override fun getPaperRect(): PaperRectangle = paper_rect

    override fun getCroppedPaper(): ImageView = picture_cropped
}