package au.sjowl.lib.view.charts.telegram.fragment

import android.graphics.Color
import au.sjowl.lib.view.charts.telegram.data.ChartData
import au.sjowl.lib.view.charts.telegram.data.ChartLineData
import org.json.JSONArray
import org.json.JSONObject

class ChartColumnJsonParser(val json: String) {

    fun parse(): List<ChartData> {

        val charts = JSONArray(json)
        val chartDataList = arrayListOf<ChartData>()

        for (i in 0 until charts.length()) {
            val chartData = ChartData()

            chartDataList.add(chartData)

            val chart = charts[i] as JSONObject

            val colors = chart.getJSONObject("colors")
            colors.keys().forEach { key ->
                chartData.columns[key] = ChartLineData(key).apply {
                    color = Color.parseColor(colors.getString(key))
                }
            }

            val names = chart.getJSONObject("names")
            names.keys().forEach { key ->
                chartData.columns[key]?.name = names.getString(key)
            }

            val types = chart.getJSONObject("types")
            types.keys().forEach { key ->
                chartData.columns[key]?.type = types.getString(key)
            }

            val columns = chart.getJSONArray("columns")
            for (j in 0 until columns.length()) {
                val jsonColumn = columns[j] as JSONArray
                val key = jsonColumn[0] as String

                if (key == "x") {
                    for (k in 1 until jsonColumn.length()) {
                        chartData.time.values.add(jsonColumn[k] as Long)
                    }
                } else {
                    val column = chartData.columns[key] as ChartLineData
                    for (k in 1 until jsonColumn.length()) {
                        column.values.add(jsonColumn[k] as Int)
                    }
                }
            }
        }

        return chartDataList
    }
}