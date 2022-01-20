package tokyo.mstp015v.timetable_pro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.kotlin.where
import tokyo.mstp015v.timetable_pro.databinding.FragmentMakeTimeTableForGroupBinding
import tokyo.mstp015v.timetable_pro.realm.Group
import tokyo.mstp015v.timetable_pro.tools.GroupRealmAdapter

class MakeTimeTableForGroupFragment : Fragment() {


    private var _binding : FragmentMakeTimeTableForGroupBinding? = null
    private val binding get() = _binding!!
    private lateinit var realm: Realm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMakeTimeTableForGroupBinding.inflate( inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        realm = Realm.getDefaultInstance()
        val ret = realm.where<Group>().distinct("g_name").findAll()
        val adapter = GroupRealmAdapter( ret )
        binding.recyclerMakeGropuForTimeTable.adapter = adapter
        adapter.setOnItemClickListener {
            val action = MakeTimeTableForGroupFragmentDirections.actionToMakeTimeTableFragment(it)
            findNavController().navigate( action )
        }
        binding.recyclerMakeGropuForTimeTable.layoutManager = LinearLayoutManager(this.context)

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}