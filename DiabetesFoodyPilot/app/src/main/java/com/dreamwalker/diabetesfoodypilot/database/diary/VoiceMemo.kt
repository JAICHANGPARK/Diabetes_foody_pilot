
package com.dreamwalker.diabetesfoodypilot.database.diary

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class VoiceMemo : RealmObject() {
    var type: String? = null
    var memo: String? = null

    var rawDate: String? = null
    var rawTime: String? = null
    var datetime: String? = null
    var date: Date? = null
}