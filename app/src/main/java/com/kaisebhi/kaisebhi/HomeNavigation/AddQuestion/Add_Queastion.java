package com.kaisebhi.kaisebhi.HomeNavigation.AddQuestion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;
import com.kaisebhi.kaisebhi.Utility.ApplicationCustom;
import com.kaisebhi.kaisebhi.Utility.SharedPrefManager;
import com.kaisebhi.kaisebhi.Utility.Utility;
import com.kaisebhi.kaisebhi.room.PortalsEntity;
import com.kaisebhi.kaisebhi.room.RoomDb;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Add_Queastion extends AppCompatActivity {

    private Button addDelivery;
    private EditText quesTitle, quesDesc, otherPortalEditText;
    String audioDownloadUrl = "";
    private Uri postUri = null;
    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore mFirestore;
    private Button uploadBtn;
    private FirebaseStorage storage;
    String pCheck = null, TAG = "Add_Question.java", portal = "";
    ProgressDialog progressDialog;
    ImageView selectQues;
    String Qid = "", selectedPortal = "";
    String fileName; //Obtained by intent
    Uri audiouri;
    ParcelFileDescriptor file;

    private Spinner spinner;
    private RoomDb roomDb;
    private TextInputLayout portalIL;
    private Button recordBtn;
    private File mFile;
    private boolean isPermissionGranted = false;

    private int i = 0, sec = 0;
    private CountDownTimer cDT;
    private MediaRecorder mRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_ques);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPrefManager = new SharedPrefManager(Add_Queastion.this);
        mFirestore = ((ApplicationCustom) getApplication()).mFirestore;
        storage = ((ApplicationCustom) getApplication()).storage;
        progressDialog = new ProgressDialog(Add_Queastion.this);
        progressDialog.setCancelable(false);
        roomDb = ((ApplicationCustom) getApplication()).roomDb;
        spinner = findViewById(R.id.portalSpinner);
        otherPortalEditText = findViewById(R.id.otherPortal);
        portalIL = findViewById(R.id.portalIL);
        recordBtn = findViewById(R.id.recordAudioBtn);

        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Question details processing....");

        selectQues = findViewById(R.id.quesImage);
        if (roomDb.getPortalDao().getPortals() == null) {
            mFirestore.collection("appData").document("portals")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    DocumentSnapshot d = task.getResult();
                                    List<String> list = new ArrayList<>();
                                    int i = 1;
                                    while (d.getString(i + "") != null) {
                                        list.add(d.getString(i + ""));
                                        i++;
                                    }

                                    PortalsEntity portalsEntity1 = new PortalsEntity(
                                            list.toArray(new String[0]),
                                            System.currentTimeMillis()
                                    );
                                    roomDb.getPortalDao().insertPortals(portalsEntity1);
                                    spinner.setAdapter(new ArrayAdapter<>(Add_Queastion.this,
                                            android.R.layout.simple_dropdown_item_1line,
                                            roomDb.getPortalDao().getPortals().portals));
                                    Log.d(TAG, "onComplete: protals " + task.getResult());
                                } catch (Exception e) {
                                    Log.d(TAG, "onCatch: " + e);
                                }
                            }
                        }
                    });
        } else {
            Log.d(TAG, "onCreate: hasData");
            spinner.setAdapter(new ArrayAdapter<String>(Add_Queastion.this, android.R.layout.simple_dropdown_item_1line,
                    roomDb.getPortalDao().getPortals().portals));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPortal = spinner.getSelectedItem().toString();
                Log.d(TAG, "onItemSelected: " + selectedPortal);
                if (selectedPortal.equalsIgnoreCase("other")) {
                    portalIL.setVisibility(View.VISIBLE);
                    selectedPortal = "";
                } else {
                    portalIL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        quesTitle = findViewById(R.id.q_title);
        quesDesc = findViewById(R.id.a_desc);
        uploadBtn = findViewById(R.id.uploadQues);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Qid = extras.getString("key");
            quesTitle.setText(extras.getString("title"));
            quesDesc.setText(extras.getString("desc"));
            portal = extras.getString("portal");

            for (int i = 0; i < roomDb.getPortalDao().getPortals().portals.length; i++) {
                if (roomDb.getPortalDao().getPortals().portals[i].equals(portal)) {
                    spinner.setSelection(i);
                }
            }

            if (extras.getString("qimg").length() > 0) {
                Glide.with(getApplicationContext()).load(extras.getString("qimg")).fitCenter().into(selectQues);
            }
            Button uploadBtn = findViewById(R.id.uploadQues);
            uploadBtn.setText("Update Question");
            recordBtn.setText("Recorded");
            recordBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.pastelOrange, getTheme()));
            recordBtn.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_check_circle_24, getTheme()), null, null, null);
            recordBtn.setEnabled(false);
        }

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick bottom sheet");
                if (isPermissionGranted) {
                    bottomSheet(getLayoutInflater(), findViewById(R.id.root), new Bundle());
                } else {
                    checkPerm();
                }
            }
        });
        selectQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGranted)
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .setMinCropResultSize(512, 512)
                            .start(Add_Queastion.this);
                else
                    checkPerm();
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBtn.setEnabled(false);
                if (pCheck == null) {
                    uploadQues();
                } else {
                    uploadQuesImage();
                }
            }
        });
    }

    private void checkPerm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(Add_Queastion.this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(Add_Queastion.this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(Add_Queastion.this, Manifest.permission.READ_MEDIA_AUDIO)
                    == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permArr = new String[]{
                        Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.READ_MEDIA_IMAGES
                        , Manifest.permission.READ_MEDIA_AUDIO
                        , Manifest.permission.CAMERA
                };

                ActivityCompat.requestPermissions(Add_Queastion.this, permArr, 101);
            } else {
                bottomSheet(getLayoutInflater(), findViewById(R.id.root), new Bundle());
                isPermissionGranted = true;
            }
        } else {
            if (ContextCompat.checkSelfPermission(Add_Queastion.this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(Add_Queastion.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(Add_Queastion.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permArr = new String[]{Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA};

                ActivityCompat.requestPermissions(Add_Queastion.this, permArr, 101);
            } else {
                bottomSheet(getLayoutInflater(), findViewById(R.id.root), new Bundle());
                isPermissionGranted = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    isPermissionGranted = false;
                    break;
                }
            }

            if (!isPermissionGranted) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage("Please allow all permissions to continue")
                        .setTitle("Permission denied")
                        .setCancelable(false)
                        .setIcon(R.drawable.appicon)
                        .setPositiveButton("Allow Permissions", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(intent);
                                dialogInterface.dismiss();
                            }
                        });
                builder.show();
            }
        }
    }

    public void bottomSheet(LayoutInflater inflater, ViewGroup container, Bundle saveInsState) {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        sheet.setCancelable(true);
        View view = inflater.inflate(R.layout.record_bottom_sheet_dialog, container, false);
        sheet.setContentView(view);
        TextView secs = view.findViewById(R.id.secsTV);
        ProgressBar progressBar = view.findViewById(R.id.secCirPB);
        Button startBtn = view.findViewById(R.id.startBtn);
        Button stopBtn = view.findViewById(R.id.stopBtn);
        sheet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cDT = new CountDownTimer(100000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (i == 60) {
                    progressBar.setProgress(i);
                    i++;
                    sec = 0;
                    secs.setText("01 : 0" + sec);
                    return;
                }

                if (i >= 60) {
                    if (sec < 10)
                        secs.setText("01 : 0" + sec);
                    else
                        secs.setText("01 : " + sec);
                } else {
                    if (sec < 10)
                        secs.setText("00 : 0" + sec);
                    else
                        secs.setText("00 : " + sec);
                }
                progressBar.setProgress(i);
                i++;
                sec++;
            }

            @Override
            public void onFinish() {
                try {
                    Toast.makeText(getApplicationContext(), "Recorded", Toast.LENGTH_SHORT).show();
                    cDT.cancel();
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    status();
                    sheet.dismiss();
                } catch (Exception e) {
                    Log.d("BottomSheet.java", "onFinish: " + e);
                }
            }
        };

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BottomSheet.java", "onClick: clicked");
                try {
                    cDT.start();
                    startBtn.setVisibility(View.GONE);
                    stopBtn.setVisibility(View.VISIBLE);

                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    String path = String.valueOf(getExternalFilesDir(Environment.DIRECTORY_DCIM));
                    File dir = new File(path);
                    if (!dir.exists())
                        dir.mkdirs();
                    String myFile = path + "/filename" + System.currentTimeMillis() + ".mp4";
                    mFile = new File(myFile);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mRecorder.setOutputFile(mFile);
                    }
                    mRecorder.prepare();
                    mRecorder.start();
                } catch (Exception e) {
                    Log.d("BottomSheet.java", "onCatch: " + e);
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(getApplicationContext(), "Recorded", Toast.LENGTH_SHORT).show();
                    cDT.cancel();
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    sheet.dismiss();
                    status();
                } catch (Exception e) {
                    Log.d("BottomSheet.java", "onClick: " + e);
                }
            }
        });
        sheet.show();
    }

    private void startRecording() throws IOException {
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Audio.Media.TITLE, fileName);
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/");

        audiouri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        file = getContentResolver().openFileDescriptor(audiouri, "w");
        Log.d(TAG, "startRecording: " + audiouri);
        if (file != null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setOutputFile(file.getFileDescriptor());
            mRecorder.setAudioChannels(1);
            mRecorder.prepare();
            mRecorder.start();
        }
    }

    public void status() {
        recordBtn.setText("Recorded");
        recordBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.pastelOrange, getTheme()));
        recordBtn.setCompoundDrawablesWithIntrinsicBounds(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_check_circle_24, getTheme()), null, null, null);
        recordBtn.setEnabled(false);
    }

    private void uploadQues() {
        String title = quesTitle.getText().toString();
        String desc = quesDesc.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add Question !", Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        }

        if (desc.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        }

        selectedPortal = spinner.getSelectedItem().toString();
        if (selectedPortal.equalsIgnoreCase("Please Select a Portal")) {
            Toast.makeText(getApplicationContext(), "Select Portal !", Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        } else if (selectedPortal.equalsIgnoreCase("other") && otherPortalEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add Portal !", Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        } else if (selectedPortal.equalsIgnoreCase("other")) {
            selectedPortal = otherPortalEditText.getText().toString();
        }

        progressDialog.show();
        if (uploadBtn.getText().toString().equalsIgnoreCase("Update Question")) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("title", title);
            map.put("desc", desc);
            if (!spinner.getSelectedItem().toString().equalsIgnoreCase(portal))
                map.put("portal", selectedPortal);
            mFirestore.collection("questions").document(Qid).update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Log.d(TAG, "onSuccess: updated success");
                            Utility.toast(Add_Queastion.this, "Question Waiting for Approve! ");
                            Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
                            cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(cart);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadBtn.setEnabled(true);
                            Log.d(TAG, "onFailure: " + e);
                        }
                    });
        } else {
            String UUID = java.util.UUID.randomUUID().toString();
            if (mFile != null) {
                StorageReference rootRef = storage.getReference();
                StorageReference audioRef = rootRef.child("audios/" + UUID);
                audioRef.putFile(Uri.fromFile(mFile)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            audioRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: success audio");
                                        audioDownloadUrl = task.getResult().toString();
                                        mFirestore.collection("ids").document("questionId").get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            long updateId = task.getResult().getLong("id") + 1;
                                                            HashMap<String, Object> questionMap = new HashMap<>();
                                                            questionMap.put("title", title);
                                                            questionMap.put("desc", desc);
                                                            questionMap.put("likes", "0");
                                                            questionMap.put("qpic", "na");
                                                            questionMap.put("checkFav", false);
                                                            questionMap.put("checkLike", false);
                                                            questionMap.put("tanswers", "false");
                                                            questionMap.put("uname", sharedPrefManager.getsUser().getName());
                                                            questionMap.put("userId", sharedPrefManager.getsUser().getUid());
                                                            questionMap.put("id", updateId + "");
                                                            questionMap.put("qualityCheck", "pending");
                                                            questionMap.put("timestamp", System.currentTimeMillis());
                                                            questionMap.put("likedByUser", "");
                                                            questionMap.put("image", "");
                                                            questionMap.put("userPicUrl", sharedPrefManager.getProfilePic());
                                                            questionMap.put("imageRef", "");
                                                            questionMap.put("portal", selectedPortal);
                                                            questionMap.put("audio", audioDownloadUrl);
                                                            if (!audioDownloadUrl.isEmpty())
                                                                questionMap.put("audioRef", UUID);
                                                            else
                                                                questionMap.put("audioRef", UUID);
                                                            mFirestore.collection("questions").document(updateId + "").set(questionMap)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            HashMap<String, Object> map = new HashMap<>();
                                                                            map.put("id", updateId);
                                                                            mFirestore.collection("ids").document("questionId").update(map)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            Log.d(TAG, "onSuccess: success");
                                                                                            progressDialog.dismiss();
                                                                                            Utility.toast(Add_Queastion.this, "Question Waiting for Approve! ");
                                                                                            Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
                                                                                            cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                            cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            startActivity(cart);
                                                                                            finish();
                                                                                        }
                                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            uploadBtn.setEnabled(true);
                                                                                            Log.d(TAG, "onFailure: " + e);
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(Exception e) {
                                                                            uploadBtn.setEnabled(true);
                                                                            Log.d(TAG, "onFailure: " + e);
                                                                        }
                                                                    });
                                                        } else
                                                            uploadBtn.setEnabled(true);
                                                    }
                                                });
                                    } else
                                        uploadBtn.setEnabled(true);
                                }
                            });
                        } else {
                            uploadBtn.setEnabled(true);
                            Log.d(TAG, "onError: audio " + task.getException());
                        }
                    }
                });
            } else {
                mFirestore.collection("ids").document("questionId").get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    long updateId = task.getResult().getLong("id") + 1;
                                    HashMap<String, Object> questionMap = new HashMap<>();
                                    questionMap.put("title", title);
                                    questionMap.put("desc", desc);
                                    questionMap.put("likes", "0");
                                    questionMap.put("qpic", "na");
                                    questionMap.put("checkFav", false);
                                    questionMap.put("checkLike", false);
                                    questionMap.put("tanswers", "false");
                                    questionMap.put("uname", sharedPrefManager.getsUser().getName());
                                    questionMap.put("userId", sharedPrefManager.getsUser().getUid());
                                    questionMap.put("id", updateId + "");
                                    questionMap.put("qualityCheck", "pending");
                                    questionMap.put("timestamp", System.currentTimeMillis());
                                    questionMap.put("likedByUser", "");
                                    questionMap.put("image", "");
                                    questionMap.put("userPicUrl", sharedPrefManager.getProfilePic());
                                    questionMap.put("imageRef", "");
                                    questionMap.put("portal", selectedPortal);
                                    questionMap.put("audio", audioDownloadUrl);
                                    if (!audioDownloadUrl.isEmpty())
                                        questionMap.put("audioRef", UUID);
                                    else
                                        questionMap.put("audioRef", "");
                                    mFirestore.collection("questions").document(updateId + "").set(questionMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    map.put("id", updateId);
                                                    mFirestore.collection("ids").document("questionId").update(map)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Log.d(TAG, "onSuccess: success");
                                                                    progressDialog.dismiss();
                                                                    Utility.toast(Add_Queastion.this, "Question Waiting for Approve! ");
                                                                    Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
                                                                    cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(cart);
                                                                    finish();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    uploadBtn.setEnabled(true);
                                                                    Log.d(TAG, "onFailure: " + e);
                                                                }
                                                            });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(Exception e) {
                                                    uploadBtn.setEnabled(true);
                                                    Log.d(TAG, "onFailure: " + e);
                                                }
                                            });
                                } else
                                    uploadBtn.setEnabled(true);
                            }
                        });
            }
        }
    }

    private void uploadQuesImage() {

        String title = quesTitle.getText().toString();
        String desc = quesDesc.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add Question !", Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        }

        if (desc.isEmpty()) {
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        }

        selectedPortal = spinner.getSelectedItem().toString();
        if (selectedPortal.equalsIgnoreCase("Please Select a Portal")) {
            Toast.makeText(getApplicationContext(), "Select Portal !", Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        } else if (selectedPortal.equalsIgnoreCase("other") && otherPortalEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add Portal !", Toast.LENGTH_SHORT).show();
            uploadBtn.setEnabled(true);
            return;
        } else if (selectedPortal.equalsIgnoreCase("other")) {
            selectedPortal = otherPortalEditText.getText().toString();
        }

        progressDialog.show();

        try {
            if (uploadBtn.getText().toString().matches("Update Question")) {
                Log.d(TAG, "uploadQuesImage: " + getIntent().getStringExtra("quesImgPath"));
                File file = new File(postUri.getPath());
                StorageReference storageReference = storage.getReference();
                StorageReference fileRef = storageReference.child("images/" + getIntent().getStringExtra("quesImgPath"));
                InputStream stream = new FileInputStream(file);
                UploadTask uploadTask = fileRef.putStream(stream);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Task<Uri> taskUri = fileRef.getDownloadUrl();
                        taskUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task1) {
                                if (task1.isSuccessful()) {
                                    String uri = task1.getResult().toString();
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("title", title);
                                    map.put("desc", desc);
                                    map.put("image", uri);
                                    if (spinner.getSelectedItem().toString().matches(portal))
                                        map.put("portal", selectedPortal);
                                    mFirestore.collection("questions").document(Qid).update(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    try {
                                                        Log.d(TAG, "onSuccess: updated success");
                                                        Utility.toast(Add_Queastion.this, "Question Updated for Approve! ");
                                                        Intent cart = new Intent(Add_Queastion.this, HomeActivity.class);
                                                        cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(cart);
                                                        finish();
                                                    } catch (Exception e) {
                                                        Log.d(TAG, "onSuccess: " + e);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    uploadBtn.setEnabled(true);
                                                    Log.d(TAG, "onFailure: " + e);
                                                    progressDialog.dismiss();
                                                }
                                            });
                                } else
                                    uploadBtn.setEnabled(true);
                            }
                        });
                    }
                });
            } else {
                File file = new File(postUri.getPath());
                StorageReference storageRef = storage.getReference();
                String imagePath = UUID.randomUUID().toString();
                StorageReference imageRef = storageRef.child("images/" + imagePath);
                InputStream inputStream = new FileInputStream(file);
                UploadTask uploadTask = imageRef.putStream(inputStream);

                String UUID = java.util.UUID.randomUUID().toString();
                if (mFile != null) {
                    StorageReference audioRef = storageRef.child("audios/" + UUID);
                    audioRef.putFile(Uri.fromFile(mFile)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                audioRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: success audio");
                                            audioDownloadUrl = task.getResult().toString();
                                        }
                                    }
                                });
                            } else {
                                Log.d(TAG, "onError: audio " + task.getException());
                            }
                        }
                    });
                }
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> imageTsk = imageRef.getDownloadUrl();
                        imageTsk.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> downloadImgTask) {
                                if (downloadImgTask.isSuccessful()) {
                                    mFirestore.collection("ids").document("questionId").get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        long updateId = task.getResult().getLong("id") + 1;
                                                        HashMap<String, Object> questionMap = new HashMap<>();
                                                        questionMap.put("title", title);
                                                        questionMap.put("desc", desc);
                                                        questionMap.put("likes", "0");
                                                        questionMap.put("qpic", "na");
                                                        questionMap.put("checkFav", false);
                                                        questionMap.put("checkLike", false);
                                                        questionMap.put("tanswers", "false");
                                                        questionMap.put("uname", sharedPrefManager.getsUser().getName());
                                                        questionMap.put("userId", sharedPrefManager.getsUser().getUid());
                                                        questionMap.put("id", updateId + "");
                                                        questionMap.put("qualityCheck", "pending");
                                                        questionMap.put("timestamp", System.currentTimeMillis());
                                                        questionMap.put("likedByUser", "");
                                                        questionMap.put("image", downloadImgTask.getResult().toString());
                                                        questionMap.put("userPicUrl", sharedPrefManager.getProfilePic());
                                                        questionMap.put("imageRef", imagePath);
                                                        questionMap.put("portal", selectedPortal);
                                                        questionMap.put("audio", audioDownloadUrl);
                                                        if (!audioDownloadUrl.isEmpty())
                                                            questionMap.put("audioRef", UUID);
                                                        else
                                                            questionMap.put("audioRef", "");
                                                        mFirestore.collection("questions").document(updateId + "").set(questionMap)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        HashMap<String, Object> map = new HashMap<>();
                                                                        map.put("id", updateId);
                                                                        mFirestore.collection("ids").document("questionId").update(map)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Log.d(TAG, "onSuccess: success");
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getApplicationContext(), "Question Waiting for Approve! ", Toast.LENGTH_SHORT).show();
                                                                                        Intent cart = new Intent(getApplicationContext(), HomeActivity.class);
                                                                                        cart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                        cart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                        startActivity(cart);
                                                                                        finish();
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        uploadBtn.setEnabled(true);
                                                                                        Log.d(TAG, "onFailure: " + e);
                                                                                    }
                                                                                });
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(Exception e) {
                                                                        uploadBtn.setEnabled(true);
                                                                        Log.d(TAG, "onFailure: " + e);
                                                                    }
                                                                });
                                                    } else
                                                        uploadBtn.setEnabled(true);
                                                }
                                            });
                                } else
                                    uploadBtn.setEnabled(true);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadBtn.setEnabled(true);
                        Log.d(TAG, "onFailure: " + e);
                        progressDialog.dismiss();
                        Toast.makeText(Add_Queastion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    }
                });
            }
        } catch (Exception e) {
            uploadBtn.setEnabled(true);
            Log.d(TAG, "uploadQuesImage: " + e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postUri = result.getUri();
                selectQues.setImageURI(postUri);
                pCheck = "Profile";

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}