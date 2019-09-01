package com.watering.moneyrecord.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.watering.moneyrecord.MainActivity
import com.watering.moneyrecord.databinding.FragmentHomeGraphBinding
import com.watering.moneyrecord.model.MyCalendar
import com.watering.moneyrecord.viewmodel.ViewModelApp
import java.io.BufferedWriter
import java.io.FileWriter

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.watering.moneyrecord.R
import java.io.IOException

class FragmentHomeGraph : Fragment() {
    private lateinit var mViewModel: ViewModelApp
    private lateinit var binding: FragmentHomeGraphBinding
    private lateinit var mWeb: WebView
    var group: String? = ""
    var duration = 7
    var interval = 1
    private val mFragmentManager by lazy { (activity as MainActivity).supportFragmentManager as FragmentManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflate(inflater, R.layout.fragment_home_graph, container, false)
        initLayout()
        return binding.root
    }
    private fun initLayout() {
        val activity = activity as MainActivity

        mViewModel = activity.mViewModel
//        makeHTMLFile()

//        openWebView()
        setHasOptionsMenu(false)
    }

    private fun openWebView() {
        mWeb = binding.webviewFragmentHomeGraph
        val set = mWeb.settings
        set.javaScriptEnabled = true
        set.builtInZoomControls = true
        mWeb.loadUrl("""file:///${activity?.filesDir}graph_total.html""")
    }

    private fun makeHTMLFile() = try {
        val bw = BufferedWriter(FileWriter("${activity?.filesDir}graph_total.html", false))

        (if(group == "") mViewModel.allAccounts else mViewModel.getAccountsByGroup(mViewModel.currentGroupId))
            .observe(this, Observer { listOfAccounts -> listOfAccounts?.let {
                mViewModel.getFirstDate().observe(this, Observer { firstDate -> firstDate?.let {
                    val data = MutableLiveData<StringBuilder>()
                    val today = MyCalendar.getToday()
                    var date = MyCalendar.strToCalendar(today)
                    var sumEvaluation = 0
                    var sumPrincipal = 0
                    var rate = 0.0
                    var i = 0

                    // TODO("data를 별도로 처리")
                    do {
                        val strDate = MyCalendar.calendarToStr(date)

                        listOfAccounts.forEach {account ->
                            mViewModel.loadingDairyTotal(account.id, strDate, false).observeOnce( Observer { dairy -> dairy?.let {
                                sumEvaluation += dairy.evaluationKRW!!
                                sumPrincipal += dairy.principalKRW!!
                                if(account == listOfAccounts.last()) {
                                    if (sumPrincipal != 0 && sumEvaluation != 0) rate = sumEvaluation.toDouble() / sumPrincipal.toDouble() * 100 - 100
                                    data.run { value?.append("[")?.append("new Date('")?.append(strDate)?.append("')")?.append(", ")?.append(sumEvaluation)?.append(", ")?.append(rate)?.append("],\n") }
                                }
                            } })
                        }

                        sumEvaluation = 0
                        sumPrincipal = 0
                        rate = 0.0
                        i++

                        if(duration in 0 until i) break
                        date = MyCalendar.changeDate(today, -i*interval)

                    } while (date > MyCalendar.strToCalendar(firstDate))

                    data.observe(this, Observer { data -> data?.let {
                        if(data.isNotEmpty()) data.delete(data.length-2, data.length-1)

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
                        Log.i("MoneyRecord", html)
                        bw.write(html)
                        bw.close()
                    } })
                } })
            } })
    } catch (e: IOException) {
        Toast.makeText(context, "html 파일 생성 실패", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        makeHTMLFile()
//        mWeb.reload()
    }

    private fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object: Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}