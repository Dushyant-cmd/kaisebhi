package com.kaisebhi.kaisebhi.HomeNavigation.home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.kaisebhi.kaisebhi.ActivityForFrag;
import com.kaisebhi.kaisebhi.HomeNavigation.Profile.ProfileUpdate;
import com.kaisebhi.kaisebhi.HomeNavigation.Reward.RewardFragment;
import com.kaisebhi.kaisebhi.MainActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.DefaultResponse;
import com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kaisebhi.kaisebhi.Utility.Network.RetrofitClient.BASE_URL;

public class MenuFragment extends Fragment implements View.OnClickListener {

    private FrameLayout MainFrame;
    private FirebaseAuth mAuth;
    TextView name,user;
    CircleImageView prof;
    SharedPrefManager sharedPrefManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_navigation, container, false);
        mAuth = ((ApplicationCustom) getActivity().getApplication()).mAuth;

        sharedPrefManager = new SharedPrefManager(getActivity());
        TextView m1,m01,m2,m3,m4,m5,m6,m7,m8,m9,m10;
        ImageView i1;

        MainFrame = getActivity().findViewById(R.id.nav_host_fragment);

        name = root.findViewById(R.id.user_navName);
        user = root.findViewById(R.id.user_navId);
        prof = root.findViewById(R.id.showPro);

        name.setText(sharedPrefManager.getsUser().getName());
        user.setText(sharedPrefManager.getsUser().getMobile());

        fetchProfile();


        m1 = root.findViewById(R.id.quesNav);
        m01 = root.findViewById(R.id.answersPaid);
        m9 = root.findViewById(R.id.answersHide);
        m2 = root.findViewById(R.id.mineNav);
        m3 = root.findViewById(R.id.rewardNav);
        m4 = root.findViewById(R.id.logNav);
        m5 = root.findViewById(R.id.termNav);
        m6 = root.findViewById(R.id.favNav);
        m7 = root.findViewById(R.id.viewProfile);
        m8 = root.findViewById(R.id.answersNav);
        m10 = root.findViewById(R.id.SugNav);
        i1 = root.findViewById(R.id.showPro);



        m1.setOnClickListener(this);
        m9.setOnClickListener(this);
        m2.setOnClickListener(this);
        m3.setOnClickListener(this);
        m4.setOnClickListener(this);
        m5.setOnClickListener(this);
        m6.setOnClickListener(this);
        i1.setOnClickListener(this);
        m7.setOnClickListener(this);
        m8.setOnClickListener(this);
        m10.setOnClickListener(this);
        m01.setOnClickListener(this);


        return root;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.quesNav:
                changeFragment(new SearchQuestionFragment());
                break;
            case R.id.favNav:
                changeFragment(new FavoriteFragment());
                break;
            case R.id.viewProfile:
                startActivity(new Intent(getActivity(), ProfileUpdate.class));
                break;
            case R.id.showPro:
                startActivity(new Intent(getActivity(), ProfileUpdate.class));
                break;
            case R.id.mineNav:
                changeFragment(new MineQuestFragment());
                break;
            case R.id.rewardNav:
                changeFragment(new RewardFragment());
                break;
            case R.id.answersHide:
                Intent seac = new Intent(getActivity(), ActivityForFrag.class);
                seac.putExtra("Frag","showAns");
                seac.putExtra("tabType","hide");
                startActivity(seac);
                break;
            case R.id.answersPaid:
                Intent show = new Intent(getActivity(), ActivityForFrag.class);
                show.putExtra("Frag","showAns");
                show.putExtra("tabType","show");
                startActivity(show);
                break;
            case R.id.answersNav:
                changeFragment(new MineAnsFragment());
                break;
            case R.id.termNav:
                Uri uri = Uri.parse("https://kaisebhi.com/terms-conditions/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.SugNav:
                Uri uris = Uri.parse("https://kaisebhi.com/suggestions/"); // missing 'http://' will cause crashed
                Intent intents = new Intent(Intent.ACTION_VIEW, uris);
                startActivity(intents);
                break;
                case R.id.logNav:
                alertOpen();
                break;
            default:
                break;
        }
    }

    public void alertOpen()
    {
        @SuppressLint("ResourceType")
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setIcon(getActivity().getDrawable(R.drawable.logo))
                .setTitle("Are you sure to logout!")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        SharedPrefManager.getInstance(getActivity()).logoutUser();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                    }
                })
                .show();
    }


    private void changeFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(MainFrame.getId(), fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commitAllowingStateLoss();

    }


    public void fetchProfile() {
        Call<DefaultResponse> call = RetrofitClient.getInstance().getApi().getPro(sharedPrefManager.getsUser().getUid());
        call.enqueue(new Callback<DefaultResponse>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                DefaultResponse dr = response.body();
                    String data = dr.getMessage();
                    Glide.with(getActivity()).load(BASE_URL + "user/" + data).dontAnimate().centerCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.profile).into(prof);
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getActivity(),t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

            }

        });
    }




}
