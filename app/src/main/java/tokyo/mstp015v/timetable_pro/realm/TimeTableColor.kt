package tokyo.mstp015v.timetable_pro.realm

import android.graphics.Color
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TimeTableColor (
    @PrimaryKey
    var id : Long = 0,
    var g_name : String = "",
    var sub_name : String = "",
    var sub_mentor:String="",
    var color : Int = Color.parseColor("#FFFFFF")
):RealmObject()
