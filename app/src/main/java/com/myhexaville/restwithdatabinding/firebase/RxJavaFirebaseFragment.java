package com.myhexaville.restwithdatabinding.firebase;


import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
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
import kotlin.Unit;

import static io.reactivex.BackpressureStrategy.DROP;


public class RxJavaFirebaseFragment extends Fragment {
    private static final String LOG_TAG = "RxJavaFirebaseFragment";
    public static final String PASSWORD = "MyPasswordIsStrong";
    public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxy".toCharArray();
    public static final String AVATAR = "https://goo.gl/ZpbDSH";
    public static final String USER_EMAIL = "User Email";

    private FragmentRxJavaFirebaseBinding binding;
    private String email;
    private FirebaseAuth auth;
    private DatabaseReference dataBase;
    private StorageReference storageReference;
    private FirebaseAuth.AuthStateListener authListener;
    private byte[] avatar;
    private Registration registration;

    public RxJavaFirebaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_rx_java_firebase, container, false);

        binding.setFragment(this);

        registration = new Registration();

        generateEmail();

        setupFirebase();

        downloadAvatar();

        return binding.getRoot();
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

                        avatar = getBytes(resource);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        auth.removeAuthStateListener(authListener);
        super.onStop();
    }

    private void setupFirebase() {
        auth = FirebaseAuth.getInstance();
        dataBase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        authListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                // User signed in
                binding.btnSignUp.setEnabled(false);
                binding.btnDeleteAccount.setEnabled(true);
            } else {
                // User signed out
                binding.btnSignUp.setEnabled(true);
                binding.btnDeleteAccount.setEnabled(false);
            }
        };
    }

    public void signUp(View v) {
//        signUp();
        RxJavaSignUp();
    }

    public void deleteAccount(View v) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(getUserEmail(), PASSWORD);

        if (user != null) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(task ->
                            user.delete()
                                    .addOnCompleteListener(task12 -> {
                                        if (task12.isSuccessful()) {
                                            Log.d(LOG_TAG, "User account deleted.");
                                        }
                                    }));
        }
    }

    private void signUp() {
        auth.createUserWithEmailAndPassword(email, PASSWORD)
                .addOnFailureListener(e -> Log.e(LOG_TAG, "signUp: ", e))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "signUp: created new user");
                        String uid = task.getResult().getUser().getUid();

                        saveUserEmailToPreferences(email);

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
                                        storageReference.child(uid)
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
                                        dataBase.child(uid)
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
        createUser();

        uploadPicture();

        saveUser();
    }

    private void createUser() {
        Flowable<AuthResult> user = RxFirebaseAuth
                .createUserWithEmailAndPassword(auth, email, PASSWORD)
                .filter(authResult -> authResult.getUser() != null);

        registration.setUser(user);
    }

    private void uploadPicture() {
        Flowable<Pair<UploadTask.TaskSnapshot, String>> uploadPicture =
                registration.getUser()
                        .flatMap(authResult -> {
                            String uid = authResult.getUser().getUid();

                            saveUserEmailToPreferences(email);

                            Flowable<UploadTask.TaskSnapshot> upload = RxFirebaseStorage
                                    .putBytes(storageReference.child(uid), avatar);

                            return Flowable.zip(
                                    upload,
                                    Flowable.just(uid),
                                    Pair::new);
                        });

        registration.setUploadPicture(uploadPicture);
    }

    private void saveUser() {
        registration.getUploadPicture()
                .flatMap(pair -> saveUserToDatabase(
                        pair.second,
                        new User("Jeff", toString(pair.first.getDownloadUrl()))))
                .subscribe(new DisposableSubscriber<Unit>() {
                    @Override
                    public void onNext(Unit aVoid) {
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
    private Flowable<Unit> saveUserToDatabase(String uid, User user) {
        return Flowable
                .create(e -> {
                    Task<Void> task = dataBase.child(uid).setValue(user);
                    task.addOnSuccessListener((value) -> e.onNext(Unit.INSTANCE));
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
        email = builder.toString();
    }

    public static byte[] getBytes(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private String toString(Uri uri) {
        return uri == null ? "" : uri.toString();
    }

    private void saveUserEmailToPreferences(String email) {
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit()
                .putString(USER_EMAIL, email)
                .apply();
    }

    private String getUserEmail() {
        return PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(USER_EMAIL, "");
    }

}
