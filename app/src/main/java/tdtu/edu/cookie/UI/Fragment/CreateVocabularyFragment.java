package tdtu.edu.cookie.UI.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.ViewModel.TopicVM;
import tdtu.edu.cookie.databinding.FragmentCreateVocabularyBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateVocabularyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateVocabularyFragment extends Fragment {
    TopicVM topicVM;
    com.google.android.material.textfield.TextInputEditText editTextVocabulary;
    com.google.android.material.textfield.TextInputEditText editTextDefinition;
    FragmentCreateVocabularyBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateVocabularyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateVocabularyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateVocabularyFragment newInstance(String param1, String param2) {
        CreateVocabularyFragment fragment = new CreateVocabularyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateVocabularyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextVocabulary = binding.editTextVocabulary;
        editTextDefinition = binding.editTextDefinition;
        editTextVocabulary.setText("hello");
        editTextDefinition.setText("xin chao");
        editTextVocabulary.requestFocus();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }
}