package com.likeminds.chatmm.pushnotification.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.pushnotification.model.ChatroomNotificationViewData
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import javax.inject.Inject

class LMNotificationViewModel @Inject constructor(
    private val applicationContext: Application,
    private val sdkPreferences: SDKPreferences,
) : AndroidViewModel(applicationContext) {

    private val lmChatClient = LMChatClient.getInstance()

    fun fetchUnreadConversations(cb: (List<ChatroomNotificationViewData>?) -> Unit) {
        viewModelScope.launchIO {
            val response = lmChatClient.getUnreadConversationNotification()
            if (response.success) {
                val data = response.data?.unreadConversation ?: return@launchIO
                val conversations = ViewDataConverter.convertChatroomNotificationDataList(data)
                cb(conversations)
            } else {
                Log.e(
                    SDKApplication.LOG_TAG,
                    "unread notification failed: ${response.errorMessage}"
                )
            }
        }
    }

    fun sendChatroomResponded(
        conversation: ConversationViewData?,
        chatroomId: String,
        chatroomName: String
    ) {
        // todo: Analytics
//        LMAnalytics.track(LMAnalytics.Keys.CHATROOM_RESPONDED, JSONObject().apply {
//            put("chatroom_id", chatroomId)
//            put("chatroom_name", chatroomName)
//            put("message_type", "text")
//            put("message", conversation?.answer())
//            put("member_id", sdkPreferences.getMemberId())
//            put("source", "notification")
//        })
    }
}