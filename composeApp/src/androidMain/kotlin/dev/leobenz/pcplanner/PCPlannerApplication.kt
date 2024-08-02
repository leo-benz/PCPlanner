import android.app.Application
import di.initKoin

class PCPlannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}