package com.likeminds.chatmm.report.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likeminds.chatmm.report.model.ReportTagViewData
import com.likeminds.chatmm.utils.ViewDataConverter
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.likemindschat.LMChatClient
import com.likeminds.likemindschat.LMResponse
import com.likeminds.likemindschat.moderation.model.GetReportTagsRequest
import com.likeminds.likemindschat.moderation.model.GetReportTagsResponse
import com.likeminds.likemindschat.moderation.model.PostReportRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class ReportViewModel @Inject constructor() : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    private val _postReportResponse = MutableLiveData<Boolean>()
    val postReportResponse: LiveData<Boolean> = _postReportResponse

    private val _listOfTagViewData = MutableLiveData<List<ReportTagViewData>>()
    val listOfTagViewData: LiveData<List<ReportTagViewData>> = _listOfTagViewData

    private val errorMessageChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorMessageEventFlow = errorMessageChannel.receiveAsFlow()

    sealed class ErrorMessageEvent {
        data class PostReport(val errorMessage: String?) : ErrorMessageEvent()
        data class GetReportTags(val errorMessage: String?) : ErrorMessageEvent()
    }

    //for reporting member.conversation
    fun postReport(
        tagId: Int?,
        reportedMemberId: String?,
        reportedConversationId: String?,
        reason: String?
    ) {
        viewModelScope.launchIO {
            //if reason is empty then send [null] in request
            val updatedReason = if (reason.isNullOrEmpty()) null else reason

            //create request
            val request = PostReportRequest.Builder()
                .tagId(tagId ?: 0)
                .reason(updatedReason)
                .reportedMemberId(reportedMemberId?.toInt())
                .reportedConversationId(reportedConversationId?.toInt())
                .build()

            val response = lmChatClient.postReport(request)

            if (response.success) {
                _postReportResponse.postValue(true)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.PostReport(response.errorMessage))
            }
        }
    }

    //Get report tags for reporting
    fun getReportTags(type: Int) {
        viewModelScope.launchIO {
            val request = GetReportTagsRequest.Builder()
                .type(type)
                .build()

            reportTagsFetched(lmChatClient.getReportTags(request))
        }
    }

    //to convert to TagViewData
    private fun reportTagsFetched(response: LMResponse<GetReportTagsResponse>) {
        viewModelScope.launchIO {
            if (response.success) {
                val data = response.data ?: return@launchIO
                val tags = data.tags
                val tagsViewData = ViewDataConverter.convertReportTag(tags)
                _listOfTagViewData.postValue(tagsViewData)
            } else {
                errorMessageChannel.send(ErrorMessageEvent.GetReportTags(response.errorMessage))
            }
        }
    }

    // todo:
    /**------------------------------------------------------------
     * Analytics
    ---------------------------------------------------------------*/

    /**
     * Triggers when a user clicks on the report member button from another user profile
     * @param memberId: Id of the reported member
     * */
    fun sendMemberProfileReport(memberId: String?) {
//        val communityId = sdkPreferences.getCommunityId()
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_MEMBER_PROFILE_REPORT,
//            "community_id" to communityId,
//            "reported_user_id" to memberId
//        )
    }

    /**
     * Triggers when a user selects a message and chooses ‘Report the message’ from the menu
     * @param conversationId: id of the message reported
     * @param issue: tag selected for the report
     * @param chatroomId: id of chatroom in which message was sent
     * @param chatroomName: name of the chatroom in which message was sent
     * @param conversationType: type of message, like: text, image, video etc.
     **/
    fun sendMessageReportedEvent(
        conversationId: String?,
        issue: String?,
        chatroomId: String?,
        chatroomName: String?,
        conversationType: String?
    ) {
//        val userId = loginPreferences.getMemberId() //id of the user reporting the message
//        val communityId = sdkPreferences.getCommunityId()
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_MESSAGE_REPORTED,
//            "conversation_id" to conversationId,
//            "community_id" to communityId,
//            "chatroom_id" to chatroomId,
//            "chatroom_name" to chatroomName,
//            "user_id" to userId,
//            "type" to conversationType,
//            "issue" to issue
//        )
    }

    /**
     * Triggers when a user submits a report for a member
     * @param reportedMemberId: id of the member reported
     * @param issue: tag selected for report
     */
    fun sendMemberProfileReportConfirmed(
        reportedMemberId: String?,
        issue: String?
    ) {
//        val communityId = sdkPreferences.getCommunityId()
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_MEMBER_PROFILE_REPORT_CONFIRMED,
//            "community_id" to communityId,
//            "reported_user_id" to reportedMemberId,
//            "issue" to issue
//        )
    }
}