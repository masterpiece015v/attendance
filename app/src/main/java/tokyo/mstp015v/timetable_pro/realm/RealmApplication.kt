package tokyo.mstp015v.timetable_pro.realm

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init( this )
        val config = RealmConfiguration.Builder().allowWritesOnUiThread(true).build()
        Realm.setDefaultConfiguration( config )

    }
}