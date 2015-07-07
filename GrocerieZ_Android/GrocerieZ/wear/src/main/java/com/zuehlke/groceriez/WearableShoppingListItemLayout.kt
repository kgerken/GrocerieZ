package com.zuehlke.groceriez

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.wearable.view.WearableListView
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

public class WearableShoppingListItemLayout @jvmOverloads constructor(context: Context,
                                                              attrs: AttributeSet,
                                                              defStyle: Int = 0)
:
        LinearLayout(context, attrs, defStyle),
        WearableListView.OnCenterProximityListener {

    private var mCircle: ImageView? = null
    private var mName: TextView? = null

    private val mFadedTextAlpha: Float
    private val mFadedCircleColor: Int
    private val mChosenCircleColor: Int

    init {
        mFadedTextAlpha = 0.5f
        mFadedCircleColor = getResources().getColor(R.color.grey)
        mChosenCircleColor = getResources().getColor(R.color.blue)
    }

    override fun onFinishInflate() {
        super<LinearLayout>.onFinishInflate()
        mCircle = findViewById(R.id.checkmark) as ImageView
        mName = findViewById(R.id.name) as TextView
    }

    override fun onCenterPosition(animate: Boolean) {
        mName?.setAlpha(1f)
    }

    override fun onNonCenterPosition(animate: Boolean) {
        mName?.setAlpha(mFadedTextAlpha)
    }
}