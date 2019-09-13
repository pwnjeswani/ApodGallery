package com.pawanjeswani.apodgallery.util;

import androidx.viewpager.widget.ViewPager;

public class ViewPagerPaginate {



    public ViewPager viewPager;
    private Callbacks callbacks;
    private ViewPagerCallBacks viewPagerCallBacks;

    private int lastPageLoaded = 1;


    private int loadingTriggerThreshold = 3;

    public interface Callbacks {
        /** Called when next page of data needs to be loaded. */
        void onLoadMore();

        /**
         * Called to check if loading of the next page is currently in progress. While loading is in progress won't be called.
         *
         * @return true if loading is currently in progress, false otherwise.
         */
        boolean isLoading();

        /**
         * Called to check if there is more data (more pages) to load. If there is no more pages to load, won't be called and loading row, if used, won't be added.
         *
         * @return true if all pages has been loaded, false otherwise.
         */
        boolean hasLoadedAllItems();
    }

    public interface ViewPagerCallBacks{

        void onPageScrollStateChanged(int state);

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);


    }

    public ViewPagerPaginate(ViewPager viewPager, Callbacks callback, ViewPagerCallBacks viewPagerCallBacks) throws Exception {
        if(viewPager!=null){
            this.viewPager = viewPager;
        }
        else{
            throw new Exception("view pager null");
        }

        if(callback!=null){
            this.callbacks = callback;
        }
        else{
            throw new Exception("callback null");
        }
        if(viewPagerCallBacks != null){
            this.viewPagerCallBacks = viewPagerCallBacks;
        }
        else{
            throw new Exception("callbacks null");
        }
        setViewPagerPageChangeListener();


    }


    public void setLoadingTriggerThreshold(int loadingTriggerThreshold) {
        this.loadingTriggerThreshold = loadingTriggerThreshold;
    }

    private void setViewPagerPageChangeListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                viewPagerCallBacks.onPageScrolled(i,v,i1);
            }

            @Override
            public void onPageSelected(int i) {
                if(!(callbacks.hasLoadedAllItems() || callbacks.isLoading())){
                    if(i != lastPageLoaded && i-lastPageLoaded>0){

                        if(viewPager.getAdapter().getCount()-loadingTriggerThreshold<=i){
                            callbacks.onLoadMore();
                        }
                    }
                }


                lastPageLoaded = i;
                viewPagerCallBacks.onPageSelected(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                viewPagerCallBacks.onPageScrollStateChanged( i);
            }
        });
    }



}
