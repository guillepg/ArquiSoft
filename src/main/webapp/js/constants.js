angular.module('starter')

    .constant("API", {
        "URL": "http://"+window.location.host+"/myusick/api",
        "WS_URL": "http://"+window.location.host+"/myusick/api/ws/sub/",
        "WS_URL_UNSUB": "http://"+window.location.host+"/myusick/api/ws/unsub/",
        "AUTH_ENDPOINT": "/auth",
        "REGISTER_ENDPOINT": "/register",
        "PROFILE_ENDPOINT": "/profile/",
        "CREATE_BAND_ENDPOINT": "/newgroup",
        "TAG_ENDPOINT": "/newtag",
        "POST_ENDPOINT": "/post/",
        "FOLLOW_ENDPOINT": "/follow/",
        "UNFOLLOW_ENDPOINT": "/unfollow/",
        "ISFOLLOW_ENDPOINT": "/isfollowing/",
        "FOLLOWERS_ENDPOINT": "/followers/",
        "FOLLOWING_ENDPOINT": "/following/",
        "BAND_APPLICANTS_ENDPOINT": "/band/applicants/",
        "ACCEPT_APPLICANT_ENDPOINT": "/band/accept/",
        "REJECT_APPLICANT_ENDPOINT": "/band/reject/",
        "BAND_APPLY_ENDPOINT": "/band/apply/",
        "BAND_LEAVE_ENDPOINT": "/band/leave/",
        "USER_GROUPS_ENDPOINT": "/groups/",
        "LAST_MESSAGES_ENDPOINT": "/last/",
        "SEARCH_PERSON_ENDPOINT": "/search/person/",
        "SEARCH_GROUP_ENDPOINT": "/search/group/",
        "SEARCH_TAG_ENDPOINT": "/search/tag/",
        "SEARCH_SKILL_ENDPOINT": "/search/skill/"
    });
