package tokyo.mstp015v.timetable_pro.tools

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import tokyo.mstp015v.timetable_pro.R
import java.util.*

class InputDialog (val okSelected:()->Unit,
                   val cancelSelected:()->Unit): DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //一番外のlayout
        val container = LinearLayout( this.context ).apply{
            this.orientation = LinearLayout.VERTICAL
            this.setPadding(40,40,40,40)
        }
        //年月を表示
        var textTitle = TextView(this.context).apply{
            this.text = ""
            this.textSize = 24.0f
            this.setPadding(5,5,5,5)
        }

        container.addView( textTitle )

        val builder = AlertDialog.Builder( requireActivity()).apply{
            this.setView( container )
            this.setPositiveButton("OK"){ dialog,which->
                okSelected.invoke()
            }
            this.setNegativeButton("Cancel"){dialog,which->
                //cancelSelected()
            }
        }

        return builder.create()
    }
}