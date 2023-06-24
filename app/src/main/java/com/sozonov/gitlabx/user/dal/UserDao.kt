package com.sozonov.gitlabx.user.dal

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class UserDao(
    @PrimaryKey var id: Int,
    var username: String,
    var name: String,
    var avatarUrl: String,
    var publicEmail: String
) : RealmObject {
    constructor() : this(0, "", "", "", "")
}