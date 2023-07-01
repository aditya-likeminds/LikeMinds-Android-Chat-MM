package com.likeminds.chatmm.utils

import com.likeminds.chatmm.chatroom.detail.model.MemberViewData

object MemberUtil {

    fun getFirstNameToShow(
        sdkPreferences: SDKPreferences,
        memberViewData: MemberViewData?
    ): String {
        val memberUUID = memberViewData?.sdkClientInfo?.uuid
        return if (memberViewData == null) ""
        else if (sdkPreferences.getUUID() == memberUUID) "You:"
        else {
            val name = memberViewData.name?.trim()?.split(" ")?.get(0)
            if (name != null) "$name:" else ""
        }
    }

    fun getMemberNameForDisplay(
        member: MemberViewData,
        currentMemberId: String
    ): String {
        return if (currentMemberId == member.sdkClientInfo.uuid) {
            "You"
        } else {
            member.name ?: ""
        }
    }
}