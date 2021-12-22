package tokyo.mstp015v.attendance

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import tokyo.mstp015v.attendance.databinding.FragmentEnterAttendanceForGroupBinding
import tokyo.mstp015v.attendance.databinding.FragmentEnterAttendanceTimeTableBinding
import java.util.*


class EnterAttendanceTimeTableFragment : Fragment() {

    private val args : EnterAttendanceTimeTableFragmentArgs by navArgs()
    private var _binding : FragmentEnterAttendanceTimeTableBinding? = null
    private val binding get() = _binding!!


    private var sat_enable :Boolean = false
    private var max_timed :Int = 0
    private var calendar = Calendar.getInstance()
    private val days1 = listOf("月","火","水","木","金")
    private val days2 = listOf("月","火","水","木","金","土")
    private var year = 0
    private var month = 0
    private var date = 0
    private var day = 0


    inner class DaysAdapter( fa: FragmentActivity? ): FragmentStateAdapter( fa!! ){
        override fun getItemCount():Int = if( sat_enable ){ days2.size} else {days1.size}
        override fun createFragment( p:Int):Fragment =
            if( sat_enable ){
                EnterTimeTableDayFragment.newInstance(args.gName!!,days2[p],max_timed,year,month,date)
            }else{
                EnterTimeTableDayFragment.newInstance(args.gName!!,days1[p],max_timed,year,month,date)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEnterAttendanceTimeTableBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //シェアードプリファレンスの取得
        val pref = context?.getSharedPreferences("init", Context.MODE_PRIVATE)
        sat_enable = pref!!.getBoolean("sat_enable",false)
        max_timed = pref!!.getInt("max_timed",5)
        //viewPagerの設定
        binding.viewPagerEnterAttendance.adapter = DaysAdapter(activity)
        TabLayoutMediator( binding.tabEnterAttendance , binding.viewPagerEnterAttendance ){tab,p->
            if( sat_enable ) {
                tab.text = days2[p]
            }else{
                tab.text = days1[p]
            }
        }.attach()
        //日付の設定
        calendarRefresh(0)

        //ページの選択イベント
        binding.viewPagerEnterAttendance.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position:Int){
                super.onPageSelected(position)
                calendarRefresh( position - day )
            }
        })

        //カレンダーを表示
        binding.textDate.setOnClickListener {
            val dialog = DatePickerDialog(requireContext() ,
                DatePickerDialog.OnDateSetListener(){view,year,month,date->
                    //calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR,year)
                    calendar.set(Calendar.MONTH,month)
                    calendar.set(Calendar.DATE,date)
                    calendarRefresh(0)

                },
                year,
                month-1,
                date
            )
            dialog.show()
        }
    }
    fun calendarRefresh(i : Int){
        calendar.add(Calendar.DATE, i )
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        date = calendar.get(Calendar.DATE)
        day = calendar.get(Calendar.DAY_OF_WEEK)-2
        day = if( sat_enable ){
            //土曜が有効
            if( day < 0 ) 0 else day
        }else{
            //土曜が無効
            if( day < 0 || day >= 5 ) 0 else day
        }

        if( sat_enable ) {
            binding.textDate.text = "${year}年${month}月${date}日${days2[day]}"
        }else{
            binding.textDate.text = "${year}年${month}月${date}日${days1[day]}"
        }

        binding.viewPagerEnterAttendance.setCurrentItem(day,false)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}