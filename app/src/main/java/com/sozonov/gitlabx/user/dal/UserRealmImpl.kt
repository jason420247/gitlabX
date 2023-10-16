package com.sozonov.gitlabx.user.dal

import com.sozonov.gitlabx.user.model.UserModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

internal class UserRealmImpl(private val config: RealmConfiguration) : IUserCache {

    private val realm: Realm get() = Realm.open(config)
    override suspend fun saveUser(userModel: UserModel) {
        val realm = realm
        realm.write {
            copyToRealm(userModel.let {
                UserDao(
                    it.id, it.username, it.fullName, it.avatarUrl, it.publicEmail
                )
            })
        }
        realm.close()
    }

    override suspend fun getUser(): UserModel? {
        val realm = realm
        val result = realm.query<UserDao>().find()
        if (result.size > 1) {
            realm.close()
            throw IllegalStateException("There was saved more than one user.")
        }
        val user = result.singleOrNull()
            ?.let { UserModel(it.id, it.username, it.name, it.avatarUrl, it.publicEmail) }
        realm.close()
        return user
    }

    override suspend fun deleteUser() {
        val realm = realm
        realm.write {
            delete(query<UserDao>().find())
        }
        realm.close()
    }
}