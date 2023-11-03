package com.up.dbcircleprogress.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.up.dbcircleprogress.R

/**
 * 进度条，
 *
 */
class MyCircleProgress(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //抗锯齿
    private var antiAlias = true

    //圆心
    private lateinit var centerPoint: Point

    //背景圆画笔
    private lateinit var mBgCirclePaint: Paint

    //背景圆宽度
    private var mBgCircleWidth: Float = 15f

    //背景圆颜色
    private var mBgCircleColor: Int? = null

    //进度圆画笔
    private lateinit var mCirclePaint: Paint

    //进度圆宽度
    private var mCircleWidth: Float = 15f

    //进度圆颜色
    private var mCircleColor: Int? = null

    //半径
    private var mRadius: Float? = null

    //圆弧所在的椭圆范围
    private var mRectF: RectF? = null

    //数值画笔
    private lateinit var mValuePaint:TextPaint
    //颜色
    private var mValueTextColor: Int? = null
    private var mValueTextSize: Float? = null
    //描述信息画笔
    private var mMessagePaint:TextPaint? = null
    private var mMessageColor: Int? = null
    private var mMessageSize: Float? = null

    //百分比单位
    private var mUnit:String = ""


    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        centerPoint = Point()
        mRectF = RectF()
        //初始化参数
        initAttrs(attrs,context)
        initPaint()
    }

    private fun initAttrs(attrs: AttributeSet?, context: Context?) {
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.MyCircleProgress)
        mBgCircleColor = typedArray.getColor(R.styleable.MyCircleProgress_bgCircleColor, Color.GRAY)
        mBgCircleWidth = typedArray.getDimension(R.styleable.MyCircleProgress_bgCircleWidth, 15f)
        mCircleColor = typedArray.getColor(R.styleable.MyCircleProgress_circleColor, Color.BLUE)
        mCircleWidth = typedArray.getDimension(R.styleable.MyCircleProgress_circleWidth, 15f)
        mValueTextColor = typedArray.getColor(R.styleable.MyCircleProgress_valueTextColor, Color.BLACK)
        mValueTextSize = typedArray.getDimension(R.styleable.MyCircleProgress_valueTextSize, 14f)
        mMessageColor = typedArray.getColor(R.styleable.MyCircleProgress_messageTextColor, Color.GRAY)
        mMessageSize = typedArray.getDimension(R.styleable.MyCircleProgress_messageTextSize, 14f)
        mUnit = typedArray.getString(R.styleable.MyCircleProgress_unit)?:""
        typedArray.recycle()
    }

    private fun initPaint() {
        //背景圆画笔
        mBgCirclePaint = Paint()
        mBgCirclePaint.isAntiAlias = antiAlias
        mBgCirclePaint.style = Paint.Style.STROKE//画笔实心
        mBgCirclePaint.strokeWidth = mBgCircleWidth//画笔宽度
        mBgCirclePaint.strokeCap = Paint.Cap.ROUND//笔刷圆角
        mBgCirclePaint.color = mBgCircleColor!!
        //进度圆画笔
        mCirclePaint = Paint()
        mCirclePaint.isAntiAlias = antiAlias
        mCirclePaint.style = Paint.Style.STROKE//画笔实心
        mCirclePaint.strokeWidth = mBgCircleWidth//画笔宽度
        mCirclePaint.strokeCap = Paint.Cap.ROUND//笔刷圆角
        mCirclePaint.color = mCircleColor!!
        //数值画笔
        mValuePaint = TextPaint()
        mValuePaint.isAntiAlias = antiAlias
        mValuePaint.color = mValueTextColor!!
        mValuePaint.textSize = mValueTextSize!!
        mValuePaint.style = Paint.Style.STROKE
        mValuePaint.textAlign = Paint.Align.CENTER


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //确定圆心位置
        centerPoint.x = w / 2
        centerPoint.y = h / 2
        /** 确定半径 */
        //先获取两个圆的最大宽度，计算半径时需要根据控件的宽高减去画笔的宽度
        val maxCircleWidth = Math.max(mBgCircleWidth, mCircleWidth)
        val minWidth = Math.min(
            w - paddingLeft - paddingRight - 2 * maxCircleWidth,
            h - paddingTop - paddingBottom - 2 * maxCircleWidth
        )
        mRadius = minWidth / 2

        //需要确定椭圆的范围
        mRectF!!.left = centerPoint.x - mRadius!! - maxCircleWidth / 2
        mRectF!!.top = centerPoint.y - mRadius!! - maxCircleWidth / 2
        mRectF!!.right = centerPoint.x + mRadius!! + maxCircleWidth / 2
        mRectF!!.bottom = centerPoint.y + mRadius!! + maxCircleWidth / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawText(canvas)
        drawCircle(canvas)
    }

    private fun drawCircle(canvas: Canvas?) {

    }

    private fun drawText(canvas: Canvas?) {
        canvas!!.drawText("你好呀",centerPoint.x.toFloat(),centerPoint.y.toFloat(),mValuePaint)
    }
}