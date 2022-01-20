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
import tokyo.mstp015v.timetable_pro.databinding.FragmentEnterTimeTableForGroupBinding
import tokyo.mstp015v.timetable_pro.realm.Group
import tokyo.mstp015v.timetable_pro.tools.GroupRealmAdapter

class EnterTimeTableForGroupFragment : Fragment() {
    private var _binding : FragmentEnterTimeTableForGroupBinding? = null
    private val binding get() = _binding!!
    private lateinit var realm : Realm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEnterTimeTableForGroupBinding.inflate( inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()
        val ret = realm.where<Group>().findAll()

        val adapter = GroupRealmAdapter(ret)

        binding.recyclerEnterAttendanceForGroup.adapter = adapter
        adapter.setOnItemClickListener {
            val action = EnterTimeTableForGroupFragmentDirections.actionToEnterTimeTableFragment(it)
            findNavController().navigate( action )
        }
        binding.recyclerEnterAttendanceForGroup.layoutManager = LinearLayoutManager(context)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}