package com.myhexaville.restwithdatabinding.movies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myhexaville.restwithdatabinding.R;
import com.myhexaville.restwithdatabinding.databinding.FragmentMoviesListBinding;
import com.myhexaville.restwithdatabinding.retrofit.TmdbApi;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.myhexaville.restwithdatabinding.Constants.API_KEY;

public class MoviesListFragment extends Fragment {
    private static final String LOG_TAG = "MoviesListFragment";

    private FragmentMoviesListBinding binding;
    private Adapter adapter;
    private String sortType;

    public MoviesListFragment() {
    }

    public static MoviesListFragment newInstance(String sortType) {
        Bundle args = new Bundle();
        args.putString("sortType", sortType);
        MoviesListFragment fragment = new MoviesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortType = getArguments().getString("sortType");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(LayoutInflater.from(getContext()), R.layout.fragment_movies_list, container, false);


        setupMovieList();

        fetchMovies();

        return binding.getRoot();
    }

    private void setupMovieList() {
        adapter = new Adapter(getActivity());
        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private void fetchMovies() {
        String sort = getSortType();

        TmdbApi.INSTANCE.getInstance()
                .findMovies(sort, "1", API_KEY)
                .flatMap(Observable::fromIterable)
//                .flatMap(movie -> {
//                    Log.d(LOG_TAG, "fetchMovies: "+ movie.getId());
//                    Observable<Movie> details = TmdbApi.INSTANCE.getInstance().getDetails(movie.getId(), API_KEY);
//                    return Observable.zip(details,
//                            Observable.just(movie),
//                            Pair::new);
//                })
//                .map(listMoviePair -> {
//                    Log.d(LOG_TAG, "fetchMovies: ");
//                    listMoviePair.second.setProductionCompanies(listMoviePair.first.getProductionCompanies());
//                    return listMoviePair.second;
//                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> value) {
                        Log.d(LOG_TAG, "onSuccess: ");
//                        for (Movie movie : value) {
//                            for (String c : movie.getProductionCompanies()) {
//                                Log.d(LOG_TAG, "onSuccess: " + c + " title: " + movie.getTitle());
//                            }
//                        }
                        adapter.setMovies(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "onError: ", e);
                    }
                });
    }

    @NonNull
    private String getSortType() {
        switch (sortType) {
            case "latest":
                return "release_date.desc&primary_release_date.lte=2017-05-22";
            case "top_rated":
                return "vote_average.desc&vote_count.gte=50";
            default:
                return "popularity.desc";
        }
    }

}
