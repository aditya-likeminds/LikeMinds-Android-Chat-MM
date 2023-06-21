package com.likeminds.chatmm.moderation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReportExtras private constructor(
    val type: Int,
    val memberId: String?,
    val conversationId: String?,
    val chatroomId: String?,
    val chatroomName: String?,
    val conversationType: String?,
) : Parcelable {
    class Builder {
        private var type: Int = -1
        private var memberId: String? = null
        private var conversationId: String? = null
        private var chatroomId: String? = null
        private var chatroomName: String? = null
        private var conversationType: String? = null


        fun type(type: Int) = apply { this.type = type }
        fun memberId(memberId: String?) = apply { this.memberId = memberId }
        fun conversationId(conversationId: String?) = apply { this.conversationId = conversationId }
        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }
        fun chatroomName(chatroomName: String?) = apply { this.chatroomName = chatroomName }
        fun conversationType(conversationType: String?) =
            apply { this.conversationType = conversationType }

        fun build() = ReportExtras(
            type,
            memberId,
            conversationId,
            chatroomId,
            chatroomName,
            conversationType
        )
    }

    fun toBuilder(): Builder {
        return Builder().memberId(memberId)
            .type(type)
            .conversationId(conversationId)
            .chatroomId(chatroomId)
            .chatroomName(chatroomName)
            .conversationType(conversationType)
    }
}