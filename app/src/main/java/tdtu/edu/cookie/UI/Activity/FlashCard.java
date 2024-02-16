package tdtu.edu.cookie.UI.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.Length;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import tdtu.edu.cookie.Database.Dao.WordsDao;
import tdtu.edu.cookie.Database.Entity.TestModel;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.Database.Entity.wordsQuizz;
import tdtu.edu.cookie.R;
import tdtu.edu.cookie.UI.Adapter.CardStackAdapter;
import tdtu.edu.cookie.databinding.ActivityFlashCardBinding;


public class FlashCard extends AppCompatActivity {

    ActivityFlashCardBinding binding;

    private static final String TAG = "FlashCardActivity";
    private CardStackLayoutManager manager;

    private CardStackAdapter adapter;
    private List<Words> list = new ArrayList<>();


    private boolean isFrontCardShowing = true;
    private WordsDao wordsDao;

    CardStackView cardStackView;


    //Attribute in view
    private TextView txtNumber;

    private boolean isAutoplayRunning = false; // Flag to track autoplay state
    private Handler autoplayHandler = new Handler();
    private Runnable autoplayRunnable;
    ImageButton autoplayButton;
    ImageButton shuffleButton;
    ImageButton soundButton;

    ImageButton rewindButton;

    private Stack<Integer> cardPositions;


    private int wordlearned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlashCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cardStackView = binding.cardStackView;
        txtNumber = binding.txtNumber;
        String topicId = String.valueOf(getIntent().getIntExtra("topicId", 0));
        wordlearned = 0;


        cardStackView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            private boolean isSwipeAction = false;

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                final int touchSlop = ViewConfiguration.get(rv.getContext()).getScaledTouchSlop();

                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isSwipeAction = false; // Reset the flag when a touch event starts
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = Math.abs(e.getX() - e.getRawX());
                        float deltaY = Math.abs(e.getY() - e.getRawY());

                        if (deltaX > touchSlop && deltaX > deltaY) {
                            // Set the flag if horizontal movement exceeds the threshold
                            isSwipeAction = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isSwipeAction) {
                            // Get the clicked card position
                            int clickedPosition = manager.getTopPosition();
                            toggleCardView(cardStackView, clickedPosition, e);
                        }
                        isSwipeAction = false; // Reset the flag when the touch event ends
                        break;
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });


        //Card Stack manager
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right) {
                    int cardSwiped = manager.getTopPosition() - 1;
                    wordlearned += 1;
                    updateWordStatus(cardSwiped, Integer.parseInt(topicId), "Đã học ");

                }
                if (direction == Direction.Top) {
//                    Toast.makeText(FlashCard.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left) {
//                    Toast.makeText(FlashCard.this, "Direction Left", Toast.LENGTH_SHORT).show();
                    int cardSwiped = manager.getTopPosition() - 1;
                    updateWordStatus(cardSwiped, Integer.parseInt(topicId), "Đang học ");
                }
                if (direction == Direction.Bottom) {
//                    Toast.makeText(FlashCard.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                // Handle the case when there are no cards (empty list)
                if (list.size() == 0) {
                    return;
                }
                int newPosition = manager.getTopPosition();
                if (newPosition + 1 >= list.size()) {
                    return;
                }
                manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
                manager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Top)
                        .setInterpolator(new AccelerateInterpolator())
                        .build());
                cardStackView.swipe();

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                int currentPosition = manager.getTopPosition(); // 0-based index
                int totalCards = manager.getItemCount();

                // Handle the case when there are no cards (empty list)
                if (totalCards == 0) {
                    return;
                }
                currentPosition += 1;

                currentPosition = Math.min(currentPosition, totalCards);

                String displayText = currentPosition + "/" + totalCards;
                txtNumber.setText(displayText);

                int progressValue = (int) ((float) currentPosition / totalCards * 100);

                // Update the LinearProgressIndicator
                LinearProgressIndicator progressIndicator = findViewById(R.id.progressIndicator);
                int indicatorColor = Color.parseColor("#B99B6B");
                progressIndicator.setIndicatorColor(indicatorColor);
                progressIndicator.setProgressCompat(progressValue, true);

            }


            @Override
            public void onCardDisappeared(View view, int position) {
                if (position == list.size() - 1) {
                    // The last card has disappeared

                    // Show congratulatory dialog
                    FancyAlertDialog.Builder
                            .with(FlashCard.this)
                            .setTitle("Chúc mừng")
                            .setBackgroundColor(Color.parseColor("#FEF5E7"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                            .setMessage("Bạn đã hoàn thành flashcard này !")
                            .setNegativeBtnText("Học lại")
                            .setPositiveBtnBackground(Color.parseColor("#FEF5E7"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                            .setPositiveBtnText("Hoàn tất")
                            .setAnimation(Animation.POP)
                            .setIcon(R.drawable.cookie_flash, View.VISIBLE)
                            .onPositiveClicked(dialog -> finish())
                            .onNegativeClicked(dialog -> {
                                        dialog.dismiss();
                                        adapter.notifyDataSetChanged();
                                    }
                            ).build().show();
                }
            }
        });


//        Get words by topic ID here
        getListWordsByTopic(Integer.parseInt(topicId));


        //Setup the card stack
        manager.setStackFrom(StackFrom.Top);
        manager.setVisibleCount(1);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(this, list);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());


        soundButton = binding.autosoundBtn;
        shuffleButton = binding.shuffleBtn;
        autoplayButton = binding.autoplay;
        rewindButton = binding.rewindBtn;
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int topPosition = manager.getTopPosition();
                Words currentWord = list.get(topPosition);
                playAudio(currentWord.getAudio());
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAutoplay();
                // Shuffle the list
                Collections.shuffle(list);

                // Notify the adapter of the data set change
                adapter.notifyDataSetChanged();

                // Reset the card stack position
                manager.scrollToPosition(0);
            }
        });

        autoplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAutoplayRunning) {
                    // Autoplay is running, stop it
                    binding.autoplay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                    stopAutoplay();
                } else {
                    // Autoplay is not running, start it
                    int topPosition = manager.getTopPosition();
                    Words currentWord = list.get(topPosition);
                    playAudio(currentWord.getAudio());
                    startAutoplay();

                }
            }
        });

        cardPositions = new Stack<>();

        //Rewind button
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardStackView.rewind();
            }
        });


        //Close activity
        binding.imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAutoplay();
                finish();
            }
        });
    }


    private void startAutoplay() {
        binding.autoplay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
        isAutoplayRunning = true;

        autoplayRunnable = new Runnable() {

            @Override
            public void run() {
                // Swipe right
                int topPosition = manager.getTopPosition();
                if (topPosition + 1 >= list.size()) {
                    stopAutoplay();
                }
                Words currentWord = list.get(topPosition + 1);
                playAudio(currentWord.getAudio());

                manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
                manager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Top)
                        .setInterpolator(new AccelerateInterpolator())
                        .build());
                cardStackView.swipe();

                // Reschedule the autoplay after a delay

                autoplayHandler.postDelayed(this, 5000);
                // Adjust the delay time as needed
            }
        };

        // Schedule the autoplay runnable for the first time
        autoplayHandler.postDelayed(autoplayRunnable, 5000); // Adjust the delay time as needed
    }

    private void stopAutoplay() {
        isAutoplayRunning = false;

        // Remove any pending runnables
        autoplayHandler.removeCallbacks(autoplayRunnable);

        // Reset the swipeable method
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);

        // Reset the swipe animation setting
        manager.setSwipeAnimationSetting(new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new DecelerateInterpolator())
                .build());
    }

    private void updateWordStatus(int cardPosition, int topicId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (cardPosition >= 0 && cardPosition < list.size()) {
            Words word = list.get(cardPosition);

            DocumentReference wordRef = db.collection("Topics").document(String.valueOf(topicId)).collection("Words").document(String.valueOf(word.getId()));
            wordRef.update("status", status).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (status.equals("Đã học ")) {
                                FancyToast.makeText(FlashCard.this, status + list.get(cardPosition).getEnglish_word(), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            } else if (status.equals("Đang học ")) {
                                FancyToast.makeText(FlashCard.this, status + list.get(cardPosition).getEnglish_word(), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error updating word status", e);
                            // Handle the error as needed
                        }
                    });
        }
    }


    private void getListWordsByTopic(int topicId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Topics").document(String.valueOf(topicId)).collection("Words").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    Words word = new Words(Integer.parseInt(snapshot.getId()), snapshot.getString("english_word"),
                            snapshot.getString("vietnamese_word"), snapshot.getString("word_form"), snapshot.getString("audio"), snapshot.getString("status"));
                    list.add(word);

                    adapter.notifyDataSetChanged();

                }
            }
        });
    }


    private void playAudio(String url) {

        //  Play audio from url but not use intent
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(FlashCard.this, "There is no sound of this word", Toast.LENGTH_SHORT).show();
        }

    }

    private void toggleCardView(CardStackView cardStackView, int position, MotionEvent event) {
        View clickedCard = manager.findViewByPosition(position);

        if (clickedCard != null) {
            ViewFlipper viewFlipper = clickedCard.findViewById(R.id.viewFlipper);

            // Determine the current displayed child view
            int displayedChild = viewFlipper.getDisplayedChild();

            View targetView = viewFlipper.getChildAt(displayedChild == 0 ? 1 : 0);

            if (displayedChild == 0) {
                // Currently showing the front card, flip to show the back card
                if (event == null) {
                    circularRevealAnimation(clickedCard, (float) 482.96387, (float) 724.93164, targetView);
                } else {
                    circularRevealAnimation(clickedCard, event.getX(), event.getY(), targetView);
                }

                viewFlipper.setDisplayedChild(1);
            } else {
                // Currently showing the back card, flip to show the front card
                if (event == null) {
                    circularRevealAnimation(clickedCard, (float) 482.96387, (float) 724.93164, targetView);
                } else {
                    circularRevealAnimation(clickedCard, event.getX(), event.getY(), targetView);
                }
                viewFlipper.setDisplayedChild(0);
            }
        }
    }


    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }


    private void circularRevealAnimation(View view, float centerX, float centerY, View targetView) {
        int startRadius = 0;
        int endRadius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(targetView, (int) centerX, (int) centerY, startRadius, endRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                targetView.setVisibility(View.VISIBLE);
//            }
//        });
        animator.start();
    }


}