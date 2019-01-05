package com.example.dev00.translator.helpers

import android.content.Context
import android.view.View
import android.animation.AnimatorSet
import android.graphics.*
import android.util.Log
import com.example.dev00.translator.R
import com.example.dev00.translator.utils.ScreenUtils
import android.animation.ObjectAnimator
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.dev00.translator.utils.Utils

open class VoiceView: View{

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        init()
    }

    private val TAG = VoiceView::class.java.name

    private val STATE_NORMAL = 0
    private val STATE_PRESSED = 1
    private val STATE_RECORDING = 2

    private var mNormalBitmap: Bitmap? = null
    private var mPressedBitmap: Bitmap? = null
    private var mRecordingBitmap: Bitmap? = null
    private var mPaint: Paint? = null
    private var mAnimatorSet = AnimatorSet()
    private var mOnRecordListener: OnRecordListener? = null

    private var mState = STATE_NORMAL
    private var mIsRecording = false
    private var mMinRadius: Float = 0.toFloat()
    private var mMaxRadius: Float = 0.toFloat()
    private var mCurrentRadius: Float = 0.toFloat()

    private fun init() {
        mNormalBitmap = BitmapFactory.decodeResource(resources, R.drawable.vs_micbtn_off)
        mNormalBitmap = Utils.getResizedBitmap(mNormalBitmap!!, 200, 200)
        mPressedBitmap = BitmapFactory.decodeResource(resources, R.drawable.vs_micbtn_pressed)
        mPressedBitmap = Utils.getResizedBitmap(mPressedBitmap!!, 200, 200)
        mRecordingBitmap = BitmapFactory.decodeResource(resources, R.drawable.vs_micbtn_on)
        mRecordingBitmap = Utils.getResizedBitmap(mRecordingBitmap!!, 200, 200)

        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = Color.argb(255, 219, 219, 219)

        mMinRadius = ScreenUtils.dp2px(context, 68) / 2f
        mCurrentRadius = mMinRadius
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mMaxRadius = Math.min(w, h) / 2f
        Log.d(TAG, "MaxRadius: " + mMaxRadius)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = canvas!!.width
        val height = canvas.height

        if (mCurrentRadius > mMinRadius) {
            canvas.drawCircle(width / 2f, height / 2f, mCurrentRadius, mPaint)
        }

        when (mState) {
            STATE_NORMAL -> {
                canvas.drawBitmap(mNormalBitmap, width / 2 - mMinRadius, height / 2 - mMinRadius, mPaint)
            }
            STATE_PRESSED -> {
                canvas.drawBitmap(mPressedBitmap, width / 2 - mMinRadius, height / 2 - mMinRadius, mPaint)
            }
            STATE_RECORDING -> {
                canvas.drawBitmap(mRecordingBitmap, width / 2 - mMinRadius, height / 2 - mMinRadius, mPaint)
            }
        }
    }

    fun animateRadius(radius: Float) {
        var radius = radius
        if (radius <= mCurrentRadius) {
            return
        }
        if (radius > mMaxRadius) {
            radius = mMaxRadius
        } else if (radius < mMinRadius) {
            radius = mMinRadius
        }
        if (radius == mCurrentRadius) {
            return
        }
        if (mAnimatorSet.isRunning) {
            mAnimatorSet.cancel()
        }
        mAnimatorSet.playSequentially(
                ObjectAnimator.ofFloat(this, "CurrentRadius", getCurrentRadius(), radius).setDuration(50),
                ObjectAnimator.ofFloat(this, "CurrentRadius", radius, mMinRadius).setDuration(600)
        )
        mAnimatorSet.start()
    }

    fun getCurrentRadius(): Float {
        return mCurrentRadius
    }

    fun setCurrentRadius(currentRadius: Float) {
        mCurrentRadius = currentRadius
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.getActionMasked()) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "ACTION_DOWN")
                mState = STATE_PRESSED
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "ACTION_UP")
                if (mIsRecording) {
                    mState = STATE_NORMAL
                    if (mOnRecordListener != null) {
                        mOnRecordListener!!.onRecordFinish()
                    }
                } else {
                    mState = STATE_RECORDING
                    if (mOnRecordListener != null) {
                        mOnRecordListener!!.onRecordStart()
                    }
                }
                mIsRecording = !mIsRecording
                invalidate()
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    fun setOnRecordListener(onRecordListener: OnRecordListener) {
        mOnRecordListener = onRecordListener
    }

    interface OnRecordListener {
        fun onRecordStart()
        fun onRecordFinish()
    }
}