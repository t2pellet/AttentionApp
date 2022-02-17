package com.commodorethrawn.attentionapp.util

import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

object DatabaseUtil {

    data class Partner(
        val name : String,
        val token : String,
        val role : PreferenceUtil.Role
    )

    private val database = FirebaseDatabase.getInstance()

    fun getPartner(coupleId : String = PreferenceUtil.coupleId, role : PreferenceUtil.Role = PreferenceUtil.role) : Task<Partner?> {
        val partnerRole = if (role == PreferenceUtil.Role.SENDER)
                            PreferenceUtil.Role.RECEIVER
                          else PreferenceUtil.Role.SENDER
        return database.getReference(coupleId).get().continueWith {
            val snapshot = it.result
            if (snapshot != null && snapshot.exists()) {
                val partner = snapshot.child(partnerRole.name)
                return@continueWith Partner(partner.child("name").getValue<String>()!!,
                    partner.child("token").getValue<String>()!!,
                    partnerRole)
            } else {
                return@continueWith null
            }
        }
    }

    fun coupleExists(id : String = PreferenceUtil.coupleId) : Task<Boolean> {
        return database.getReference(id).get().continueWith {
            return@continueWith it.isSuccessful && it.result.exists()
        }
    }

    fun addToDB(id : String = PreferenceUtil.coupleId, role : PreferenceUtil.Role = PreferenceUtil.role) {
        if (id.isNotEmpty()) {
            val reference = database.getReference(id).child(role.name)
            reference.child("name").setValue(PreferenceUtil.name)
            reference.child("token").setValue(PreferenceUtil.token)
        } else {
            throw Exception("Tried to populate DB but couple ID is empty. Did something go wrong during setup?")
        }
    }
}