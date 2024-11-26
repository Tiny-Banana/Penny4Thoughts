package com.mobdeve.s12.group4.mco.utility

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.mobdeve.s12.group4.mco.R

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val textView: TextView = findViewById(R.id.marker_text)


    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e is com.github.mikephil.charting.data.PieEntry) {
            // Format text with category and percentage
            textView.text = "${e.label}: \n${String.format("%.2f%%", e.value)}"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        // Adjust the position of the popup
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}
