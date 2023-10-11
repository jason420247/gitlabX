package com.sozonov.gitlabx.user

import android.util.Log
import com.sozonov.gitlabx.user.dal.UserDao
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import java.util.concurrent.CancellationException

internal class UserRealmImpl(private val config: RealmConfiguration) : IUserCache,
    IUserStateObserver {

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

    override val observeCreation: Flow<UserState.UserCreated>
        get() {
            val realm = realm
            val user = realm.query<UserDao>().find().asFlow()
            return user.filter { changes ->
                when (changes) {
                    is UpdatedResults -> {
                        changes.insertions.isNotEmpty()
                    }

                    is InitialResults -> {
                        changes.list.isNotEmpty()
                    }

                    else -> {
                        false
                    }
                }
            }
                .map { _ -> UserState.UserCreated }
                .catch { e ->
                    if (e !is CancellationException) {
                        Log.e("REALM", e.message, e)
                    }
                    realm.close()
                }
        }

    override val observeDeletion: Flow<UserState.UserDeleted>
        get() {
            val realm = realm
            val user = realm.query<UserDao>().find().asFlow()
            return user.filter { changes ->
                when (changes) {
                    is UpdatedResults -> {
                        changes.deletions.isNotEmpty()
                    }

                    is InitialResults -> {
                        changes.list.isNotEmpty()
                    }

                    else -> {
                        false
                    }
                }
            }
                .map { _ -> UserState.UserDeleted }
                .catch { e ->
                    if (e !is CancellationException) {
                        Log.e("REALM", e.message, e)
                    }
                    realm.close()
                }
        }
}