package tokyo.mstp015v.timetable_pro.realm

import android.graphics.Color
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TimeTableDetail(
    @PrimaryKey
    var id:Long=0,
    var g_name:String="",
    var year:Int=0,
    var month:Int=0,
    var date:Int=0,
    var timed:Int=0,
    var sub_name:String="",
    var sub_mentor:String="",
    var at_code:Int=0,
    var color:Int= Color.parseColor("#FFFFFF"),
    var memo:String=""
): RealmObject()