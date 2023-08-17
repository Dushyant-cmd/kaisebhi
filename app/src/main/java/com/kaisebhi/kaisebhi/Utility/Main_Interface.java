package com.kaisebhi.kaisebhi.Utility;

import com.kaisebhi.kaisebhi.HomeNavigation.Notifications.NotifyViewModel;
import com.kaisebhi.kaisebhi.HomeNavigation.Reward.ModelWalletHistory;
import com.kaisebhi.kaisebhi.HomeNavigation.home.AnswersModel;
import com.kaisebhi.kaisebhi.HomeNavigation.home.HideAnswersModel;
import com.kaisebhi.kaisebhi.HomeNavigation.home.QuestionsModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Main_Interface {

    @POST("showPro.php")
    Call<DefaultResponse> getProfile(@Query("proDetails") String key);

    @POST("showPro.php")
    Call<DefaultResponse> getProfilePic(@Query("proDetailsPic") String key);

    @POST("showPro.php")
    Call<DefaultResponse> getFullProfile(@Query("fullproDetails") String key);

    @POST("showPro.php")
    Call<DefaultResponse> getPro(@Query("profileOnly") String key);

    @POST("promo.php")
    Call<DefaultResponse> deleteAddress(
            @Query("deleteAddress") String id
    );

    @POST("notifications.php")
    Call<List<NotifyViewModel>> getNotifications(@Query("allNotify") String key);

    @POST("userCrud.php")
    Call<DefaultResponse> moneyRequest(
            @Query("moneyRequest") String key,
            @Query("upi") String type,
            @Query("upitype") String ty

    );

    @POST("wallet.php")
    Call<DefaultResponse> getBal(@Query("custId") String key);

    @POST("wallet.php")
    Call<List<ModelWalletHistory>> getWalletHistory(@Query("walletId") String key);


    @POST("userCrud.php")
    Call<DefaultResponse> createUserEmail(
            @Query("name") String name,
            @Query("emailUser") String email,
            @Query("tokenUser") String token
    );


    @POST("userCrud.php")
    Call<DefaultResponse> loginEmail(
            @Query("loginEmail") String name,
            @Query("loginPass") String email,
            @Query("tokenUser") String token
    );


    @POST("profile.php")
    Call<DefaultResponse> updatePro(
            @Query("name") String mobile,
            @Query("mobile") String pass,
            @Query("email") String gen,
            @Query("address") String intrest,
            @Query("updateDetails") String userid
    );


    @POST("profile.php")
    Call<DefaultResponse> updatePassword(
            @Query("password") String gen,
            @Query("updatePass") String userid
    );


    @Multipart
    @POST("profile.php")
    Call<DefaultResponse>
    uploadProfile(  @Part MultipartBody.Part file,
                    @Part("file") RequestBody title,
                    @Part("name") String mobile,
                    @Part("mobile") String pass,
                    @Part("email") String gen,
                    @Part("address") String intrest,
                    @Part("profileUpdate") String userid
    );


    @Multipart
    @POST("questionsCRUD.php")
    Call<DefaultResponse>
    addImgQuestion(  @Part MultipartBody.Part file,
                    @Part("file") RequestBody title,
                    @Part("qTitle") String mobile,
                    @Part("qDesc") String pass,
                    @Part("cat") String intrest,
                    @Part("addImgQuestion") String userid
    );


    @POST("questionsCRUD.php")
    Call<DefaultResponse>
    addQuestion(
            @Query("qTitle") String title,
            @Query("qDesc") String desc,
            @Query("cat") String intrest,
            @Query("addQuestion") String userid
    );

    @POST("questions.php")
    Call<List<QuestionsModel>> getallQuestionsHome(@Query("all_questions") String key, @Query("searchQues") String search);

    @POST("questions.php")
    Call<List<QuestionsModel>> getallQuestions(@Query("all_questions") String key,@Query("searchQues") String search);

    @POST("questions.php")
    Call<List<QuestionsModel>> getSearchQ(@Query("search_question") String key,@Query("searchQues") String search);

    @POST("answers.php")
    Call<List<AnswersModel>> getMineAnswers(@Query("mineanswers") String key);

    @POST("answers.php")
    Call<List<HideAnswersModel>> getHideAns(@Query("hideAns") String key,@Query("ansType") String reg);

    @POST("questions.php")
    Call<List<QuestionsModel>> getPaginationQues(@Query("all_questions") String key,@Query("searchQues") String search);

    @POST("answers.php")
    Call<List<AnswersModel>> getAnswers(@Query("answers") String key,@Query("user") String user);

    @POST("questions.php")
    Call<DefaultResponse>
    likeQues(
            @Query("qid") String title,
            @Query("changeLike") String desc,
            @Query("userid") String userid
    );

    @POST("answers.php")
    Call<DefaultResponse>
    reportAbuse(
            @Query("report_answer_id") String title,
            @Query("userid") String userid
    );

    @POST("questions.php")
    Call<DefaultResponse>
    deleteQues(
            @Query("deleteQues") String title
    );

    @POST("questions.php")
    Call<DefaultResponse>
    favQues(
            @Query("qid") String title,
            @Query("changeFav") String desc,
            @Query("userid") String userid
    );

    @POST("answers.php")
    Call<DefaultResponse> addAns(
            @Query("addNew") String key,
            @Query("qid") String qid,
            @Query("uid") String uid
    );

    @POST("answers.php")
    Call<DefaultResponse> hideAns(
            @Query("qid") String key,
            @Query("hidepay") String qid,
            @Query("user") String uid
    );

    @POST("answers.php")
    Call<DefaultResponse> showAns(
            @Query("qid") String key,
            @Query("showpay") String qid,
            @Query("user") String uid
    );

    /**Below method is to get the translated string. */
    @GET("get")
    Call<DefaultResponse> getTranslated(
            @Query("q") String query,
            @Query("langpair") String langPair
    );
}
