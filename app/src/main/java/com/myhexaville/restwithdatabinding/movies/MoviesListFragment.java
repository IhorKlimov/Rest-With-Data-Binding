package com.myhexaville.restwithdatabinding.movies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
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

public class MoviesListFragment extends Fragment {
    private static final String LOG_TAG = "MoviesListFragment";
    public static final String API_KEY = "daa8e62fb35a4e6821d58725b5abb88f";

    private FragmentMoviesListBinding mBinding;
    private Adapter mAdapter;

    public MoviesListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil
                .inflate(LayoutInflater.from(getContext()), R.layout.fragment_movies_list, container, false);

        setupMovieList();

        fetchMovies();

        return mBinding.getRoot();
    }

    private void setupMovieList() {
        mAdapter = new Adapter(getActivity());
        mBinding.list.setAdapter(mAdapter);
        mBinding.list.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private void fetchMovies() {
        TmdbApi.getInstance()
                .findMovies("popularity.desc", "1", API_KEY)
                .flatMap(Observable::fromIterable)
                .flatMap(movie -> {
                    Observable<List<String>> details = TmdbApi.getInstance().getDetails(movie.getId(), API_KEY);
                    return Observable.zip(details,
                            Observable.just(movie),
                            Pair::new);
                })
                .map(listMoviePair -> {
                    listMoviePair.second.setProductionCompanies(listMoviePair.first);
                    return listMoviePair.second;
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> value) {
                        for (Movie movie : value) {
                            for (String c : movie.getProductionCompanies()) {
                                Log.d(LOG_TAG, "onSuccess: "+ c + " title: "+ movie.getTitle());
                            }
                        }
                        mAdapter.setmMovies(value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
