package com.likeminds.chatmm.utils

import android.net.Uri
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.chatroom.detail.model.SDKClientInfoViewData
import com.likeminds.chatmm.chatroom.explore.model.ExploreViewData
import com.likeminds.chatmm.conversation.model.AttachmentMetaViewData
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.LinkOGTagsViewData
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM
import com.likeminds.likemindschat.chatroom.model.Chatroom
import com.likeminds.likemindschat.community.model.Member
import com.likeminds.likemindschat.conversation.model.Attachment
import com.likeminds.likemindschat.conversation.model.Conversation
import com.likeminds.likemindschat.conversation.model.LinkOGTags
import com.likeminds.likemindschat.helper.model.GroupTag
import com.likeminds.likemindschat.user.model.SDKClientInfo
import com.likeminds.likemindschat.user.model.User

object ViewDataConverter {

    /**--------------------------------
     * Network Model -> View Data Model
    --------------------------------*/

    // converts Chatroom network model to view data model
    fun convertChatroom(chatroom: Chatroom): ChatroomViewData {
        // todo: member state
        return ChatroomViewData.Builder()
            .id(chatroom.id)
            .communityId(chatroom.communityId)
            .communityName(chatroom.communityName)
            .memberViewData(convertMember(chatroom.member))
            .createdAt(chatroom.createdAt)
            .title(chatroom.title)
            .answerText(chatroom.answerText)
            .state(chatroom.state)
            .type(chatroom.type)
            .header(chatroom.header)
            .dynamicViewType(ITEM_HOME_CHAT_ROOM)
            .muteStatus(chatroom.muteStatus)
            .followStatus(chatroom.followStatus)
            .date(chatroom.date)
            .isTagged(chatroom.isTagged)
            .isPending(chatroom.isPending)
            .deletedBy(chatroom.deletedBy)
            .updatedAt(chatroom.updatedAt)
            .isSecret(chatroom.isSecret)
            .unseenCount(chatroom.unseenCount)
            .isEdited(chatroom.isEdited)
            .chatroomImageUrl(chatroom.chatroomImageUrl)
            .build()
    }

    fun convertChatroom(
        chatroom: Chatroom?,
        memberUUID: String,
        sortIndex: Int
    ): ExploreViewData? {
        if (chatroom == null) return null
        return ExploreViewData.Builder()
            .isPinned(chatroom.isPinned)
            .isCreator(chatroom.member?.sdkClientInfo?.uuid == memberUUID)
            .externalSeen(chatroom.externalSeen)
            .isSecret(chatroom.isSecret)
            .followStatus(chatroom.followStatus)
            .participantsCount(chatroom.participantsCount?.toIntOrNull())
            .totalResponseCount(chatroom.totalResponseCount)
            .sortIndex(sortIndex)
            .id(chatroom.id)
            .header(chatroom.header)
            .title(chatroom.title)
            .imageUrl(chatroom.member?.imageUrl)
            .chatroomImageUrl(chatroom.chatroomImageUrl)
            .chatroomViewData(convertChatroom(chatroom))
            .build()
    }

    // todo: change from backend in deletedBy key
    /**
     * convert [Conversation] to [ConversationViewData]
     */
    fun convertConversation(conversation: Conversation?): ConversationViewData? {
        if (conversation == null) {
            return null
        }
        return ConversationViewData.Builder()
            .id(conversation.id ?: "")
            .memberViewData(convertMember(conversation.member))
            .createdAt(conversation.createdAt.toString())
            .answer(conversation.answer)
            .state(conversation.state)
            .attachments(
                conversation.attachments?.mapNotNull { attachment ->
                    convertAttachment(attachment)
                }?.let {
                    ArrayList(it)
                }
            )
            .ogTags(convertOGTags(conversation.ogTags))
            .date(conversation.date)
            .deletedBy(conversation.deletedBy)
            .attachmentCount(conversation.attachmentCount)
            .attachmentsUploaded(conversation.attachmentUploaded)
            .uploadWorkerUUID(conversation.uploadWorkerUUID)
            .shortAnswer(ViewMoreUtil.getShortAnswer(conversation.answer, 1000))
            .build()
    }

    // converts Member network model to view data model
    private fun convertMember(member: Member?): MemberViewData {
        if (member == null) {
            return MemberViewData.Builder().build()
        }
        return MemberViewData.Builder()
            .id(member.id)
            .name(member.name)
            .imageUrl(member.imageUrl)
            .state(member.state ?: 0)
            .customIntroText(member.customIntroText)
            .customClickText(member.customClickText)
            .customTitle(member.customTitle)
            .communityId(member.communityId.toString())
            .isOwner(member.isOwner)
            .isGuest(member.isGuest)
            .sdkClientInfo(convertSDKClientInfo(member.sdkClientInfo))
            .build()
    }

    // converts SDKClientInfo network model to view data model
    private fun convertSDKClientInfo(
        sdkClientInfo: SDKClientInfo?
    ): SDKClientInfoViewData {
        if (sdkClientInfo == null) {
            return SDKClientInfoViewData.Builder().build()
        }
        return SDKClientInfoViewData.Builder()
            .communityId(sdkClientInfo.community)
            .user(sdkClientInfo.user)
            .userUniqueId(sdkClientInfo.userUniqueId)
            .uuid(sdkClientInfo.uuid)
            .build()
    }

    // converts LinkOGTags network model to view data model
    private fun convertOGTags(
        linkOGTags: LinkOGTags?
    ): LinkOGTagsViewData? {
        if (linkOGTags == null) {
            return null
        }
        return LinkOGTagsViewData.Builder()
            .title(linkOGTags.title)
            .image(linkOGTags.image)
            .description(linkOGTags.description)
            .url(linkOGTags.url)
            .build()
    }

    private fun convertAttachment(
        attachment: Attachment?,
        title: String? = null,
        subTitle: String? = null
    ): AttachmentViewData? {
        if (attachment == null) {
            return null
        }
        val attachmentMeta = if (attachment.meta != null) {
            AttachmentMetaViewData.Builder()
                .duration(attachment.meta?.duration)
                .numberOfPage(attachment.meta?.numberOfPage)
                .size(attachment.meta?.size)
                .build()
        } else {
            null
        }
        return AttachmentViewData.Builder()
            .id(attachment.id)
            .name(attachment.name)
            .uri(Uri.parse(attachment.url))
            .type(attachment.type)
            .index(attachment.index)
            .width(attachment.width)
            .height(attachment.height)
            .title(title)
            .subTitle(subTitle)
            .awsFolderPath(attachment.awsFolderPath)
            .localFilePath(attachment.localFilePath)
            .thumbnail(attachment.thumbnailUrl)
            .thumbnailAWSFolderPath(attachment.thumbnailAWSFolderPath)
            .thumbnailLocalFilePath(attachment.thumbnailLocalFilePath)
            .meta(attachmentMeta)
            .build()
    }

    // todo: have to be refactored
    fun convertUser(user: User?): MemberViewData? {
        if (user == null) {
            return null
        }
        return MemberViewData.Builder()
            .id(user.id)
            .name(user.name)
            .imageUrl(user.imageUrl)
            .customTitle(user.customTitle)
            .isGuest(user.isGuest)
            .build()
    }

    fun convertGroupTag(groupTag: GroupTag?): TagViewData? {
        if (groupTag == null) return null
        return TagViewData.Builder()
            .name(groupTag.name)
            .imageUrl(groupTag.imageUrl)
            .tag(groupTag.tag)
            .route(groupTag.route)
            .description(groupTag.description)
            .build()
    }

    // todo: check id
    fun convertMemberTag(memberTag: Member?): TagViewData? {
        if (memberTag == null) return null
        val uuid = memberTag.sdkClientInfo?.uuid
        val nameDrawable = MemberImageUtil.getNameDrawable(
            MemberImageUtil.SIXTY_PX,
            uuid,
            memberTag.name
        )
        return TagViewData.Builder()
            .name(memberTag.name)
            .id(memberTag.id.toInt())
            .imageUrl(memberTag.imageUrl)
            .isGuest(memberTag.isGuest)
            .userUniqueId(memberTag.userUniqueId)
            .placeHolder(nameDrawable.first)
            .sdkClientInfo(convertSDKClientInfo(memberTag.sdkClientInfo))
            .build()
    }

    /**
     * convert [LinkOGTags] to [LinkOGTagsViewData]
     * @param linkOGTags: object of [LinkOGTags]
     **/
    fun convertLinkOGTags(linkOGTags: LinkOGTags): LinkOGTagsViewData {
        return LinkOGTagsViewData.Builder()
            .url(linkOGTags.url)
            .description(linkOGTags.description)
            .title(linkOGTags.title)
            .image(linkOGTags.image)
            .build()
    }
}