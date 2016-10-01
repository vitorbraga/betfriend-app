package br.com.betfriend.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import br.com.betfriend.BetInvitationsActivity;
import br.com.betfriend.R;
import br.com.betfriend.api.ServerApi;
import br.com.betfriend.model.Bet;
import br.com.betfriend.model.JsonResponse;
import br.com.betfriend.model.Match;
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

    private ArrayList<Bet> bets;

    private UserDataDTO userData;

    static class ViewHolderGroup {
        public TextView homeTeam;
        public TextView awayTeam;
        public TextView matchId;
        public TextView matchDate;
        public ImageView homeLogo;
        public ImageView awayLogo;
        public TextView amount;
        public ImageView personPhoto;
        public TextView personName;
    }

    static class ViewHolderChild {
        public Button acceptButton;
        public Button refuseButton;
        public TextView betId;
        public TextView matchId;
        public TextView amount;
        public TextView friendOption;
        public TextView friendId;
        public RadioGroup radioGroup;
        public RadioButton homeRadioButton;
        public RadioButton drawRadioButton;
        public RadioButton awayRadioButton;
    }

    @Override
    public View getRealChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

        View rowView = convertView;

        // reuse views
        if (rowView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.invite_bet_list_item, parent, false);

            ViewHolderChild viewHolderChild = new ViewHolderChild();
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
        }

        final ViewHolderChild holder = (ViewHolderChild) rowView.getTag();

        String homeTeam = bets.get(groupPosition).getMatch().getHomeTeam().trim();
        String awayTeam = bets.get(groupPosition).getMatch().getAwayTeam().trim();
        String matchId = bets.get(groupPosition).getMatch().getMatchId().toString();
        String betId = bets.get(groupPosition).get_id();
        String friendId = bets.get(groupPosition).getSrcPerson().getPersonId();
        Integer amount = bets.get(groupPosition).getAmount();

        String option = bets.get(groupPosition).getOption();

        holder.homeRadioButton.setText(TeamsDataEnum.get(homeTeam).label());
        holder.awayRadioButton.setText(TeamsDataEnum.get(awayTeam).label());
        holder.matchId.setText(matchId);
        holder.amount.setText(amount.toString());
        holder.betId.setText(betId);
        holder.friendId.setText(friendId);

        Drawable img = null;
        // Set ragio buttons
        if (option.equals("1")) {

            holder.homeRadioButton.setEnabled(false);
            img = getDrawableFromURL(bets.get(groupPosition).getSrcPerson().getPersonPhoto());
            holder.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.friendOption.setText(holder.homeRadioButton.getId() + "");

        } else if (option.equals("X")) {

            holder.drawRadioButton.setEnabled(false);
            img = getDrawableFromURL(bets.get(groupPosition).getSrcPerson().getPersonPhoto());
            holder.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.friendOption.setText(holder.drawRadioButton.getId() + "");

        } else {

            holder.awayRadioButton.setEnabled(false);
            img = getDrawableFromURL(bets.get(groupPosition).getSrcPerson().getPersonPhoto());
            holder.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.friendOption.setText(holder.awayRadioButton.getId() + "");
        }

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checked = group.getCheckedRadioButtonId();

                Drawable img = getDrawableFromURL(userData.getPersonPhoto());
                switch (checked) {

                    case R.id.radio_team_1:

                        holder.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                        if(holder.drawRadioButton.isEnabled()) {
                            holder.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        if(holder.awayRadioButton.isEnabled()) {
                            holder.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        break;

                    case R.id.radio_draw:

                        holder.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                        if(holder.homeRadioButton.isEnabled()) {
                            holder.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        if(holder.awayRadioButton.isEnabled()) {
                            holder.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        break;

                    case R.id.radio_team_2:

                        holder.awayRadioButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                        if(holder.homeRadioButton.isEnabled()) {
                            holder.homeRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        if(holder.drawRadioButton.isEnabled()) {
                            holder.drawRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                        break;
                }
            }
        });

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {

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
                    Toast.makeText(context, "Escolha um resultado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (checked == friendOption) {
                    Toast.makeText(context, "Resultado já escolhido. Escolha outro.", Toast.LENGTH_SHORT).show();
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
                                // continue with delete
                                RestAdapter restAdapter = new RestAdapter.Builder()
                                        .setEndpoint(Constants.SERVER_API_BASE_URI).build();

                                ServerApi api = restAdapter.create(ServerApi.class);

                                api.acceptBet(betId, userData.getPersonId(), friendId, matchId, optionBet, amount, new Callback<JsonResponse>() {

                                    @Override
                                    public void success(JsonResponse json, Response response) {

                                        Intent intent = new Intent(context, BetInvitationsActivity.class);
                                        intent.putExtra("BET_ACCEPTED", true);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Toast.makeText(context, "falha no aceito.", Toast.LENGTH_SHORT).show();
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

        holder.refuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout viewParent = (LinearLayout) v.getParent().getParent();
                final String betId = ((TextView) viewParent.findViewById(R.id.bet_id)).getText().toString();

                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.refuse_dialog_title))
                        .setMessage(context.getString(R.string.refuse_dialog_description))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete

                                RestAdapter restAdapter = new RestAdapter.Builder()
                                        .setEndpoint(Constants.SERVER_API_BASE_URI).build();

                                ServerApi api = restAdapter.create(ServerApi.class);

                                api.refuseBet(betId, new Callback<JsonResponse>() {

                                    @Override
                                    public void success(JsonResponse json, Response response) {
                                        Intent intent = new Intent(context, BetInvitationsActivity.class);
                                        intent.putExtra("BET_REFUSED", true);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
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

    private static Match getMatchById(Integer matchId, ArrayList<Match> matches) {

        for (Match sm : matches) {
            if (sm.getMatchId().equals(matchId)) {
                return sm;
            }
        }

        return null;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 1;
    }

    public InvitesExpandableListAdapter(Context context, ArrayList<Bet> bets, UserDataDTO userData) {
        this.context = context;
        this.bets = bets;
        this.userData = userData;
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

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.invite_bet_list_group, null);

            // configure view holder
            ViewHolderGroup viewHolderGroup = new ViewHolderGroup();
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
        }

        // fill data
        ViewHolderGroup holder = (ViewHolderGroup) convertView.getTag();

        String homeTeam = bets.get(groupPosition).getMatch().getHomeTeam().trim();
        String awayTeam = bets.get(groupPosition).getMatch().getAwayTeam().trim();
        String matchId = bets.get(groupPosition).getMatch().getMatchId().toString();
        String personName = bets.get(groupPosition).getSrcPerson().getPersonName();
        String personPhoto = bets.get(groupPosition).getSrcPerson().getPersonPhoto();
        Integer amount = bets.get(groupPosition).getAmount();

        holder.homeTeam.setText(TeamsDataEnum.get(homeTeam).label());
        holder.awayTeam.setText(TeamsDataEnum.get(awayTeam).label());
        holder.matchId.setText(matchId);
        holder.personName.setText(personName);
        holder.amount.setText(amount.toString());
        holder.matchDate.setText(ConvertHelper.dateToViewShort(bets.get(groupPosition).getMatch().getTstamp()));

        Picasso.with(context)
                .load(TeamsDataEnum.get(homeTeam).logo())
                .fit()
                .into(holder.homeLogo);

        Picasso.with(context)
                .load(TeamsDataEnum.get(awayTeam).logo())
                .fit()
                .into(holder.awayLogo);

        Picasso.with(context)
                .load(personPhoto)
                .transform(new CircleTransformation())
                .into(holder.personPhoto);

        return convertView;
    }

    public static Drawable getDrawableFromURL(String url) {

        Bitmap x;

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();

            InputStream input = connection.getInputStream();
            x = BitmapFactory.decodeStream(input);

            return new BitmapDrawable((new CircleTransformation()).transform(x));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
