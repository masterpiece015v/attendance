package tokyo.mstp015v.timetable_pro

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import tokyo.mstp015v.timetable_pro.databinding.FragmentMakeTimeTableDayBinding
import tokyo.mstp015v.timetable_pro.realm.TimeTable
import tokyo.mstp015v.timetable_pro.realm.TimeTableColor

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class MakeTimeTableDayFragment : Fragment() {
    //カスタムスピナーのアダプター
    class SpinnerAdapter(val context:Context):BaseAdapter(){
        val list = mutableListOf<String>()
        val resourceId : Int = R.layout.spinner_color_item
        val layoutInflater = LayoutInflater.from( context )
        inner class ViewHolder(val textView : TextView)
        companion object {
            val colors = mapOf(
                Pair("White",Color.parseColor("#FFFFFF")),
                Pair("Red", Color.parseColor("#FF9999")),
                Pair("Green", Color.parseColor("#99FF99")),
                Pair("Blue", Color.parseColor("#9999FF")),
                Pair("Perple", Color.parseColor("#FF99FF")),
                Pair("LigntBlue", Color.parseColor("#99FFFF")),
                Pair("Yellow", Color.parseColor("#FFFF99"))
            )
            val revcolors = mutableMapOf<Int,Int>()
        }
        init{
            var i = 0
            colors.forEach{
                list.add( it.key )
                revcolors.put( it.value , i )
                i++
            }

        }

        override fun getCount(): Int = colors.keys.size

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if( convertView == null ) {
                val view = layoutInflater.inflate(resourceId, parent, false)
                val holder = ViewHolder(view.findViewById(R.id.textColor))
                holder.textView.text = list[position]
                holder.textView.setBackgroundColor(colors.get( list[position] )!!)
                view.tag = holder
                return view
            }else{
                val holder = ( convertView.tag as ViewHolder )
                holder.textView.text = list[position]
                holder.textView.setBackgroundColor(colors.get( list[position] )!!)
                return convertView
            }
        }
    }
    //レルムのアダプター
    class TimeTableAdapter( data:OrderedRealmCollection<TimeTable>):RealmRecyclerViewAdapter<TimeTable,TimeTableAdapter.ViewHolder>(data,true){

        private var listener : ((Long,String?,String?,Int?)->Unit)? = null
        private var listener2 : ((Long)->Unit)? = null
        fun setOnItemClickListener( listener : ((Long,String?,String?,Int?)->Unit)){
            this.listener = listener
        }
        fun setOnImageTrushClickListener( listener2 : ((Long)->Unit)){
            this.listener2 = listener2
        }
        class ViewHolder( view:View): RecyclerView.ViewHolder( view ){
            val text3_1 = view.findViewById<TextView>(R.id.textTrush3_1)
            val text3_2 = view.findViewById<TextView>(R.id.textTrush3_2)
            val text3_3 = view.findViewById<TextView>(R.id.textTrush3_3)
            val linear3 = view.findViewById<ConstraintLayout>(R.id.linearTrush3 )
            val image3 = view.findViewById<ImageButton>(R.id.imageTrushButton )
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from( parent.context).inflate( R.layout.text_item_3_trush,parent,false)
            return ViewHolder( view )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem( position )
            holder.text3_1.text = item!!.timed.toString()
            holder.text3_2.text = item.sub_name
            holder.text3_3.text = item.sub_mentor
            holder.linear3.setOnClickListener {
                listener?.invoke( item.id ,item.sub_name,item.sub_mentor,item.color)
            }

            holder.linear3.setBackgroundColor( item.color )

            holder.image3.setOnClickListener {
                listener2?.invoke( item.id )
            }
        }
    }

    //更新ダイアログ
    class EditGroupDialog(
            val id:Long,
            val g_name : String?,
            val sub_name: String?,
            val sub_mentor:String?,
            val before_color:Int?,
            val okSelected: (id:Long,sub_name:String?,sub_mentor:String?,color:Int?) -> Unit,
            val cancelSelected:()->Unit
    ): DialogFragment(){
        var color : Int? = null
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val spinneritems = mutableMapOf<String,Int>()
            val realm = Realm.getDefaultInstance()
            val ret = realm.where<TimeTableColor>().equalTo("g_name",g_name ).findAll()
            val list = mutableListOf<String>()
            list.add( "" )
            ret.forEach{
                spinneritems.put( "${it.sub_name}_${it.sub_mentor}" , it.color )
                list.add( "${it.sub_name}_${it.sub_mentor}")
            }

            realm.close()

            val adapter1 = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, list )
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            val spinner1 = Spinner( this.context ).apply{
                this.adapter = adapter1
            }

            val edit1 = EditText(this.context).apply{
                this.hint=resources.getString(R.string.subject_name)
                this.setText( sub_name )
                this.width = 800
                this.textSize = 24.0f }
            val edit2 = EditText(this.context).apply{
                this.hint=resources.getString(R.string.subject_mentor)
                this.setText( sub_mentor )
                this.width = 800
                this.textSize = 24.0f
            }

            val adapter2 = SpinnerAdapter( requireContext() )

            val spinner2 = Spinner(this.context).apply{
                this.adapter = adapter2
            }

            spinner2.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                    // An item was selected. You can retrieve the selected item using
                    // parent.getItemAtPosition(pos)
                    Log.d("item","select${parent.selectedItem}")
                    color = SpinnerAdapter.colors[parent.selectedItem]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                    Log.d("item","nothing")
                }

            }
            //すでに色を持っていればその色にする
            spinner2.setSelection( SpinnerAdapter.revcolors.get(before_color) ?: 0)

            //履歴選択のイベント
            spinner1.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if( parent!!.selectedItem.toString().length > 0 ){
                        val item = parent.selectedItem.toString().split("_")
                        edit1.setText( item[0] )
                        edit2.setText( item[1] )
                        spinner2.setSelection( SpinnerAdapter.revcolors.get( spinneritems.get(parent.selectedItem.toString() ) ) ?: 0)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            //表示する
            val linear = LinearLayout( this.context ).apply{
                this.addView( TextView(this.context).apply{ this.text = resources.getString(R.string.history_select)})
                this.addView( spinner1 )
                this.addView( TextView(this.context).apply{this.text = resources.getString(R.string.new_input)})
                this.addView( edit1 )
                this.addView( edit2 )
                this.addView( TextView( this.context).apply{this.text = resources.getString(R.string.color_select)})
                this.addView( spinner2 )
                this.orientation = LinearLayout.VERTICAL
                this.setPadding(50)

            }
            val builder = AlertDialog.Builder( requireActivity()).apply{
                this.setView( linear )
                this.setPositiveButton(resources.getString(R.string.edit )){ dialog,which->
                    okSelected.invoke(id.toString().toLong(), edit1.text.toString() , edit2.text.toString(),color)

                }
                this.setNegativeButton(resources.getString(R.string.back )){dialog,which->
                    cancelSelected()
                }
            }

            return builder.create()
        }
    }

    // TODO: Rename and change types of parameters
    private var g_name : String? = null
    private var day: String? = null
    private var timed: Int? = null
    private var _binding : FragmentMakeTimeTableDayBinding? = null
    private val binding get() = _binding!!
    private lateinit var realm : Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            g_name = it.getString(ARG_PARAM1)
            day = it.getString(ARG_PARAM2)
            timed = it.getInt(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMakeTimeTableDayBinding.inflate( inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realm = Realm.getDefaultInstance()
        val ret = realm.where<TimeTable>()
            .equalTo("g_name",g_name)
            .equalTo("day",day)
            .lessThanOrEqualTo("timed",timed!!)
            .findAll()
        Log.d("realm","${g_name},${day},${timed}")
        val adapter = TimeTableAdapter( ret )
        binding.recyclerMakeTimeTableDay.adapter = adapter

        adapter.setOnItemClickListener { id,sub_name,sub_mentor, before_color->

            val dialog = EditGroupDialog( id ,g_name,sub_name,sub_mentor,before_color, { id, sub_name,sub_mentor ,after_color ->
                realm.executeTransaction {

                    //時間割に登録
                    val result = it.where<TimeTable>()
                        .equalTo("id",id)
                        .findFirst()
                    if( result == null ){
                        //なければ新しく作る
                        val max = it.where<TimeTable>().max("id")
                        val nextID = (max?.toLong() ?: 0L) + 1L

                        val item = it.createObject<TimeTable>(nextID)
                        item.g_name = g_name ?: ""
                        item.day = day ?: ""
                        item.timed = timed ?: 0
                        item.sub_name = sub_name ?: ""
                        item.sub_mentor = sub_mentor ?: ""
                        item.color = after_color ?: 0
                    }else {
                        //あったので更新のみ
                        result!!.sub_name = sub_name ?: ""
                        result!!.sub_mentor = sub_mentor ?: ""
                        result!!.color = after_color ?: 0
                    }

                    //時間割の色
                    val timeTableColor = it.where<TimeTableColor>()
                        .equalTo("g_name",g_name)
                        .equalTo("sub_name",sub_name)
                        .equalTo( "sub_mentor",sub_mentor)
                        .findFirst()
                    if( timeTableColor == null ){
                        //ないので作る
                        val max = it.where<TimeTableColor>().max( "id" )
                        val nextID = ( max?.toLong() ?: 0L ) + 1L
                        val item = it.createObject<TimeTableColor>( nextID )
                        item.g_name = g_name ?: ""
                        item.sub_name = sub_name ?: ""
                        item.sub_mentor = sub_mentor ?: ""
                        item.color = after_color ?: 0

                    }else{
                        timeTableColor.color = after_color ?: 0
                    }

                }

            },{
                Snackbar.make(binding.root,"キャンセルしました。", Snackbar.LENGTH_SHORT).show()
            })
            dialog.show(parentFragmentManager,"dialog")
        }
        adapter.setOnImageTrushClickListener { id->
            realm.executeTransaction {
                Log.d("id" , id.toString())
                val result = it.where<TimeTable>().equalTo("id",id).findFirst()
                result!!.sub_name = ""
                result!!.sub_mentor = ""
            }
        }

        binding.recyclerMakeTimeTableDay.layoutManager = LinearLayoutManager(context)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {
        fun newInstance(g_name:String,day: String, timed: Int) =
            MakeTimeTableDayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1,g_name)
                    putString(ARG_PARAM2, day)
                    putInt(ARG_PARAM3, timed)
                }
            }
    }
}