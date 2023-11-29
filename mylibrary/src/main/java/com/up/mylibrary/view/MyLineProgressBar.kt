package com.up.mylibrary.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.up.mylibrary.R
import java.text.DecimalFormat

class MyLineProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val Number.dp
        get() = this.toFloat() * Resources.getSystem().displayMetrics.density

    /**
     * 默认进度条颜色
     */
    private val DEFAULT_PROGRESS_COLOR: Int = Color.parseColor("#FFA5E05B")
    private val DEFAULT_REMAIN_COLOR: Int = Color.parseColor("#FFFFFFFF")
    private val DEFAULT_TEXT_COLOR: Int = Color.parseColor("#FFFFFFFF")

    /**
     * 默认进度
     */
    private val DEFAULT_MAX: Float = 100f
    private val DEFAULT_PROGRESS: Float = 0f

    /**
     * 进度条最大值
     */
    private var mMax: Float = DEFAULT_MAX
    var mMin: Float = DEFAULT_PROGRESS

    /**
     * 进度条当前进度值
     */
    private var mProgress: Float = DEFAULT_PROGRESS

    /**
     * 进度条颜色
     * 完成
     */
    private var mProgressColor = DEFAULT_PROGRESS_COLOR

    /**
     * 进度条颜色
     * 剩余
     */
    private var mRemainColor = DEFAULT_REMAIN_COLOR

    /**
     * 进度条高度
     */
    private var mBarHeight = 30f.dp

    /**
     * 剩余进度区域
     */
    private val mRemainRectF = RectF(0f, 0f, 0f, 0f)

    /**
     * 已完成进度区域
     */
    private val mProgressRectF = RectF(0f, 0f, 0f, 0f)

    /**
     * ANTI_ALIAS_FLAG 抗锯齿
     * isAntiAlias 防抖动
     * Paint.Cap.ROUND 笔画凸出成半圆形
     */
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    /**
     * 进度文字颜色
     */
    private var mTextColor = Color.WHITE
    private var mTextVisibility = false
    private var progressFormat = DecimalFormat("#")

    /**
     * 读取自定义的布局属性
     */
    private fun initArr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyLineProgressBar)
        typedArray.run {
            mMax = getFloat(R.styleable.MyLineProgressBar_zMax, mMax)
            mProgress = getFloat(R.styleable.MyLineProgressBar_zProgress, mProgress)
            mProgressColor =
                getColor(R.styleable.MyLineProgressBar_zProgressColor, DEFAULT_PROGRESS_COLOR)
            mRemainColor =
                getColor(R.styleable.MyLineProgressBar_zRemainColor, DEFAULT_REMAIN_COLOR)
            mBarHeight = getDimension(R.styleable.MyLineProgressBar_zBarHeight, 30f.dp)
            mTextColor = getColor(R.styleable.MyLineProgressBar_zTextColor, DEFAULT_REMAIN_COLOR)
            mTextVisibility = getBoolean(R.styleable.MyLineProgressBar_zTextVisibility, true)
        }
        typedArray.recycle()
    }

    init {
        initArr(attrs)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        calculateProgressRect()
        canvas?.run {
            //先画剩余的，要让进度压在上面
            mPaint.color = mRemainColor
            //进度和剩余进度，就是两个圆角方形叠在一起
            drawRoundRect(mRemainRectF, mBarHeight / 2, mBarHeight / 2, mPaint)
            //再画进度
            mPaint.color = mProgressColor
            drawRoundRect(mProgressRectF, mBarHeight / 2, mBarHeight / 2, mPaint)
            //然后文字在最上层
            mPaint.textSize = mBarHeight * 0.5f
            //格式化Progress
            val mCurrentDrawText: String = progressFormat.format(mProgress * 100 / mMax)
            //画文字的基本操作，先测一下宽度
            val mDrawTextWidth = mPaint.measureText(mCurrentDrawText)
            //要判断下进度的宽度，够不够画文字出来
            if (mTextVisibility && mProgress > 0 && mProgressRectF.right > mDrawTextWidth) {
                mPaint.color = mTextColor
                drawText(
                    mCurrentDrawText,
                    mProgressRectF.right - mDrawTextWidth - mBarHeight * 0.4f,
                    //descent/ascent 关于文字的高度可以去看看相关文章
                    (height / 2.0f - (mPaint.descent() + mPaint.ascent()) / 2.0f).toInt().toFloat(),
                    mPaint
                )
            }
        }
    }

    /**
     * 计算底部进度条和实际进度条的绘制区域
     */
    private fun calculateProgressRect() {
        val ttop = (height - mBarHeight) / 2.0f
        val bbottom = (height + mBarHeight) / 2.0f
        mProgressRectF.run {
            left = paddingLeft.toFloat()
            top = ttop
            /**
             * 计算已完成进度的长度
             */
            right = (width - paddingLeft - paddingRight) / (mMax * 1.0f) * mProgress + paddingLeft
            bottom = bbottom
        }
        mRemainRectF.run {
            left = paddingLeft.toFloat()
            top = ttop
            right = (width - paddingRight).toFloat()
            bottom = bbottom
        }
    }

}