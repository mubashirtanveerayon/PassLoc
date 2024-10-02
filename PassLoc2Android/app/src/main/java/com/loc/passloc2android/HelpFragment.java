package com.loc.passloc2android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.loc.service.utils.SwipeDetector;

public class HelpFragment extends Fragment {


    int currentCardIndex=0;


    CardView[] cards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_help, container, false);


        String[] aboutText = getActivity().getResources().getStringArray(R.array.about);


        ArrayAdapter<String> aboutArrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,aboutText);

        ListView aboutListView = view.findViewById(R.id.about_list_view);
        aboutListView.setAdapter(aboutArrayAdapter);


        String[] encryptionText = getActivity().getResources().getStringArray(R.array.encryption);
        ArrayAdapter<String>encryptionArrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,encryptionText);

        ListView encryptionListView = view.findViewById(R.id.encryption_list_view);
        encryptionListView.setAdapter(encryptionArrayAdapter);


        String[] uiText = getActivity().getResources().getStringArray(R.array.ui);
        ArrayAdapter<String>uiArrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,uiText);

        ListView uiListView = view.findViewById(R.id.ui_list_view);
        uiListView.setAdapter(uiArrayAdapter);



        cards = new CardView[]{view.findViewById(R.id.about_card),view.findViewById(R.id.encryption_card),view.findViewById(R.id.ui_card)};




        new SwipeDetector(view).setOnSwipeListener(new SwipeDetector.onSwipeEvent(){

            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType) {

                Animation enterAnimation;

                int newCardIndex;

                if(swipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT){
                    newCardIndex = (currentCardIndex+cards.length-1)%cards.length;
                    enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_left_to_right);
                }else{
                    newCardIndex = (currentCardIndex+1)%cards.length;
                    enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_right_to_left);

                }


                enterAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        cards[currentCardIndex].setVisibility(View.GONE);
                        cards[newCardIndex].setVisibility(View.VISIBLE);

                        currentCardIndex = newCardIndex;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                cards[newCardIndex].startAnimation(enterAnimation);










            }
        });

        return view;
    }
}
