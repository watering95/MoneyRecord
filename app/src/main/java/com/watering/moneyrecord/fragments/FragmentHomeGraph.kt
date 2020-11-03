package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import androidx.databinding.DataBindingUtil.inflate
import com.watering.moneyrecord.databinding.FragmentHomeGraphBinding
import com.watering.moneyrecord.model.MyCalendar
import java.io.BufferedWriter
import java.io.FileWriter

import android.widget.Toast
import com.watering.moneyrecord.R
import com.watering.moneyrecord.viewmodel.ViewModelHomeGraph
import java.io.IOException
import java.util.*

class FragmentHomeGraph : ParentFragment() {
    private lateinit var binding: FragmentHomeGraphBinding
    private lateinit var mWeb: WebView
    var group: String? = ""
    private var duration = 100
    private var interval = 1
    private val viewModel by lazy { application?.let { ViewModelHomeGraph(it) } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_home_graph, container, false)
        initLayout()

        return binding.root
    }

    private fun initLayout() {
        openWebView()
        setHasOptionsMenu(false)
    }

    private fun openWebView() {
        mWeb = binding.webviewFragmentHomeGraph
        val set = mWeb.settings
        set.javaScriptEnabled = true
        set.builtInZoomControls = true
        mWeb.loadUrl("""file:///${activity.filesDir}graph_total.html""")
    }

    private fun makeHTMLFile() = try {
        mViewModel.getFirstDate(group).observeOnce { firstDate ->
            firstDate?.let {
                val data = StringBuilder().append("")
                val today = MyCalendar.getToday()
                makeData(firstDate, MyCalendar.strToCalendar(today), 0, data)
            }
        }
    } catch (e: IOException) {
        Toast.makeText(context, "html 파일 생성 실패", Toast.LENGTH_SHORT).show()
    }

    private fun makeData(firstDate: String, date: Calendar, i: Int, data: StringBuilder) {
        if(date >= MyCalendar.strToCalendar(firstDate) && i < duration) {
            val strDate = MyCalendar.calendarToStr(date)
            viewModel?.sumOfEvaluation(strDate)?.observeOnce { sumEvaluation ->
                sumEvaluation?.let {
                    viewModel?.sumOfPrincipal(strDate)?.observeOnce { sumPrincipal ->
                        sumPrincipal?.let {
                            var rate = 0.0
                            if (sumPrincipal != 0 && sumEvaluation != 0) rate =
                                sumEvaluation.toDouble() / sumPrincipal.toDouble() * 100 - 100
                            data.append("[").append("new Date('").append(strDate).append("')")
                                .append(", ").append(sumEvaluation).append(", ").append(rate)
                                .append("],\n")

                            makeData(
                                firstDate,
                                MyCalendar.changeDate(strDate, -interval),
                                i + 1,
                                data
                            )
                        }
                    }
                }
            }
        } else {
            if(data.isNotEmpty()) data.delete(data.length-2, data.length-1)

            val bw = BufferedWriter(FileWriter("${activity.filesDir}graph_total.html", false))
            val function = ("function drawChart() {\n"
                    + "var chartDiv = document.getElementById('chart_div');\n\n"

                    + "var data = new google.visualization.DataTable();\n"
                    + "data.addColumn('date','Day');\n"
                    + "data.addColumn('number','평가액');\n"
                    + "data.addColumn('number','수익율');\n"
                    + "data.addRows([\n" + data + "]);\n\n"

                    + "var options = {"
                    + "series: {0: {targetAxisIndex: 0}, 1: {targetAxisIndex: 1}},"
                    + "vAxes: {0: {title: '평가액'}, 1: {title: '수익율'}},"
                    + "};\n\n"

                    + "var chart = new google.visualization.LineChart(chartDiv);\n"
                    + "chart.draw(data, options);\n"

                    + "}\n")
            val script = ("<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n"
                    + "<script type=\"text/javascript\">\n"
                    + "google.charts.load('current', {'packages':['line', 'corechart']});\n"
                    + "google.charts.setOnLoadCallback(drawChart);\n"
                    + function
                    + "</script>\n")
            val body = "<div id=\"chart_div\"></div>\n"
            val html = "<!DOCTYPE html>\n<head>\n$script</head>\n<body>\n$body</body>\n</html>"
            Log.i("html",html)
            bw.write(html)
            bw.close()
            mWeb.reload()
        }
    }

    override fun onResume() {
        super.onResume()
        makeHTMLFile()
    }
}