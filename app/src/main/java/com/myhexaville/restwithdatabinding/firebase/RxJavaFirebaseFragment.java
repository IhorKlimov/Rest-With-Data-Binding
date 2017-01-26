package com.myhexaville.restwithdatabinding.firebase;


import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myhexaville.restwithdatabinding.R;
import com.myhexaville.restwithdatabinding.databinding.FragmentRxJavaFirebaseBinding;

import java.io.ByteArrayOutputStream;

import durdinapps.rxfirebase2.RxFirebaseAuth;
import durdinapps.rxfirebase2.RxFirebaseStorage;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;

import static io.reactivex.BackpressureStrategy.DROP;


public class RxJavaFirebaseFragment extends Fragment {
    private static final String LOG_TAG = "RxJavaFirebaseFragment";
    public static final String PASSWORD = "MyPasswordIsStrong";
    public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxy".toCharArray();
    public static final String AVATAR = "https://goo.gl/ZpbDSH";

    private FragmentRxJavaFirebaseBinding mBinding;
    private String mEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;
    private StorageReference mStorage;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private byte[] mAvatar;
    private Registration mRegistration;

    public RxJavaFirebaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_rx_java_firebase, container, false);

        mBinding.setFragment(this);

        mRegistration = new Registration();

        generateEmail();

        setupFirebase();

        downloadAvatar();

        return mBinding.getRoot();
    }

    private void downloadAvatar() {
        Glide.with(getContext())
                .load(AVATAR)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            Bitmap resource,
                            GlideAnimation<? super Bitmap> glideAnimation) {

                        mAvatar = getBytes(resource);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        mAuth.removeAuthStateListener(mAuthListener);
        super.onStop();
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                // User signed in
                mBinding.btnSignUp.setEnabled(false);
                mBinding.btnDeleteAccount.setEnabled(true);
            } else {
                // User signed out
                mBinding.btnSignUp.setEnabled(true);
                mBinding.btnDeleteAccount.setEnabled(false);
            }
        };
    }

    public void signUp(View v) {
//        signUp();
        RxJavaSignUp();
    }

    public void deleteAccount(View v) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnFailureListener(e -> Log.e(LOG_TAG, "deleteAccount: ", e))
                    .addOnCompleteListener(task -> Log.d(LOG_TAG, "deleteAccount: " + task.isSuccessful()));
        }
    }

    private void signUp() {
        mAuth.createUserWithEmailAndPassword(mEmail, PASSWORD)
                .addOnFailureListener(e -> Log.e(LOG_TAG, "signUp: ", e))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "signUp: created new user");
                        String uid = task.getResult().getUser().getUid();

                        Glide.with(getContext())
                                .load(AVATAR)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(
                                            Bitmap resource,
                                            GlideAnimation<? super Bitmap> glideAnimation) {

                                        uploadAvatarAndSaveUser(resource);
                                    }

                                    private void uploadAvatarAndSaveUser(Bitmap b) {
                                        mStorage.child(uid)
                                                .putBytes(getBytes(b))
                                                .addOnFailureListener(e -> Log.e(LOG_TAG, "uploadAvatarAndSaveUser: ", e))
                                                .addOnCompleteListener(upload -> {
                                                    if (upload.isSuccessful()) {
                                                        Log.d(LOG_TAG, "uploadAvatarAndSaveUser: successfuly uploaded picture");
                                                        Uri pictureUrl = upload.getResult().getDownloadUrl();

                                                        createUser(pictureUrl);
                                                    }
                                                });
                                    }

                                    private void createUser(Uri pictureUrl) {
                                        mDataBase.child(uid)
                                                .setValue(new User("Frank", pictureUrl.toString()))
                                                .addOnFailureListener(e -> Log.e(LOG_TAG, "createUser: ", e))
                                                .addOnCompleteListener(registraion -> {
                                                    if (registraion.isSuccessful()) {
                                                        Log.d(LOG_TAG, "createUser: successfuly saved user");
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void RxJavaSignUp() {
        Flowable<AuthResult> user = createUser();

        uploadPicture(user);

        saveUser();
    }

    private Flowable<AuthResult> createUser() {
        Flowable<AuthResult> user = RxFirebaseAuth
                .createUserWithEmailAndPassword(mAuth, mEmail, PASSWORD)
                .filter(authResult -> authResult.getUser() != null);

        mRegistration.setUser(user);

        return user;
    }

    private Flowable<Pair<UploadTask.TaskSnapshot, String>> uploadPicture(Flowable<AuthResult> user) {
        Flowable<Pair<UploadTask.TaskSnapshot, String>> uploadPicture =
                mRegistration.getUser()
                        .flatMap(authResult -> {
                            String uid = authResult.getUser().getUid();

                            Flowable<UploadTask.TaskSnapshot> upload = RxFirebaseStorage
                                    .putBytes(mStorage.child(uid), mAvatar);

                            return Flowable.zip(
                                    upload,
                                    Flowable.just(uid),
                                    Pair::new);
                        });

        mRegistration.setUpload(uploadPicture);
        
        return uploadPicture;
    }

    private void saveUser() {
        mRegistration.getUploadPicture()
                .flatMap(pair -> saveUserToDatabase(
                        pair.second,
                        new User("Jeff", toString(pair.first.getDownloadUrl()))))
                .subscribe(new DisposableSubscriber<Ignore>() {
                    @Override
                    public void onNext(Ignore aVoid) {
                        Log.d(LOG_TAG, "onNext: ");
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(LOG_TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @NonNull
    private Flowable<Ignore> saveUserToDatabase(String uid, User user) {
        return Flowable
                .create(e -> {
                    Task<Void> task = mDataBase.child(uid).setValue(user);
                    task.addOnSuccessListener((value) -> e.onNext(Ignore.GET));
                    task.addOnFailureListener(e::onError);
                    task.addOnCompleteListener(task1 -> e.onComplete());
                }, DROP);
    }

    private void generateEmail() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            builder.append(ALPHABET[(int) (Math.random() * ALPHABET.length)]);
        }

        builder.append("@gmail.com");
        mEmail = builder.toString();
    }

    public static byte[] getBytes(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private String toString(Uri uri) {
        return uri == null ? "" : uri.toString();
    }

}
