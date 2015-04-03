

package com.aumum.app.mobile.core;

import java.util.HashMap;

/**
 * Bootstrap constants
 */
public final class Constants {
    private Constants() {}

    public static final String APP_NAME = "com.aumum.app.mobile";

    public static final class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "com.aumum.app.mobile";

        /**
         * Auth token type
         */
        public static final String AUTH_TOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;

        /**
         * Account email
         */
        public static final String KEY_ACCOUNT_EMAIL = "accountEmail";
    }

    /**
     * All HTTP is done through a REST style API built for demonstration purposes on Parse.com
     * Thanks to the nice people at Parse for creating such a nice system for us to use for bootstrap!
     */
    public static final class Http {
        private Http() {}


        /**
         * Base URL for all requests
         */
        public static final String URL_BASE = "https://api.parse.com";


        /**
         * Login URL
         */
        public static final String URL_LOGIN_FRAG = "/1/login";

        /**
         * Reset Password URL
         */
        public static final String URL_RESET_PASSWORD_FRAG = "/1/requestPasswordReset";

        /**
         * Batch URL
         */
        public static final String URL_BATCH_FRAG = "/1/batch";

        /**
         * List Users URL
         */
        public static final String URL_USERS_FRAG = "/1/users";

        /**
         * User By Id URL
         */
        public static final String URL_USER_BY_ID_FRAG = "/1/users/{id}";

        /**
         * List Parties URL
         */
        public static final String URL_PARTIES_FRAG = "/1/classes/Parties";

        /**
         * Party By Id URL
         */
        public static final String URL_PARTY_BY_ID_FRAG = "/1/classes/Parties/{id}";

        /**
         * List Party Comments URL
         */
        public static final String URL_PARTY_COMMENTS_FRAG = "/1/classes/PartyComments";

        /**
         * Party Comment By Id URL
         */
        public static final String URL_PARTY_COMMENT_BY_ID_FRAG = "/1/classes/PartyComments/{id}";

        /**
         * List Party Reasons URL
         */
        public static final String URL_PARTY_REASONS_FRAG = "/1/classes/PartyReasons";

        /**
         * List Askings URL
         */
        public static final String URL_ASKINGS_FRAG = "/1/classes/Askings";

        /**
         * Asking By Id URL
         */
        public static final String URL_ASKING_BY_ID_FRAG = "/1/classes/Askings/{id}";

        /**
         * List Asking Reply URL
         */
        public static final String URL_ASKING_REPLIES_FRAG = "/1/classes/AskingReplies";

        /**
         * Asking Reply By Id URL
         */
        public static final String URL_ASKING_REPLY_BY_ID_FRAG = "/1/classes/AskingReplies/{id}";

        /**
         * List Report URL
         */
        public static final String URL_REPORTS_FRAG = "/1/classes/Reports";

        /**
         * List Feedback URL
         */
        public static final String URL_FEEDBACK_FRAG = "/1/classes/Feedback";

        /**
         * List Area URL
         */
        public static final String URL_AREAS_FRAG = "/1/classes/Areas";

        /**
         * List City Group URL
         */
        public static final String URL_CITY_GROUP_FRAG = "/1/classes/CityGroup";

        /**
         * List User Tags URL
         */
        public static final String URL_USER_TAGS_FRAG = "/1/classes/UserTags";

        /**
         * List Moments URL
         */
        public static final String URL_MOMENTS_FRAG = "/1/classes/Moments";

        /**
         * Moment By Id URL
         */
        public static final String URL_MOMENT_BY_ID_FRAG = "/1/classes/Moments/{id}";

        /**
         * List Moment Comments URL
         */
        public static final String URL_MOMENT_COMMENTS_FRAG = "/1/classes/MomentComments";

        /**
         * Moment Comment By Id URL
         */
        public static final String URL_MOMENT_COMMENT_BY_ID_FRAG = "/1/classes/MomentComments/{id}";

        /**
         * List Special URL
         */
        public static final String URL_SPECIALS_FRAG = "/1/classes/Specials";

        /**
         * List Special Products URL
         */
        public static final String URL_SPECIAL_PRODUCTS_FRAG = "/1/classes/SpecialProducts";

        /**
         * Special Product By Id URL
         */
        public static final String URL_SPECIAL_PRODUCT_BY_ID_FRAG = "/1/classes/SpecialProducts/{id}";

        /**
         * List Party Requests URL
         */
        public static final String URL_PARTY_REQUESTS_FRAG = "/1/classes/PartyRequests";

        /**
         * Party Request By Id URL
         */
        public static final String URL_PARTY_REQUEST_BY_ID_FRAG = "/1/classes/PartyRequests/{id}";

        /**
         * List Feed URL
         */
        public static final String URL_FEEDS_FRAG = "/1/classes/Feeds";

        /**
         * List Event Category URL
         */
        public static final String URL_EVENT_CATEGORIES_FRAG = "/1/classes/EventCategories";

        /**
         * List Game URL
         */
        public static final String URL_GAMES_FRAG = "/1/classes/Games";

        /**
         * List Credit Gift URL
         */
        public static final String URL_CREDIT_GIFTS_FRAG = "/1/classes/CreditGifts";

        /**
         * List Credit Order URL
         */
        public static final String URL_CREDIT_ORDERS_FRAG = "/1/classes/CreditOrders";

        /**
         * List Asking Group URL
         */
        public static final String URL_ASKING_GROUPS_FRAG = "/1/classes/AskingGroups";

        /**
         * List Asking Board URL
         */
        public static final String URL_ASKING_BOARDS_FRAG = "/1/classes/AskingBoards";

        /**
         * PARAMS for auth
         */
        public static final String PARAM_USERNAME = "username";
        public static final String PARAM_PASSWORD = "password";
        public static final String PARAM_EMAIL = "email";

        /* Prod Keys */
        public static final String PARSE_APP_ID = "1CWTxIB11kQiHz7QAY1hZzA1PstlJ2TQuAmZ8Nc6";
        public static final String PARSE_CLIENT_KEY = "Cxo0Yu68VFoZSDyHVl43qMTfYb23AG9uBrbOqgFk";
        public static final String PARSE_REST_API_KEY = "YT6Gt3CQUEtLkQsiCMMAnBIm7FrGkX8sqWMNElNx";
        public static final String PARSE_MASTER_KEY = "MMtsR9A9hywGlZ505DD43VbQwPIu7XT6KFVQpJm8";

        /* Dev Keys
        public static final String PARSE_APP_ID = "hJSBmj3YSXBuZkpXIPuFbR3nZiIZWr0uNfCFBXLl";
        public static final String PARSE_CLIENT_KEY = "8hexm2xMkmMrS5Y1vzXOpmPGr98lvCyO1IIX0ejM";
        public static final String PARSE_REST_API_KEY = "bLKzd37O5lF6o11FdQ2q0NwQferhjEEvIXFVxEcA";
        public static final String PARSE_MASTER_KEY = "2Cjggz9DM0HCCdhlmps7YuWk1SE1vnxnqjJhz3SI";
        */
        public static final String HEADER_PARSE_REST_API_KEY = "X-Parse-REST-API-Key";
        public static final String HEADER_PARSE_APP_ID = "X-Parse-Application-Id";
        public static final String HEADER_PARSE_MASTER_KEY = "X-Parse-Master-Key";
        public static final String HEADER_PARSE_SESSION_TOKEN = "X-Parse-Session-Token";

        public static final class Batch {
            public static final String PARAM_REQUESTS = "requests";
        }

        public static final class Party {
            public static final String PARAM_MEMBERS = "members";
            public static final String PARAM_LIKES = "likes";
            public static final String PARAM_COMMENTS = "comments";
            public static final String PARAM_REASONS = "reasons";
            public static final String PARAM_FAVORITES = "favorites";
            public static final String PARAM_GROUP_ID = "groupId";
        }

        public static final class PartyComment {
            public static final String PARAM_LIKES = "likes";
        }

        public static final class User {
            public static final String PARAM_CONTACTS = "contacts";
            public static final String PARAM_PARTIES = "parties";
            public static final String PARAM_AVATAR_URL = "avatarUrl";
            public static final String PARAM_SCREEN_NAME = "screenName";
            public static final String PARAM_EMAIL = "email";
            public static final String PARAM_CITY = "city";
            public static final String PARAM_AREA = "area";
            public static final String PARAM_TAGS = "tags";
            public static final String PARAM_ABOUT = "about";
            public static final String PARAM_CHAT_ID = "chatId";
            public static final String PARAM_ASKINGS = "askings";
            public static final String PARAM_MOMENTS = "moments";
            public static final String PARAM_PARTY_FAVORITES = "favParties";
            public static final String PARAM_ASKING_FAVORITES = "favAskings";
            public static final String PARAM_SPECIAL_FAVORITES = "favSpecials";
            public static final String PARAM_ASKING_GROUPS = "askingGroups";
            public static final String PARAM_CREDIT = "credit";
        }

        public static final class Asking {
            public static final String PARAM_REPLIES = "replies";
            public static final String PARAM_LIKES = "likes";
            public static final String PARAM_FAVORITES = "favorites";
        }

        public static final class AskingReply {
            public static final String PARAM_LIKES = "likes";
        }

        public static final class Moment {
            public static final String PARAM_LIKES = "likes";
            public static final String PARAM_COMMENTS = "comments";
        }

        public static final class MomentComment {
            public static final String PARAM_LIKES = "likes";
        }

        public static final class Special {
            public static final String PARAM_LIKES = "likes";
        }

        public static final class SpecialProduct {
            public static final String PARAM_LIKES = "likes";
            public static final String PARAM_FAVORITES = "favorites";
        }
    }

    public static final class DateTime {
        public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    }

    public static final class Options {
        private Options() {
        }

        public static final String COUNTRY_CODES[] = {
                "+61",
                "+64",
                "+86"
        };

        public static final String COUNTRY_OPTIONS[] = {
            "澳大利亚",
            "新西兰",
            "中国"
        };

        public static final String CITY_OPTIONS[] = {
            "悉尼",
            "墨尔本",
            "布里斯班",
            "珀斯",
            "阿德莱德",
            "堪培拉",
            "达尔文",
            "霍巴特",
            "奥克兰",
            "惠灵顿",
            "北京",
            "上海",
            "深圳",
            "广州",
            "其他城市"
        };

        public static HashMap<String, Integer> CITY_ID = new HashMap<String, Integer>(){
            {
                put("悉尼", 0);
                put("墨尔本", 1);
                put("布里斯班", 2);
                put("珀斯", 3);
                put("阿德莱德", 4);
                put("堪培拉", 5);
                put("达尔文", 6);
                put("霍巴特", 7);
                put("奥克兰", 8);
                put("惠灵顿", 9);
                put("北京", 10);
                put("上海", 11);
                put("深圳", 12);
                put("广州", 13);
                put("其他城市", 1000);
            }
        };
    }

    public static final class RequestCode {
        private RequestCode() {

        }

        public static final int NEW_PARTY_REQ_CODE = 30;
        public static final int GET_PARTY_DETAILS_REQ_CODE = 31;
        public static final int SETTINGS_REQ_CODE = 32;
        public static final int IMAGE_PICKER_REQ_CODE = 33;
        public static final int CROP_PROFILE_IMAGE_REQ_CODE = 34;
        public static final int NEW_ASKING_REQ_CODE = 35;
        public static final int GET_ASKING_DETAILS_REQ_CODE = 36;
        public static final int GET_PARTY_COMMENTS_REQ_CODE = 37;
        public static final int GET_GROUP_DETAILS_REQ_CODE = 38;
        public static final int GET_AREA_LIST_REQ_CODE = 39;
        public static final int GET_USER_TAG_LIST_REQ_CODE = 40;
        public static final int GET_PARTY_REQ_CODE = 41;
        public static final int GET_MOMENT_REQ_CODE = 42;
        public static final int NEW_PARTY_REQUEST_REQ_CODE = 43;
        public static final int CREDIT_PURCHASE_REQ_CODE = 44;
        public static final int GET_ASKING_LIST_REQ_CODE = 45;
        public static final int GET_ASKING_BOARD_REQ_CODE = 46;
    }

    public static final class Link {

        public static final String GOOGLE_PLAY_APP = "market://details?id=";
        public static final String GOOGLE_PLAY_URL = "http://play.google.com/store/apps/details?id=";
        public static final String HOME = "http://www.aumums.com";
        public static final String VERSION_DETAILS = "http://www.aumums.com/version";
        public static final String AGREEMENT = "http://www.aumums.com/agreement";
        public static final String CREDIT = "http://www.aumums.com/credit";
    }
}


