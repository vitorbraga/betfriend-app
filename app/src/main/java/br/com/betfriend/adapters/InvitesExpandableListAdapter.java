package br.com.betfriend.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import br.com.betfriend.MainActivity;
import br.com.betfriend.R;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.UserDataDTO;
import br.com.betfriend.utils.CircleTransformation;
import br.com.betfriend.utils.Constants;
import br.com.betfriend.utils.ConvertHelper;
import br.com.betfriend.utils.TeamsDataEnum;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InvitesExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private final Context context;

    private ProgressBar mProgressBar;

    private ArrayList<Bet> bets;

    private UserDataDTO userData;

    private static class ViewHolderGroup {
        TextView homeTeam;
        TextView awayTeam;
        TextView matchId;
        TextView matchDate;
        ImageView homeLogo;
        ImageView awayLogo;
        TextView amount;
        ImageView personPhoto;
        TextView personName;
    }

    private static class ViewHolderChild {
        Button acceptButton;
        Button refuseButton;
        TextView betId;
        TextView matchId;
        TextView amount;
        TextView friendOption;
        TextView friendId;
        RadioGroup radioGroup;
        RadioButton homeRadioButton;
        RadioButton drawRadioButton;
        RadioButton awayRadioButton;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        View rowView = convertView;
        ViewHolderChild viewHolderChild;

        // reuse views
        if (rowView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.invite_bet_list_item, parent, false);

            viewHolderChild = new ViewHolderChild();
            viewHolderChild.acceptButton = (Button) rowView.findViewById(R.id.accept_button);
            viewHolderChild.refuseButton = (Button) rowView.findViewById(R.id.refuse_button);
            viewHolderChild.betId = (TextView) rowView.findViewById(R.id.bet_id);
            viewHolderChild.matchId = (TextView) rowView.findViewById(R.id.match_id);
            viewHolderChild.amount = (TextView) rowView.findViewById(R.id.amount);
            viewHolderChild.friendOption = (TextView) rowView.findViewById(R.id.friend_option);
            viewHolderChild.friendId = (TextView) rowView.findViewById(R.id.friend_id);
            viewHolderChild.radioGroup = (RadioGroup) rowView.findViewById(R.id.radio_group);
            viewHolderChild.homeRadioButton = (RadioButton) rowView.findViewById(R.id.radio_team_1);
            viewHolderChild.drawRadioButton = (RadioButton) rowView.findViewById(R.id.radio_draw);
            viewHolderChild.awayRadioButton = (RadioButton) rowView.findViewById(R.id.radio_team_2);

            rowView.setTag(viewHolderChild);

        } else {
            viewHolderChild = (ViewHolderChild) rowView.getTag();
        }

        String homeTeam = bets.get(groupPosition).getMatch().getHomeTeam().trim();
        String awayTeam = bets.get(groupPosition).getMatch().getAwayTeam().trim();
        String matchId = bets.get(groupPosition).getMatch().getMatchId().toString();
        String betId = bets.get(groupPosition).get_id();
        String friendId = bets.get(groupPosition).getSrcPerson().getPersonId();
        Integer amount = bets.get(groupPosition).getAmount();

        String option = bets.get(groupPosition).getOption();

        viewHolderChild.homeRadioButton.setText(TeamsDataEnum.get(homeTeam).label());
        viewHolderChild.awayRadioButton.setText(TeamsDataEnum.get(awayTeam).label());
        viewHolderChild.matchId.setText(matchId);
        viewHolderChild.amount.setText(context.getString(R.string.amount, amount));
        viewHolderChild.betId.setText(betId);
        viewHolderChild.friendId.setText(friendId);

        Drawable img;

        // Set radio buttons
        switch (option) {

            case "1":

                viewHolderChild.homeRadioButton.setEnabled(false);
                img = getDrawableFromURL(bets.get(groupPosition).getSrcPerson().getPersonPhoto());
                viewHolderChild.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                viewHolderChild.friendOption.setText(String.valueOf(viewHolderChild.homeRadioButton.getId()));

                break;

            case "X":

                viewHolderChild.drawRadioButton.setEnabled(false);
                img = getDrawableFromURL(bets.get(groupPosition).getSrcPerson().getPersonPhoto());
                viewHolderChild.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                viewHolderChild.friendOption.setText(String.valueOf(viewHolderChild.drawRadioButton.getId()));

                break;

            default:

                viewHolderChild.awayRadioButton.setEnabled(false);
                img = getDrawableFromURL(bets.get(groupPosition).getSrcPerson().getPersonPhoto());
                viewHolderChild.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                viewHolderChild.friendOption.setText(String.valueOf(viewHolderChild.awayRadioButton.getId()));
                break;
        }

        final RadioButton homeRadioButton = viewHolderChild.homeRadioButton;
        final RadioButton drawRadioButton = viewHolderChild.drawRadioButton;
        final RadioButton awayRadioButton = viewHolderChild.awayRadioButton;

        viewHolderChild.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checked = group.getCheckedRadioButtonId();

                Drawable img = getDrawableFromURL(userData.getPersonPhoto());
                switch (checked) {

                    case R.id.radio_team_1:

                        homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                        if (drawRadioButton.isEnabled()) {
                            drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        if (awayRadioButton.isEnabled()) {
                            awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        break;

                    case R.id.radio_draw:

                        drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                        if (homeRadioButton.isEnabled()) {
                            homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        if (awayRadioButton.isEnabled()) {
                            awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        break;

                    case R.id.radio_team_2:

                        awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                        if (homeRadioButton.isEnabled()) {
                            homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        if (drawRadioButton.isEnabled()) {
                            drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                        break;
                }
            }
        });

        viewHolderChild.acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LinearLayout viewParent = (LinearLayout) v.getParent().getParent();

                final String matchId = ((TextView) viewParent.findViewById(R.id.match_id)).getText().toString();
                final String betId = ((TextView) viewParent.findViewById(R.id.bet_id)).getText().toString();
                final String friendId = ((TextView) viewParent.findViewById(R.id.friend_id)).getText().toString();
                final Integer amount = Integer.parseInt(((TextView) viewParent.findViewById(R.id.amount)).getText().toString());

                RadioGroup radioGroup = (RadioGroup) viewParent.findViewById(R.id.radio_group);
                int checked = radioGroup.getCheckedRadioButtonId();
                int friendOption = Integer.parseInt(((TextView) viewParent
                        .findViewById(R.id.friend_option)).getText().toString());

                // Validating radio buttons
                if (checked == -1) {
                    Toast.makeText(context, context.getString(R.string.choose_some_result), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checked == friendOption) {
                    Toast.makeText(context, context.getString(R.string.result_already_chosen), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validating if user has enough coins
                if (amount > userData.getPoints()) {
                    Toast.makeText(context, context.getString(R.string.no_coins), Toast.LENGTH_SHORT).show();
                    return;
                }

                String option = "";
                switch (checked) {
                    case R.id.radio_team_1:
                        option = "1";
                        break;
                    case R.id.radio_draw:
                        option = "X";
                        break;
                    case R.id.radio_team_2:
                        option = "2";
                        break;
                }
                final String optionBet = option;

                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.accept_dialog_title))
                        .setMessage(context.getString(R.string.accept_dialog_description))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                mProgressBar.setVisibility(View.VISIBLE);

                                RestAdapter restAdapter = new RestAdapter.Builder()
                                        .setEndpoint(Constants.SERVER_API_BASE_URI).build();

                                ServerApi api = restAdapter.create(ServerApi.class);

                                api.acceptBet(Constants.SERVER_KEY, betId, userData.getPersonId(), friendId, matchId, optionBet, amount, new Callback<JsonResponse>() {

                                    @Override
                                    public void success(JsonResponse json, Response response) {

                                        mProgressBar.setVisibility(View.GONE);

                                        // Update userPoints at SharedPreferences
                                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putInt("POINTS", userData.getPoints() - amount);
                                        editor.apply();

                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.putExtra("MENU_FRAGMENT", R.id.nav_invites);
                                        intent.putExtra("BET_ACCEPTED", true);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).show();
            }
        });

        viewHolderChild.refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout viewParent = (LinearLayout) v.getParent().getParent();
                final String betId = ((TextView) viewParent.findViewById(R.id.bet_id)).getText().toString();

                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.refuse_dialog_title))
                        .setMessage(context.getString(R.string.refuse_dialog_description))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                mProgressBar.setVisibility(View.VISIBLE);

                                RestAdapter restAdapter = new RestAdapter.Builder()
                                        .setEndpoint(Constants.SERVER_API_BASE_URI).build();

                                ServerApi api = restAdapter.create(ServerApi.class);

                                api.refuseBet(Constants.SERVER_KEY, betId, new Callback<JsonResponse>() {

                                    @Override
                                    public void success(JsonResponse json, Response response) {
                                        mProgressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.putExtra("MENU_FRAGMENT", R.id.nav_invites);
                                        intent.putExtra("BET_REFUSED", true);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        })
                        .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

            }
        });

        return rowView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    public InvitesExpandableListAdapter(Context context, ArrayList<Bet> bets, UserDataDTO userData, ProgressBar progressBar) {
        this.context = context;
        this.bets = bets;
        this.userData = userData;
        this.mProgressBar = progressBar;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return bets.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return this.bets.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        ViewHolderGroup viewHolderGroup;

        if (convertView == null) {

            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = infalInflater.inflate(R.layout.invite_bet_list_group, null);

            // configure view holder
            viewHolderGroup = new ViewHolderGroup();
            viewHolderGroup.homeTeam = (TextView) convertView.findViewById(R.id.home_team);
            viewHolderGroup.awayTeam = (TextView) convertView.findViewById(R.id.away_team);
            viewHolderGroup.matchId = (TextView) convertView.findViewById(R.id.match_id);
            viewHolderGroup.matchDate = (TextView) convertView.findViewById(R.id.match_date);
            viewHolderGroup.homeLogo = (ImageView) convertView.findViewById(R.id.home_logo);
            viewHolderGroup.awayLogo = (ImageView) convertView.findViewById(R.id.away_logo);
            viewHolderGroup.amount = (TextView) convertView.findViewById(R.id.amount);
            viewHolderGroup.personName = (TextView) convertView.findViewById(R.id.person_name);
            viewHolderGroup.personPhoto = (ImageView) convertView.findViewById(R.id.person_photo);

            convertView.setTag(viewHolderGroup);

        } else {
            viewHolderGroup = (ViewHolderGroup) convertView.getTag();
        }

        if (isExpanded) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.very_light_grey));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        // Fill data
        String homeTeam = bets.get(groupPosition).getMatch().getHomeTeam().trim();
        String awayTeam = bets.get(groupPosition).getMatch().getAwayTeam().trim();
        String matchId = bets.get(groupPosition).getMatch().getMatchId().toString();
        String personName = bets.get(groupPosition).getSrcPerson().getPersonName();
        String personPhoto = bets.get(groupPosition).getSrcPerson().getPersonPhoto();
        Integer amount = bets.get(groupPosition).getAmount();

        viewHolderGroup.homeTeam.setText(TeamsDataEnum.get(homeTeam).label());
        viewHolderGroup.awayTeam.setText(TeamsDataEnum.get(awayTeam).label());
        viewHolderGroup.matchId.setText(matchId);
        viewHolderGroup.personName.setText(personName);
        viewHolderGroup.amount.setText(context.getString(R.string.amount, amount));
        viewHolderGroup.matchDate.setText(ConvertHelper.dateToViewShort(bets.get(groupPosition).getMatch().getTstamp()));

        Picasso.with(context)
                .load(TeamsDataEnum.get(homeTeam).logo())
                .fit()
                .into(viewHolderGroup.homeLogo);

        Picasso.with(context)
                .load(TeamsDataEnum.get(awayTeam).logo())
                .fit()
                .into(viewHolderGroup.awayLogo);

        Picasso.with(context)
                .load(personPhoto)
                .transform(new CircleTransformation())
                .into(viewHolderGroup.personPhoto);

        return convertView;
    }

    private Drawable getDrawableFromURL(String url) {

        Bitmap x;

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(2500);
            connection.connect();

            InputStream input = connection.getInputStream();
            x = BitmapFactory.decodeStream(input);

            return new BitmapDrawable((new CircleTransformation()).transform(x));

        } catch (Exception e) {

            e.printStackTrace();
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_no_image);
            return new BitmapDrawable((new CircleTransformation()).transform(icon));
        }
    }

    public void setUserData(UserDataDTO user) {
        userData = user;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
