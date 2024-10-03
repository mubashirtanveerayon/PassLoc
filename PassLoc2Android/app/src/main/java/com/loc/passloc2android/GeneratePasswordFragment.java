package com.loc.passloc2android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import services.generator.password.UniquePasswordGenerator;
import services.model.PasswordElements;
import services.model.PasswordUnit;


public class GeneratePasswordFragment extends Fragment {


    int currentLength;

    TextInputLayout passwordInputLayout;
    private TextInputEditText passwordEditText;

    Switch upperCaseToggleSwitch, lowerCaseToggleSwitch, digitToggleSwitch, specialToggleSwitch;

    Slider lengthSlider;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_generate_password, container, false);
        upperCaseToggleSwitch = view.findViewById(R.id.generate_password_A_Z_switch);
        lowerCaseToggleSwitch = view.findViewById(R.id.generate_password_a_z_switch);
        digitToggleSwitch = view.findViewById(R.id.generate_password_digits_switch);
        specialToggleSwitch = view.findViewById(R.id.generate_password_specials_switch);

        passwordEditText = view.findViewById(R.id.generate_password_text_view);


        passwordInputLayout = view.findViewById(R.id.generate_password_generate_input_layout);


        lengthSlider = view.findViewById(R.id.length_slider);





        return view;

    }

    public void onResume() {
        super.onResume();

        lengthSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                int length = (int)value;
                slider.setValue(length);
                if(length == currentLength)return;
                generatePassword();
            }


        });

        passwordInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("passloc_entry", passwordEditText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(),"Password copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void generatePassword() {
        int length = (int) lengthSlider.getValue();
        int activeCount = 0;
        if(lowerCaseToggleSwitch.isChecked()) activeCount++;
        if(specialToggleSwitch.isChecked()) activeCount++;
        if(upperCaseToggleSwitch.isChecked()) activeCount++;
        if(digitToggleSwitch.isChecked()) activeCount++;

        if(activeCount == 0)
            return;
        int countPerUnit = length/activeCount;


        ArrayList<PasswordUnit> units = new ArrayList<>();
        if(upperCaseToggleSwitch.isChecked())
            units.add(new PasswordUnit(PasswordElements.CharacterType.UPPERCASE_LETTER,countPerUnit,true));
        if(lowerCaseToggleSwitch.isChecked())
            units.add(new PasswordUnit(PasswordElements.CharacterType.LOWERCASE_LETTER,countPerUnit,true));
        if(digitToggleSwitch.isChecked())
            units.add(new PasswordUnit(PasswordElements.CharacterType.DIGIT,countPerUnit,true));
        if(specialToggleSwitch.isChecked())
            units.add(new PasswordUnit(PasswordElements.CharacterType.SPECIAL,countPerUnit,true));

        int remaining = length - countPerUnit * units.size();
        if(remaining != 0){
            PasswordUnit first = units.get(0);
            PasswordUnit newUnit = new PasswordUnit(first.getType(),remaining+countPerUnit,false);
            units.remove(first);
            units.add(newUnit);
        }


        String allCharacters = UniquePasswordGenerator.generateStringFromPasswordUnits(units);
        String password = UniquePasswordGenerator.shuffle(allCharacters);


        passwordEditText.setText(password);

        currentLength = length;



    }

}