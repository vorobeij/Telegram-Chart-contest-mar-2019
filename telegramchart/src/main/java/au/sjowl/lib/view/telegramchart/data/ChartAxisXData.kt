package au.sjowl.lib.view.telegramchart.data

class ChartAxisXData {
    val values: ArrayList<Long> = arrayListOf() // todo long array

    val min get() = values[0]
    val max get() = values[values.size - 1]
    val interval get() = max - min
}