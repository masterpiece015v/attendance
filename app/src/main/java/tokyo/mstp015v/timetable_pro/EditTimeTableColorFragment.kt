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
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.internal.ObservableCollection
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import tokyo.mstp015v.timetable_pro.databinding.FragmentEditTimeTableColorBinding
//import tokyo.mstp015v.timetable_pro.databinding.FragmentEditTimeTableColorBinding
import tokyo.mstp015v.timetable_pro.realm.Group
//import tokyo.mstp015v.timetable_pro.databinding.FragmentEditTimeTableColorBinding
import tokyo.mstp015v.timetable_pro.realm.TimeTable
import tokyo.mstp015v.timetable_pro.realm.TimeTableColor

class EditTimeTableColorFragment : Fragment() {
    //カスタムスピナーのアダプター
    class SpinnerAdapter(val context: Context):BaseAdapter(){
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
    //追加ダイアログ
    class AddDialog(
        val okSelected: (g_name:String?,sub_name:String?,sub_mentor:String?,color:Int?) -> Unit,
        val cancelSelected:()->Unit
    ): DialogFragment() {
        var color: Int? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            //val spinneritems = mutableMapOf<String, Int>()
            val list = mutableListOf<String>()
            list.add("")
            val realm = Realm.getDefaultInstance()
            val ret = realm.where<Group>().distinct("g_name").findAll()
            ret.forEach{
                list.add( it.g_name )
            }
            val adapter1 =
                ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item_1, list)
            adapter1.setDropDownViewResource(R.layout.spinner_dropdown_item_1)
            val spinner0 = Spinner(this.context).apply{
                this.adapter = adapter1
            }


            val edit1 = EditText(this.context).apply {
                this.hint = resources.getString(R.string.subject_name)
                this.width = 800
                this.textSize = 24.0f
            }
            val edit2 = EditText(this.context).apply {
                this.hint = resources.getString(R.string.subject_mentor)
                this.width = 800
                this.textSize = 24.0f
            }

            val adapter2 = SpinnerAdapter(requireContext())

            val spinner2 = Spinner(this.context).apply {
                this.adapter = adapter2
            }

            spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    // An item was selected. You can retrieve the selected item using
                    // parent.getItemAtPosition(pos)
                    Log.d("item", "select${parent.selectedItem}")
                    color = SpinnerAdapter.colors[parent.selectedItem]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                    Log.d("item", "nothing")
                }

            }

            //表示する
            val linear = LinearLayout(this.context).apply {
                this.addView(TextView(this.context).apply {
                    this.text = resources.getString(R.string.history_select)
                })

                this.addView( spinner0 )

                this.addView(TextView(this.context).apply {
                    this.text = resources.getString(R.string.new_input)
                })
                this.addView(edit1)
                this.addView(edit2)
                this.addView(TextView(this.context).apply {
                    this.text = resources.getString(R.string.color_select)
                })
                this.addView(spinner2)
                this.orientation = LinearLayout.VERTICAL
                this.setPadding(50)

            }
            val builder = AlertDialog.Builder(requireActivity()).apply {
                this.setView(linear)
                this.setPositiveButton(resources.getString(R.string.edit)) { dialog, which ->
                    okSelected.invoke(
                        spinner0.selectedItem as String ,
                        edit1.text.toString(),
                        edit2.text.toString(),
                        color)

                }
                this.setNegativeButton(resources.getString(R.string.back)) { dialog, which ->
                    cancelSelected()
                }
            }

            return builder.create()
        }
    }
    //ダイアログ
    class EditDialog(
        val id:Long,
        val g_name : String?,
        val sub_name: String?,
        val sub_mentor:String?,
        val before_color:Int?,
        val okSelected: (id:Long,g_name:String?,sub_name:String?,sub_mentor:String?,color:Int?) -> Unit,
        val cancelSelected:()->Unit
    ): DialogFragment() {
        var color: Int? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            //val spinneritems = mutableMapOf<String, Int>()
            val realm = Realm.getDefaultInstance()
            val ret = realm.where<Group>().equalTo("g_name", g_name).findAll()
            val list = mutableListOf<String>()
            list.add("")
            ret.forEach {
                //spinneritems.put("${it.sub_name}_${it.sub_mentor}", it.color)
                list.add( it.g_name )
            }
            realm.close()
            val adapter1 =
                ArrayAdapter(requireContext(),R.layout.spinner_dropdown_item_1, list)
            adapter1.setDropDownViewResource(R.layout.spinner_dropdown_item_1)

            var i = list.indexOf( g_name )

            val spinner0 = Spinner(this.context).apply{
                this.adapter = adapter1
                this.setSelection( i )
            }
            //val edit0 = EditText(this.context).apply {
            //    this.hint = resources.getString(R.string.group_edit)
            //    this.setText( g_name )
            //    this.width = 800
            //    this.textSize = 24.0f
            //}

            val edit1 = EditText(this.context).apply {
                this.hint = resources.getString(R.string.subject_name)
                this.setText(sub_name)
                this.width = 800
                this.textSize = 24.0f
            }
            val edit2 = EditText(this.context).apply {
                this.hint = resources.getString(R.string.subject_mentor)
                this.setText(sub_mentor)
                this.width = 800
                this.textSize = 24.0f
            }

            val adapter2 = SpinnerAdapter(requireContext())

            val spinner2 = Spinner(this.context).apply {
                this.adapter = adapter2
            }

            spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    // An item was selected. You can retrieve the selected item using
                    // parent.getItemAtPosition(pos)
                    Log.d("item", "select${parent.selectedItem}")
                    color = SpinnerAdapter.colors[parent.selectedItem]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Another interface callback
                    Log.d("item", "nothing")
                }

            }
            //すでに色を持っていればその色にする
            spinner2.setSelection(SpinnerAdapter.revcolors.get(before_color) ?: 0)

            //表示する
            val linear = LinearLayout(this.context).apply {
                this.addView(TextView(this.context).apply {
                    this.text = resources.getString(R.string.history_select)
                })
                this.addView(spinner0)
                this.addView(TextView(this.context).apply {
                    this.text = resources.getString(R.string.new_input)
                })
                this.addView(edit1)
                this.addView(edit2)
                this.addView(TextView(this.context).apply {
                    this.text = resources.getString(R.string.color_select)
                })
                this.addView(spinner2)
                this.orientation = LinearLayout.VERTICAL
                this.setPadding(50)

            }
            val builder = AlertDialog.Builder(requireActivity()).apply {
                this.setView(linear)
                this.setPositiveButton(resources.getString(R.string.edit)) { dialog, which ->
                    okSelected.invoke(id.toString().toLong(),
                        //edit0.text.toString(),
                        spinner0.selectedItem.toString(),
                        edit1.text.toString(),
                        edit2.text.toString(),
                        color)

                }
                this.setNegativeButton(resources.getString(R.string.back)) { dialog, which ->
                    cancelSelected()
                }
            }

            return builder.create()
        }
    }

    //reamlアダプター
    class RealmAdapter( data : OrderedRealmCollection<TimeTableColor> ,val manager : FragmentManager ) : RealmRecyclerViewAdapter<TimeTableColor,RealmAdapter.ViewHolder>(data,true){

        lateinit var listener : (Long?)->Unit
        lateinit var clicklistener : (Long?)->Unit

        fun setOnImageTrushClickListener( listener :(Long?)->Unit ){
            this.listener = listener
        }

        fun setOnListClickListener( clicklistener : (Long?)->Unit ){
            this.clicklistener = clicklistener
        }

        class ViewHolder(view:View): RecyclerView.ViewHolder(view){
            val g_name = view.findViewById<TextView>(R.id.textEditTimeTableColorGroup)
            val sub_name = view.findViewById<TextView>(R.id.textEditTimeTableColorSubject)
            val sub_mentor = view.findViewById<TextView>(R.id.textEditTimeTableColorMentor)
            val linear = view.findViewById<LinearLayout>(R.id.linearEditTableColor)
            val imagetrush = view.findViewById<ImageButton>(R.id.imageTrushEditTimeTableColor)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from( parent.context ).inflate(R.layout.edit_time_table_color_item,parent,false)
            return ViewHolder( view )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem( position )
            holder.g_name.text = item?.g_name ?: ""
            holder.sub_name.text = item?.sub_name ?: ""
            holder.sub_mentor.text = item?.sub_mentor ?: ""
            holder.linear.setBackgroundColor( item?.color ?: Color.parseColor("#FFFFFF") )

            //タップイベント（更新）
            holder.linear.setOnClickListener {
                val dialog = EditDialog( item!!.id , item.g_name,item.sub_name,item.sub_mentor,item.color,{
                        id,g_name,sub_name,sub_mentor,color->

                    val realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        val ret = it.where<TimeTableColor>().equalTo("id",id).findFirst()
                        ret!!.g_name = g_name ?: ""
                        ret!!.sub_name = sub_name ?: ""
                        ret!!.sub_mentor = sub_mentor ?: ""
                        ret!!.color = color ?: Color.parseColor("#FFFFFF")

                    }
                    realm.close()

                },{

                } )
                dialog.show( manager ,"dialog" )
            }
            //長押しイベント
            holder.linear.setOnLongClickListener {

                if( holder.imagetrush!!.visibility == ImageButton.VISIBLE){
                    holder.imagetrush!!.visibility = ImageButton.INVISIBLE
                    holder.linear.setBackgroundColor( item?.color ?: Color.parseColor("#FFFFFF") )
                }else{
                    holder.imagetrush!!.visibility = ImageButton.VISIBLE
                    holder.linear.setBackgroundColor( Color.parseColor("#CCCCCC") )

                }
                return@setOnLongClickListener true
            }
            //ゴミ箱ボタンクリックイベント
            holder.imagetrush.setOnClickListener {
                listener.invoke( item?.id )
            }
        }
    }

    private var _binding : FragmentEditTimeTableColorBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentEditTimeTableColorBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }
    lateinit var realm : Realm
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realm = Realm.getDefaultInstance()
        val result = realm.where<TimeTableColor>().findAll()
        val adapter = RealmAdapter( result , parentFragmentManager )
        binding.recyclerEditTimeTableColor.adapter = adapter
        adapter.setOnImageTrushClickListener { id->
            realm.executeTransaction {
                val delete = it.where<TimeTableColor>().equalTo("id",id ).findFirst()

                if( delete != null ){
                    delete.deleteFromRealm()
                }
            }
        }
        binding.recyclerEditTimeTableColor.layoutManager = LinearLayoutManager(context)

        binding.buttonTimeTableColorAdd.setOnClickListener {
            val dialog = AddDialog({
                g_name,sub_name,sub_mentor,color->

                Log.d("add", "${g_name},${sub_name},${sub_mentor},${color}")
                val realm = Realm.getDefaultInstance()
                val max:Long? = realm.where<TimeTableColor>().max("id") as Long
                val nextid = ( max ?: 0L )+ 1L

                realm.executeTransaction {
                    val newrow = it.createObject<TimeTableColor>(nextid)
                    newrow.g_name = g_name ?: ""
                    newrow.sub_name = sub_name ?: ""
                    newrow.sub_mentor = sub_mentor ?: ""
                    newrow.color = color ?: Color.parseColor("#FFFFFF")
                }

            },{})

            dialog.show( parentFragmentManager,"dialog ")
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        _binding = null
        realm.close()
    }
}