package mccammon.cale.jeopardyandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;
import static mccammon.cale.jeopardyandroid.ClueActivity.getLifetimeScore;

//Play Fragment is the most essential fragment. It makes the API call, updates the UI with the
//clue's information, provides error handling, updates the scores, and handles all game logic.
public class PlayFragment extends Fragment {
    private TextView responseView;
    private TextView categoryText;
    private EditText answerText;
    private ProgressBar progressBar;
    private Clue clue;
    private static final String API_URL = "http://jservice.io/api/random";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play,
                container, false);

        //Get layout elements.
        answerText = view.findViewById(R.id.answerText);
        responseView = view.findViewById(R.id.responseView);
        categoryText = view.findViewById(R.id.categoryText);
        progressBar = view.findViewById(R.id.progressBar);
        Button skipButton = view.findViewById(R.id.skipButton);
        Button submitButton = view.findViewById(R.id.submitButton);
        Button helpButton = view.findViewById(R.id.helpButton);

        //Action listener for the text field where the user enters the answer. This listens for the
        //keyboard DONE action. If the field is blank, do nothing. If the answer is correct,
        //show the appropriate dialog and dismiss the keyboard. If the answer is incorrect, show
        // the appropriate dialog and dismiss the keyboard.
        answerText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if(answerText.getText().toString().equals(""))  {
                        return false;
                    } else if(validateAnswer()) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        showCorrectDialog();
                        answerText.setText("");
                        return true;
                    } else {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        showIncorrectDialog();
                        return true;
                    }
                }
                return false;
            }
        });

        //Submit button click listener. Does basically the same thing as the above listener
        //except this is for when the user presses the Submit button.
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if(answerText.getText().toString().equals(""))  {
                        return;
                    } else if(validateAnswer()) {
                        showCorrectDialog();
                        answerText.setText("");
                    } else {
                        showIncorrectDialog();
                    }
                } catch (NullPointerException e) {
                    showNetworkError();
                }
            }
        });

        //Skip button listener. If the user presses skip, the get the next clue.
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new RetrieveFeedTask().execute();
                    answerText.setText("");
                } catch (NullPointerException e) {
                    showNetworkError();
                }
            }
        });

        //Help button listener. If the user presses the Show Answer button, then show the
        //help dialog.
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showHelpDialog();
                    answerText.setText("");
                } catch (NullPointerException e) {
                    showNetworkError();
                }
            }
        });

        if(savedInstanceState == null) {
            new RetrieveFeedTask().execute();
        }

        return view;
    }

    //Validate whether the entered answer and the actual answer match.
    private boolean validateAnswer() {
            boolean isCorrect = false;

            //Convert the entered answer and actual answer to upper case for comparison.
            String enteredAnswer = answerText.getText().toString().toUpperCase();
            String actualAnswer = clue.getAnswer().toUpperCase();

            //To improve the user experience, provide logic to ignore the first instances of a, an,
            //and the in the answer.
            String[] actualAnswerArray = actualAnswer.split(" ");
            String[] wordsToReplace = {"THE ", "A ", "AN "};
            for (int i = 0; i < wordsToReplace.length; i++) {
                if (wordsToReplace[i].equals(actualAnswerArray[0] + " ")) {
                    enteredAnswer = enteredAnswer.replaceFirst(wordsToReplace[i], "");
                    actualAnswer = actualAnswer.replaceFirst(wordsToReplace[i], "");
                }
            }

            if (enteredAnswer.equals(actualAnswer)) {
                isCorrect = true;
            }

            return isCorrect;
    }

    //Show dialog when answer is correct. Get next clue when dialog is dismissed.
    private void showCorrectDialog() {
        if(isNetworkAvailable()) {
            if(responseView.getText().equals("Content unavailable.")) {
                return;
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        this.getActivity()).create();
                alertDialog.setMessage("Good job! Your answer is correct. Let's try another.");
                alertDialog.show();

                //Calculate session and lifetime scores.
                int valueAsInt = Integer.parseInt((clue.getValue()));
                int sessionScore = valueAsInt + ClueActivity.getSessionScore();
                ClueActivity.setSessionScore(sessionScore);
                editLifetimeScore(getLifetimeScore(getActivity()) + valueAsInt);

                //Show snackbar with updated scores.
                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(android.R.id.content),
                                "+" + clue.getValue() + " (Session: " +
                                        String.valueOf(ClueActivity.getSessionScore()) + " | " +
                                        "Lifetime: " + String.valueOf(getLifetimeScore(getActivity())) + ")",
                                Snackbar.LENGTH_LONG);

                //Change snackbar appearance.
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);
                snackbar.show();

                //When the dialog is dismissed, get the next clue.
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        new RetrieveFeedTask().execute();
                    }
                });
            }
        } else {
            showNetworkError();
        }
    }

    //Show dialog when answer is not correct.
    private void showIncorrectDialog() {
        if(isNetworkAvailable()) {
            if(responseView.getText().equals("Content unavailable.")) {
                return;
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        this.getActivity()).create();
                alertDialog.setMessage("Sorry! Your answer is incorrect. Why not try again?");
                alertDialog.show();

                //Calculate session and lifetime scores.
                int valueAsInt = Integer.parseInt((clue.getValue()));
                int sessionScore = ClueActivity.getSessionScore() - valueAsInt;
                ClueActivity.setSessionScore(sessionScore);
                editLifetimeScore(getLifetimeScore(getActivity()) - valueAsInt);

                //Show snackbar with updated scores.
                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(android.R.id.content),
                                "-" + clue.getValue() + " (Session: " +
                                        String.valueOf(ClueActivity.getSessionScore()) + " | " +
                                        "Lifetime: " + String.valueOf(getLifetimeScore(getActivity())) + ")",
                                Snackbar.LENGTH_LONG);

                //When the dialog is dismissed, get the next clue.
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
            }
        } else {
            showNetworkError();
        }
    }

    //Helper method for updating lifetime score.
    public void editLifetimeScore(int input) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                ClueActivity.getPrefsData(), MODE_PRIVATE).edit();
        editor.putInt("lifetimeScore", input);
        editor.apply();
    }

    //Reveal answer. Get next clue when dialog is dismissed.
    private void showHelpDialog() {
        if(isNetworkAvailable()) {
            if(responseView.getText().equals("Content unavailable.")) {
                return;
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        this.getActivity()).create();
                alertDialog.setMessage("The answer is: " + clue.getAnswer() + ". Try another?");
                alertDialog.show();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        new RetrieveFeedTask().execute();
                    }
                });
            }
        } else {
            showNetworkError();
        }
    }

    //Check if user is connected to internet.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Network error dialog.
    private void showNetworkError() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                this.getActivity()).create();
        alertDialog.setMessage("Check your network connection and try again.");
        alertDialog.show();
    }

    //RetrieveFeedTask for making API call.
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            categoryText.setText("");
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray jsonObj = new JSONArray(response);
                progressBar.setVisibility(View.GONE);
                ArrayList<Clue> clues = Clue.fromJson(jsonObj);
                clue = clues.get(0);
                categoryText.setText(clue.getCategory().getTitle().toUpperCase());
                responseView.setText(clue.getQuestion());

                //We don't want any bad data or empty fields. If any of the fields are null or
                //empty, then show Content Unavailable text.
                if (clue.getCategory().getTitle() == null ||
                        clue.getCategory().getTitle().equals("null") ||
                        clue.getCategory().getTitle().equals("") ||
                        clue.getCategory().getTitle().isEmpty() || clue.getValue() == null ||
                        clue.getValue().equals("null") || clue.getValue().isEmpty() ||
                        clue.getValue().equals("0") || clue.getValue().equals("") ||
                        clue.getQuestion() == null || clue.getQuestion().equals("null") ||
                        clue.getQuestion().equals("") || clue.getQuestion().isEmpty() ||
                        clue.getAnswer() == null || clue.getAnswer().equals("null") ||
                        clue.getAnswer().equals("") || clue.getAnswer().isEmpty()) {
                    categoryText.setText("");
                    responseView.setText("Content unavailable.");
                }

            } catch (JSONException | NullPointerException e) {
                responseView.setText("Content unavailable.");
                progressBar.setVisibility(View.GONE);
                showNetworkError();
            }
        }
    }

}