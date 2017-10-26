package com.sdsmdg.harjot.AZMusic.fragments.FavouritesFragment;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.sdsmdg.harjot.AZMusic.Config;
import com.sdsmdg.harjot.AZMusic.clickitemtouchlistener.ClickItemTouchListener;
import com.sdsmdg.harjot.AZMusic.itemtouchhelpers.SimpleItemTouchHelperCallback;
import com.sdsmdg.harjot.AZMusic.activities.HomeActivity;
import com.sdsmdg.harjot.AZMusic.models.LocalTrack;
import com.sdsmdg.harjot.AZMusic.models.Track;
import com.sdsmdg.harjot.AZMusic.models.UnifiedTrack;
import com.sdsmdg.harjot.AZMusic.MusicDNAApplication;
import com.sdsmdg.harjot.AZMusic.R;
import com.sdsmdg.harjot.AZMusic.activities.SplashActivity;
import com.sdsmdg.harjot.AZMusic.utilities.CommonUtils;
import com.sdsmdg.harjot.AZMusic.imageloader.ImageLoader;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment implements
        FavouriteTrackAdapter.OnDragStartListener,
        FavouriteTrackAdapter.onEmptyListener,
        FavouriteTrackAdapter.onMoveRemoveistener {


    RecyclerView favouriteRecycler;
    FavouriteTrackAdapter fAdapter;
    LinearLayoutManager mLayoutManager2;

    ItemTouchHelper mItemTouchHelper;

    favouriteFragmentCallback mCallback;

    LinearLayout noFavouriteContent;

    FloatingActionButton playAll;

    View bottomMarginLayout;

    ImageView backdrop;
    TextView fragTitle;
    ImageView backBtn, addToQueueIcon, fragIcon;

    ImageLoader imgLoader;

    @Override
    public void onEmpty() {
        noFavouriteContent.setVisibility(View.VISIBLE);
        playAll.setVisibility(View.GONE);
    }

    @Override
    public void updateFavFragment() {
        if (HomeActivity.favouriteTracks.getFavourite().size() > 0) {
            UnifiedTrack ut = HomeActivity.favouriteTracks.getFavourite().get(0);
            if (ut.getType()) {
                LocalTrack lt = ut.getLocalTrack();
                imgLoader.DisplayImage(lt.getPath(), backdrop);
            } else {
                Track t = ut.getStreamTrack();
                Picasso.with(getContext())
                        .load(t.getArtworkURL())
                        .resize(100, 100)
                        .error(R.drawable.ic_default)
                        .placeholder(R.drawable.ic_default)
                        .into(backdrop);
            }
        }
    }

    public interface onFavouriteItemClickedListener {
        public void onFavouriteItemClicked(int position);
    }

    public interface onFavouritePlayAllListener {
        public void onFavouritePlayAll();
    }

    public interface favouriteFragmentCallback {
        void onFavouriteItemClicked(int position);

        void onFavouritePlayAll();

        void addFavToQueue();
    }

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (favouriteFragmentCallback) context;
            imgLoader = new ImageLoader(context);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.fav_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragIcon = (ImageView) view.findViewById(R.id.fav_frag_icon);
        fragIcon.setImageTintList(ColorStateList.valueOf(HomeActivity.themeColor));

        fragTitle = (TextView) view.findViewById(R.id.fav_fragment_title);
        if (SplashActivity.tf4 != null)
            fragTitle.setTypeface(SplashActivity.tf4);

        addToQueueIcon = (ImageView) view.findViewById(R.id.add_fav_to_queue_icon);
        addToQueueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.addFavToQueue();
            }
        });

        backdrop = (ImageView) view.findViewById(R.id.fav_backdrop);

        if (HomeActivity.favouriteTracks.getFavourite().size() > 0) {
            UnifiedTrack ut = HomeActivity.favouriteTracks.getFavourite().get(0);
            if (ut.getType()) {
                LocalTrack lt = ut.getLocalTrack();
                imgLoader.DisplayImage(lt.getPath(), backdrop);
            } else {
                Track t = ut.getStreamTrack();
                Picasso.with(getContext())
                        .load(t.getArtworkURL())
                        .resize(100, 100)
                        .error(R.drawable.ic_default)
                        .placeholder(R.drawable.ic_default)
                        .into(backdrop);
            }
        }

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = CommonUtils.dpTopx(65, getContext());

        favouriteRecycler = (RecyclerView) view.findViewById(R.id.favouriteRecycler);
        noFavouriteContent = (LinearLayout) view.findViewById(R.id.noFavouriteContent);
        playAll = (FloatingActionButton) view.findViewById(R.id.fav_play_all_fab);
        if (SplashActivity.tf4 != null)
            ((TextView) view.findViewById(R.id.favNoContentText)).setTypeface(SplashActivity.tf4);

        if (HomeActivity.favouriteTracks.getFavourite().size() == 0) {
            favouriteRecycler.setVisibility(View.INVISIBLE);
            playAll.setVisibility(View.INVISIBLE);
            noFavouriteContent.setVisibility(View.VISIBLE);
        } else {
            favouriteRecycler.setVisibility(View.VISIBLE);
            playAll.setVisibility(View.VISIBLE);
            noFavouriteContent.setVisibility(View.INVISIBLE);
        }

        fAdapter = new FavouriteTrackAdapter(HomeActivity.favouriteTracks.getFavourite(), this, getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        favouriteRecycler.setLayoutManager(mLayoutManager2);
        favouriteRecycler.setItemAnimator(new DefaultItemAnimator());
        favouriteRecycler.setAdapter(fAdapter);

        favouriteRecycler.addOnItemTouchListener(new ClickItemTouchListener(favouriteRecycler) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onFavouriteItemClicked(position);
                return true;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        if (HomeActivity.favouriteTracks.getFavourite().size() == 0) {
            playAll.setVisibility(View.INVISIBLE);
        } else {
            playAll.setVisibility(View.VISIBLE);
        }
        playAll.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeActivity.favouriteTracks.getFavourite().size() > 0) {
                    HomeActivity.queue.getQueue().clear();
                    for (int i = 0; i < HomeActivity.favouriteTracks.getFavourite().size(); i++) {
                        HomeActivity.queue.getQueue().add(HomeActivity.favouriteTracks.getFavourite().get(i));
                    }
                    HomeActivity.queueCurrentIndex = 0;
                    mCallback.onFavouritePlayAll();
                }
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(fAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(favouriteRecycler);

        rlAds = (RelativeLayout) view.findViewById(R.id.rlAds);
        setDisplayBanner();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    AdView mAdView;
    RelativeLayout rlAds;
    private void setDisplayBanner()
    {


        //String deviceid = tm.getDeviceId();

        mAdView = new AdView(getActivity());
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(Config.ADS_BNR_2);

        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
        rlAds.addView(mAdView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("88AA2259C43545CCDA7EE426C04BB233")
                .build();
        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //.addTestDevice(deviceid).build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAll.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator());
            }
        }, 500);
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicDNAApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    public void updateData() {
        if (HomeActivity.favouriteTracks.getFavourite().size() == 0) {
            favouriteRecycler.setVisibility(View.INVISIBLE);
            playAll.setVisibility(View.INVISIBLE);
            noFavouriteContent.setVisibility(View.VISIBLE);
        } else {
            favouriteRecycler.setVisibility(View.VISIBLE);
            playAll.setVisibility(View.VISIBLE);
            noFavouriteContent.setVisibility(View.INVISIBLE);
        }
        if (fAdapter != null) {
            fAdapter.notifyDataSetChanged();
        }
    }

}
