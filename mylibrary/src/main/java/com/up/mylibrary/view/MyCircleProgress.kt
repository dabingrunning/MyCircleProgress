package com.up.mylibrary.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.up.mylibrary.R
import java.text.DecimalFormat

/**
 * 进度条，
 *
 */
class MyCircleProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //抗锯齿
    private var antiAlias = true

    //圆心
    private var centerPoint: Point

    //背景圆画笔
    private lateinit var mBgPaint: Paint

    //背景圆宽度
    private var mBgPaintWidth: Float = 15f

    //背景圆颜色
    private var mBgPaintColor: Int? = null

    //进度圆画笔
    private lateinit var mProgressPaint: Paint

    //进度圆宽度
    private var mProgressWidth: Float = 15f

    //进度圆颜色
    private var mProgressColor: Int? = null

    //半径
    private var mRadius: Float? = null

    //圆弧所在的椭圆范围
    private var mRectF: RectF? = null

    //数值画笔
    private lateinit var mValuePaint: TextPaint

    //颜色
    private var mValueTextColor: Int? = null
    private var mValueTextSize: Float? = null

    //描述信息画笔
    private lateinit var mMessagePaint: TextPaint
    private var mMessageColor: Int? = null
    private var mMessageSize: Float? = null

    //百分比单位
    private var mUnit: String = ""
    private var percentValue = 0f
    private var mMessage: String? = null

    private var mStartAngle = 0f
    private var mSweepAngle = 360f

    //初始角度便宜百分比
    private var anglePercent = 0f
    private var mMaxValue = 100f

    private lateinit var valueAnimator: ValueAnimator

    init {
//        setLayerType(LAYER_TYPE_SOFTWARE, null)
        centerPoint = Point()
        mRectF = RectF()
        //初始化参数
        initAttrs(attrs, context)
        initPaint()
    }

    private fun initAttrs(attrs: AttributeSet?, context: Context?) {
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.MyCircleProgress)
        mBgPaintColor = typedArray.getColor(R.styleable.MyCircleProgress_bgPaintColor, Color.GRAY)
        mBgPaintWidth = typedArray.getDimension(R.styleable.MyCircleProgress_bgPaintWidth, 15f)
        mProgressColor = typedArray.getColor(R.styleable.MyCircleProgress_progressColor, Color.BLUE)
        mProgressWidth = typedArray.getDimension(R.styleable.MyCircleProgress_progressWidth, 15f)
        mValueTextColor =
            typedArray.getColor(R.styleable.MyCircleProgress_valueTextColor, Color.BLACK)
        mValueTextSize = typedArray.getDimension(R.styleable.MyCircleProgress_valueTextSize, 14f)
        mMessageColor =
            typedArray.getColor(R.styleable.MyCircleProgress_messageTextColor, Color.GRAY)
        mMessageSize = typedArray.getDimension(R.styleable.MyCircleProgress_messageTextSize, 14f)
        mUnit = typedArray.getString(R.styleable.MyCircleProgress_unit) ?: ""
        percentValue = typedArray.getFloat(R.styleable.MyCircleProgress_percent, 0f)
        mMessage = typedArray.getString(R.styleable.MyCircleProgress_message)
        mStartAngle = typedArray.getFloat(R.styleable.MyCircleProgress_startAngle, 0f)
        mSweepAngle = typedArray.getFloat(R.styleable.MyCircleProgress_sweepAngle, 360f)
        mMaxValue = typedArray.getFloat(R.styleable.MyCircleProgress_maxValue, 100f)
        typedArray.recycle()
    }

    private fun initPaint() {
        //背景圆画笔
        mBgPaint = Paint()
        mBgPaint.isAntiAlias = antiAlias
        mBgPaint.style = Paint.Style.STROKE//画笔空心
        mBgPaint.strokeWidth = mBgPaintWidth//画笔宽度
        mBgPaint.strokeCap = Paint.Cap.ROUND//笔刷圆角
        mBgPaint.color = mBgPaintColor!!
        //进度圆画笔
        mProgressPaint = Paint()
        mProgressPaint.isAntiAlias = antiAlias
        mProgressPaint.style = Paint.Style.STROKE//画笔空心
        mProgressPaint.strokeWidth = mProgressWidth//画笔宽度
        mProgressPaint.strokeCap = Paint.Cap.ROUND//笔刷圆角
        mProgressPaint.color = mProgressColor!!
        //数值画笔
        mValuePaint = TextPaint()
        mValuePaint.isAntiAlias = antiAlias
        mValuePaint.color = mValueTextColor!!
        mValuePaint.textSize = mValueTextSize!!
        mValuePaint.textAlign = Paint.Align.CENTER
        //描述信息画笔
        mMessagePaint = TextPaint()
        mMessagePaint.isAntiAlias = antiAlias
        mMessagePaint.color = mMessageColor!!
        mMessagePaint.textSize = mMessageSize!!
        mMessagePaint.textAlign = Paint.Align.CENTER

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        centerPoint.x = width / 2
        centerPoint.y = height / 2
        /** 确定半径 */
        //先获取两个圆的最大宽度，计算半径时需要根据控件的宽高减去画笔的宽度
        val maxCircleWidth = mBgPaintWidth.coerceAtLeast(mProgressWidth)
        val minWidth =
            (width - paddingLeft - paddingRight - 2 * maxCircleWidth).coerceAtMost(height - paddingTop - paddingBottom - 2 * maxCircleWidth)
        mRadius = minWidth / 2

        //需要确定椭圆的范围
        mRectF!!.left = centerPoint.x - mRadius!! - maxCircleWidth / 2
        mRectF!!.top = centerPoint.y - mRadius!! - maxCircleWidth / 2
        mRectF!!.right = centerPoint.x + mRadius!! + maxCircleWidth / 2
        mRectF!!.bottom = centerPoint.y + mRadius!! + maxCircleWidth / 2
        anglePercent = percentValue / mMaxValue
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawText(canvas)
        drawCircle(canvas)


    }

    private fun drawText(canvas: Canvas?) {
        var string = "$percentValue$mUnit"
        canvas!!.drawText(string, centerPoint.x.toFloat(), centerPoint.y.toFloat(), mValuePaint)
        if (mMessage != null || mMessage != "") {
            canvas.drawText(
                mMessage.toString(),
                centerPoint.x.toFloat(),
                centerPoint.y - mMessagePaint.ascent() + 15,   //设置间距
                mMessagePaint
            )
        }
    }


    private fun drawCircle(canvas: Canvas?) {
        canvas?.save()
        //绘制背景圆
        canvas!!.drawArc(mRectF!!, mStartAngle, mSweepAngle, false, mBgPaint)
        canvas.drawArc(mRectF!!, mStartAngle, mSweepAngle * anglePercent, false, mProgressPaint)
        canvas.restore()
    }

    fun setPercentValue(value: Float) {
        startAnimate(anglePercent, value / mMaxValue)
    }

    fun getPercentValue() = percentValue

    /**
     * 传入始末百分比，计算出当前角度，刷新UI
     * @param start 起始角度百分比
     */
    private fun startAnimate(start: Float, end: Float) {
        valueAnimator = ValueAnimator.ofFloat(start, end)
        valueAnimator.duration = 1000L
        valueAnimator.addUpdateListener {
            anglePercent = it.animatedValue as Float
            percentValue = formatValue2Decimal(mMaxValue * anglePercent)
            postInvalidate()
        }
        valueAnimator.start()

    }

    /**
     * 格式化两位小数
     */
    private fun formatValue2Decimal(animatedValue: Float) =
        DecimalFormat("0.00").format(animatedValue).toFloat()
}