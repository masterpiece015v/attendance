package tokyo.mstp015v.attendance

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import io.realm.Realm
import io.realm.kotlin.where
import tokyo.mstp015v.attendance.databinding.FragmentCheckAttendanceBinding
import tokyo.mstp015v.attendance.realm.Attendance

class CheckAttendanceFragment : Fragment() {
    private var _binding : FragmentCheckAttendanceBinding? = null
    private val binding get() = _binding!!
    private val args : CheckAttendanceFragmentArgs by navArgs()
    private val realm = Realm.getDefaultInstance()

    class CheckAttendance(
        val sub_name : String,
        var total : Int,
        var kesseki : Int,
        var chikoku : Int,
        var kouketsu : Int
    ){
        override fun toString(): String {
            return "${sub_name} 合:${total} 欠:${kesseki} 遅:${chikoku} 公:${kouketsu}"
        }
    }

    class CheckAttendanceList(){
        val map = mutableMapOf<String,CheckAttendance>()

        fun add( attendance : Attendance ){
            var item : CheckAttendance? = null
            if( map.get( attendance.sub_name ) == null ){
                //存在しないので新しく登録
                item = CheckAttendance(attendance.sub_name,0,0,0,0)
            }else{
                //存在するので、追加
                item = map.get(attendance.sub_name)
            }
            when( attendance.at_code){
                1-> item!!.chikoku++
                2-> item!!.kesseki++
                3-> item!!.kouketsu++
            }
            item!!.total++
            //Log.d( "checklist" , attendance.toString() )
        }
        fun getList() : List<CheckAttendance>{
            val list = mutableListOf<CheckAttendance>()

            for( item in map ){
                list.add( item.value )
                Log.d( "check" , item.value.toString() )
            }

            return list
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCheckAttendanceBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = CheckAttendanceList()

        val ret = realm.where<Attendance>().equalTo("st_id",args.stId).findAll()

        for( item in ret ){
            list.add( item )
            //Log.d("ret" , item.toString() )
        }

        for( item in list.getList() ){
            Log.d( "list" , item.toString() )
        }

        //Log.d( "list",list.getList().toString() )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}