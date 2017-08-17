package com.ml.yx.web;

import com.ml.yx.comm.Constants;

/**
 * @Author:Lijj
 * @Todo:TODO
 */
public class WebService {

    private static final String TEST_URL = "http://qa.u-thin.com/";// 开发环境
    private static final String PRODUCT_URL = "http://api.u-thin.com/";// 开发环境
    public static final String BASE_URL = Constants.IS_TEST ? TEST_URL : PRODUCT_URL;
    public static final String ACCOUNT_REGISTER_SENDSMS_URL = BASE_URL + "account/register/sendsms";
    public static final String ACCOUNT_REGISTER_USESMS_URL = BASE_URL + "account/register/usesms";
    public static final String ACCOUNT_REGISTER_WELCOME_URL = BASE_URL + "account/register/welcome";
    public static final String ACCOUNT_LOGIN_SENDSMS_URL = BASE_URL + "account/login/sendsms";
    public static final String ACCOUNT_LOGIN_USESMS_URL = BASE_URL + "account/login/usesms";
    public static final String ACCOUNT_REGISTER_COACH_LIST_URL = BASE_URL + "account/register/instructors";
    public static final String ACCOUNT_PROFILE_DETAIL_URL = BASE_URL + "account/profile/detail";
    public static final String ACCOUNT_PROFILE_EDIT_URL = BASE_URL + "account/profile/edit";
    public static final String ACCOUNT_LOGIN_USERWX_URL = BASE_URL + "account/login/usewx";
    public static final String ACCOUNT_QA_CHECK_NEW_URL = BASE_URL + "account/qa/new";//检查新消息
    public static final String ACCOUNT_QA_LIST_URL = BASE_URL + "account/qa/list";//获取消息列表
    public static final String ACCOUNT_QA_QUESTION_URL = BASE_URL + "account/qa/question";//用户提问
    public static final String ACCOUNT_QA_ANSWER_URL = BASE_URL + "account/qa/answer";//用户回答

    public static final String ACCOUNT_FEEDBACK_QUESTION_URL = BASE_URL + "account/feedback/question";

    //关卡相关
    public static final String ACCOUNT_LEVEL_LIST_URL = BASE_URL + "account/level/list";// 获取关卡列表
    public static final String ACCOUNT_LEVEL_DONE_URL = BASE_URL + "account/level/done";// 完成某一天的训练
    public static final String ACCOUNT_LEVEL_DETAIL_URL = BASE_URL + "account/level/detail";// 查看某个关卡详情
    public static final String ACCOUNT_LEVEL_CURRENTSTAT_URL = BASE_URL + "account/level/currentstat";// 查看当前阶段的统计
    public static final String ACCOUNT_LEVEL_RESET_URL = BASE_URL + "account/level/reset";// 重新设置训练阶段



}
